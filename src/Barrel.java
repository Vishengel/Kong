import java.util.ArrayList;

public class Barrel extends MovingObject{
	//false : left   true : right
	private boolean direction;
	private boolean moveDownLadder;
	
	public Barrel(int x, int y, int h, int w, ArrayList<GameObject> GOList, boolean d) {
		super(x, y, h, w, GOList);
		symbol = 'O';
		killOnCollision = true;
		direction = d;
	}
	
	public void act() {
		
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
