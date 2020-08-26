package tech.ikora.selenium.locator.evolution.versions;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.LocalRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitProvider implements VersionProvider {
    private static final Logger logger = LogManager.getLogger(GitProvider.class);

    private File tmpFolder;
    private final Map<LocalRepository, List<GitCommit>> repositories;

    public GitProvider() {
        this.repositories = new HashMap<>();
    }

    public void addRepository(LocalRepository localRepository, List<GitCommit> commits) {
        this.repositories.put(localRepository, commits);
    }

    public File getTmpFolder() throws IOException {
        if(this.tmpFolder == null){
            this.tmpFolder = new File(System.getProperty("java.io.tmpdir"), "git-provider");

            if(this.tmpFolder.exists()){
                FileUtils.deleteDirectory(this.tmpFolder);
            }

            if(!this.tmpFolder.mkdir()){
                throw new IOException(String.format("Failed to create directory: %s",
                        this.tmpFolder.getAbsolutePath()));
            }
        }

        return this.tmpFolder;
    }

    @Override
    public void clean() throws IOException {
        for(LocalRepository localRepository: repositories.keySet()){
            localRepository.getGit().getRepository().close();
        }

        FileUtils.forceDelete(this.tmpFolder);
    }

    @Override
    public Iterator<Version> iterator() {
        return null;
    }
}
