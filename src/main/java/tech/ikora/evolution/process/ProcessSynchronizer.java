package tech.ikora.evolution.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessSynchronizer extends Listener{
    private static final Logger logger = LogManager.getLogger(ProcessSynchronizer.class);

    private final Process process;
    private final String name;
    private final String message;

    public ProcessSynchronizer(Process process, String name, String message) {
        this.process = process;
        this.name = name;
        this.message = message;
    }

    @Override
    protected void onStartListening() {
        logger.info(String.format("Synchronizing process [pid:%d] '%s' on message: '%s'...",
                process.pid(), name, message));
    }

    @Override
    protected boolean onMessageReceived(String string) {
        return !string.contains(message);
    }

    @Override
    protected void onEndListening(){
        logger.info(String.format("Process [pid:%d] '%s' synchronized!", process.pid(), name));
    }

    @Override
    protected void onExceptionRaised(Exception e) {

    }
}
