package tech.ikora.evolution.process;

import tech.ikora.evolution.utils.OsUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessLauncher {
    protected List<String> initCommand(){
        final List<String> command = new ArrayList<>();

        if(OsUtils.isWindows()){
            command.add("cmd.exe");
            command.add("/c");
            command.add("start");
            command.add("cmd");
        }

        return command;
    }
}
