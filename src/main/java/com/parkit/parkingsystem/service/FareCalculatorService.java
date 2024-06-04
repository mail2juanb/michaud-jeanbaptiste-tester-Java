package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }

    public void calculateFare(Ticket ticket, boolean discount){
        if(outTimeIncoherence(ticket)) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double price = calculatePrice(ticket, discount);

        ticket.setPrice(price);
    }

    private static boolean outTimeIncoherence(Ticket ticket) {
        return (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()));
    }

    /**
     * Calculate parking fare depending on duration, type of vehicle and discount.
     * @param ticket parking ticket including in/out time, vehicle RegNumber and parking spot
     * @param discount 5% discount for regular users
     * @return parking fare price in double
     */
    private static double calculatePrice(Ticket ticket, boolean discount) {
        double duration = parkingTime(ticket);
        double price = 0;

        if(isEligibleToFree(duration)) {
            return price;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                price = duration * Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                price = duration * Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }

        if(discount) {
            price = price - (0.05 * price);
        }

        return price;
    }

    private static double parkingTime(Ticket ticket) {
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        return (outHour - inHour) / (1000.*3600);
    }

    private static boolean isEligibleToFree(double duration) {
        return duration < 0.5;
    }
}