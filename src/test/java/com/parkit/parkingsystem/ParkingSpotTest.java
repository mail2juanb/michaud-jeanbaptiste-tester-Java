package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;

public class ParkingSpotTest {

    @Test
    @DisplayName("The Id value is correctly defined and retrieved.")
    public void getIdParkingSpotTest() {
        //GIVEN an Id of ParkingSpot
        int expectedIdParkingSpot = 1;

        //WHEN define ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot(expectedIdParkingSpot, ParkingType.CAR, true);

        //THEN retrieve the correct Id
        assertEquals(expectedIdParkingSpot, parkingSpot.getId());
    }

    @Test
    @DisplayName("The ParkingType is correctly defined and retrieved.")
    public void getParkingTypeParkingSpotTest() {
        //GIVEN a ParkingType CAR
        ParkingType expectedParkingType = ParkingType.CAR;

        //WHEN define ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot(1, expectedParkingType, true);

        //THEN retrieve the correct ParkingType
        assertEquals(expectedParkingType, parkingSpot.getParkingType());
    }

}
