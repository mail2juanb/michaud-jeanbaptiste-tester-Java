package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private Ticket ticket;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setVehicleRegNumber("ABCDEF");
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    private static Stream<Arguments> provideArgForDiscount() {
        return Stream.of(
                Arguments.of(0, ParkingType.CAR),               // Car - Without Discount
                Arguments.of(1, ParkingType.CAR),               // Car - With Discount
                Arguments.of(0, ParkingType.BIKE),              // Bike - Without Discount
                Arguments.of(1, ParkingType.BIKE)               // Bike - With Discount
        );
    }

    @ParameterizedTest(name = "{index} => {1}")
    @MethodSource("provideArgForDiscount")
    public void processExitingVehicle(int value, ParkingType parkingType){
        // GIVEN a vehicle parked
        try {
            doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);
        ticket.setParkingSpot(parkingSpot);
        doReturn(ticket).when(ticketDAO).getTicket(anyString());
        doReturn(true).when(ticketDAO).updateTicket(any(Ticket.class));
        doReturn(value).when(ticketDAO).getNbTicket(any(Ticket.class));
        doReturn(true).when(parkingSpotDAO).updateParking(any(ParkingSpot.class));

        // WHEN vehicle exiting
        parkingService.processExitingVehicle();

        // THEN verify that methods updateParkingSpot, updateTicket and getNbTicket called one time
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(Ticket.class));
    }

    @ParameterizedTest(name = "{index} => {1}")
    @MethodSource("provideArgForDiscount")
    public void processIncomingVehicle(int value, ParkingType parkingType) {
        // GIVEN a vehicle unknown incoming
        try {
            doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);
        doReturn((parkingType == ParkingType.CAR) ? 1 : (parkingType == ParkingType.BIKE) ? 2 : 0).when(inputReaderUtil).readSelection();
        doReturn(1).when(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));
        ticket.setParkingSpot(parkingSpot);
        doReturn(value).when(ticketDAO).getNbTicket(any(Ticket.class));
        doReturn(true).when(parkingSpotDAO).updateParking(any(ParkingSpot.class));

        // WHEN vehicle incoming
        parkingService.processIncomingVehicle();

        // THEN check that methods updateParkingSpot, saveTicket and getNbTicket called one time
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(Ticket.class));
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    public void processExitingVehicleUnableUpdate(ParkingType parkingType) {
        // GIVEN a vehicle parked but update ticket fail
        try {
            doReturn("ABCDEF").when(inputReaderUtil).readVehicleRegistrationNumber();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);
        ticket.setParkingSpot(parkingSpot);
        doReturn(ticket).when(ticketDAO).getTicket(anyString());
        doReturn(false).when(ticketDAO).updateTicket(any(Ticket.class));
        System.setOut(new PrintStream(outContent));

        // WHEN vehicle exiting
        parkingService.processExitingVehicle();

        // THEN check that the error message is as expected
        String expectedOutput = "Unable to update ticket information. Error occurred";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    public void getNextParkingNumberIfAvailable()  {
        // GIVEN a car want an available place in car's park
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(0);
        doReturn(1).when(inputReaderUtil).readSelection();
        doReturn(1).when(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));

        // WHEN finding next available parking place
        ParkingSpot resultParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // THEN find an available spot with ID 1
        assertEquals(1, resultParkingSpot.getId());
        assertEquals(ParkingType.CAR, resultParkingSpot.getParkingType());
        assertTrue(resultParkingSpot.isAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableParkingNumberNotFound() {
        // GIVEN a vehicle type selection for CAR
        doReturn(1).when(inputReaderUtil).readSelection();

        // AND no available spot
        doReturn(0).when(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));  // Simulate no available spots

        // WHEN try to find the next available parking place
        ParkingSpot resultParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // THEN result should be null
        assertNull(resultParkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // GIVEN an invalid vehicle type selection
        doReturn(3).when(inputReaderUtil).readSelection();

        // WHEN finding the next available parking place
        ParkingSpot resultParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // THEN the result should be null
        assertNull(resultParkingSpot);
    }
}