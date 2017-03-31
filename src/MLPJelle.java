import java.util.ArrayList;

//Implementation of a multi-layer perceptron
public class MLPJelle {
	//Define the number of nodes in input, hidden and output layers
	private int nInput = 3;
	private int nHidden = 2;
	private int nOutput = 1;
	//Define the number of hidden layers
	private int nHiddenLayers = 1;
	//All four possible input combinations of two input boolean values, plus the threshold multiplier
	private double[][] input = new double[][] {
										{0.0, 0.0, -1.0},
										{1.0, 0.0, -1.0},
										{0.0, 1.0, -1.0},
										{1.0, 1.0, -1.0}
										};
	//The target values corresponding with each input				
	private double[] target = new double[] {0.0, 0.0, 0.0, 1.0};
	//Define a list that contains a list of nodes for each hidden layer
	private ArrayList<ArrayList<NeuronJelle>> hiddenList = new ArrayList<ArrayList<NeuronJelle>>();
	//Define a list of output neurons
	private ArrayList<NeuronJelle> outputLayer = new ArrayList<NeuronJelle>();
	//Define the learning rate, error threshold and the maximum number of epochs
	private double learningRate = 0.3;
	private double errorThreshold = 0.0001;
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
			hiddenLayer.clear();
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
			
			for (int i=0; i<this.nInput; i++) {
				this.forwardPass(this.input[i]);
				totalError = this.backwardPass(i);
			}
			
			epoch++;
		//Continue until either the maximum number of epochs is reached 
		//or the decrease in the error is smaller than the threshold
		} while (Math.abs(previousTotalError - totalError) > errorThreshold || epoch < maxEpochs);
		System.out.println("Training completed in " + epoch + " epochs");
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
			//n.updateWeights(target[patternIndex], learningRate);
			//totalError += n.getError();
		}
		
		nextLayer = outputLayer;
		
		//Loop through all hidden layers in reverse order
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<nHidden; j++) {
				//Calculate gradients for nodes in each hidden layer
				hiddenList.get(i).get(j).setHiddenGradient(j, nextLayer);
				//hiddenList.get(i).get(j).updateWeights(target[patternIndex], learningRate);
			}
			nextLayer = hiddenList.get(i);
		}
		
		//Update the weights for nodes in the output layer
		for (NeuronJelle n : outputLayer) {
			n.updateWeights(target[patternIndex], learningRate);
			totalError += n.getError();
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
		for (int i=0; i<this.input.length; i++) {
			this.forwardPass(this.input[i]);
			for (NeuronJelle n : outputLayer) {
				System.out.println("Pattern " + i);	
				
				for(int j=0; j<input[i].length-1; j++) {
					System.out.println("	Input: " + input[i][j]);	
				}
				
				System.out.println("	Output: " + (n.getActivation() >= 0 ? 1 : 0));	
			}
		}
	}
		
}
