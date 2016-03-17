/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab.preparation;

import com.dclab.Sponsor;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author richardthorne
 */
public class LoadSponsors {
    public static void main(String[]  args ) {
        new LoadSponsors();
    }
    
    private final String PERSISTENCE_UNIT_NAME = "grants";
    private EntityManagerFactory factory;
    private EntityManager em;
    private String FILENAME = "tmp.csv";
    
    public LoadSponsors()  {
            
          Set<String> set = new HashSet();
         try {
             factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
             em = factory.createEntityManager();
             List<String> sponsorSet = Files.readLines(new File(FILENAME), Charsets.UTF_8);
             em.getTransaction().begin();
             for( String src : sponsorSet ) {
                 String fld[] = src.split("\t");
                 String s = fld[0];
                 if ( set.add(s.trim().toLowerCase())) {
                    Sponsor sponsor = new Sponsor( s.trim() );
                    em.persist(sponsor);
                 }
             }
             em.getTransaction().commit();
         } catch (IOException ex) {
             Logger.getLogger(LoadSponsors.class.getName()).log(Level.SEVERE, null, ex);
         }
         
    }
}
