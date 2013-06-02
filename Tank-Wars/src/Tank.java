import java.awt.Color;

public class Tank {
	
	private Player player;
	private int x;
	private int y;
	private Color color;
	private int angle;
	private int velocity;
	public final static double g = 9.81;	// stała grawitacyjna xD
	
	Tank(int x, int y, Player pl) {
		player = pl;
		this.x = x;
		this.y = y;
		
		//Random ran = new Random();
		this.angle = 45;
		
		this.velocity = 40;
		color = player.getColor();
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
	
	public Player getPlayer() {
		return this.player;
	}
	
	public int getAngle() {
		return angle;
	}
	
	public void setAngle(int a) {
		angle = a;
	}
	
	public void incAngle() {
		if(angle == 180) angle = 0;
		else angle++;
	}
	
	public void decAngle() {
		if(angle == 0) angle = 180;
		else angle--;
	}
	
	public int getVelocity() {
		return velocity;
	}
	
	public void setVelocity(int v) {
		velocity = v;
	}
	
	public void incVelocity() {
		if(velocity == 200) velocity = 200;
		else velocity++;
	}
	
	public void decVelocity() {
		if(velocity == 0) velocity = 0;
		else velocity--;
	}
	
	public void fall(Particle[][] Grid) {
		for(int k = 0; k < 7; k++) {
			Grid[x+k][y-1].setTank(this);
			Grid[x+k][y+6].setAir();
		}
		for(int m = 0; m < 7; m++) {
			for(int n = -1; n < 6; n++) {
				Grid[x+m][y+n].setFlag(true);
			}
		}
		y--;
	}
	
	public void hit(int hp) {
		this.player.decHealth(hp);
	}
	
	public int[] fire(Particle[][] Grid) {
		
		// obliczamy tor pocisku
		
		int v = velocity / 2;
		
		int s1 = x + 3;
		int s2 = y + 2;	// punkt startowy
		
		//System.out.println("strzelamy z "+s1+" "+s2);
		
		int p, phi, psi;

		int[] exp = new int[4];			// tu trzymamy wynik (miejsce eksplozji)
		
		exp[0] = 0; 					// exp[0] - 0: brak wybuchu, 1: wybuch
										// exp[1] - 1. współrzędna trafienia
										// exp[2] - 2. współrzędna trafienia
										// exp[3] - typ pocisku
		
		if(angle <= 90) {	// jeśli jesteśmy w pierwszej ćwiartce
			
			for(int i = 0; i < Map.w; i++) {
				
				// obliczamy krzywą balistyczną
				
				p = (int) Math.round( 
						i * Math.tan(Math.toRadians(angle)) 
						- g * i * i / (2 * v * v 
						* Math.cos(Math.toRadians(angle)) * Math.cos(Math.toRadians(angle))) 
				);
				
				phi = i + s1;
				psi = p + s2;
				
				if(phi < Map.w && phi > 0 && psi < Map.h && psi > 0) {
					Particle curr = Grid[phi][psi];
					Particle.Type ctype = curr.getType();
					if(ctype == Particle.Type.DIRT) {
						// BOOM
						//System.out.println("wybuch w "+phi+" "+psi);
						exp[0] = 1;
						exp[1] = phi;
						exp[2] = psi;
						exp[3] = this.getPlayer().getCurrWeapon();
						// w exp[3] można schować typ pocisku
						break;
					}
					else if(ctype == Particle.Type.TANK) {
						if(!curr.getTank().equals(this)) {
							// BOOM
							//System.out.println("wybuch w "+phi+" "+psi);
							exp[0] = 1;
							exp[1] = phi;
							exp[2] = psi;
							exp[3] = this.getPlayer().getCurrWeapon();
							// w exp[3] można schować typ pocisku
							break;
						}
					}
					else curr.setShell(this);
				}
				else {
					//System.out.println("wyszliśmy poza planszę w "+phi+" "+psi);
					break;
				}
			}
		}
		
		else {		// jeśli jesteśmy w drugiej ćwiartce, szukany wykres jest symetryczny wzgl. OY
			
			for(int i = 0; i < Map.w; i++) {
				p = (int) Math.round( 
						(-i) * Math.tan(Math.toRadians(angle)) 
						- g * (-i) * (-i) / (2 * v * v 
						* Math.cos(Math.toRadians(angle)) * Math.cos(Math.toRadians(angle))) 
				);
				
				phi = s1 - i;
				psi = p + s2;
				
				if(phi < Map.w && phi > 0 && psi < Map.h && psi > 0) {
					Particle curr = Grid[phi][psi];
					Particle.Type ctype = curr.getType();
					if(ctype == Particle.Type.DIRT) {
						// BOOM
						//System.out.println("wybuch w "+phi+" "+psi);
						exp[0] = 1;
						exp[1] = phi;
						exp[2] = psi;
						exp[3] = this.getPlayer().getCurrWeapon();
						// w exp[3] można schować typ pocisku
						break;
					}
					else if(ctype == Particle.Type.TANK) {
						if(!curr.getTank().equals(this)) {
							// BOOM
							//System.out.println("wybuch w "+phi+" "+psi);
							exp[0] = 1;
							exp[1] = phi;
							exp[2] = psi;
							exp[3] = this.getPlayer().getCurrWeapon();
							// w exp[3] można schować typ pocisku
							break;
						}
					}
					else curr.setShell(this);
				}
				else {
					//System.out.println("wyszliśmy poza planszę w "+phi+" "+psi);
					exp[0] = 1;
					exp[1] = phi;
					exp[2] = psi;
					exp[3] = this.getPlayer().getCurrWeapon();
					break;
				}
			}
		}
		return exp;		// zwracamy dane o eksplozji
	}

}
