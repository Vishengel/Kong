import java.util.ArrayList;

public class NeuronJelle {
	private int nInputs;
	private int[] input;
	private double activation;
	private double[] weights;
	private double delta;
	private double output;
	private double error;
	
	public NeuronJelle(int nInputs) {
		this.nInputs = nInputs;
		input = new int[nInputs];
		weights = new double[nInputs];
		this.initWeights();
	}
	
	public void initWeights() {
		for(int i=0; i<nInputs; i++) {
			weights[i] = Math.random();
		}
	}
	
	public void setInput(int[] input) {
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
	
	public void updateWeights(int target, double learningRate) {
		this.output = sigmoid(this.activation);
		this.delta = target - this.output;
		this.error = 0.5*delta*delta;
		
		for(int i=0; i<this.weights.length; i++) {
			this.weights[i] += learningRate*sigmoidPrime(this.activation)*delta*this.input[i];
		}
	}
	
	public double[] getWeights() {
		return this.weights;
	}
	
	public double getError() {
		return this.error;
	}
	
	public double sigmoid(double activation){
		 return (1/( 1 + Math.pow(Math.E,(-1*activation))));
	}
	
	public double sigmoidPrime(double activation){
		return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));
	}
}
