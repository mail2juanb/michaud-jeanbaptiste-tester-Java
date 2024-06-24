package com.parkit.parkingsystem;

import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;

public class TicketTest {

    @Test
    @DisplayName("the getId() method works correctly. the ID value is correctly defined and retrieved.")
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

}