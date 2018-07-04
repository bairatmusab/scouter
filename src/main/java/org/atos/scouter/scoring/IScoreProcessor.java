

package org.atos.scouter.scoring;

import org.atos.scouter.models.Event;

/**
 * Interface to implement if you want to use your class as module of ScoreProcessor
 *
 * @version 1.0
 */
public interface IScoreProcessor {
    /**
     * This method create a score to the event in param
     *
     * @param event {@link Event} without score
     * @return {@link Event} with score gave by processScore
     */
    Event processScore(Event event);
}
