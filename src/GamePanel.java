import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observer;
import java.util.Observable;

public class GamePanel extends JPanel implements Observer {
	private GameModel model;
	
	public GamePanel(GameModel model) {
		setOpaque(true);
        setBackground(Color.BLACK);
        
		this.model = model;
		this.model.addObserver(this);
	}
	
	 public void paintComponent(Graphics g){
		super.paintComponent(g); 
         
        for (GameObject object : model.getGOList()){
            
        }
        
        for (MovingObject object : model.getMOList()){
        	g.setColor(object.getColor());
            g.fillRect(object.getXPos(), object.getYPos(), object.getWidth(), object.getHeight());
        }
    }
	
	public void update(Observable caller, Object data){
        repaint();
    }

}
