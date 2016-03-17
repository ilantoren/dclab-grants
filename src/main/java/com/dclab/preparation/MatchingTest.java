/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab.preparation;

import com.dclab.Sponsor;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author richardthorne
 */
public class MatchingTest {
    
    public static void main( String[] args ) {
       MatchingTest app =  new MatchingTest();
       app.test();
    }
    
    private final String PERSISTENCE_UNIT_NAME = "grants";
    private final EntityManagerFactory factory;
    private final EntityManager em;
    private final String FILENAME = "sponsors.txt";
    private final Pattern pat = Pattern.compile("^\\p{Punct}|\\p{Punct}$");
    private static final int LIMIT = 5;
    private static final String[] splitwords = { "and", "under", "by" , "of", "to", "from" };
    private static final String[] stopwords = { "the", "an" };
    private final Set<String> ignore;
    private final  TypedQuery<Sponsor> nq;
    TypedQuery<Sponsor> like;
    private Writer grantsLog;
    
    
    
    
    public MatchingTest() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
           ignore = Arrays.stream(stopwords).collect(Collectors.toSet());
            em = factory.createEntityManager();
            nq = em.createNamedQuery(Sponsor.SEARCH, Sponsor.class);
            like = em.createNamedQuery(Sponsor.LIKE, Sponsor.class);

        try {
             grantsLog = new BufferedWriter( new FileWriter( "grants_scores.log"));
        } catch (IOException ex) {
            Logger.getLogger(MatchingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void test () {
        try {
            List<String> sponsorSet = Files.readLines(new File(FILENAME), Charsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            
            for( String s : sponsorSet ) {
                findSponsor(s,  sb);
            }
            
            System.out.println( sb.toString() );
        } catch (IOException ex) {
            Logger.getLogger(MatchingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void findSponsor(String s, StringBuilder sb) {
        // remove punctuation

        String test = pat.matcher(s).replaceAll("").trim();
        nq.setParameter("searchTerm", test);
        List<Sponsor> list = nq.getResultList();
        if (exactMatch(test, list)) {
            sb.append(test).append("\n");
        } else {
            String found = matchWithSplit(test, list);
            if (found == null) {
                //sb.append("\n");
            } else {
                sb.append(found);
                StringBuilder tb = new StringBuilder();
                int indx = test.indexOf(found) + found.length();
                if (indx < test.length()) {
                    findSponsor(test.substring(indx), tb);
                    if (!tb.toString().isEmpty()) {
                        sb.append("#").append(tb.toString());
                    } 
                }

            }

        }
        Logger.getAnonymousLogger().info(s + "@" + sb.toString());

    }
    
    
    
    // "<span class=\"org\">" + lexeme + "</span>";
    // introducing a side effect of a file for model
    public String htmlSponsor( String org ) throws IOException {
        StringBuilder sb = new StringBuilder();
        StringBuilder result = new StringBuilder();
        findSponsor( org, sb );
        String source = sb.toString();
        String[] sections = source.split("#");
        for (String src : sections) {
            String tmp;
            int indx = org.indexOf(src);
            int end = indx + src.length();
            if (indx > -1 && (tmp = buildResponse(src, org.substring(0, end))) != null) {
                result.append( tmp );
                org = org.substring(end);
            }
        }
        
        result.append( org );
        
        if ( result.length() > 0)
            return result.toString();
        
        grantsLog.append( org).append(",0\n");
        return org;
    }

    protected String buildResponse(String str, String org) throws IOException {
        String found = str.trim();
        int i = org.indexOf(found);
        if (found.length() > 2 && (org.equalsIgnoreCase(found) || i >= 0)) {
            StringBuilder result = new StringBuilder();
            grantsLog.append( org.substring(0, i)).append( ", 0\n");
            result.append( org.substring(0, i)).append("<span class=\"org\">");
            result.append( found ).append( "</span>" );
            grantsLog.append( found ).append( ",1\n");
            if ( i+found.length() < org.length()) {
                String remainder = org.substring(i+found.length());
                if (  remainder.equals(org) ||  remainder.length() < 4  ) {
                    result.append( remainder );
                    grantsLog.append( remainder ).append(",0\n");
                }
                else {
                    String nxtResult = htmlSponsor(remainder);
                    result.append( nxtResult);        
                }
            }
            return result.toString();
        }
        return null;
    }
    

    protected String withinMatch(String s, List<Sponsor> list) {
        String test = s.toLowerCase();
        for (Sponsor spns : list) {
            String match = spns.getSponsor().toLowerCase();
            if ((test.contains(match) || match.contains(test)) && (canMatch(test, match) || canMatch( match, test )) ) {
                return spns.getSponsor();
            }
        }
        return null;
    }

    protected String matchWithSplit(String s, List<Sponsor> list) {
        String test = s.toLowerCase().trim();
        // if the split string maatches within the candidate sponsors terminate,
        //   but if the result is not found (null) keep on trying to split down the string to something
        // that could match
          if ( exactMatch( s, list)) return s;
          String x = null;;
          if ( null != (x = nearsearch( s, list)) ) return x;
          if ( null != ( x =  beginOrEnds(s, list )) ) return x;
          String result = withinMatch(s, list);
        if (result == null) {
            for (String word : splitwords) {
                if (test.contains(word)) {
                    int i = test.indexOf(word);
                    if (i < 0) {
                        continue;
                    }
                    String before = test.substring(0, i);
                    result = withinMatch(before, list);
                    String after = test.substring(i + word.length());
                    if ( after.length() > 0) {
                       String resulty = matchWithSplit(after, list);
                       if( result == null ) {
                           result = resulty;
                       }
                       else {
                        result =  result + "##" + resulty;
                       }
                    }
                }
                
            }
        }

        return result;
    }

    protected boolean exactMatch( String s,  List<Sponsor> list ) {
        String test = s.trim();
        int n = 0;
        for( Sponsor spons : list ) {
            if ( test.equalsIgnoreCase(spons.getSponsor())) {
                return true;
            }
            n++;
            if ( n > LIMIT) break;
        }
       return false;
    }

    private boolean canMatch(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i =  a.indexOf(b);
        if ( i < 0 ) return false;
        sb.append( a.substring(0, i)).append(" ");
        int matchEnd = i+b.length();
        if ( a.length() < matchEnd ) {
            sb.append( a.substring(i+b.indexOf(i)) );
        }
        Set<String> tks = Arrays.stream( sb.toString().split("\\s")).collect(toSet());
        return ignore.addAll(tks);
    }

    private String nearsearch(String s, List<Sponsor> list) {
        // take off the first word and test
        int indx = s.indexOf(" ");
        int indxj = s.lastIndexOf(" ");
        String t; 
        
        if ( indx > -1) {
            t = s.substring(indx);
            if ( exactMatch( t, list )) return t;
        }
        else if ( indxj > indx) {
            t = s.substring(0, indxj);
            if ( exactMatch( t, list )) return t; 
        }
        return null;
    }
    
    private String beginOrEnds(String s, List<Sponsor> lst) {
        List<String> m = lst.stream().map(w -> w.getSponsor().toLowerCase()).collect(toList());
        String ls = s.toLowerCase().trim();  
        for (String wrd : m) {
            int indx = ls.indexOf(wrd);
            if (indx > -1) {
                int len = wrd.length();
                return s.substring(indx, indx + len);
            }
        }
        return null;
    }
    
   
}
    

