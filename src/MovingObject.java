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
	protected boolean isClimbing = false;
	protected boolean collidingWithPeach = false;
	
	protected GameObject collidingWithLadder;
	
	protected float gravity = 2;
	protected boolean pointAwarded = true;
	
	
	//represents the action that the object can take
	protected int action;
	
	protected ArrayList<GameObject> GOList;
	
	public MovingObject(int x, int y, int h, int w, ArrayList<GameObject> GOList) {
		super(x, y, h, w);	
		this.GOList = GOList;
		
	}
	
	public void act(int time){
		if (standing()) {
			dy = 0;
		}
		if (!isClimbing) {
			dy += gravity * time;
		}
	}
	
	//each subclass of this class implements its own version of the act, movement and collision
	//public abstract boolean checkCollisions(ArrayList<GameObject> GOList);
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
		for(GameObject GO : GOList){
			if (!(GO instanceof Platform)) {
				continue;
			}
			float l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			float l2 = GO.xPos, r2 = GO.xPos+GO.width, t2 = GO.yPos, b2 = GO.yPos+GO.height;
			if((b1 <= b2 && b1 >= t2) && r1 > l2 && l1 < r2 && GO.isSolid()){ 
				//make object stand exactly on top of the platform 
				yPos = t2 - height;
				//System.out.println("Standing on platform!");
				
				return true;
			}
			
		}
		//System.out.println("Not standing..");
		
		return false;
		
	}
	
	
	
	public boolean checkWallCollisions(ArrayList<GameObject> GOList) {
		for(GameObject GO : GOList) {
			// Store the left side, right side, top and bottom coordinates of the player
			float l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			// Store the left side, right side, top and bottom coordinates of the other object
			// Only works for two rectangular objects

			float l2 = GO.xPos, r2 = GO.xPos+GO.width, t2 = GO.yPos, b2 = GO.yPos+GO.height;
			//System.out.println(b1);
			//System.out.println(b2);
			if (!(l1>=r2 || l2>=r1 || t1>=b2 || t2>=b1) && t2 < b1 && b1 > b2) {
				if (GO instanceof Ladder) {
					collidingWithLadder = GO;
				}
				if (GO instanceof Peach) {
					collidingWithPeach = true;
				}
				//System.out.println("Collision");
				return true;
			}
		}	
		// The player is not in collision with a ladder
		collidingWithLadder = null;
		collidingWithPeach = false;
		
		return false;
	}
	
	public boolean checkMOCollision(ArrayList<MovingObject> MOList) {
		for(int i = 1; i < MOList.size(); i++) {
			MovingObject MO = MOList.get(i);
			// Store the left side, right side, top and bottom coordinates of the player
			float l1 = xPos, r1 = xPos+width, t1 = yPos, b1 = yPos+height;
			// Store the left side, right side, top and bottom coordinates of the other object
			// Only works for two rectangular objects

			float l2 = MO.xPos, r2 = MO.xPos+MO.width, t2 = MO.yPos, b2 = MO.yPos+MO.height;
			//System.out.println(b1);
			//System.out.println(b2);
			if (!(l1>=r2 || l2>=r1 || t1>=b2 || t2>=b1)) {
				return MO.killOnCollision;
			}	
		}
		// The player is not in collision with any other object
		return false;
		
	}
	public void setPointAwarded(){
		pointAwarded = true;
	}
	public boolean pointAwarded(){
		return pointAwarded;
	}
} 
