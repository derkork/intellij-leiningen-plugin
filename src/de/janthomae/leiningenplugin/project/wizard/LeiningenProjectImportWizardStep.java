package de.janthomae.leiningenplugin.project.wizard;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportWizardStep;
import de.janthomae.leiningenplugin.module.forms.LeiningenModuleInformationForm;
import de.janthomae.leiningenplugin.module.model.ModuleInformation;
import de.janthomae.leiningenplugin.module.model.ModuleInformationUtils;
import de.janthomae.leiningenplugin.project.LeiningenProjectBuilder;

import javax.swing.*;


/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 4/14/13
 * Time: 4:33 PM
 * <p/>
 * The first page in the 'Import Project' workflow.  After the user chooses the project.clj file location.
 */
public class LeiningenProjectImportWizardStep extends ProjectImportWizardStep {

    private final ModuleInformationUtils moduleInformationUtils;
    private final String projectFile;
    private ModuleInformation moduleInformation;
    private LeiningenModuleInformationForm moduleInformationForm;


    /**
     * Initialize the wizard step with wizard context and the path of the project.clj file.
     *
     * @param context The wizard context.
     * @param projectFile Absolute path to the project.clj file to import.
     */
    public LeiningenProjectImportWizardStep(WizardContext context,String projectFile) {
        super(context);
        this.projectFile = projectFile;

        moduleInformationForm = new LeiningenModuleInformationForm(false);
        this.moduleInformationUtils = new ModuleInformationUtils();
    }

    @Override
    public boolean validate() throws ConfigurationException {

        boolean result = true;

        moduleInformationForm.getData(moduleInformation);

        return result;
    }

    @Override
    public void updateStep() {
        String path = FileUtil.toSystemDependentName(projectFile);
        moduleInformation = moduleInformationUtils.fromProjectFile(path);
        moduleInformationForm.setData(moduleInformation);
    }

    @Override
    public JComponent getComponent() {
        return moduleInformationForm.getMainPanel();
    }

    @Override
    public void updateDataModel() {
        moduleInformationForm.getData(moduleInformation);

        //Tell the builder where the projectFile is.
        VirtualFile projectFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(moduleInformation.getProjectFilePath());
        getBuilder().setProjectFile(projectFile);

        //Point to the parent directory so we can create the .idea directory.
        VirtualFile parent = getBuilder().getProjectFile().getParent();
        String parentDir = FileUtil.toSystemDependentName(parent.getCanonicalPath());
        getWizardContext().setProjectFileDirectory(parentDir);
    }

    @Override
    protected LeiningenProjectBuilder getBuilder() {
        return (LeiningenProjectBuilder) super.getBuilder();
    }
}
