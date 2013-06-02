import java.awt.*;
import java.util.Random;

public class Particle {
	
	public enum Type {
		DIRT, TANK, AIR, GUN, SHELL
	}
	
	private int x;
	private int y;
	public final static int size = 5;
	private Color color;
	private Type type;
	public Tank tank;
	private boolean flagged;
	private Random ran;
	
	private Color[] CDirt = new Color[3];
	
	Particle(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = Type.AIR;
		this.tank = null;
		flagged = false;
		ran = new Random();
		
		CDirt[0] = new Color(20,166,73);
		CDirt[1] = new Color(28,166,43);
		CDirt[2] = new Color(40,179,53);
	}
	
	public void setDirt() {
		this.type = Type.DIRT;
		color = CDirt[ran.nextInt(3)];
		this.tank = null;
	}
	
	public void setAir() {
		this.type = Type.AIR;
		this.tank = null;
	}
	
	public void setTank(Tank t) {
		this.type = Type.TANK;
		color = t.getColor();
		this.tank = t;
	}
	
	public void setShell(Tank t) {
		this.type = Type.SHELL;
		//color = Color.yellow;
		color = t.getColor();
		this.tank = t;
	}
	
	public Tank getTank() {
		return tank;
	}
	
//	public void setGun(Tank t) {
//		this.type = Type.GUN;
//		color = new Color(156,98,23);
//		this.tank = t;
//	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isFlagged() {
		return flagged;
	}
	
	public void setFlag(boolean f) {
		flagged = f;
	}
	
	public void draw(Graphics2D g) {
		/*if(type == Type.AIR) {
			g.setColor(Color.gray);
			g.fillRect(x*size, (Map.h-y)*size - 13, size, size);
		}
		else */if(type == Type.DIRT) {
			g.setColor(color);
			g.fillRect(x*size, (Map.h-y)*size - 13, size, size);
		}
		else if (type == Type.TANK) {
			g.setColor(color);
			//g.fillRect(x*size, (Map.h-y)*size - 13, size, size);
		}
		else if (type == Type.SHELL) {
			g.setColor(color);
			g.fillRect(x*size, (Map.h-y)*size - 13, 3, 3);
		}
		else if (type == Type.GUN) {
			g.setColor(color);
			g.fillRect(x*size, (Map.h-y)*size - 13, size, size);
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
}
