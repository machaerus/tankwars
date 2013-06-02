
public class Shell {
	private int range;
	private int width;
	private int height;
	private int time;
	private int destruction;
	private String name;
	private int ammo;
	
	Shell(int r, int w, int h, int t, int d, String n) {
		range = r;
		width = w;
		height = h;
		time = t;
		destruction = d;
		name = n;
		ammo = 0;
	}
	
	public int getRange() {
		return range;
	}
	
	public int getW() {
		return width;
	}
	
	public int getH() {
		return height;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTime() {
		return time;
	}
	
	public int destruction() {
		return destruction;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public void addAmmo(int n) {
		ammo += n;
	}
	
	public void decAmmo() {
		ammo--;
	}
}
