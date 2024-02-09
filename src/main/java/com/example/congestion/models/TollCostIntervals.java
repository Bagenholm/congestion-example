package com.example.congestion.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class TollCostIntervals {

    private Map<Interval, Integer> intervalsMap;

    public TollCostIntervals(String tollCostIntervalsString) {
        this.intervalsMap = parseTollCostIntervals(tollCostIntervalsString);
    }

    public Map<Interval, Integer> getIntervalsMap() {
        return intervalsMap;
    }

    public static class Interval {
        private LocalTime start;
        private LocalTime end;

        public Interval(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        public Interval(String interval) {
            String[] startEnd = interval.split("-");
            this.start = LocalTime.parse(startEnd[0]);
            this.end = LocalTime.parse(startEnd[1]);
        }

        public boolean contains(LocalTime date) {
            return date.isAfter(start) && date.isBefore(end);
        }
    }

    private Map<TollCostIntervals.Interval, Integer> parseTollCostIntervals(String tollCostIntervalsString) {
        Map<TollCostIntervals.Interval, Integer> tollCostIntervals = new HashMap<>();

        tollCostIntervalsString = tollCostIntervalsString.replaceAll("[{}]", "");

        String[] intervals = tollCostIntervalsString.split(", ");
        for (String interval : intervals) {
            if (interval.isEmpty()) {
                continue;
            }

            String[] parts = interval.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid interval format: " + interval);
            }

            String intervalString = parts[0].trim();
            int cost = Integer.parseInt(parts[1].trim());

            String[] timeParts = intervalString.split("-");
            if (timeParts.length != 2) {
                throw new IllegalArgumentException("Invalid interval format: " + intervalString);
            }

            String startTimeStr = timeParts[0].trim();
            String endTimeStr = timeParts[1].trim();

            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            TollCostIntervals.Interval timeInterval = new TollCostIntervals.Interval(startTime, endTime);
            tollCostIntervals.put(timeInterval, cost);
        }

        return tollCostIntervals;
    }


}
