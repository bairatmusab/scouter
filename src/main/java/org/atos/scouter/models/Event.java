
package org.atos.scouter.models;

import org.atos.scouter.util.PropertiesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Event class represents an event with starting date and end date
 * @version 1.0
 */
public class Event {
    /**
     * Properties of this class
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     * @see PropertiesManager#getInstance()
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();
    /**
     * Position of the {@link Event} represented by a BoundingBox
     *
     * @see Event#getLocation()
     */
    private LatLong[] location = new LatLong[0];
    /**
     * Description of an {@link Event}
     *
     * @see Event#getDescription()
     */
    private String description = "";
    /**
     * Source where {@link Event} provides
     *
     * @see Event#getSource()
     */
    private String source = "";
    /**
     * Beginning of {@link Event}
     *
     * @see Event#getStart()
     */
    private Date start = new Date();
    /**
     * Score associated to this {@link Event}
     *
     * @see Event#getScore()
     */
    private byte score = 0;
    /**
     * End of this {@link Event}
     *
     * @see Event#getEnd()
     */
    private Date end = new Date();

    private  String entityName = "";

    private  long sourceId = 0;

    private  boolean isVerified;

    private ArrayList<String> eventsKeyTerms;

    private boolean isMergedEvent = false;

    private boolean isParentEventWhenMerging = false;

    private ArrayList<Event> eventsMerged;

    private String sentimentSense;

    /**
     * Create an Event without score
     *
     * @param location    the location of the event, defined by a BondingBox (LatLong[])
     * @param start       The moment when the event begins
     * @param end         End of the event, or the current date
     * @param description the event content. For instance, the message of a tweet.
     * @param source      from which datasource the event is provided
     * @throws NullPointerException     if a param is null
     * @throws IllegalArgumentException if source is empty
     */
    public Event(LatLong[] location, Date start, Date end, String description, String source) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        Objects.requireNonNull(description);
        Objects.requireNonNull(source);
        if (source.isEmpty()) {
            throw new IllegalArgumentException("Source argument cannot be empty.");
        }
        if (!location[0].equals(location[location.length - 1])) {
            throw new IllegalArgumentException("BoundingBox is not closed.");
        }
        this.location = location;
        this.start = start;
        this.end = end;
        this.description = description;
        this.score = -1;
        this.source = source;
    }

    /**
     * Create an Event with a score
     *
     * @param location    the location of the event, defined by a BondingBox (LatLong[])
     * @param start       The moment when the event begins
     * @param end         End of the event, or the current date
     * @param description the event content. For instance, the message of a tweet.
     * @param score       Score of this event between 0 and 100
     * @param source      from which datasource the event is provided
     * @throws NullPointerException     If one params is null
     * @throws IllegalArgumentException If source is empty or score is not between 0 and 100
     */
    public Event(LatLong[] location, Date start, Date end, String description, byte score, String source) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        Objects.requireNonNull(description);
        Objects.requireNonNull(source);
        if (source.isEmpty()) {
            throw new IllegalArgumentException("Source argument cannot be empty.");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score need to be between 0 and 100.");
        }
        if (!location[0].equals(location[location.length - 1])) {
            throw new IllegalArgumentException("BoundingBox is not closed.");
        }
        this.location = location;
        this.start = start;
        this.end = end;
        this.description = description;
        this.score = score;
        this.source = source;
    }

    /**
     * Create an Event without score
     *
     * @param location    the location of the event, defined by a LatLong
     * @param start       The moment when the event begins
     * @param end         End of the event, or the current date
     * @param description the event content. For instance, the message of a tweet.
     * @param source      from which datasource the event is provided
     * @throws NullPointerException     If a param is null
     * @throws IllegalArgumentException If source is empty
     */
    public Event(LatLong location, Date start, Date end, String description, String source) {
        this(new LatLong[]{location}, start, end, description, source);
    }

    /**
     * Create an Event with a score
     *
     * @param location    the location of the event, defined by a LatLong
     * @param start       The moment when the event begins
     * @param end         End of the event, or the current date
     * @param description the event content. For instance, the message of a tweet.
     * @param score       Score of this event between 0 and 100
     * @param source      from which datasource the event is provided
     * @throws NullPointerException     If one params is null
     * @throws IllegalArgumentException If source is empty or score is not between 0 and 100
     */
    public Event(LatLong location, Date start, Date end, String description, byte score, String source) {
        this(new LatLong[]{location}, start, end, description, score, source);
    }

    public Event(){

    }

    /**
     * Get location of this event
     *
     * @return LatLong[] to represent the position of this Event
     * @see Event#location
     */
    public LatLong[] getLocation() {
        return location;
    }

    /**
     * Get the moment when the event begins
     *
     * @return start date
     * @see Event#start
     */
    public Date getStart() {
        return start;
    }

    /**
     * Get the moment when the end of the event
     *
     * @return end date
     * @see Event#end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Get description of this event
     *
     * @return description
     * @see Event#description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get source from which datasource, this event is provided
     *
     * @return source
     * @see Event#source
     */
    public String getSource() {
        return source;
    }


    public boolean isMergedEvent() {
        return isMergedEvent;
    }

    public void setMergedEvent(boolean mergedEvent) {
        isMergedEvent = mergedEvent;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getEntityName() {
        return entityName;
    }

    public long getSourceId() {
        return sourceId;
    }

    public boolean getVerified() {
        return isVerified;
    }


    public ArrayList<String> getEventsKeyTerms() {
        return eventsKeyTerms;
    }

    public void setEventsKeyTerms(ArrayList<String> eventsKeyTerms) {
        this.eventsKeyTerms = eventsKeyTerms;
    }

    /**
     * Get score of this event after score processing
     *
     * @return score or -1 if this event hasn't been analysed by score processor
     * @see Event#score
     */
    public byte getScore() {
        return score;
    }

    /**
     * Generated method to test if {@link Event} are same
     *
     * @param o Other {@link Event} to compare
     * @return true if there are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        if (score != event.score) return false;
        if (!Arrays.equals(location, event.location)) return false;
        if (start != null ? !start.equals(event.start) : event.start != null) return false;
        if (end != null ? !end.equals(event.end) : event.end != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        return source != null ? source.equals(event.source) : event.source == null;
    }

    /**
     * Generated method
     *
     * @return hashcode of {@link Event}
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(location);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) score;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    /**
     * Represent this {@link Event} as a String
     *
     * @return a string which contains information about an event
     * @see Event#location
     * @see Event#start
     * @see Event#end
     * @see Event#description
     * @see Event#score
     * @see Event#source
     */
    @Override
    public String toString() {
        return "Event{" +
                "location=" + Arrays.stream(location).map(LatLong::toString).collect(Collectors.joining(",", "[", "]")) +
                ", start=" + start +
                ", end=" + end +
                ", description='" + description + '\'' +
                ", score=" + score +
                ", source = " + source +
                '}';
    }

    /**
     * Get score min for an event
     *
     * @return Score min to apply to an event
     * @see Event#PROPERTIES_MANAGER
     */
    public static byte getScoreMin() {
        try {
            return Byte.parseByte(PROPERTIES_MANAGER.getProperty("score.min"));
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    /**
     * Get score max for an event
     *
     * @return Score max to apply to an event
     * @see Event#PROPERTIES_MANAGER
     */
    public static byte getScoreMax() {
        try {
            return Byte.parseByte(PROPERTIES_MANAGER.getProperty("score.max"));
        } catch (IllegalArgumentException e) {
            return 100;
        }
    }

    public boolean isParentEventWhenMerging() {
        return isParentEventWhenMerging;
    }

    public void setParentEventWhenMerging(boolean parentEventWhenMerging) {
        isParentEventWhenMerging = parentEventWhenMerging;
    }

    public ArrayList<Event> getEventsMerged() {
        return eventsMerged;
    }

    public void setEventsMerged(ArrayList<Event> eventsMerged) {
        this.eventsMerged = eventsMerged;
    }

    public String getSentimentSense() {
        return sentimentSense;
    }

    public void setSentimentSense(String sentimentSense) {
        this.sentimentSense = sentimentSense;
    }
}
