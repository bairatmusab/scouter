

package org.atos.scouter.Main;

import org.apache.commons.cli.MissingArgumentException;
import org.atos.scouter.TikaTools.PdfExtracter;
import org.atos.scouter.metrics.MetricsLogger;
import org.atos.scouter.util.ConceptTopologyUtility;
import org.atos.scouter.util.MetricsKeys;
import org.atos.scouter.util.PropertiesManager;
import org.atos.scouter.util.TopicExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {


    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    static MetricsLogger METRICS_LOGGER;


    /**
     * This method executes the program
     *
     * @param args producer module starting arguments
     */
    public static void main(String[] args) throws Exception {


        if (args.length == 0){
            throw new MissingArgumentException("Config path should be passed as parameter!");
        }

        String configPath = args[0];
        final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance(configPath);
        //METRICS_LOGGER = MetricsLogger.getMetricsLogger();

        PdfExtracter extracter = new PdfExtracter();
        extracter.extractText(PROPERTIES_MANAGER.getProperty("pdf.root.path"));

//        initializeConceptTopology();
//        buildTopicExtractionModel();

    }

    private static void initializeConceptTopology(){
        ConceptTopologyUtility.initilizeInputTopology();
        ConceptTopologyUtility.printKeywordsScoresMap();
    }

    private static void buildTopicExtractionModel() throws Exception {
        TopicExtractor topicExtractor = TopicExtractor.getInstance();

        long trainingStartTime = System.currentTimeMillis();
        topicExtractor.trainTopicExtractionModel();
        METRICS_LOGGER.log(MetricsKeys.TOPIC_EXTRACTION_TRAINING_TIME, System.currentTimeMillis() - trainingStartTime);
    }

}

