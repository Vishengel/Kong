import java.awt.Color;

public class Ladder extends GameObject{

	public Ladder(float x, float y, int h, int w) {
		super(x, y, h, w);
		color = Color.cyan;
		isSolid = false;
		name = "ladder";
	}

}
