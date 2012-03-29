package de.janthomae.leiningenplugin.project;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
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
    private List<LeiningenProject> leiningenProjects = new ArrayList<LeiningenProject>();
    private LeiningenProjectsManagerWatcher watcher;
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
                watcher = new LeiningenProjectsManagerWatcher(myProject, LeiningenProjectsManager.this);
                watcher.start();
            }
        });
    }

    public void addProjectsManagerListener(LeiningenProjectsManagerListener listener) {
        listeners.add(listener);
    }

    public LeiningenProject byPath(String path) {
        for (LeiningenProject leiningenProject : leiningenProjects) {
            if (leiningenProject.getVirtualFile().getPath().equals(path)) {
                return leiningenProject;
            }
        }
        return null;
    }

    public boolean hasProjects() {
        return !leiningenProjects.isEmpty();
    }

    public List<Module> importLeiningenProject(VirtualFile projectFile, Project project) {
        List<Module> result = new ArrayList<Module>();
        LeiningenProject leiningenProject = null;

        try {
            leiningenProject = LeiningenProject.create(projectFile, project);
            Module m = leiningenProject.reimport();

            if ( !hasProject( leiningenProject) ) {
                addLeiningenProject(leiningenProject);
            }
        } catch (LeiningenProjectException e) {
            // Just do nothing for now
        }

        return result;
    }

    public boolean hasProject(LeiningenProject project) {
        return leiningenProjects.contains(project);
    }

    private void addLeiningenProject(LeiningenProject leiningenProject) {
        leiningenProjects.add(leiningenProject);
        notifyListeners();
    }

    public boolean isManagedFile(VirtualFile file) {
        for (LeiningenProject myLeiningenProject : leiningenProjects) {
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
        return leiningenProjects;
    }

//    private void findProjectFiles() {
//        leiningenProjects.clear();
//        VirtualFile projectFile = myProject.getBaseDir().findChild(LeiningenConstants.PROJECT_CLJ);
//        if (projectFile != null) {
//            addLeiningenProject(new LeiningenProject(projectFile, myProject));
//        }
//    }

    private void removeLeiningenProject(LeiningenProject leiningenProject) {
        leiningenProjects.remove(leiningenProject);
        notifyListeners();
    }

    private void notifyListeners() {
        for (LeiningenProjectsManagerListener listener : listeners) {
            listener.projectsChanged();
        }
    }

    public LeiningenProjectsManagerState getState() {
        LeiningenProjectsManagerState state = new LeiningenProjectsManagerState();
        for (LeiningenProject leiningenProject : leiningenProjects) {
            state.projectFiles.add(leiningenProject.getVirtualFile().getUrl());
        }
        return state;
    }

    public void loadState(LeiningenProjectsManagerState leiningenProjectsManagerState) {
        final List<LeiningenProject> result = new ArrayList<LeiningenProject>();
        for (String projectFile : leiningenProjectsManagerState.projectFiles) {
            VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl(projectFile);
            if ( vf != null ) {
                LeiningenProject forReimport = LeiningenProject.create(vf, myProject);
                result.add(forReimport);
            }
        }
        LeiningenUtil.runWhenInitialized(myProject, new Runnable() {
            public void run() {
                for (LeiningenProject leiningenProject : result) {
                    try {
                        leiningenProject.reimport();
                    } catch (LeiningenProjectException e) {
                        // Do nothing for now
                    }
                    addLeiningenProject(leiningenProject);
                }
            }
        });
    }
}
