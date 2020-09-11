package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ProcessConfiguration {
    @JsonProperty(value = "java home")
    String javaHome = "";
    @JsonProperty(value = "environment variables")
    List<Entry> environment = Collections.emptyList();
    @JsonProperty(value = "java arguments")
    List<Entry> arguments = Collections.emptyList();
    @JsonProperty(value = "java tool options")
    List<Entry> javaToolOptions = Collections.emptyList();
    @JsonProperty(value = "maven options")
    List<Entry> mavenOptions = Collections.emptyList();
    @JsonProperty(value = "profiles")
    List<String> profiles = Collections.emptyList();

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public List<Entry> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<Entry> environment) {
        this.environment = environment;
    }

    public List<Entry> getArguments() {
        return arguments;
    }

    public void setArguments(List<Entry> arguments) {
        this.arguments = arguments;
    }

    public List<Entry> getJavaToolOptions() {
        return javaToolOptions;
    }

    public void setJavaToolOptions(List<Entry> javaToolOptions) {
        this.javaToolOptions = javaToolOptions;
    }

    public List<Entry> getMavenOptions() {
        return mavenOptions;
    }

    public void setMavenOptions(List<Entry> mavenOptions) {
        this.mavenOptions = mavenOptions;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> freeFormParameters) {
        this.profiles = freeFormParameters;
    }
}
