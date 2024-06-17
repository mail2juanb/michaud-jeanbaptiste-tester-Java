package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;


    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareUnkownType(){
        // GIVEN a vehicle parked 1 hour
        double parkingTimeInHour = 1.;
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) - (parkingTimeInHour * 3600 * 1000)));
        Date outTime = new Date();

        // AND type of vehicle unknown
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // THEN an exception should be thrown WHEN calculating fare
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    private static Stream<Arguments> provideArgForAnyVehicle() {
        return Stream.of(
                Arguments.of(ParkingType.CAR),
                Arguments.of(ParkingType.BIKE)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgForAnyVehicle")
    public void calculateFareAnyVehicleWithFutureInTime(ParkingType parkingType){
        // GIVEN a vehicle with a future entry date
        double parkingTimeInHour = 1.;
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) + (parkingTimeInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // THEN an exception should be thrown WHEN calculating fare
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    private static Stream<Arguments> provideArgForAnyDurationAnyDiscountAnyVehicle() {
        return Stream.of(
                Arguments.of(1, false, ParkingType.CAR, 1.5), // 1hour / !Discount / CAR / Fare=1.5
                Arguments.of(1, false, ParkingType.BIKE, 1.), // 1hour / !Discount / BIKE / Fare=1
                Arguments.of(1, true, ParkingType.CAR, 1.42), // 1hour / Discount / CAR / Fare=1.425
                Arguments.of(1, true, ParkingType.BIKE, 0.95), // 1hour / Discount / BIKE / Fare=0.95
                Arguments.of(0.75, false, ParkingType.CAR, 1.12), // 45mn / !Discount / CAR / Fare=1.125
                Arguments.of(0.75, false, ParkingType.BIKE, 0.75), // 45mn / !Discount / BIKE / Fare=0.75
                Arguments.of(0.45, false, ParkingType.CAR, 0.), // 27mn (Free) / !Discount / CAR / Fare=0.
                Arguments.of(0.45, false, ParkingType.BIKE, 0.), // 27mn (Free) / !Discount / BIKE / Fare=0.
                Arguments.of(24, false, ParkingType.CAR, 36.), // 24hours / !Discount / CAR / Fare=36
                Arguments.of(24, false, ParkingType.BIKE, 24.) // 24hours / !Discount / BIKE / Fare=24
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgForAnyDurationAnyDiscountAnyVehicle")
    public void calculateFareAny(double durationInHour, boolean discount, ParkingType parkingType, double expectedPrice) {
        // GIVEN a vehicle parked
        Date inTime = new Date();
        inTime.setTime((long) (System.currentTimeMillis() - (durationInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket, discount);

        // THEN Fare depend on values entered
        assertEquals(expectedPrice, ticket.getPrice());
    }

}
