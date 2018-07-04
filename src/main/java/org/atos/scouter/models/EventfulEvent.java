package org.atos.scouter.models;

import java.util.Date;

/**
 * Class representing events retrieved from Eventful service.
 * Created by Musab on 31/03/2017.
 */

public class EventfulEvent {

    // Event description
    private String description;

    // Event title
    private String title;

    // Event time
    private Date time;

    /**
     * Event location
     * @see {@link LatLong}
     */
    private LatLong location;

    // Event city
    private String city;

    // Event country
    private String country;

    // Event address
    private String address;

    // Event source
    private String source;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
