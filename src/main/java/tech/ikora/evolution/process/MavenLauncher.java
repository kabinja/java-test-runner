package tech.ikora.evolution.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.evolution.configuration.Entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MavenLauncher extends ProcessLauncher {
    private static final Logger logger = LogManager.getLogger(MavenLauncher.class);

    private String javaHome;
    private final Entries javaToolOptions = new Entries();
    private final Entries mavenOptions = new Entries();
    private final Entries environmentVariables = new Entries();
    private final Entries javaParameters = new Entries();
    private final List<String> freeFormParameters = new ArrayList<>();
    private File directory = null;
    private boolean clean = true;
    private boolean test = false;
    private boolean compile = false;
    private boolean install = true;
    private int wallTime = 20;
    private TimeUnit wallTimeUnit = TimeUnit.MINUTES;

    public MavenLauncher usingJavaVersion(String javaHome){
        this.javaHome = javaHome;
        return this;
    }

    public MavenLauncher withMavenOptions(String key, String value){
        mavenOptions.put(key, value);
        return this;
    }

    public MavenLauncher withMavenOptions(List<Entry> options){
        mavenOptions.putAll(options);
        return this;
    }

    public MavenLauncher withEnvironmentVariables(List<Entry> environmentVariables){
        this.environmentVariables.putAll(environmentVariables);
        return this;
    }

    public MavenLauncher withJavaToolOptions(List<Entry> javaToolOptions){
        this.javaToolOptions.putAll(javaToolOptions);
        return this;
    }

    public MavenLauncher withJavaParameter(String name, String value){
        this.javaParameters.put(name, value);
        return this;
    }

    public MavenLauncher withJavaParameters(List<Entry> extraParameters){
        this.javaParameters.putAll(extraParameters);
        return this;
    }

    public MavenLauncher withFreeFormParameters(List<String> freeFormParameters){
        this.freeFormParameters.addAll(freeFormParameters);
        return this;
    }

    public MavenLauncher waitFor(int time, TimeUnit timeUnit){
        this.wallTime = time;
        this.wallTimeUnit = timeUnit;
        return this;
    }

    public MavenLauncher inDirectory(File directory){
        this.directory = directory;
        return this;
    }


    public MavenLauncher clean(boolean clean){
        this.clean = clean;
        return this;
    }

    public MavenLauncher compile(boolean compile){
        this.compile = compile;
        return this;
    }

    public MavenLauncher test(boolean test){
        this.test = test;
        return this;
    }

    public MavenLauncher install(boolean install){
        this.install = install;
        return this;
    }


    public String execute() throws IOException, InterruptedException, TimeoutException {
        final ProcessBuilder builder = new ProcessBuilder(super.initCommand());

        builder.redirectErrorStream(true);
        setDirectory(builder);
        setEnvironment(builder);
        setCommand(builder);

        final Process process = builder.start();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null){
            logger.info(line);
            output.append(line);
            output.append("\n");
        }

        reader.close();

        if(!process.waitFor(wallTime, wallTimeUnit)){
            process.destroy();

            throw new TimeoutException("Process ran out of time");
        }

        return output.toString();
    }

    private void setDirectory(ProcessBuilder builder){
        if(directory != null && directory.exists()){
            builder.directory(directory);
        }
    }

    private void setEnvironment(ProcessBuilder builder){
        if(!mavenOptions.isEmpty()){
            Map<String, String> localEnv = builder.environment();

            if(javaHome != null && new File(javaHome).exists()){
                localEnv.put("JAVA_HOME", javaHome);
            }

            List<String> formattedMavenOptions = new ArrayList<>();
            for(Entry entry: mavenOptions){
                formattedMavenOptions.add(entry.format("-", ":"));
            }
            localEnv.put("MAVEN_OPTS", String.join(" ", formattedMavenOptions));

            List<String> formattedJavaToolOptions = new ArrayList<>();
            for(Entry entry: javaToolOptions){
                formattedJavaToolOptions.add(entry.format("-D", ":"));
            }
            localEnv.put("JAVA_TOOL_OPTIONS", String.join(" ", formattedJavaToolOptions));

            for(Entry entry: environmentVariables){
                localEnv.put(entry.getName(), entry.getValue());
            }
        }
    }

    private void setCommand(ProcessBuilder builder){
        final List<String> command = new ArrayList<>();

        command.add(isWindows() ? "mvn.cmd" : "mvn");

        if(clean){
            command.add("clean");
        }

        if(compile){
            command.add("compile");
        }

        if(test){
            command.add("test");
        }

        if(install){
            command.add("install");
            command.add("--fail-never");
        }

        command.addAll(freeFormParameters);

        for(Entry entry: javaParameters){
            command.add(entry.format("-D", "="));
        }

        builder.command(command);
    }
}
