import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class Player extends MovingObject{

	private boolean hasPowerUp = false, goLeft, goRight, goUp, goDown, jump;
	private boolean keysDown[] = new boolean[255];	 
	private boolean jumping;
	private float jumpHeight = 3.6f;
	private boolean hasWon = false;
	private boolean isKilled = false;
	
    
	public Player(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w, GOList);

		xVel = 5f;
		yVel = 5f;

		killOnCollision = false;
		color = Color.blue;
		action = -1;
		symbol = 'x';
		
		name = "player";
	}
	
	public void act(int time){
		dx = 0;
		dy = 0;
		System.out.println("Standing on Ladder:" + isClimbing);
		System.out.println("Standing on platform: " + standing);
		//call the super act function for gravity and standing on platform
		super.act(time);
		readInput();
		
		
		//if the jump key is down and the player is currently standing on a platform and not
		// already jumping, start jumping
		if(jump && !jumping && standing){
			jumping = true;	
		}
		if(standing) {
			isClimbing = false;
		}
		
		dx += (goRight ? xVel : 0) - (goLeft ? xVel : 0);
		
		if (!isClimbing && canClimb && (goUp || goDown)) {
			isClimbing = true;
		}
		
		if (isClimbing) {
			dy += (goDown ? yVel/2 : 0) - (goUp ? yVel/2 : 0);
		}

		//apply vertical force if jumping
		if(jumping){ 
			dy += -jumpHeight;
			//out.println(dy);
		}
		
		xPos += dx;
		yPos += dy;	
		
		//If Mario is in collision with Peach, the game is over
		if (collidingWithPeach) {
			hasWon = true;
		}
		
	}
	
	
	
	public void readInput() {
		// There are two control schemes: WASD + shift and arrows + space bar
		goLeft = keysDown[65] || keysDown[37];
		goRight = keysDown[68] || keysDown[39];
		goUp = keysDown[87] || keysDown[38];
		goDown = keysDown[83] || keysDown[40];
		jump = keysDown[16] || keysDown[32];
	}
	
	public void setKeysDown(boolean[] down) {
		keysDown = down;
	}
	
	public boolean left() {
		return goLeft;
	}
	
	public boolean right() {
		return goRight;
	}
	
	public boolean up() {
		return goUp;
	}
	
	public boolean down() {
		return goDown;
	}
	
	public boolean jump() {
		return jump;
	}
	public void setJump(boolean b){
		jumping = b;
	}
	
	public boolean hasWon(){
		return hasWon;
	}
	
	public boolean isKilled(){
		return isKilled;
	}
	
}
