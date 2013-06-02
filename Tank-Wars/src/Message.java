import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;


public class Message {

	private static int def_x = 30;
	private static int def_y = 30;
	private static Color def_color = Color.white;
	private static int def_time = 100;
	private static int def_fontSize = 14;
	
	private String message;
	private int x;				// współrzędne w Particles
	private int y;
	private int fontSize;
	private Color color;
	private Color bg = null;
	private int time;
	
	private int STEP;
	private boolean done = false;
	
	Message(String message, int x, int y, Color color, int time) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.fontSize = def_fontSize;
		this.color = color;
		this.time = time;
		
		STEP = 0;
	}
	
	Message(String message) {
		this.message = message;
		this.x = def_x;
		this.y = def_y;
		this.fontSize = def_fontSize;
		this.color = def_color;
		this.time = def_time;
		
		STEP = 0;
	}
	
	Message(String message, int x, int y, Color color, Color bg, int time) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.fontSize = def_fontSize;
		this.color = color;
		this.bg = bg;
		this.time = time;
		
		STEP = 0;
	}
	
	Message(String message, int x, int y, Color color, Color bg, int fS, int time) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.fontSize = fS;
		this.color = color;
		this.bg = bg;
		this.time = time;
		
		STEP = 0;
	}
	
	Message(String message, int x, int y) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.fontSize = def_fontSize;
		this.color = def_color;
		this.time = def_time;
		
		STEP = 0;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean show(Graphics2D g) {
		if(!done) {
			
			Font font = new Font("Monospaced",Font.PLAIN,fontSize);
			g.setFont(font);
			
			FontMetrics fm = g.getFontMetrics();
            Rectangle2D rect = fm.getStringBounds(message, g);
			
            if(bg != null) {
            	g.setColor(bg);
            	g.fillRect(
            			x * Particle.size - 5, 
            			(120 - y) * Particle.size - fm.getAscent() + 2, 
            			(int) rect.getWidth() + 10, 
            			(int) rect.getHeight()
            	);
            }
            
			g.setColor(color);
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.drawString(
					message, 
					x * Particle.size, 
					(120 - y) * Particle.size
			);
			STEP++;
			if(STEP == time) done = true;
		}
		return done;
	}
	

	
}
