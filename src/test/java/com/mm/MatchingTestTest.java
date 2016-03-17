/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mm;

import com.dclab.preparation.MatchingTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardthorne
 */
public class MatchingTestTest {
    
    public MatchingTestTest() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of main method, of class MatchingTest.
     */
    @org.junit.Test
    public void testMain() {
    }

    /**
     * Test of test method, of class MatchingTest.
     */
    @org.junit.Test
    public void testTest() {
    }

    /**
     * Test of findSponsor method, of class MatchingTest.
     */
    @org.junit.Test
    public void testFindSponsor() {
        MatchingTest instance = new MatchingTest();
        StringBuilder sb = new StringBuilder();
        instance.findSponsor("Alumni Association of University of Leicester PhD studentship and International Mentoring Travel Award by American Heart Association", sb);
        assertEquals(sb.toString(),  "University of Leicester#American Heart Association" );
    }

    /**
     * Test of htmlSponsor method, of class MatchingTest.
     */
    @org.junit.Test
    public void testHtmlSponsor() throws Exception {
    }

    /**
     * Test of buildResponse method, of class MatchingTest.
     */
    @org.junit.Test
    public void testBuildResponse() throws Exception {
    }

    /**
     * Test of withinMatch method, of class MatchingTest.
     */
    @org.junit.Test
    public void testWithinMatch() {
    }

    /**
     * Test of matchWithSplit method, of class MatchingTest.
     */
    @org.junit.Test
    public void testMatchWithSplit() {
    }

    /**
     * Test of exactMatch method, of class MatchingTest.
     */
    @org.junit.Test
    public void testExactMatch() {
    }
    
}
