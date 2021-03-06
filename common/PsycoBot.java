package common;

import java.util.logging.Level;
import java.util.logging.Logger;
import client.GameEngine;

public class PsycoBot extends ComputerPlayer {
	public static Logger logger = Logger.getLogger(CLIENT_LOGGER_NAME);
	
	public PsycoBot(byte playerID, String name, GameEngine engine)
	{
		super(playerID, name, engine);
	}
	
	public boolean animate(float dTime, float currentTime)
	{
		float dx = RANDOM.nextFloat()*2-1, dy = RANDOM.nextFloat()*2-1;
		accelerate(PLAYER_ACCELERATION*dx*dTime, PLAYER_ACCELERATION*dy*dTime);
		
		return super.animate(dTime, currentTime);
	}
}
