import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Timer;



public class GameController {
	private GameModel model;
	private GameView  view;
	private InputController inputController = new InputController();
	private Thread thread;
	private Executor executor;
	
	public GameController() {
		//create game model and view
		model = new GameModel();
	}
	
	public void start() throws IOException, InterruptedException{
		//make a thread that controls game model logic
		thread = new Thread(){
		    public void run(){	
		      try {
				model.runGame();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		    }
		  };
		/*
		executor = Executors.newFixedThreadPool(1);
		
		executor.execute(new Runnable(){
		    public void run(){	
		      try {
		    	  
				model.runGame();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		    }
		  });
		 */
		thread.start();
		
		if(constants.GUI_ON){
			view = new GameView(model);
			view.addKeyListener(inputController);
			AbstractAction FPSTimer = new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					view.gamePanel.repaint();					
				}			
			};
			new Timer(1, FPSTimer).start();
		}
		
		thread.join();
		//If GUI is on, add frame rate and update timer
		
	}
	
	
	class InputController implements KeyListener {
		private boolean[] down = new boolean[255];
		
		@Override
		public void keyPressed(KeyEvent e) {
			//System.out.println("Key pressed");
			down[e.getKeyCode()] = true;
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
	
	public GameModel getGameModel() {
		return this.model;
	}
}
