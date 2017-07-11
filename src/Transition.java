//This class represents a transition from one state to the next, to be stored in a Replay Memory.
public class Transition {
	//Represents the state at time t
	private double[] previousState;
	//Represents the state at time t+1
	private double[] nextState;
	//Represents the action taken at time t
	private int action;
	//Represents the reward received at time t+1 by taking action at time t
	private double reward;
	private double feedback;
	private boolean gameLost;
	private boolean gameWon;
	
	public Transition(double[] previousState, double[] nextState, int action, double reward, double feedback, boolean gameLost, boolean gameWon){
		this.previousState = new double[previousState.length];
		this.nextState = new double[previousState.length];
		//Copy contents of incoming state arrays into class state arrays
		for(int i = 0; i < previousState.length; i++){
			this.previousState[i] = previousState[i];
			this.nextState[i] = nextState[i];
		}
		this.action = action;
		this.reward = reward;
		this.feedback = feedback;
		this.gameLost = gameLost;
		this.gameWon = gameWon;
	}
	
	public double[] getPreviousState(){
		return previousState;
	}
	public double[] getNextState(){
		return nextState;
	}
	public int getAction(){
		return action;
	}
	public double getReward(){
		return reward;
	}
	public double getFeedback(){
		return feedback;
	}
	public boolean getGameLost(){
		return gameLost;
	}
	public boolean getGameWon(){
		return gameWon;
	}
}
