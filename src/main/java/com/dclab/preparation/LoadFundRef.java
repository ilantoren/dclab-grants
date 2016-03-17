/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab.preparation;

import com.dclab.FundRefArray;
import com.dclab.FundRefEntry;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author richardthorne
 */
public class LoadFundRef {

    public static void main(String[] args) {
        String filename = "sponsor_data.json";
        LoadFundRef app = new LoadFundRef(filename);
    }

    
    private final String PERSISTENCE_UNIT_NAME = "grants";
    private final EntityManagerFactory factory;
    private final EntityManager em;

    public LoadFundRef(String filename) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
        try {

            ObjectMapper mapper = new ObjectMapper();
            //JsonParser jsonParser = new JsonFactory().createParser(new File(filename));
           FundRefArray entries = mapper.readValue(new File(filename), FundRefArray.class);
            em.getTransaction().begin();

            for  (FundRefEntry ref : entries.getEntries()) {
                em.persist(ref);
            }

            em.getTransaction().commit();
        } catch (IOException ex) {
            Logger.getLogger(LoadSponsors.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
