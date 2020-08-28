package tech.ikora.selenium.locator.evolution.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final Entries options = new Entries();
    private final Entries extraArguments = new Entries();
    private File directory = null;
    private boolean clean = true;
    private boolean test = true;
    private boolean compile = true;
    private int wallTime = 20;
    private TimeUnit wallTimeUnit = TimeUnit.MINUTES;

    public MavenLauncher withMavenOptions(String key, String value){
        options.put(key, value);
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

    public MavenLauncher withExtraParameter(String name, String value){
        this.extraArguments.put(name, value);
        return this;
    }

    public MavenLauncher waitFor(int time, TimeUnit timeUnit){
        this.wallTime = time;
        this.wallTimeUnit = timeUnit;
        return this;
    }

    public String execute() throws IOException, InterruptedException, TimeoutException {
        final List<String> command = createCommand();

        final ProcessBuilder builder = new ProcessBuilder(command);

        builder.redirectErrorStream(true);
        setDirectory(builder);
        setMavenOptions(builder);

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

    private void setMavenOptions(ProcessBuilder builder){
        if(!options.isEmpty()){
            Map<String, String> localEnv = builder.environment();

            List<String> mavenOptions = new ArrayList<>();

            for(Entry entry: options){
                mavenOptions.add(entry.format("-", ":"));
            }

            localEnv.put("MAVEN_OPTS", String.join(" ", mavenOptions));
        }
    }

    private List<String> createCommand(){
        final List<String> command = super.initCommand();

        command.add("mvn");

        if(clean){
            command.add("clean");
        }

        if(compile){
            command.add("compile");
        }

        if(test){
            command.add("test");
        }

        for(Entry entry: extraArguments){
            command.add(entry.format("-D", "="));
        }

        return command;
    }
}
