package com.MonitoringApp.API.data;

import java.sql.Timestamp;

public class Task {
    public long task_id;
    public String creator_login;
    public String project_name;
    public String task_name;
    public String task_description;
    public Timestamp start_time;
    public Timestamp end_time;
    public String status;
    public String progress;
    public String worker_login;

    @Override
    public String toString() {
        return task_name + "\t\t" + status +
                "\n" + creator_login;
    }

    public static class Status{
        public static final String[] mas = new String[] {"Новый", "PROGRESS", "Завершено", "Переделывается"};
        public static final String new_task = mas[0];
        public static final String in_progress = mas[1];
        public static final String ended = mas[2];
        public static final String rejected = mas[3];

    }
}
