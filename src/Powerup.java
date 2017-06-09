
public class Powerup extends GameObject {
	public Powerup(int x, int y, int h, int w) {
		super(x, y, h, w);
		isSolid = false;
		name = "powerup";
	}
	
	public Powerup(Powerup PU) {
		super(PU);
	}
}
