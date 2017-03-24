import java.io.IOException;

//runs the entire game 
public class Main {
	public static void main(String[] args) throws IOException{
		GameController gc = new GameController();
		gc.start();
		//NeuralNetwork n = new NeuralNetwork();
		//n.trainNetwork();
		//MLP mlp = new MLP();
		//mlp.trainNetwork();
		//FileHandler fh = new FileHandler();
		
		//float[] floats = new float[]{0.1f,0.3f,0.4f,99999,0.0f,8};
		//boolean booleans[] = new boolean[]{true,true,false};
		//fh.readFile(floats,booleans,5);
	}
}
