package de.janthomae.leiningenplugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;

public class LeiningenUtil {
    public static boolean isNoBackgroundMode() {
        return ApplicationManager.getApplication().isUnitTestMode()
                || ApplicationManager.getApplication().isHeadlessEnvironment();
    }

    public static void runWhenInitialized(final Project project, final Runnable r) {
        if (project.isDisposed()) return;

        if (isNoBackgroundMode()) {
            r.run();
            return;
        }

        if (!project.isInitialized()) {
            StartupManager.getInstance(project).registerPostStartupActivity(r);
            return;
        }

        runDumbAware(project, r);
    }

    public static void runDumbAware(final Project project, final Runnable r) {
        if (r instanceof DumbAware) {
            r.run();
        } else {
            DumbService.getInstance(project).runWhenSmart(new Runnable() {
                public void run() {
                    if (project.isDisposed()) return;
                    r.run();
                }
            });
        }
    }

    public static void invokeLater(Project p, Runnable r) {
        invokeLater(p, ModalityState.defaultModalityState(), r);
    }

    public static void invokeLater(final Project p, final ModalityState state, final Runnable r) {
        if (isNoBackgroundMode()) {
            r.run();
        } else {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    if (p.isDisposed()) return;
                    r.run();
                }
            }, state);
        }
    }
}