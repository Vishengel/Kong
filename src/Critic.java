import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {
	
	double discount = 0.6;
	
	
	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName) {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName);
		learningRate = 0.001;
	}
	
	public void initNetwork(){
		initializeLayers();
		//the target array contains the target: reward(next_state) + ( discount * value(next_state) )
		target = new double[1][1];
		System.out.println("Critic");
		
	}
	
	//calculate the target for the Critic to learn:  V(previousState) = reward + ( discount * V(state) )
		public void trainCritic(double[] state, double[] previousState, double reward){
			//present the current state to the critic
			forwardPass(state);
			//look at the output of the critic
			System.out.println("Reward: " + reward);
			double value = outputLayer.get(0).getOutput();
			//System.out.println(value);
			double target = reward + (discount * value);
			//System.out.println("Target:" + target);
			setTarget(target);
			//present previous state to the Critic and apply the target in backpropagation.
			forwardPass(previousState);
			backwardPass(0);
			//printNetwork(); 
			System.out.println("State value: " + outputLayer.get(0).getOutput());
		}
		
		//This function calculated the feedback that the critic feeds back to the actor
		public double calculateFeedback(double[] state, double[] previousState, double reward){
			forwardPass(state);
			double stateValue = outputLayer.get(0).getOutput();
			forwardPass(previousState);
			double previousStateValue = outputLayer.get(0).getOutput();
			return reward + (discount * stateValue) - previousStateValue;
		}
	
	
	public void setTarget(double value){
		target[0][0] = value;
	}
}
