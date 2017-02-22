import java.util.Scanner;


public class GameController {
	GameModel model;
	GameView  view;
	//for use input
	Scanner reader;
	
	public void start(){
		//create game model and view
		model = new GameModel();
		view = new GameView();
		reader = new Scanner(System.in);
		view.drawView(model.getGOList(), model.getMOList());
		
		//main game loop
		while(!model.isGameOver()){
			//get user input
			int action = getInput(reader);
			//change action of player
			model.setPlayerAction(action);
			//update the rest of the model
			model.runGame();
			//redraw view
			view.drawView(model.getGOList(), model.getMOList());			
		}
	}
	
	private int getInput(Scanner reader) {
		System.out.println("1 - left    0 - right");
		System.out.print("> ");
		return reader.nextInt();
	}
}
