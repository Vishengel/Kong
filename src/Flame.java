import java.util.ArrayList;

public class Flame extends MovingObject{
	//false : left   true : right
	private boolean direction = true;

	public Flame(int x, int y, int h, int w) {
		super(x, y, h, w);
		//killOnCollision = true;
		name = "flame";
		
		//killOnCollision = false;
		direction = true;
		xVel = 1f;
		yVel = 1f;
	}
	
	public void act(int time) {
		dx = 0;
		dy = 0;
		
		dx += (direction ? xVel : -xVel);
		
		//super.act(time);
		super.act();
		
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
		return false;
	}
	
	public boolean jump() {
		return false;
	}
	
	public void setDirection(float playerX, float playerY){
		//If the player is to the right of the flame, the flame moves to the right as well
		if(playerX > xPos) {
			direction = true;
		} else {
			direction = false;
		}
	}
}