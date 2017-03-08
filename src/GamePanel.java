import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observer;
import java.util.Observable;

public class GamePanel extends JPanel implements Observer {
	private GameModel model;
	BufferedImage image;
	
	public GamePanel(GameModel model) throws IOException {
		setOpaque(true);
        setBackground(Color.BLACK);
        
		this.model = model;
		this.model.addObserver(this);
		
		image = ImageIO.read(new File("mario_lives.png"));
		
	}
	
	 public void paintComponent(Graphics g){
		super.paintComponent(g); 
        for (GameObject object : model.getGOList()){
        	g.setColor(object.getColor());
            g.fillRect((int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight());
        }
        g.drawString("Score: " + model.getScore(), 500, 50); 
        
        for (MovingObject object : model.getMOList()){
        	g.setColor(object.getColor());
            g.fillRect((int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight());
            /*if(object instanceof Player){
            	g.drawImage(image, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);	
            }*/
        }
        for(int i = 0; i < model.getLives(); i++){    	
        	g.drawImage(image, 0 + 30*i,10,30,39, null);
        }
        
    }
	
	public void update(Observable caller, Object data){
        repaint();
    }

}
