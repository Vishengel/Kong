import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class GameModel extends Observable implements constants {
	private int score = 0;
	private int lives = 3;
	private int timer = 0;
	private int spawnTime = 2;
	private int spawnTimer = 0;
	private int gravityTime = 10;
	private int barrelSpawnTime = 100;
	private int epochs;
	
	private ArrayList<Integer> gravityTimes;
	private boolean gameOver = false; 
	private ArrayList<GameObject> GOList;
	private ArrayList<MovingObject> MOList;
	private Player mario;
	private Peach peach;
		
	public GameModel(){
		initGame();
	}
	
	public void spawnBarrel(){
		gravityTimes.add(0);
		MOList.add(new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WIDTH, GOList, true));
		spawnTimer = 0;
	}
	public void incrementTime(){
		for(int i = 0; i < MOList.size(); i++){
			MovingObject MO = MOList.get(i);
			if(MO.standing || MO.isClimbing){
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
	
	
	//main game loop

	public void runGame() throws InterruptedException{
		while(epochs < constants.MAX_EPOCHS){
			epochs++;
			//System.out.println(epochs);
			timer++;
			//handle gravity every 150 milliseconds
			if(timer % gravityTime == 0){
				incrementTime();	
			}
			//spawn a barrel every 450 milliseconds
			if(timer % barrelSpawnTime == 0){
				//spawnBarrel();
			} 
			//reset timer eventually, to avoid overflow
			if(timer > 1500){
				timer = 0;
			}
			//System.out.println(timer);
			//System.out.println(gravityTimes);
			
			for(int i = 0; i < MOList.size(); i++){
					//make all moving objects act/move
					MOList.get(i).act(gravityTimes.get(i));
					//check collisions and update moving object states
					MovingObject MO = checkCollisions(MOList.get(i));
					MOList.set(i, MO); 
					if(mario.hasWon()) {
						MOList.clear();
						gravityTimes.clear();
						initMovingObjects();
						score+=1000;
					}
					//if player is hit, reset objects and subtract a life
					if(MOList.get(0).isKilled){
						//System.out.println("MARIO IS DEAD!!!!!");
						MOList.clear();
						gravityTimes.clear();
						initMovingObjects();
						lives--;
					} 
					
					//If object falls out of the game screen, delete it
					else if(MOList.get(i).getYPos() >= constants.SCREEN_Y){
						MOList.remove(i);
						gravityTimes.remove(i);
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
			Thread.sleep(15);
		}
			//setChanged();
			//notifyObservers();
		
			//stopping condition
			//gameOver = true; 
		
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
		float l2 = o2.xPos, r2 = o2.xPos+o2.width, t2 = o2.yPos, b2 = o2.yPos+o2.height;
		if(b1 <= b2 && b1 >= t2 && r1 > l2 && l1 < r2){
			return true;
		}
		return false;
	}
	
	//check collisions of moving objects with other objects such as platforms 
	public MovingObject checkCollisions(MovingObject MO){
		//For every moving object, check if it is standing on a platform
		MO.standing = false;
		MO.isClimbing = false;
		
		for(GameObject GO : GOList){
			boolean isColliding = isColliding(MO,GO);
			
			//check climbing
			if(!MO.isClimbing){
				if(GO.getName() == "ladder" && isColliding){
					MO.isClimbing = true;
					break;
				}
				if(GO.getName() == "ladder" && !isColliding){
					MO.isClimbing = false;
				}
			}
			//check standing
			if(!MO.standing){
				if(GO.getName() == "platform" && isColliding){ 
					//make object stand exactly on top of the platform 
					MO.standing = true;
					//make object stand exactly on top of the platform, unless climbing on ladder
					if(!MO.isClimbing){
						MO.setYPos(GO.getYPos() - MO.getHeight());
					}
					//System.out.println("Standing on platform!");
									
				}
				
				if(GO.getName() == "platform" && !isColliding){		
					MO.standing = false;
					//System.out.println("Not standing..");
				}
			}
			
			
		}	
		if(MO.isClimbing){
			MO.standing = false;
		}
		
		//check if moving objects touch each other, but make sure an object isn't checked with itself
		for(MovingObject MO2 : MOList){
			if(MO != MO2 && isColliding(MO,MO2)){
				MO.isKilled = true;						
			}
		}
		return MO;
	} 
		
	
	private void initObjects() {
		//initialize list
		GOList = new ArrayList<GameObject>();
		
		//create platforms		
		
		//bottom layer first half
		for(int i = 0; i < constants.SCREEN_X /2; i = i + constants.platform_WIDTH){
			GOList.add(new Platform(50 + i,constants.SCREEN_Y - 50,constants.platform_HEIGHT,constants.platform_WIDTH));
		}
		
		
		//bottom layer second half 
		int y = constants.SCREEN_Y - 50;
		for(int i = constants.SCREEN_X /2; i < constants.SCREEN_X - 50; i = i + constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 1;
		}
		
		//second layer
		int x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 150;
		for(int i = x - 20; i > 35; i = i - constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 1;
		}
		
		//third layer
		y = constants.SCREEN_Y - 300;
		for(int i = 100; i < constants.SCREEN_X - 50; i = i + constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 1;
		}
		
		//upper layer?
		x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 450;
		for(int i = x - 20; i > 50; i = i - constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 1;
		}
		
		
		//create ladders
		for(int i = 0; i < 11*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			GOList.add(new Ladder(500,612-i,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//Spawn Peach
		peach = new Peach(constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_HEIGHT,constants.PEACH_WIDTH);
		GOList.add(peach);
	}

	private void initMovingObjects() {
		gravityTimes = new ArrayList<Integer>();
		//initialize list
		MOList = new ArrayList<MovingObject>();
		//initialize player
		mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH, GOList);	
		//add possible initial barrels or flames
		//Barrel b = new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WEIGHT, GOList, true);
		//Barrel b2 = new Barrel(constants.BARREL_START_X + 50,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WEIGHT, GOList, true);
		//add objects to list of moving objects
		MOList.add(mario);		
		//MOList.add(b);
		//MOList.add(b2);
		
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
	
	
	//return functions for both object lists, which can be used by the controller to pass to the view 
	public ArrayList<GameObject> getGOList(){
		return GOList;
	}
	
	public ArrayList<MovingObject> getMOList(){
		return MOList;
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
