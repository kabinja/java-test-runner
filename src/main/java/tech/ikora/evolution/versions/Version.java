package tech.ikora.evolution.versions;

import tech.ikora.evolution.configuration.ProcessConfiguration;

import java.io.File;
import java.time.LocalDateTime;

public class Version {
    private final String id;
    private final File location;
    private final LocalDateTime date;
    private final String commitId;
    private final ProcessConfiguration processConfiguration;

    public Version(String id, File location, LocalDateTime date, String commitId, ProcessConfiguration processConfiguration) {
        this.id = id;
        this.location = location;
        this.date = date;
        this.commitId = commitId;
        this.processConfiguration = processConfiguration;
    }

    public String getId() {
        return id;
    }

    public File getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getCommitId() {
        return commitId;
    }

    public ProcessConfiguration getProcessConfiguration() {
        return processConfiguration;
    }
}
