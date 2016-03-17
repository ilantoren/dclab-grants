/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 *
 * @author richardthorne 

 */
@Entity 
@Table( name = "Sponsor", 
    indexes = {     
       @Index( name = Sponsor.SP_INDEX,  unique = false, columnList = "sponsor")   
    })
@NamedQueries ({
    @NamedQuery( name = Sponsor.SEARCH, query="SELECT OBJECT(e) FROM Sponsor e WHERE SQL( 'MATCH(sponsor) AGAINST( ?  IN NATURAL LANGUAGE MODE) order by  match( sponsor ) against ( ? in natural language mode ) desc limit 20', :searchTerm, :searchTerm )"),
    @NamedQuery( name = Sponsor.LIKE,   query="SELECT OBJECT(e) FROM Sponsor e WHERE SQL( 'sponsor LIKE ? ', :term) ")
})

public class Sponsor {
    public static final String SEARCH = "Sponsor.findByText";
    public static final String LIKE = "Sponsor.findbyPhrase";
    public static final String SP_INDEX = "idx_SPONSOR_text";
    
    // no arg construct
    protected Sponsor() {}
    
    public Sponsor( String sponsor) {
        this.sponsor = sponsor;
    }
    
    
    
   @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
   private Integer id;
   
   private String sponsor;

    public Integer getId() {
        return id;
    }


    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
   
   
}
