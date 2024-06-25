package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static junit.framework.Assert.*;

public class TicketDAODataBaseIT {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();


    private static final DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    private TicketDAO ticketDAO;
    private Ticket ticket;
    public static final String VEHICLE_REG_NUMBER = "ABCDEF";

    @BeforeEach
    public void setUp() {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService.clearDataBaseEntries();
        ticket = new Ticket();
    }

    @Test
    public void saveTicketTest() {
        //GIVEN a ticket
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        ticket.setPrice(10.);
        ticket.setInTime(new Date());

        //WHEN save ticket
        ticketDAO.saveTicket(ticket);

        //THEN check that ticket is correctly saved
        Ticket fetchedTicket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        assertNotNull(fetchedTicket);
        assertEquals(VEHICLE_REG_NUMBER, fetchedTicket.getVehicleRegNumber());
        assertEquals(10., fetchedTicket.getPrice());
        assertNotNull(fetchedTicket.getInTime());
        assertNull(fetchedTicket.getOutTime());
    }

    @Test
    public void getTicket() {
        //GIVEN a ticket in test database
        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(expectedParkingSpot);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        ticket.setPrice(10.);
        ticket.setInTime(new Date());

        ticketDAO.saveTicket(ticket);

        //WHEN save ticket
        Ticket currentTicket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);

        //THEN retrieve ticket correctly
        assertNotNull(currentTicket);
        assertEquals(VEHICLE_REG_NUMBER, currentTicket.getVehicleRegNumber());
        assertEquals(expectedParkingSpot, currentTicket.getParkingSpot());
        assertEquals(ticket.getPrice(), currentTicket.getPrice());
    }

    @Test
    public void updateTicketTest() {
        //GIVEN a ticket to update in test database
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        ticket.setInTime(new Date());

        ticketDAO.saveTicket(ticket);

        // And update this ticket
        ticket.setPrice(10.);
        ticket.setOutTime(new Date());

        //WHEN update ticket
        boolean currentTicketUpdated = ticketDAO.updateTicket(ticket);

        //THEN check ticket's details updated correctly
        assertTrue(currentTicketUpdated);

        Ticket currentTicket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        assertEquals(10., currentTicket.getPrice());

        assertNotNull(currentTicket.getOutTime());
    }

    @Test
    public void getNbTicketTest() {
        //GIVEN same vehicle come twice
        ParkingSpot parkingSpotA = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticketA = new Ticket();
        ticketA.setParkingSpot(parkingSpotA);
        ticketA.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        ticketA.setInTime(new Date(100000)); // Thursday, January 1st 1970 - 01:01:40
        ticketA.setOutTime(new Date(250000)); // Thursday, January 1st 1970 - 01:04:40
        ticketA.setPrice(4.25);
        ticketDAO.saveTicket(ticketA);

        ParkingSpot parkingSpotB = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticketB = new Ticket();
        ticketB.setParkingSpot(parkingSpotB);
        ticketB.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        ticketB.setInTime(new Date(4450000)); // Thursday, January 1st 1970 - 02:14:10
        ticketB.setOutTime(new Date(6660000)); // Thursday, January 1st 1970 - 02:51:00
        ticketB.setPrice(4.25);
        ticketDAO.saveTicket(ticketB);

        //WHEN counts the number of occurrences of the vehicle
        int count = ticketDAO.getNbTicket(VEHICLE_REG_NUMBER);

        //THEN return vehicle twice
        assertEquals(2, count);
    }

}
