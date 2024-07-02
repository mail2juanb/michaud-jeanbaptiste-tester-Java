package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    public static final String VEHICLE_REG_NUMBER = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_REG_NUMBER);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar(){
        // GIVEN a car with registration number ABCDEF parked
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // WHEN the car come in park
        parkingService.processIncomingVehicle();

        // THEN a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        assertEquals(VEHICLE_REG_NUMBER, ticket.getVehicleRegNumber());
        assertFalse(parkingSpot.isAvailable());
        assertNotEquals(parkingSpot.getId(), parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType()));
    }

    @Test
    public void testParkingLotExit(){
        // GIVEN a car with registration number ABCDEF parked since 1 hour
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        Ticket currentTicket = new Ticket();
        currentTicket.setParkingSpot(parkingSpot);
        currentTicket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        long enteringDate = new Date().getTime() - (3600 * 1000); // Current date/time minus 1 hour
        currentTicket.setInTime(new Date(enteringDate));
        ticketDAO.saveTicket(currentTicket);

        // WHEN car exiting parking
        parkingService.processExitingVehicle();

        // THEN fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice() >= 0);
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        // GIVEN a car with registration number ABCDEF parked with an existing ticket
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        Ticket oldTicket = new Ticket();
        oldTicket.setParkingSpot(parkingSpot);
        oldTicket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        oldTicket.setInTime(new Date(100000)); // Thursday, January 1st 1970 - 01:01:40
        oldTicket.setOutTime(new Date(250000)); // Thursday, January 1st 1970 - 01:04:40
        oldTicket.setPrice(4.25);
        ticketDAO.saveTicket(oldTicket);

        // AND the same vehicule entering, this user become recurring user
        long enteringDate = new Date().getTime() - (3600 * 1000); // Current date/time minus 1 hour

        Ticket newEnteringTicket = new Ticket();
        newEnteringTicket.setParkingSpot(parkingSpot);
        newEnteringTicket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
        newEnteringTicket.setInTime(new Date(enteringDate));
        ticketDAO.saveTicket(newEnteringTicket);

        // AND the expected price with discount
        double expectedPriceWithDiscount = FareCalculatorService.truncatePrice((1 * Fare.CAR_RATE_PER_HOUR) * 0.95);

        // WHEN recurring user exit parking again
        parkingService.processExitingVehicle();

        // THEN a discount is trigger
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        assertNotNull(ticket.getOutTime());

        assertEquals(expectedPriceWithDiscount, ticket.getPrice());
    }

}
