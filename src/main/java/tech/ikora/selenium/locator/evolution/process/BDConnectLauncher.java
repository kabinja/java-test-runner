package tech.ikora.selenium.locator.evolution.process;

import org.apache.commons.io.FilenameUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BDConnectLauncher extends ProcessLauncher implements AutoCloseable {
    private final int port;
    private final File database;
    private final File jar;

    public BDConnectLauncher(final File jar, final File database, int port) throws IOException {
        this.jar = jar;
        this.port = port;
        this.database = database;

        launch();
    }

    private void launch() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(createCommand());
        builder.directory(getDirectory());
        builder.inheritIO().start();
    }

    private List<String> createCommand(){
        final List<String> command = super.initCommand();

        command.add("java");
        command.add("-jar");
        command.add(getJarName());
        command.add("-port");
        command.add(String.valueOf(port));
        command.add("-database");
        command.add(database.getAbsolutePath());

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
}
