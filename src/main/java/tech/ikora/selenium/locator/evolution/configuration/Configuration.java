package tech.ikora.selenium.locator.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {
    @JsonProperty(value = "output database")
    private String outputDatabase;

    @JsonProperty(value = "git")
    private GitConfiguration gitConfiguration;

    public String getOutputDatabase() {
        return outputDatabase;
    }

    public void setOutputDatabase(String outputDatabase) {
        this.outputDatabase = outputDatabase;
    }

    public GitConfiguration getGitConfiguration() {
        return gitConfiguration;
    }

    public void setGitConfiguration(GitConfiguration gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }
}
