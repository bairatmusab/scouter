

package org.atos.scouter.dao;

/**
 * FunctionalInterface use after reading in database
 *
 * @version 1.0
 */
@FunctionalInterface
public interface DatabaseReaderCallback {
    /**
     * Method call after reading in database
     *
     * @param t      Throwable in case of error during reading
     * @param result Result of database request
     */
    void onResult(Throwable t, String result);
}
