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
	
    
	public Player(int x, int y, int h, int w) {
		super(x, y, h, w);

		xVel = 2f;
		yVel = 2f;

		killOnCollision = false;
		name = "player";
		
	}
	
	
	//actions: 
	//0 : left
	//1 : right
	//2 : up
	//3 : down
	public void selectAction(){
				
		if(!jumping){
			if(goLeft){
				action = 0;
			}
			if(goRight){
				action = 1;
			}
			if(goUp){
				action = 2;
			}
			if(goDown){
				action = 3;
			}
			if(jump){
				action = 4;
			}
			if(jump && goLeft){
				action = 5;
			}
			if(jump && goRight){
				action = 6;
			}
			//random mario behavior
			if(constants.AI_MARIO){
				action = actionSelector.nextInt(7);
			}
		}
		
		
		
		
		
	}
	
public void move(){
		
		switch(action){
		//don't allow vertical movement when climbing
		//move left
		case 0:
			if(!isClimbing){
				dx += -xVel;
			}
			break;
		//move right
		case 1: 
			if(!isClimbing){
				dx += xVel;
			}
			break;
		//move up
		case 2:
			if(canClimb && !jumping){
				isClimbing = true;
				dy -= yVel/2;
			}
			break;
		//move down
		case 3:
			if(canClimb && !jumping){
				isClimbing = true;
				dy += yVel/2;
			}
			break;
		//jump up
		case 4:
			jumping = true;
			break;
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
	
	public void act(int time){
		//System.out.println("Standing: " + standing);
		dx = 0;
		dy = 0;
		//reset action
		if(!jumping){
			action = -1;
		}
		
		
		readInput();
		selectAction();
		move();
		
		System.out.println(action);
		
		
		//prevent jumping while climbing a ladder
		if(isClimbing){
			jumping = false;
			dx = 0;
			xPos = ladderXPos + constants.LADDER_WIDTH / 2 - constants.PLAYER_WIDTH / 2;
		}

		if(jumping){
			dy += -jumpHeight;
		}
			
		super.act(time);
		
		//System.out.println(dy);
		
		xPos += dx;
		yPos += dy;
		
		//prevent player from walking out of the screen
		if(xPos <= 0){
			xPos = 0;
		}
		if(xPos >= constants.SCREEN_X - 30){
			xPos = constants.SCREEN_X - 30;
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
