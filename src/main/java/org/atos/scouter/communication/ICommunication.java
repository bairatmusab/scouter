

package org.atos.scouter.communication;

import org.atos.scouter.dao.DatabaseReader;
import org.atos.scouter.dao.IDatabaseReader;

/**
 * Interface to implement if you want to use your class as module of communication
 *
 * @version 1.0
 */
public interface ICommunication {
    /**
     * Called at startup
     *
     * @param databaseReader Implementation of {@link DatabaseReader DatabaseReader}
     * @see IDatabaseReader
     */
    void start(IDatabaseReader databaseReader);

    /**
     * Called at closure
     */
    void close();

    /**
     * Called to know if module should be launch
     *
     * @return true if Communication module must be started
     */
    boolean isActive();
}
