package com.micro.workload.model.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YearSummary {
    private int year;
    private Map<Integer, MonthSummary> monthSummaries = new ConcurrentHashMap<>();

    public YearSummary(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public Map<Integer, MonthSummary> getMonthSummaries() {
        return monthSummaries;
    }
}
