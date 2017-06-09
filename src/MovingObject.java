import java.util.Random;

//This abstract class is the super class of all the moving objects present in the game
public abstract class MovingObject extends GameObject{
	//These values represent the velocity in the x and y plane
	protected float xVel;
	protected float yVel;
	//These values represent changes in x and y plane
	protected float dx;
	protected float dy;
	//The x-coordinates of barrels and the player snap to a ladder when climbing
	//We therefore tell moving objects the x-coordinates of ladders they are colliding with
	protected float ladderXPos;
	//this value determines how long an object has been in the air; this value is sued to calculate the strength
	//of the gravity pulling the object back to the ground
	protected int time = 0;
	protected float gravity = 0.12f;
	protected boolean isClimbing = false;
	protected boolean canClimb = false;
	protected boolean standing = false;
	protected boolean jumping = false;
	protected boolean falling = false;
	protected boolean pointAwarded = true;
	protected boolean isKilled = false;
	protected boolean firstCanClimb = true;
	protected boolean collidingWithTop;
	protected boolean hasWon = false;
	protected Random random;
	
	//represents the action that the object can take
	protected int action;
	
	public MovingObject(int x, int y, int h, int w) {
		super(x, y, h, w);	
		random = new Random();
	}
	
	public MovingObject(MovingObject MO) {
		super(MO);
		this.isClimbing = MO.isClimbing();
		this.canClimb = MO.getCanClimb();
		this.standing = MO.getStanding();
		this.falling = MO.getFalling();
		this.isKilled = MO.getIsKilled();
	}
	
	public void act(){
		dy += gravity * time;	
		xPos += dx;
		yPos += dy;
	}
	
	//each subclass of this class implements its own version of the act, movement and collision
	public abstract boolean left();
	public abstract boolean right();
	public abstract boolean up();
	public abstract boolean down();	
	
	public void selectAction(){
		
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	public int getTime(){
		return time;
	}

	public float getXVel(){
		return xVel;
	}
	
	public void setXVel(float xVel){
		this.xVel = xVel;
	}
	
	public float getYVel(){
		return yVel;
	}
	
	public void setYVel(float yVel){
		this.yVel = yVel;
	}
	
	public int getAction(){
		return action;
	}
	
	public void setAction(int action){
		this.action = action;
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
	
	public boolean getIsKilled() {
		return isKilled;
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
	
	public boolean isJumping() {
		return jumping;
	}
	
	public void setJump(boolean b){
		jumping = b;
	}
	
	public boolean hasWon(){
		return hasWon;
	}
	
	public boolean getCanClimb() {
		return canClimb;
	}
	
	public boolean isClimbing() {
		return isClimbing;
	}
	
	public void setIsClimbing(boolean isClimbing) {
		this.isClimbing = isClimbing;
	}
	
	public boolean getFalling() {
		return falling;
	}
	
	public void setFirstCanClimb(boolean firstCanClimb) {
		this.firstCanClimb = firstCanClimb;
	}
	
	public boolean isKilled(){
		return isKilled;
	}
	
	public boolean getCollidingWithTop() {
		return collidingWithTop;
	}
	
	public void setCollidingWithTop(boolean collidingWithTop) {
		this.collidingWithTop = collidingWithTop;
	}
} 
