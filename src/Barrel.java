import java.util.ArrayList;

public class Barrel extends MovingObject{
	//false : left   true : right
	private boolean direction;
	private boolean moveDownLadder;
	//keep track of the distance fallen in order to change direction 
	private int distanceFallen = 0;
	
	public Barrel(int x, int y, int h, int w, boolean d) {
		super(x, y, h, w);
		symbol = 'O';
		killOnCollision = false;
		direction = true;
		xVel = 1.6f;
		yVel = 3;
		color = color.orange;
		pointAwarded = false;
		name = "barrel";
	}
	
	public void act(int time) {
		dx = 0;
		dy = 0;
		super.act(time);
			
		//If barrel is on a ladder, 25 % to fall down ladder
		
		//if falling for longer than 2 time units, change direction
		if(distanceFallen > 30 && standing){
			direction = !direction;
			//System.out.println(direction);
			}
		//System.out.println(distanceFallen);
		
		if(standing){
			//reset distance fallen
			distanceFallen = 0;
			
			if(direction){
				dx += xVel;
			}
			else{
				dx += -xVel;
			}
		}
		else{
			//Only let a barrel pause in its horizontal movement if it falls a long distance
			if(distanceFallen > 3){
				dx = 0f;
			}
		}
		
		
		
		if(canClimb){
			if(actionSelector.nextInt(4) == 0){
				dx = 0;
				dy += yVel;
			}
		} 
		
		
		xPos += dx;
		yPos += dy;
		distanceFallen += dy;
	}
	
	public boolean checkCollisions(ArrayList<GameObject> GOList) {
		return false;
	}
	
	public boolean left() {
		return !direction;
	}
	
	public boolean right() {
		return direction;
	}
	
	public boolean up() {
		return false;
	}
	
	public boolean down() {
		return moveDownLadder;
	}
	
	public boolean jump() {
		return false;
	}
	

}
