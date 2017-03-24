import java.util.ArrayList;
import java.util.Random;

public abstract class MovingObject extends GameObject{
	//These values represent the velocity in the x and y plane
	protected float xVel;
	protected float yVel;
	//These values represent changes in x and y plane
	protected float dx;
	protected float dy;
	//The x-cordinates of barrels and the player snap to a ladder when climbing
	//We therefore tell moving objects the x-coordinates of ladders they are colliding with
	protected float ladderXPos;
	protected float gravity = 0.12f;
	//This value is true if the moving object is colliding with another object
	protected boolean hasCollision = false;
	protected boolean killOnCollision;
	protected boolean isClimbing = false;
	protected boolean canClimb = false;
	protected boolean collidingWithPeach = false;
	protected boolean standing = false;
	protected boolean falling = false;
	protected boolean pointAwarded = true;
	protected boolean isKilled = false;
	protected boolean firstCanClimb = true;
	protected boolean collidingWithTop;
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
	
	public void setCanClimb(boolean canClimb) {
		this.canClimb = canClimb;
	}
	
	public boolean isFalling() {
		return falling;
	}
	
	public void setLadderXPos(float ladderXPos) {
		this.ladderXPos = ladderXPos;
	}
	
	public boolean getStanding() {
		return standing;
	}
	
	public void setStanding(boolean standing) {
		this.standing = standing;
	}
	
	public boolean getCanClimb() {
		return canClimb;
	}
	
	public boolean getIsClimbing() {
		return isClimbing;
	}
	
	public void setIsClimbing(boolean isClimbing) {
		this.isClimbing = isClimbing;
	}
	
	public void setFirstCanClimb(boolean firstCanClimb) {
		this.firstCanClimb = firstCanClimb;
	}
	
	public boolean getCollidingWithTop() {
		return collidingWithTop;
	}
	
	public void setCollidingWithTop(boolean collidingWithTop) {
		this.collidingWithTop = collidingWithTop;
	}
} 
