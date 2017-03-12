import java.util.ArrayList;

public class Barrel extends MovingObject{
	//false : left   true : right
	private boolean direction;
	private boolean moveDownLadder;
	//keep track of the distance fallen in order to change direction 
	private int distanceFallen = 0;
	
	public Barrel(int x, int y, int h, int w, boolean d, boolean falling) {
		super(x, y, h, w);
		symbol = 'O';
		killOnCollision = false;
		direction = true;
		xVel = 2.2f;
		yVel = 3;
		color = color.orange;
		pointAwarded = false;
		name = "barrel";
		this.falling = falling;
	}
	
	public void act(int time) {
		dx = 0;
		dy = 0;
		
		if(!falling) {
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
			
			
			//If barrel is on a ladder, 25 % to fall down ladder
			if(canClimb){
				if(actionSelector.nextInt(4) >= 2){
					isClimbing = true;
				}
			} 
			
			if(isClimbing){
				//dx = 0;
				dy += yVel;
			}
			super.act(time);
			distanceFallen += dy;
		} else {
			//Placeholder constant falling velocity
			//The falling barrel ignores gravity
			dy += 3;
		}
		
		xPos += dx;
		yPos += dy;
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
