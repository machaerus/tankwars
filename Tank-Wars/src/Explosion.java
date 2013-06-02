import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {
	
	private Map map;
	private int STEP;
	private int x;
	private int y;
	private int width;
	private int height;
	private int initialWidth;
	private int initialHeight;
	private int range;
	private Color color;
	private int time;
	private Shell shellType;
	private boolean done = false;
	
	Explosion(Map m, int x0, int y0, Shell sT) {
		map = m;
		shellType = sT;
		setX(x0);					// tutaj wszystkie dane w pikselach, ale argumenty setterów w cząsteczkach
		setY(y0);
		width = 0;
		height = 0;
		initialWidth = sT.getW();
		initialHeight = sT.getH();
		range = sT.getRange();				// promień rażenia
		time = sT.getTime();
		color = Color.red;
		STEP = 0;
	}
	
	private void setX(int x) {
		this.x = x * Particle.size;
	}
	
	private void setY(int y) {
		this.y = (Map.h - y) * Particle.size;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getInitialWidth() {
		return initialWidth;
	}
	
	public int getInitialHeight() {
		return initialHeight;
	}
	
	public int getRange() {
		return range;
	}
	
	public boolean isDone() {
		return done;
	}
	
	// TODO: obsługa różnych typów broni
	
	public void draw(Graphics2D g) {	// animacja sekwencyjna xD
		
		if(!done) {
			
			if(STEP == 0) {
				g.setColor(color);
				g.fillOval(x - width/2, y - height/2, width, height);
				width = initialWidth;
				height = initialHeight;
			}
			else {
				g.setColor(color);
				g.fillOval(x - width/2, y - height/2, width, height);
				width++;
				height++;
			}
			
			STEP++;
			if(STEP == time) {
				done = true;
				map.applyDestruction(x,y,shellType);
			}
		}
	}
}
