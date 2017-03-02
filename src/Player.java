import java.awt.Color;

public class Player extends MovingObject{
	private boolean hasPowerUp = false, goLeft, goRight;
	private boolean keysDown[] = new boolean[255];	
    
	public Player(int x, int y, int h, int w) {
		super(x, y, h, w);
		xVel = 5;
		yVel = -5;
		killOnCollision = false;
		color = Color.red;
		action = -1;
	}
	
	public void act(){
		goLeft = keysDown[65];
		goRight = keysDown[68];
		dx = 0;
		dy = 0;
		
		dx += (goRight ? xVel : 0.0) - (goLeft ? xVel : 0.0);
		dy = 0;
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
	}
	
	public void setKeysDown(boolean[] down) {
		keysDown = down;
	}
}
