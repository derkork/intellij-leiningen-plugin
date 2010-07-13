package de.janthomae.leiningenplugin.project;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.janthomae.leiningenplugin.LeiningenConstants;
import de.janthomae.leiningenplugin.LeiningenUtil;
import de.janthomae.leiningenplugin.SimpleProjectComponent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
@State(name = "LeiningenProjectsManager", storages = {@Storage(id = "default", file = "$PROJECT_FILE$")})
public class LeiningenProjectsManager extends  SimpleProjectComponent implements PersistentStateComponent<LeiningenProjectsManagerState> {
    private List<LeiningenProject> myLeiningenProjects = new ArrayList<LeiningenProject>();
    private LeiningenProjectsManagerWatcher myWatcher;
    private List<LeiningenProjectsManagerListener> listeners = new ArrayList<LeiningenProjectsManagerListener>();

    public static LeiningenProjectsManager getInstance(Project p) {
        return p.getComponent(LeiningenProjectsManager.class);
    }

    public static boolean isProjectFile(VirtualFile file) {
        return file != null && !file.isDirectory() && file.exists() &&
                file.getName().equals(LeiningenConstants.PROJECT_CLJ);
    }

    protected LeiningenProjectsManager(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        LeiningenUtil.runWhenInitialized(myProject, new Runnable() {
            public void run() {
                myWatcher = new LeiningenProjectsManagerWatcher(myProject, LeiningenProjectsManager.this);
                myWatcher.start();
            }
        });
    }

    public void addProjectsManagerListener(LeiningenProjectsManagerListener listener) {
        listeners.add(listener);
    }

    public LeiningenProject byPath(String path) {
        for (LeiningenProject myLeiningenProject : myLeiningenProjects) {
            if (myLeiningenProject.getVirtualFile().getPath().equals(path)) {
                return myLeiningenProject;
            }
        }
        return null;
    }

    public boolean hasProjects() {
        return !myLeiningenProjects.isEmpty();
    }

    public List<Module> importLeiningenProject(LeiningenProject project) {
        List<Module> result = new ArrayList<Module>();
        result.add(project.reimport());
        if ( !hasProject( project) ) {
            addLeiningenProject(project);
        }
        return result;
    }

    public boolean hasProject(LeiningenProject project) {
        return myLeiningenProjects.contains(project);
    }

    private void addLeiningenProject(LeiningenProject leiningenProject) {
        myLeiningenProjects.add(leiningenProject);
        notifyListeners();
    }

    public boolean isManagedFile(VirtualFile file) {
        for (LeiningenProject myLeiningenProject : myLeiningenProjects) {
            if (myLeiningenProject.getVirtualFile().equals(file)) {
                return true;
            }
        }
        return false;
    }

    public void removeProjectsManagerListener(LeiningenProjectsManagerListener listener) {
        listeners.remove(listener);
    }

    public List<LeiningenProject> getLeiningenProjects() {
        return myLeiningenProjects;
    }

    private void findProjectFiles() {
        myLeiningenProjects.clear();
        VirtualFile projectFile = myProject.getBaseDir().findChild(LeiningenConstants.PROJECT_CLJ);
        if (projectFile != null) {
            addLeiningenProject(new LeiningenProject(projectFile, myProject));
        }
    }

    private void removeLeiningenProject(LeiningenProject leiningenProject) {
        myLeiningenProjects.remove(leiningenProject);
        notifyListeners();
    }

    private void notifyListeners() {
        for (LeiningenProjectsManagerListener listener : listeners) {
            listener.projectsChanged();
        }
    }

    public LeiningenProjectsManagerState getState() {
        LeiningenProjectsManagerState state = new LeiningenProjectsManagerState();
        for (LeiningenProject leiningenProject : myLeiningenProjects) {
            state.projectFiles.add(leiningenProject.getVirtualFile().getUrl());
        }
        return state;
    }

    public void loadState(LeiningenProjectsManagerState leiningenProjectsManagerState) {
        final List<LeiningenProject> result = new ArrayList<LeiningenProject>();
        for (String projectFile : leiningenProjectsManagerState.projectFiles) {
            try {
                VirtualFile vf = VfsUtil.findFileByURL(new URL(projectFile));
                if ( vf != null ) {
                    LeiningenProject forReimport = new LeiningenProject(vf, myProject );
                    result.add(forReimport);
                }
            } catch (MalformedURLException e) {
                // hmm ok
            }
        }
        LeiningenUtil.runWhenInitialized(myProject, new Runnable() {
            public void run() {
                for (LeiningenProject leiningenProject : result) {
                    leiningenProject.reimport();
                    addLeiningenProject(leiningenProject);
                }
            }
        });
    }
}
