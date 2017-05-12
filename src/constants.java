//this file contains many constant values that determine either positions, dimensions and states of the game
//and game objects

public interface constants {

	//This boolean determines whether the visual representation of the game should be on or off
	//Turning this value off will cause the game model to speed up significantly
	boolean GUI_ON = true;

	//These values determine whether the datasets about dodging or climbing should be created, respectively.
	//boolean DEMO_PHASE_DODGING = false; 
	//boolean DEMO_PHASE_CLIMBING = false;  
	
	boolean DEMO_PHASE = false;
	
	boolean RANDOM_ACTOR = false;
		
	//These values determine whether mario should learn and test dodging or climbing, respectively.
	//boolean TEST_PHASE_DODGING = false; 
	//boolean TEST_PHASE_CLIMBING = false;
	
	boolean TEST_PHASE = true; 
	
	//This value determines whether the game should create the standard level, or the alternative level 
	//created for training to jump over barrels.
	boolean BARREL_TRAINING = false;
	
	//This value determines whether Mario can die in the game when touching a barrel
	boolean DEATH = true;

	//This value determines for how many epochs the game should run
	int MAX_EPOCHS = 200000000; 

	
	//The following values determine positions and dimensions for game objects
	
	//Width and Height of the game frame
	int SCREEN_X = 580;
	int SCREEN_Y = 680;
	
	//player position and dimensions
	int PLAYER_START_X = 220;
	int PLAYER_START_Y = 600;

	int PLAYER_HEIGHT = 32;
	int PLAYER_WIDTH = 20;

	//platform dimensions
	int PLATFORM_HEIGHT = 17;
	int PLATFORM_WIDTH = 40;
	
	//ladder dimensions
	int LADDER_WIDTH = 20;
	int LADDER_HEIGHT = 10;
	
	//Position and dimension of princess Peach
	int PEACH_START_X = SCREEN_X / 2 - PLATFORM_WIDTH + 100;
	int PEACH_START_Y = 155;
	int PEACH_HEIGHT = 35;
	int PEACH_WIDTH = 20;
	
	//barrel position and dimensions
	int BARREL_START_X = 120;
	int BARREL_START_Y = 250;	
	int BARREL_HEIGHT = 13;
	int BARREL_WIDTH = 13;
	
	//powerup dimensions
	int POWERUP_HEIGHT = 27;
	int POWERUP_WIDTH = 23;

}
