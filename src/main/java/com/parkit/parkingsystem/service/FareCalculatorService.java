package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
//	private TicketDAO ticketDAO = new TicketDAO();
	
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMinutes = ticket.getInTime().getTime();
        long outMinutes = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationMinutes =  (outMinutes - inMinutes)/1000/60;
//        String vehicleRegNumber = ticket.getVehicleRegNumber();
        
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
		
//		TicketDAO ticketDAO = new TicketDAO();
//		Ticket oldTicket = new Ticket();
//         oldTicket = ticketDAO.getTicket(vehicleRegNumber);
//        oldTicket.getOutTime()
//
//		if (oldTicket==null ) {
//			System.out.println("try");
//			ticket.setPrice(0.95 * ticket.getPrice());
//		}

    }
    
}