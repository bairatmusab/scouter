

package org.atos.scouter.Main;

import org.apache.commons.cli.MissingArgumentException;
import org.atos.scouter.TikaTools.PdfExtracter;
import org.atos.scouter.util.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {


    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);



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
    }

}

