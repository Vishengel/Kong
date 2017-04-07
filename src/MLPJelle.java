import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;




//Implementation of a multi-layer perceptron
public class MLPJelle {
	//temporary file handler for testing 
	FileHandler FH = new FileHandler();
	
	//All four possible input combinations of two input boolean values, plus the threshold multiplier
	private double[][] input;
	//The target values corresponding with each input				
	private double[][] target /*= new double[][] {{0.0,0.0,0.0}, {0.0,0.0,1.0}, {0.0,1.0,0.0}, {0.0,1.0,1.0}}*/;
	//Define the number of nodes in input, hidden and output layers
	private int nInput;
	private int nHidden = 70; 
	private int nOutput = 7; 
	//Define the number of hidden layers
	private int nHiddenLayers = 1; 
	//Define a list that contains a list of nodes for each hidden layer
	private ArrayList<ArrayList<NeuronJelle>> hiddenList = new ArrayList<ArrayList<NeuronJelle>>();
	//Define a list of output neurons
	private ArrayList<NeuronJelle> outputLayer = new ArrayList<NeuronJelle>();
	//Define the learning rate, error threshold and the maximum number of epochs
	private double learningRate = 0.6;
	private double errorThreshold = 0.0000000004;
	private double maxEpochs = 10000;  
	private double momentum = 0.6; 
	public MLPJelle() {
		this.initNetwork();	
	}
	
	public void initNetwork() {
		//read the demonstration data to be learned from 
		double[][] initialInput = FH.readFile();
		input = new double[initialInput.length][7];
		//filter out the target values from the input array
		for(int i = 0; i < initialInput.length; i++){
			//System.out.println("Pattern: " + i);
			for(int j = 0; j < 6; j++){	
				input[i][j] = initialInput[i][j];
				//System.out.print(input[i][j] + " ");
			}
			//add the -1 value at the end of the input
			input[i][6] = -1;
		}
		//fill the target array with the target values 
		target = new double[input.length][nOutput];
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < initialInput[0].length - input[0].length; j++){
				target[i][j] = initialInput[i][j + initialInput[0].length - input[0].length - 1];
				
			}
		}	
		nInput = input[0].length;
		
		ArrayList<NeuronJelle> hiddenLayer = new ArrayList<NeuronJelle>();
		int nWeights = nInput;
		
		//Initialize hidden layers
		for (int i=0; i<nHiddenLayers; i++) {
			//Clear the hiddenlayer after each iteration
			hiddenLayer = new ArrayList<NeuronJelle>();
			for (int j=0; j<nHidden; j++) {
				//Add nWeights amount of nodes to the hidden layer
				//For the first iteration, this amount is equal to the number of inputs
				//Afterwards, this amount is equal to the number of nodes in the previous layer
				hiddenLayer.add(new NeuronJelle(nWeights));
			}
			nWeights = hiddenLayer.size();
			//Add the hidden layer to the list of hidden layers
			hiddenList.add(hiddenLayer);
		}
		//Initialize output layer
		for(int i=0; i<nOutput; i++) {
			outputLayer.add(new NeuronJelle(nWeights));
		}
	}
	
	public void trainNetwork() {
		System.out.println("Training:");
		double totalError = 0, previousTotalError; 
		double epoch = 0;
		do {
			//print training progress
			System.out.println(Math.round((epoch / maxEpochs) * 100) + "%");
			//System.out.println(Math.round((epoch / maxEpochs) * 100) + "%");
			//System.out.println("Training completed in " + epoch + " epochs");
			previousTotalError = totalError;
			totalError = 0;
			
			this.shuffleInput();
			
			for (int i=0; i<this.nInput; i++) {
				this.forwardPass(this.input[i]);
				totalError = this.backwardPass(i);
			}
			
			totalError /= -1*this.input.length;
			
			//System.out.println(totalError);
			
			epoch++;
		//Continue until either the maximum number of epochs is reached 
		//or the decrease in the error is smaller than the threshold
		} while (totalError > errorThreshold && epoch < maxEpochs);
		System.out.println("Training completed in " + epoch + " epochs");
	}
	
	public void shuffleInput() {
		Random rnd = ThreadLocalRandom.current();
		int index;
		double[] a;
		double[] b;
		
		for (int i = input.length - 1; i > 0; i--)
		{
		  index = rnd.nextInt(i + 1);
		  // Simple swap
		  a = input[index];
		  b = target[index];
		  input[index] = input[i];
		  input[i] = a;
		  target[index] = target[i];
		  target[i] = b;
		}
	}
	
	public void forwardPass(double[] input) {
		//First, feed the input to the first hidden layer
		double[] currentInput = input;
		//Stores the output to be fed to the next hidden layer as input
		double[] outputArray = new double[nHidden];
		//Loop through hidden layers
		for (int i=0; i<nHiddenLayers; i++) {
			//Calculate the output for each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				hiddenList.get(i).get(j).setInput(currentInput);
				hiddenList.get(i).get(j).setActivation();
				hiddenList.get(i).get(j).setOutput();
				outputArray[j] = hiddenList.get(i).get(j).getOutput();
			}
			//Store the output of the hidden layer as input for the next layer
			currentInput = outputArray;
		}
		
		for (NeuronJelle n : outputLayer) {
			n.setInput(currentInput);
			n.setActivation();
			n.setOutput();
			//n.printWeights();
		}
	}
	
	public double backwardPass(int patternIndex) {
		double totalError=0;
		ArrayList<NeuronJelle> nextLayer = new ArrayList<NeuronJelle>();
		
		//Calculate gradients for nodes in the output layer
		for(int i = 0; i < nOutput; i++){
			outputLayer.get(i).setOutputGradient(target[patternIndex][i]);
		}
		
		nextLayer = outputLayer;
		
		//Loop through all hidden layers in reverse order
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				//Calculate gradients for nodes in each hidden layer
				hiddenList.get(i).get(j).setHiddenGradient(j, nextLayer);
			}
			nextLayer = hiddenList.get(i);
		}
		
		//Update the weights for nodes in the output layer
		for(int i = 0; i < nOutput; i++){
			outputLayer.get(i).updateWeights(target[patternIndex][i], learningRate, momentum);
			//totalError += outputLayer.get(i).getError();
			totalError += outputLayer.get(i).getCrossEntropy();
		}
		
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				//Update the weights for each node in the hidden layer
				hiddenList.get(i).get(j).updateWeights(target[patternIndex][i], learningRate, momentum);
			}
		}
		
		return totalError;
	}
	
	//converts binary encoding of ouput nodes to an integer 
	public int binaryToInt(){
		int number = 0;
		int multiplier = (int) Math.pow(2, nOutput - 1);
		for(int i = 0; i < nOutput; i++){
			int output = outputLayer.get(i).getActivation() >= 0? 1 : 0;
			number += output * multiplier;
			multiplier = multiplier /2;
		}
		return number;
	}
	
	public int maxOutput(){
		double max = 0;
		int maxIndex = 0;
		for(int i = 0; i < nOutput; i++){
			double output = outputLayer.get(i).getOutput();
			if(output > max){
				max = output;
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public int testNetwork(double[] input) {
		int testEpoch; 
		double nCorrect = 0;
		double output = 0;
		double tar;
		//present game state to the network, calculate output
		forwardPass(input);
		//System.out.println(binaryToInt());	
		/* activation of output nodes
		for(int i = 0; i < nOutput; i++){
			System.out.print("Output node " + i + ": " + outputLayer.get(i).getActivation());
			System.out.println(" " + outputLayer.get(i).getOutput());
			
		}*/
		return maxOutput();	
		
		/*
		for (testEpoch=0; testEpoch < 1; testEpoch++) {
			for (int i=0; i<this.input.length; i++) {
		
				this.forwardPass(this.input[i]);
				//print pattern number
				System.out.println("Pattern " + i);
				
				//print input for each pattern
				for(int j=0; j< this.input[i].length; j++) {
					System.out.println("	Input: " + this.input[i][j]);	
				}
				//print all output of the output nodes for each pattern
				for(int n = 0; n < nOutput; n++){					
					System.out.println("	Output " + n + ": " + (outputLayer.get(n).getOutput()));	
						
				System.out.println("Max output: " + maxOutput());
					//target = this.target[i][n];
					//System.out.println(output + " " + target);
					//if (output == target) {
						//nCorrect++;
					//}
				} 
				output = binaryToInt();
			
				//print the integer version of the binary encoded output	
				//print target
				for(int t = 0; t < target[0].length; t++){
				System.out.print("Target: " + target[i][t] + " ");
				System.out.println();
				}
				//tar = target[i][0] * 4 + target[i][1] * 2 + target[i][2];
				//System.out.println("T: " + tar);
				//nCorrect += output == tar ? 1 : 0;
				System.out.println("Percentage correct: " + Math.round((nCorrect / this.input.length)*100));
			}
		}
		System.out.println(nCorrect + " " + ((double)testEpoch*(double)this.input.length));
		System.out.println("Accuracy: " + (nCorrect * 100) / ((double)testEpoch*(double)this.input.length) + "%");
		 
		 
		 
		return 0;*/
	}
	
	public void printNetwork() {
		for (int i=0; i<nHiddenLayers; i++) {
			System.out.println("Hidden layer " + i + ": ");
			//Calculate the output for each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				System.out.println("Hidden node " + j);
				hiddenList.get(i).get(j).printWeights();
			}

		}
		
		for (NeuronJelle n : outputLayer) {
			System.out.println("Output node " + outputLayer.indexOf(n));
			n.printWeights();
		}
		
		for (int i=0; i<target.length; i++) {
			System.out.println(target[i]);
		}
	}
		
}
