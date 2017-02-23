import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;

public class GameModel extends Observable implements constants {
	private int score = 0;
	private int lives = 3;
	private boolean gameOver = false; 
	private ArrayList<GameObject> GOList;
	private ArrayList<MovingObject> MOList;
	private Player mario;
	
	public GameModel(){
		initGame();
	}
	
	
	//main game loop
	void runGame(){
		for(MovingObject mo : MOList){
				mo.act();
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
		//Platform pl = new Platform(30,48,constants.platform_HEIGHT,constants.platform_WIDTH);
		//Platform pl2 = new Platform(32,47,constants.platform_HEIGHT,constants.platform_WIDTH);
		//Platform pl3 = new Platform(34,46,constants.platform_HEIGHT,constants.platform_WIDTH);

		//create ladders
		
		//add objects to list of game objects
		//GOList.add(pl);
		//GOList.add(pl2);
		//GOList.add(pl3);
	}

	private void initMovingObjects() {
		//initialize list
		MOList = new ArrayList<MovingObject>();
		//initialize player
		mario = new Player(constants.PLAYER_START_X,constants.PLAYER_START_Y,constants.PLAYER_HEIGHT,constants.PLAYER_WIDTH);	
		//add possible initial barrels or flames
		//Barrel b = new Barrel(15,48,2,2, 0);
		//add objects to list of moving objects
		MOList.add(mario);		
		//MOList.add(b);
	}
	
	public void setPlayerAction(int action){
		mario.setAction(action);
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
