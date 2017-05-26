import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


public class GamePanel extends JPanel {
	private GameModel model;
	private int animationTimer = 0;
	private int barrelAnimationTimer = 0;
	

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
	
	Image barrel1 = new ImageIcon(getClass().getResource("images/b1.png")).getImage();
	Image barrel2 = new ImageIcon(getClass().getResource("images/b2.png")).getImage();
	Image barrel3 = new ImageIcon(getClass().getResource("images/b3.png")).getImage();
	Image barrel4 = new ImageIcon(getClass().getResource("images/b4.png")).getImage();
	Image climbingBarrel1 = new ImageIcon(getClass().getResource("images/climbingBarrel1.png")).getImage();
	Image climbingBarrel2 = new ImageIcon(getClass().getResource("images/climbingBarrel2.png")).getImage();
	
	Image peach1 = new ImageIcon(getClass().getResource("images/peach1.png")).getImage();
	Image peach2 = new ImageIcon(getClass().getResource("images/peach2.png")).getImage();
	Image help = new ImageIcon(getClass().getResource("images/help.png")).getImage();
	
	Image kong = new ImageIcon(getClass().getResource("images/konky_dong.gif")).getImage();
	Image peach = new ImageIcon(getClass().getResource("images/peach.png")).getImage();
	Image platform = new ImageIcon(getClass().getResource("images/platform.png")).getImage();
	Image ladder = new ImageIcon(getClass().getResource("images/ladder.png")).getImage();
	Image powerup = new ImageIcon(getClass().getResource("images/powerup.png")).getImage();
	Image bonus = new ImageIcon(getClass().getResource("images/bonus.png")).getImage();
	Image bStack = new ImageIcon(getClass().getResource("images/BarrelStack.png")).getImage();
	
	Image blue = new ImageIcon(getClass().getResource("images/ladder.png")).getImage();
	Image orange = new ImageIcon(getClass().getResource("images/b1.png")).getImage();
	Image red = new ImageIcon(getClass().getResource("images/powerup.png")).getImage();
	Image pink = new ImageIcon(getClass().getResource("images/peach.png")).getImage();
	
	public GamePanel(GameModel model) throws IOException {
		setOpaque(true);
        setBackground(Color.BLACK);      
		this.model = model;
		
		
		AbstractAction animation = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				animationTimer++;
				//System.out.println("ani: " + animationTimer);
				if(animationTimer > 10){
					animationTimer = 0;
				}
			}			
		}; 
		new Timer(18, animation).start();
		
		AbstractAction barrelAnimation = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				barrelAnimationTimer++;
				//System.out.println("ani: " + animationTimer);
				if(barrelAnimationTimer > 100){
					barrelAnimationTimer = 0;
				}
			}			
		};
		new Timer(6, barrelAnimation).start();
		
		
	}
	
	 public void paintComponent(Graphics g){
		super.paintComponent(g); 
		
		
		//Temporary: draw visionGrid
		g.drawRect((int)model.getVisionGrid().getXPos(),(int) model.getVisionGrid().getYPos(),(int) model.getVisionGrid().getWidth(),(int) model.getVisionGrid().getHeight());
		for(VisionBlock b : model.getVisionGrid().getBlocks()){
			/*if(b.detectedBarrel() == 1){
				g.drawImage(orange, (int)b.getXPos(), (int)b.getYPos(), (int)b.getWidth(), (int)b.getHeight(), null);
			}
			else if(b.detectedLadder() == 1){
				g.drawImage(blue, (int)b.getXPos(), (int)b.getYPos(), (int)b.getWidth(), (int)b.getHeight(), null);			
			}
			else if(b.detectedPowerup() == 1){
				g.drawImage(red, (int)b.getXPos(), (int)b.getYPos(), (int)b.getWidth(), (int)b.getHeight(), null);
			}
			else if(b.detectedPeach() == 1){
				g.drawImage(pink, (int)b.getXPos(), (int)b.getYPos(), (int)b.getWidth(), (int)b.getHeight(), null);
			}
			else{*/
				g.drawRect((int)b.getXPos(),(int) b.getYPos(),(int) b.getWidth(),(int) b.getHeight());
			//}
		}
		
				
		//Draw game objects	
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
        		animateBarrel(g, object);
        		//g.drawImage(barrel,(int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight(), null);
        	}
            if(name == "player"){
            	animateMario(g, object);
            }
        }
           
        //The following images are drawn for cosmetic purposes
            
        //draw Donkey Kong
        g.drawImage(kong, 60,165,100,100, null);
        //draw peach
        animatePeach(g);
        g.drawImage(help, constants.PEACH_START_X + 20, constants.PEACH_START_Y - 5, 40, 15, null);
        //draw bonus image
        g.drawImage(bonus, 400, 50, 100, 50, null);
        //draw the stack of barrels next to Donkey Kong
        g.drawImage(bStack, 12, 190, 55, 70, null);  
        //draw score
        g.drawString(model.getScore() + "", 440, 82);
    }
	
	
	public void animatePeach(Graphics g){
		if(animationTimer <= 5){
			g.drawImage(peach1,constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_WIDTH, constants.PEACH_HEIGHT, null);
		}
		else{
			g.drawImage(peach2,constants.PEACH_START_X,constants.PEACH_START_Y,constants.PEACH_WIDTH, constants.PEACH_HEIGHT, null);
		}
	}
	public void animateBarrel(Graphics g, MovingObject object){
		int action = object.getAction();
		if(!object.isClimbing){
			if(object.right()){
				if(barrelAnimationTimer <= 25){
					g.drawImage(barrel1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else if(barrelAnimationTimer > 25 && barrelAnimationTimer <= 50){
					g.drawImage(barrel2, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else if(barrelAnimationTimer > 50 && barrelAnimationTimer <= 75){
					g.drawImage(barrel3, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else{
					g.drawImage(barrel4, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
			}
			else if(object.left()){
				if(barrelAnimationTimer <= 25){
					g.drawImage(barrel4, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else if(barrelAnimationTimer > 25 && barrelAnimationTimer <= 50){
					g.drawImage(barrel3, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else if(barrelAnimationTimer > 50 && barrelAnimationTimer <= 75){
					g.drawImage(barrel2, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
				else{
					g.drawImage(barrel1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
				}
			}
	    }
		else{
			if(barrelAnimationTimer <= 50){
				g.drawImage(climbingBarrel1, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
			else{
				g.drawImage(climbingBarrel2, (int)object.getXPos(),(int)object.getYPos(),(int)object.getWidth(),(int)object.getHeight(), null);
			}
		}
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
