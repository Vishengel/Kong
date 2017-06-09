import java.util.ArrayList;

//This class represents the vision grid for barrels and ladders
public class VisionGrid extends GameObject{
	
	//The entire grid will be size * size blocks
	private int size;
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
	//contains the mario detections for every block
	private double marioDetections[];
	
	 
	public VisionGrid(float x, float y, float h, float w, int size) {
		super(x, y, h, w);
		this.size = size;
		barrelDetections = new double[(int) (size * size)];
		ladderDetections = new double[(int) (size * size)];
		powerupDetections = new double[(int) (size * size)];
		peachDetections = new double[(int) (size * size)];
		marioDetections = new double[(int) (size * size)];
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
		float marioCenterX = (size / 2) * blocks.get(0).getWidth()+10;
		float marioCenterY  = (size / 2) * blocks.get(0).getHeight() + 10;
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
			b.marioDetected(0);
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
    
    public double[] getMarioInputs(){
    	for(int i = 0; i < blocks.size(); i++){
    		marioDetections[i] = blocks.get(i).detectedMario();
		}
		return marioDetections;
    }
	
  //Check if any barrels or ladders are detected in the blocks of the vision grid
  	public void checkDetections(ArrayList<MovingObject> MOList, ArrayList<Ladder> ladderList, ArrayList<Powerup> PUList, Player mario, Peach peach){
  		float barrelDetections = 0;
  		float ladderDetections = 0;
  		float powerupDetections = 0;
  		float peachDetections = 0;
  		float marioDetections = 0;
  		//for every block, check if a barrel or ladder is inside 
  		for(VisionBlock b : blocks){
  			//detect barrels
  			for(int i = 1; i < MOList.size(); i++){
  				if(GameModel.isColliding(b, MOList.get(i))){
  					b.barrelDetected(1);
  					barrelDetections++;
  					
  				}
  			}
  			//detect ladders
  			for(int i = 0; i < ladderList.size(); i++){
  				if(GameModel.isColliding(b, ladderList.get(i))){
  					b.ladderDetected(1);
  					ladderDetections++;
  				}
  			}
  			
  			//detect powerups
  			for(int i = 0; i < PUList.size(); i++){
  				if(GameModel.isColliding(b, PUList.get(i))){
  					b.powerupDetected(1);
  					powerupDetections++;
  				}
  			}
  			//detect peach
  			if(GameModel.isColliding(b, peach)){
  				b.peachDetected(1);
  				peachDetections++;
  			}
  		//detect mario
			if(GameModel.isColliding(b, mario)){
				b.marioDetected(1);
				marioDetections++;
			}
  			
  		}
  		
  		//Divide the detection values by the number of detections;
  		//Divide the value in each block by the total number of blocks that the object occupies
  		for(VisionBlock b : blocks){
  			if(barrelDetections > 0){
  				b.barrelDetected(b.detectedBarrel()/barrelDetections);
  			}
  			if(ladderDetections > 0){
  				b.ladderDetected(b.detectedLadder()/ladderDetections);
  			}
  			if(powerupDetections > 0){
  				b.powerupDetected(b.detectedPowerup()/powerupDetections);
  			}
  			if(peachDetections > 0){
  				b.peachDetected(b.detectedPeach()/peachDetections);
  			}
  			if(marioDetections > 0){
  				b.marioDetected(b.detectedMario()/marioDetections); 
  			}
  		}
  	}
    
    
	
	public int getSize(){
		return size;
	}
	
}
