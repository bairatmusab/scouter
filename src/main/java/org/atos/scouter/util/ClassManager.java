

package org.atos.scouter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * ClassManager which provides static method to apply to Class
 *
 * @version 1.0
 * @see JarLoader
 */
public class ClassManager {
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassManager.class);

    /**
     * Private constructor to prevent instantiation
     */
    private ClassManager() {

    }

    /**
     * Create a new instance of clazz
     *
     * @param clazz Class to instantiate
     * @return New object instantiated of clazz
     * @throws NullPointerException     if clazz is null
     * @throws IllegalArgumentException if clazz cannot be instantiated
     */
    public static Object newInstance(Class clazz) {
        Objects.requireNonNull(clazz);
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Class : " + clazz.getName() + " cannot be cast");
        }
    }

    /**
     * Load class which has className as class name
     *
     * @param className Class name to load
     * @param child     ClassLoader used to load this className
     * @return Class if it's can be instantiate or null otherwise
     * @throws NullPointerException if className or child is null
     */
    public static Class<?> loadClass(String className, ClassLoader child) {
        Objects.requireNonNull(className);
        Objects.requireNonNull(child);
        try {
            return Class.forName(className, true, child);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Class " + className + " cannot be found");
            return null;
        } catch (NoClassDefFoundError e1) {
            LOGGER.warn("ClassDef" + className + " cannot be found");
            return null;
        }
    }

    /**
     * Test if clazz implements interfaceClass
     *
     * @param clazz          Class to test
     * @param interfaceClass Interface to test if it's implement
     * @return True if clazz implement interfaceClass
     * @throws NullPointerException if interfaceClass is null
     */
    public static boolean implementInterface(Class<?> clazz, Class<?> interfaceClass) {
        if (clazz == null) return false;
        Objects.requireNonNull(interfaceClass);
        return interfaceClass.isAssignableFrom(clazz);
    }
}