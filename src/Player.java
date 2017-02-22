public class Player extends MovingObject{
	private boolean hasPowerUp;
    
	public Player(int x, int y, int h, int w) {
		super(x, y, h, w);
		symbol = 'X';
		killOnCollision = false;
		hasPowerUp = false;		
		action = -1;
	}
	
	public void act(){
		dx = 0;
		dy = 0;
		// 1: move right  0: move left 
		switch(action){
		case 0:
			dx = 1;
			break;
		case 1:
			dx = -1;
			break;
		}
		xPos += dx;
		yPos += dy;
		
	}

}
