package com.example.congestion.services;

import com.example.congestion.models.Vehicle;
import com.example.congestion.repositories.VehicleRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public ResponseEntity<Vehicle> addVehicle(Vehicle vehicle) {
        if(vehicleRepository.findById(vehicle.getLicensePlate()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(vehicleRepository.save(vehicle));
    }

    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAll());
    }
}
