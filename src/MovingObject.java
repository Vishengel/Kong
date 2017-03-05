import java.util.ArrayList;

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
	
	protected float gravity = 2;
	
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
	//check if object is standing on a platform
	public boolean standing(){
		for(GameObject GO :GOList){
			float l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			float l2 = GO.xPos, r2 = GO.xPos+GO.width, t2 = GO.yPos, b2 = GO.yPos+GO.height;
			if((b1 <= b2 && b1 >= t2) && r1 > l2 && l1 < r2){ 
				//make object stand exactly on top of the platform 
				yPos = t2 - height;
				//System.out.println("Standing on platform!");
				return true;
			}
			
		}
		System.out.println("Not standing..");
		return false;
		
	}
	
} 
