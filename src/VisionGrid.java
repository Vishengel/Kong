import java.util.ArrayList;

//This class represents the vision grid for barrels and ladders
public class VisionGrid extends GameObject{
	
	//The entire grid will be size * size blocks
	private float size;
	//Contains all the vision blocks
	private ArrayList<VisionBlock> blocks;
	//Contains the barrel detections for every block
	private double barrelDetections[];
	//Contains the ladder detections for every block
	private double ladderDetections[];
	//Contains the powerup detections for every block
    private double powerupDetections[];
	//Contains the peach detections for every block
	private double peachDetections[];
	
	 
	public VisionGrid(float x, float y, float h, float w, float size) {
		super(x, y, h, w);
		this.size = size;
		barrelDetections = new double[(int) (size * size)];
		ladderDetections = new double[(int) (size * size)];
		powerupDetections = new double[(int) (size * size)];
		peachDetections = new double[(int) (size * size)];
		createBlocks();
	}

	//create size * size blocks and add them to the vision grid
	public void createBlocks(){
		blocks = new ArrayList<VisionBlock>();
		float blockWidth = width / size;
		float blockHeight = height / size;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				VisionBlock block = new VisionBlock(xPos + (j * blockWidth), yPos + (i * blockHeight), blockHeight, blockWidth);
				blocks.add(block); 
			}
		}
	}
	//Move the entire grid to the specified (x,y) coordinates
	public void moveGrid(float x, float y){
		//calculate difference in x and y position compared to current position
		float marioCenterX = (size / 2) * blocks.get(0).getWidth() - 10;
		float marioCenterY  = (size / 2) * blocks.get(0).getHeight() + 15;
		x -= marioCenterX;
		y -= marioCenterY;
		float xdiff;
		float ydiff;
		xdiff = this.xPos - x;
		ydiff = this.yPos - y;
		this.xPos = x;
		this.yPos = y; 
		//then propagate this to every block in the grid
		for(VisionBlock b : blocks){

			b.setXPos(b.getXPos() - xdiff);
			if(ydiff > b.getYPos()){
				b.setXPos(b.getYPos() + ydiff);
			}
			else{
				b.setYPos(b.getYPos() - ydiff);
			}
			
		}		
	}
	
	//reset the detections of the vision blocks
	public void resetDetections(){
		for(VisionBlock b : blocks){
			b.barrelDetected(0);
			b.ladderDetected(0);
			b.powerupDetected(0);
			b.peachDetected(0);		
		}		
	}
	
	public ArrayList<VisionBlock> getBlocks(){
		return blocks;
	}
	
	//fill barrel detection array, then return
	public double[] getBarrelInputs(){
		for(int i = 0; i < blocks.size(); i++){
			barrelDetections[i] = blocks.get(i).detectedBarrel();
		}
		return barrelDetections;
	}
	//fill ladder detection array, then return
	public double[] getLadderInputs(){
		for(int i = 0; i < blocks.size(); i++){
			ladderDetections[i] = blocks.get(i).detectedLadder();
		}
		return ladderDetections;
	}
	
	public double[] getPowerupInputs(){
		for(int i = 0; i < blocks.size(); i++){
			powerupDetections[i] = blocks.get(i).detectedPowerup();
		}
		return powerupDetections;
	}
	
    public double[] getPeachInputs(){
    	for(int i = 0; i < blocks.size(); i++){
    		peachDetections[i] = blocks.get(i).detectedPeach();
		}
		return peachDetections;
	}
	
	
	public float getSize(){
		return size;
	}
	
}
