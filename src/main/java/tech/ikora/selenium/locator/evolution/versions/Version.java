package tech.ikora.selenium.locator.evolution.versions;

import java.io.File;
import java.time.LocalDateTime;

public class Version {
    private final File location;
    private final LocalDateTime date;
    private final String commitId;

    public Version(File location, LocalDateTime date, String commitId) {
        this.location = location;
        this.date = date;
        this.commitId = commitId;
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
}
