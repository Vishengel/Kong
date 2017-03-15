import java.util.ArrayList;


public class MLP {
	//amount of inputs
	private int amountInput = 3;
	//amount of nodes in the hidden layer
	private int amountHidden = 3;
	//amount of nodes in the output layer
	private int amountOutput = 1;
	//amount of patterns
	private int amountPatterns = 4;
	//define the learning rate
	private double learningRate = 0.3;
	
	private int[] target = new int[] {0, 1, 1, 1};
	private int[][] inputs = new int[][] {
			{0,0,-1}, 
			{0,1,-1}, 
			{1,0,-1}, 
			{1,1,-1}
			};
	//matrix of weights going from inputs to hidden nodes
	private double[][] weightsHidden = new double[amountInput][amountHidden];
	//matrix of weights going from hidden nodes to output nodes
	private double[][] weightsOutput = new double[amountHidden][amountOutput];	
	
	//list of hidden nodes
	ArrayList<Neuron> hiddenNodes;
	//list of output nodes
	ArrayList<Neuron> outputNodes;
	public MLP(){
		//initialize weight matrices
		for(int i = 0; i < amountInput; i++){
			for(int j = 0; j < amountHidden; j++ ){
				weightsHidden[i][j] = Math.random();
				//System.out.print(" " + weightsHidden[i][j]);
			}
			//System.out.println();
		}
		
		for(int i = 0; i < amountHidden; i++){
			for(int j = 0; j < amountOutput; j++ ){
				weightsOutput[i][j] = Math.random();
				//System.out.print(" " + weightsOutput[i][j]);
			}
			//System.out.println();
		}
		//initalize neuron lists
		hiddenNodes = new ArrayList<Neuron>();
		outputNodes = new ArrayList<Neuron>();
		for(int i = 0; i < amountHidden; i++){
			Neuron n = new Neuron();
			hiddenNodes.add(n);
		}
		
		for(int i = 0; i < amountOutput; i++){
			Neuron n = new Neuron();
			outputNodes.add(n);
		}
		
	}
	
	public double sigmoidPrime(double activation){
		return Math.pow(Math.E, activation) / ((1 + Math.pow(Math.E, activation))* (1 + Math.pow(Math.E, activation)));
	}
	
	public double sigmoid(double activation){
		 return (1/( 1 + Math.pow(Math.E,(-1*activation))));
	}
	
	public void calculateHiddenOutput(){
		//calculate activation of one hidden node: input vector * hidden weight matrix
		for(int pattern = 0; pattern < amountPatterns; pattern++){
			System.out.println("Pattern: " + pattern); 
			//current pattern
			int[] input = inputs[pattern];
			//loop through the hidden weights and calculate the hidden activation
			for(int i = 0; i < amountHidden; i++){
				double activation = 0;
				for(int j = 0; j < amountInput; j++){
					activation += input[j] * weightsHidden[j][i];
				}
				hiddenNodes.get(i).setActivation(activation);
				//calculate output of hidden layer by using the sigmoid function
				hiddenNodes.get(i).setOutput(sigmoid(activation));
			}
			//print output of hidden layer
			for(int i = 0; i < amountHidden; i++){
				//System.out.println(hiddenNodes.get(i).getOutput());
			}
		}
		
	}
	
	public void calculateOutput(){
				for(int pattern = 0; pattern < amountPatterns; pattern++){
					System.out.println("Pattern: " + pattern);
					//reset activation in all hidden nodes
	
					int[] input = inputs[pattern];
					for(int i = 0; i < amountOutput; i++){
						double activation = 0;
						for(int j = 0; j < amountHidden; j++){
							activation += input[j] * weightsOutput[j][i];
						}
						outputNodes.get(i).setActivation(activation);
						
						outputNodes.get(i).setOutput(sigmoid(activation));
					
						double gradient = sigmoidPrime(activation) * (target[pattern] - outputNodes.get(i).getOutput());
						outputNodes.get(i).setGradient(gradient);
						
					}
					
					for(int i = 0; i < amountOutput; i++){
						//System.out.println("Gradient of output node " + i + ": " + outputNodes.get(i).getGradient());
						System.out.println("Output: " + outputNodes.get(i).getOutput());
					}
				}
				
	}
	
	public void forwardPass(){
		System.out.println("----Hidden layer----");
		calculateHiddenOutput();
		System.out.println("----output layer----");
		calculateOutput();
	}
	
	public void backPropagation(){
		//calculate the local gradient of the hidden layer
		for(int i= 0; i < amountHidden; i++){
			double gradient = sigmoidPrime(hiddenNodes.get(i).getActivation());
			double gradientOutput = 0;
			//loop over the output weight matrix and sum over the gradients of the output layer
			for(int col = 0; col < amountOutput; col++){
				gradientOutput += outputNodes.get(col).getGradient();
			}
			gradient = gradient * gradientOutput;
			hiddenNodes.get(i).setGradient(gradient);
			System.out.println("Gradient of hidden node " + i + ": " + hiddenNodes.get(i).getGradient());
		}
		//calculate weight changes of hidden layer
		for(int pattern = 0; pattern < amountPatterns; pattern++){
			int[] input = inputs[pattern];
			for(int i = 0; i < amountInput; i++){
				for(int j = 0; j < amountHidden; j++){
					weightsHidden[i][j] += learningRate * input[j] * hiddenNodes.get(j).getGradient();
				}
			}
			//calculate weight changes of output layer
			for(int i = 0; i < amountHidden; i++){
				for(int j = 0; j < amountOutput; j++){
					weightsOutput[i][j] += learningRate * hiddenNodes.get(j).getOutput() * outputNodes.get(j).getGradient();
				}
			}
		}
		
	}
	
	public void testNetwork(){
		forwardPass();
	}
	
	public void trainNetwork(){
		int epochs = 1000;
		int epoch = 0;
		while(epoch < epochs){
			forwardPass();
			backPropagation();
			epoch++;
		}
		testNetwork();
	}
	
}
