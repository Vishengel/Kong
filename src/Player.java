import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class Player extends MovingObject{

	private boolean goLeft, goRight, goUp, goDown, jump;
	private boolean keysDown[] = new boolean[255];	 
	private boolean jumping = false;
	private float jumpHeight = 2.6f;
	private boolean hasWon = false;
	private boolean isKilled = false;
	private boolean isPoweredUp = false;
	
    
	public Player(float x, float y, int h, int w) {
		super(x, y, h, w);
		xVel = 1.5f;
		yVel = 1.8f; 
		name = "player";	
	}
	
	
	/*actions: 
	0 : stand still
	1 : left
	2 : right
	3 : climb up
	4 : climb down
	5 : jump left
	6 : jump right
	*/
	
	//This function is only for human players, where action is selected based on keyboard input
	public void selectAction(){		
		if(!jumping){
			if(goLeft){
				action = 1;
			}
			if(goRight){
				action = 2;
			}
			if(goUp){
				action = 3;
			}
			if(goDown){
				action = 4;
			}
			if(jump && goLeft){
				action = 5;
			}
			if(jump && goRight){
				action = 6;
			}
		}		
	}
	
public void move(){
		switch(action){
		//move left
		case 1:
			dx += -xVel;	
			break;
		//move right
		case 2: 
			dx += xVel;
			break;
		case 3:
			// We can move up if:
			// -Mario is colliding with a ladder (canClimb)
			// -Mario is not jumping
			// -Mario is either standing below the ladder or already climbing
			if(canClimb && !jumping && (collidingWithTop || isClimbing)){
				isClimbing = true;
				dy -= yVel/2;
			}
			break;
		//move down
		case 4:
			// We can move down if:
			// -Mario is colliding with a ladder (canClimb)
			// -Mario is not jumping
			// -Mario is either standing above the ladder or already climbing
			if(canClimb && !jumping && (!collidingWithTop || isClimbing)){
				isClimbing = true;
				dy += yVel/2;
			}
			break;
		//jumping is only possible if standing
		//jump left
		case 5:
			jumping = true;
			dx += -xVel;
			break;
		//jump right
		case 6:
			jumping = true;
			dx += xVel;
			break;
		}
	} 
	
	public void act(){
		dx = 0;
		dy = 0;
		
		//reset action for smoother player control when not controlled by AI
		if(!jumping && !constants.TEST_PHASE){
			action = 0;
		}
		
		readInput();
		selectAction();
		move();
		
		//System.out.println(action);
		
		//prevent jumping while climbing a ladder
		if(isClimbing){
			jumping = false;
			dx = 0;
			xPos = ladderXPos + constants.LADDER_WIDTH / 2 - constants.PLAYER_WIDTH / 2;
		}
		
		//Elevate player if jumping
		if(jumping){
			dy += -jumpHeight;
		}
		
			
		super.act();
		
		
		//prevent player from walking out of the screen
		if(xPos <= 0){
			xPos = 0;
		}
		if(xPos >= constants.SCREEN_X - 30){
			xPos = constants.SCREEN_X - 30;
		}
		if(xPos <= 170 && yPos <= 260){
			xPos = 170;
		}
		
	}
	
	
	//Read keyboard input
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


	public boolean isClimbing() {	
		return isClimbing;
	}


	public boolean isJumping() {
		return jumping;
	}
	
	public void setPoweredUp(boolean isPoweredUp){
		this.isPoweredUp = isPoweredUp;
	}
	public boolean isPoweredUp(){
		return isPoweredUp;
	}
	
}
