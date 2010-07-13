package de.janthomae.leiningenplugin.project;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessorBase;
import de.janthomae.leiningenplugin.LeiningenConstants;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectOpenProcessor extends ProjectOpenProcessorBase {

    public LeiningenProjectOpenProcessor(LeiningenProjectBuilder builder) {
        super(builder);
    }

    public LeiningenProjectBuilder getBuilder() {
        return (LeiningenProjectBuilder) super.getBuilder();
    }

    @Nullable
    public String[] getSupportedExtensions() {
        return new String[]{LeiningenConstants.PROJECT_CLJ};
    }

    public boolean doQuickImport(VirtualFile file, WizardContext wizardContext) {
        getBuilder().setProjectFile(file);
        wizardContext.setProjectName(getBuilder().getSuggestedProjectName());
        return true;
    }
}

