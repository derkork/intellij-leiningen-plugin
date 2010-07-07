package de.janthomae.leiningenplugin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.vfs.encoding.EncodingManager;


/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenCommandLineState extends CommandLineState {
    private final LeiningenRunnerSettings mySettings;
    private final LeiningenRunnerParameters myParameters;

    protected LeiningenCommandLineState(LeiningenRunnerSettings settings, LeiningenRunnerParameters parameters,
                                        ExecutionEnvironment environment) {
        super(environment);
        mySettings = settings;
        this.myParameters = parameters;
    }

    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(mySettings.getLeiningenPath());
        commandLine.addParameters(myParameters.getMyGoals());
        commandLine.setWorkDirectory(myParameters.getWorkingDirectory());
        return new ColoredProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString(),
                EncodingManager.getInstance().getDefaultCharset());
    }
}
