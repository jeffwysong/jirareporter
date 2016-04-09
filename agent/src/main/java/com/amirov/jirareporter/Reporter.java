package com.amirov.jirareporter;

import com.amirov.jirareporter.jira.JIRAConfig;
import com.amirov.jirareporter.teamcity.TeamCityXMLParser;
import com.atlassian.jira.rest.client.domain.Comment;
import jetbrains.buildServer.agent.BuildProgressLogger;

import static com.amirov.jirareporter.jira.JIRAClient.*;

public class Reporter {
    private static String issueId;
    private BuildProgressLogger myLogger;
    private TeamCityXMLParser parser = new TeamCityXMLParser();


    public Reporter(BuildProgressLogger logger){
        myLogger = logger;
    }

    public void report(String issue){
        issueId = issue;
        myLogger.message("\nISSUE: " + issue
                + "\nTitle: " + getIssue().getSummary()
                + "\nDescription: " + getIssue().getDescription());
        getRestClient().getIssueClient().addComment(getIssue().getCommentsUri(), Comment.valueOf(parser.getTestResultText()));
    }

    public void progressIssue(){
        if(RunnerParamsProvider.progressIssueIsEnable() == null){}
        else if(RunnerParamsProvider.progressIssueIsEnable().equals("true")){
            String transitionName = JIRAConfig.prepareJiraWorkflow(parser.getStatusBuild()).get(getIssueStatus());
            if (transitionName != null) {
                getRestClient().getIssueClient().transition(getIssue().getTransitionsUri(), getTransitionInput(transitionName));
            }
        }
    }

    public static String getIssueId(){
        return issueId;
    }
}
