import java.awt.Color;

public class Platform extends GameObject{

	public Platform(int x, int y, int h, int w) {
		super(x, y, h, w);
		color = Color.red;
		isSolid = true;
	}

}
