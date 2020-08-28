package tech.ikora.selenium.locator.evolution;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.gitloader.exception.InvalidGitRepositoryException;
import tech.ikora.gitloader.git.CommitCollector;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import tech.ikora.selenium.locator.evolution.configuration.Configuration;
import tech.ikora.selenium.locator.evolution.configuration.FolderConfiguration;
import tech.ikora.selenium.locator.evolution.configuration.GitConfiguration;
import tech.ikora.selenium.locator.evolution.versions.FolderProvider;
import tech.ikora.selenium.locator.evolution.versions.GitProvider;
import tech.ikora.selenium.locator.evolution.versions.VersionProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RunnerFactory {
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

    private static VersionProvider createGitProvider(GitConfiguration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
        final File tmpFolder = getTmpFolder();
        final GitProvider provider = new GitProvider(tmpFolder);

        for(URL repository: configuration.getRepositories()){
            final File repositoryFolder = new File(tmpFolder, GitUtils.extractProjectName(repository.toString()));

            final LocalRepository localRepository = GitUtils.loadCurrentRepository(
                    repository.toString(),
                    configuration.getToken(),
                    repositoryFolder,
                    configuration.getBranch()
            );

            List<GitCommit> commits = new CommitCollector()
                    .forGit(localRepository.getGit())
                    .onBranch(configuration.getBranch())
                    .from(configuration.getStartDate())
                    .to(configuration.getEndDate())
                    .ignoring(configuration.getIgnoreCommits())
                    .every(configuration.getFrequency())
                    .limit(configuration.getMaximumCommitsNumber())
                    .collect();

            provider.addRepository(localRepository, commits);
        }

        return provider;
    }

    private static VersionProvider createFolderProvider(FolderConfiguration configuration) {
        return new FolderProvider(configuration.getRootFolder(), configuration.getNameFormat(), configuration.getDateFormat());
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
