package tech.ikora.selenium.locator.evolution;

import tech.ikora.selenium.locator.evolution.versions.Version;
import tech.ikora.selenium.locator.evolution.versions.VersionProvider;

import java.io.File;

public class Runner {
    private final VersionProvider versionProvider;
    private final File database;
    private final int port;

    public Runner(VersionProvider versionProvider, File database, int port) {
        this.versionProvider = versionProvider;
        this.database = database;
        this.port = port;
    }

    public void execute(){

        for(Version version: versionProvider){

        }
    }
}
