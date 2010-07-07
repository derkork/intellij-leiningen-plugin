package de.janthomae.leiningenplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectsManager extends SimpleProjectComponent {
    private List<LeiningenProject> myLeiningenProjects = new ArrayList<LeiningenProject>();

    protected LeiningenProjectsManager(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        runWhenInitialized(myProject, new Runnable() {
            public void run() {
                findProjectFiles();
            }
        });
    }

    private void findProjectFiles() {
        myLeiningenProjects.clear();
        VirtualFile pom = myProject.getBaseDir().findChild("project.clj");
        if (pom != null) {

            myLeiningenProjects.add( new LeiningenProject(pom));
        }

    }

    public List<LeiningenProject> getLeiningenProjects() {
        return myLeiningenProjects;
    }

    public boolean hasProjects() {
        return !myLeiningenProjects.isEmpty();
    }
}
