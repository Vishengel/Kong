import java.awt.Color;

public class Platform extends GameObject{
	private boolean hasLadder = false;
	
	public Platform(float x, float y, int h, int w) {
		super(x, y, h, w);
		color = Color.red;
		isSolid = true;
		name = "platform";
	}
	
	public boolean getHasLadder() {
		return this.hasLadder;
	}
	
	public void setHasLadder(boolean hasLadder) {
		this.hasLadder = hasLadder;
	}

}
