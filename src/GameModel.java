import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;
import java.util.Observable;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class GameModel extends Observable implements constants {
	private int score = 0;
	private int lives = 3;
	
	//these values determine how fast barrels are spawned in the game
	private int spawnTimer = 300;	
	private int barrelSpawnTime = 300; 
	
	//These values relate to powerups and destroying barrels
	private int smashedBarrelIndex = -1;
	private int powerupTimer = 0;	
	private int powerupDuration = 300;
	private int powerupIndex = -1;
	
	//This value determines for how many epochs the game has been running already
	private int epochs;
	//This value determines how long the game model should sleep or slow down, in order to make the game playable
	//for a human
	private int sleepTime = 15;
	
	//These values determine the rspective amount of inputs to the Multi-layer Perceptron for learning
	//to climb ladders or dodging barrels
	private int nInputsClimb = 5;
	private int nInputsDodge = 2;
	private int nOutputs = 7;
	
	private ArrayList<Integer> gravityTimes;
	
	//These lists contain all the game objects
	private ArrayList<Platform> platformList;
	private ArrayList<Ladder> ladderList;
	private ArrayList<MovingObject> MOList;
	private ArrayList<Powerup> PUList;
	
	//These lists contain the entire respective inputs + targets training setfor each epoch. These arrays 
	//are later written to a file
	private ArrayList<double[]> dodgeTrainingSet = new ArrayList<double[]>();
	private ArrayList<double[]> climbTrainingSet = new ArrayList<double[]>();
	
	//These variables are the two multi-layer perceptrons that are used for learning 
	MLPJelle dodgeMLP;
	MLPJelle climbMLP;
	
	private Player mario;
	private Peach peach;
	
	private boolean powerupActivated = false;
	private boolean gameWon = false;
	private boolean gameOver = false; 
	
	
	//allow for alternating left and right barrel spawing during training
	private boolean leftSpawned = true;
	
	//These values relate to specific inputs necessary for learning to jump over barrels
	private int ladderRight;
	private int barrelRight;
	private int barrelOnSameLevel;
	private int barrelClimbing;
	
	
	//The file handler is used to write to and read from the text files
	FileHandler fh = new FileHandler();
		
	public GameModel(){
		initGame();
	}
	
	//Create a new barrel object 
	public void spawnBarrel(boolean falling){
		gravityTimes.add(0);
		MOList.add(new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, true, falling));
	}
	
	//these two functions are used for alternative spawning of barrels, to be used during dodge training
	//in the alternative dodge level
	public void spawnLeftBarrel(){
		gravityTimes.add(0);
		MOList.add(new Barrel(0,constants.SCREEN_Y-220,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, true, false));
	}
	
	public void spawnRightBarrel(){
		gravityTimes.add(0);
		MOList.add(new Barrel(constants.SCREEN_X, constants.SCREEN_Y-220,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, false, false));
	}
	
	
	public void incrementTime(){
		for(int i = 0; i < MOList.size(); i++){
			MovingObject MO = MOList.get(i);
			//reset gravity when standing or climbing
			if((MO.standing )|| MO.isClimbing){
				gravityTimes.set(i, 0);
				if(MO instanceof Player){
					((Player) MOList.get(i)).setJump(false);
				}			
			}
			else{
				gravityTimes.set(i, (gravityTimes.get(i)) + 1) ;
			}		
		}	
	}
	
	public float getEuclideanDistance(GameObject go1, GameObject go2) {
		float x1 = go1.getXPos() + go1.getWidth() / 2;
		float x2 = go2.getXPos() + go2.getWidth() / 2;
		float y1 = go1.getYPos() + go1.getHeight() / 2;
		float y2 = go2.getYPos() + go2.getHeight() / 2;
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) );
	}
	
	public float findNearestObject(String objectName){
		float minimumDistance = 300;
		if(objectName == "ladder"){
			for(int i = 0; i < ladderList.size(); i++){
				Ladder l = ladderList.get(i);
				float distance = getEuclideanDistance(mario,ladderList.get(i));
				//if platform is closest to mario, save the distance
				//only do this if the platform is between the top and middle of mario
				if(distance < minimumDistance && (l.getYPos() + l.getHeight()) > mario.getYPos()
				&& (l.getYPos() + l.getHeight()) < (mario.getYPos() + mario.getHeight() / 2)){
					minimumDistance = distance;
					if(mario.getXPos() <= l.getXPos()){
						ladderRight = 1;				
					}
					else{
						ladderRight = 0;
					}
				}
				
			}
			
		}
		else if(objectName == "powerup"){
			for(int i = 0; i < PUList.size(); i++){
				float distance = getEuclideanDistance(mario,PUList.get(i));
				if(distance < minimumDistance){
					minimumDistance = distance;
				}
			}		
		}
		
		else if(objectName == "barrel"){
			for(int i = 0; i < MOList.size(); i++){
				if(MOList.get(i).getName() == "barrel"){
					Barrel b = (Barrel) MOList.get(i);
					float distance = getEuclideanDistance(mario,MOList.get(i));
					
					//only consider barrels that still wield a point when jumped over; 
					//don't look at barrels that are no longer a threat
					if(distance < minimumDistance && !b.pointAwarded()){
							minimumDistance = distance;
							//check if barrel is to the left or to the right
							if(mario.getXPos() <= b.getXPos()){
								barrelRight = 1;
							}
							else{
								barrelRight = 0;
							}
					//if barrel is on the same level
					if((b.getYPos() + b.getHeight()) >= (mario.getYPos())
					    && (b.getYPos() + b.getHeight()) <= (mario.getYPos() + mario.getHeight()) ){
					    	barrelOnSameLevel = 1;							
					}
					else{
						barrelOnSameLevel = 0;
					}
					if(b.isClimbing){
						barrelClimbing = 1;
					}
					else{
						barrelClimbing = 0;
					}
				}
			}
		}
	  }
		return minimumDistance;
	}
	
	public float normalize(float distance){
		if(distance < 15){
			return 1;
			
		}
		else if(distance >= 15 && distance < 200){
			return 0;
		}
		else{
			return -1;
		}
		//return (distance < 500? (500-distance)/500 : 500);
	}
	
	public double[] calculateClimbInputs(){
		//this array contains the 3 boolean and 5 float inputs for the ladder climbing MLP
		double[] climbInputs = new double[nInputsClimb + nOutputs];
		
		//climb mlp inputs
		climbInputs[0] = mario.isClimbing() ? 1 : -1;
		//climbInputs[1] = mario.isJumping() ? 1 : -1;
		//calculate distance to nearest power-up
		//climbInputs[2] = normalize(findNearestObject("powerup"));
		//calculate distance to nearest ladder
		climbInputs[1] = normalize(findNearestObject("ladder"));	
		climbInputs[2] = ladderRight;	
		climbInputs[3] = normalizeForDodging(findNearestObject("barrel"));
		climbInputs[4] = barrelRight;
		climbInputs[5] = barrelClimbing;
		climbInputs[6] = barrelOnSameLevel;
		//calculate distance to peach 
		//climbInputs[3] = normalize(getEuclideanDistance(mario, peach));
		
		System.out.println("1: Mario is climbing: " + mario.isClimbing());
		System.out.println("2: Nearest Ladder: " + climbInputs[1]);
		System.out.println("3: Ladder is to the right: " + climbInputs[2]);
		System.out.println("4: Nearest barrel? " + climbInputs[3]);
		System.out.println("5: Barrel to right? " + climbInputs[4]);
		System.out.println("6: Barrel climbing? " + climbInputs[5]);
		System.out.println("7: Barrel on same level? " + climbInputs[6]);
		//System.out.println("Distance to peach: " + climbInputs[3]);
		
		
		return climbInputs;
	}	
	
	public float normalizeForDodging(float distance){
		if(distance < 130){
			return 1;
			
		}
		else if(distance >= 15 && distance < 200){
			return 0;
		}
		else{
			return -1;
		}
	}
	
	//This function calculates the inputs necessary for dodging barrels
	public double[] calculateDodgeInputs(){
		double[] dodgeInputs = new double[nInputsDodge + nOutputs];
		//calculate distance to the nearest barrel
		dodgeInputs[0] = normalizeForDodging(findNearestObject("barrel"));
		//determine whether or not a barrel is to the left or to the right of Mario
		dodgeInputs[1] = barrelRight;	
		
		return dodgeInputs;
	}
	
	//main game loop
	public void runGame() throws InterruptedException, IOException{
		int counter = 0;
		double[] climbInputs;
		double[] dodgeInputs;
		double[] testInputs;
		//Create the MLP's and train them on their respective training datasets
		if(constants.TEST_PHASE_DODGING){
			dodgeMLP = new MLPJelle(nInputsDodge, 2, 70, nOutputs, "dodgeData");
			dodgeMLP.trainNetwork();
		}
		if(constants.TEST_PHASE_CLIMBING){
			climbMLP = new MLPJelle(nInputsClimb, 1, 70, nOutputs, "climbData");
			climbMLP.trainNetwork();
		}
		while(epochs < constants.MAX_EPOCHS && !gameWon){
			//mario.setAction(0);
			climbInputs = calculateClimbInputs();
			dodgeInputs = calculateDodgeInputs();
			//System.out.println("Mario in danger: " + marioInDanger);
			
			//present input to networks
			if(constants.TEST_PHASE_CLIMBING){
				testInputs = Arrays.copyOfRange(climbInputs, 0, nInputsClimb);
				mario.setAction(climbMLP.testNetwork(testInputs));
			}
			
			//if only training on dodging barrels, only present input to dodging MLP
			else if(constants.BARREL_TRAINING && constants.TEST_PHASE_DODGING){
				testInputs = Arrays.copyOfRange(dodgeInputs, 0, nInputsDodge);
				mario.setAction(dodgeMLP.testNetwork(testInputs));
			}
				
			incrementTime();
				
			//spawn barrels
			if(spawnTimer == barrelSpawnTime){
				spawnTimer = 0;
				if(constants.BARREL_TRAINING){
					if(!leftSpawned){
						spawnLeftBarrel();
						leftSpawned = true;
					}
					else{
						spawnRightBarrel();
						leftSpawned = false;
					}
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
					System.out.println("Powerup deactivated");
				}	
				powerupTimer++;
			}
			
			
			for(int i = 0; i < MOList.size(); i++){
					//make all moving objects act/move
				MovingObject MO = checkCollisions(MOList.get(i));
				MOList.set(i, MO); 
					MOList.get(i).act(gravityTimes.get(i));
					//check collisions and update moving object states
						
					//if mario is hit, subtract a life
					if(MOList.get(0).isKilled){	
						lives--;
						resetGame();
					} else if(gameWon){
						//if mario saved the princess, add 1000 points instead
						System.out.println("Goal reached!!!!!!!!!!!!!!!!!!!!!");
						//gameWon = false;
						score += 1000;
						resetGame();
					}
										
					//If object falls out of the game screen, delete it
					else if(MOList.get(i).getYPos() >= constants.SCREEN_Y){
						MOList.remove(i);
						gravityTimes.remove(i);
					}
					
					//If a barrel has been smashed, the value of smashedBarrelIndex >= 0
					//We remove the barrel using this index
					else if (smashedBarrelIndex >= 0) {
						MOList.remove(smashedBarrelIndex);
						gravityTimes.remove(smashedBarrelIndex);
						smashedBarrelIndex = -1;
					}

					//If mario jumps over a barrel, increment score by 100
					else if(MOList.get(i).getYPos() >= mario.getYPos()  && MOList.get(i).getYPos() <= mario.getYPos() + 100 && 
						mario.getXPos() >= MOList.get(i).getXPos()	&&
						mario.getXPos() <= MOList.get(i).getXPos()+MOList.get(i).getWidth() &&
						!(MOList.get(i).pointAwarded)){
						
						MOList.get(i).setPointAwarded();
						score += 100;
					}
			}
			
			/*if(flame != null) {
				//The flame's direction depends on the player's direction, so we handle that here
				flame.setDirection(mario.getXPos(), mario.getYPos());
			}*/
			
			//slow game model down, so that game can be played by human
			if(GUI_ON){
				Thread.sleep(sleepTime);
			}
			epochs++;
			
			if(constants.DEMO_PHASE_DODGING){
			dodgeInputs[nInputsDodge + mario.getAction()] = 1.0;
			dodgeTrainingSet.add(dodgeInputs);
			}
			if(constants.DEMO_PHASE_CLIMBING){
				climbInputs[nInputsClimb + mario.getAction()] = 1.0;
				climbTrainingSet.add(climbInputs);
			}
			
			//for(int i = 0; i < nInputsClimb + nOutputs; i++){
				//System.out.print(climbInputs[i] + " ");
			//}
			//System.out.println();
			
			
		}

		//write entire state-action array to training file(s)
		if(constants.DEMO_PHASE_DODGING  ){
			fh.writeToFile(dodgeTrainingSet, "dodgeData");
			
		}
		if(constants.DEMO_PHASE_CLIMBING){
			fh.writeToFile(climbTrainingSet, "climbData");
		}
		
	}	
	
	//This function is called at the start of the game and runs the entire model
	public void initGame() {
		//initialize game objects 
		initObjects();
		//initialize player and possibly other moving objects
		initMovingObjects();
		
	}
	
	public void resetGame() {
		MOList.clear();
		PUList.clear();
		gravityTimes.clear();
		initObjects();
		initMovingObjects();
		//firstBarrel = true;
		powerupActivated = false;
	}
	
	public boolean isColliding(GameObject o1, GameObject o2){
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
					//System.out.println("COLLIDING WITH LADDER");
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
	
						//System.out.println("Standing on platform!");
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
				smashedBarrelIndex = MOList.indexOf(MO2);
				score += 200;
			}
		}
	
		//If Mario is in collision with Peach, the game is over
		if(MO.getName() == "player" && isColliding(MO,peach)) {
			gameWon = true;
		}
		
		/*If a falling barrel hits the oil barrel, spawn a flame
		if(MO.isFalling() && isColliding(MO,oil) && firstFlameOilCollision) {
			//This boolean makes sure a flame is only spawned once for each collision between the barrel and the oil
			firstFlameOilCollision = false;
			//Initialize flame
			flame = new Flame(constants.FLAME_START_X,constants.FLAME_START_Y,constants.FLAME_HEIGHT,constants.FLAME_WIDTH);	
			//Add flame to Moving Object list
			MOList.add(flame);
			gravityTimes.add(0);
		} else if (MO.isFalling() && !isColliding(MO,oil)) {
			firstFlameOilCollision = true;
		}*/
		
		//Check whether Mario is colliding with a powerup
		//Barrels never collide with a powerup, so we don't check the type of moving object
		for(Powerup PU : PUList) {
			if(isColliding(MO,PU)) {
				powerupActivated = true;
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
		peach = new Peach(constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_HEIGHT,constants.PEACH_WIDTH);
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
			//ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
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
		y += 4 * constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		//Draw top layer ladders
		x -= (int)(2.5 * constants.PLATFORM_WIDTH);
		y -= 1 * platformYDiff;
		for(int i = 0; i < 7 * constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		/*x -= 3 * constants.PLATFORM_WIDTH;
		y += 7 * constants.LADDER_HEIGHT;
		for(int i = 0; i < 15*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			GOList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x -= constants.PLATFORM_WIDTH;
		y += 16 * constants.LADDER_HEIGHT;
		for(int i = 0; i < 15*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			GOList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		*/
		peach = new Peach(constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_HEIGHT,constants.PEACH_WIDTH);
		PUList.add(new Powerup(425,500,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
		PUList.add(new Powerup(50,285,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
		//Check for each platform whether it collides with a ladder
		checkPlatformLadderCollisions();
	}
	
	private void initMovingObjects() {
		gravityTimes = new ArrayList<Integer>();
		//initialize list
		MOList = new ArrayList<MovingObject>();
		//initialize player
		if(constants.BARREL_TRAINING){
			mario = new Player(constants.PLAYER_START_X,constants.SCREEN_Y - 220,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	
		}
		else{
			mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	
			
		}
		//add objects to list of moving objects
		MOList.add(mario);	
		
		//initalize the gravity timers of the moving objects
		for(int i = 0; i < MOList.size(); i++){
			gravityTimes.add(0);
		}
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
	
	public boolean isGameOver(){
		return gameOver;
	}
	public int getLives(){
		return lives;
	}
	public int getScore(){
		return score;
	}

	
	
	
}
