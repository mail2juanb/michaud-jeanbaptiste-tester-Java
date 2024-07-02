package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ParkingSpotTest {

    @Test
    @DisplayName("The Id value is correctly retrieved.")
    public void getIdParkingSpotTest() {
        //GIVEN an Id of ParkingSpot
        int expectedIdParkingSpot = 1;

        //WHEN define ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot(expectedIdParkingSpot, ParkingType.CAR, true);

        //THEN retrieve the correct Id
        assertEquals(expectedIdParkingSpot, parkingSpot.getId());
    }

    @Test
    @DisplayName("The Id value is correctly defined.")
    public void setIdTicketTest() {
        //GIVEN a ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        //WHEN set a new Id
        parkingSpot.setId(2);

        //THEN
        assertEquals(2, parkingSpot.getId());
    }

    @ParameterizedTest
    @EnumSource(ParkingType.class)
    @DisplayName("The ParkingType is correctly retrieved.")
    public void getParkingTypeParkingSpotTest(ParkingType expectedParkingType) {
        //GIVEN a ParkingType CAR or BIKE - See Enum

        //WHEN define ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot(1, expectedParkingType, true);

        //THEN retrieve the correct ParkingType
        assertEquals(expectedParkingType, parkingSpot.getParkingType());
    }

    @Test
    @DisplayName("The ParkingType is correctly defined.")
    public void setParkingTypeParkingSpotTest() {
        //GIVEN a ParkingType CAR
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        //WHEN set a new ParkingType
        parkingSpot.setParkingType(ParkingType.BIKE);

        //THEN retrieve the correct ParkingType
        assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }

    @Test
    @DisplayName("The parkingSpot is available and return true.")
    public void isAvailableParkingSpotTest() {
        //GIVEN a ParkingSpot with Id=1 available
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        //WHEN check ParkingSpot Id=1 status
        boolean expectedValue = parkingSpot.isAvailable();

        //THEN retrieve the correct status for ParkingSpot Id=1
        assertTrue(expectedValue);
    }

    @Test
    @DisplayName("The parkingSpot is NOT available and return false.")
    public void isNotAvailableParkingSpotTest() {
        //GIVEN a ParkingSpot with Id=1 not available
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        //WHEN check ParkingSpot Id=1 status
        boolean expectedValue = parkingSpot.isAvailable();

        //THEN retrieve the correct status for ParkingSpot Id=1
        assertFalse(expectedValue);
    }

    @Test
    @DisplayName("Check if objects are equals with same object")
    public void equalsSameObjectTest() {
        //GIVEN an object ParkingSpot
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);

        //THEN retrieve the same ParkingSpot
        assertEquals(spotA, spotA);
    }

    @Test
    @DisplayName("Check if objects are equals with different object with same Id")
    public void equalsDifferentObjectsWithSameIdTest() {
        //GIVEN an object ParkingSpot A
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);

        //AND an object ParkingSpot B
        ParkingSpot spotB = new ParkingSpot(1, ParkingType.BIKE, true);

        //THEN retrieve equal objects ParkingSpot
        assertEquals(spotA, spotB);
    }

    @Test
    @DisplayName("Check if objects are NOT equals with different object with different Id")
    public void notEqualsDifferentObjectsWithDifferentIdTest() {
        //GIVEN an object ParkingSpot A
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);

        //AND an object ParkingSpot B
        ParkingSpot spotB = new ParkingSpot(2, ParkingType.CAR, true);

        //THEN retrieve not equals objects ParkingSpot
        assertFalse(spotA.equals(spotB));
    }

    @Test
    @DisplayName("Check if objects are NOT equals with different object with different Id")
    public void NotEqualsWithNullTest() {
        //GIVEN an object ParkingSpot
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);

        //THEN retrieve not equals objects
        assertFalse(spotA.equals(null));
    }

    @Test
    @DisplayName("Check if objects are equals with same object with same Id")
    public void hashCodeConsistencyWithSameObjectWithSameIdTest() {
        //GIVEN 2 ParkingSpot with same Ids
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spotB = new ParkingSpot(1, ParkingType.CAR, true);

        //THEN return not equals objects
        assertEquals(spotA.hashCode(), spotB.hashCode());
    }

    @Test
    @DisplayName("Check if objects are NOT equals with same object with different Id")
    public void hashCodeConsistencyWithSameObjectWithDifferentIdTest() {
        //GIVEN 2 ParkingSpot with same Ids
        ParkingSpot spotA = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot spotB = new ParkingSpot(2, ParkingType.CAR, true);

        //THEN return not equals objects
        assertNotEquals(spotA.hashCode(), spotB.hashCode());
    }

    @Test
    @DisplayName("Check that same object are equals")
    public void equalsSameObjectsTest() {
        //GIVEN an object ParkingSpot
        ParkingSpot spot1 = new ParkingSpot(1, ParkingType.CAR, true);

        //THEN both objects are same
        assertTrue(spot1.equals(spot1));
    }

}
