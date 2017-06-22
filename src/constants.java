//this file contains many constant values that determine either positions, dimensions and states of the game
//and game objects

public interface constants {

	//This boolean determines whether the visual representation of the game should be on or off
	//Turning this value off will cause the game model to speed up significantly
	boolean GUI_ON = true;
	
	boolean DEMO_PHASE = false;  
	boolean TEST_PHASE = true;  
	boolean CRITIC_ON = false; 
	
	int GAME_SPEED = 0;
	
	boolean RANDOM_ACTOR = false;
	
	String DATA_FILEPATH = "TrainingData/FinalData/FullDataSetFinal";
	String SCORE_FILE_NAME = "LearningRateScores";
	String PERFORMANCE_FILE_NAME = "LearningRatePerformance";

	int RUNS_PER_PARAMETER = 10;
	int GAMES_PER_RUN = 100;
	
	//This value determines whether the game should create the standard level, or the alternative level 
	//created for training to jump over barrels.
	boolean BARREL_TRAINING = false;
	
	//This value determines whether Mario can die in the game when touching a barrel
	boolean DEATH = true;

	//This value determines for how many epochs the game should run
	int MAX_EPOCHS = 1000000; 

	
	int ACTOR_HIDDEN_NODES = 100;  
	int CRITIC_HIDDEN_NODES = 50;
	
	//These values determine the amount of hidden layers of both the actor and critic network 
	int N_HIDDEN_LAYERS_ACTOR = 1;     
	int N_HIDDEN_LAYERS_CRITIC = 2;  
	
	boolean TEST_CRITIC = false;
	
	double ACTOR_CRITIC_LEARNING_RATE = 0.0001; 
	int LEARNING_RATE_REDUCTION_EPOCHS = 50000;
			
			
			
	//The following values determine positions and dimensions for game objects
	
	//Width and Height of the game frame
	int SCREEN_X = 580;
	int SCREEN_Y = 680;
	
	//player position and dimensions
	int PLAYER_START_X = 50;
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
	int BARREL_HEIGHT = 15;
	int BARREL_WIDTH = 15;
	
	//powerup dimensions
	int POWERUP_HEIGHT = 27;
	int POWERUP_WIDTH = 23;

}
