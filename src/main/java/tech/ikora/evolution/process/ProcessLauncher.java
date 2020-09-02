package tech.ikora.evolution.process;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessLauncher {
    private static final String OS;

    protected boolean isWindows() {
        return OS.startsWith("Windows");
    }

    static {
        OS = System.getProperty("os.name");
    }

    protected List<String> initCommand(){
        final List<String> command = new ArrayList<>();

        if(isWindows()){
            command.add("cmd.exe");
            command.add("/C");
        }

        return command;
    }
}
