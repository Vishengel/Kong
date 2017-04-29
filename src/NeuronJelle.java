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
	//store previous weight changes for momentum
	private double previousWeightChange = 0;
	
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

		for(int i=0; i<this.nInputs; i++) {
			//System.out.println(i + "; " + this.input[i]);
			//System.out.println(this.weights[i]);
			//System.out.println(weights.length);
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
	
	public void setReLuOutput(){
		this.output = ReLU(this.activation);
	}
	
	public void setSoftmaxOutput(ArrayList<NeuronJelle> outputLayer) {
		this.output = softmax(this.activation, outputLayer);
	}
	
	public double getGradient() {
		return this.gradient;
	}
	
	public void setHiddenGradient(int nodeIndex, ArrayList<NeuronJelle> nextLayer, int hiddenLayer, boolean isCritic) {
		double sum = 0;
		for (NeuronJelle n : nextLayer) {
			sum += n.getGradient() * n.getWeights()[nodeIndex];
		}
		if(isCritic){
			this.gradient = ReLUPrime(this.activation) * sum;
		}
		else{
			this.gradient = sigmoidPrime(this.activation) * sum;
		}
		
	}
	
	public void setHiddenOutputFunction(boolean isCritic){
		//The critic uses the ReLU function instead of the sigmoid function
		if(isCritic){
			setReLuOutput();
		}
		else{
			setSigmoidOutput();
		}
	}
	
	
	public void setOutputFunction(ArrayList<NeuronJelle> outputLayer, boolean isCritic){
		//The critic uses the linear activation instead of the softmax output for regression
		if(isCritic){
			this.output = ReLU(this.activation);
		}
		else{
			setSoftmaxOutput(outputLayer);
		}
	}
	
	
	public void setOutputGradient(double target, boolean isCritic) {
		//The critic uses the activation instead of sigmoid in the output layer
		if(isCritic){
			this.gradient = ReLUPrime(this.activation * (target - this.output));
		}
		else{
			this.gradient = sigmoidPrime(this.activation) * (target - this.output);
		}
		
	}
	
	public void updateWeights(double target, double learningRate, double momentum) {
		this.error = 0.5*(target - this.output)*(target - this.output);
		this.crossEntropy = target * Math.log(this.output) + (1 - target)*Math.log(1 - this.output);
		
		
		for(int i=0; i<this.weights.length; i++) {
			this.weights[i] += learningRate*gradient*this.input[i] + (momentum * this.previousWeightChange);
			this.previousWeightChange = learningRate*gradient*this.input[i] + (momentum * this.previousWeightChange);
			//System.out.println(this.weights[i]);
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
	
	public double ReLU(double activation){
		return Math.max(0, activation);
	}
	
	public double ReLUPrime(double activation){
		if(activation < 0){
			return 0;
		}
		return 1;
	}
	public double tanh(double activation){
		return 2 / (1 + Math.pow(Math.E, -2*activation)) - 1;
	}
	
	public double tanhPrime(double activation){
		return 1 - Math.pow(tanh(activation), 2);
	}
	
	public double softmax(double activation, ArrayList<NeuronJelle> outputLayer) {
		double activationSum = 0;
		
		for(NeuronJelle n : outputLayer) {
			activationSum += Math.exp(n.getActivation());
		}
		
		return Math.exp(activation) / activationSum;
	}
	
	public void printWeights() {
		int i=0;
		for(double w : weights) {
			System.out.println("Weight " + i++ + ": " + w);
		}
	}
}