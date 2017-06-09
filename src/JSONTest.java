import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class JSONTest {
		
	public JSONTest() {
		Gson gson = new Gson();
	}

	public void writeGameStateToFile(ArrayList<ArrayList<MovingObject>> MOCollection, ArrayList<ArrayList<Powerup>> PUCollection,
			ArrayList<Platform> platformList, ArrayList<Ladder> ladderList, Peach peach, Oil oil, Flame flame, String fileName) throws IOException{
		
		try
		{
		    String filePath= "src/" + fileName + ".csv";
		    FileWriter fw = new FileWriter(filePath,true);
		    BufferedWriter out = new BufferedWriter(fw);
		    out.write(MOCollection.get(0).get(0).getXPos() + "");
		    //write the inputs to the file
		    /*
		    for(int i = 0; i < inputs.length; i++){
			    fw.write(inputs[i] + ",");			    
		    }
		   
		    //write the action taken to a file
		    writeActionToFile(fw, action);*/
		    System.out.println("Written to file!");
		    out.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}	
	
}
