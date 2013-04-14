package de.janthomae.leiningenplugin.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import de.janthomae.leiningenplugin.LeiningenConstants;
import de.janthomae.leiningenplugin.project.wizard.LeiningenProjectImportWizardStep;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 4/14/13
 * Time: 12:54 PM
 *
 * Plug into the 'Import Project' workflow of IDEA.  Allows for the 'project.clj' to be an option for a file that can
 * be imported as an IntelliJ project.
 *
 */
public class LeiningenProjectImportProvider extends ProjectImportProvider{

    protected LeiningenProjectImportProvider(LeiningenProjectBuilder builder){
        super(builder);
    }

    @Override
    public ModuleWizardStep[] createSteps(WizardContext context) {
        ProjectWizardStepFactory stepFactory = ProjectWizardStepFactory.getInstance();
        return new ModuleWizardStep[]{new LeiningenProjectImportWizardStep(context, context.getProjectFileDirectory()),stepFactory.createProjectJdkStep(context)};
    }

    @Override
    protected boolean canImportFromFile(VirtualFile file) {
        return LeiningenConstants.PROJECT_CLJ.equals(file.getName());
    }

    @Override
    public String getPathToBeImported(VirtualFile file) {
        return file.getPath();
    }

    @Nullable
    @Override
    public String getFileSample() {
        return "<b>Leiningen</b> project file (project.clj)";
    }
}
