package com.example.congestion.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
public class VehicleType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    String type;
    boolean tollFree = false;
}
