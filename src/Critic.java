import java.io.IOException;
import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {
	
	double discount = 0.999;  
	double[] rewards;
	double[][] target = new double[1][1];
	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName, boolean loadNetwork) throws IOException {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName, loadNetwork);
		//System.out.println("n in: " + target.length);
		errorThreshold = 0.525;  
		learningRate = 0.001;
		//minimumChange = 0;
	}
	
	
	
	public void trainNetwork() {
		target = new double[input.length][1];
		double totalError = 0, previousTotalError, epoch = 0, reward, t; 

		do {
			//print training progress
			System.out.println("Learning critic.. " + Math.round((epoch / maxEpochs) * 100) + "%");
			//System.out.println("Current epoch: " + epoch);
			//System.out.println("Training completed in " + epoch + " epochs");
			previousTotalError = totalError;
			totalError = 0;
			
			//this.shuffleInput();
			double nextStateValue = 0;
			for (int i=0; i < this.input.length; i++) {
				forwardPass(this.input[i], false);
				//calculate the target for the critic: target = reward + (discount * nextValue) - previousValue
				reward = rewards[i];
				if(reward <= -20 || reward >= 100){
					nextStateValue = 0;
				}
				else{
					nextStateValue = outputLayer.get(0).getOutput();
				}
				if(i > 0){
					forwardPass(this.input[i-1], false);
					t = reward + (discount * nextStateValue);
					setTarget(t);
					totalError += backwardPass(0);
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
	@Override
	public void initNetwork(){
			System.out.println("Critic init called!");
			//read the demonstration data to be learned from 
			double[][] initialInput = FH.readFile(fileName, nInput, nOutput);
			input = new double[initialInput.length][nInput - 1];
			rewards = new double[initialInput.length];
			//filter out the target values from the input array
			for(int i = 0; i < initialInput.length; i++){
				//System.out.println("Pattern: " + i);
				for(int j = 0; j < nInput-1; j++){	
					input[i][j] = initialInput[i][j];
				}
				rewards[i] = initialInput[i][nInput - 1];
				//System.out.println("Reward at pattern " + i + ": " + rewards[i]);
			}					
			initializeLayers();	
	}
	
	//calculate the target for the Critic to learn:  V(previousState) = reward + ( discount * V(state) )
		public void trainCritic(double[] state, double[] previousState, double reward, boolean marioKilled, boolean gameWon){
			//present the current state to the critic
			forwardPass(state, false);
			//look at the output of the critic
			//If mario is dead, the value of the next state is just 0
			double valueNextState = 0;
			if(!marioKilled && !gameWon){
				valueNextState = outputLayer.get(0).getOutput();
			}
			//present previous state to the Critic and apply the target in backpropagation.
			forwardPass(previousState, false);
			System.out.println("Reward received: " + reward);
			
			System.out.println("Value of current state: " + valueNextState);
			double target = reward + (discount * valueNextState);		
			System.out.println("Target for previous state: " + target);
			//System.out.println("Target:" + target);
			setTarget(target); 
			System.out.println("Value of previous state before backprop: " + outputLayer.get(0).getOutput());
			backwardPass(0);
			forwardPass(previousState, false); 
			System.out.println("Value of previous state after backprop: " + outputLayer.get(0).getOutput());					
		}
		
		//This function calculated the feedback that the critic feeds back to the actor
		public double calculateFeedback(double[] state, double[] previousState, double reward, boolean marioKilled, boolean gameWon){
			forwardPass(state, false); 
			double stateValue = 0; 
			//If mario is dead, the value of the next state is just 0
			if(!marioKilled && !gameWon){
				//Get the estimated value of the new state
				stateValue = outputLayer.get(0).getOutput();
			}
			forwardPass(previousState, false);
			//Get the estimated value of the previous state
			double previousStateValue = outputLayer.get(0).getOutput();
	
			if(previousStateValue < 0){
				previousStateValue = previousStateValue * -1;
			} 
			//Calculate the Temporal-Difference error: reward_t-1 + (discount * value_t) - value_t-1
			double feedback = reward + (discount * stateValue) - previousStateValue;
			//System.out.println("Current state value: " + stateValue);
			//System.out.println("Value of previous state: " + previousStateValue);
			System.out.println("TD-error: " + feedback);  
		    return feedback; 
			
		}
	
	public double[][] getInput() {
		return this.input;
	}
	
	public void setTarget(double value){
			target[0][0] = value; 
	}
}
