package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

public class TicketTest {

    @Test
    @DisplayName("The ID value is correctly defined and retrieved.")
    public void getIdTicketTest() {
        //GIVEN a ticket Id
        int expectedId = 123;
        Ticket ticket = new Ticket();
        ticket.setId(expectedId);

        //WHEN retrieve Id
        int currentId = ticket.getId();

        //THEN return the expected ticket Id
        assertEquals(expectedId, currentId);
    }

    @Test
    @DisplayName("The ParkingSpot is correctly defined and retrieved.")
    public void getParkingSpotTicketTest() {
        //GIVEN a ParkingSpot
        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(expectedParkingSpot);

        //WHEN retrieve ParkingSpot
        ParkingSpot currentParkingSpot = ticket.getParkingSpot();

        //THEN return the expected ParkingSpot
        assertSame(expectedParkingSpot, currentParkingSpot);
    }

    @Test
    @DisplayName("The VehicleRegNumber is correctly defined and retrieved.")
    public void getVehicleRegNumberTicketTest() {
        //GIVEN a VehicleRegNumber
        String expectedVehicleRegNumber = "ABC123";
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(expectedVehicleRegNumber);

        //WHEN retrieve getVehicleRegNumber
        String currentVehicleRegNumber = ticket.getVehicleRegNumber();

        //THEN return the expected VehicleRegNumber
        assertEquals(expectedVehicleRegNumber, currentVehicleRegNumber);
    }

    @Test
    @DisplayName("The price is correctly defined and retrieved.")
    public void getPriceTicketTest() {
        //GIVEN a Price
        double expectedPrice = 15.75;
        Ticket ticket = new Ticket();
        ticket.setPrice(expectedPrice);

        //WHEN retrieve Price
        double currentPrice = ticket.getPrice();

        //THEN return the expected Price
        assertEquals(expectedPrice, currentPrice, 0);
    }

    @Test
    @DisplayName("InTime is correctly defined and retrieved.")
    public void getInTimeTicketTest() {
        //GIVEN an InTime
        Date expectedInTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setInTime(expectedInTime);

        //WHEN retrieve the InTime
        Date currentInTime = ticket.getInTime();

        //THEN return the expected InTime
        assertEquals(expectedInTime, currentInTime);
    }

    @Test
    @DisplayName("OutTime is correctly defined and retrieved.")
    public void getOutTimeTicketTest() {
        //GIVEN an OutTime
        Date expectedOutTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setOutTime(expectedOutTime);

        //WHEN retrieve the OutTime
        Date currentOutTime = ticket.getOutTime();

        //THEN return the expected OutTime
        assertEquals(expectedOutTime, currentOutTime);
    }

}