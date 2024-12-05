package com.micro.workload.model.base;

public class MonthSummary {
    private int month;
    private int totalDuration;

    public MonthSummary(int month) {
        this.month = month;
        this.totalDuration = 0;
    }

    public int getMonth() {
        return month;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public synchronized void addDuration(int duration) {
        this.totalDuration += duration;
    }

    public synchronized void subtractDuration(int duration) {
        this.totalDuration -= duration;
        if (this.totalDuration < 0) {
            this.totalDuration = 0;
        }
    }
}
