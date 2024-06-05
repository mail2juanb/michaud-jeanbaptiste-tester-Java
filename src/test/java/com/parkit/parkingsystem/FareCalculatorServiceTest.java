package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
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

    @ParameterizedTest
    @EnumSource(ParkingType.class)
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

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @Disabled("Test method can be removed if calculateFareAny is correct")
    public void calculateFareAnyVehicle(ParkingType parkingType){
        // GIVEN a vehicle parked 1 hour
        double parkingTimeInHour = 1.;
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) - (parkingTimeInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double farePerHour;
        farePerHour = vehicleRatePerHour(parkingType);

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket);

        // THEN Fare should be equals to parkingTimeInHour * parking fare per hour
        assertEquals((parkingTimeInHour * farePerHour) , ticket.getPrice());
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @Disabled("Test method can be removed if calculateFareAny is correct")
    public void calculateFareAnyVehicleWithLessThanOneHourParkingTime(ParkingType parkingType){
        // GIVEN a vehicle parked less than 1 hour and more than half an hour
        double parkingTimeInHour = 45. / 60; //45mn parking time
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) - (parkingTimeInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double farePerHour;
        farePerHour = vehicleRatePerHour(parkingType);

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket);

        // THEN Fare should be equals to 0.75 * parking fare per hour
        assertEquals( (parkingTimeInHour * farePerHour) , ticket.getPrice());
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @Disabled("Test method can be removed if calculateFareAny is correct")
    public void calculateFareAnyVehicleWithMoreThanADayParkingTime(ParkingType parkingType){
        // GIVEN a vehicle parked more than a day
        double parkingTimeInHour = 24.; //24 hours parking time
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) - (parkingTimeInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double farePerHour;
        farePerHour = vehicleRatePerHour(parkingType);

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket);

        // THEN Fare should be equal to 24 * parking fare per hour
        assertEquals( (parkingTimeInHour * farePerHour) , ticket.getPrice());
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @Disabled("Test method can be removed if calculateFareAny is correct")
    public void calculateFareAnyVehicleWithLessThan30minutesParkingTime(ParkingType parkingType){
        // GIVEN a vehicle parked less than 30mn
        double parkingTimeInHour = 29. / 60; //29mn parking time
        Date inTime = new Date();
        inTime.setTime((long) ((System.currentTimeMillis()) - (parkingTimeInHour * (3600 * 1000))));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket);

        // THEN Fare should be free because <30mn parking time
        assertEquals( (0) , ticket.getPrice());
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @Disabled("Test method can be removed if calculateFareAny is correct")
    public void calculateFareAnyVehicleWithDiscount(ParkingType parkingType){
        // GIVEN a vehicle parked more than 30mn
        double parkingTimeInHour = 60. / 60; //60mn parking time
        Date inTime = new Date();
        inTime.setTime((long) (System.currentTimeMillis() - (parkingTimeInHour * (3600 * 1000))));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertEquals(0, ticket.getPrice());

        double farePerHour = vehicleRatePerHour(parkingType);

        // AND vehicle known at least 1 times
        boolean discount = true;

        final double originalPrice = parkingTimeInHour * farePerHour;
        final double discountPrice = 0.05 * originalPrice;
        final double expectedPrice = originalPrice - discountPrice;

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket, discount);

        // THEN Fare should be reduced by 5%.
        assertEquals(expectedPrice, ticket.getPrice());
    }

    private static Stream<Arguments> provide() {
        return Stream.of(
                Arguments.of(1, false, ParkingType.CAR), // 1hour / !Discount / CAR
                Arguments.of(1, false, ParkingType.BIKE), // 1hour / !Discount / BIKE
                Arguments.of(1, true, ParkingType.CAR), // 1hour / Discount / CAR
                Arguments.of(1, true, ParkingType.BIKE), // 1hour / Discount / BIKE
                Arguments.of(0.75, false, ParkingType.CAR), // 45mn / !Discount / CAR
                Arguments.of(0.75, false, ParkingType.BIKE), // 45mn / !Discount / BIKE
                Arguments.of(0.45, false, ParkingType.CAR), // 27mn / !Discount / CAR
                Arguments.of(0.45, false, ParkingType.BIKE), // 27mn / !Discount / BIKE
                Arguments.of(24, false, ParkingType.CAR), // 24hours / !Discount / CAR
                Arguments.of(24, false, ParkingType.BIKE) // 24hours / !Discount / BIKE
        );
    }

    @ParameterizedTest
    @MethodSource("provide")
    public void calculateFareAny(double durationInHour, boolean discount, ParkingType parkingType) {
        // GIVEN a vehicle parked
        Date inTime = new Date();
        inTime.setTime((long) (System.currentTimeMillis() - (durationInHour * 3600 * 1000)));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double farePerHour = vehicleRatePerHour(parkingType);

        // >30mn = Free
        double originalPrice = durationInHour * farePerHour;
        if (durationInHour < 0.5) {
            originalPrice = 0;
        }

        // Known Vehicle = 5%Off
        double discountPrice = 0;
        if (discount) {
            discountPrice = 0.05 * originalPrice;
        }
        final double expectedPrice = originalPrice - discountPrice;

        // WHEN vehicle exit
        fareCalculatorService.calculateFare(ticket, discount);

        // THEN Fare depend on values entered
        assertEquals(expectedPrice, ticket.getPrice());
    }

    private static double vehicleRatePerHour(ParkingType parkingType) {
        double farePerHour;
        switch (parkingType.name()) {
            case "CAR":
                farePerHour = Fare.CAR_RATE_PER_HOUR;
                break;
            case "BIKE":
                farePerHour = Fare.BIKE_RATE_PER_HOUR;
                break;
            default:
                throw new IllegalStateException("Unexpected parkingType value: " + parkingType.name());
        }
        return farePerHour;
    }

}
