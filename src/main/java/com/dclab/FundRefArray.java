/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author richardthorne
 */
@JsonIgnoreProperties
@JacksonXmlRootElement(localName = "data")
public class FundRefArray {
    @JacksonXmlProperty(localName = "entries")
    @JacksonXmlElementWrapper(useWrapping = false)
    FundRefEntry[] entries;
    
    public FundRefArray() {}

    public FundRefEntry[] getEntries() {
        return entries;
    }

    public void setEntries(FundRefEntry[] entries) {
        this.entries = entries;
    }
    
}
