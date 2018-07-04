

package org.atos.scouter.communication;

import org.atos.scouter.models.Request;

/**
 * Functional Interface called after Kafka Request
 *
 * @version 1.0
 */
@FunctionalInterface
public interface IPollCallback {
    /**
     * Method call when a new request come
     *
     * @param request {@link Request} from Kafka
     * @return The result of Kafka Request
     */
    String onNewRequest(Request request);
}
