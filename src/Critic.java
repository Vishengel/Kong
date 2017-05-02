import java.util.ArrayList;


//This MLP represents the value function:  state ---> stateValue
public class Critic extends MLPJelle {
	
	double discount = 0.5;
	double actorLearningRate = 0;
	
	public Critic(int nInput, int nHiddenLayers, int nHidden, int nOutput, String fileName) {
		super(nInput, nHiddenLayers, nHidden, nOutput, fileName);
		learningRate = 0.6;
	}
	
	public void initNetwork(){
		initializeLayers();
		//the target array contains the target: reward(next_state) + ( discount * value(next_state) )
		target = new double[1][1];
		
		
	}
	
	//calculate the target for the Critic to learn:  V(previousState) = reward + ( discount * V(state) )
		public void trainCritic(double[] state, double[] previousState, double reward){
			//present the current state to the critic
			forwardPass(state);
			//look at the output of the critic
			//System.out.println("Reward: " + reward);
			double valueNextState = outputLayer.get(0).getOutput();
			double target = reward + (discount * valueNextState);
			//System.out.println("Target:" + target);
			setTarget(target);
			//present previous state to the Critic and apply the target in backpropagation.
			forwardPass(previousState);
			//System.out.print("Target: " + target);
			//System.out.println(" Value: " + outputLayer.get(0).getOutput());
			backwardPass(0);
			//System.out.println("Critic error: " + backwardPass(0));
			//printNetwork(); 
			//System.out.println("State value: " + outputLayer.get(0).getOutput());
			
		}
		
		//This function calculated the feedback that the critic feeds back to the actor
		public double calculateFeedback(double[] state, double[] previousState, double reward){
			forwardPass(state);
			double stateValue = outputLayer.get(0).getOutput();
			forwardPass(previousState);
			double previousStateValue = outputLayer.get(0).getOutput();
			double feedback = actorLearningRate * ( reward + (discount * stateValue) - previousStateValue );
			//if(feedback >= 0){
				//System.out.println("Feedback: " + feedback);
			//}
			return feedback;
			
		}
	
	
	public void setTarget(double value){
		//System.out.println("Feedback: " + value);
		for(int i = 0; i < nOutput; i++){
			target[0][i] = value;
		}
	}
}
