import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {
	
	double discount = 0.6; 
	
	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName) {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName);
		learningRate = 0.05;
	}
	
	public void initNetwork(){
		initializeLayers();
		//System.out.println(nInput);
		//the target array contains the target: reward(next_state) + ( discount * value(next_state) )
		target = new double[1][1];
		
		
	}
	
	//calculate the target for the Critic to learn:  V(previousState) = reward + ( discount * V(state) )
		public void trainCritic(double[] state, double[] previousState, double reward, boolean marioKilled){
			//present the current state to the critic
			forwardPass(state);
			//look at the output of the critic
			//System.out.println("Reward: " + reward);
			
			//If mario is dead, the value of the next state is just 0
			double valueNextState = 0;
			if(!marioKilled){
				valueNextState = outputLayer.get(0).getOutput();
			}		
			double target = reward + (discount * valueNextState);		
			//System.out.println("Target:" + target);
			setTarget(target);
			//present previous state to the Critic and apply the target in backpropagation.
			forwardPass(previousState);
			//System.out.print("Target: " + target);
			//System.out.println(" Value: " + outputLayer.get(0).getOutput());
			//System.out.println("State value: " + outputLayer.get(0).getOutput());
			//System.out.println("Target: " + target); 
			backwardPass(0);
			//System.out.println("Critic error: " + backwardPass(0)); 
			//printNetwork(); 
			
			
		}
		
		//This function calculated the feedback that the critic feeds back to the actor
		public double calculateFeedback(double[] state, double[] previousState, double reward, boolean marioKilled){
			forwardPass(state);
			double stateValue = 0;
			//If mario is dead, the value of the next state is just 0
			if(!marioKilled){
				stateValue = outputLayer.get(0).getOutput();
			}
			forwardPass(previousState);
			double previousStateValue = outputLayer.get(0).getOutput();
			double feedback = reward + (discount * stateValue) - previousStateValue;
		    System.out.println("Feedback: " + feedback);
			
		    return learningRate * feedback;
			
		}
	
	
	public void setTarget(double value){
		//System.out.println("Feedback: " + value);
		for(int i = 0; i < nOutput; i++){
			target[0][i] = value;
		}
	}
}
