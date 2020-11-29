package tech.ikora.evolution.process;

import org.apache.commons.io.FilenameUtils;
import tech.ikora.evolution.configuration.Entry;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BDConnectLauncher extends ProcessLauncher implements AutoCloseable, Synchronizable {
    private final int port;
    private final File database;
    private final File jar;
    private final ProcessListenerThread initializationThread;

    public BDConnectLauncher(final File jar, final File database, int port) throws IOException {
        this.jar = jar;
        this.port = port;
        this.database = database;

        this.initializationThread = new ProcessListenerThread(
                startProcess(),
                "Database Logger",
                "Initialized JPA EntityManagerFactory"
        );

        this.initializationThread.start();
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
        command.add(new Entry("sever.port", String.valueOf(port)).format("--", "="));
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
        try (Socket socket = new Socket("localhost", port)){
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeChar('e');
        }
    }

    @Override
    public Thread getInitializationThread() {
        return this.initializationThread;
    }
}
