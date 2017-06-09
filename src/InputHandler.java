import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.*;


public class InputHandler {
	private String filePath;
	
	private ArrayList<ArrayList<MovingObject>> MOCollection = new ArrayList<ArrayList<MovingObject>>();
	private ArrayList<ArrayList<Powerup>> PUCollection = new ArrayList<ArrayList<Powerup>>();
	private ArrayList<Platform> platformList = new ArrayList<Platform>();
	private ArrayList<Ladder> ladderList = new ArrayList<Ladder>();
	private Peach peach;
	private Oil oil = new Oil(0,0,0,0);
	private Flame flame = new Flame(0,0,0,0);
	
	public InputHandler(String filePath) {
		//The path to the json file where the input is stored
		this.filePath = filePath;
		readFromJson();
	}

	public void readFromJson() {
		//long start = System.currentTimeMillis();
		try {
            JsonElement root = new JsonParser().parse(new FileReader(filePath));
            
            readMovingObjectsFromJson(root.getAsJsonObject().get("stateList").getAsJsonArray());
            readStaticObjectsFromJson(root.getAsJsonObject().get("staticObjects").getAsJsonObject());

            
        } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
		//System.out.println("Time taken to read JSON: " + (System.currentTimeMillis() - start));
	}
	
	public void readMovingObjectsFromJson(JsonArray stateList) {
		ArrayList<MovingObject> MOList;
		ArrayList<Powerup> PUList;
		JsonObject playerAsJson, barrelAsJson, powerupAsJson;
		JsonArray barrelList, powerupList;
		Player player;
		MovingObject barrel;
		Powerup powerup;
		
        for(int i=0; i<stateList.size(); i++) {
        	MOList = new ArrayList<MovingObject>();
        	playerAsJson = stateList.get(i).getAsJsonObject().get("player").getAsJsonObject();
        	player = new Player(playerAsJson.get("xPos").getAsFloat(), playerAsJson.get("yPos").getAsFloat(),
        			constants.PLAYER_HEIGHT, constants.PLAYER_WIDTH, playerAsJson.get("action").getAsInt(),
        			playerAsJson.get("isJumping").getAsBoolean(), playerAsJson.get("isClimbing").getAsBoolean(),
        			playerAsJson.get("canClimb").getAsBoolean(), playerAsJson.get("isStanding").getAsBoolean(),
        			playerAsJson.get("isKilled").getAsBoolean(), playerAsJson.get("hasWon").getAsBoolean());
        	MOList.add(player);
        	
        	barrelList = stateList.get(i).getAsJsonObject().get("barrelList").getAsJsonArray();
        	
        	for(int j=0; j<barrelList.size(); j++) {
        		barrelAsJson = barrelList.get(j).getAsJsonObject();
        		barrel = new Barrel(barrelAsJson.get("xPos").getAsFloat(), barrelAsJson.get("yPos").getAsFloat(),
        				constants.BARREL_HEIGHT, constants.BARREL_WIDTH, barrelAsJson.get("action").getAsInt());
        		MOList.add(barrel);
        	}
        	
        	PUList = new ArrayList<Powerup>();
        	powerupList = stateList.get(i).getAsJsonObject().get("powerupList").getAsJsonArray();
        	
        	for(int j=0; j<powerupList.size(); j++) {
        		powerupAsJson = powerupList.get(j).getAsJsonObject();
        		powerup = new Powerup(powerupAsJson.get("xPos").getAsFloat(), powerupAsJson.get("yPos").getAsFloat(),
        				constants.POWERUP_HEIGHT, constants.POWERUP_WIDTH);
        		PUList.add(powerup);
        	}
        	
        	MOCollection.add(MOList);
        	PUCollection.add(PUList);
        } 
        
	}
	
	public void readStaticObjectsFromJson(JsonObject staticObjects) {
		JsonArray platformListAsJson = staticObjects.get("platformList").getAsJsonArray(), 
				ladderListAsJson = staticObjects.get("ladderList").getAsJsonArray();
		JsonObject platformAsJson, ladderAsJson;
		Platform platform;
		Ladder ladder;
		
		for(int i=0; i<platformListAsJson.size(); i++) {
			platformAsJson = platformListAsJson.get(i).getAsJsonObject();
			platform = new Platform(platformAsJson.get("xPos").getAsFloat(), platformAsJson.get("yPos").getAsFloat(),
					constants.PLATFORM_HEIGHT, constants.PLATFORM_WIDTH);
			platform.setHasLadder(platformAsJson.get("hasLadder").getAsBoolean());
			this.platformList.add(platform);
		}
		
		for(int i=0; i<ladderListAsJson.size(); i++) {
			ladderAsJson = ladderListAsJson.get(i).getAsJsonObject();
			ladder = new Ladder(ladderAsJson.get("xPos").getAsFloat(), ladderAsJson.get("yPos").getAsFloat(),
					constants.LADDER_HEIGHT, constants.LADDER_WIDTH);		
			this.ladderList.add(ladder);
		}
		
        peach = new Peach(staticObjects.get("peach").getAsJsonArray().get(0).getAsFloat(),
        		staticObjects.get("peach").getAsJsonArray().get(1).getAsFloat(),
        		constants.PEACH_HEIGHT, constants.PEACH_WIDTH);
	}
	
	public void test() throws IOException {
		FileHandler fh = new FileHandler();
		fh.writeGameStateToFile(MOCollection, PUCollection, platformList, ladderList, peach, "./TrainingData/gameStateDataTest");
	}
	
}
