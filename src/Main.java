import java.io.IOException;
import java.util.ArrayList;

//runs the entire game 
public class Main {
	public static void main(String[] args) throws IOException{
		GameController gc = new GameController();
		gc.start();
		
		//NeuralNetworkJelle n = new NeuralNetworkJelle();
		//n.trainNetwork();
		//MLP mlp = new MLP();
		//mlp.trainNetwork();
		//FileHandler fh = new FileHandler();
		
		//float[] floats = new float[]{0.1f,0.3f,0.4f,99999,0.0f,8};
		//boolean booleans[] = new boolean[]{true,true,false};
		//fh.readFile(floats,booleans,5);
		//n.printResults();
		
		//MLPJelle mlp = new MLPJelle(2, 2, 2, 3, "XORData");

		//mlp.trainNetwork();
	    //mlp.testNetwork();
		//mlp.printNetwork();
		
		/*for(int i=0; i<10; i++) {
			mlp = new MLPJelle();
			mlp.trainNetwork();
			mlp.testNetwork();
		}
		*/
		//mlp.printNetwork();
		/*
		MLP mlp = new MLP();
		mlp.trainNetwork();
		*/
		
		
		//IH.test();

	}
}
