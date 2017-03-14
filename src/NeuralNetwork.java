import java.util.ArrayList;
import java.util.Scanner;



public class NeuralNetwork {
	
	//network parameters
	private int nHiddenNodes = 0;
	private int nOutputNodes = 1;
	private float learningRate = 0.3f;
	private float minimumError = 0.0001f;
	
	private ArrayList<Neuron> outputNeurons;
	private ArrayList<Neuron> hiddenNeurons;
	
	
	//represents the function to be learned; AND function
	private int[] goal = new int[] {0, 0, 0, 1};
	//represents all the four boolean input patterns
	private int[][] inputs = new int[][] {
										{0,0,-1}, 
										{0,1,-1}, 
										{1,0,-1}, 
										{1,1,-1}
										};
	private int epoch;
	private int maxEpochs = 100000;
	
	public double sigmoidPrime(double activation){
		return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));
	}
	
	public double sigmoid(double activation){
		 return (1/( 1 + Math.pow(Math.E,(-1*activation))));
	}
	
	public NeuralNetwork(){
		//initialize the neuron lists
		outputNeurons = new ArrayList<Neuron>();
		hiddenNeurons = new ArrayList<Neuron>();
		
		//for every Neuron in the network, initialize and randomize the weights
		//The amount of weights is determined by the size of the input patterns	
		for(int i = 0; i < nOutputNodes; i++){
			Neuron n = new Neuron();
			n.initializeWeights(inputs[0].length);
			outputNeurons.add(n);
			}
	   }
	
	public void trainNetwork(){
		double totalError = 10;	
		epoch = 1;
		//train the network until the rate of change of the error is small enough or until maximum epochs is reached
		while(epoch < maxEpochs && totalError > minimumError){
			//loop through each of the (4) input patterns
			for(int pattern = 0; pattern < inputs.length; pattern++){
				//for each pattern, multiply the input with the corresponding weight
				Neuron n = outputNeurons.get(0); 
				//reset activation
				n.setActivation(0);  
				for(int x = 0; x < n.getNWeights(); x++){
					//compute activation of neuron: sum (x_n * w_n) for all n
					n.setActivation(n.getActivation() + n.getWeights().get(x) * inputs[pattern][x]);										
				} 
				//calculate the output of the neuron by using the activation in the sigmoid function
				double output = sigmoid(n.getActivation());
				//calculate difference between target and output of net
				double difference = (goal[pattern] - output);	
				//adjust all weights: learningRate * sigmoid' * difference * input
				for(int w = 0; w < n.getNWeights(); w++){
					double weightChange = learningRate * sigmoidPrime(n.getActivation())* difference * inputs[pattern][w];
					//System.out.println(weightChange);
					n.getWeights().set(w, n.getWeights().get(w) + weightChange);
				}
				totalError += difference * difference * 1/2;
			} 
		    epoch++;
		    totalError = totalError / inputs.length;
		   
		}	
		System.out.println("------------Print results------------");
		System.out.println("Epochs trained: " + epoch);
		Neuron n = outputNeurons.get(0);	
		System.out.println("Weight 1: " + n.getWeights().get(0));
		System.out.println("Weight 2: " + n.getWeights().get(1));
		System.out.println("Threshold: " + n.getWeights().get(2));
		for(int pattern = 0; pattern < inputs.length; pattern++){
			n.setActivation(0);  
			System.out.println("---Pattern--- " + pattern);
			for(int x = 0; x < n.getNWeights(); x++){
				if(x < n.getNWeights() -1){
					System.out.println("Input: " + inputs[pattern][x]);
				}
				n.setActivation(n.getActivation() + n.getWeights().get(x) * inputs[pattern][x]);	
			} 
			//use threshold function instead of sigmoidal function for clearer output
			System.out.println("Output: " + (n.getActivation() >= 0 ? 1 : 0));	
		}		
	}
}
