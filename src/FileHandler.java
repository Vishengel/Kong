import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileHandler {
	private String filePath;
	
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
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		    String filename= "src/" + fileName + "_" + timeStamp + ".csv";
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
			ArrayList<Platform> platformList, ArrayList<Ladder> ladderList, Peach peach, String fileName) throws IOException{
		
		try
		{
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		    this.filePath= "src/" + fileName + "_" + timeStamp + ".json";
		    FileWriter fw = new FileWriter(filePath,true);
		    BufferedWriter out = new BufferedWriter(fw);
		    Gson gson = new GsonBuilder().create();
		    
		    JsonObject trainingData = new JsonObject();
		    JsonArray stateList = new JsonArray();
		    JsonObject state;
		    JsonObject MOJson;
		    JsonArray barrelList;
		    JsonObject PUJson;
		    JsonArray powerupList;
		    
		    for (ArrayList<MovingObject> MOList : MOCollection) {
		    	state = new JsonObject();
		    	barrelList = new JsonArray();
		    	powerupList = new JsonArray();

		    	for (MovingObject MO : MOList) {
		    		MOJson = new JsonObject();
		    		if (MO.getName() == "player") {	    			    			
		    			MOJson.addProperty("xPos", MO.getXPos());
		    			MOJson.addProperty("yPos", MO.getYPos());
		    			MOJson.addProperty("action", MO.getAction());
			    		MOJson.addProperty("isJumping", MO.isJumping());
		    			MOJson.addProperty("isClimbing", MO.isClimbing());
		    			MOJson.addProperty("canClimb", MO.getCanClimb());
		    			MOJson.addProperty("isStanding", MO.getStanding());
		    			MOJson.addProperty("isKilled", MO.isKilled());
			    		MOJson.addProperty("hasWon", MO.hasWon());
			    		state.add("player", MOJson);
		    		} else if (MO.getName() == "barrel") {
		    			MOJson.addProperty("xPos", MO.getXPos());
		    			MOJson.addProperty("yPos", MO.getYPos());
		    			MOJson.addProperty("action", MO.getAction());
		    			barrelList.add(MOJson);
		    		}
		    		
		    	}
		    	state.add("barrelList", barrelList);
		    	
		    	for (Powerup PU : PUCollection.get(MOCollection.indexOf(MOList))) {
		    		PUJson = new JsonObject();
		    		PUJson.addProperty("xPos", PU.getXPos());
		    		PUJson.addProperty("yPos", PU.getYPos());
		    		powerupList.add(PUJson);
		    	}
		    	state.add("powerupList", powerupList);
		    	
		    	stateList.add(state);
		    	
		    }
		    
		    trainingData.add("stateList", stateList);
		    
		    JsonObject staticObjects = new JsonObject();
		    
		    //Write platform data to file
		    JsonArray platformArray = new JsonArray();
		    JsonObject platform;
		    for (Platform p : platformList) {
		    	platform  = new JsonObject();
		    	platform.addProperty("xPos", p.getXPos());
		    	platform.addProperty("yPos", p.getYPos());
		    	platform.addProperty("hasLadder", p.getHasLadder());
		    	platformArray.add(platform);
		    }
		    staticObjects.add("platformList", platformArray);
		    
		    //Write ladder data to file
		    JsonArray ladderArray = new JsonArray();
		    JsonObject ladder;
		    for (Ladder l : ladderList) {
		    	ladder  = new JsonObject();
		    	ladder.addProperty("xPos", l.getXPos());
		    	ladder.addProperty("yPos", l.getYPos());
		    	ladderArray.add(ladder);
		    }
		    staticObjects.add("ladderList", ladderArray);
		    
		    //Write coordinates of Peach to file
		    JsonArray peachPos = new JsonArray();
		    peachPos.add(peach.getXPos());
		    peachPos.add(peach.getYPos());
		    staticObjects.add("peach", peachPos);
		    
		    trainingData.add("staticObjects", staticObjects);
		    
		    gson.toJson(trainingData, out);
		    
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
	            for(int i = 0; i < nInput + nOutput; i++){
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
	public String getFilePath() {
		return this.filePath;
	}

	//Store the weights of the network in a text file 
	public void storeNetwork(MLPJelle mlp, ArrayList<ArrayList<NeuronJelle>> hiddenList, int hiddenLayers) throws IOException{
		String filename= "src/" + "storedNet" + ".csv";
		FileWriter fw = new FileWriter(filename,true);
		int nWeights = hiddenList.get(0).get(0).getWeights().length;
		//Write amount of hidden layers and hidden nodes of first layer to file 
		fw.write(hiddenLayers + ",");
		for(int i = 0; i < hiddenLayers; i++){
			fw.write(hiddenList.get(i).get(0).getWeights().length + ",");
		}
		fw.write("\n");
		
		
		//Write all hidden weights to file, for every hidden layer
		for(int i = 0; i < hiddenLayers; i++){
			//Write weights for every hidden node
			for(NeuronJelle n: hiddenList.get(i)){
				for(int w = 0; w < n.getWeights().length; w++){
					fw.write(n.getWeights()[w] + ",");
				}
				fw.write("\n");
				//Next hidden layer has half the weights compared to the previous layer
				nWeights = nWeights / 2;
			}
		}
		fw.write("" + mlp.getOutputLayer().get(0).getWeights().length);
		fw.write("\n");
		//Store weights of output layer
		for(NeuronJelle n: mlp.getOutputLayer()){
			for(int w = 0; w < n.getWeights().length; w++){
				fw.write(n.getWeights()[w] + ",");
			}
			fw.write("\n");
		}
		fw.close();
	}
}

    

