import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import org.json.*;




public class FileHandler {
	
	//write action in binary form to file
	/*public void writeActionToFile(FileWriter fw, int action) throws IOException{
		//System.out.println(action);
		switch(action){
		case 0:
			fw.write(1 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 +"\n");
			break;
		case 1:
			fw.write(0 + "," + 1 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 +"\n");
			break;
		case 2:
			fw.write(0 + "," + 0 + "," + 1 + "," + 0 + "," + 0 + "," + 0 + "," + 0 +"\n");
			break;
		case 3:
			fw.write(0 + "," + 0 + "," + 0 + "," + 1 + "," + 0 + "," + 0 + "," + 0 +"\n");
			break;
		case 4:
			fw.write(0 + "," + 0 + "," + 0 + "," + 0 + "," + 1 + "," + 0 + "," + 0 +"\n");
			break;
		case 5:
			fw.write(0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 1 + "," + 0 +"\n");
			break;
		case 6:
			fw.write(0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 1 +"\n");
			break;
			
		}
	}
    */
	//write the game state plus the action taken to a csv file
	public void writeToFile(ArrayList<double[]> inputs, String fileName) throws IOException{
		try
		{
			
		    String filename= "src/" + fileName + ".csv";
		    FileWriter fw = new FileWriter(filename,true);
		    //write the inputs to the file
		    for(int i = 0; i < inputs.size(); i++){
		    	for(int j = 0; j < inputs.get(0).length; j++){
		    		//System.out.print(inputs.get(i)[j] + ",");
		    		fw.write(inputs.get(i)[j] + ",");	
		    	}
		    	//System.out.println();
		    	fw.write("\n");
			    		    
		    }		   		    
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public void writeGameStateToFile(ArrayList<ArrayList<MovingObject>> MOCollection, ArrayList<ArrayList<Powerup>> PUCollection,
			ArrayList<Platform> platformList, ArrayList<Ladder> ladderList, Peach peach, Oil oil, Flame flame, String fileName) throws IOException{
		
		//JSONObject peach = new JSONObject();
		
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
	
	public double[][] readFile(String fileName, int nInput, int nOutput){
		BufferedReader in;
		
		int amountOfLines = 0;
		int amountInputs = 0;
		//count amount of lines
		try {
	        in = new BufferedReader(new FileReader("src/" + fileName + ".csv"));
	        while ((in.readLine()) != null) {
	        	//count amount of lines
	        	amountOfLines++; 
	        	/*String str;
		        str = in.readLine();
	        	String[] ar =str.split(",");
	        	amountInputs = ar.length;*/
	        	
	        	
	        	
	        }
	        in.close();
	    } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
		double[][] inputs = new double[amountOfLines][nInput + nOutput];
		try {
	        in = new BufferedReader(new FileReader("src/" + fileName + ".csv"));
	        String str;
	        int line = 0;
	        while ((str = in.readLine()) != null) {
	            String[] ar =str.split(",");
	            for(int i = 0; i < ar.length; i++){
	            	inputs[line][i] = Double.parseDouble(ar[i]);
	            	//System.out.print(inputs[line][i] + ",");
	            }
	            //System.out.println();
	            line++;
	            
	        }
	        in.close();
	    } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
		/*for(int i = 0; i < amountOfLines; i++){
			for(int j = 0; j < 11; j++){
				System.out.print(inputs[i][j] + " ");			
			}
			System.out.println();
		}*/
		return inputs;
	}
}

    

