package tech.ikora.evolution.versions;

import java.io.IOException;

public interface VersionProvider extends Iterable<Version> {
    void clean() throws IOException;
}
