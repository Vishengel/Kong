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
		//System.out.println("Weights: " + weights.length); 
		this.initWeights();
	}
	
	public void initWeights() {
		for(int i=0; i<nInputs; i++) {
			weights[i] = 0.2 *(-1.0 + (Math.random() * 2.0));
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

		for(int i=0; i<this.nInputs; i++) {
			this.activation += this.input[i] * this.weights[i];
		}
		//System.out.println(this.activation);
		//System.out.println("___");
	}
	
	public double getOutput() {
		return this.output;
	}
	
	public void setOutput(double output) {
		this.output = output;
	}
	
	public void setSigmoidOutput() {
		this.output = sigmoid(this.activation);
	}
	
	public void setReluOutput(){
		this.output = (this.activation > 0 ? this.activation : 0);
	}
	
	/*public void setSoftmaxOutput(ArrayList<NeuronJelle> outputLayer, double temperature) {
		this.output = softmax(this.activation, outputLayer, temperature);
	}*/
	
	public double getGradient() {
		return this.gradient;
	}
	
	public void setHiddenGradient(int nodeIndex, ArrayList<NeuronJelle> nextLayer, int hiddenLayer, boolean isCritic) {
		double sum = 0;
		for (NeuronJelle n : nextLayer) {
			sum += n.getGradient() * n.getWeights()[nodeIndex];
		}
		if(!isCritic){
			if(hiddenLayer != 10){ 
				this.gradient = sigmoidPrime(this.activation) * sum;	
			}
			else{
				this.gradient = reluPrime(this.activation) * sum;
			}
		}
		else{
			if(hiddenLayer == 10){
				this.gradient = reluPrime(this.activation) * sum;
			}
			else{
				this.gradient = sigmoidPrime(this.activation) * sum;	
			}
		}
	}
	
	
	public void setOutputGradient (double target) {
		this.gradient = (target - this.output);
	}
	
	public void updateWeights(double target, double learningRate) {
		this.error = 0.5*(target - this.output)*(target - this.output);
		this.crossEntropy = target * Math.log(this.output) + (1 - target)*Math.log(1 - this.output);
		
		
		for(int i=0; i<this.weights.length; i++) {
			//this.weights[i] += learningRate*gradient*this.input[i] + (momentum * this.previousWeightChange);
			//this.previousWeightChange = learningRate*gradient*this.input[i] + (momentum * this.previousWeightChange);

			this.weights[i] += learningRate*gradient*this.input[i];
			//this.previousWeightChange = learningRate*gradient*this.input[i];
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
		double output = sigmoid(activation);
		return output * (1-this.output);
		//return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));		
	}
	
	public double reluPrime(double activation){
		return (activation > 0 ? 1 : 0);
	}
	
	public double tanh(double activation){
		return 2 / (1 + Math.pow(Math.E, -2*activation)) - 1;
	}
	
	public double tanhPrime(double activation){
		return 1 - Math.pow(tanh(activation), 2);
	}
	
	/*public double softmax(double activation, ArrayList<NeuronJelle> outputLayer, double temperature) {
		double activationSum = 0;
		
		for(NeuronJelle n : outputLayer) {
			activationSum += Math.exp(n.getActivation() / temperature);
		}
		
		return Math.exp(activation / temperature) / activationSum;
	}*/
	
	
	public void printWeights() {
		int i=0;
		for(double w : weights) {
			System.out.println("Weight " + i++ + ": " + w);
		}
	}
}
