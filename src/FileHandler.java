import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;




public class FileHandler {

	//write the game state plus the action taken to a csv file
	public void writeToFile(float[] inputs, int action) throws IOException{
		try
		{
		    String filename= "src/MyFile.csv";
		    FileWriter fw = new FileWriter(filename,true);
		    //write the inputs to the file
		    for(int i = 0; i < 9; i++){
			    fw.write(inputs[i] + ",");			    
		    }
		   
		    //write the action taken to a file
		    fw.write(action + "\n");	
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	public void readFile(){
		
	}
}

    

