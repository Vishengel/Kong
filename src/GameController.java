import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Timer;



public class GameController {
	private GameModel model;
	private GameView  view;
	//for use input
	private Scanner reader;
	private InputController inputController = new InputController();
	
	public void start(){
		//create game model and view
		model = new GameModel();
		view = new GameView(model);
		reader = new Scanner(System.in);
		
		view.drawView(model.getGOList(), model.getMOList());
		view.addKeyListener(inputController);
		
		AbstractAction FPSTimer = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				model.runGame();
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
			
			/*
			if(e.getKeyChar() == 'a') {
				 System.out.println("A pressed");
				 model.setPlayerAction(1);
				 
	         }
			if(e.getKeyChar() == 'd') {
				 System.out.println("D pressed");
				 model.setPlayerAction(2);
	         }
			*/
		}

		@Override
		public void keyReleased(KeyEvent e) {
			down[e.getKeyCode()]=false;
			model.passKeysDownToPlayer(down);
			/*
			if(e.getKeyChar() == 'a') {
				 System.out.println("A pressed");
				 model.setPlayerAction(4);
				 
	         }
			if(e.getKeyChar() == 'd') {
				 System.out.println("D pressed");
				 model.setPlayerAction(4);
	         }
	         */
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
