import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Map {

	private GamePanel GPanel;
	public static int w = 200;
	public static int h = 120;
	private Particle[][] Grid;
	private Random ran;
	
	
	Map(GamePanel gp) throws FileNotFoundException {
		GPanel = gp;
		Grid = new Particle[w][h];
		
		for(int i = h-1; i >= 0; i--) {		// lewy dolny róg to (0,0)
			for(int j = 0; j < w; j++) {
				Grid[j][i] = new Particle(j,i);
			}
		}
		
		// generujemy teren
		
		ran = new Random();
		
		generate();
		
	}
	
	public Particle[][] getGrid() {
		return Grid;
	}
	
	private void generate() {	
		Scanner reader = null;
		try {
			reader = new Scanner(new File((this.getClass().getClassLoader().getResource("data/fx.ss")).getFile()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> Poly = new ArrayList<String>();
		while(reader.hasNextLine()) {
			Poly.add(reader.nextLine());
			//System.out.println("czytam kolejna linijke");
		}
		int	p = ran.nextInt(Poly.size());
		StringTokenizer tokenizer = new StringTokenizer(Poly.get(p));
		ArrayList<Double> poly = new ArrayList<Double>();
		while(tokenizer.hasMoreTokens()) poly.add(Double.parseDouble(tokenizer.nextToken()));
		
		for(int i = 0; i < h; i++) {		// lewy dolny róg to (0,0)
			for(int j = 0; j < w; j++) {
				double fx = 0;
				for(int k = 0; k < poly.size(); k++) {
					//System.out.println(poly.get(k));
					fx += poly.get(k) * Power(j,k);
				}
				if(i < fx)
					Grid[j][i].setDirt();
			}
		}
	}
	
	public void spawnTank(Tank t) {			// zrzucamy czołg na planszę
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 7; j++) {
				Grid[t.getX()+i][t.getY()+j].setTank(t);
			}
		}
	}
	
	public void purgeTank(Tank t) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Particle p = Grid[i+t.getX()][j+t.getY()];
				if(p.getType() == Particle.Type.TANK) {
					p.setAir();
				}
			}
		}
	}
	
	public void fire(Tank t) {
		int[] exp = t.fire(Grid);
		if(exp[0] == 1) {
			// obsługa wybuchu
			GPanel.Explosions.add(new Explosion(this, exp[1], exp[2], t.getPlayer().getWeapon(exp[3])));
		}
	}
	
	public void draw(Graphics2D g) {
		for(int i = 0; i < h; i++) {		// lewy dolny róg to (0,0)
			for(int j = 0; j < w; j++) {
				Grid[j][i].setFlag(false);
				Grid[j][i].draw(g);
			}
		}
	}
	
	public void drawGun(Tank t, Graphics2D g) {	
		int gunLength = 30;
		
		g.setColor(t.getColor());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(
				new BasicStroke(
						5,
						BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER
				)
		);
		g.drawLine(
				(int) ((t.getX()+3) 	* Particle.size	+ 	4 * Math.cos(Math.toRadians(t.getAngle())) + 3 ), 
				(int) ((Map.h-t.getY()-5) * Particle.size -	4 * Math.sin(Math.toRadians(t.getAngle())) ), 
				(int) ((t.getX()+4) 	* Particle.size + 	gunLength * Math.cos(Math.toRadians(t.getAngle())) ), 
				(int) ((Map.h-t.getY()-4) * Particle.size - 	gunLength * Math.sin(Math.toRadians(t.getAngle())) )
		);	
	}
	
	public void applyDestruction(int ex, int ey, Shell shellType) {		// wprowadza efekty napierdalania się
		
		int x0, y0, x1, x2, y1, y2;
		
		int range = shellType.getRange();
		
		// obliczamy brzegi prostokąta, w ktorym zawiera się eksplozja:
		
//		x1 = e.getX();
//		x2 = x1 + e.getRange()*2;
//		y1 = e.getY();
//		y2 = y1 + e.getRange()*2;
		
		x1 = ex - range;
		x2 = x1 + range*2;
		y1 = ey - range;
		y2 = y1 + range*2;
		
		// obliczamy centrum eksplozji:
		
//		x0 = x1 + (x2 - x1) / 2;
//		y0 = y1 + (y2 - y1) / 2;
		
		x0 = ex;
		y0 = ey;
		
		GPanel.checkTanks(x0,y0,shellType);
		
		// sprawdzamy, które cząsteczki w zasięgu rażenia
		
		int m, n;
		double r;
		
		for(int i = x1; i < x2; i++) {
			for(int j = y1; j < y2; j++) {
				if(dist(i,j,x0,y0) < range) {
					m = Math.round((i)/Particle.size);
					n = Map.h - Math.round((j)/Particle.size);
					if(m >= 0 && m < Map.w && n >=0 && n < Map.h) {
						Particle p = Grid[m][n];
						r = ran.nextGaussian();
						if(p.getType() == Particle.Type.DIRT && Math.abs(0-r) > 2) p.setAir(); 
					}
				}
			}
		}
	}
	
	public void applyGravity() {		// wprowadza efekty grawitacji
		
		Particle p, q;
		
		for(int i = 1; i < h; i++) {		// lewy dolny róg to (0,0)
			for(int j = 0; j < w; j++) {
				p = Grid[j][i];
				if(!p.isFlagged()) {
					q = Grid[j][i-1];
					if(p.getType() == Particle.Type.DIRT && q != null) {	// jeśli mamy powietrze pod ziemią
						if(q.getType() == Particle.Type.AIR) {
							p.setAir();
							q.setDirt();
							q.setFlag(true);
						}
					}
					else if(p.getType() == Particle.Type.TANK && q != null) {	// jeśli mamy powietrze pod czołgiem
						if(q.getType() == Particle.Type.AIR) {
							
							// musimy sprawdzić, czy czołg może się o coś oprzeć
							
							if(		   Grid[j+1][i-1].getType() == Particle.Type.AIR
									&& Grid[j+2][i-1].getType() == Particle.Type.AIR
									&& Grid[j+3][i-1].getType() == Particle.Type.AIR
									&& Grid[j+4][i-1].getType() == Particle.Type.AIR
									&& Grid[j+5][i-1].getType() == Particle.Type.AIR
									&& Grid[j+6][i-1].getType() == Particle.Type.AIR ) {
								p.getTank().fall(Grid);
							}							
						}
					}
				}
			}
		}
	} // koniec applyGravity()	
	
	// potrzebne do obliczeń:
	
	public static double dist(double x1, double y1, double x2, double y2) {	// definiujemy se odległość xD
		return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
	}
	
	public static double Power(double base, double exp) {
		return Math.pow(base, exp);
	}
	
} // koniec Map
