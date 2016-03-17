/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author richardthorne
 */
public class ShowResults {
    public static void main ( String[] args) {
        try {
            String in = "tokens.html";
            String out = "display_results.html";
            ShowResults app = new ShowResults( in, out );
        } catch (IOException ex) {
            Logger.getLogger(ShowResults.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Pattern pat = Pattern.compile( "<[^>]+>" );
    Writer outfile;

    public ShowResults(String in, String out) throws IOException {
        outfile = new FileWriter( out );
        Document doc = Jsoup.parse(new File( in ),Charsets.UTF_8.name() );
        Elements divs = doc.select("div");
        
        for ( Element d : divs ) {
            if ( d.html().contains("START")) continue;
            if ( d.html().contains("END")) continue;
            String innerHtml = d.html();
            String content = pat.matcher(innerHtml).replaceAll("");
            d.append( "<code>" + content + "</code><br/><hr/>" );
        }
        outfile.write( doc.html() );
        outfile.close();
    }
}
