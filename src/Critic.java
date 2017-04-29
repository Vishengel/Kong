import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {

	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName) {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName);		
	}
	
	public void initNetwork(){
		initializeLayers();
		//the target array contains the target: reward(next_state) + ( discount * value(next_state) )
		target = new double[1][1];
		System.out.println("Critic");
		
	}
	
	public void initializeLayers(){
		ArrayList<NeuronJelle> hiddenLayer = new ArrayList<NeuronJelle>();
		int nWeights = nInput;
		//Initialize hidden layers
		for (int i=0; i<nHiddenLayers; i++) {
			//Clear the hiddenlayer after each iteration
			hiddenLayer = new ArrayList<NeuronJelle>();
			for (int j=0; j<nHidden; j++) {
				//Add nWeights amount of nodes to the hidden layer
				//For the first iteration, this amount is equal to the number of inputs
				//Afterwards, this amount is equal to the number of nodes in the previous layer
				hiddenLayer.add(new NeuronJelle(nWeights));
			}
			//Add a node for the bias
			//hiddenLayer.add(new NeuronJelle(0));
			//Set the amount of weights for each neuron in the next layer
			//to be the same as the amount of hidden neurons in the current layer
			nWeights = hiddenLayer.size();
			//Make sure the output of the bias neuron is -1
			//hiddenLayer.get(nWeights-1).setOutput(-1);
			//Add the hidden layer to the list of hidden layers
			hiddenList.add(hiddenLayer);
		}
		//Initialize output layer
		for(int i=0; i<nOutput; i++) {
			outputLayer.add(new NeuronJelle(nWeights));
		}
	}
	
	public void setTarget(double value){
		target[0][0] = value;
	}
}
