/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Index;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.regex.Pattern;
import javax.persistence.Transient;

/**
 *
 * @author richardthorne  *
 */
@Entity
@Table(name = "FundRef",
        indexes = {
            @Index(name = FundRefEntry.SP_INDEX, unique = false, columnList = "sponsor")
        })
@NamedQueries({
    @NamedQuery(name = FundRefEntry.SEARCH, query = "SELECT OBJECT(e) FROM FundRefEntry e WHERE SQL( 'MATCH(sponsor,alt) AGAINST( ?  IN NATURAL LANGUAGE MODE) order by  match( sponsor,alt ) against ( ? in natural language mode ) desc limit 20', :searchTerm, :searchTerm )"),
    @NamedQuery(name = FundRefEntry.LIKE, query = "SELECT OBJECT(e) FROM FundRefEntry e WHERE SQL( 'sponsor LIKE ? ', :term) ")
})


@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "FundRef")
public class FundRefEntry {

    @Transient
    @JsonIgnore
    private Pattern pat = Pattern.compile( "\"");
    
    @Transient
    @JsonIgnore
    private Pattern pat2 = Pattern.compile("@\\w{2}");
    
    public static final String SP_INDEX = "FUNDREF_INDX_SPONSOR";
    public static final String SEARCH = "FUNDREF.SEARCH";
    public static final String LIKE = "FUNDREF.LIKE";

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long Id;

    @JsonProperty
    @JacksonXmlProperty
    private String doi;

    @JsonProperty
    @JacksonXmlProperty
    private String sponsor;

    @JsonProperty
    @JacksonXmlProperty
    private String alt;

    @JsonProperty
    @JacksonXmlProperty
    private String type;

    @JsonProperty
    @JacksonXmlProperty
    private String country;
    
     @JsonProperty
    @JacksonXmlProperty
     private String affiliated;

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = clean( sponsor );
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt =   clean ( alt );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = clean(type);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = clean( country );
    }
    
    private String clean( String s ) {
        String t = pat.matcher( s ).replaceAll("");
        return pat2.matcher( t ).replaceAll("");
    }

    public String getAffiliated() {
        return affiliated;
    }

    public void setAffiliated(String affiliated) {
        this.affiliated = affiliated;
    }
    
    

}
