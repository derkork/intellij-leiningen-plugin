package de.janthomae.leiningenplugin.project;

import com.intellij.ProjectTopics;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.update.MergingUpdateQueue;
import de.janthomae.leiningenplugin.LeiningenConstants;

import java.util.List;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectsManagerWatcher {

    private Project project;
    private final LeiningenProjectsManager manager;
    private MergingUpdateQueue myQueue;

    public LeiningenProjectsManagerWatcher(Project project, LeiningenProjectsManager manager) {
        this.project = project;
        this.manager = manager;
        myQueue = new MergingUpdateQueue(getClass() + ": Document changes queue",
                1000,
                false,
                MergingUpdateQueue.ANY_COMPONENT);
    }

    public synchronized void start() {
        final MessageBusConnection myBusConnection = project.getMessageBus().connect(myQueue);
        myBusConnection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            public void before(List<? extends VFileEvent> vFileEvents) {
            }

            public void after(List<? extends VFileEvent> vFileEvents) {
                for (VFileEvent vFileEvent : vFileEvents) {
//                    if (vFileEvent instanceof VFileMoveEvent) {
//                        if (isRelevant(vFileEvent.getPath())) {
//                            LeiningenProject leiningenProject = manager.byPath(vFileEvent.getPath());
//                            if (leiningenProject != null) {
//                                manager.removeLeiningenProject(leiningenProject);
//                            }
//
//                            VirtualFile newProjectFile = ((VFileMoveEvent)vFileEvent).getNewParent().findFileByRelativePath(((VFileMoveEvent) vFileEvent).getFile().getName());
//                            if ( newProjectFile != null ) {
//                                LeiningenProject newProject = new LeiningenProject(newProjectFile, theProject);
//                            }
//                        }
//                    }
//                    if (vFileEvent instanceof VFileDeleteEvent) {
//                        if (isRelevant(vFileEvent.getPath())) {
//                            LeiningenProject leiningenProject = manager.byPath(vFileEvent.getPath());
//                            if (leiningenProject != null) {
//                                manager.removeLeiningenProject(leiningenProject);
//                            }
//                        }
//                    }
                    if (vFileEvent instanceof VFileCreateEvent) {
                        if (isRelevant(vFileEvent.getPath())) {
                            manager.importLeiningenProject(
                                    vFileEvent.getFileSystem().findFileByPath(vFileEvent.getPath()),
                                    project
                            );
                        }
                    }
                }
            }

            private boolean isRelevant(String path) {
                return path != null && path.endsWith(LeiningenConstants.PROJECT_CLJ);
            }
        });

        myBusConnection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
            public void beforeRootsChange(ModuleRootEvent moduleRootEvent) {

            }

            public void rootsChanged(ModuleRootEvent moduleRootEvent) {

            }
        });

        myQueue.activate();
    }

}
