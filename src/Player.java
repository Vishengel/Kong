import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class Player extends MovingObject{

	private boolean hasPowerUp = false, isClimbing = false, goLeft, goRight, goUp, goDown, jump;
	private boolean keysDown[] = new boolean[255];	 
	private boolean jumping;
	private float jumpHeight = 4;
    private float time;
    
	public Player(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w, GOList);

		xVel = 5f;
		yVel = 0;

		killOnCollision = false;
		color = Color.blue;
		action = -1;
		time = 0;
		AbstractAction gravityTimer = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				System.out.println(time);
				if(standing()){
					time = 0;
					jumping = false;
				}
				else{
					time++;
				}
			}
		};
		new Timer(150, gravityTimer).start();
		
		
	}
	
	
	public void act(){

		readInput();
		
		dx = 0;
		dy = 0;
		//if the jump key is down and the player is currently standing on a platform and not
		// already jumping, start jumping
		if(jump && !jumping && standing()){
			jumping = true;				
		}
		
		
		dx += (goRight ? xVel : 0) - (goLeft ? xVel : 0);
		
		/*
		if (isClimbing) {
			dy += (goDown ? yVel : 0) - (goUp ? yVel : 0);
		}
		
		//The player can only jump while not climbing a ladder and while standing on an object
		if (!isClimbing && isStanding) {
			// Temporary: only moves up
			dy += (jump ? yVel : 0);
		} 
		*/
		dy = gravity * time;



		//apply vertical force if jumping
		if(jumping){ 
			yPos += -jumpHeight;
		}
		
		
		if(standing()){
			dy = 0;
			jumping = false;
		}
		
		xPos += dx;
		
		if(checkCollisions(GOList)) {
			// If the movement on the x-axis would result in a collision, we do not move
			xPos -= dx;
		}
		
		// Next, we try to move on the y-axis
		yPos += dy;
		
		if(checkCollisions(GOList)) {
			// If the movement on the y-axis would result in a collision, we do not move
			yPos -= dy;
		}

			
		// If the next move would make the player collide with any other object,
		// do not make the move
		if(checkCollisions(GOList)) {
			xPos -= dx;
			//yPos -= dy;
		} 	
		
	}
	
	public boolean checkCollisions(ArrayList<GameObject> GOList) {
		for(GameObject GO : GOList) {
			// Store the left side, right side, top and bottom coordinates of the player
			float l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			// Store the left side, right side, top and bottom coordinates of the other object
			// Only works for two rectangular objects

			float l2 = GO.xPos, r2 = GO.xPos+GO.width, t2 = GO.yPos, b2 = GO.yPos+GO.height;
			//System.out.println(b1);
			//System.out.println(b2);
			if (!(l1>=r2 || l2>=r1 || t1>=b2 || t2>=b1) && t2 < b1 && b1 > b2) {

				System.out.println("Collision");
				return true;
			} else {
				//System.out.println("No collision");
			}
		}
		// The player is not in collision with any other object
		return false;
	}
	
	public void readInput() {
		// There are two control schemes: WASD + shift and arrows + space bar
		goLeft = keysDown[65] || keysDown[37];
		goRight = keysDown[68] || keysDown[39];
		//goUp and goDown will be implemented once ladders have been implemented as well
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
	
}
