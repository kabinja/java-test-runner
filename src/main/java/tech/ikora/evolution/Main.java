package tech.ikora.evolution;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.evolution.configuration.ConfigurationParser;
import tech.ikora.evolution.configuration.Configuration;

import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args){
        try{
            RunnerFactory.fromConfiguration(loadConfiguration(args)).execute();
        } catch (Exception e){
            logger.error(String.format("Exit with error: %s", e.getMessage()));
            System.exit(1);
        }

        logger.info("Finished without error");
    }

    static private Configuration loadConfiguration(String[] args) throws ParseException, IOException {
        final Options options = new Options();
        final CommandLineParser parser = new DefaultParser();

        options.addOption("config", true, "path to the json configuration file");
        final CommandLine cmd = parser.parse(options, args);

        if(!cmd.hasOption("config")){
            throw new MissingArgumentException("config");
        }

        return ConfigurationParser.parse(cmd.getOptionValue("config"));
    }
}
