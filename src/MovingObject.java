import java.util.ArrayList;

public abstract class MovingObject extends GameObject{
	//These values represent the velocity in the x and y plane
	protected int xVel;
	protected int yVel;
	//these values represent changes in x and y plane
	protected int dx;
	protected int dy;
	//This value is true if the moving object is colliding with another object
	protected boolean hasCollision = false;
	protected boolean killOnCollision;
	
	//represents the action that the object can take
	protected int action;
	
	protected ArrayList<GameObject> GOList;
	
	public MovingObject(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w);	
		this.GOList = GOList;
	}
	
	//each subclass of this class implements its own version of the act, movement and collision
	public abstract void act();
	public abstract boolean checkCollisions(ArrayList<GameObject> GOList);
	public abstract boolean left();
	public abstract boolean right();
	public abstract boolean up();
	public abstract boolean down();	
	
	public int getXVel(){
		return xVel;
	}
	public void setXVel(int x){
		xVel = x;
	}
	public int getYVel(){
		return xVel;
	}
	public void setYVel(int y){
		yVel = y;
	}
	public int getAction(){
		return action;
	}
	public void setAction(int a){
		action = a;
	}
	
	
}
