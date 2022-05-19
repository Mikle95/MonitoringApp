package com.MonitoringApp.API.data;

import java.text.ParseException;

public class JobActivity {
    public String start_time;
    public String end_time;

    public long getStartTime(){
        try {
            return Task.df.parse(start_time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
