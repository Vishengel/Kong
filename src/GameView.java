import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
	//temporary 2D array that shows the game world with ASCII characters
	private char[][] gameWorld;
	private GamePanel gamePanel;
	//private int worldWidth = 50;
	//private int worldHeight = 50;
	
	//draw a temporary view for debugging and being able to see what's going on
	public GameView(GameModel model, int width, int height){
		//init game world array
		/*
			gameWorld = new char[worldWidth][worldHeight];
			for(int y = 0; y < worldHeight; y++){
				for(int x = 0; x < worldWidth; x++){
					gameWorld[y][x] = '.';
				}
			}
		*/
		JTextField title = new JTextField("Donkey Kong");
        setTitle(title.getText()); 
         
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(width,height);
        setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
         
        gamePanel = new GamePanel(model);
        gamePanel.setSize(width,height);
        add(gamePanel);
	}
	
	public GameView(GameModel model) {
		this(model, constants.SCREEN_X, constants.SCREEN_Y);
	}
	// Not used anymore
	public void drawView(ArrayList<GameObject> GOList, ArrayList<MovingObject> MOList) {
		//initView();
		//draw each game object
		for(GameObject go : GOList){
			float x = go.getXPos();
			float y = go.getYPos();
			float height = go.getHeight();
			float width = go.getWidth();
			/*
			for(int i = y; i < y + height; i++){
				for(int j = x; j < x + width; j++){
					gameWorld[i][j] = go.getSymbol();
				}		
			}
			*/
		} 
		//draw each moving object			
		for(MovingObject mo : MOList){
			float x = mo.getXPos();
			float y = mo.getYPos();
			float height = mo.getHeight();
			float width = mo.getWidth();
			/*
			for(int i = y; i < y + height; i++){
				for(int j = x; j < x + width; j++){
					gameWorld[i][j] = mo.getSymbol();
				}		
			}
			*/
		}
		/*
		for(int y = 0; y < worldHeight; y++){
			for(int x = 0; x < worldWidth; x++){
				System.out.print(gameWorld[y][x]);
			}
			System.out.println();
		}
		*/
		//System.out.println();
		//System.out.println("X: Mario");
		//System.out.println("-: Platform");
		//System.out.println("O: Barrel");
			
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}
		
}
