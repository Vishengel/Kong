import java.util.ArrayList;

public class Barrel extends MovingObject{
	private boolean moveDownLadder;
	//keep track of the distance fallen in order to change direction 
	private int distanceFallen = 0;
	
	public Barrel(float x, float y, int h, int w, int action) {
		super(x, y, h, w);
		this.action = action;
		xVel = 1f;
		yVel = 1f;
		pointAwarded = false;
		name = "barrel";
	}
	
	public Barrel(MovingObject MO) {
		super(MO);
		this.action = action;
		xVel = 1.5f;
		yVel = 1.7f;
		pointAwarded = false;
		name = "barrel";
	}
	
	/*actions: 
	0 : move left
	1 : move right
	*/
	
	
	
	public void act() {
		dx = 0;
		dy = 0;
		
		//if falling for longer than 25 pixels in the vertical direction, change horizontal direction
		if(distanceFallen > 25 && standing){
			action = 1 - action;
		}
			
		//Only move a barrel in the horizontal direction if it is standing on a platform
		if(standing){
			distanceFallen = 0;
			
			if(action == 1){
				dx += xVel;
			}
			else{
				dx += -xVel;
			}

		
			//Only let a barrel pause in its horizontal movement if it falls a long distance
			if(distanceFallen > 3){
				dx = 0f;
			}
			
			

			//If barrel is on a ladder, 50% chance to fall down ladder
			if(canClimb && firstCanClimb && !collidingWithTop){
				//System.out.println(collidingWithTop);
				firstCanClimb = false;
				
				if(random.nextInt(4) >= 2){
					//System.out.println(++i);
					isClimbing = true;
					standing = false;
				}
				//dy += yVel;
			} 
			
			if(isClimbing){
				//System.out.println("Climbing");
				dx = 0;
				xPos = ladderXPos + constants.LADDER_WIDTH / 2 - constants.BARREL_WIDTH / 2;
				dy += yVel;
			}
			super.act();
			distanceFallen += dy;
		} 

		
		
			
		//If barrel is on a ladder, 50% chance to fall down ladder
		if(canClimb && firstCanClimb && !collidingWithTop){
			firstCanClimb = false;
			if(random.nextInt(4) >= 2){
				isClimbing = true;
				standing = false;
			}
		} 
		
		//If a barrel is rolling down a ladder, snap its x-position to the middle of the ladder.
		if(isClimbing){
			dx = 0;
			xPos = ladderXPos + constants.LADDER_WIDTH / 2 - constants.BARREL_WIDTH / 2;
			dy += yVel;
		}
		super.act();
		
		//increment the distance fallen
		distanceFallen += dy;
	} 
	
	
	
	public boolean left() {
		return action == 0;
	}
	
	public boolean right() {
		return action == 1;
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
