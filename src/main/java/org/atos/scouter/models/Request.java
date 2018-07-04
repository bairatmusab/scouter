
package org.atos.scouter.models;

import java.util.Date;
import java.util.Objects;

/**
 * Request class which represents an anomaly
 *
 * @version 1.0
 */
public class Request {
    /**
     * Beginning of Events to get from database
     */
    private final Date start;
    /**
     * End of Events to get from database
     */
    private final Date end;
    /**
     * {@link BoundingBox} of Event to get
     */
    private final BoundingBox boundingBox;
    /**
     * The reception date of the request
     */
    private final Date requestReception;

    /**
     * The Request class constructor
     *
     * @param start            is the starting date of an anomaly
     * @param end              is the end date of an anomaly
     * @param boundingBox      coordinates
     * @param requestReception is the reception date of the request
     * @throws NullPointerException if one of params is null
     */
    public Request(Date start, Date end, BoundingBox boundingBox, Date requestReception) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        Objects.requireNonNull(boundingBox);
        Objects.requireNonNull(requestReception);

        this.start = start;
        this.end = end;
        this.boundingBox = boundingBox;
        this.requestReception = requestReception;
    }

    /**
     * Get starting date
     *
     * @return the starting date of an anomaly
     * @see Request#start
     */
    public Date getStart() {
        return start;
    }

    /**
     * Get ending date
     *
     * @return the end date of an anomaly
     * @see Request#end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Get {@link BoundingBox} of Request
     *
     * @return the coordinates of the {@link BoundingBox}
     * @see Request#boundingBox
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Represent that object in string
     *
     * @return String that represents this {@link Request}
     * @see Request#start
     * @see Request#end
     * @see Request#boundingBox
     * @see Request#requestReception
     */
    @Override
    public String toString() {
        return "Request{" +
                "start=" + start +
                ", end=" + end +
                ", boundingBox=" + boundingBox +
                ", requestReception=" + requestReception +
                '}';
    }
}
