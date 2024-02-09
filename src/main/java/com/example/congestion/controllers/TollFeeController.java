package com.example.congestion.controllers;

import com.example.congestion.models.Vehicle;
import com.example.congestion.services.TollFeeService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/toll")
public class TollFeeController {

    @Autowired
    TollFeeService tollFeeService;

    public TollFeeController(TollFeeService tollFeeService) {
        this.tollFeeService = tollFeeService;
    }

    @PostMapping("/passage")
    public ResponseEntity<Vehicle> addTollFee(@RequestBody JsonNode jsonNode) {
        return tollFeeService.addTollFee(jsonNode);
    }
}
