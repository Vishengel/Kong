import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class GameModel extends Observable implements constants {
	private int score = 0;
	private int lives = 3;
	private int spawnTimer = 0;	
	private int barrelSpawnTime = 250;
	private int smashedBarrelIndex = -1;
	private int powerupTimer = 0;	
	private int powerupDuration = 500;
	private int powerupIndex = -1;
	private int epochs;
	private int sleepTime = 15;
	
	private ArrayList<Integer> gravityTimes;
	private ArrayList<Platform> platformList;
	private ArrayList<Ladder> ladderList;
	private ArrayList<MovingObject> MOList;
	private ArrayList<Powerup> PUList;
	
	//this array contains the 3 boolean and 6 float inputs for the MLP
	private float[] inputs = new float[9];
	
	
	private Player mario;
	private Peach peach;
	private Oil oil;
	private Flame flame;
	private Powerup powerup;
	
	private boolean powerupActivated = false;
	private boolean gameWon = false;
	private boolean gameOver = false; 
	private boolean firstBarrel = true;
	private boolean firstFlameOilCollision = true;
	
	FileHandler fh = new FileHandler();
		
	public GameModel(){
		initGame();
	}
	
	public void spawnBarrel(boolean falling){
		gravityTimes.add(0);
		MOList.add(new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, true, falling));
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
		float minimumDistance = 9999999;
		if(objectName == "ladder"){
			for(int i = 0; i < ladderList.size(); i++){
				Ladder l = ladderList.get(i);
				float distance = getEuclideanDistance(mario,ladderList.get(i));
				//if platform is closest to mario, save the distance
				//only do this if the platform is between the top and middle of mario
				if(distance < minimumDistance && (l.getYPos() + l.getHeight()) > mario.getYPos()
				&& (l.getYPos() + l.getHeight()) < (mario.getYPos() + mario.getHeight() / 2)){
					minimumDistance = distance;
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
					float distance = getEuclideanDistance(mario,ladderList.get(i));
					if(distance < minimumDistance){
								minimumDistance = distance;
					}
				}
			}
		}
		
		return minimumDistance;
	}
	
	public void calculateInputs(){
		//update boolean input array
		inputs[0] = powerupActivated ? 1 : 0;
		inputs[1] = mario.isClimbing() ? 1 : 0;
		inputs[2] = mario.isJumping() ? 1 : 0;
		//update distance input array
		//calculate distance to flame enemy
		if(flame != null){
			inputs[3] = getEuclideanDistance(mario, flame);
			//System.out.println("1. nearest flame " + inputs[0]);
		}
		//calculate distance to nearest power-up
		inputs[4] = findNearestObject("powerup");
		//System.out.println("2. nearest powerup " + inputs[1]);
		//calculate distance to nearest ladder
		inputs[5] = findNearestObject("ladder");
		//System.out.println("3. Nearest Ladder: " + inputs[2]);
		//calculate distance to peach
		inputs[6] = getEuclideanDistance(mario, peach);
		//System.out.println("4. peach: " + inputs[3]);
		//calculate distance to the nearest barrel on the same platform
		inputs[7] =	findNearestObject("barrel");
		//System.out.println("nearest barrel: " + inputs[4]);
		//calculate distance to nearest barrel on upper platform
		inputs[8] = findNearestObject("upperBarrel");
		//System.out.println("nearest upper-barrel: " + inputs[5]);
	}
	
	
	//main game loop
	public void runGame() throws InterruptedException, IOException{
		while(epochs < constants.MAX_EPOCHS){
			calculateInputs();
			//handle gravity
			incrementTime();
				
			//spawn barrel
			if(spawnTimer == barrelSpawnTime){
				spawnTimer = 0;
				//The first barrel always goes directly down to the oil barrel
				if (firstBarrel) {
					spawnBarrel(true);
					firstBarrel = false;
				} else {
					spawnBarrel(false);
				}
			}	
			spawnTimer++;
			
			//I
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
					
					if(gameWon) {
						score+=1000;
					}
					//if player is hit or game is won, reset objects. 
					if(MOList.get(0).isKilled || gameWon){
						//System.out.println("MARIO IS DEAD!!!!!");
						MOList.clear();
						PUList.clear();
						gravityTimes.clear();
						initFirstLevel();
						initMovingObjects();
						firstBarrel = true;
						powerupActivated = false;
						
						//if mario is hit, subtract a life
						if(MOList.get(0).isKilled){	
							lives--;
						}
						//if mario saved the princess, add 1000 points instead
						else{
							gameWon = false;
							//score += 1000;
						}
						
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
			
			if(flame != null) {
				//The flame's direction depends on the player's direction, so we handle that here
				flame.setDirection(mario.getXPos(), mario.getYPos());
			}

			//slow game model down, so that game can be played by human
			if(GUI_ON){
				Thread.sleep(sleepTime);
			}
			epochs++;
			//write game state + action taken to training set
			fh.writeToFile(inputs,mario.getAction());
			
			
		}
		
	}	
	
	//This function is called at the start of the game and runs the entire model
	public void initGame() {
		//initialize game objects 
		initObjects();
		//initialize player and possibly other moving objects
		initMovingObjects();
		
	}
	
	public boolean isColliding(GameObject o1, GameObject o2){
		float l1 = o1.getXPos(), r1 = l1+o1.getWidth(), t1 = o1.getYPos(), b1 = t1+o1.getHeight();
		float l2 = o2.getXPos(), r2 = o2.getXPos()+o2.getWidth(), t2 = o2.getYPos(), b2 = o2.getYPos()+o2.getHeight();
		//For Jelle: remember that y = 0 is at THE TOP of the screen
		if(t1 <= b2 && b1 >= t2 && r1 > l2 && l1 < r2){
			return true;
		}
		return false;
	}
	
	public boolean isCollidingWithPlatformOrLadder(MovingObject o1, GameObject o2){
		float l1 = o1.getXPos(), r1 = l1+o1.getWidth(), t1 = o1.getYPos(), b1 = t1+o1.getHeight();
		float l2 = o2.getXPos(), r2 = o2.getXPos()+o2.getWidth(), t2 = o2.getYPos(), b2 = o2.getYPos()+o2.getHeight();
		//For Jelle: remember that y = 0 is at THE TOP of the screen
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
		for(GameObject ladder : ladderList){
			if(isCollidingWithPlatformOrLadder(MO, ladder)){		
				MO.canClimb = true;
				//System.out.println("COLLIDING WITH LADDER");
				return MO;
			}
		}
		MO.canClimb = false;
		return MO;
	}
	
	
	//check collisions of moving objects with other objects such as platforms 
	public MovingObject checkCollisions(MovingObject MO){
		//For every moving object, check if it is standing on a platform
		MO.standing = false;
		MO = checkLadderCollisions(MO);
		
		//Handle collision of moving object with a platform
		for(GameObject platform : platformList){
			boolean isColliding = isCollidingWithPlatformOrLadder(MO,platform);
			
			//check standing
			if(!MO.standing){
				if(isColliding){ 
					//make object stand exactly on top of the platform 
					MO.standing = true;
					//make object stand exactly on top of the platform, unless climbing on ladder
					if(!MO.isClimbing){
						MO.setYPos(platform.getYPos() - MO.getHeight());
					}

					//System.out.println("Standing on platform!");
									
				}
				
			}	
			
			//If not colliding with ladder or standing on platform, stop climbing
			if(MO.standing || !MO.canClimb){
				MO.isClimbing = false;
			}
			
		}	
		
		
		//check if moving objects touch each other, but make sure an object isn't checked with itself
		for(MovingObject MO2 : MOList){
			//If Mario collides with a barrel, the game is over...
			if(MO.getName() == "player" && MO != MO2 && isColliding(MO,MO2) && powerupActivated == false){
				MO.isKilled = true;						
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
		
		//If a falling barrel hits the oil barrel, spawn a flame
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
		}
		
		//Check whether Mario is colliding with a powerup
		//Barrels never collide with a powerup, so we don't check the type of moving object
		for(Powerup PU : PUList) {
			if(isColliding(MO,PU)) {
				powerupActivated = true;
				powerupIndex = PUList.indexOf(PU);
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
		
		//initTestLevel();
		initFirstLevel();
	}
	
	private void initTestLevel() {
		//create platforms		
		
		//bottom layer first half
		for(int i = 0; i < constants.SCREEN_X /2; i = i + constants.PLATFORM_WIDTH){
			platformList.add(new Platform(50 + i,constants.SCREEN_Y - 50,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
		}
		
		
		//bottom layer second half 
		int y = constants.SCREEN_Y - 50;
		for(int i = constants.SCREEN_X /2; i < constants.SCREEN_X - 50; i = i + constants.PLATFORM_WIDTH){
			platformList.add(new Platform(i,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			y = y - 1;
		}
		
		//second layer
		int x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 150;
		for(int i = x - 20; i > 35; i = i - constants.PLATFORM_WIDTH){
			platformList.add(new Platform(i,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			y = y - 1;
		}
		
		//third layer
		y = constants.SCREEN_Y - 300;
		for(int i = 100; i < constants.SCREEN_X - 50; i = i + constants.PLATFORM_WIDTH){
			platformList.add(new Platform(i,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			y = y - 1;
		}
		
		//upper layer
		x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 450;
		for(int i = x - 20; i > 50; i = i - constants.PLATFORM_WIDTH){
			platformList.add(new Platform(i,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			y = y - 1;
		}
		
		
		//create ladders
		for(int i = 0; i < 11*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			ladderList.add(new Ladder(500,612-i,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
	}
	
	private void initFirstLevel() {
		int x = 0;
		int y = constants.SCREEN_Y - 50;
		int platformYDiff = 2;

		//bottom layer left half
		for(int i = 0; i < constants.SCREEN_X /2; i += constants.PLATFORM_WIDTH){
			platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			x = i;
		}
		
		//bottom layer right half
		for(int i = x; i <= constants.SCREEN_X - constants.PLATFORM_WIDTH; i += constants.PLATFORM_WIDTH){
			x = i;
			y -= platformYDiff;
			platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
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
					platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
				}
			} else {
				x += constants.PLATFORM_WIDTH;
				
				//second layer
				for(int i = x; i <= constants.SCREEN_X - constants.PLATFORM_WIDTH; i += constants.PLATFORM_WIDTH){
					x = i;
					y -= platformYDiff;
					platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
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
			platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
		}
		
		for(int i = x; i >= 0; i -= constants.PLATFORM_WIDTH){
			x = i;
			platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
		}
		
		//Peach layer
		x = constants.SCREEN_X/2 - constants.PLATFORM_WIDTH;
		y -= 4*constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 3; i++){
			platformList.add(new Platform(x,y,constants.PLATFORM_HEIGHT,constants.PLATFORM_WIDTH));
			x += constants.PLATFORM_WIDTH;
		}
		
		//Draw bottom layer ladders 
		x = 5 * constants.PLATFORM_WIDTH;
		y = constants.SCREEN_Y - 50;
		for(int i = 0; i < 2*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		y -= 2 * constants.PLATFORM_HEIGHT;
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		x = constants.SCREEN_X - 2 * constants.PLATFORM_WIDTH;
		y = constants.SCREEN_Y - 50 - 5 * platformYDiff;
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
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
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
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
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
		y += 4 * constants.PLATFORM_HEIGHT - 2 * platformYDiff;
		for(int i = 0; i < 6*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Draw fifth layer ladders
		x += 4 * constants.PLATFORM_WIDTH;
		y -= 2 * platformYDiff;
		for(int i = 0; i < 3*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			y -= constants.LADDER_HEIGHT;
			ladderList.add(new Ladder(x,y,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
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
		oil = new Oil(constants.OIL_START_X,constants.OIL_START_Y,constants.OIL_HEIGHT,constants.OIL_WIDTH);
		PUList.add(new Powerup(425,500,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
		PUList.add(new Powerup(50,285,constants.POWERUP_HEIGHT,constants.POWERUP_WIDTH));
	}

	private void initMovingObjects() {
		gravityTimes = new ArrayList<Integer>();
		//initialize list
		MOList = new ArrayList<MovingObject>();
		//initialize player
		mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	

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