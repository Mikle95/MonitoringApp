package com.MonitoringApp.API.data;

import java.util.List;


public class Project {
    public String project_name;
    public String project_description;
    public String project_creator_login;
    public String project_company_name;

    public List<Task> tasks;

    @Override
    public String toString() {
        return project_name + "\n" + project_creator_login;
    }
}
