import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class InputHandler {
	private String filePath;
	
	private ArrayList<ArrayList<MovingObject>> MOCollection = new ArrayList<ArrayList<MovingObject>>();
	private ArrayList<ArrayList<Powerup>> PUCollection = new ArrayList<ArrayList<Powerup>>();
	private ArrayList<Platform> platformList;
	private ArrayList<Ladder> ladderList;
	private ArrayList<MovingObject> MOList;
	private ArrayList<Powerup> PUList; 
	
	public InputHandler(String filePath) {
		//The path to the json file where the input is stored
		this.filePath = filePath;
		readFromJson();
	}

	public void readFromJson() {
		BufferedReader in;
		try {
            Gson gson = new GsonBuilder().create();
            in = new BufferedReader(new FileReader(filePath));
            JsonParser parser = new JsonParser();
            //JsonObject obj = parser.parse(filePath).getAsJsonObject();
            JsonReader reader = new JsonReader(new StringReader(filePath));
            reader.setLenient(true);
            System.out.println(reader.nextString());
            System.out.println(reader.nextString());
            
            //Map jsonJavaRootObject = gson.fromJson(filePath, Map.class);
            //System.out.println(jsonJavaRootObject.get("player"));
        } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
	}
	
}
