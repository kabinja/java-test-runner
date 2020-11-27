package tech.ikora.evolution.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessListenerThread extends Thread {
    private static final Logger logger = LogManager.getLogger(ProcessListenerThread.class);

    private final Process process;
    private final String name;
    private final String message;

    public ProcessListenerThread(Process process, String name, String message) {
        this.process = process;
        this.name = name;
        this.message = message;
    }

    @Override
    public void run(){
        logger.info(String.format("Synchronizing process [pid:%d] '%s' on message: '%s'...",
                process.pid(), name, message));

        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        try{
            while ((line = reader.readLine()) != null){
                logger.debug(line);

                if(line.contains("ERROR")){
                    logger.error(String.format("[pid:%d] %s", process.pid(), line));
                }

                if(line.contains(message)){
                    break;
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Something went wrong when reading stream from process [pid:%d] '%s': %s",
                    process.pid(), name, e.getMessage()));
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error(String.format("Something went wrong when closing stream from process [pid:%d] '%s': %s",
                        process.pid(), name, e.getMessage()));
            }
        }

        logger.info(String.format("Process [pid:%d] '%s' synchronized!", process.pid(), name));
    }
}
