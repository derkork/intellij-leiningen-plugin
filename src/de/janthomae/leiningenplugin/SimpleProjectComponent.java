package de.janthomae.leiningenplugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

public abstract class SimpleProjectComponent extends AbstractProjectComponent {
    protected SimpleProjectComponent(Project project) {
        super(project);
    }

    protected boolean isNormalProject() {
        return !isUnitTestMode() && !isHeadless() && !isDefault();
    }

    protected boolean isUnitTestMode() {
        return ApplicationManager.getApplication().isUnitTestMode();
    }

    protected boolean isHeadless() {
        return ApplicationManager.getApplication().isHeadlessEnvironment();
    }

    protected boolean isDefault() {
        return myProject.isDefault();
    }


}
