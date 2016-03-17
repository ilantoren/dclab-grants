/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

/**
 *
 * @author richardthorne
 */
public class FundRefSearch {

   

    private final String PERSISTENCE_UNIT_NAME = "grants";
    private final EntityManagerFactory factory;
    private final EntityManager em;
    private final String FILENAME = "sponsors.txt";
    private final Pattern pat = Pattern.compile("^\\p{Punct}|\\p{Punct}$");
    private static final int LIMIT = 5;
    private static final String[] splitwords = {"and", "under", "by", "of", "to", "for", "from"};
    private static final String[] stopwords = {"the", "an"};
    private final Set<String> ignore;
    private final TypedQuery<FundRefEntry> nq;
    TypedQuery<FundRefEntry> like;
    private Writer grantsLog;
    private final Set<String> removeWords;
    final double SCORE_LIM = 0.799;
    private final Set<String> grantWordSet;
    private Pattern numbers = Pattern.compile( "(\\d+)");

    public FundRefSearch() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        ignore = Arrays.stream(stopwords).collect(Collectors.toSet());
        removeWords = Arrays.stream(splitwords).collect(toSet());
        removeWords.addAll(ignore);
        grantWordSet  = Arrays.stream( this.grantWords ).collect( toSet() );
        em = factory.createEntityManager();
        nq = em.createNamedQuery(FundRefEntry.SEARCH, FundRefEntry.class);
        like = em.createNamedQuery(FundRefEntry.LIKE, FundRefEntry.class);

        try {
            grantsLog = new BufferedWriter(new FileWriter("grants_scores.log"));
        } catch (IOException ex) {
            Logger.getLogger(FundRefSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
  

    String removeWords(final String s) {
        String t = s.replaceAll("\\p{Punct}", "" );
        Matcher mat = pat.matcher(t);
        String[] tkens = mat.replaceAll("").toLowerCase().split(" ");
        return Arrays.stream(tkens).filter(z -> !removeWords.contains(z)).collect(Collectors.joining(" "));
    }

    public List<SearchResult> findFundRefEntry(String search) {
        List<SearchResult> results = new ArrayList();
        String test = pat.matcher(search).replaceAll("").trim();
        nq.setParameter("searchTerm", test);
        List<FundRefEntry> list = nq.getResultList();
        for (FundRefEntry e : list) {
            if (search.equalsIgnoreCase(e.getSponsor()) || search.equalsIgnoreCase(e.getAlt())) {
                SearchResult sr = new SearchResult(search, e.getSponsor(), e);
                results.add(sr);
            } else {
                // partials
                SearchResult sr2 = LookForPartial(search, e);
                results.add(sr2);
            }
        }

        return results.stream().filter(x -> x != null).collect( toList() );
    }

    public List<SearchResult> getBestMatch(List<SearchResult> list) {
        Map<String,SearchResult> map = new HashMap();
        for( SearchResult r  : list ) {
            String search = r.getSearchTerm();
            if ( map.containsKey( search )) {
                SearchResult held = map.get( search );
                Double score = held.getScore();
                if ( r.getScore() > score ) {
                    map.put( search, r );
                }
            }
            else {
                map.put( search, r);
            }
        }
        return map.values().stream().collect( toList() );
    }


  

    private SearchResult LookForPartial(String search, FundRefEntry e) {
        SearchResult result;
        result = highSimilarity(search, e);
        if (result == null) {
            result = removePrepositionMatch(search, e);
        }
        if (result == null) {
            result = leftEdge(search, e);
        }
        if (result == null) {
            result = rightEdge(search, e);
        }
        if (result == null) {
            result = contains(search, e);
        }
        return result;
    }

    private SearchResult removePrepositionMatch(String search, FundRefEntry e) {
        String s1 = removeWords(search);
        String s2 = removeWords(e.getSponsor());
        String s3  = removeWords( e.getAlt() );
        if (s1.equals(s2) || s1.equals(s3)) {
            SearchResult result = new SearchResult(search, e.getSponsor(), e);
            result.setScore(0.9);
            result.setDesc("remove stop words");
            return result;
        }
        return null;
    }

    private SearchResult leftEdge(String search, FundRefEntry e) {
        if (search.startsWith(e.getSponsor())) {
            int len = e.getSponsor().length();
            double score = len / search.length(); 
            if (score > SCORE_LIM) {
                SearchResult result = new SearchResult(search, e.getSponsor(), e);
                result.setDesc("left edge");
                result.setScore(score);
                return result;
            }
        }
        return null;
    }

    private SearchResult rightEdge(String search, FundRefEntry e) {
        if (search.endsWith(e.getSponsor())) {
            int len = e.getSponsor().length();
            double score = len /search.length();
            if (score > SCORE_LIM) {
                SearchResult result = new SearchResult(search, e.getSponsor(), e);
                result.setDesc("right edge");
                result.setScore(score);
                return result;
            }
        }
        return null;
    }

    private SearchResult contains(String search, FundRefEntry e) {
        SearchResult result = null;
        String target = e.getSponsor();
        if (target.contains(search)) {

            double score =  search.length() /target.length(); 
            if (score > SCORE_LIM) {
                result = new SearchResult(search, e.getSponsor() , e);
                result.setScore(score);
                result.setDesc("contains");
                return result;
            }
        }

        if (result == null && search.contains(target)) {
            double score = target.length() / search.length();
            if (score > SCORE_LIM) {
                result = new SearchResult(search, e.getSponsor(), e);
                result.setScore(score);
                result.setDesc("contains");
                return result;
            }
        }

        return null;
    }

    private SearchResult highSimilarity(String search, FundRefEntry e) {
        StringMetric metric = StringMetrics.cosineSimilarity();
        float score = metric.compare(search, e.getSponsor());
        if (score > 0.91) {
            SearchResult result = new SearchResult(search, e.getSponsor(), e);
            result.setDesc("similarity metric");
            result.setScore(new Double(score));
            return result;
        }
        return null;
    }
    
    public boolean isGrantPhrase( String s ) {
        String[] tokens = s.toLowerCase().replaceAll( "\\p{Punct}", "").split("\\s+");
        long count = Arrays.stream( tokens ).filter(x -> grantWordSet.contains(x) ).count();
        boolean found = count > 0;
        return found;
    }
    
    public String getCountryCode( String s ) {
        Matcher mat = numbers.matcher(s);
        if ( mat.find()) {
            String t = mat.group(1);
            // US record is not in the db, didn't load properly
            if ( t.equals("6252001")) return "USA";
            Integer indx = Integer.parseInt(t);
            GeoName  geo = em.find(GeoName.class, indx);
            if ( geo != null) {
                return geo.getCountry_code();
            }
            else {
                Logger.getLogger(this.getClass().getName()).log( Level.WARNING, "{0} is not listed", t );
            }
        }
        return null;
    }
    
    private final String[] grantWords = {"group",  "nih", "unit", "department", "institute", "grant", "trust", "foundation", "initiative", "center", "organization", "organisation", "council" };

}
