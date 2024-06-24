package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

public class TicketTest {

    @Test
    @DisplayName("The ID value is correctly defined and retrieved.")
    public void getIdTest() {
        //GIVEN a ticket Id
        int expectedId = 123;
        Ticket ticket = new Ticket();
        ticket.setId(expectedId);

        //WHEN call getId
        int currentId = ticket.getId();

        //THEN return the expected ticket Id
        assertEquals(expectedId, currentId);
    }

    @Test
    @DisplayName("The ParkingSpot is correctly defined and retrieved.")
    public void getParkingSpotTest() {
        //GIVEN a parking spot defined
        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(expectedParkingSpot);

        //WHEN call getParkingSpot
        ParkingSpot currentParkingSpot = ticket.getParkingSpot();

        //THEN return the expected ParkingSpot
        assertSame(expectedParkingSpot, currentParkingSpot);
    }

}