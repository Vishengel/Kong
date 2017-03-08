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
	
	public GameModel(){
		initGame();
		gravityTimes = new ArrayList<Integer>();
		for(int i = 0; i < MOList.size(); i++){
			gravityTimes.add(i, 0);		
		}
		AbstractAction gravityTimer = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
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
				//System.out.println(gravityTimes);
				
				
			}
		};
		new Timer(150, gravityTimer).start();
	}
	
	
	//main game loop
	public void runGame(){
		System.out.println(gravityTimes);
		for(int i = 0; i < MOList.size(); i++){
				MOList.get(i).act(gravityTimes.get(i));
				//if player is hit, reset objects
				if(MOList.get(0).checkMOCollision(MOList)){
					System.out.println("MARIO IS DEAD!!!!!");
					MOList.clear();
					initMovingObjects();
				} 
				//If object falls out of the game screen, delete it
				if(MOList.get(i).getYPos() >= constants.SCREEN_Y){
					MOList.remove(i);
					gravityTimes.remove(i);
				}
		}
		setChanged();
		notifyObservers();
	
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
		
		for(int i = 0; i < 8*constants.LADDER_HEIGHT; i += constants.LADDER_HEIGHT){
			GOList.add(new Ladder(500,610-i,constants.LADDER_HEIGHT,constants.LADDER_WIDTH));
		}
		
		//bottom layer second half 
		int y = constants.SCREEN_Y - 50;
		for(int i = constants.SCREEN_X /2; i < constants.SCREEN_X - 50; i = i + constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 2;
		}
		
		//second layer
		int x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 150;
		for(int i = x; i > 50; i = i - constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 2;
		}
		
		//third layer
		y = constants.SCREEN_Y - 300;
		for(int i = 100; i < constants.SCREEN_X - 50; i = i + constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 2;
		}
		
		//upper layer?
		x = constants.SCREEN_X - 100;
		y = constants.SCREEN_Y - 450;
		for(int i = x; i > 50; i = i - constants.platform_WIDTH){
			GOList.add(new Platform(i,y,constants.platform_HEIGHT,constants.platform_WIDTH));
			y = y - 2;
		}
		
		
		//create ladders
		
		
	}

	private void initMovingObjects() {
		//initialize list
		MOList = new ArrayList<MovingObject>();
		//initialize player
		mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH, GOList);	
		//add possible initial barrels or flames
		Barrel b = new Barrel(constants.BARREL_START_X,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WEIGHT, GOList, true);
		Barrel b2 = new Barrel(constants.BARREL_START_X + 50,constants.BARREL_START_Y,constants.BARREL_HEIGHT,constants.BARREL_WEIGHT, GOList, true);
		//add objects to list of moving objects
		MOList.add(mario);		
		MOList.add(b);
		MOList.add(b2);
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
	
	

	
	
	
}
