
public class Powerup extends GameObject {
	public Powerup(float x, float y, int h, int w) {
		super(x, y, h, w);
		isSolid = false;
		name = "powerup";
	}
	
	public Powerup(Powerup PU) {
		super(PU);
	}
}
