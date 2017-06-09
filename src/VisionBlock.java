//This class represnts one of the blocks of the NxN vision grid
public class VisionBlock extends GameObject{
 
	private double barrelDetected = 0;
	private double ladderDetected = 0;
	private double powerupDetected = 0;
	private double peachDetected = 0;
	private double marioDetected = 0;
			
	
	public VisionBlock(float x, float y, float h, float w) {
		super(x, y, h, w);
	}
	
	public void barrelDetected(double barrelDetected){
		this.barrelDetected = barrelDetected;
	}
	
	public void ladderDetected(double ladderDetected){
		this.ladderDetected = ladderDetected;
	}
	
	public void powerupDetected(double powerupDetected){
		this.powerupDetected = powerupDetected;
	}
	
	public void peachDetected(double peachDetected){
		this.peachDetected = peachDetected;
	}
	
	public void marioDetected(double marioDetected){
		this.marioDetected = marioDetected;
	}
	
	public double detectedBarrel(){
		return barrelDetected;
	}
	
	public double detectedLadder(){
		return ladderDetected;
	}
	
	public double detectedPowerup(){
		return powerupDetected;
	}
	
	public double detectedPeach(){
		return peachDetected;
	}
	
	public double detectedMario(){
		return marioDetected;
	}
		
	
	
	
	

}
