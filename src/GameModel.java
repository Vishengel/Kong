
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class GameModel implements constants {
	
	String filename = "FullDataSetFinal"; 
	ArrayList<Double> performancePerGame = new ArrayList<Double>();
	
	//performance variables
	double gamesWon = 0;
	double gamesPlayed = 0;
	double performance = 0; 
	
	private double score = 0;
	private int lives = 3;
	
	//these values determine how fast barrels are spawned in the game
	private int spawnTimer = 250; 	
	private int barrelSpawnTime = 250;     
	
	//These values relate to powerups and destroying barrels
	private int smashedBarrelIndex = -1;
	private double powerupTimer = 0;	
	//This value determines how long the powerup lasts when Mario picks it up
	private double powerupDuration = 300;
	private int powerupIndex = -1;
	
	//This value determines for how many epochs the game has been running already
	private int epochs = 0;
	double temperature =  4;    
	private double minTemp = 4; 
	//This value determines how long the game model should sleep or slow down, in order to make the game playable
	//for a human
	private int sleepTime = constants.GAME_SPEED;         
	 
	
	double epsilon = 0;
	
	
	private int nOutput = 7;
	
	private ArrayList<Platform> platformList;
	private ArrayList<Ladder> ladderList;
	private ArrayList<MovingObject> MOList;
	private ArrayList<Powerup> PUList;
	
	private ArrayList<double[]> trainingSet = new ArrayList<double[]>();
	private ArrayList<ArrayList<MovingObject>> MOCollection = new ArrayList<ArrayList<MovingObject>>();
	private ArrayList<ArrayList<Powerup>> PUCollection = new ArrayList<ArrayList<Powerup>>();
	

	MLPJelle actor;	
	Critic critic;
	
	//Initialize the Replay Memory
	ReplayMemory memory = new ReplayMemory();
	//double[] state;
	//double[] previousState;
	
	private Player mario;
	private Peach peach;

	private boolean powerupActivated = false;
	
	private VisionGrid visionGrid = new VisionGrid(constants.PLAYER_START_X, constants.PLAYER_START_Y, 180, 230, 7);
	private VisionGrid marioTracker = new VisionGrid(0, 0,constants.SCREEN_Y, constants.SCREEN_X, 20 );
	
	//N x N vision grid inputs 
	private int visionGridInputs = visionGrid.getSize() * visionGrid.getSize() * 3;
	//N x N mario tracker inputs 
	private int marioTrackInputs = marioTracker.getSize() * marioTracker.getSize();
	//4 additional inputs + bias + reward
	private int otherInputs = 6;
	
			
	//These values determine the rewards 
	private boolean gameWon = false;
	private boolean hitByBarrel = false;
	private boolean touchedPowerUp = false;
	private boolean jumpedOverBarrel = false;
	private boolean destroyedBarrel = false;
	private boolean barrelLeft = true;
	
	private boolean saveStateBeforeJump = false;
	private boolean justLanded = false; 
	private int epochBeforeJump = 0;
	
	double feedback = 0;

	
	//allow for alternating left and right barrel spawing during training
	private boolean leftSpawned = true;
	
	//The file handler is used to write to and read from the text files
	FileHandler fh = new FileHandler();
		
	public GameModel(){
		initGame();
	}
	
	//Create a new barrel object 
	public void spawnBarrel(boolean falling){
		MOList.add(new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, 1));
	}
	
	//these two functions are used for alternative spawning of barrels, to be used during dodge training
	//in the alternative dodge level
	public void spawnLeftBarrel(){
		MOList.add(new Barrel(0,constants.SCREEN_Y-220,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, 1));
	}
	
	public void spawnRightBarrel(){
		MOList.add(new Barrel(constants.SCREEN_X, constants.SCREEN_Y-220,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, 0));
	}
	
	
	public void incrementTime(MovingObject MO){
		//reset gravity when standing or climbing
		if((MO.standing )|| MO.isClimbing){
			MO.setTime(0);
			if(MO instanceof Player){
				((Player) MO).setJump(false);
			}		
		}	
		else{
			MO.setTime(MO.getTime() + 1);
		}		
			
	}
	
	public double euclideanDistance(GameObject o1, GameObject o2){
		return Math.sqrt( ((o1.getXPos() - o2.getXPos()) * (o1.getXPos() - o2.getXPos()))
				+ ((o1.getYPos() - o2.getYPos()) * (o1.getYPos() - o2.getYPos())) );
	}

	public double[] calculateState(double reward){
		double[] state = new double[visionGridInputs + marioTrackInputs + otherInputs + nOutput];
		//Check whether or not barrels / ladders are in the grid's blocks
		visionGrid.checkDetections(MOList,ladderList, PUList, mario, peach);
		marioTracker.checkMarioDetections(mario);
		
		//fill the state array with the detections inputs of the vision grid
		for(int i = 0; i < visionGridInputs / 3; i++){
			state[i] = visionGrid.getBarrelInputs()[i];
		}
		for(int i = 0; i < visionGridInputs / 3; i++){
			state[i + visionGridInputs / 3] = visionGrid.getLadderInputs()[i];
		}
		for(int i = 0; i < visionGridInputs / 3; i++){
			state[i + visionGridInputs / 3 + visionGridInputs / 3] = visionGrid.getPowerupInputs()[i];
		}
		for(int i = 0; i < marioTrackInputs; i++){
			state[i + visionGridInputs] = marioTracker.getMarioInputs()[i];
		}
		
		state[visionGridInputs + marioTrackInputs] = mario.isClimbing() ? 1 : 0;
		state[visionGridInputs + marioTrackInputs + 1] = mario.canClimb ? 1 : 0;
		state[visionGridInputs + marioTrackInputs + 2] = powerupActivated ? 1 : 0;
		//Give mario the time passed since he picked up a powerup
		state[visionGridInputs + marioTrackInputs + 3] = powerupTimer/powerupDuration;
		state[visionGridInputs + marioTrackInputs + 4] = 0.0;
		//Add the reward received in this state
		state[visionGridInputs + marioTrackInputs + 5] = reward;
		
		System.out.println("Total state size should be: " + (visionGridInputs + marioTrackInputs + otherInputs+ nOutput));
		return state;
		
	}

	//calculate rewards and reset the appropriate values
	public double calculateReward(int action, int previousAction){
		double reward = 0;

		if(gameWon){
			reward += 200;
			score += 200; 
		}
		
		if(hitByBarrel){
			System.out.println("Hit by barrel!");
			reward -= 10;
			score -= 10; 	
		}
		
		/*else if(jumpedOverBarrel){
			System.out.println("Jumped over a barrel!");
			reward += 0.5; 
			score += 0.5;
		}*/
		else if(destroyedBarrel){
			System.out.println("Smashed a barrel!");
			reward += 2;
			score += 2;
		}
		
		/*if(touchedPowerUp){
			System.out.println("Picked up powerup!");
			reward += 1;
			score += 1;
		}*/
		
		
		if(jumpedOverBarrel && (saveStateBeforeJump||justLanded)){
			reward += 3;
		}
		if(touchedPowerUp && (saveStateBeforeJump||justLanded)){
			reward += 1; 
		}
		//penalize useless jumping
		else if(saveStateBeforeJump || justLanded){
			reward -= 20; 
		}	
		/*
		else if(mario.getAction() == 5 && barrelLeft){
			reward -= 30;
		}
		else if(mario.getAction() == 6 && !barrelLeft){
			reward -= 30;
		}*/
		/*if(mario.isClimbing() && action == 3){
			reward += 2;
		}*/ 
		/*if(mario.canClimb && action == 3){
			reward += 2;
		}*/
		/*  
		//penalize useless climbing
		if((action == 3 || action == 4) && (!mario.isClimbing() || !mario.canClimb)){
			reward -= 20;
		}
		
		//penalize useless  actions on ladders
		if(mario.isClimbing() && (action != 3 && action != 4)){
			reward -= 20;
		}
		*/
		//Penalize mario if he touches the edge of the screen
		/*if(mario.getXPos() <= 0 || mario.getXPos() >= constants.SCREEN_X - 30){
			reward = -100;
		}*/ 
		/*if(mario.getAction() == 5){
			reward -= 1000;
		}*/
		
		System.out.println("Calculated reward: " + reward);
		return reward;
	}
	
	//Prints all the vital information pertaining to the current state
	public void printState(double reward, int previousAction){
		System.out.println("--------------Current epoch: " + epochs + "----------------------");
		System.out.println("Temperature: " + temperature);
		System.out.println("Current action: " + mario.getAction());
		System.out.println("Previous action: " + previousAction); 
		System.out.println("Previous reward: " + reward);
	}
	
	public void reduceTemperature(){
		temperature = (temperature > minTemp? temperature - 1: minTemp);	
	}
	
	//Make every moving object act and adjust booleans
	public void moveAll(){
		
		//Spawn a barrel, determine by the spawn timer
		if(spawnTimer == barrelSpawnTime){
			spawnTimer = 0;
			if(constants.BARREL_TRAINING){
				/*if(!leftSpawned){
					spawnLeftBarrel();
					leftSpawned = true;
				}*/
				//else{
					spawnRightBarrel();
					leftSpawned = false;
				//}
			}
			else{
				spawnBarrel(true);
			}
			
		}	
	
		spawnTimer++;

		if(powerupActivated) {
			if (powerupIndex >=0) {
				PUList.remove(powerupIndex);
				powerupIndex = -1;
			}
			
			if(powerupTimer == powerupDuration){
				powerupTimer = 0;
				powerupActivated = false;
				mario.setPoweredUp(false);
				System.out.println("Powerup deactivated");
			}
			else{
				powerupTimer++;
			}
		}
		
		
		for(int i = 0; i < MOList.size(); i++){
			//Increment the air time of moving objects, to properly apply gravity 
			incrementTime(MOList.get(i));
			//check collisions and update moving object states
			MovingObject MO = checkCollisions(MOList.get(i));
			MOList.set(i, MO); 
			//make all moving objects act/move
			MOList.get(i).act();
			
			//if mario is hit, subtract a life
			if(MOList.get(0).isKilled){	
				gamesPlayed++;
				hitByBarrel = true;
				lives--;
				resetGame();
			}  
			else if(gameWon){
					//if mario saved the princess, reset game
					System.out.println("Princess saved!");	
					gamesPlayed++;
					gamesWon++;
					//gameWon = false; 
					resetGame();					
			}					
			//If object falls or rolls out of the game screen, delete it
			else if(MOList.get(i).getXPos() < 0){
				MOList.remove(i);						
			}				
	       //If a barrel has been smashed, the value of smashedBarrelIndex >= 0
		   //We remove the barrel using this index
			else if (smashedBarrelIndex >= 0) {
				MOList.remove(smashedBarrelIndex);
				smashedBarrelIndex = -1;
			}

			//If mario jumps over a barrel, increment score 
			else if(MOList.get(i).getYPos() >= mario.getYPos()  && MOList.get(i).getYPos() <= mario.getYPos() + 100 && 
				mario.getXPos() >= MOList.get(i).getXPos()	&&
				mario.getXPos() <= MOList.get(i).getXPos()+MOList.get(i).getWidth() &&
				!(MOList.get(i).pointAwarded) && mario.isJumping()){
				jumpedOverBarrel = true;
				MOList.get(i).setPointAwarded();
				if(MOList.get(i).getAction() == 0){
					barrelLeft = true;
				}
				else{
					barrelLeft = false;
				}
				System.out.println("JUMPED OVER A BARREL!");
			}
		}
	}
	
	//main game function
	public void runGame() throws InterruptedException, IOException{
	
		//Initialize the Replay Memory
		//ReplayMemory memory = new ReplayMemory();
		
		//This array contains all the game inputs + the bias value
		double[] currentState;
		//After each epoch, assign the current state to the previous state
		double[] previousState;
		
		//Variables for transitions and the Experience Replay
		double[] tempCurrentState;
		double[] tempPreviousState;
		int tempPreviousAction;
		double tempReward;
		double tempFeedback;
		boolean tempGameLost = false;
		boolean tempGameWon = false;
		
		//After the game state contains everything from the current state plus the obtained reward plus the 
		//targets for the actions. This information is written to a trainingSet.
		double[] gameState;
		
		gameState = new double[visionGridInputs + marioTrackInputs + otherInputs + nOutput];
		currentState = new double[visionGridInputs + marioTrackInputs + otherInputs-1];
		previousState = new double[visionGridInputs + marioTrackInputs + otherInputs-1];
		
		tempCurrentState = new double[visionGridInputs + marioTrackInputs + otherInputs-1];
		tempPreviousState = new double[visionGridInputs + marioTrackInputs + otherInputs-1];
		
		visionGrid.moveGrid(mario.getXPos(), mario.getYPos());
		

		//don't create the actor and critic if in the demonstration phase
		if(constants.TEST_PHASE){
			actor = new MLPJelle(visionGridInputs + marioTrackInputs + otherInputs, constants.N_HIDDEN_LAYERS_ACTOR, constants.ACTOR_HIDDEN_NODES/*(visionGridInputs + marioTrackInputs + otherInputs-1 + nOutput)/2*/, nOutput, filename, constants.LOAD_ACTOR || constants.LOAD_TRAINED_ACTOR); 
			//critic = new Critic(visionGridInputs + marioTrackInputs + otherInputs, constants.N_HIDDEN_LAYERS_CRITIC, 40/*(visionGridInputs + marioTrackInputs + otherInputs) / 2*/, 1, "FinalSet2New"); 		
		}
		if(constants.TEST_PHASE && !constants.RANDOM_ACTOR && !constants.LOAD_ACTOR && !constants.LOAD_TRAINED_ACTOR){ 
			actor.trainNetwork(); 
		}
		if(constants.CRITIC_ON){
			critic = new Critic(visionGridInputs + marioTrackInputs + otherInputs, constants.N_HIDDEN_LAYERS_CRITIC, constants.CRITIC_HIDDEN_NODES/*(visionGridInputs + marioTrackInputs + otherInputs) / 2*/, 1, filename, constants.LOAD_CRITIC); 
			if(!constants.LOAD_CRITIC && !constants.RANDOM_CRITIC){
				critic.trainNetwork();
			}
			critic.setLearningRate(constants.CRITIC_LEARNING_RATE);
			//critic.setLearningRate(0.0001); 
			
		}
		
		double reward = 0;
		//double feedback = 0;
		int action = 0;
		int previousAction = 0;
		int previousEpoch = 0;
		
		boolean condition = true;
		
		if(constants.TEST_PHASE){
			//critic.setLearningRate(constants.ACTOR_CRITIC_LEARNING_RATE); 
			actor.setLearningRate(constants.ACTOR_CRITIC_LEARNING_RATE); 
		}
		
		
		//main game loop 
		while(condition){
			/*if(epochs % 2 == 0){*/
				//critic.forwardPass(currentState, false);
				//System.out.println("STATE VALUE: " + critic.getOutputLayer().get(0).getOutput());
				if(constants.TEST_PHASE){
					System.out.println("Current learning rate: " + actor.getLearningRate());
				}
				//every few games, reduce the learning rate of the actor and critic for smoother convergence
				//if(gamesPlayed % constants.LEARNING_RATE_REDUCTION_GAMES == 0 && gamesPlayed > 0 && constants.TEST_PHASE){ 
					//actor.setLearningRate(actor.getLearningRate() * 0.90);  
					/*if(constants.CRITIC_ON){
						critic.setLearningRate(constants.ACTOR_CRITIC_LEARNING_RATE); 
					}*/	
				//}
				
				
				condition = gamesPlayed < constants.MAX_GAMES;
	
				//System.out.println("Performance: " + gamesWon / gamesLost);
				System.out.println("------------- Current epoch: " + epochs + " -------------");
				
				System.out.println("Temperature: " + temperature);
				
				//reset the vision grid 
				visionGrid.resetDetections();
				marioTracker.resetDetections();
				
				//Add temperature to the actor
				if(constants.TEST_PHASE){
					actor.setTemperature(temperature);
				}
				
			
				
				//this state becomes the previous state in the next iteration, but only if Mario is not jumping.
				if(!saveStateBeforeJump && !justLanded){
					for(int i = 0; i < previousState.length; i++){
						previousState[i] = currentState[i];
					}
					previousEpoch = epochs;
				}
				//Calculate the total state for this epoch: inputs + bias + reward + outputs
				gameState = calculateState(reward);	
				//Assign the inputs to the current state
				for(int i = 0; i < currentState.length; i++){
					currentState[i] = gameState[i];
				}
				//The action taken in this state becomes the previous action
				//Don't overwrite the action when Mario has jumped
				if(!saveStateBeforeJump && !justLanded){
					previousAction = action;
				}
				System.out.println("Previous action: " + previousAction);
					
				//reset mario's action when standing
				if(mario.standing){
					mario.setAction(0); 
				}
				
				//Present state to actor for action selection; don't allow action selection while jumping
				if(constants.TEST_PHASE){ 
					//testInputs = Arrays.copyOf(currentState, visionGridInputs + marioTrackInputs + otherInputs-1);
					action = actor.presentInput(currentState, epsilon);
					if(!mario.isJumping()){
						mario.setAction(action);
					}
				}
				
				//If mario was standing and chooses to jump, save this state until Mario is done jumping.
				if(  ((mario.standing && !mario.isJumping()) || (!mario.standing && !mario.isJumping() && !mario.isClimbing) ) && (action == 5 || action == 6) && !saveStateBeforeJump ){
					saveStateBeforeJump = true;
					justLanded = false;
					epochBeforeJump = epochs;
					System.out.println("NOW STARTING JUMP!");
					for(int i = 0; i < previousState.length; i++){
						previousState[i] = currentState[i];
					}
					previousAction = action;
					previousEpoch = epochs;
				}
				System.out.println("Previous state is temporarily the state before jumping: " + saveStateBeforeJump);
				System.out.println("Epoch right before jumping: " + epochBeforeJump);
				
			
				if(constants.TEST_CRITIC /*&& !Arrays.equals(currentState, previousState)*/){
					feedback = critic.calculateFeedback(currentState, previousState, reward, hitByBarrel, gameWon);
					//System.out.println("TD: " + feedback);
					critic.trainCritic(currentState, previousState, reward, hitByBarrel, gameWon);	
				}
				
				/*if(constants.TEST_PHASE && constants.CRITIC_ON && (!saveStateBeforeJump || justLanded) && !Arrays.equals(currentState, previousState) && epochs > 0){
					feedback = critic.calculateFeedback(currentState, previousState, reward, hitByBarrel, gameWon); 
					//backpropagate the feedback to the actor in the form of a TD-error (Temporal-Difference)
					actor.propagateFeedback(previousState, feedback, previousAction);	
					//train the critic  
					critic.trainCritic(currentState, previousState, reward, hitByBarrel, gameWon);	
				}
				*/
				
				System.out.println("Replay memory size: " + memory.transitions.size());
				if(constants.TEST_PHASE && constants.CRITIC_ON && (!saveStateBeforeJump || justLanded) && !Arrays.equals(currentState, previousState) && epochs > 0){
					justLanded = false; 
					//Add transition to Replay Memory 
					//Prioritized RM: ONLY add transition if the TD error is significant enough
					feedback = critic.calculateFeedback(currentState, previousState, reward, hitByBarrel, gameWon);
					System.out.println("TD of this transition: " + feedback);
					if(Math.abs(feedback) >= constants.PRIORITY_THRESHOLD){
						System.out.println("Transition significant enough; added to RM!");
						Transition transition = new Transition(previousState, currentState, previousAction, reward, feedback, hitByBarrel, gameWon);
						memory.storeTransition(transition);
					}
					else{
						System.out.println("Transition not signifcant! Discarded!");
					}
				}
					
				//Only update actor and critic once every update interval
				if( (memory.transitions.size() > 1000 || memory.transitions.size() == memory.maxsize ) && epochs % constants.UPDATE_INTERVAL == 0){
					//perform N updates
					int n = constants.UPDATES;
					for(int u = 0; u < n; u++){
						//Sample random transition from Replay Memory
						Transition randomTransition = memory.getTransition();
						//Use the elements of the random transition for learning
						for(int i = 0; i < previousState.length; i++){
							tempCurrentState[i] = randomTransition.getNextState()[i];
							tempPreviousState[i] = randomTransition.getPreviousState()[i];			
						}
						tempPreviousAction = randomTransition.getAction();
						tempReward = randomTransition.getReward();
						tempFeedback = randomTransition.getFeedback();
						tempGameLost = randomTransition.getGameLost();
						tempGameWon = randomTransition.getGameWon();
											
						//feedback = critic.calculateFeedback(tempCurrentState, tempPreviousState, tempReward, tempGameLost, tempGameWon); 
						//backpropagate the feedback to the actor in the form of a TD-error (Temporal-Difference)
						actor.propagateFeedback(tempPreviousState, tempFeedback, tempPreviousAction);	
						//train the critic 
						critic.trainCritic(tempCurrentState, tempPreviousState, tempReward, tempGameLost, tempGameWon);	
						
						
					}
				}
				
				//Set the reward booleans to false again, unless Mario is still in a jump
				if(!saveStateBeforeJump){
					hitByBarrel = false;
					gameWon = false;  
					touchedPowerUp = false;
					jumpedOverBarrel = false;
					destroyedBarrel = false;
				}
				
				
				
				moveAll();
				
				if(mario.isClimbing()){
					saveStateBeforeJump = false;
					justLanded = false;				
				}
				System.out.println("Standing: " + mario.standing);
				System.out.println("Jumping: " + mario.isJumping());
				System.out.println("Climbing " + mario.isClimbing());
				
				if(mario.standing && !mario.isJumping() && saveStateBeforeJump && epochs > (epochBeforeJump + 20)){
					saveStateBeforeJump = false;
					justLanded = true;
					System.out.println("STANDING AGAIN!");
				}
				
				//Move vision grid to mario's new position
				visionGrid.moveGrid(mario.getXPos(), mario.getYPos());
				//calculate the reward for the current state 
				reward = calculateReward(action, previousAction); 
				
				System.out.println("Just landed: " + justLanded);
				
				
				//slow game model down, so that game can be played by human
				if(GUI_ON){
					Thread.sleep(sleepTime);
				}	
				//Test
				System.out.println("Current and previous state are equal: " + Arrays.equals(currentState, previousState));
				if(Arrays.equals(currentState, previousState)){
					System.out.println("TRANSITION NOT ADDED, SAME STATE!!!!!");
				}
				
				if(constants.DEMO_PHASE /*(!mario.isJumping() || epochs == epochBeforeJump)*/ ){
					gameState[visionGridInputs + marioTrackInputs + otherInputs + mario.getAction()] = 1.0;
					trainingSet.add(gameState); 
				}
				//increment epoch
				epochs++;	
											
			}
			//Only move objects and nothing else every odd epoch
			/*else{
				hitByBarrel = false;
				gameWon = false;  
				touchedPowerUp = false;
				jumpedOverBarrel = false;
				destroyedBarrel = false;
				moveAll();
			}*/
			
			
			
		//}
		//Write all the data gathered during the demonstration phase to a txt file for training
		if(constants.DEMO_PHASE){
			fh.writeToFile(trainingSet, filename);
			System.out.println("Written to file!");
		}
		for(int i = 0; i < constants.MAX_GAMES; i++){
		System.out.println("Game " + i + ": " + performancePerGame.get(i));
		}
		fh.writePerformanceToFile(performancePerGame); 
		//Print final performance
		System.out.println("Final performance: " + performance);
		//Store the network(s)
		if(constants.SAVE_ACTOR){
			fh.storeNetwork(actor, actor.getHiddenLayers(),actor.getHiddenLayers().size());
		}
		if(constants.SAVE_CRITIC){
			fh.storeNetwork(critic, critic.getHiddenLayers(),critic.getHiddenLayers().size());
		}
		if(constants.SAVE_TRAINED_ACTOR){
			fh.storeNetwork(actor, actor.getHiddenLayers(),actor.getHiddenLayers().size());
		}
		//Quit the program 
		System.out.println("Quitting the simulation.");
		System.exit(0);

	}	
	
	//This function is called at the start of the game and runs the entire model
	public void initGame() {
		//initialize game objects 
		initObjects();
		//initialize player and possibly other moving objects
		initMovingObjects();
		
	}
	
	public void resetGame() {
		double[] tempCurrentState = new double[552];
		double[] tempPreviousState = new double[552];
		int tempPreviousAction;
		boolean tempGameLost, tempGameWon;
		double tempReward, tempFeedback;
		//Only update actor and critic once every update interval
		/*if( (memory.transitions.size() > 100 || memory.transitions.size() == memory.maxsize ) && epochs % constants.UPDATE_INTERVAL == 0){
			//perform N updates
			int n = constants.UPDATES;
			for(int u = 0; u < n; u++){
				//Sample random transition from Replay Memory
				Transition randomTransition = memory.getTransition();
				//Use the elements of the random transition for learning
				for(int i = 0; i < 552; i++){
					tempCurrentState[i] = randomTransition.getNextState()[i];
					tempPreviousState[i] = randomTransition.getPreviousState()[i];			
				}
				tempPreviousAction = randomTransition.getAction();
				tempReward = randomTransition.getReward();
				tempFeedback = randomTransition.getFeedback();
				tempGameLost = randomTransition.getGameLost();
				tempGameWon = randomTransition.getGameWon();
									
				//feedback = critic.calculateFeedback(tempCurrentState, tempPreviousState, tempReward, tempGameLost, tempGameWon); 
				//backpropagate the feedback to the actor in the form of a TD-error (Temporal-Difference)
				actor.propagateFeedback(tempPreviousState, tempFeedback, tempPreviousAction);	
				//train the critic 
				critic.trainCritic(tempCurrentState, tempPreviousState, tempReward, tempGameLost, tempGameWon);	
				
			}
			
		}
		*/
		MOList.clear();
		PUList.clear();
		initObjects();
		initMovingObjects();
		//firstBarrel = true;
		powerupActivated = false;
		saveStateBeforeJump = false;
		//justLanded = false;
		mario.setPoweredUp(false);
		performance = 100 * gamesWon / gamesPlayed;
		//lower the temperature and learning rates every few games
		if(gamesPlayed % constants.temperatureCooling == 0){
			reduceTemperature(); 
		}
		if(gamesPlayed % constants.CRITIC_LEARNING_REDUCTION == 0 && constants.CRITIC_ON){
			critic.setLearningRate(critic.getLearningRate()/2);
		}
		if(gamesPlayed % constants.ACTOR_LEARNING_REDUCTION == 0 && constants.TEST_PHASE){
			actor.setLearningRate(actor.getLearningRate() / 2);
		}
		
		performancePerGame.add(Math.round(performance * 100) / 100.0);
		
	}
	
	public static boolean isColliding(GameObject o1, GameObject o2){
		float l1 = o1.getXPos(), r1 = l1+o1.getWidth(), t1 = o1.getYPos(), b1 = t1+o1.getHeight();
		float l2 = o2.getXPos(), r2 = o2.getXPos()+o2.getWidth(), t2 = o2.getYPos(), b2 = o2.getYPos()+o2.getHeight();
		//For Jelle: remember that y = 0 is at THE TOP of the screen
		if(!(l1>=r2 || l2>=r1 || t1>=b2 || t2>=b1)){
			return true;
		}
		return false;
	}
	
	public boolean isCollidingWithPlatformOrLadder(MovingObject o1, GameObject o2){
		float l1 = o1.getXPos(), r1 = l1+o1.getWidth(), t1 = o1.getYPos(), b1 = t1+o1.getHeight();
		float l2 = o2.getXPos(), r2 = o2.getXPos()+o2.getWidth(), t2 = o2.getYPos(), b2 = o2.getYPos()+o2.getHeight();
		//Jelle: remember that y = 0 is at THE TOP of the screen
		if((b1 <= b2 && b1 >= t2 && r1 > l2 && l1 < r2)){
			//If a moving object is falling, ladders and platforms are ignored
			if(o1.isFalling()) {
				//System.out.println("Falling!");
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	public MovingObject checkLadderCollisions(MovingObject MO){
		boolean collidingWithLadder = false;
		
		for(GameObject ladder : ladderList){
			if(isColliding(MO, ladder)){	
				collidingWithLadder = true;
				MO.setCanClimb(true);
				MO.setLadderXPos(ladder.getXPos());
				if (MO.getYPos() > ladder.getYPos()) {
					MO.setCollidingWithTop(true);
				}
				
			}
		}
		if (!collidingWithLadder) {
			MO.setCanClimb(false);
			MO.setFirstCanClimb(true);
			MO.setCollidingWithTop(false);
		}
		
		return MO;
	}
	
	
	//check collisions of moving objects with other objects such as platforms 
	public MovingObject checkCollisions(MovingObject MO){
		//For every moving object, check if it is standing on a platform
		MO.standing = false;
		MO = checkLadderCollisions(MO);
		
		//Handle collision of moving object with a platform
		for(Platform platform : platformList){
			boolean isColliding = isCollidingWithPlatformOrLadder(MO,platform);

			//check standing
			if(!MO.getStanding()){
				if(isColliding){ 

					if(!(MO.getIsClimbing() && platform.getHasLadder())) {
						//make object stand exactly on top of the platform 
						MO.setStanding(true);
		
						//make object stand exactly on top of the platform, unless climbing on ladder
						if(!MO.getIsClimbing()){
							MO.setYPos(platform.getYPos() - MO.getHeight());
						}
	
					}			

				}
				
			}	
			
			//If not colliding with ladder or standing on platform, stop climbing
			if(MO.getStanding() || !MO.getCanClimb()){
				MO.setIsClimbing(false);
			}
			
		}	
		
		
		//check if moving objects touch each other, but make sure an object isn't checked with itself
		for(MovingObject MO2 : MOList){
			//If Mario collides with a barrel, the game is over...
			if(MO.getName() == "player" && MO != MO2 && isColliding(MO,MO2) && powerupActivated == false){
				MO.isKilled = constants.DEATH;						
			} else if(MO.getName() == "player" && MO != MO2 && powerupActivated && isColliding(MO,MO2)) {
				//...unless Mario has a powerup. Then, the barrel is deleted and the score is incremented by 200
				//The barrel is stored in a temporary variable
				destroyedBarrel = true;
				smashedBarrelIndex = MOList.indexOf(MO2);
				System.out.println("Destroyed barrel!");
				//score += 200;
			}
		}
	
		//If Mario is in collision with Peach, the game is over
		if(MO.getName() == "player" && isColliding(MO,peach)) {
			gameWon = true;
		}
		
		
		//Check whether Mario is colliding with a powerup
		//Barrels never collide with a powerup, so we don't check the type of moving object
		for(Powerup PU : PUList) {
			if(isColliding(MO,PU)) {
				touchedPowerUp = true;
				powerupActivated = true;
				mario.setPoweredUp(true);
				//Store the index of the activated powerup so it can be deleted from the list
				powerupIndex = PUList.indexOf(PU);
				//This makes sure the timer is reset when a powerup is picked up while a powerup is already active
				powerupTimer = 0;
				System.out.println("Powerup activated");
			}
		}
		
		return MO;
	} 
	
	private void initObjects() {
		//initialize list
		platformList = new ArrayList<Platform>();
		ladderList =  new ArrayList<Ladder>();
		PUList = new ArrayList<Powerup>();
		
		
		
		if(constants.BARREL_TRAINING){
			initBarrelTestLevel();
		}
		else{
			initFirstLevel();
		}
	}
	
	//Function that determines for a given platform whether it's colliding with a ladder before it's added to the list
	private void checkPlatformLadderCollisions() {
		for(Platform platform : platformList) {
			for(Ladder ladder : ladderList){
				if(isColliding(platform, ladder)){	
					platform.setHasLadder(true);
				}
			}
		}
	}
	
	private void initBarrelTestLevel(){
		Platform p;
		for(int i = 0;  i < constants.SCREEN_X ; i += constants.PLATFORM_WIDTH){
			p = new Platform(i,constants.SCREEN_Y - 200, constants.PLATFORM_HEIGHT, constants.PLATFORM_WIDTH);
			platformList.add(p);
		}
		peach = new Peach(500, constants.PEACH_START_Y+300,constants.PEACH_HEIGHT,constants.PEACH_WIDTH);
	}
	
	private void initFirstLevel() {
		int x = 0;
		int y = constants.SCREEN_Y - 50;
		int platformYDiff = 2;
		Platform p;

		//bottom layer left half
		for(int i = 0; i < constants.SCREEN_X /2; i += constants.PLATFORM_WIDTH){
			p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
			platformList.add(p);
			x = i;
		}
		
		//bottom layer right half
		for(int i = x; i <= constants.SCREEN_X - constants.PLATFORM_WIDTH; i += constants.PLATFORM_WIDTH){
			x = i;
			y -= platformYDiff;
			p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
			platformList.add(p);
		}
		p = new Platform(x-10,y-50,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
		platformList.add(p);
		p = new Platform(x-10,y-200,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
		platformList.add(p);
		//Middle four layers
		for(int j = 0; j < 4; j++) {
			y += platformYDiff;
			y -= 3*constants.PLATFORM_HEIGHT;
			
			// If true, draw to the left
			if (j % 2 == 0) {
				x -= constants.PLATFORM_WIDTH;		
				//second layer
				for(int i = x; i >= 0; i -= constants.PLATFORM_WIDTH){
					x = i;
					y -= platformYDiff;
					p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
					platformList.add(p);
				}
			} else {
				x += constants.PLATFORM_WIDTH;
				
				//second layer
				for(int i = x; i <= constants.SCREEN_X - constants.PLATFORM_WIDTH; i += constants.PLATFORM_WIDTH){
					x = i;
					y -= platformYDiff;
					p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
					platformList.add(p);
				}
			}
		}
		p = new Platform(x-500,y+25,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
		platformList.add(p);
		p = new Platform(x-500,y+175,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
		platformList.add(p);
		//Top layer
		y += platformYDiff;
		y -= 3*constants.PLATFORM_HEIGHT;
		x -= constants.PLATFORM_WIDTH;	
		
		for(int i = x; i > constants.SCREEN_X/2 + constants.PLATFORM_WIDTH; i -= constants.PLATFORM_WIDTH){
			x = i;
			y -= platformYDiff;
			p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
			platformList.add(p);
		}
		p = new Platform(x+150,y+7,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
		platformList.add(p);
		for(int i = x; i >= 0; i -= constants.PLATFORM_WIDTH){
			x = i;
			p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
			platformList.add(p);
		}
		
		//Peach layer
		x = constants.SCREEN_X/2 - constants.PLATFORM_WIDTH;
		y -= 4*constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 3; i++){
			p = new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH);
			platformList.add(p);
			x += constants.PLATFORM_WIDTH;
		}
		
		//Draw bottom layer ladders 
		x = 4 * constants.PLATFORM_WIDTH + 10;
		y = constants.SCREEN_Y - 60;
		for(int i = 0; i < 2*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
		}
		
		y -= constants.PLATFORM_HEIGHT + 5 * platformYDiff;
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x = constants.SCREEN_X - 2 * constants.PLATFORM_WIDTH;
		y = constants.SCREEN_Y - 31 * platformYDiff;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Draw second layer ladders
		x = constants.SCREEN_X / 2 - constants.PLATFORM_WIDTH;
		y = constants.SCREEN_Y - 7*constants.PLATFORM_HEIGHT - 4 * platformYDiff;
		for(int i = 0; i < 8*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x -= 4*constants.PLATFORM_WIDTH;
		y = constants.SCREEN_Y - 7*constants.PLATFORM_HEIGHT - 8 * platformYDiff;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Draw third layer ladders
		x += 2 * constants.PLATFORM_WIDTH;
		y -= platformYDiff;
		for(int i = 0; i < 2*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			//ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		y -= 4 * constants.LADDER_HEIGHT;
		
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x += 3*constants.PLATFORM_WIDTH;
		y += 5*constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 8*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x += 4*constants.PLATFORM_WIDTH;
		y += 4*constants.PLATFORM_HEIGHT + 2* platformYDiff;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Draw fourth layer ladders
		x -= constants.PLATFORM_WIDTH;

		for(int i = 0; i < 2*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			//ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		y -= 4 * constants.LADDER_HEIGHT;
		
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x -= 5 * constants.PLATFORM_WIDTH;
		y += 5 * constants.PLATFORM_HEIGHT - 2 * platformYDiff;
		for(int i = 0; i < 7*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x -= 3 * constants.PLATFORM_WIDTH;
		y += 4 * constants.PLATFORM_HEIGHT - 3 * platformYDiff;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Draw fifth layer ladders
		x += 4 * constants.PLATFORM_WIDTH;
		y -= 3 * platformYDiff;
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			//ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		y -= constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x += 5 * constants.PLATFORM_WIDTH;
		y += 4.1 * constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		//Draw top layer ladders
		x -= (int)(2.5 * constants.PLATFORM_WIDTH);
		y -= 0.5 * platformYDiff;
		for(int i = 0; i < 7 * constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		
		peach = new Peach(constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_HEIGHT,constants.PEACH_WIDTH);
		PUList.add(new Powerup(425,500,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
		PUList.add(new Powerup(50,285,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
		//Check for each platform whether it collides with a ladder
		checkPlatformLadderCollisions();
	}
	
	private void initMovingObjects() {
		//initialize moving object list
		MOList = new ArrayList<MovingObject>();
		//initialize player. Position depends on the level
		if(constants.BARREL_TRAINING){
			mario = new Player(constants.PLAYER_START_X - 50,constants.SCREEN_Y - 220,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	
		}
		else{
			mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	
			
		}
		//add objects to list of moving objects
		MOList.add(mario);	
	}
	
	public void setPlayerAction(int action){
		mario.setAction(action);
	}
	
	public void passKeysDownToPlayer(boolean[] down) {
		mario.setKeysDown(down);
	}
	
	
	public ArrayList<Platform> getPlatformList(){
		return platformList;
	}
	
	public ArrayList<Ladder> getLadderList(){
		return ladderList;
	}
	
	public ArrayList<MovingObject> getMOList(){
		return MOList;
	}
	
	public ArrayList<Powerup> getPUList(){
		return PUList;
	}
	
	
	public int getLives(){
		return lives;
	}
	public double getScore(){
		return score;
	}

	//Allow the game panel to get the vision grid for drawing them on the screen 
	public VisionGrid getVisionGrid() {
		return visionGrid;
	}
	public VisionGrid getMarioTracker(){
		return marioTracker;
	}	
}
