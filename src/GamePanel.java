import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.Observable;

public class GamePanel extends JPanel implements Observer {
	private GameModel model;
	//BufferedImage life;
	//BufferedImage barrel;
	Image mario = new ImageIcon(getClass().getResource("images/mario.png")).getImage();
	Image kong = new ImageIcon(getClass().getResource("images/konky_dong.gif")).getImage();
	Image peach = new ImageIcon(getClass().getResource("images/peach.png")).getImage();
	Image barrel = new ImageIcon(getClass().getResource("images/barrel.png")).getImage();
	Image platform = new ImageIcon(getClass().getResource("images/platform.png")).getImage();
	Image ladder = new ImageIcon(getClass().getResource("images/ladder.png")).getImage();
	
	public GamePanel(GameModel model) throws IOException {
		setOpaque(true);
        setBackground(Color.BLACK);
        
		this.model = model;
		this.model.addObserver(this);
		
		
	}
	
	 public void paintComponent(Graphics g){
		super.paintComponent(g); 
				
		//draw the Konger himself
		g.drawImage(kong, 60,120,100,100, null);
		
        for (GameObject object : model.getGOList()){
        	if(object instanceof Platform){
        		g.drawImage(platform,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
    		}
        	
        	if(object instanceof Ladder) {
        		g.drawImage(ladder,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
        	}
        	
        	if(object instanceof Peach) {
        		g.drawImage(peach,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
        	}
     
        }
        g.setColor(Color.WHITE);
        g.drawString("Score: " + model.getScore(), 500, 50); 
        
        for (MovingObject object : model.getMOList()){
        	//g.setColor(object.getColor());
        	if(object instanceof Barrel){
        		g.drawImage(barrel,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
        	}
            if(object instanceof Player){
            	g.drawImage(mario, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);	
            }
        }
       /* for(int i = 0; i < model.getLives(); i++){    	
        	g.drawImage(life, 0 + 30*i,10,30,39, null);
        }
        */
    }
	
	public void update(Observable caller, Object data){
        repaint();
    }

}
