package com.example.congestion.services;

import com.example.congestion.models.TollCostIntervals;
import com.example.congestion.models.Vehicle;
import com.example.congestion.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

    @Service
    public class TollFeeService {
        private TollCostIntervals tollCostIntervals;
        @Autowired
        private VehicleRepository vehicleRepository;
        @Value("#{'${toll.free.vehicle.types}'.split(', ')}")
        private List<String> tollFreeVehicleTypes;
        @Value("#{'${toll.free.months}'.split(', ')}")
        private List<String> tollFreeMonths;

        @Value("#{'${toll.free.dayofweek}'.split(', ')}")
        private List<String> tollFreeDayOfWeek;

        @Value("#{'${toll.free.holidays2013}'.split(', ')}")
        private List<String> tollFreeHolidays2013;


        private static final int PASSAGE_FEE_WINDOW_MINUTES = 60;
        private static final int DAILY_MAX_TOLL_FEE = 60;


        public TollFeeService(VehicleRepository vehicleRepository, TollCostIntervals tollCostIntervals) {
            this.vehicleRepository = vehicleRepository;
            this.tollCostIntervals = tollCostIntervals;
        }


    private int calculateTollFeeFromDateTime(Vehicle vehicle, LocalDateTime dateTime) {
        if(isTollFreeMonth(dateTime)) {
            return 0;
        }
        if(isTollFreeDayOfWeek(dateTime)) {
            return 0;
        }

        if(isTollFreeHoliday(dateTime)) {
            return 0;
        }

        LocalTime time = dateTime.toLocalTime();

        for (Map.Entry<TollCostIntervals.Interval, Integer> entry : tollCostIntervals.getIntervalsMap().entrySet()) {
            TollCostIntervals.Interval interval = entry.getKey();
            int fee = entry.getValue();

            if (interval.contains(time)) {
                return fee;
            }
        }
        return 0;
    }

    private boolean isTollFreeDayOfWeek(LocalDateTime dateTime) {
            return tollFreeDayOfWeek.contains(dateTime.getDayOfWeek().toString().toLowerCase());
    }

    private boolean isTollFreeMonth(LocalDateTime dateTime) {
            return tollFreeMonths.contains(dateTime.getMonth().toString().toLowerCase());
    }

    private boolean isTollFreeHoliday(LocalDateTime dateTime) {
        String dateStr = dateTime.format(DateTimeFormatter.ofPattern("MM-dd"));
        return tollFreeHolidays2013.contains(dateStr);
    }


    public ResponseEntity<Vehicle> addTollFee(JsonNode requestNode) {
        String licensePlate = requestNode.get("licensePlate").asText();

        Vehicle vehicle;
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(licensePlate);

        if(vehicleOptional.isPresent()) {
            vehicle = vehicleOptional.get();
        } else {
            return ResponseEntity.notFound().build();
        }

        if(isTollFreeVehicle(vehicle)) {
            return ResponseEntity.ok(vehicle);
        }

        List<LocalDateTime> dates = extractSortedDatesList(requestNode);

        Map<LocalDateTime, Integer> passageToll = new HashMap<>();

        for (LocalDateTime dateTime : dates) {
            int toll = calculateTollFeeFromDateTime(vehicle, dateTime);
            passageToll.put(dateTime, toll);
        }

        removeLowerFeeWhenWithinPassageWindow(passageToll);
        compareTollWithDailyMax(passageToll, vehicle);

        vehicleRepository.save(vehicle);
        return ResponseEntity.ok(vehicle);
    }

    private void compareTollWithDailyMax(Map<LocalDateTime, Integer> passageToll, Vehicle vehicle) {
        passageToll.forEach((date, toll) -> {
            if (vehicle.getTollsMap().containsKey(date.toLocalDate())) {
                toll += vehicle.getTollsMap().get(date.toLocalDate());
                if(toll > DAILY_MAX_TOLL_FEE) {
                    toll = DAILY_MAX_TOLL_FEE;
                }
            }
            vehicle.getTollsMap().put(date.toLocalDate(), toll);
        });
    }

    //Assumption: all passages for a specific day is in this list, will not compare to existing vehicle tolls
    //Assumption: the passage time comparison window starts on the first passage, not that it's the most expensive passage within any window
    private void removeLowerFeeWhenWithinPassageWindow(Map<LocalDateTime, Integer> tollFees) {
        tollFees.entrySet().removeIf(entry -> {
            LocalDateTime current = entry.getKey();
            return tollFees.entrySet().stream()
                    .anyMatch(other -> {
                        LocalDateTime otherDate = other.getKey();
                        if(current.isBefore(otherDate.plusMinutes(PASSAGE_FEE_WINDOW_MINUTES)) && current.isAfter(otherDate)) {
                            return entry.getValue() >= other.getValue();
                        } else {
                            return false;
                        }
                    });
        });

    }

    private List<LocalDateTime> extractSortedDatesList(JsonNode requestNode) {
        JsonNode datesNode = requestNode.get("dates");

        List<LocalDateTime> dates = new ArrayList<>();
        for (JsonNode dateNode : datesNode) {
            dates.add(LocalDateTime.parse(dateNode.asText()));
        }

        dates.sort(Comparator.naturalOrder());
        return dates;
    }

    private boolean isTollFreeVehicle(Vehicle vehicle) {
        if(tollFreeVehicleTypes.contains(vehicle.getType().toLowerCase())) {
            return true;
        }
        return false;
    }

    public ResponseEntity<Integer> getTollByDate(String licensePlate, LocalDate date) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(licensePlate);

        if(vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get().getTollsMap().getOrDefault(date, 0));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Vehicle> addVehicle(String licensePlate, String type) {
        if(vehicleRepository.findById(licensePlate).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Vehicle vehicle = vehicleRepository.save(new Vehicle(licensePlate, type));
        return ResponseEntity.ok(vehicle);
    }

}

