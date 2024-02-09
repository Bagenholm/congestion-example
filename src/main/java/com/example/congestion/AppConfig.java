package com.example.congestion;

import com.example.congestion.models.TollCostIntervals;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${toll.cost.intervals}")
    private String tollCostIntervalsString;

    @Bean
    public TollCostIntervals tollCostIntervals() {
        return new TollCostIntervals(tollCostIntervalsString);
    }
}
