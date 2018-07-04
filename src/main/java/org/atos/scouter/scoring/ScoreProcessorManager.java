

package org.atos.scouter.scoring;

import org.atos.scouter.models.Event;
import org.atos.scouter.util.PropertiesManager;
import org.atos.scouter.util.ClassManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Object which manage all {@link IScoreProcessor}
 *
 * @version 1.0
 */
public class ScoreProcessorManager {
    /**
     * Properties of this class
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     * @see PropertiesManager#getInstance()
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreProcessorManager.class);

    /**
     * ClassLoader of {@link ScoreProcessorManager}
     */
    private final ClassLoader parent = ScoreProcessorManager.class.getClassLoader();


    private IScoreProcessor genericProcessor;

    public ScoreProcessorManager() {
        instantiateScoreProcessors();
    }

    
    private void instantiateScoreProcessors(){
        genericProcessor = (IScoreProcessor) ClassManager.newInstance(EventScoreProcessor.class);
    }
    
    /**
     * Process NLP Algorithm to an event
     *
     * @param event {@link Event} to score
     * @return Copy of {@link Event} with a new score
     * @throws NullPointerException if event is null
     */
    public Event processScore(Event event) {
        Objects.requireNonNull(event);
        Event tmp = event;
        tmp = genericProcessor.processScore(tmp);
        return tmp;
    }


}
