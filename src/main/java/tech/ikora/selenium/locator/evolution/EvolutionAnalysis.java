package tech.ikora.selenium.locator.evolution;

import org.apache.commons.cli.*;
import tech.ikora.selenium.locator.evolution.configuration.Configuration;
import tech.ikora.selenium.locator.evolution.configuration.ConfigurationParser;

import java.io.IOException;

public class EvolutionAnalysis {

    public static void main(String[] args){
        try{
            final Configuration configuration = loadConfiguration(args);
        } catch (Exception e){

        }
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
