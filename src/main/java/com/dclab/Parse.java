/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import com.dclab.preparation.MatchingTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author richardthorne
 */
public class Parse {
       public static void main(String[] args) {
        String fileName = "free-text.txt";
        if (args.length > 0) {
            fileName = args[0];
        }
        Logger.getLogger(Parse.class.getName()).info("Parsing file " + fileName);
       
        Parse rp=null;
        try {
            FileHandler fh = new FileHandler(  "parsing%s.log");
            fh.setFormatter( new SimpleFormatter() );
            fh.setLevel(Level.FINE);
            Logger.getLogger(Parse.class.getName()).addHandler(fh);
            Logger.getAnonymousLogger().addHandler(fh);
            rp = new Parse(new FileReader( fileName ));
            rp.run();
           
        } catch (IOException ex) {
            Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, null, ex);
        }
      

    }
        Writer sponsors = null;
    private GrantToken lastToken;
    private GrantsLexer lexer;
    private Reader reader;
    private  Writer writer;
    private  MatchingTest mt;
    private static final String header = "<html>\n" +
"<head>\n" +
"  <meta charset=\"utf-8\"/>\n" +
"  <style>\n" +
"    body { background-color: #F2FFFF }" +
"    .st  { color:green; text-decoration:underline}\n" +
"    .org { color:orange; font-weight:600}\n" +
"    .au  { color:blue}\n" +
"    .gr  { color:red}\n" +
"  </style>\n" +
"  </head>\n" ;

    private  FileWriter orgs;

    public Parse(Reader  in)  {
          
           mt = new MatchingTest();
            
            try {
                
                 orgs = new FileWriter( "sponsors_orig.txt" );
                sponsors = new BufferedWriter( new FileWriter( "sponsors.txt"));
                BufferedReader bwr = new BufferedReader( in );
                StringWriter sw = new StringWriter();
                writer = new BufferedWriter( new FileWriter("tokens.html"));
                writer.write(header);
                writer.write( "    <body><div class=\"txt\"><p>" );
                CharBuffer cb = CharBuffer.allocate(24080);
                while( bwr.ready()) {
                    bwr.read(cb);
                    cb.flip();
                    sw.write(cb.toString());
                }
                String content = sw.toString();
                String decoded = null;
                try {
                    decoded = decode(content);
                } catch (CharacterCodingException ex) {
                    Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                lastToken = new GrantToken(TC.EOF);
            reader = new StringReader(decoded);
            } catch (IOException ex) {
                Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, "Failed with file " , ex);
            }
    }

    

    public void run() throws IOException {
        List<String> lines = new ArrayList();
        GrantToken token;
        int n, i;
        
         lexer = new GrantsLexer( reader );
       

        n = -1;
        StringBuilder sb = new StringBuilder();
        do {
            
            try {
                token = lexer.nextToken();
                
                if ( nonCapture( token ) ) {
                    if (token.symbol().equals(TC.FLUSH)) {
                        writer.flush();
                    }
                    
                    if ( token.lexeme().equals("\n")) {
                        writer.write("</p></div><div class=\"txt\"><p>");
                    }
                    writer.write( token.lexeme()  );
                    sb.append( token.lexeme());
                }
                else {
                    
                    if ( token.symbol().equals(TC.ORG)) {
                        sponsors.write( token.lexeme() );
                        sponsors.write("\n");
                    }
                       
                   
                     sb.append( token );
                     if (token.symbol().equals(TC.ORG) ) {
                         orgs.write( token.lexeme() + "\n\n");
                         String res = mt.htmlSponsor(token.lexeme() );
                         writer.write( res );
                     }else {
                        writer.write( token.toString() );
                     }
                     
                    //System.out.print( sb );
                    sb = new StringBuilder();
                }
                
            }catch (Exception  e ) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "FAILED TO GET TKEN", e );
                return;
            }

        } while (!token.symbol().equals(TC.EOF));
       writer.write("</p></p></body></html>");
        writer.close();
        orgs.close();
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
