package tech.ikora.evolution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.kelloggs.communication.frame.VersionFrame;
import org.example.kelloggs.communication.handler.Communication;
import tech.ikora.evolution.process.Synchronization;
import tech.ikora.evolution.versions.Version;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.evolution.process.BDConnectLauncher;
import tech.ikora.evolution.process.MavenLauncher;

import java.io.File;
import java.io.IOException;

public class Runner {
    private static final Logger logger = LogManager.getLogger(Runner.class);

    private final VersionProvider versionProvider;
    private final File agentJar;
    private final File dbConnectJar;
    private final File database;
    private final int port;

    public Runner(VersionProvider versionProvider, File agentJar, File dbConnectJar, File database, int port) {
        this.versionProvider = versionProvider;
        this.agentJar = agentJar;
        this.dbConnectJar = dbConnectJar;
        this.database = database;
        this.port = port;
    }

    public void execute() throws IOException {
        final Synchronization synchronization = new Synchronization();

        try(final BDConnectLauncher dbConnect = new BDConnectLauncher(dbConnectJar, database, port)){
            synchronization.register(dbConnect);

            synchronization.waitForInitialization();

            for(Version version: versionProvider){
                logger.info(String.format("Start execution for %s [%s]",
                        version.getLocation(),
                        version.getCommitId()
                ));

                sendFrame(port, version);

                try {
                    final String agent = String.format("%s=localhost,%d",
                            agentJar.getAbsolutePath(),
                            port
                    );

                    final String result = new MavenLauncher()
                            .withJavaParameter("forkCount", "0")
                            .withJavaParameter("reuseForks", "true")
                            .withJavaParameter("maven.test.failure.ignore", "true")
                            .withMavenOptions("javaagent", agent)
                            .usingJavaVersion(version.getProcessConfiguration().getJavaHome())
                            .withMavenOptions(version.getProcessConfiguration().getMavenOptions())
                            .withJavaToolOptions(version.getProcessConfiguration().getJavaToolOptions())
                            .withEnvironmentVariables(version.getProcessConfiguration().getEnvironment())
                            .withJavaParameters(version.getProcessConfiguration().getArguments())
                            .usingProfile(version.getProcessConfiguration().getProfiles())
                            .inDirectory(version.getLocation())
                            .forGoals(version.getProcessConfiguration().getGoals())
                            .execute();
                } catch (Exception e) {
                    logger.error(String.format("Failed to execute mvn for %s [%s]: %s",
                            version.getLocation(),
                            version.getCommitId(),
                            e.getMessage()
                    ));
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Something went wrong: %s", e.getMessage()));
        } finally {
            versionProvider.clean();
        }
    }

    private static void sendFrame(int port, Version version) throws IOException {
        final VersionFrame frame = new VersionFrame(version.getId(), version.getDate(), version.getCommitId(), "");
        final Communication communication = new Communication("localhost", port);
        communication.sendFrame(frame);
    }
}
