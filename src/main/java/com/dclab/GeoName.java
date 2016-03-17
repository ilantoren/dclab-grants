/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.persistence.Transient;

/**
 *
 * @author richardthorne
 *
 * geonameid : integer id of record in geonames database name : name of
 * geographical point (utf8) varchar(200) asciiname : name of geographical point
 * in plain ascii characters, varchar(200) alternatenames : alternatenames,
 * comma separated, ascii names automatically transliterated, convenience
 * attribute from alternatename table, varchar(10000) latitude : latitude in
 * decimal degrees (wgs84) longitude : longitude in decimal degrees (wgs84)
 * feature class : see http://www.geonames.org/export/codes.html, char(1)
 * feature code : see http://www.geonames.org/export/codes.html, varchar(10)
 * country code : ISO-3166 2-letter country code, 2 characters cc2 : alternate
 * country codes, comma separated, ISO-3166 2-letter country code, 200
 * characters admin1 code : fipscode (subject to change to iso code), see
 * exceptions below, see file admin1Codes.txt for display names of this code;
 * varchar(20) admin2 code : code for the second administrative division, a
 * county in the US, see file admin2Codes.txt; varchar(80) admin3 code : code
 * for third level administrative division, varchar(20) admin4 code : code for
 * fourth level administrative division, varchar(20) population : bigint (8 byte
 * int) elevation : in meters, integer dem : digital elevation model, srtm3 or
 * gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca
 * 900mx900m) area in meters, integer. srtm processed by cgiar/ciat. timezone :
 * the timezone id (see file timeZone.txt) varchar(40) modification date : date
 * of last modification in yyyy-MM-dd format
 *
 */
@Entity
public class GeoName implements Serializable {

    @Transient
    DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer geonameid;
    private String name;
    private String asciiname;
    @Column(name = "alternatenames", columnDefinition = "LongBlob")
    @Lob
    private char[] alternatenames;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String feature_class;
    private String feature_code;
    private String country_code;
    private String cc2;
    private String admin1;
    private String admin2;
    private String admin3;
    private String admin4;
    private BigDecimal population;
    private BigDecimal elevation;
    private String dem;
    private String timezone;
    @Temporal(DATE)
    private Date mod_date;

    public Integer getGeonameid() {
        return geonameid;
    }

    public void setGeonameid(Integer geonameid) {
        this.geonameid = geonameid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsciiname() {
        return asciiname;
    }

    public void setAsciiname(String asciiname) {
        this.asciiname = asciiname;
    }

    public String getAlternatenames() {
        return alternatenames.toString();
    }

    public void setAlternatenames(String alternatenames) {
        String[] flds = alternatenames.split(",");
        int len = flds.length > 20 ? 20 : flds.length;
        String load = Arrays.stream(flds, 0, len).collect(Collectors.joining(","));
        this.alternatenames = load.toCharArray();
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getFeature_class() {
        return feature_class;
    }

    public void setFeature_class(String feature_class) {
        this.feature_class = feature_class;
    }

    public String getFeature_code() {
        return feature_code;
    }

    public void setFeature_code(String feature_code) {
        this.feature_code = feature_code;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCc2() {
        return cc2;
    }

    public void setCc2(String cc2) {
        this.cc2 = cc2;
    }

    public String getAdmin1() {
        return admin1;
    }

    public void setAdmin1(String admin1) {
        this.admin1 = admin1;
    }

    public String getAdmin2() {
        return admin2;
    }

    public void setAdmin2(String admin2) {
        this.admin2 = admin2;
    }

    public String getAdmin3() {
        return admin3;
    }

    public void setAdmin3(String admin3) {
        this.admin3 = admin3;
    }

    public String getAdmin4() {
        return admin4;
    }

    public void setAdmin4(String admin4) {
        this.admin4 = admin4;
    }

    public BigDecimal getPopulation() {
        return population;
    }

    public void setPopulation(BigDecimal population) {
        this.population = population;
    }

    public BigDecimal getElevation() {
        return elevation;
    }

    public void setElevation(BigDecimal elevation) {
        this.elevation = elevation;
    }

    public String getDem() {
        return dem;
    }

    public void setDem(String dem) {
        this.dem = dem;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getMod_date() {
        return mod_date;
    }

    public void setMod_date(Date mod_date) {
        this.mod_date = mod_date;
    }

    public GeoName() {
    }

    ;
    public GeoName(String[] vals) throws ParseException {
        geonameid = Integer.parseInt(vals[0]);
        name = vals[1];
        asciiname = vals[2];
        int len = vals[3].length() > 200 ? 200 : vals[3].length();
        setAlternatenames(vals[3]);
        try {
            latitude = BigDecimal.valueOf(Double.parseDouble(vals[4]));
            longitude = BigDecimal.valueOf(Double.parseDouble(vals[5]));
            if (vals[18].isEmpty()) {

            } else {
                mod_date = dtf.parse(vals[18]);
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(GeoName.class.getName()).log(Level.WARNING, "no latitude or longitutde for record {0}", geonameid);
        }
        feature_class = vals[6];
        feature_code = vals[7];
        country_code = vals[8];
        cc2 = vals[9];
        admin1 = vals[10];
        admin2 = vals[11];
        admin3 = vals[12];
        admin4 = vals[13];
        if (!vals[14].isEmpty()) {
            population = BigDecimal.valueOf(Long.parseLong(vals[14]));
        }

        if (!vals[15].isEmpty()) {
            elevation = BigDecimal.valueOf(Long.parseLong(vals[15]));
        }

        dem = vals[16];
        timezone = vals[17];

    }
}
