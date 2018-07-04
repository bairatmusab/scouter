
package org.atos.scouter.dao;

import org.atos.scouter.communication.ICommunication;
import org.atos.scouter.models.Request;

/**
 * Interface of {@link DatabaseReader DatabaseReader} given to {@link ICommunication}
 *
 * @version 1.0
 */
@FunctionalInterface
public interface IDatabaseReader {
    /**
     * Get Event based on {@link Request}
     *
     * @param request  {@link Request} Request to execute on database
     * @param callback {@link DatabaseReaderCallback} Callback use after response of request
     */
    void getEvent(Request request, DatabaseReaderCallback callback);
}
