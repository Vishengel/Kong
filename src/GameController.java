import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Timer;



public class GameController {
	private GameModel model;
	private GameView  view;
	//for use input
	private Scanner reader;
	private InputController inputController = new InputController();
	private int timer = 0;
	
	public void start() throws IOException{
		//create game model and view
		model = new GameModel();
		view = new GameView(model);
		reader = new Scanner(System.in);
		
		view.drawView(model.getGOList(), model.getMOList());
		view.addKeyListener(inputController);
		
		AbstractAction FPSTimer = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				model.runGame();
				
				timer++;
				//handle gravity every 150 milliseconds
				if(timer % 10 == 0){
					model.incrementTime();	
				}
				//spawn a barrel every 450 milliseconds
				if(timer % 30 == 0){
					model.spawnBarrel();
				} 
				//reset timer eventually, to avoid overflow
				if(timer > 1500){
					timer = 0;
				}
				//System.out.println(timer);
				
			}			
		};
		new Timer(15, FPSTimer).start();
		//view.add(view.getGamePanel());
		//main game loop
		//while(!model.isGameOver()){
			//get user input
			//int action = getInput(reader);
			//change action of player
			//model.setPlayerAction(action);
			//update the rest of the model
			//model.runGame();
			//redraw view
			//view.drawView(model.getGOList(), model.getMOList());			
		//}
	}
	/*
	private int getInput(Scanner reader) {
		System.out.println("1 - left    2 - right	3 - jump	4 - idle");
		System.out.print("> ");
		return reader.nextInt();
	}
	*/
	
	class InputController implements KeyListener {
		private boolean[] down = new boolean[255];
		private boolean[] pressed = new boolean[255];
		
		@Override
		public void keyPressed(KeyEvent e) {
			//System.out.println("Key pressed");
			down[e.getKeyCode()] = true;
			pressed[e.getKeyCode()] = true;
			model.passKeysDownToPlayer(down);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			down[e.getKeyCode()]=false;
			model.passKeysDownToPlayer(down);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
