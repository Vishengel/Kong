import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.Observable;

public class GamePanel extends JPanel implements Observer {
	private GameModel model;
	private int animationTimer = 0;
	
	//BufferedImage life;
	//BufferedImage barrel;
	Image marioLeft1 = new ImageIcon(getClass().getResource("images/left1.png")).getImage();
	Image marioLeft2 = new ImageIcon(getClass().getResource("images/left2.png")).getImage();
	Image marioLeft3 = new ImageIcon(getClass().getResource("images/left3.png")).getImage();
	Image marioRight1 = new ImageIcon(getClass().getResource("images/right1.png")).getImage();
	Image marioRight2 = new ImageIcon(getClass().getResource("images/right2.png")).getImage();
	Image marioRight3 = new ImageIcon(getClass().getResource("images/right3.png")).getImage();
	Image marioClimbLeft = new ImageIcon(getClass().getResource("images/ClimbLeft.png")).getImage();
	Image marioClimbRight = new ImageIcon(getClass().getResource("images/ClimbRight.png")).getImage();
	Image marioJumpLeft = new ImageIcon(getClass().getResource("images/jumpLeft.png")).getImage();
	Image marioJumpRight = new ImageIcon(getClass().getResource("images/jumpRight.png")).getImage();
	Image kong = new ImageIcon(getClass().getResource("images/konky_dong.gif")).getImage();
	Image peach = new ImageIcon(getClass().getResource("images/peach.png")).getImage();
	Image barrel = new ImageIcon(getClass().getResource("images/barrel.png")).getImage();
	Image platform = new ImageIcon(getClass().getResource("images/platform.png")).getImage();
	Image ladder = new ImageIcon(getClass().getResource("images/ladder.png")).getImage();
	Image oil = new ImageIcon(getClass().getResource("images/oil.png")).getImage();
	Image flame = new ImageIcon(getClass().getResource("images/flame.png")).getImage();
	Image powerup = new ImageIcon(getClass().getResource("images/powerup.png")).getImage();
	Image bonus = new ImageIcon(getClass().getResource("images/bonus.png")).getImage();
	Image bStack = new ImageIcon(getClass().getResource("images/BarrelStack.png")).getImage();
	
	public GamePanel(GameModel model) throws IOException {
		setOpaque(true);
        setBackground(Color.BLACK);
        
		this.model = model;
		this.model.addObserver(this);
		
		
		AbstractAction animation = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				animationTimer++;
				//System.out.println("ani: " + animationTimer);
				if(animationTimer > 10){
					animationTimer = 0;
				}
			}			
		};
		new Timer(20, animation).start();
		
		
	}
	
	 public void paintComponent(Graphics g){
		super.paintComponent(g); 
				
		//Draw game objects
		g.drawImage(peach,constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_WIDTH, constants.PEACH_HEIGHT, null);
		g.drawImage(oil,constants.OIL_START_X,constants.OIL_START_Y,constants.OIL_WIDTH, constants.OIL_HEIGHT, null);
		
        for (Platform p : model.getPlatformList()){
        	//if(p.getHasLadder()) {
        	g.drawImage(platform,(int)p.getXPos(), (int)p.getYPos(), (int)p.getWidth(), (int)p.getHeight(), null);
        	//}
        }
        for(Ladder l: model.getLadderList()){
        	g.drawImage(ladder,(int)l.getXPos(), (int)l.getYPos(), (int)l.getWidth(), (int)l.getHeight(), null);
        }
        
        for(Powerup pu : model.getPUList()) {
        	g.drawImage(powerup,(int)pu.getXPos(), (int)pu.getYPos(), (int)pu.getWidth(), (int)pu.getHeight(), null);
        }
        	
       
        g.setColor(Color.WHITE);
       
        
        for (MovingObject object : model.getMOList()){
        	String name = object.getName();
        	if(name == "barrel"){
        		g.drawImage(barrel,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
        	}
            if(name == "player"){
            	animateMario(g, object);
            	//g.drawImage(mario, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);	
            }
            if(name == "flame") {
            	g.drawImage(flame, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
            }
        }
       /* for(int i = 0; i < model.getLives(); i++){    	
        	g.drawImage(life, 0 + 30*i,10,30,39, null);
        }
        */
        //draw cosmetic stuff
        //kong
        g.drawImage(kong, 60,165,100,100, null);
        //bonus image
        g.drawImage(bonus, 400, 50, 100, 50, null);
        //barrel stack
        g.drawImage(bStack, 12, 190, 55, 70, null);  
        //draw score
        g.drawString(model.getScore() + "", 440, 82); 
    }
	
	public void update(Observable caller, Object data){
        repaint();
    }
	
	
	public void animateMario(Graphics g, MovingObject object){
		int action = object.getAction();
		switch(action){
		case 0:
			g.drawImage(marioLeft1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			break;
		case 1:
			if(animationTimer < 4){
				g.drawImage(marioLeft1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			else if(animationTimer >= 4 && animationTimer < 7){
				g.drawImage(marioLeft2, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			else{
				g.drawImage(marioLeft3, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			break;
		case 2:
			if(animationTimer < 4){
				g.drawImage(marioRight1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			else if(animationTimer >= 4 && animationTimer < 7){
				g.drawImage(marioRight2, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			else{
				g.drawImage(marioRight3, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}	
			break;
		case 3:
			if(object.isClimbing){
				if(animationTimer <= 5){
					g.drawImage(marioClimbLeft, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);			
				}
				else{
					g.drawImage(marioClimbRight, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);						
				}
			}
			else{
				g.drawImage(marioLeft1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			break;
		case 4:
			if(object.isClimbing){
				if(animationTimer <= 5){
					g.drawImage(marioClimbLeft, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);			
				}
				else{
					g.drawImage(marioClimbRight, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);						
				}
			}
			else{
				g.drawImage(marioLeft1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			break;
		case 5:
			g.drawImage(marioJumpLeft, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);	
			break;
		case 6:
			g.drawImage(marioJumpRight, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			break;
		}
			
	}

}
