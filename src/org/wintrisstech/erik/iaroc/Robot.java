package org.wintrisstech.erik.iaroc;

import ioio.lib.api.exception.ConnectionLostException;

/**************************************************************************
 * A class to abstract an  higher level API to control the robot
 **************************************************************************/
public class Robot {
	
	private Lada lada;
	private final Dashboard dashboard;
	private int TURN_SPEED = 25;


	public Robot(Dashboard dashboard, Lada lada)
	{
		this.dashboard = dashboard;
		this.lada = lada;
	}
	
	public void log(String message)
	{
		dashboard.log(message);
	}
	
	public void speak(String message)
	{
		dashboard.speak(message);
	}
	
	public void goForward(int centimeters) throws ConnectionLostException
	{
		int totalDistance = 0;
		lada.readSensors(Lada.SENSORS_GROUP_ID6);
		lada.driveDirect(250, 250);
		while (totalDistance < centimeters * 10)
		{
			lada.readSensors(Lada.SENSORS_GROUP_ID6);
			int dd = lada.getDistance();
			totalDistance += dd;
			log("" + totalDistance / 10 + " cm");
		}
		stop();
	}
	
	public void stop() throws ConnectionLostException
	{
		lada.driveDirect(0, 0);
	}
	
	public void turnToHeading(int desiredHeading) throws ConnectionLostException
	{
		int currentHeading = readCompass();
		int delta = currentHeading - desiredHeading;
		log("Current Heading:" + currentHeading);
		if  (delta <= 3 )
		{
			stop();
			TURN_SPEED = 1;
		}
		else
		{
			if (delta > 0 && delta <= 180 || delta < 0 && delta >= 180)
			{
				rotateLeft();
			}
			else
			{
				rotateRight();
			}
		}
	}
	
	public int readCompass() {
		return (int) (dashboard.getAzimuth() + 360) % 360;
	}
	
	public void rotateRight() throws ConnectionLostException
	{
		lada.driveDirect(-TURN_SPEED,TURN_SPEED); 
	}
	
	public void rotateLeft() throws ConnectionLostException
	{
		lada.driveDirect(TURN_SPEED,-TURN_SPEED); 
	}
}
