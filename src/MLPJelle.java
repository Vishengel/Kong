import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//Implementation of a multi-layer perceptron
public class MLPJelle {
	//All four possible input combinations of two input boolean values, plus the threshold multiplier
	private double[][] input = new double[][] {
										{0.0, 0.0, -1.0},
										{1.0, 0.0, -1.0},
										{0.0, 1.0, -1.0},
										{1.0, 1.0, -1.0}
										};
	//The target values corresponding with each input				
	private double[] target = new double[] {0.0, 1.0, 1.0, 0.0};
	//Define the number of nodes in input, hidden and output layers
	private int nInput = input[0].length;
	private int nHidden = 4;
	private int nOutput = 1;
	//Define the number of hidden layers
	private int nHiddenLayers = 1;
	//Define a list that contains a list of nodes for each hidden layer
	private ArrayList<ArrayList<NeuronJelle>> hiddenList = new ArrayList<ArrayList<NeuronJelle>>();
	//Define a list of output neurons
	private ArrayList<NeuronJelle> outputLayer = new ArrayList<NeuronJelle>();
	//Define the learning rate, error threshold and the maximum number of epochs
	private double learningRate = 0.3;
	private double errorThreshold = 0.005;
	private int maxEpochs = 50000;
	
	public MLPJelle() {
		this.initNetwork();
	}
	
	public void initNetwork() {
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
		double totalError = 0, previousTotalError; 
		int epoch = 0;
		do {
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
		double b;
		
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
		for (NeuronJelle n : outputLayer) {
			n.setOutputGradient(target[patternIndex]);
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
		for (NeuronJelle n : outputLayer) {
			n.updateWeights(target[patternIndex], learningRate);
			//totalError += n.getError();
			totalError += n.getCrossEntropy();
		}
		
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				//Update the weights for each node in the hidden layer
				hiddenList.get(i).get(j).updateWeights(target[patternIndex], learningRate);
			}
		}
		
		return totalError;
	}
	
	public void testNetwork() {
		int testEpoch;
		double nCorrect = 0;
		double output, target;
		
		for (testEpoch=0; testEpoch < 10000; testEpoch++) {
			for (int i=0; i<this.input.length; i++) {
				this.forwardPass(this.input[i]);
				for (NeuronJelle n : outputLayer) {
					/*
					System.out.println("Pattern " + i);	
					//Don't print the bias
					for(int j=0; j<input[i].length-1; j++) {
						System.out.println("	Input: " + input[i][j]);	
					}
					
					System.out.println("	Output: " + (n.getActivation() >= 0 ? 1 : 0));	
					*/
					
					output = (n.getActivation() >= 0.0 ? 1.0 : 0);
					target = this.target[i];
					//System.out.println(output + " " + target);
					if (output == target) {
						nCorrect++;
					}
				}
			}
		}
		//System.out.println(nCorrect + " " + ((double)testEpoch*(double)this.input.length));
		System.out.println("Accuracy: " + (nCorrect * 100) / ((double)testEpoch*(double)this.input.length) + "%");
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
