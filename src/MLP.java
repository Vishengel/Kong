import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class MLP {
	Random ran;
	//amount of inputs
	private int amountInput = 3;
	//amount of nodes in the hidden layer
	private int amountHidden = 50;
	//amount of nodes in the output layer
	private int amountOutput = 1;
	//amount of patterns
	private int amountPatterns = 4;
	//define the learning rate
	private double learningRate = 0.3;
	//private double noiseLevel = 0.1;
	private double error = 1;
	//define a stopping criterion
	private double minimumError = 0.017;
	
	private double[] target = new double[] {0.2, 0.8, 0.8, 0.8};
	private double[][] inputs = new double[][] {
			{0,0,-1}, 
			{0,1,-1}, 
			{1,0,-1}, 
			{1,1,-1}
			};
	//matrix of weights going from inputs to hidden nodes
	private double[][] weightsHidden = new double[amountInput][amountHidden];
	//matrix of weights going from hidden nodes to output nodes
	private double[][] weightsOutput = new double[amountHidden][amountOutput];
	
	private double[][] shuffledPatterns = new double[amountPatterns][amountInput];
	
	//list of hidden nodes
	ArrayList<Neuron> hiddenNodes;
	//list of output nodes
	ArrayList<Neuron> outputNodes;
	//integer list for shuffling the order of patterns
	ArrayList<Integer> shuffleList;
	
	public MLP(){
		ran = new Random();
		
		
		//initialize weight matrices
		for(int i = 0; i < amountInput; i++){
			for(int j = 0; j < amountHidden; j++ ){
				weightsHidden[i][j] = Math.random();
				//System.out.print(" " + weightsHidden[i][j]);
			}
			//System.out.println();
		}
		
		for(int i = 0; i < amountHidden; i++){
			for(int j = 0; j < amountOutput; j++ ){
				weightsOutput[i][j] = Math.random();
				//System.out.print(" " + weightsOutput[i][j]);
			}
			//System.out.println();
		}
		//initalize neuron lists
		hiddenNodes = new ArrayList<Neuron>();
		outputNodes = new ArrayList<Neuron>();
		shuffleList = new ArrayList<Integer>();
		
		for(int i = 0; i < amountHidden; i++){
			Neuron n = new Neuron();
			hiddenNodes.add(n);
		}
		
		for(int i = 0; i < amountOutput; i++){
			Neuron n = new Neuron();
			outputNodes.add(n);
		}
		//initialize shuffle list
		for(int i = 0; i < amountPatterns; i++){
			shuffleList.add(i);
		}
	}
	
	public double sigmoidPrime(double activation){
		return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));
	}
	
	public double sigmoid(double activation){
		 return (1/( 1 + Math.pow(Math.E,(-1*activation))));
	}
	
	public void calculateHiddenOutput(int pattern){
		double[] input = shuffledPatterns[pattern];
		//loop through the hidden weights and calculate the hidden activation
		for(int i = 0; i < amountHidden; i++){
			double activation = 0;
			for(int j = 0; j < amountInput; j++){
				activation += input[j] * weightsHidden[j][i];
			}
			hiddenNodes.get(i).setActivation(activation);
			//calculate output of hidden layer by using the sigmoid function
			hiddenNodes.get(i).setOutput(sigmoid(activation));
		}
		/*print output of hidden layer
		for(int i = 0; i < amountHidden; i++){
			//System.out.println(hiddenNodes.get(i).getOutput());
		}
		*/
	}
		
	
	
	public void calculateOutput(int pattern){
		for(int i = 0; i < amountOutput; i++){
			double activation = 0;
			for(int j = 0; j < amountHidden; j++){
				activation += hiddenNodes.get(j).getOutput() * weightsOutput[j][i];
			}
			outputNodes.get(i).setActivation(activation);					
			outputNodes.get(i).setOutput(sigmoid(activation));		
			double gradient = sigmoidPrime(activation) * (target[pattern] - outputNodes.get(i).getOutput());
			outputNodes.get(i).setGradient(gradient);	
			error += (target[pattern] - outputNodes.get(i).getOutput()) * (target[pattern] - outputNodes.get(i).getOutput());
		}
		error = error / 2;			
		for(int i = 0; i < amountOutput; i++){
			//System.out.println("Gradient of output node " + i + ": " + outputNodes.get(i).getGradient());
			System.out.println("Output: " + (outputNodes.get(i).getActivation() >= 0 ? 1 : 0) + " ");
		}
	}
				
	
	
	public void forwardPass(int pattern){
		//System.out.println("----Hidden layer----");
		calculateHiddenOutput(pattern);
		//System.out.println("----output layer----");
		calculateOutput(pattern);
	}
	
	public void backPropagation(int pattern){
		//calculate the local gradient of the hidden layer
		for(int i= 0; i < amountHidden; i++){
			double gradient = sigmoidPrime(hiddenNodes.get(i).getActivation());
			double gradientOutput = 0;
			//loop over the output weight matrix and sum over the gradients of the output layer
			for(int col = 0; col < amountOutput; col++){
				gradientOutput += outputNodes.get(col).getGradient();
			}
			gradient = gradient * gradientOutput;
			hiddenNodes.get(i).setGradient(gradient);
			//System.out.println("Gradient of hidden node " + i + ": " + hiddenNodes.get(i).getGradient());
		}
		//calculate weight changes of hidden layer
		double[] input = shuffledPatterns[pattern];
		for(int i = 0; i < amountInput; i++){
			for(int j = 0; j < amountHidden; j++){
				weightsHidden[i][j] += learningRate * input[i] * hiddenNodes.get(j).getGradient();
			}
		}
		//calculate weight changes of output layer
		for(int i = 0; i < amountHidden; i++){
			for(int j = 0; j < amountOutput; j++){
				weightsOutput[i][j] += learningRate * hiddenNodes.get(i).getOutput() * outputNodes.get(j).getGradient();
			}
		}
	}
		
	
	
	public void testNetwork(){	
		for(int pattern = 0; pattern < amountPatterns; pattern++){
			System.out.println("pattern: " + pattern + ":");
			forwardPass(pattern);
		}
	}
	
	
	public void shufflePatterns(){
		shuffledPatterns = new double[amountPatterns][amountInput];
		Collections.shuffle(shuffleList);
		
		for(int i = 0; i < amountPatterns; i++){
			shuffledPatterns[i] = inputs[shuffleList.get(i)];
		
		}
		
		//System.out.println(shuffleList);
		for(int i = 0; i < amountPatterns; i++){
			
			for(int j = 0; j < amountInput; j++){
				System.out.print(shuffledPatterns[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();System.out.println();
	}
	
	public void trainNetwork(){
		int epochs = 30000;
		int epoch = 0;
		//train a predetermined amount of epochs, or until the error reaches a low enough number 
		while(epoch < epochs && error > minimumError){
			System.out.println(epoch);
			//shuffle the order of patterns presented to the network 
			//shufflePatterns();
			shuffledPatterns = inputs;
			error = 1;
			//add noise to input to help the network to escape local minimum
			//show each input to the net and perform forward and backwards propagation
			for(int pattern = 0; pattern < amountPatterns; pattern++){
				//System.out.println("Pattern " + pattern + ":");
				forwardPass(pattern);
				backPropagation(pattern);	
			}
			error = error / amountPatterns;
			System.out.println("Error: " + error);
			epoch++;
			
			}
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("------RESULTS-----");
			System.out.println("Epochs trained: " + epoch);
			testNetwork();
	}
	
}
