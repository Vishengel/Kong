import java.util.ArrayList;

public class Flame extends MovingObject{
	private boolean direction;

	public Flame(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w, GOList);
		killOnCollision = true;
		name = "flame";
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
		return false;
	}
	
	public boolean jump() {
		return false;
	}
}
