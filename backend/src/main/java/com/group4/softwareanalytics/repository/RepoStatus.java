package com.group4.softwareanalytics.repository;

public class RepoStatus {

    private Boolean fetchedInfo = false;
    private Boolean fetchedIssues = false;
    private Boolean fetchedCommits = false;

    public RepoStatus() {}

    public Boolean getFetchedInfo() {
        return fetchedInfo;
    }

    public void setFetchedInfo(Boolean fetchedInfo) {
        this.fetchedInfo = fetchedInfo;
    }

    public Boolean getFetchedIssues() {
        return fetchedIssues;
    }

    public void setFetchedIssues(Boolean fetchedIssues) {
        this.fetchedIssues = fetchedIssues;
    }

    public Boolean getFetchedCommits() {
        return fetchedCommits;
    }

    public void setFetchedCommits(Boolean fetchedCommits) {
        this.fetchedCommits = fetchedCommits;
    }

}