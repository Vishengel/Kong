//this file contains many constant values that determine either positions, dimensions and states of the game
//and game objects

public interface constants {

	//This boolean determines whether the visual representation of the game should be on or off
	//Turning this value off will cause the game model to speed up significantly
	boolean GUI_ON = true;
	
	//Demo phase: Human player plays N games, these get stored in the training set.
	boolean DEMO_PHASE = false; 
	//Set this value to true when the critic output needs to be seen during the demo phase
	boolean TEST_CRITIC = false; 
	//Test phase: The AI actor plays the game.
	boolean TEST_PHASE = true;  
	//If true, the Critic is trained/loaded and gives the actor feedback
	boolean CRITIC_ON = true;   

	//Determine the game speed. Default for demo phase is 15, default for test phase is 0. 
	int GAME_SPEED = 0; 
	
	//If true, the actor will not be trained.
	boolean RANDOM_ACTOR = true;
	
	//Network restoring parameters
	boolean LOAD_ACTOR = false; 
	boolean LOAD_CRITIC = true;
	boolean LOAD_TRAINED_ACTOR = false;

	//Network saving parameters 
	boolean SAVE_ACTOR = false;
	boolean SAVE_CRITIC = false;
	boolean SAVE_TRAINED_ACTOR = true;
		
	//Draw/don't draw on screen
	boolean SHOW_VISION_GRID = false;
	boolean SHOW_MARIO_TRACKER = false;

	//This value determines whether the game should create the standard level, or the alternative level 
	//created for training to jump over barrels.
	boolean BARREL_TRAINING = false;
	
	//This value determines whether Mario can die in the game when touching a barrel
	boolean DEATH = true;

	//This value determines for how many epochs the game should run
	int MAX_EPOCHS = 1000000; 

	int MAX_GAMES = 10000; 
	int temperatureCooling = 1000; 
	
	int ACTOR_HIDDEN_NODES = 60;  
	int CRITIC_HIDDEN_NODES = 200; 
	
	//These values determine the amount of hidden layers of both the actor and critic network 
	int N_HIDDEN_LAYERS_ACTOR = 2;       
	int N_HIDDEN_LAYERS_CRITIC = 3;
	
	
	double CRITIC_LEARNING_RATE = 0.0004; 
	//double CRITIC_LEARNING_RATE = 0;
	double CRITIC_LEARNING_REDUCTION = 20000000;
	
	double ACTOR_CRITIC_LEARNING_RATE = 0.0001;
	//double ACTOR_CRITIC_LEARNING_RATE = 0; 
	double ACTOR_LEARNING_REDUCTION = 5000000;
	//int LEARNING_RATE_REDUCTION_GAMES = 25;  
			
    //Determine size of Experience Replay Memory
	int MEMORY_SIZE = 5000;  
	double PRIORITY_THRESHOLD = 0; 
	int UPDATE_INTERVAL = 100; 
	int UPDATES = 20; 
	
	
	
	String AC_ALGORITHM = "ACLA";
			
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
