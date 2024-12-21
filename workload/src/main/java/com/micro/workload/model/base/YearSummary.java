package com.micro.workload.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearSummary {
    private int year;
    private Map<Integer, MonthSummary> monthSummaries = new ConcurrentHashMap<>();

    public YearSummary(int year) {
        this.year = year;
    }

}
