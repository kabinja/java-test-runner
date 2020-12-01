package tech.ikora.evolution;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.evolution.configuration.FolderConfiguration;
import tech.ikora.evolution.versions.FolderProvider;
import tech.ikora.evolution.versions.GitProvider;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.gitloader.exception.InvalidGitRepositoryException;
import tech.ikora.evolution.configuration.Configuration;
import tech.ikora.evolution.configuration.GitConfiguration;

import java.io.File;
import java.io.IOException;

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

    private static VersionProvider createGitProvider(GitConfiguration configuration) throws IOException, InvalidGitRepositoryException {
        return new GitProvider(configuration);
    }

    private static VersionProvider createFolderProvider(FolderConfiguration configuration) {
        return new FolderProvider(configuration.getRootFolder(), configuration.getNameFormat(), configuration.getDateFormat(), configuration.getProcessConfiguration());
    }
}
