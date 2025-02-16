package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @Mock
    private static FareCalculatorService fareCalculatorService;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        assertNotNull(ticketDAO.getTicket("ABCDEF"));
        assertEquals(2,parkingSpotDAO.getNextAvailableSlot(ticketDAO.getTicket("ABCDEF").getParkingSpot().getParkingType()));
    }

    @Test
    public void testParkingLotExit(){
//    	testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime());
        assertNotNull(ticketDAO.getTicket("ABCDEF").getPrice());
        
    }

	@Test
	public void testRecurrentUser() {
		testParkingLotExit();
        dataBasePrepareService.clearDataBaseEntries();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		Ticket ticket = new Ticket();

		// first time ABCDEF enters the parking
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");		
		ticket.setInTime(new Date(System.currentTimeMillis() - (120 * 60 * 1000)));
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);
		parkingService.processExitingVehicle();

		// second time ABCDEF enters the parking
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);
		parkingService.processExitingVehicle();
		ticket = ticketDAO.getTicket("ABCDEF");

        assertEquals(0.95*Fare.CAR_RATE_PER_HOUR,ticket.getPrice());
    }    
}
