import javax.swing.*;
import java.awt.*;
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
        	g.setColor(object.getColor());
            g.fillRect((int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight());
        }
        
        for (MovingObject object : model.getMOList()){
        	g.setColor(object.getColor());
            g.fillRect((int)object.getXPos(), (int)object.getYPos(), (int)object.getWidth(), (int)object.getHeight());
        }
    }
	
	public void update(Observable caller, Object data){
        repaint();
    }

}
