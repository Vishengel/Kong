import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class GameModel extends Observable implements constants {
	private int score = 0;
	private int lives = 3;
	private ArrayList<Integer> gravityTimes;
	private boolean gameOver = false; 
	private ArrayList<GameObject> GOList;
	private ArrayList<MovingObject> MOList;
	private Player mario;
	private int timer = 0;
	protected int spawnTime = 2;
	protected int spawnTimer = 0;
	
	
	
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
			if(MO.standing()){
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
		while(true){
		timer++;
		//handle gravity every 150 milliseconds
		if(timer % 10 == 0){
			incrementTime();	
		}
		//spawn a barrel every 450 milliseconds
		if(timer % 30 == 0){
			spawnBarrel();
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
				//if player is hit, reset objects and subtract a life
				if(mario.checkMOCollision(MOList)){
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

	private void initObjects() {
		//initialize list
		GOList = new ArrayList<GameObject>();
		
		//create platforms		
		//GOList.add(new Platform(50-constants.platform_WIDTH,580-constants.platform_HEIGHT,constants.platform_HEIGHT,constants.platform_WIDTH));
		
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
