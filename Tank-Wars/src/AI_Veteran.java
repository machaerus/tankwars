
public class AI_Veteran extends AI{

	//private int[] prevShot;
	
	AI_Veteran() {
		super();
		//prevShot = new int[3];
	}
	
	public void saveData() {
		// zapisujemy dane ostatniego strzału do prevShot
	}
	
	public void aim(Particle[][] Grid) {
		
		// losujemy cel
		
		int num;
		do {
			num = ran.nextInt(opps.size());
		}
		while(opps.get(num).equals(tank));
		target = opps.get(num);
		
		// odległość od celu
		//double dist0 = Map.dist(tank.getX(), tank.getY(), target.getX(), target.getY());
		double dist1 = 0; // odległość od wybuchu
		
		int angle = 0;
		double v = 0;
		int flag = 5;
		
		while(flag > 0) {
		
			// losujemy kąt lufy
			
			int r = ran.nextInt(60);
			if(target.getX() < tank.getX()) 	angle = r + 100; 				// jeśli cel po lewej, losujemy kąt [90,180]
			else 								angle = r + 20;					// losujemy [0,90]
			
			// dla wylosowanego kąta obliczamy prędkość potrzebną dla trafienia (zakładając brak przeszkód na drodze)
			
			double a = Math.toRadians(angle);
			int x = target.getX() - tank.getX();
			int y = target.getY() - tank.getY();
			v = (Math.sqrt(Tank.g)*x*(1/Math.cos(a)))/(Math.sqrt(2)*Math.sqrt(-y + x*Math.tan(a)));
			
			// sprawdzamy istnienie przeszkód i w razie czego powtarzamy
			// - algorytm analogiczny do Tank.fire()
			
			int s1 = tank.getX() + 3;
			int s2 = tank.getY() + 2;	// punkt startowy
			
			int p, phi, psi;
			int velocity = (int)v / 2;
			
			if(angle <= 90) {	// jeśli jesteśmy w pierwszej ćwiartce
				
				for(int i = 0; i < Map.w; i++) {
					
					// obliczamy krzywą balistyczną
					
					p = (int) Math.round( 
							i * Math.tan(Math.toRadians(angle)) 
							- Tank.g * i * i / (2 * velocity * velocity 
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
							dist1 = Map.dist(tank.getX(), tank.getY(), phi, psi);
							if(dist1 < 10) {
								flag--;
								break;
							}
						}
						else if(ctype == Particle.Type.TANK) {
							if(!curr.getTank().equals(this)) {
								// BOOM
								//System.out.println("wybuch w "+phi+" "+psi);
								flag = 0;
								break;
							}
							else {
								flag = 0;
								break;
							}
						}
					}
					else {
						//System.out.println("wyszliśmy poza planszę w "+phi+" "+psi);
						flag--;
						break;
					}
				}
			}
			
			else {		// jeśli jesteśmy w drugiej ćwiartce, szukany wykres jest symetryczny wzgl. OY
				
				for(int i = 0; i < Map.w; i++) {
					p = (int) Math.round( 
							(-i) * Math.tan(Math.toRadians(angle)) 
							- Tank.g * (-i) * (-i) / (2 * velocity * velocity 
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
							dist1 = Map.dist(tank.getX(), tank.getY(), phi, psi);
							if(dist1 < 10) {
								flag--;
								break;
							}
							else {
								flag = 0;
								break;
							}
						}
						else if(ctype == Particle.Type.TANK) {
							if(!curr.getTank().equals(this)) {
								// BOOM
								//System.out.println("wybuch w "+phi+" "+psi);
								flag = 0;
								break;
							}
						}
					}
					else {
						//System.out.println("wyszliśmy poza planszę w "+phi+" "+psi);
						flag--;
						break;
					}
				}
			}
		
		}
		
		tank.setAngle((int) Math.round(angle + ran.nextGaussian()*6));
		tank.setVelocity((int) Math.round(v*2 + ran.nextGaussian()*4));
		
	}
	
}
