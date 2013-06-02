
import java.util.ArrayList;
import java.util.Random;


public abstract class AI {
	
	protected Player player;
	protected Tank tank;
	protected ArrayList<Tank> opps;
	protected GamePanel GPanel;
	protected Random ran;
	protected Tank target;
	
	
	AI() {
		target = null;
		ran = new Random();
	}
	
	public void deploy(GamePanel gp, Player p) {
		this.GPanel = gp;
		this.player = p;
		this.tank = p.getTank();
		this.opps = gp.getTanks();
	}
	
	protected abstract void saveData();
	
	public abstract void aim(Particle[][] Grid);
	
}
