package org.atos.scouter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class to read all keywords used to scrap the web.
 */
public class KeywordsReader {

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
	private static final Logger LOGGER = LoggerFactory.getLogger(KeywordsReader.class);

	/**
	 * Public constructor
	 */
	private KeywordsReader() {
		
    }

	/**
	 * Method to read all keywords
	 * @return list of string keywords.
	 */
	public static String[] readKeywords(){
		List<String> keywords =  ConceptTopologyUtility.getKeywords();
		return keywords.toArray(new String[0]);

	}
}
