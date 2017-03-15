import java.util.ArrayList;


//This class represents the nodes in the neural network, both in the hidden and output layer
public class Neuron {
	
	private double activation = 0;
	private double output = 0;
	private double gradient = 0;
		
	public double getActivation(){
		return activation;
	}
	public void setActivation(double activation){
		this.activation = activation;
	}
	public double getGradient(){
		return gradient;
	}
	public void setGradient(double gradient){
		this.gradient = gradient;
	}
	public double getOutput(){
		return output;
	}
	public void setOutput(double output){
		this.output = output;
	}
	
	
}


