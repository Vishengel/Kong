import java.awt.Color;

public class Player extends MovingObject{
	private boolean hasPowerUp;
    
	public Player(int x, int y, int h, int w) {
		super(x, y, h, w);
		symbol = 'X';
		xVel = 10;
		yVel = -5;
		killOnCollision = false;
		hasPowerUp = false;		
		action = -1;
		color = Color.red;
	}
	
	public void act(){
		dx = 0;
		dy = 0;
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
		xPos += dx;
		yPos += dy;
		
	}

}
