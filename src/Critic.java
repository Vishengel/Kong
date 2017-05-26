import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {
	
	double discount = 0.98; 

	
	
	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName) {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName);
		target = new double[input.length][1];
		errorThreshold = 0.004;
	}
	
	
	
	public void trainNetwork() {
		double totalError = 0, previousTotalError; 
		double epoch = 0;
		do {
			//print training progress
			System.out.println("Learning critic.. " + Math.round((epoch / maxEpochs) * 100) + "%");
			//System.out.println("Current epoch: " + epoch);
			//System.out.println("Training completed in " + epoch + " epochs");
			previousTotalError = totalError;
			totalError = 0;
			
			//this.shuffleInput();
			
			for (int i=0; i < this.input.length; i++) {
				forwardPass(this.input[i], false);
				//calculate the target for the critic: target = reward + (discount * nextValue) - previousValue
				double reward = input[i][nInput-1];
				double nextStateValue = outputLayer.get(0).getOutput();
				if(i > 0){
					forwardPass(this.input[i-1], false);
					double t = reward + (discount * nextStateValue);
					setTarget(t);
					totalError += this.backwardPass(0);
				}
				
			}
		
			totalError /= this.input.length;
			
			System.out.println(totalError);
			
			epoch++;
		//Continue until either the maximum number of epochs is reached 
		//or the decrease in the error is smaller than the threshold
		} while (epoch < maxEpochs && totalError >= errorThreshold);
		System.out.println("Training completed in " + epoch + " epochs");
	}
	
	public void initNetwork(){
		//read the demonstration data to be learned from 
		double[][] initialInput = FH.readFile(fileName, nInput, 7);
		input = new double[initialInput.length][nInput];
		//filter out the target values from the input array
		for(int i = 0; i < initialInput.length; i++){
			//System.out.println("Pattern: " + i);
			for(int j = 0; j < nInput; j++){	
				input[i][j] = initialInput[i][j];
			}	
		}					
		initializeLayers();		
	}
	
	//calculate the target for the Critic to learn:  V(previousState) = reward + ( discount * V(state) )
		public void trainCritic(double[] state, double[] previousState, double reward, boolean marioKilled){
			//present the current state to the critic
			forwardPass(state, false);
			//look at the output of the critic
			//If mario is dead, the value of the next state is just 0
			double valueNextState = 0;
			if(!marioKilled){
				valueNextState = outputLayer.get(0).getOutput();
			}		
			
			double target = reward + (discount * valueNextState);		
			//System.out.println("Target:" + target);
			setTarget(target);
			//present previous state to the Critic and apply the target in backpropagation.
			forwardPass(previousState, false); 
			backwardPass(0);
			//System.out.println("Value of state: " + outputLayer.get(0).getOutput());
			
			
		}
		
		//This function calculated the feedback that the critic feeds back to the actor
		public double calculateFeedback(double[] state, double[] previousState, double reward, boolean marioKilled){
			forwardPass(state, false); 
			double stateValue = 0; 
			//If mario is dead, the value of the next state is just 0
			if(!marioKilled){
				//Get the estimated value of the new state
				stateValue = outputLayer.get(0).getOutput();
			}
			forwardPass(previousState, false);
			//Get the estimated value of the previous state
			double previousStateValue = outputLayer.get(0).getOutput();
			//Calculate the Temporal-Difference error: reward_t-1 + (discount * value_t) - value_t-1
			double feedback = reward + (discount * stateValue) - previousStateValue;
			//System.out.println("Current state value: " + stateValue);
			//System.out.println("Value of previous state: " + previousStateValue);
			System.out.println("TD-error: " + feedback);  
		    return feedback; 
			
		}
	
	
	public void setTarget(double value){
			target[0][0] = value; 
	}
}
