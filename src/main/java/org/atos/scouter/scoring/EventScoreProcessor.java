
package org.atos.scouter.scoring;

import org.atos.scouter.metrics.MetricsLogger;
import org.atos.scouter.models.Event;
import org.atos.scouter.util.ConceptTopologyUtility;
import org.atos.scouter.util.MetricsKeys;
import org.atos.scouter.util.PropertiesManager;
import org.atos.scouter.util.TopicExtractor;
import org.atos.scouter.util.nlp.OpenNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link IScoreProcessor} will be applied to different sources
 *
 * @version 1.0
 * @see IScoreProcessor
 */
public class EventScoreProcessor implements IScoreProcessor {
    /**
     * Properties of this module
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     * @see PropertiesManager#getInstance()
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();
    /**
     * Logger used to log all information in this module
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventScoreProcessor.class);
    /**
     * Object to add metrics from this class
     *
     * @see MetricsLogger#log(String, long)
     * @see MetricsLogger#getMetricsLogger()
     */
    private static final MetricsLogger METRICS_LOGGER = MetricsLogger.getMetricsLogger();
    /**
     * Single instance per thread of {@link OpenNLP}
     *
     * @see EventScoreProcessor#processScore(Event)
     */
    private final OpenNLP openNLP = OpenNLP.getOpenNLP(Thread.currentThread());
    /**
     * Max score to an {@link Event}
     *
     * @see EventScoreProcessor#processScore(Event)
     */
    private static final byte MAX = Event.getScoreMax();
    /**
     * Map word, score
     *
     * @see EventScoreProcessor#processScore(Event)
     */
    private final Map<String, Integer> rulesMap;


    TopicExtractor topicExtractor = TopicExtractor.getInstance();

    private static Long scoredEventsCount = 0L ;

    /**
     * Default constructor to initialize {@link EventScoreProcessor#rulesMap} with a {@link PropertiesManager}
     *
     * @see EventScoreProcessor#rulesMap
     * @see EventScoreProcessor#PROPERTIES_MANAGER
     */
    public EventScoreProcessor() {
        try {
            String ruleFilename = PROPERTIES_MANAGER.getProperty("event.rules.file");
            //rulesMap = RulesReader.parseJSONRules(ruleFilename);
            rulesMap = ConceptTopologyUtility.getWordsWeights();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Process score of an event from an {@link Event}
     *
     * @param event an {@link Event} without {@link Event#score}
     * @return Event with a score after {@link OpenNLP} processing
     * @throws NullPointerException if event is null
     * @see EventScoreProcessor#rulesMap
     * @see EventScoreProcessor#openNLP
     * @see EventScoreProcessor#MAX
     * @see Event
     */
    @Override
    public Event processScore(Event event) {
        Objects.requireNonNull(event);

        //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        long start = System.currentTimeMillis();
        String content = event.getDescription();
        List<String> eventList = openNLP.applyNLPlemma(content);


        //System.out.println("Tweet :"  + content);
        //System.out.println("--------------------------------------------------------");
        System.out.println(Arrays.toString(eventList.toArray()));

        //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        ArrayList<String> topics = topicExtractor.extractPhrases(event.getDescription());

        byte score = 0;
        for (String word : topics) {
            if (rulesMap.containsKey(word)) {
                score += rulesMap.get(word);
            }
        }
        if (score > MAX) {
            score = MAX;
        }

        Event newEvent = new Event(event.getLocation(), event.getStart(), event.getEnd(), event.getDescription(), score, event.getSource());
        newEvent.setEventsKeyTerms(topics);

        long time = System.currentTimeMillis() - start;
        scoredEventsCount++;

        logScoringAnalytics(event,time);
        return newEvent;
    }

    private void logScoringAnalytics(Event event , long scoringTime){
        METRICS_LOGGER.log(MetricsKeys.SCORED_EVENTS_COUNT,scoredEventsCount);
        if (event.getSource().equalsIgnoreCase("Facebook") || event.getSource().equalsIgnoreCase("Twitter")){
            METRICS_LOGGER.log(MetricsKeys.SCORING_TIME_SOCIAL, scoringTime);
        }else if(event.getSource().equalsIgnoreCase("DBpedia") || event.getSource().equalsIgnoreCase("OpenAgenda")){
            METRICS_LOGGER.log(MetricsKeys.SCORING_TIME_OPENDATA, scoringTime);
        }else{
            METRICS_LOGGER.log(MetricsKeys.SCORING_TIME_RSS, scoringTime);
        }
    }
}
