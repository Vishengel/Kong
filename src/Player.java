import java.awt.Color;
import java.util.ArrayList;

public class Player extends MovingObject{
	private boolean hasPowerUp = false, isClimbing = false, goLeft, goRight, goUp, goDown, jump;
	private boolean keysDown[] = new boolean[255];	
    
	public Player(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w, GOList);
		xVel = 5;
		yVel = -5;
		killOnCollision = false;
		color = Color.blue;
		action = -1;
	}
	
	public void act(){
		// There are two control schemes: WASD + shift and arrows + space bar
		goLeft = keysDown[65] || keysDown[37];
		goRight = keysDown[68] || keysDown[39];
		//goUp and goDown will be implemented once ladders have been implemented as well
		goUp = keysDown[87] || keysDown[38];
		goDown = keysDown[83] || keysDown[40];
		jump = keysDown[16] || keysDown[32];
				
		dx = 0;
		dy = 0;
		
		dx += (goRight ? xVel : 0.0) - (goLeft ? xVel : 0.0);
		
		if (!isClimbing) {
			// Temporary: only moves up
			dy += (jump ? yVel : 0.0);
		} 
		
		/*
		
		
		// 1: move right  0: move left 
		switch(action){
		case 1:
			dx = -xVel;
			break;
		case 2:
			dx = xVel;
			break;
		case 3:
			dy = yVel;
		case 4:
			break;
		}
		*/
		xPos += dx;
		yPos += dy;
		
		// If the next move would make the player collide with any other object,
		// do not make the move
		if(checkCollisions(GOList)) {
			xPos -= dx;
			yPos -= dy;
		}
	}
	
	public boolean checkCollisions(ArrayList<GameObject> GOList) {
		for(GameObject GO : GOList) {
			// Store the left side, right side, top and bottom coordinates of the player
			int l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			// Store the left side, right side, top and bottom coordinates of the other object
			// Only works for two rectangular objects
			int l2 = GO.xPos, r2 = GO.xPos+GO.width, t2 = GO.yPos, b2 = GO.yPos+GO.height;
			if (!(l1>=r2 || l2>=r1 || t1>=b2 || t2>=b1)) {
				System.out.println("Collision");
				return true;
			} else {
				System.out.println("No collision");
			}
		}
		// The player is not in collision with any other object
		return false;
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
