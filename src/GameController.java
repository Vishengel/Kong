import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Timer;



public class GameController {
	private boolean GUI_ON;
	private GameModel model;
	private GameView  view;
	private InputController inputController = new InputController();
	private Thread thread;
	
	public GameController(boolean GUI_ON){
		this.GUI_ON = GUI_ON;
	}
	
	
	public void start() throws IOException{
		//create game model and view
		model = new GameModel(GUI_ON);
		
		
		
		
		//make a thread that controls game model logic
		thread = new Thread(){
		    public void run(){	
		      try {
				model.runGame();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		    }
		  };
		  
		thread.start();
		
		
		//If GUI is on, add framerate and update timer
		if(GUI_ON){
			view = new GameView(model);
			view.addKeyListener(inputController);
			AbstractAction FPSTimer = new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					view.gamePanel.repaint();					
				}			
			};
			new Timer(1, FPSTimer).start();
		}
	}
	
	
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
