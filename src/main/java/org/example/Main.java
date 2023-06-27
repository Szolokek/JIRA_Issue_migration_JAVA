package org.example;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /*Number of issues called at once*/
    private static final int MAXRESULTS = 1000;
    private static final String DATEFORMAT = "yyyy-MM-dd";


    public static void main(String[] args) {
        File myFile = new File("issues.csv");
        try(FileWriter outputfile = new FileWriter(myFile);
            CSVWriter writer = new CSVWriter(outputfile)){

            /*Header of CVS file*/
            String[] header = { "id","key",
                    "fields.issuetype.id","fields.issuetype.description","fields.issuetype.name","fields.issuetype.subtask","fields.issuetype.avatarId",
                    "fields.project.id","fields.project.key","fields.project.name", "fields.project.TypeKey",
                    "fields.resolution.id", "fields.resolution.description","fields.resolution.name",
                    "fields.resolutiondate",
                    "fields.workratio",
                    "fields.lastViewed",
                    "fields.watches.watchCount","fields.watches.isWatching",
                    "fields.created",
                    "fields.priority.name","fields.priority.id",
                    "fields.assignee.name","fields.assignee.key","fields.assignee.displayName", "fields.assignee.active","fields.assignee.timeZone",
                    "fields.updated",
                    "fields.status.description","fields.status.name","fields.status.id",
                    "fields.status.statusCategory.id","fields.status.statusCategory.key","fields.status.statusCategory.colorName","fields.status.statusCategory.name",
                    "fields.summary",
                    "fields.creator.name", "fields.creator.key", "fields.creator.displayName","fields.creator.active", "fields.creator.timeZone",
                    "fields.reporter.name","fields.reporter.key", "fields.reporter.displayName", "fields.reporter.active", "fields.reporter.timeZone",
                    "fields.duedate",
                    "fields.votes.votes", "fields.votes.hasVoted"
                    };
            writer.writeNext(header);



            Response response;

            System.out.println("Processing "+MAXRESULTS+" issues!");
            response = callAPI(0);
            writeToFile(writer, response);
            System.out.println("Done! "+(response.total-MAXRESULTS)+" left.");

            for(int i = MAXRESULTS ; i < response.total; i += MAXRESULTS){
                System.out.println("Processing "+MAXRESULTS+" issues!");
                response = callAPI(i);
                writeToFile(writer, response);
                System.out.println("Done! "+(response.total-(i+MAXRESULTS))+" left.");
            }

        } catch (InterruptedException | IOException | URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public static Response callAPI(int i) throws URISyntaxException, IOException, InterruptedException{
        HttpRequest getRequest;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse;
        Gson gson = new Gson();
        getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://issues.jenkins.io/rest/api/latest/search?maxResults="+MAXRESULTS+"&startAt="+i))
                .build();

        getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(getResponse.body(), Response.class);
    }

    public static void writeToFile(CSVWriter writer, Response response){
        for(Issue issue : response.issues){
            writer.writeNext(getDataFromIssue(issue));
        }
    }
    public static String[] getDataFromIssue(Issue issue){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        ArrayList<String> values = new ArrayList<>();
        addIssueDetails(issue, values);
        addIssueTypeDetails(issue.fields.issuetype, values);
        addProjectDetails(issue.fields.project, values);
        addResolutionDetails(issue.fields.resolution, values);
        values.add((issue.fields.resolutiondate != null) ? dateFormat.format(issue.fields.resolutiondate) : "");
        values.add(Integer.toString(issue.fields.workratio));
        values.add((issue.fields.lastViewed != null) ? issue.fields.lastViewed.toString() : "");
        addWatchesDetails(issue.fields.watches, values);
        values.add((issue.fields.created != null) ? dateFormat.format(issue.fields.created) : "");
        addPriorityDetails(issue.fields.priority, values);
        addAssigneeDetails(issue.fields.assignee, values);
        values.add((issue.fields.updated != null) ? dateFormat.format(issue.fields.updated) : "");
        addStatusDetails(issue.fields.status, values);
        values.add((issue.fields.summary != null) ? issue.fields.summary : "");
        addCreatorDetails(issue.fields.creator, values);
        addReporterDetails(issue.fields.reporter, values);
        values.add((issue.fields.duedate != null) ? dateFormat.format(issue.fields.duedate) : "");
        addVotesDetails(issue.fields.votes, values);


        return values.toArray(new String[values.size()]);

    }

    private static void addVotesDetails(Votes votes, ArrayList<String> values) {
        if(votes == null){
            addEmptyStringToList(2, values);
        }
        else {
            values.add(Integer.toString(votes.votes));
            values.add(String.valueOf(votes.hasVoted));

        }
    }

    private static void addReporterDetails(Reporter reporter, ArrayList<String> values) {
        if(reporter == null){
            addEmptyStringToList(5, values);
        }
        else {
            values.add(convertNullStr(reporter.name));
            values.add(convertNullStr(reporter.key));
            values.add(convertNullStr(reporter.displayName));
            values.add(String.valueOf(reporter.active));
            values.add(convertNullStr(reporter.timeZone));
        }
    }

    private static void addCreatorDetails(Creator creator, ArrayList<String> values) {
        if(creator == null){
            addEmptyStringToList(5, values);
        }
        else {
            values.add(convertNullStr(creator.name));
            values.add(convertNullStr(creator.key));
            values.add(convertNullStr(creator.displayName));
            values.add(String.valueOf(creator.active));
            values.add(convertNullStr(creator.timeZone));
        }
    }

    private static void addStatusDetails(Status status, ArrayList<String> values) {
        if(status == null){
            addEmptyStringToList(7, values);
        }
        else {
            values.add(convertNullStr(status.description));
            values.add(convertNullStr(status.name));
            values.add(convertNullStr(status.id));
            if(status.statusCategory == null){
                addEmptyStringToList(4, values);
            }
            else{
                values.add(Integer.toString(status.statusCategory.id));
                values.add(convertNullStr(status.statusCategory.key));
                values.add(convertNullStr(status.statusCategory.colorName));
                values.add(convertNullStr(status.statusCategory.name));
            }
        }
    }

    private static void addAssigneeDetails(Assignee assignee, ArrayList<String> values) {
        if(assignee == null){
            addEmptyStringToList(5, values);
        }
        else {
            values.add(convertNullStr(assignee.name));
            values.add(convertNullStr(assignee.key));
            values.add(convertNullStr(assignee.displayName));
            values.add(String.valueOf(assignee.active));
            values.add(convertNullStr(assignee.timeZone));
        }
    }

    private static void addPriorityDetails(Priority priority, ArrayList<String> values) {
        if(priority == null){
            addEmptyStringToList(2, values);
        }
        else {
            values.add(convertNullStr(priority.name));
            values.add(convertNullStr(priority.id));
        }
    }

    private static void addWatchesDetails(Watches watches, ArrayList<String> values) {
        if(watches == null){
            addEmptyStringToList(2, values);
        }
        else {
            values.add(Integer.toString(watches.watchCount));
            values.add(String.valueOf(watches.isWatching));
        }
    }

    private static void addResolutionDetails(Resolution resolution, ArrayList<String> values) {
        if(resolution == null){
            addEmptyStringToList(3, values);
        }
        else {
            values.add(convertNullStr(resolution.id));
            values.add(convertNullStr(resolution.description));
            values.add(convertNullStr(resolution.name));
        }
    }

    private static void addProjectDetails(Project project, List<String> values) {
        if(project == null){
            addEmptyStringToList(4, values);
        }
        else {
            values.add(convertNullStr(project.id));
            values.add(convertNullStr(project.key));
            values.add(convertNullStr(project.name));
            values.add(convertNullStr(project.projectTypeKey));
        }
    }

    public static void addIssueDetails(Issue issue, List<String> values){
        values.add(convertNullStr(issue.id));
        values.add(convertNullStr(issue.key));
    }
    public static void addIssueTypeDetails(Issuetype issuetype, List<String> values){
        if(issuetype == null){
            addEmptyStringToList(5, values);
        }
        else {
            values.add(convertNullStr(issuetype.id));
            values.add(convertNullStr(issuetype.description));
            values.add(convertNullStr(issuetype.name));
            values.add(String.valueOf(issuetype.subtask));
            values.add(Integer.toString(issuetype.avatarId));
        }
    }

    public static void addEmptyStringToList(int quantity, List<String> values){
        for(int i = 0; i < quantity; i++){
            values.add("");
        }
    }

    public static String convertNullStr(String element) {
        return (element != null) ? element : "";
    }


}
