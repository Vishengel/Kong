
public interface constants {

	//GUI on or off
	boolean GUI_ON = true;
	
	//player or AI mario
	boolean AI_MARIO = false;
	//game logic constants
	int MAX_EPOCHS = 1000000;
	
	//screen constants
	int SCREEN_X = 580;
	int SCREEN_Y = 680;
	
	//player constants
	int PLAYER_START_X = 125;
	int PLAYER_START_Y = 600;

	int PLAYER_HEIGHT = 32;
	int PLAYER_WIDTH = 20;

	//platform constants
	int PLATFORM_HEIGHT = 17;
	int PLATFORM_WIDTH = 40;
	

	//ladder constants
	int LADDER_WIDTH = 20;
	int LADDER_HEIGHT = 10;
	
	//peach constants
	int PEACH_START_X = SCREEN_X / 2 - PLATFORM_WIDTH;
	int PEACH_START_Y = 160;

	int PEACH_HEIGHT = 35;
	int PEACH_WIDTH = 20;
	
	//barrel constants
	int BARREL_START_X = 120;
	int BARREL_START_Y = 250;
	
	int BARREL_HEIGHT = 20;
	int BARREL_WIDTH = 22;
	
	//oil constants
	int OIL_START_X = 60;
	int OIL_START_Y = 592;
	
	int OIL_HEIGHT = 40;
	int OIL_WIDTH = 30;
	
	//flame constants
	int FLAME_START_X = 60;
	int FLAME_START_Y = 600;
	
	int FLAME_HEIGHT = 28;
	int FLAME_WIDTH = 25;
	
	//powerup constants
	int POWERUP_HEIGHT = 27;
	int POWERUP_WIDTH = 23;

}
