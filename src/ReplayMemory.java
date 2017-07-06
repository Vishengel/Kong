import java.util.ArrayList;
import java.util.Random;

//This class represents the replay memory, storing transitions from which transitions are to be sampled for learning.
public class ReplayMemory {
	ArrayList<Transition> transitions;
	int maxsize = constants.MEMORY_SIZE;
	//Keep track of how many elements are in storage
	int counter = 0;
	Random ran = new Random();
	
	public ReplayMemory(){
		transitions = new ArrayList<Transition>();
	}
	//Store transitions. Replace earlier memories when maximum memory capacity is reached.
	public void storeTransition(Transition t){
		if(counter == maxsize){
			counter = 0;
		}
		if(transitions.size() < maxsize ){
			transitions.add(t);
		}
		else{
			transitions.set(counter, t);
		}
		counter++;
	}
	
	//Return a random transition
	public Transition getTransition(){
		return transitions.get( ran.nextInt(transitions.size()) );
	}
}
