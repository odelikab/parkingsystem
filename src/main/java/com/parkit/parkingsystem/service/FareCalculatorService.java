package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
	
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMinutes = ticket.getInTime().getTime();
        long outMinutes = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationMinutes =  (outMinutes - inMinutes)/1000/60;
        
        if (durationMinutes <= 30) {
			ticket.setPrice(0);
			return;
        }
        
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
				ticket.setPrice(durationMinutes * Fare.CAR_RATE_PER_HOUR / 60);
			break;
		}
		case BIKE: {
				ticket.setPrice(durationMinutes * Fare.BIKE_RATE_PER_HOUR / 60);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
		
    }
    
}