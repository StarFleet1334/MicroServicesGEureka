package com.micro.workload.model.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    private int month;
    private int totalDuration;

    public MonthSummary(int month) {
        this.month = month;
        this.totalDuration = 0;
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
