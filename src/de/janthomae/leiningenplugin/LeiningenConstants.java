package de.janthomae.leiningenplugin;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public interface LeiningenConstants {
    String[] GOALS = new String[]{
            "pom", "help", "upgrade", "install", "jar", "deps", "uberjar", "test", "clean", "compile", "version"};
    String PROJECT_CLJ = "project.clj";
}
