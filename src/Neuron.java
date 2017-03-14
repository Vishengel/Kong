import java.util.ArrayList;


//This class represents the nodes in the neural network, both in the hidden and output layer
public class Neuron {
	
	//this array contains all the weight of the neuron, plus 1 extra for the threshold
	private ArrayList<Double> weights;
	private int nWeights;
	private double activation = 0;
		
	public double getActivation(){
		return activation;
	}
	public void setActivation(double activation){
		this.activation = activation;
	}
	public ArrayList<Double> getWeights(){
		return weights;
	}
	//initialize the weights to random values
	public void initializeWeights(int nWeights) {	
		this.nWeights = nWeights;
		weights = new ArrayList<Double>();
		
		for(int i = 0; i < nWeights; i++){
			double weight = Math.random();
			weights.add(weight);
		}
	}
	 public int getNWeights(){
		 return nWeights;
	 }
	
}


