

package org.atos.scouter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Load configuration for a class
 *
 * @version 1.0
 */
public class PropertiesManager {

    private static PropertiesManager propertiesManager;

    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    /**
     * Constant value {@value DEFAULT_PATH}
     *
     */

//    private static final String DEFAULT_PATH = "scouter/src/main/resources/config.properties";
    /**
     * Objects which store value of configuration file
     *
     * @see PropertiesManager#getProperty(String)
     */
    private static Properties config = new Properties();

    /**
     * Singleton {@link PropertiesManager}
     *
     * @param path Path to config file
     * @throws NullPointerException if path is null
     */
    private PropertiesManager(String path) {
        Objects.requireNonNull(path);
        Path configLocation = Paths.get(path);

        try (InputStream stream = Files.newInputStream(configLocation)) {
            config.load(stream);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Get instance for {@link PropertiesManager}
     *
     * @return An instance of {@link PropertiesManager}
     */
    public static PropertiesManager getInstance() {
        return getInstance("");
    }

    /**
     * Get instance for {@link PropertiesManager}, one per class
     *
     * @param path  Path to load configuration for this clazz
     * @return An instance of {@link PropertiesManager}
     */
    public static PropertiesManager getInstance(String path) {
        Objects.requireNonNull(path);
        if (propertiesManager == null) {
            propertiesManager = new PropertiesManager(path);
        }
        return propertiesManager;
    }


    /**
     * Get property
     *
     * @param property Property to get from configuration file
     * @return Value of property
     * @throws NullPointerException     if property is null
     * @throws IllegalArgumentException if property is not set in property file
     * @see PropertiesManager#config
     */
    public String getProperty(String property) {
        Objects.requireNonNull(property);
        String value = config.getProperty(property);
        if (value == null) {
            throw new IllegalArgumentException("Property not found : " + property);
        }
        return value;
    }

    /**
     * Get property
     *
     * @param property     Property to get from configuration file
     * @param defaultValue Default value to return
     * @return Value of property
     * @throws NullPointerException if property is null
     */
    public String getPropertyOrDefault(String property, String defaultValue) {
        Objects.requireNonNull(property);
        String value = config.getProperty(property);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    public static  void printAll(){

        for(Map.Entry<Object, Object> e : config.entrySet()) {
            System.out.println(e);
        }
    }
}
