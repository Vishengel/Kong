import java.io.IOException;

//runs the entire game 
public class Main {
	public static void main(String[] args) throws IOException{
		//GameController gc = new GameController();
		//gc.start();
		NeuralNetworkJelle n = new NeuralNetworkJelle();
		n.trainNetwork();
		n.printResults();
	}
}
