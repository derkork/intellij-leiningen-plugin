package de.janthomae.leiningenplugin;

import com.intellij.openapi.actionSystem.DataKey;
import de.janthomae.leiningenplugin.project.LeiningenProject;

import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenDataKeys {
    public static final DataKey<List<String>> LEININGEN_GOALS = DataKey.create("LEININGEN_GOALS");
    public static final DataKey<LeiningenProject> LEININGEN_PROJECT = DataKey.create("LEININGEN_PROJECT");

}
