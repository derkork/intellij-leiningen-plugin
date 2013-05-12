package de.janthomae.leiningenplugin.module.model;

import de.janthomae.leiningenplugin.leiningen.LeiningenAPI;
import de.janthomae.leiningenplugin.utils.ClassPathUtils;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 4/14/13
 * Time: 6:54 PM
 *
 * Utilities for creating ModuleInformation.
 */
public class ModuleInformationUtils {

    //
    // These are private as you shouldn't be interacting directly with the LeiningenAPI
    //
    private final static String LEIN_MODULE_NAME = "name";
    private final static String LEIN_MODULE_VERSION = "version";
    private final static String LEIN_MODULE_GROUP = "group";

    /**
     * Create Module Information from a Project File.
     *
     * @param path The patch of the project.clj file.
     * @return The Module Information from a project file.
     */
    public ModuleInformation fromProjectFile(String path) {
        ClassPathUtils.getInstance().switchToPluginClassLoader();
        Map projectMap = LeiningenAPI.loadProject(path);

        ModuleInformation result = new ModuleInformation();
        result.setGroupId((String) projectMap.get(LEIN_MODULE_GROUP));
        result.setArtifactId((String) projectMap.get(LEIN_MODULE_NAME));
        result.setVersion((String) projectMap.get(LEIN_MODULE_VERSION));
        result.setProjectFilePath(path);

        return result;
    }

}
