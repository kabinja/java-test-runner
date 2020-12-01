package tech.ikora.evolution;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.evolution.configuration.FolderConfiguration;
import tech.ikora.evolution.configuration.RepositoryConfiguration;
import tech.ikora.evolution.versions.FolderProvider;
import tech.ikora.evolution.versions.GitProvider;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.gitloader.exception.InvalidGitRepositoryException;
import tech.ikora.gitloader.git.CommitCollector;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import tech.ikora.evolution.configuration.Configuration;
import tech.ikora.evolution.configuration.GitConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RunnerFactory {
    private static final Logger logger = LogManager.getLogger(RunnerFactory.class);

    public static Runner fromConfiguration(Configuration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
        final int port = configuration.getPort();
        final File dbConnectJar = configuration.getDatabaseConnectionJar();
        final File agentJar = configuration.getAgentJar();
        final File database = configuration.getOutputDatabase();
        final VersionProvider versionProvider = createVersionProvider(configuration);

        return new Runner(versionProvider, agentJar, dbConnectJar, database, port);
    }

    private static VersionProvider createVersionProvider(Configuration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
        VersionProvider provider;

        if(configuration.getFolderConfiguration() != null){
            provider = createFolderProvider(configuration.getFolderConfiguration());
        }
        else if(configuration.getGitConfiguration() != null){
            provider = createGitProvider(configuration.getGitConfiguration());
        }
        else{
            throw new InvalidConfigurationException("Configuration should have a folder or git section");
        }

        return provider;
    }

    private static VersionProvider createGitProvider(GitConfiguration configuration) throws IOException, InvalidGitRepositoryException {
        final File tmpFolder = getTmpFolder();
        final GitProvider provider = new GitProvider(tmpFolder);

        for(RepositoryConfiguration repository: configuration.getRepositories()){
            if(repository.isIgnore()){
                continue;
            }

            final File repositoryFolder = new File(tmpFolder, GitUtils.extractProjectName(repository.getLocation()));

            logger.info(String.format("Loading repository from %s...", repository.getLocation()));

            final LocalRepository localRepository = GitUtils.loadCurrentRepository(
                    repository.getLocation(),
                    configuration.getToken(),
                    repositoryFolder,
                    repository.getBranch()
            );

            logger.info("Repository loaded!");

            List<GitCommit> commits = new CommitCollector()
                    .forGit(localRepository.getGit())
                    .onBranch(repository.getBranch())
                    .from(repository.getStartDate())
                    .to(repository.getEndDate())
                    .ignoring(repository.getIgnoreCommits())
                    .every(repository.getFrequency())
                    .limit(repository.getMaximumCommitsNumber())
                    .collect();

            provider.addRepository(localRepository, repository.getProcessConfiguration(), commits);
        }

        return provider;
    }

    private static VersionProvider createFolderProvider(FolderConfiguration configuration) {
        return new FolderProvider(configuration.getRootFolder(), configuration.getNameFormat(), configuration.getDateFormat(), configuration.getProcessConfiguration());
    }

    private static File getTmpFolder() throws IOException {
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"), "git-provider");

        if(tmpFolder.exists()){
            FileUtils.deleteDirectory(tmpFolder);
        }

        if(!tmpFolder.mkdir()){
            throw new IOException(String.format("Failed to create directory: %s", tmpFolder.getAbsolutePath()));
        }

        return tmpFolder;
    }
}
