package tech.ikora.evolution.process;

import org.apache.commons.io.FilenameUtils;
import org.example.kelloggs.communication.frame.EndFrame;
import org.example.kelloggs.communication.handler.Communication;
import tech.ikora.evolution.configuration.Entry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BDConnectLauncher extends ProcessLauncher implements AutoCloseable, Synchronizable {
    private final int port;
    private final File database;
    private final File jar;
    private final Process process;

    private final InputStreamSplitter splitter;
    private final ProcessSynchronizer processSynchronizer;
    private final ProcessLogger processLogger;

    public BDConnectLauncher(final File jar, final File database, int port) throws IOException {
        this.jar = jar;
        this.port = port;
        this.database = database;
        this.process = startProcess();

        this.processSynchronizer = new ProcessSynchronizer(
                this.process,
                "Database Logger",
                "Initialized JPA EntityManagerFactory for persistence unit"
        );

        this.processLogger = new ProcessLogger(
                this.process,
                "Database Logger"
        );

        this.splitter = new InputStreamSplitter(this.process.getInputStream());
        this.splitter.register(this.processSynchronizer);
        this.splitter.register(this.processLogger);

        this.splitter.start();
    }

    public boolean isAlive(){
        return this.process.isAlive();
    }

    private Process startProcess() throws IOException {
        return new ProcessBuilder(super.initCommand())
                .directory(getDirectory())
                .command(createCommand())
                .start();
    }

    private List<String> createCommand(){
        final List<String> command = new ArrayList<>();

        command.add("java");
        command.add("-jar");
        command.add(new Entry(getJarName()).format(""));
        command.add(new Entry("server.port", String.valueOf(port)).format("--", "="));
        command.add(new Entry("spring.datasource.url", "jdbc:sqlite:" + database.getAbsolutePath()).format("--", "="));

        return command;
    }

    private File getDirectory(){
        return jar.getParentFile();
    }

    private String getJarName(){
        return FilenameUtils.getName(jar.getAbsolutePath());
    }

    @Override
    public void close() throws Exception {
        final Communication communication = new Communication("localhost", port);
        communication.sendFrame(new EndFrame("java-test-runner"));
    }

    @Override
    public Thread getInitializationThread() {
        return this.processSynchronizer;
    }
}
