package tech.ikora.selenium.locator.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.Date;
import java.util.Set;

public class GitConfiguration {
    @JsonProperty(value = "repositories", required = true)
    private Set<URL> repositories;
    @JsonProperty(value = "branch", defaultValue = "master")
    private String branch = "master";
    @JsonProperty(value = "token", required = true)
    private String token;
    @JsonProperty(value = "start date")
    private Date startDate;
    @JsonProperty(value = "end date")
    private Date endDate;
    @JsonProperty(value = "ignore commits")
    private Set<String> ignoreCommits;
    @JsonProperty(value = "maximum number of commits", defaultValue = "0")
    private int maximumCommitsNumber = 0;

    public Set<URL> getRepositories() {
        return repositories;
    }

    public void setRepositories(Set<URL> repositories) {
        this.repositories = repositories;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<String> getIgnoreCommits() {
        return ignoreCommits;
    }

    public void setIgnoreCommits(Set<String> ignoreCommits) {
        this.ignoreCommits = ignoreCommits;
    }

    public int getMaximumCommitsNumber() {
        return maximumCommitsNumber;
    }

    public void setMaximumCommitsNumber(int maximumCommitsNumber) {
        this.maximumCommitsNumber = maximumCommitsNumber;
    }
}
