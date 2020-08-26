package tech.ikora.selenium.locator.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class Configuration {
    @JsonProperty(value = "port")
    private int port;

    @JsonProperty(value = "output database")
    private File outputDatabase;

    @JsonProperty(value = "git")
    private GitConfiguration gitConfiguration;

    @JsonProperty(value = "folder")
    private FolderConfiguration folderConfiguration;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public File getOutputDatabase() {
        return outputDatabase;
    }

    public void setOutputDatabase(File outputDatabase) {
        this.outputDatabase = outputDatabase;
    }

    public GitConfiguration getGitConfiguration() {
        return gitConfiguration;
    }

    public void setGitConfiguration(GitConfiguration gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }

    public FolderConfiguration getFolderConfiguration() {
        return folderConfiguration;
    }

    public void setFolderConfiguration(FolderConfiguration folderConfiguration) {
        this.folderConfiguration = folderConfiguration;
    }
}
