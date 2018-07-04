
package org.atos.scouter.dao;

import org.atos.scouter.models.Event;

/**
 * Functional interface called in {@link DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)}
 * after insertion in database
 *
 * @version 1.0
 */
@FunctionalInterface
public interface DatabaseWriterCallback {
    /**
     * Method called after insertion in database {@link DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)}
     *
     * @param t Throwable in case of error
     */
    void onResult(Throwable t);
}
