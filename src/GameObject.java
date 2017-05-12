import java.awt.Color;

public abstract class GameObject {
	protected float xPos;
	protected float yPos;
	protected float height;
	protected float width;
	//insert here some variable that represents what the object looks like
	char symbol;
	//The color of the object
	protected Color color;
	protected boolean isSolid;
	protected String name;
	public GameObject(float x, float y, float h, float w){
		xPos = x;
		yPos = y;
		height = h;
		width = w;
		
	}
	
	public float getXPos(){
		return xPos;
	}
	public void setXPos(float x){
		xPos = x;
	}
	public float getYPos(){
		return yPos;
	}
	public void setYPos(float y){
		yPos = y;
	}
	public float getHeight(){
		return height;
	}
	public float getWidth(){
		return width;
	}
	public char getSymbol(){
		return symbol;
	}
	
	public Color getColor(){
		return color;
	}
	
	public boolean isSolid(){
		return isSolid;
	}
	public String getName(){
		return name;
	}
}
