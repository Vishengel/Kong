
public class Barrel extends MovingObject{
	//0 : left   1 : right
	int direction; 
	
	public Barrel(int x, int y, int h, int w, int d) {
		super(x, y, h, w);
		symbol = 'O';
		killOnCollision = true;
		direction = d;
	}
	
	
	

}
