import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;





public class FileHandler {
	
	//write action in binary form to file
	public void writeActionToFile(FileWriter fw, int action) throws IOException{
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

	//write the game state plus the action taken to a csv file
	public void writeToFile(double[] inputs, int action) throws IOException{
		try
		{
			String asd = "MyFile";
		    String filename= "src/" + asd + ".csv";
		    FileWriter fw = new FileWriter(filename,true);
		    //write the inputs to the file
		    for(int i = 0; i < inputs.length; i++){
			    fw.write(inputs[i] + ",");			    
		    }
		   
		    //write the action taken to a file
		    writeActionToFile(fw, action);
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	public double[][] readFile(){
		int amountOfLines = 0;
		//count amount of lines
		try {
	        BufferedReader in = new BufferedReader(new FileReader("src/MyFile.csv"));
	        while ((in.readLine()) != null) {
	        	amountOfLines++;     
	        }
	        in.close();
	    } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
		
		double[][] inputs = new double[amountOfLines][14];
		try {
	        BufferedReader in = new BufferedReader(new FileReader("src/MyFile.csv"));
	        String str;
	        str = in.readLine();
	        int line = 0;
	        while ((str = in.readLine()) != null) {
	            String[] ar =str.split(",");
	            for(int i = 0; i < ar.length; i++){
	            	inputs[line][i] = Double.parseDouble(ar[i]);
	            }
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

    

