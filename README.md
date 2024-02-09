# Congestion Toll

This project is a bare-bones implementation of a toll fee system with Spring Boot.

Add a vehicle -> Add passages.

Toll fees are calculated based on rules set in the application.properties.

Setting up the properties took more time than I expected, being a bit of a time optimist.

Things I would have done if I had more time:
- Tests!
- Error handling.
- Validation for input.
- More properties for different toll fees, like different years.
- Containerized the application with Docker.
  - Gotten the properties from environment variables, allowing for easier configuration. It wouldn't be something you would change in runtime, perhaps with blue/green deployment, but it would allow for GUI configuration for someone with less technical knowledge.
- Refactored, cleaned up the code and added comments. Things like utilities for TollFeeService. And definitely cleaned up TollCostIntervals.

## Properties

- `toll.free.vehicle.types`: emergency, bus, motorcycle, military, foreign, diplomat
- `toll.free.months`: july
- `toll.free.dayofweek`: saturday, sunday
- `toll.cost.intervals`:
    - 06:00-06:29: 8
    - 06:30-06:59: 13
    - 07:00-07:59: 18
    - 08:00-08:29: 13
    - 08:30-14:59: 8
    - 15:00-15:29: 13
    - 15:30-16:59: 18
    - 17:00-17:59: 13
    - 18:00-18:29: 8
    - 18:30-05:59: 0
- `toll.free.holidays2013`:
    - 01-01
    - 03-29
    - 03-31
    - 04-01
    - 05-01
    - 05-09
    - 06-23
    - 11-01
    - 11-11
    - 12-25
    - 12-26

## Controllers

### TollFeeController

- **Mapping:** `/toll/passage`
- **Description:** Controller for managing toll fee passages.
- **Endpoints:**
    - `POST /toll/passage`: Add toll fee passage.
- **Post Request Body Example:**
    ```json
    {
        "licensePlate": "ABC123",
        "dates": ["2013-01-01T14:00", "2024-01-02T23:59"]
    }
    ```

#### Add toll fee passage
```
curl -X POST \
http://localhost:8080/toll/passage \
-H 'Content-Type: application/json' \
-d '{
"licensePlate": "ABC123",
"dates": ["2013-01-01T14:00", "2024-01-02T23:59"]
}'
```

### VehicleController

- **Mapping:** `/vehicle`
- **Description:** Controller for managing vehicles.
- **Endpoints:**
    - `POST /vehicle/add`: Add a new vehicle.
    - `GET /vehicle`: Retrieve all vehicles.
- **Post Request Body Example:**
    ```json
    {
        "licensePlate": "ABC123",
        "type": "CAR"
    }
    ```

#### Add a new vehicle
```
curl -X POST \
http://localhost:8080/vehicle/add \
-H 'Content-Type: application/json' \
-d '{
"licensePlate": "ABC123",
"type": "car"
}'
```

# Retrieve all vehicles
curl -X GET http://localhost:8080/vehicle

## Build and Run

### Prerequisites

- Java Development Kit (JDK)
- Maven

### Build

To build the project, run:

```bash
mvn clean install
```

### Run
To run the project, run:

```bash
mvn spring-boot:run
```
The application will start and be available at http://localhost:8080.

