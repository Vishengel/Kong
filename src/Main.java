import java.io.IOException;

//runs the entire game 
public class Main {
	public static void main(String[] args) throws IOException{
		GameController gc = new GameController();
		gc.start();
		
		//NeuralNetworkJelle n = new NeuralNetworkJelle();
		//n.trainNetwork();
		//n.printResults();
		
		MLPJelle mlp = new MLPJelle();
		for(int i=0; i<10; i++) {
			mlp = new MLPJelle();
			mlp.trainNetwork();
			mlp.testNetwork();
		}
		//mlp.printNetwork();
		/*
		MLP mlp = new MLP();
		mlp.trainNetwork();
		*/

	}
}
