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

		xVel = 1.5f;
		yVel = 1.5f;

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
			//random mario behavior
			/*if(constants.AI_MARIO){
				if(random.nextInt(100) == 0){
					action = random.nextInt(6);
				}
			}
			*/
		}
		
		
		
		
		
	}
	
public void move(){
		
		switch(action){
		//don't allow vertical movement when climbing
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
		
		//reset action for smoother player control when not controlled by AI
		if(!jumping && !constants.TEST_PHASE_DODGING && !constants.TEST_PHASE_CLIMBING){
			action = 0;
		}
		
		/*if(standing && !jumping){
			action = 0;
		}*/
		
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
		
			
		super.act(time);
		
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


	public boolean isClimbing() {	
		return isClimbing;
	}


	public boolean isJumping() {
		return jumping;
	}
	
}
