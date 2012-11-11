package de.janthomae.leiningenplugin.utils;

import de.janthomae.leiningenplugin.LeiningenConstants;

/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 11/15/12
 * Time: 8:53 PM
 * <p/>
 * Utility class to manipulate the classpath for exceptional cases (ie. Can't find the Clojure Libraries at runtime).
 */
public class ClassPathUtils {
    private static ClassPathUtils ourInstance = new ClassPathUtils();

    public static ClassPathUtils getInstance() {
        return ourInstance;
    }

    private ClassPathUtils() {

    }

    /**
     * Switch the current thread's class loader to the Plugin Classloader which has all of the Leiningen Plugin classes
     * available.
     * <p/>
     * This needs to be done until calling Thread.currentThread returns the Plugin ClassLoader.
     * This was added because calls to Thread.currentThread were returning the IDE's SDK classloader which did not contain any of the plugins classes.
     */
    public void switchToPluginClassLoader() {
        Thread.currentThread().setContextClassLoader(LeiningenConstants.class.getClassLoader());
    }
}
