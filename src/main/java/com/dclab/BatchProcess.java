/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author richardthorne
 */
public class BatchProcess {
    public static void main(String[] args )  {
        new BatchProcess();
    }
    
   
    public BatchProcess() {
        try {
            FundRefSearch search = new FundRefSearch();
            List<String> content = Files.readLines(new File("free-text.txt"), Charsets.UTF_8 );
            Writer write = new BufferedWriter( new FileWriter( "results.xml"));
            StringBuilder sb = new StringBuilder();
            write.append( "<data>\n");
            for ( String s : content) {
                if ( s.matches("\\s+Acknowl.+")) {
               
                    write.append("<grantList>\n");
                   
                   
                    GrantInformation rp = new GrantInformation( sb.toString() , write, search, true, true );
                    rp.run();
                     write.append( "<grant-text>\n");
                    write.append( sb.toString() );
                    write.append( "</grant-text>\n").append("</grantList>\n");
                    sb = new StringBuilder();
                    sb.append( s );
                }
                else {
                    sb.append(s );
                }
            }
         write.append("</data>");
         write.close();
        } catch (IOException ex) {
            Logger.getLogger(BatchProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
