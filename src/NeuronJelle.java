import java.util.ArrayList;

public class NeuronJelle {
	private int nInputs;
	private double[] input;
	private double activation;
	private double[] weights;
	private double gradient;
	private double output;
	private double error;
	private double crossEntropy;
	
	public NeuronJelle(int nInputs) {
		this.nInputs = nInputs;
		input = new double[nInputs];
		weights = new double[nInputs];
		this.initWeights();
	}
	
	public void initWeights() {
		for(int i=0; i<nInputs; i++) {
			weights[i] = -1.0 + (Math.random() * 2.0);
		}
	}
	
	public double[] getInput() {
		return this.input;
	}
	
	public void setInput(double[] input) {
		this.input = input;
	}
	
	public double getActivation() {
		return this.activation;
	}
	
	public void setActivation() {
		this.activation = 0;

		for(int i=0; i<this.input.length; i++) {
			//System.out.println(this.input[i]);
			//System.out.println(this.weights[i]);
			this.activation += this.input[i] * this.weights[i];
		}
		//System.out.println(this.activation);
		//System.out.println("___");
	}
	
	public double getOutput() {
		return this.output;
	}
	
	public void setOutput() {
		this.output = sigmoid(this.activation);
	}
	
	public double getGradient() {
		return this.gradient;
	}
	
	public void setHiddenGradient(int nodeIndex, ArrayList<NeuronJelle> nextLayer) {
		double sum = 0;
		
		for (NeuronJelle n : nextLayer) {
			sum += n.getGradient() * n.getWeights()[nodeIndex];
		}
		
		this.gradient = sigmoidPrime(this.activation) * sum;
	}
	
	public void setOutputGradient(double target) {
		this.gradient = sigmoidPrime(this.activation) * (target - this.output);
	}
	
	public void updateWeights(double target, double learningRate) {
		this.error = 0.5*(target - this.output)*(target - this.output);
		this.crossEntropy = target * Math.log(this.output) + (1 - target)*Math.log(1 - this.output);
		
		for(int i=0; i<this.weights.length; i++) {
			this.weights[i] += learningRate*gradient*this.input[i];
		}
	}
	
	public double[] getWeights() {
		return this.weights;
	}
	
	public double getError() {
		return this.error;
	}
	
	public double getCrossEntropy() {
		return this.crossEntropy;
	}
	
	public double sigmoid(double activation){
		 return (1/( 1 + Math.pow(Math.E,(-1*activation))));
	}
	
	public double sigmoidPrime(double activation){
		return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));
	}
	
	public void printWeights() {
		int i=0;
		for(double w : weights) {
			System.out.println("Weight " + i++ + ": " + w);
		}
	}
}