/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab.preparation;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.csvreader.CsvReader;
import com.dclab.GeoName;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author richardthorne
 */
public class LoadGeoName {
     public static void main(String[]  args ) {
        new LoadGeoName();
    }
    
    private final String PERSISTENCE_UNIT_NAME = "grants";
    private EntityManagerFactory factory;
    private EntityManager em;
    private String FILENAME = "6252001.csv";
    private final static long BLOCK  = 5000L; 
    
    public LoadGeoName()  {
        long cnt = 0;
         try {
             factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
             em = factory.createEntityManager();
             em.getTransaction().begin();
             CsvReader csv;
             csv = new CsvReader( new BufferedReader(new FileReader(FILENAME)), '\t');
             csv.setSafetySwitch(false);   // allow long lines but data will be trimmed.
             while( csv.readRecord() ) {
                 String[] vals = csv.getValues();
                 GeoName geo = new GeoName( vals );
                 GeoName tst =  em.find(GeoName.class, geo.getGeonameid()); 
                 if ( tst == null) {
                    em.persist(geo);
                    
                  }
                 
                 cnt++;
                 if ( cnt % BLOCK == 1)  {
                     long cr = csv.getCurrentRecord();
                     Logger.getLogger(LoadGeoName.class.getName()).log(Level.INFO, "at record {0} ", cr);
                     em.getTransaction().commit();
                     em.clear();
                     em.getTransaction().begin();
                 }
             }
            em.getTransaction().commit();
         } catch (IOException ex) {
             Logger.getLogger(LoadGeoName.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ParseException ex) {
             Logger.getLogger(LoadGeoName.class.getName()).log(Level.SEVERE, null, ex);
         }
        
    }
    
}
