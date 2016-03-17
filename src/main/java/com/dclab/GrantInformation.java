/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import com.dclab.preparation.MatchingTest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author richardthorne
 */
public class GrantInformation {
    
    
       public static void main(String[] args) {
        String fileName = "free-text.txt";
        if (args.length > 0) {
            fileName = args[0];
        }
        Logger.getLogger(GrantInformation.class.getName()).log(Level.INFO, "Parsing file {0}", fileName);
        String s = " Acknowledgments  The authors acknowledge support from NIH grants HG000376 (M.B.), HG007022 (G.R.A.), and HG006513 (G.R.A.). ";
        GrantInformation rp=null;
        try {
            BufferedWriter writer = new BufferedWriter( new FileWriter( "tokens.txt"));
            rp = new GrantInformation(s, writer, new FundRefSearch(), true, true );
            rp.run();
           
        } catch (IOException ex) {
            Logger.getLogger(GrantInformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
       
       
  
    private final GrantToken lastToken;
    private GrantsLexer lexer;
    private final Reader reader;
    private  Writer writer;
    private  MatchingTest mt;
    private FundRefSearch search;
    private final Map<String,SearchResult> results;
   
    
    private SearchResult lastFundRef;
 Pattern pat = Pattern.compile( "(\\d+)" );
    private final boolean printUnlisted;
    private final boolean debug;
    public GrantInformation(String content,  Writer writer, FundRefSearch search, boolean alsoUnlisted, boolean debug )  {
          
           this.search = search;
           this.writer = writer;
           this.printUnlisted = alsoUnlisted;
           this.debug = debug;
                
                
              
                StringWriter sw = new StringWriter();
            
    
                String decoded = null;
                try {
                    decoded = decode(content);
                } catch (CharacterCodingException ex) {
                    Logger.getLogger(GrantInformation.class.getName()).log(Level.SEVERE, null, ex);
                }
                results = new HashMap();
                lastToken = new GrantToken(TC.EOF);
            reader = new StringReader(decoded);
            
    }

    

    public void run() throws IOException {
        List<String> lines = new ArrayList();
        GrantToken token;
        int n, i;
        
         lexer = new GrantsLexer( reader );
       

        n = -1;
       
        do {
            
            try {
                token = lexer.nextToken();

                    if ( token.symbol().equals(TC.ORG) ) {
                       // writer.append("<!-- search ").append(token.lexeme() ).append( " -->\n");
                       lastFundRef = null;
                        List<SearchResult> hits = search.findFundRefEntry( token.lexeme());
                        for( SearchResult e : search.getBestMatch(hits )) {
                            if ( debug ) {
                            writer.append("<!-- ").append( token.lexeme()).append(" FOUND: ")
                                    .append( e.getEntry().getSponsor() )
                                    .append( " ")
                                    .append( e.getDesc())
                                    .append( " ")
                                    .append(e.getScore().toString())
                                    .append( "  -->\n");
                            }
                           results.put( e.getSearchTerm(), e);
                           lastFundRef = e;
                        }
                        if ( (lastFundRef != null && lastFundRef.getSearchTerm().equals(token.lexeme())) && search.isGrantPhrase( token.lexeme()) ) {
                            SearchResult sr = new SearchResult( token.lexeme(), token.lexeme(), null );
                            lastFundRef = sr;
                            if ( ! results.containsKey(token.lexeme() )) {
                                results.put( sr.getMatchTerm(), sr);
                            }
                                
                            //writer.append("<!-- unlisted ").append(token.lexeme() ).append( " -->\n");
                        }
                           
                        
                     } else if ( token.symbol().equals(TC.GID) && lastFundRef != null ) {
                         lastFundRef.getGrant_id().add( token.lexeme() );
                       
                     }
                     else if ( token.symbol().equals(TC.GID)) {
                         writer.append( "<!-- GID : ")
                                 .append( token.lexeme() )
                                 .append( " -->\n");
                     }
                     else if ( token.symbol().equals(TC.STOP) || token.symbol().equals(TC.START)) {
                         lastFundRef = null;
                     }       
                
            }catch (Exception  e ) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "FAILED TO GET TKEN", e );
                return;
            }

        } while (!token.symbol().equals(TC.EOF));   
        
        for( SearchResult x : results.values() ) {
            
            // process each grant-agency at least once
            if ( x.getGrant_id().isEmpty() ) { x.getGrant_id().add( null ); }
            if ( x.getEntry() == null ) {
                if ( printUnlisted ) {
                  x.getGrant_id().stream().forEach( y -> {
                      try {
                          printUnlistedAgency(x.getSearchTerm(), y );
                      } catch (IOException ex) {
                          Logger.getLogger(GrantInformation.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  });
                }
            }
            else {
                 x.getGrant_id().stream().forEach( y -> {
                     try {
                         printGrant(x.getEntry(), y );
                     } catch (IOException ex) {
                         Logger.getLogger(GrantInformation.class.getName()).log(Level.SEVERE, null, ex);
                     }
                 });
            }
        }
    } 

    
    private void printUnlistedAgency( String name, String GID ) throws IOException  {
        writer.append( "<grant>\n");
        if ( GID != null ) {
            writer.append("<grant-id>").append( GID ).append("</grant-id>\n");
        }
        writer.append("<grant-agency>").append("<organization>").append( name ).append("</organization>").append("</grant-agency>\n");
        writer.append( "</grant>\n" );
    }
    
    
    
    
    private void printGrant(FundRefEntry agency, String GID  ) throws IOException {
        
     writer.append( "<grant>\n");
        if ( GID != null ) {
            writer.append("<grant-id>").append( GID ).append("</grant-id>\n");
        }
        writer.append("<grant-agency>").append("<organization>").append( agency.getSponsor() ).append("</organization>");
        String countryCode = search.getCountryCode(agency.getCountry() );
        if ( countryCode != null ) {
            writer.append( String.format( "<country iso-code= \"%s\"  />", countryCode ));}
        writer.append("</grant-agency>\n");
        Matcher mat = pat.matcher(agency.getDoi() );
        if ( mat.find() ) {
            String id = mat.group(1);
            writer.append("<grant-agency-id>").append( id ).append("</grant-agency-id>\n");
        }
        writer.append("</grant>\n");
    }
    
    
    
    private String decode ( String s) throws CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        ByteBuffer bb = ByteBuffer.wrap(s.getBytes());
        CharBuffer parsed = decoder.decode(bb);
          StringBuilder sb = new StringBuilder();
        for(int i=0;i<parsed.length();i++){ 
          if (Character.isHighSurrogate(parsed.charAt(i))) continue;
          if (Character.isLowSurrogate( parsed.charAt(i)) ) continue;
            sb.append(parsed.charAt(i));
        }
 
        return sb.toString();
    }

    private boolean nonCapture(GrantToken token) {
        TC sym = token.symbol();
        if ( 
             TC.AND.equals( sym)    |
             TC.DISCARD.equals(sym) |
             TC.SKIP.equals( sym)   |
             TC.STOP.equals( sym)   |
             TC.FLUSH.equals(sym)   |
             TC.PAREN.equals(sym) )
        {
            return true;
        }
        else {
            return false;
        }
    }
}
