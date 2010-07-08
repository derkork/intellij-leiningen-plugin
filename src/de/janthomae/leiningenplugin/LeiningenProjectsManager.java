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
    private LeiningenProjectsManagerWatcher myWatcher;
    private List<LeiningenProjectsManagerListener> listeners = new ArrayList<LeiningenProjectsManagerListener>();
    public static final String PROJECT_CLJ = "project.clj";

    protected LeiningenProjectsManager(Project project) {
        super(project);
    }

    public static LeiningenProjectsManager getInstance(Project p) {
        return p.getComponent(LeiningenProjectsManager.class);
    }

    @Override
    public void initComponent() {
        runWhenInitialized(myProject, new Runnable() {
            public void run() {
                findProjectFiles();
                myWatcher = new LeiningenProjectsManagerWatcher(myProject, LeiningenProjectsManager.this);
                myWatcher.start();
            }
        });
    }

    private void findProjectFiles() {
        myLeiningenProjects.clear();
        VirtualFile projectFile = myProject.getBaseDir().findChild(PROJECT_CLJ);
        if (projectFile != null) {

            addLeiningenProject(new LeiningenProject(projectFile));
        }

    }

    public void addLeiningenProject(LeiningenProject leiningenProject) {
        myLeiningenProjects.add(leiningenProject);
        notifyListeners();
    }

    public void removeLeiningenProject(LeiningenProject leiningenProject) {
        myLeiningenProjects.remove(leiningenProject);
        notifyListeners();
    }

    public LeiningenProject byPath(String path) {
        for (LeiningenProject myLeiningenProject : myLeiningenProjects) {
            if (myLeiningenProject.getVirtualFile().getPath().equals(path)) {
                return myLeiningenProject;
            }
        }
        return null;
    }

    public List<LeiningenProject> getLeiningenProjects() {
        return myLeiningenProjects;
    }

    public boolean hasProjects() {
        return !myLeiningenProjects.isEmpty();
    }

    public void addProjectsManagerListener(LeiningenProjectsManagerListener listener) {
        listeners.add(listener);
    }

    public void removeProjectsManagerListener(LeiningenProjectsManagerListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (LeiningenProjectsManagerListener listener : listeners) {
            listener.projectsChanged();
        }
    }

    public boolean isManagedFile(VirtualFile file) {
        for (LeiningenProject myLeiningenProject : myLeiningenProjects) {
            if (myLeiningenProject.getVirtualFile().equals(file)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProjectFile(VirtualFile file) {
        return file != null && !file.isDirectory() && file.exists() && file.getName().equals(PROJECT_CLJ);

    }
}
