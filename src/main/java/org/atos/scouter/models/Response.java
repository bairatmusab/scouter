
package org.atos.scouter.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Response class which represents the response of an anomaly
 *
 * @version 1.0
 */
public class Response {
    /**
     * List of all events that match {@link Request}
     *
     * @see Event
     * @see Response#getEvents()
     */
    private final List<Event> events;
    /**
     * Request to get List of {@link Event}
     *
     * @see Request
     * @see Response#getRequest()
     */
    private final Request request;

    /**
     * The response constructor
     *
     * @param events  list of events
     * @param request an anomaly
     * @throws NullPointerException if events or request is null
     */
    public Response(List<Event> events, Request request) {
        Objects.requireNonNull(events);
        Objects.requireNonNull(request);
        this.events = events;
        this.request = request;
    }

    /**
     * Get list of {@link Event} which {@link Request} found
     *
     * @return a list of {@link Event}
     */
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Get {@link Request}
     *
     * @return {@link Request} to find {@link Event}
     */
    public Request getRequest() {
        return request;
    }
}
