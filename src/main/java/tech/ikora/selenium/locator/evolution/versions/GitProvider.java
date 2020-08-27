package tech.ikora.selenium.locator.evolution.versions;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Override
    public void clean() throws IOException {
        for(LocalRepository localRepository: repositories.keySet()){
            localRepository.getGit().getRepository().close();
        }

        FileUtils.forceDelete(this.tmpFolder);
    }

    @Override
    public Iterator<Version> iterator() {
        return new Iterator<>() {
            private final Iterator<LocalRepository> projectIterator = repositories.keySet().iterator();

            private Iterator<GitCommit> commitIterator = null;
            private LocalRepository current = null;

            @Override
            public boolean hasNext() {
                while(commitIterator == null || !commitIterator.hasNext()){
                    if(projectIterator.hasNext()){
                        current = projectIterator.next();
                        commitIterator = repositories.get(current).iterator();
                    }
                    else break;
                }

                return commitIterator != null && commitIterator.hasNext();
            }

            @Override
            public Version next() {
                if(!hasNext()){
                    return null;
                }

                final GitCommit commit = commitIterator.next();

                try {
                    GitUtils.checkout(current.getGit(), commit.getId());
                } catch (GitAPIException e) {
                    logger.error(String.format("Git API error failed to load commit %s from %s: %s",
                            commit.getId(),
                            current.getRemoteUrl(),
                            e.getMessage()
                    ));
                    next();
                }

                final LocalDateTime dateTime = commit.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                return new Version(current.getLocation(), dateTime, commit.getId());
            }
        } ;
    }
}
