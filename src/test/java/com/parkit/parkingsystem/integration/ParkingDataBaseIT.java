package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        // GIVEN a car with registration number ABCDEF parked
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // WHEN the car come in park
        parkingService.processIncomingVehicle();

        // THEN a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertFalse(parkingSpot.isAvailable());
        assertNotEquals(parkingSpot.getId(), parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType()));
    }

    @Test
    public void testParkingLotExit(){
        System.out.println("  "); // just nice

        // GIVEN a car with registration number ABCDEF parked
        testParkingACar();

        // Wait little time to go to exit : wait 1sec
        waitWhile(1000); // wait 1sec

        // WHEN car exiting parking
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // THEN fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice() >= 0);
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        // GIVEN a car with registration number ABCDEF parked
        testParkingACar();

        // AND exited once
        waitWhile(1000); // wait 1sec

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Wait for a while before re-entering
        waitWhile(1000); // wait 1sec

        // WHEN the same car comes in park again
        parkingService.processIncomingVehicle();
        waitWhile(1000); // wait 1sec

        // AND exits parking again
        parkingService.processExitingVehicle();

        // THEN fare should include a 5% discount and outTime is populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getOutTime());

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        double durationInHour = (outHour - inHour) / (1000.*3600);

        double originalPrice = durationInHour * Fare.CAR_RATE_PER_HOUR;
        double expectedPriceWithDiscount = originalPrice * 0.95;

        if (durationInHour < 0.5) {  // Free park when duration < 1/2 hour
            expectedPriceWithDiscount = 0;
        }

        assertEquals(expectedPriceWithDiscount, ticket.getPrice());
    }

    private void waitWhile(int timeInMilliSeconds) {
        try {
            Thread.sleep(timeInMilliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
