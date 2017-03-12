import java.util.ArrayList;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

public class GameView extends JFrame {
	 GamePanel gamePanel;
	
	public GameView(GameModel model, int width, int height) throws IOException{
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
	
	public GameView(GameModel model) throws IOException {
		this(model, constants.SCREEN_X, constants.SCREEN_Y);
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}
		
}
