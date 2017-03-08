import java.awt.Color;

public class Ladder extends GameObject{

	public Ladder(int x, int y, int h, int w) {
		super(x, y, h, w);
		color = Color.cyan;
		isSolid = false;
	}

}
