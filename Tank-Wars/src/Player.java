import java.awt.Color;


public class Player {

	private String name;
	private boolean isAI = false;
	private AI ai;
	private int initPosition;
	private int health;
	//private int score;
	private Color color;
	private String nation;
	private Tank tank;
	private Shell[] weapons;	// amunicja w danej broni
	private int currWeapon;
	
	Player(String name, int iP, String nation, boolean isAI, AI ai) {
		this.nation = nation;
		this.name = name;
		this.isAI = isAI;
		this.ai = ai;
		this.initPosition = iP;
		health = 40;
		currWeapon = 0;
		//score = 0;
		
		weapons = new Shell[3];			// na początku mamy tylko jedną broń
		
		weapons[0] = new Shell(50,20,20,30,25,"Pocisk odłamkowy");
		weapons[1] = new Shell(60,15,15,75,40,"Pocisk burzący");
		weapons[2] = new Shell(20,5,5,10,5,"Flara");
		// zapalający
		
		weapons[0].addAmmo(15);
		weapons[1].addAmmo(1);
		weapons[2].addAmmo(30);
		
		/*
		switch(nation) {
		case "soviet":
			color = new Color(104,131,30);
			break;
		case "german":
			color = new Color(91,107,117);
			break;
		case "british":
			color = new Color(214,168,57);
			break;
		case "polish":
			color = new Color(210,124,2);
			break;
		}
		*/
		
		if		(nation == "soviet") 	color = new Color(104,131,30);
		else if	(nation == "german") 	color = new Color(91,107,117);
		else if	(nation == "british")	color = new Color(214,168,57);
		else if	(nation == "polish")	color = new Color(210,124,2);
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public void setTank(Tank t) {
		tank = t;
	}
	
	public boolean isAI() {
		return isAI;
	}
	
	public void deployAI(GamePanel gp) {
		if(isAI) this.ai.deploy(gp, this);
	}
	
	public void useAI(Particle[][] Grid) {
		ai.aim(Grid);
	}
	
	public int getInitPos() {
		return initPosition;
	}
	
	public boolean isAlive() {
		if(health > 0) return true;
		else return false;
	}
	
	public int getCurrWeapon() {
		return currWeapon;
	}
	
	public Shell getWeapon(int n) {
		return weapons[n];
	}
	
	public int getAmmo(int wNum) {
		return weapons[wNum].getAmmo();
	}
	
	public int getCurrAmmo() {
		return weapons[currWeapon].getAmmo();
	}
	
	public void addWeapon(int wNum, int n) {
		weapons[wNum].addAmmo(n);
	}
	
	public void decWeapon() {
		weapons[currWeapon].decAmmo();
	}
	
	public void nextWeapon() {
		currWeapon++;
		if(currWeapon == weapons.length) currWeapon = 0;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getNation() {
		return nation;
	}
	
	public String getName() {
		return name;
	}
	
	public int getHP() {
		return health;
	}
	
	public void decHealth(int hp) {
		health -= hp;
	}
}
