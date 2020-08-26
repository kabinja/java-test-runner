package tech.ikora.selenium.locator.evolution.versions;

import java.io.IOException;

public interface VersionProvider extends Iterable<Version> {
    void clean() throws IOException;
}
