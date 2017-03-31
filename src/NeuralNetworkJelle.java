import java.util.ArrayList;
//Implementation of a simple TLU with one output neuron
public class NeuralNetworkJelle {
	//Network parameters
	//Amount of input nodes
	private int nInputs = 3;
	//Amount of output nodes
	private int nOutputs = 1;
	
	//All four possible input combinations of two input boolean values, plus the threshold multiplier
	private double[][] input = new double[][] {
												{0.0, 0.0, -1.0},
												{1.0, 0.0, -1.0},
												{0.0, 1.0, -1.0},
												{1.0, 1.0, -1.0}
												};
	//The target values corresponding with each input				
	private double[] target = new double[] {0.0, 1.0, 1.0, 1.0};
	
	//A list of output neurons
	private ArrayList<NeuronJelle> outputList = new ArrayList<NeuronJelle>();
	
	private double learningRate = 0.3;
	private double errorThreshold = 0.0001;
	private int maxEpochs = 50000;
	
	public NeuralNetworkJelle() {
		this.initNetwork();
	}
	
	public void initNetwork() {
		for(int i=0; i<nOutputs; i++) {
			outputList.add(new NeuronJelle(nInputs));
		}
	}
	
	public void trainNetwork() {
		double totalError = 0, previousTotalError;
		int epoch = 0;
		do {
			//System.out.println("Training completed in " + epoch + " epochs");
			previousTotalError = totalError;
			totalError = 0;
			//Loop through all input patterns
			for(int i=0; i<input.length; i++) {
				for(NeuronJelle n : outputList) {	
					//Set the neuron's input to the current pattern
					n.setInput(input[i]);
					//Calculate the activation
					n.setActivation();
					//Set the output
					n.setOutput();
					//Update the weights
					n.setOutputGradient(target[i]);
					n.updateWeights(target[i], learningRate);
					//Add the neuron's error to the total error
					totalError += n.getError();
				}
			}
			epoch++;
		//Continue until either the maximum number of epochs is reached 
		//or the decrease in the error is smaller than the threshold
		} while (Math.abs(previousTotalError - totalError) > errorThreshold && epoch < maxEpochs);
		System.out.println("Training completed in " + epoch + " epochs");
	}
	
	public void printResults() {
		//Print weights and threshold of each output node
		for(NeuronJelle n : outputList) {
			System.out.println("Output node " + outputList.indexOf(n) + ":");
			for(int i=0; i<n.getWeights().length-1; i++) {
				System.out.println("	Weight " + i + ": " + n.getWeights()[i]);
			}
			System.out.println("	Threshold: " + n.getWeights()[n.getWeights().length-1]);
			
			for(int i=0; i<input.length; i++) {
				System.out.println("Pattern " + i);	
				
				for(int j=0; j<input[i].length-1; j++) {
					System.out.println("	Input: " + input[i][j]);	
				}
				
				n.setInput(input[i]);
				n.setActivation();
				System.out.println("	Output: " + (n.getActivation() >= 0 ? 1 : 0));	
			}
		}
	}
}
