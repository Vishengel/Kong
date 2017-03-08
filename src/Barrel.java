import java.util.ArrayList;

public class Barrel extends MovingObject{
	//false : left   true : right
	private boolean direction;
	private boolean moveDownLadder;
	private int distanceFallen = 0;
	
	public Barrel(int x, int y, int h, int w, ArrayList<GameObject> GOList, boolean d) {
		super(x, y, h, w, GOList);
		symbol = 'O';
		killOnCollision = true;
		direction = false;
		xVel = 6;
		color = color.orange;
		
	}
	
	public void act(int time) {
		super.act(time);
		
		//if falling for longer than 2 time units, change direction
		if(distanceFallen > 50 && standing()){
			direction = !direction;
			//System.out.println(direction);
			}
		//System.out.println(distanceFallen);
		
		if(standing()){
			//reset distance fallen
			distanceFallen = 0;
			
			if(direction){
				dx = xVel;
			}
			else{
				dx = -xVel;
			}
		}
		else{
			dx = 0f;
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
