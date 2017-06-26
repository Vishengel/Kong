import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;




//Implementation of a multi-layer perceptron
public class MLPJelle {
	//temporary file handler for testing 
	FileHandler FH = new FileHandler();
	
	//All four possible input combinations of two input boolean values, plus the threshold multiplier
	protected double[][] input;
	//The target values corresponding with each input				
	protected double[][] target = new double[1][7]; /*= new double[][] {{0.0,0.0,0.0}, {0.0,0.0,1.0}, {0.0,1.0,0.0}, {0.0,1.0,1.0}}*/;
	//Define the number of nodes in input, hidden and output layers
	protected int nInput;
	private int nHidden; 
	protected int nOutput; 
	//Define the number of hidden layers
	private int nHiddenLayers; 
	//Define a list that contains a list of nodes for each hidden layer
	private ArrayList<ArrayList<NeuronJelle>> hiddenList = new ArrayList<ArrayList<NeuronJelle>>();
	//Define a list of output neurons
	protected ArrayList<NeuronJelle> outputLayer = new ArrayList<NeuronJelle>();
	//Define the learning rate, error threshold and the maximum number of epochs
	protected double learningRate = 0.001;   
	protected double errorThreshold = 0;     
	//protected double errorThreshold = 0.065;   
	//Define a minimum change that makes the training phase stop when this minimum difference between training epochs
	//is reached
	protected double minimumChange = 0.000005;  
	protected double maxEpochs = 500;  
	protected String fileName;
	
	private double temperature = 2; 
	
	public MLPJelle(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName, boolean loadNetwork) throws IOException {
		this.nInput = nInput;
		this.nHiddenLayers = nHiddenLayers;
		this.nHidden = nHidden;
		this.nOutput = nOutput;
		this.fileName = fileName;
		
		//If an old network needs to be loaded, don't use the normal mlp initialization
		if(loadNetwork){
			this.loadNetwork(); 
		}
		else{
			this.initNetwork();	
		}
	}
	//Restore a previously trained and stored network 
	public void loadNetwork() throws IOException{
		String network, str;
		String[] line;
		BufferedReader in;
		//Load the right file, depending on the network
		if(this instanceof Critic){
			System.out.println("Loading Critic!");
			network = "storedCritic";
		}
		else{
			System.out.println("Loading Actor!");
			if(constants.LOAD_TRAINED_ACTOR){
				network = "storedActorTrained";
			}
			else{
				network = "storedActor";
			}
		}
		String filename= "src/" + network + ".csv";
		in = new BufferedReader(new FileReader(filename));
		str = in.readLine();
		line = str.split(",");
		//Get the amount of hidden layers,
		nHiddenLayers = Integer.parseInt(line[0]);
		System.out.println("Hidden layers: " + nHiddenLayers);
		//Store the size of each hidden layer
		int[] hiddenLayerSizes = new int[nHiddenLayers];
		int[] hiddenLayerWeights = new int[nHiddenLayers];
		for(int i = 0; i < nHiddenLayers; i++){
			hiddenLayerSizes[i] = Integer.parseInt(line[i + 1]);
			System.out.println("Hidden layer " + i + " nodes: " + hiddenLayerSizes[i]);
		}
		//get the amount of output nodes
		int outputNodes = Integer.parseInt(line[line.length - 1]);
		System.out.println("Output nodes: " + outputNodes);
		//Go to the next line 
		str = in.readLine();
		line = str.split(",");
		//Get weights for every type of neuron
		for(int i = 0; i < nHiddenLayers; i++){
			hiddenLayerWeights[i] = Integer.parseInt(line[i]);
			System.out.println("Neurons in hidden layer " + i + " have " + hiddenLayerWeights[i] + " weights.");
		}
		int outputWeights = Integer.parseInt(line[line.length-1]);
		System.out.println("Neurons in output layer have " + outputWeights + " weights.");
		
		
		//recreate network architecture
		//ArrayList<NeuronJelle> hiddenLayer = new ArrayList<NeuronJelle>();
		//Create hidden layers and hidden nodes and fill them with the correct weights
		//Loop through every hidden layer
		for(int i = 0; i < nHiddenLayers; i++){
			//Create the hidden layer
			ArrayList<NeuronJelle> hiddenLayer = new ArrayList<NeuronJelle>();
			//Create the hidden nodes
			for(int j = 0; j < hiddenLayerSizes[i]; j++){
				hiddenLayer.add(new NeuronJelle(hiddenLayerWeights[i]));		
				str = in.readLine();
				line = str.split(",");
				//System.out.println(line.length);
				//Add the weights from the stored network to the weights of neuron
				for(int w = 0; w < hiddenLayerWeights[i]; w++){
					hiddenLayer.get(j).getWeights()[w] = Double.parseDouble(line[w]);
				}
			}
			hiddenList.add(hiddenLayer);
		}
		
		
		//Create output layer nodes and weights
		for(int i = 0; i < nOutput; i++){
			outputLayer.add(new NeuronJelle(outputWeights));
			str = in.readLine();
			line = str.split(",");
			for(int w = 0; w < outputWeights; w++){
				outputLayer.get(i).getWeights()[w] = Double.parseDouble(line[w]);
				
			}
		}
		
		
		
		in.close();
	}
	
	public void initializeLayers(){
		ArrayList<NeuronJelle> hiddenLayer = new ArrayList<NeuronJelle>();
		int nWeights = nInput - 1;
		int hiddenLayerSize = nHidden;
		//Initialize hidden layers
		for (int i=0; i<nHiddenLayers; i++) {
			//Clear the hiddenlayer after each iteration
			hiddenLayer = new ArrayList<NeuronJelle>();
			for (int j=0; j<hiddenLayerSize; j++) {
				//Add nWeights amount of nodes to the hidden layer
				//For the first iteration, this amount is equal to the number of inputs
				//Afterwards, this amount is equal to the number of nodes in the previous layer
				hiddenLayer.add(new NeuronJelle(nWeights));
			}
			System.out.println("Hidden layer: " + i);
			System.out.println("Size: " + hiddenLayerSize);
			//Add a node for the bias
			//hiddenLayer.add(new NeuronJelle(0));
			//Set the amount of weights for each neuron in the next layer
			//to be the same as the amount of hidden neurons in the current layer, plus one for the bias
			nWeights = hiddenLayer.size() + 1;
			//Make sure the output of the bias neuron is 1
			//hiddenLayer.get(nWeights - 1).setOutput(1);
			//Add the hidden layer to the list of hidden layers
			hiddenList.add(hiddenLayer);
			//Divide the hiddenlayerSize, so that each hidden layer (except the first one)
			//has half the nodes of the previous hidden layer
			hiddenLayerSize = hiddenLayerSize / 2;
		}
		//Initialize output layer
		for(int i=0; i<nOutput; i++) {
			outputLayer.add(new NeuronJelle(nWeights));
		}
		//System.out.println("NWEIGHTS: " + nWeights);
		//System.out.println("OUTPUT LAYER WEIGHT LENGTH: " + getOutputLayer().get(0).getWeights().length);
	}
	public void initNetwork() {
		
		System.out.println("Actor init called!");
		//read the demonstration data to be learned from 
		double[][] initialInput = FH.readFile(fileName, nInput, nOutput);
		
		input = new double[initialInput.length][nInput-1];
		//filter out the target values from the input array
		for(int i = 0; i < initialInput.length; i++){
			//System.out.println("Pattern: " + i);
			for(int j = 0; j < nInput-1; j++){	
				input[i][j] = initialInput[i][j];
			}
		}
		
		//fill the target array with the target values 
		target = new double[input.length][nOutput];
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < initialInput[0].length - nInput; j++){
				target[i][j] = initialInput[i][j + nInput];
				//System.out.print(target[i][j] + ",");				
			}
			//System.out.println();
		}	
		//nInput = input[0].length;
		
		initializeLayers();
	}
	
	
	
	public void trainNetwork() {
		double previousError = 0;
		double totalError = 0; 
		double epoch = 0;
		do {
			previousError = totalError;
			//print training progress
			System.out.println("Learning.. " + Math.round((epoch / maxEpochs) * 100) + "%");		
			totalError = 0;
			
			this.shuffleInput();
			
			for (int i=0; i < this.input.length; i++) {
				this.forwardPass(this.input[i], true);
				totalError += this.backwardPass(i);
			}
			
			totalError /= this.input.length;
			System.out.println(totalError);
			
			epoch++;
		//Continue until either the maximum number of epochs is reached 
		//or the decrease in the error is smaller than the threshold
		} while (epoch < maxEpochs && totalError >= errorThreshold &&  Math.abs(totalError - previousError) > minimumChange);
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
	
	public void forwardPass(double[] currentInput, boolean training) {
		double activationSum = 0;
		//First, feed the input to the first hidden layer
		//double[] currentInput = input;
		//System.out.println("Starting input length: " + currentInput.length);
		//Stores the output to be fed to the next hidden layer as input
		double[] outputArray = new double[nHidden + 1]; 
		//Loop through hidden layers
		for (int i=0; i<nHiddenLayers; i++) {
			//Calculate the output for each node in the hidden layer
			for (int j=0; j<hiddenList.get(i).size(); j++) {
				hiddenList.get(i).get(j).setInput(currentInput);
				hiddenList.get(i).get(j).setActivation();
				if(this instanceof Critic){
					//Don't compute sigmoid when then input/activation is 0
					if(i != 10){
						hiddenList.get(i).get(j).setSigmoidOutput();
					}
					else if(i == 10){
						hiddenList.get(i).get(j).setReluOutput();
					}
					
				} 
				else{ 
					if(i != 10){   
						hiddenList.get(i).get(j).setSigmoidOutput();	
					}
					else{
						hiddenList.get(i).get(j).setReluOutput();
					}
					
				}
				//System.out.println("Act.: " + hiddenList.get(i).get(j).getActivation());
				outputArray[j] = hiddenList.get(i).get(j).getOutput();
			}
			//Set the final output to be the bias
			outputArray[nHidden] = 1.0;
			//System.out.println("Input for next layer length: " + outputArray.length);
			//Store the output of the hidden layer as input for the next layer
			currentInput = Arrays.copyOf(outputArray,outputArray.length);
		}
		//double softmaxSum = 0;
		for (NeuronJelle n : outputLayer) {
			//System.out.println("Input length for output layer: " + currentInput.length);
			n.setInput(currentInput);
			n.setActivation();
		}
		
		for (NeuronJelle n : outputLayer) {
			//If training on demonstration data, use softmax, otherwise linear activation
			if(!training){
				n.setOutput(n.getActivation());
			}
			else{
				//calculate total activation sum of each neuron for softmax
				activationSum += Math.exp(n.getActivation() / temperature);
			}
		}	
		//set corresponding softmax output for each neuron
		if(training){
			for (NeuronJelle n : outputLayer) {
				n.setOutput(Math.exp(n.getActivation() / temperature) / activationSum);
			}
		}
	}
	
	public double backwardPass(int patternIndex) {
		double totalError=0;
		ArrayList<NeuronJelle> nextLayer;
		
		//Calculate gradients for nodes in the output layer
		for(int i = 0; i < nOutput; i++){
			outputLayer.get(i).setOutputGradient(target[patternIndex][i]);		
		}
		
		nextLayer = new ArrayList<NeuronJelle>(outputLayer);
		
		//Loop through all hidden layers in reverse order
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<hiddenList.get(i).size(); j++) { 
				//Calculate gradients for nodes in each hidden layer
				hiddenList.get(i).get(j).setHiddenGradient(j, nextLayer, i, this instanceof Critic);
			}
			nextLayer = new ArrayList<NeuronJelle>(hiddenList.get(i));
		}
		
		//Update the weights for nodes in the output layer
		for(int i = 0; i < nOutput; i++){
			outputLayer.get(i).updateWeights(target[patternIndex][i], learningRate);
			//totalError += outputLayer.get(i).getError();
			totalError += outputLayer.get(i).getError();
			//System.out.println(totalError);
		}
		
		for (int i=nHiddenLayers-1; i>=0; i--) {
			//Train each node in the hidden layer
			for (int j=0; j<hiddenList.get(i).size(); j++) {
				//Update the weights for each node in the hidden layer
				hiddenList.get(i).get(j).updateWeights(0, learningRate);
			}
		}
		return totalError;
		
	}
	
	public int pickOutputByProbability() {
		double activationSum = 0.0;
		//convert output layer output to softmax for action selection
		for(NeuronJelle n: outputLayer){
			//calculate total activation sum of each neuron for softmax
			activationSum += Math.exp(n.getActivation() / temperature);		
		}
		for(NeuronJelle n: outputLayer){
			n.setOutput(Math.exp(n.getActivation() / temperature) / activationSum);
			System.out.print("Output node " + outputLayer.indexOf(n) + ": " + n.getActivation());
			System.out.println(" " + n.getOutput());		
		}
		double p = Math.random();
		double cumulativeProbability = 0.0;
		
		for (NeuronJelle n : outputLayer) {
		    cumulativeProbability += n.getOutput();
		    
		    if (p <= cumulativeProbability) {
		    	System.out.println("Action chosen: " + outputLayer.indexOf(n));
		        return outputLayer.indexOf(n);
		    }
		}
		
		return 0;
	}
	
	public void propagateFeedback(double[] state, double feedback, int action){
		System.out.println("TD-error: " + feedback); 
		//if(feedback >= 0){ 
			//Present the state, then backpropagate for improvement
			forwardPass(state, true);  
			System.out.println("ACTION BEFORE FEEDBACK BACKPROP: " + action);
			//Create the new targets, using the feedback from the critic: 
			//target = new double[1][nOutput];
			if(feedback >= 0){		
				//new_target = old_target + feedback
				for(int i = 0; i < nOutput; i++){
					target[0][i] = 0;
				}
				//Action taken in previous state has to be positively or negatively reinforced
				target[0][action] = 1; 
			}
			else{
				//double activationSum = 0.0;
				//convert output layer output to softmax for action selection
				/*for(NeuronJelle n: outputLayer){
					//calculate total activation sum of each neuron for softmax
					activationSum += Math.exp(n.getActivation() / temperature);		
				}
				for(NeuronJelle n: outputLayer){
					n.setOutput(Math.exp(n.getActivation() / temperature) / activationSum);	
				}*/
				for(int i = 0; i < nOutput; i++){
					target[0][i] = outputLayer.get(i).getOutput();
				}
				/*for(NeuronJelle n: outputLayer){
					n.setOutput(n.getActivation());
				}*/
				//Action taken in previous state has to be positively or negatively reinforced
				target[0][action] = 0;
			}
			for(int i = 0; i < nOutput; i++){
				System.out.println(target[0][i]);
			}
			for(int i = 0; i < nOutput; i++){
				System.out.println("Target for output node " + i + ": " + target[0][i]);
			} 
			for(int i = 0; i < nOutput; i++){
				System.out.println("ACTOR OUTPUT BEFORE BACKPROP; NODE: " + i + ": " + outputLayer.get(i).getOutput());
			}	
			backwardPass(0);
			forwardPass(state, true); 
			for(int i = 0; i < nOutput; i++){
				System.out.println("ACTOR OUTPUT AFTER BACKPROP; NODE: " + i + ": " + outputLayer.get(i).getOutput());
			}	
		//}
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
	
	public void testNetwork() {
		
		for (int i=0; i<this.input.length; i++) {
			
			this.forwardPass(this.input[i], true);
			//print pattern number
			System.out.println("Pattern " + i);
			
			//print input for each pattern
			for(int j=0; j< this.input[i].length; j++) {
				System.out.println("	Input: " + this.input[i][j]);	
			}
			//print all output of the output nodes for each pattern
			for(int n = 0; n < nOutput; n++){					
				System.out.println("	Output " + n + ": " + (n == maxOutput() ? 1.0 : 0.0));
			}
		}
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
	
	public int presentInput(double[] input) {
		
		//print input
		//for(int i = 0; i < input.length; i++){
			//System.out.print(input[i] + ",");
		//}
		//System.out.println();
		
		//System.out.println("Mario jumping? " + input[0]);
		//System.out.println("Nearest barrel? " + input[1]);
		//System.out.println("Barrel to right? " + input[2]);
		//System.out.println("Barrel on same level? " + input[3]);
		
		//present game state to the network, calculate output
		forwardPass(input, false);
		//System.out.println(binaryToInt());	
		
		//activation of output nodes
		
		return pickOutputByProbability();	
		
	}
	
	public void printInput() {
		for(int i=0; i<input.length; i++) {
			for(int j=0; j<nInput; j++) {
				System.out.print(input[i][j] + ", ");
				if(j == nInput - 1) {
					System.out.println("");
				}
			}
		}
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
	
	public void setTemperature(double temperature){
		this.temperature = temperature;
	}
	
	public double getLearningRate(){
		return learningRate;
	}
	public void setLearningRate(double learningRate){
		this.learningRate = learningRate; 
	}
	public ArrayList<ArrayList<NeuronJelle>> getHiddenLayers(){
		return hiddenList;
	}
	public ArrayList<NeuronJelle> getOutputLayer(){
		return outputLayer;
	}
	
		
}
