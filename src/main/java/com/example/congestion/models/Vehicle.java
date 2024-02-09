package com.example.congestion.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class Vehicle implements Serializable {

    public Vehicle(String licensePlate, String type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public Vehicle() {
    }
    
    @Serial
    private static final long serialVersionUID = 0L;

    @Id
    String licensePlate;
    String type;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<LocalDate, Integer> tollsMap = new HashMap<>();
}
