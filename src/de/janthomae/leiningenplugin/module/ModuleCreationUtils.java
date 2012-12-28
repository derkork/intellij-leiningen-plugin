package de.janthomae.leiningenplugin.module;

import clojure.lang.LazySeq;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import de.janthomae.leiningenplugin.leiningen.LeiningenAPI;
import de.janthomae.leiningenplugin.utils.ClassPathUtils;
import gnu.trove.THashMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Chris Shellenbarger
 * Date: 12/6/12
 * Time: 5:53 AM
 * <p/>
 * Class used to assist in creation an IDEA module.  Extracted out of the project creation for reusability and testing purposes.
 */
public class ModuleCreationUtils {

    public final static String LEIN_COMPILE_PATH = "compile-path";
    public final static String LEIN_RESOURCE_PATHS = "resource-paths";
    public final static String LEIN_SOURCE_PATHS = "source-paths";
    public final static String LEIN_JAVA_SOURCE_PATHS = "java-source-paths";
    public final static String LEIN_TEST_PATHS = "test-paths";
    public final static String LEIN_PROJECT_NAME = "name";
    public final static String LEIN_PROJECT_VERSION = "version";
    public final static String LEIN_PROJECT_GROUP = "group";
    /**
     * Default Constructor - intentionally side-effect free.
     */
    public ModuleCreationUtils() {

    }

    /**
     * This function returns a list of virtual files pointing to the paths supporting a particular type (as defined in a leiningen project file: "resource-paths", "test-paths", and "source-paths" are examples).
     * It extracts them based off the corresponding values in leinProjectMap.
     *
     * @param type           The type of paths to extract from the project.  Corresponds with the names of the respective keys in a leiningen project file.  Examples: "resource-paths", "test-paths", or "source-paths"
     * @param leinProjectMap The map to extract values from.
     * @return A list of paths to folders of type.
     */
    public List<String> getPaths(String type, Map leinProjectMap) {
        LazySeq pathStrings = ((LazySeq) leinProjectMap.get(type));
        List<String> results = new ArrayList<String>();
        if (pathStrings != null) {
            for (Object obj : pathStrings) {
                String path = (String) obj;
                results.add(path);
            }
        }
        return results;
    }

    /**
     * Internal method used to add absolute paths to a content entry.
     * <p/>
     * This is done as part of support for multiple source entries.
     * <p/>
     * SIDE-EFFECT: Will modify contentEntry
     *
     * Note: This function will only add values to the content entry if they exist on the file system.  If the path doesn't
     * exist on the file system, then it will be ignored.
     *
     * @param contentEntry The contentEntry to be updated
     * @param paths        The list of paths to add.  These need to be absolute paths.
     * @param isTestSource Indicate if this is a test directory
     */
    protected void addSourceFoldersToContentEntry(final ContentEntry contentEntry, final List<String> paths, final boolean isTestSource) {

        for (final String path : paths) {
            new WriteAction() {
                @Override
                protected void run(Result result) throws Throwable {
                    VirtualFile directory = LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
                    if (directory != null) {
                        contentEntry.addSourceFolder(directory, isTestSource);
                    }
                }
            }.execute();
        }
    }

    /**
     * Update the contentEntry with the following values from the leinProjectMap added as source directories.
     * - "resource-paths"
     * - "source-paths"
     * - "test-paths"
     *
     * @param contentEntry   The contentEntry to update.
     * @param leinProjectMap The map to extract values from.
     * @return The contentEntry updated with the sourcePaths added.
     */
    public ContentEntry updateSourceAndResourcesPaths(ContentEntry contentEntry, Map leinProjectMap) {

        List<String> resourcePaths = getPaths(LEIN_RESOURCE_PATHS, leinProjectMap);
        addSourceFoldersToContentEntry(contentEntry, resourcePaths, false);

        List<String> sourcePaths = getPaths(LEIN_SOURCE_PATHS, leinProjectMap);
        addSourceFoldersToContentEntry(contentEntry, sourcePaths, false);

        List<String> javaSourcePaths = getPaths(LEIN_JAVA_SOURCE_PATHS,leinProjectMap);
        addSourceFoldersToContentEntry(contentEntry,javaSourcePaths,false);

        List<String> testPaths = getPaths(LEIN_TEST_PATHS, leinProjectMap);
        addSourceFoldersToContentEntry(contentEntry, testPaths, true);

        return contentEntry;
    }

    /**
     * Update the compiler extension to have the appropriate paths as configured in the project map.
     * <p/>
     * SIDE-EFFECT: Changes state of extension
     *
     * @param extension      The compiler extension.
     * @param leinProjectMap The map to extract values from.
     * @return The compiler extension updated with the given settings.
     */
    public CompilerModuleExtension updateCompilePath(final CompilerModuleExtension extension, Map leinProjectMap) {

        final String outputPathString = (String) leinProjectMap.get(LEIN_COMPILE_PATH);
        new WriteAction() {
            @Override
            protected void run(Result result) throws Throwable {
                try {
                    VirtualFile outputPath = VfsUtil.createDirectoryIfMissing(outputPathString);
                    extension.inheritCompilerOutputPath(false);
                    extension.setCompilerOutputPath(outputPath);
                    extension.setCompilerOutputPathForTests(outputPath);
                } catch (IOException e) {
                    throw new RuntimeException("Could not create output directory" + outputPathString);
                }
            }
        }.execute();
        return extension;
    }

    /**
     * Utility method to obtain the root model of the module.
     * <p/>
     * This performs a read of the iml file so we can reconstruct the state.
     *
     * @param module The module to obtain the root model for.
     * @return The root model of the module.
     */
    public ModifiableRootModel getRootModel(final Module module) {
        return new ReadAction<ModifiableRootModel>() {
            protected void run(Result<ModifiableRootModel> result) throws Throwable {
                result.setResult(ModuleRootManager.getInstance(module).getModifiableModel());
            }
        }.execute().getResultObject();
    }

    /**
     * Private utility to create the module manager, which manages the list of modules that this project knows about.
     *
     * @param ideaProject The idea project.
     * @return The Model for the module manager.
     */
    private ModifiableModuleModel createModuleManager(final Project ideaProject) {
        return new ReadAction<ModifiableModuleModel>() {
            protected void run(Result<ModifiableModuleModel> result) throws Throwable {
                result.setResult(ModuleManager.getInstance(ideaProject).getModifiableModel());
            }
        }.execute().getResultObject();
    }

    /**
     * Create or find the modules root model.
     * <p/>
     * If there exists a module with a matching name, that will be returned.
     * <p/>
     * Otherwise, we'll create a new module and add it to the moduleManager.
     *
     * @param moduleManager The module manager
     * @param name          The name of the module
     * @return The module's modifiable model
     */
    public ModifiableRootModel createModule(ModifiableModuleModel moduleManager, String workingDir, String name) {
        Module module = moduleManager.findModuleByName(name);

        if (module == null) {
            // oh-kay we don't have a module yet.
            String filePath = workingDir + File.separator + FileUtil.sanitizeFileName(name) + ModuleFileType.DOT_DEFAULT_EXTENSION;
            module = moduleManager.newModule(filePath, StdModuleTypes.JAVA.getId());
        }

        return getRootModel(module);
    }

    /**
     * Initialize the source, resources, test, and compile paths on module.
     *
     * @param projectMap  The leiningen project map.
     * @param module      The module to update
     * @param contentRoot The virtual file pointing to the leiningen project root directory. (Usually where the project.clj file is)
     */
    public void initializeModulePaths(Map projectMap, ModifiableRootModel module, VirtualFile contentRoot) {
        //Set up the paths
        module.inheritSdk();
        final ContentEntry contentEntry = module.addContentEntry(contentRoot);

        //Maven doesn't let you have source files that aren't configured in the pom.xml for consistency reasons.
        //We'll apply the same laws to leiningen projects.
        contentEntry.clearSourceFolders();

        //Add the source and resource paths to the module
        updateSourceAndResourcesPaths(contentEntry, projectMap);

        //Handle the compile path (output)
        CompilerModuleExtension compilerExtension = module.getModuleExtension(CompilerModuleExtension.class);
        updateCompilePath(compilerExtension, projectMap);
    }

    /**
     * Initialize the dependencies for the module.  This will add any dependencies to the list of project libraries and
     * then add those libraries to the module via Order Entries.
     *
     *
     * @param module           The module to update.
     * @param projectLibraries The list of project libraries.
     * @param dependencyMaps The list of maps containing the dependency information.
     * @return The set of libraries that were created.
     */
    private Set<Library.ModifiableModel> initializeDependencies(ModifiableRootModel module, LibraryTable.ModifiableModel projectLibraries, List dependencyMaps) {

        //Reset them module's library order entries here - this actually happens in org.jetbrains.idea.maven.importing.MavenRootModelAdapter.initOrderEntries()
        for (OrderEntry orderEntry : module.getOrderEntries()) {
            if (orderEntry instanceof LibraryOrderEntry) {
                //Remove any library from the project list so it can be refreshed.
                projectLibraries.removeLibrary(((LibraryOrderEntry) orderEntry).getLibrary());
                module.removeOrderEntry(orderEntry);
            }
        }

        //Add the dependencies to the projects's library table - this is how maven does it - but we could put the libraries directly on the module - but maybe it's better if we share a lot of libraries between modules.
        Map<Library.ModifiableModel, DependencyScope> scopeMap = createLibraries(projectLibraries, dependencyMaps);

        //Now add the libraries to the modules.
        for (Map.Entry<Library.ModifiableModel, DependencyScope> entry : scopeMap.entrySet()) {
            module.addLibraryEntry(projectLibraries.getLibraryByName(entry.getKey().getName())).setScope(entry.getValue());
        }
        return scopeMap.keySet();
    }


    /**
     * This method imports a leiningen module from a leiningen project file and imports it into the idea project.
     * <p/>
     * Notes:
     * <p/>
     * Each of the IDEA components has a getModifiableModel on it.  This method returns a new instance each time you
     * invoke it.  Once you have a modifiable model of the component you wish to update, you mutate it to the state
     * you wish.  Once you're done, you call commit() on the modifiable model and it updates the component it came from.
     * <p/>
     * Since a lot of the components are persisted in files, commit() updates these files as well.  Therefore you need
     * to make any calls to commit() from within a WriteAction.
     *
     * @param ideaProject The IDEA project to add the leiningen module to.
     * @param leinProjectFile  The leiningen project file
     * @return The leiningen project map.
     */
    public Map importModule(Project ideaProject, VirtualFile leinProjectFile) {

        ClassPathUtils.getInstance().switchToPluginClassLoader();
        Map projectMap = LeiningenAPI.loadProject(leinProjectFile.getPath());
        String name = (String) projectMap.get(LEIN_PROJECT_NAME);

        final ModifiableModuleModel moduleManager = createModuleManager(ideaProject);
        final ModifiableRootModel module = createModule(moduleManager, leinProjectFile.getParent().getPath(), name);
        initializeModulePaths(projectMap, module, leinProjectFile.getParent());

        //Setup the dependencies
        // Based loosely on org.jetbrains.idea.maven.importing.MavenRootModelAdapter#addLibraryDependency

        //We could use the module table here, but then the libraries wouldn't be shared across modules.
        final LibraryTable.ModifiableModel projectLibraries = ProjectLibraryTable.getInstance(ideaProject).getModifiableModel();

        //Load all the dependencies from the project file
        List dependencyMaps = LeiningenAPI.loadDependencies(leinProjectFile.getCanonicalPath());
        final Set<Library.ModifiableModel> dependencies = initializeDependencies(module, projectLibraries,dependencyMaps);

        new WriteAction() {
            @Override
            protected void run(Result result) throws Throwable {

                for (Library.ModifiableModel library : dependencies) {
                    library.commit();
                }

                //Save the project libraries
                projectLibraries.commit();

                //Save the module itself to the module file.
                module.commit();

                //Save the list of modules that are in this project to the IDEA project file
                moduleManager.commit();
            }
        }.execute();

        return projectMap;
    }

    /**
     * Create the libraries for the list of dependency maps.
     * <p/>
     * This will add the libraries to the libraryTable (if they don't already exist) and return a map of libraries mapped to their scopes as used in the module.
     *
     * @param libraryTable   The library table to add the libraries to.
     * @param dependencyMaps The list of dependency maps definining the libraries needed.
     * @return A Map of the Libraries which were described in dependencyMaps along with their scope for the module
     */
    private Map<Library.ModifiableModel, DependencyScope> createLibraries(LibraryTable.ModifiableModel libraryTable, List dependencyMaps) {

        Map<Library.ModifiableModel, DependencyScope> libraryDependencyScopeMap = new THashMap<Library.ModifiableModel, DependencyScope>();
        final String LEIN_LIB_PREFIX = "Leiningen";
        for (Object obj : dependencyMaps) {
            Map dependency = (Map) obj;
            //Check if the library already exists
            String libraryName = LEIN_LIB_PREFIX + ": " + (String) dependency.get("groupid") + ":" + (String) dependency.get("artifactid") + ":" + dependency.get("version");
            Library library = libraryTable.getLibraryByName(libraryName);
            if (library == null) {
                library = libraryTable.createLibrary(libraryName);
            }

            // Add the library to a library model, which represents the data for a single library.
            Library.ModifiableModel libraryModel = library.getModifiableModel();

            //Right now only deal with classes - a lot of clojure libraries have the .clj in them and not in a separate file
            //Remove existing classes as this is what maven does - you need to declare the dependencies in the project file
            for (String url : libraryModel.getUrls(OrderRootType.CLASSES)) {
                libraryModel.removeRoot(url, OrderRootType.CLASSES);
            }

            File file = ((File) dependency.get("file"));
            String path = file.getAbsolutePath();
            String url = VirtualFileManager.constructUrl(JarFileSystem.PROTOCOL, path) + JarFileSystem.JAR_SEPARATOR;
            libraryModel.addRoot(url, OrderRootType.CLASSES);

            DependencyScope scope = determineScope((String) dependency.get("scope"));
            libraryDependencyScopeMap.put(libraryModel, scope);
        }
        return libraryDependencyScopeMap;
    }

    /**
     * Do a check to determine the proper scope.
     *
     * @param s The string scope.
     * @return A DependencyScope object
     */
    private DependencyScope determineScope(String s) {
        DependencyScope scope = null;
        if (s.equalsIgnoreCase("compile")) {
            scope = DependencyScope.COMPILE;
        }

        if (s.equalsIgnoreCase("test")) {
            scope = DependencyScope.TEST;
        }

        if (s.equalsIgnoreCase("runtime")) {
            scope = DependencyScope.RUNTIME;
        }

        if (s.equalsIgnoreCase("provided")) {
            scope = DependencyScope.PROVIDED;
        }
        return scope;
    }

}
