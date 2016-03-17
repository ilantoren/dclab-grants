/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author richardthorne
 */
public class SearchResult {
    private String searchTerm;
    private String matchTerm;
    private FundRefEntry   entry;
    private String desc;
    private Double score;
    private List<String> grant_id;
    
    
    public SearchResult( String searchTerm, String matchTerm,  final FundRefEntry entry) {
        this.searchTerm = searchTerm;
        this.matchTerm = matchTerm;
        score = 1D;
        desc = "exact";
        this.entry = entry;
        grant_id = new ArrayList();
    }

    public FundRefEntry getEntry() {
        return entry;
    }

    public void setEntry(FundRefEntry entry) {
        this.entry = entry;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getMatchTerm() {
        return matchTerm;
    }

    public void setMatchTerm(String matchTerm) {
        this.matchTerm = matchTerm;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<String> getGrant_id() {
        return grant_id;
    }

    public void setGrant_id(List<String> grant_id) {
        this.grant_id = grant_id;
    }
    
    
    
}
