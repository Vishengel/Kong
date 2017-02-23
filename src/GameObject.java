import java.awt.Color;

public abstract class GameObject {
	protected int xPos;
	protected int yPos;
	protected int height;
	protected int width;
	//insert here some variable that represents what the object looks like
	char symbol;
	//The color of the object
	Color color;
	
	public GameObject(int x, int y, int h, int w){
		xPos = x;
		yPos = y;
		height = h;
		width = w;
		
	}
	
	public int getXPos(){
		return xPos;
	}
	public void setXPos(int x){
		xPos = x;
	}
	public int getYPos(){
		return yPos;
	}
	public void setYPos(int y){
		yPos = y;
	}
	public int getHeight(){
		return height;
	}
	public int getWidth(){
		return width;
	}
	public char getSymbol(){
		return symbol;
	}
	
	public Color getColor(){
		return color;
	}
}
