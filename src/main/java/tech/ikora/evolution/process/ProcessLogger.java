package tech.ikora.evolution.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessLogger extends Listener {
    private static final Logger logger = LogManager.getLogger(ProcessSynchronizer.class);

    private final Process process;
    private final String name;

    public ProcessLogger(Process process, String name) {
        this.process = process;
        this.name = name;
    }

    @Override
    protected void onStartListening() {
        logger.info(String.format("Process [pid:%d] '%s' is started",
                process.pid(), name));
    }

    @Override
    protected boolean onMessageReceived(String line) {
        logger.debug(line);

        if(line.contains("ERROR")){
            logger.error(String.format("[pid:%d] %s", process.pid(), line));
        }

        return true;
    }

    @Override
    protected void onEndListening() {
        logger.info(String.format("process [pid:%d] '%s' is ready to terminated",
                process.pid(), name));
    }

    @Override
    protected void onExceptionRaised(Exception e) {
        logger.error(String.format("Something went wrong when reading stream from process [pid:%d] '%s': %s",
                process.pid(), name, e.getMessage()));
    }
}
