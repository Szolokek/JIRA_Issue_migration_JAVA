package org.example;
import java.util.Date;

public class Fields{
    public Issuetype issuetype;
    public Project project;
    public Resolution resolution;
    public Date resolutiondate;
    public int workratio;
    public Object lastViewed;
    public Watches watches;
    public Date created;
    public Assignee assignee;
    public Date updated;
    public Status status;
    public String summary;
    public Creator creator;
    public Reporter reporter;
    public Date duedate;
    public Votes votes;
    public Priority priority;
}

class Assignee{
    public String name;
    public String key;
    public String displayName;
    public boolean active;
    public String timeZone;
}

class Component{
    public String id;
    public String name;
    public String description;
}

class Creator{
    public String name;
    public String key;
    public String displayName;
    public boolean active;
    public String timeZone;
}

class Issuetype{
    public String self;
    public String id;
    public String description;
    public String name;
    public boolean subtask;
    public int avatarId;
}

class Priority{
    public String name;
    public String id;
}

class Project{
    public String self;
    public String id;
    public String key;
    public String name;
    public String projectTypeKey;
}

class Reporter{
    public String name;
    public String key;
    public String displayName;
    public boolean active;
    public String timeZone;
}

class Resolution {
    public String id;
    public String description;
    public String name;
}

class Status{
    public String description;
    public String name;
    public String id;
    public StatusCategory statusCategory;
}

class StatusCategory{
    public int id;
    public String key;
    public String colorName;
    public String name;
}

class Votes{
    public int votes;
    public boolean hasVoted;
}

class Watches{
    public int watchCount;
    public boolean isWatching;
}


