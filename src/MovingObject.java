import java.util.ArrayList;
import java.util.Random;

public abstract class MovingObject extends GameObject{
	//These values represent the velocity in the x and y plane
	protected float xVel;
	protected float yVel;
	//these values represent changes in x and y plane
	protected float dx;
	protected float dy;
	//This value is true if the moving object is colliding with another object
	protected boolean hasCollision = false;
	protected boolean killOnCollision;
	protected boolean isClimbing = false;
	protected boolean canClimb = false;
	protected boolean collidingWithPeach = false;
	protected boolean standing = false;
	protected float gravity = 2;
	protected boolean pointAwarded = true;
	protected boolean isKilled = false;
	protected Random actionSelector;
	
	//represents the action that the object can take
	protected int action;
	
	protected ArrayList<GameObject> GOList;
	
	public MovingObject(int x, int y, int h, int w) {
		super(x, y, h, w);	
		actionSelector = new Random();
		
	}
	
	public void act(int time){
		dy += gravity * time;	
	
		
	}
	
	//each subclass of this class implements its own version of the act, movement and collision
	//public abstract boolean checkCollisions(ArrayList<GameObject> GOList);
	public abstract boolean left();
	public abstract boolean right();
	public abstract boolean up();
	public abstract boolean down();	
	
	
	public void selectAction(){
		
	}

	public float getXVel(){
		return xVel;
	}
	public void setXVel(float x){
		xVel = x;
	}
	public float getYVel(){
		return xVel;
	}
	public void setYVel(float y){
		yVel = y;
	}
	public int getAction(){
		return action;
	}
	public void setAction(int a){
		action = a;
	}
		
	public void setPointAwarded(){
		pointAwarded = true;
	}
	public boolean pointAwarded(){
		return pointAwarded;
	}
} 
