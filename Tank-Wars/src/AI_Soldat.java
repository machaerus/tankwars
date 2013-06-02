
public class AI_Soldat extends AI{

	//private int[] prevShot;
	
	AI_Soldat() {
		super();
		//prevShot = new int[3];
	}
	
	public void saveData() {
		// zapisujemy dane ostatniego strzału do prevShot
	}
	
	public void aim(Particle[][] Grid) {
		
//		// określamy odległości od celów
//		
//		ArrayList<Double> dist = new ArrayList<Double>(opps.size());
//		for(int i = 0; i < opps.size(); i++) {
//			if(!opps.get(i).equals(tank)) {
//				dist.add(Map.dist(tank.getX(), tank.getY(), opps.get(i).getX(), opps.get(i).getY()));
//			} 
//			else dist.add(Double.MAX_VALUE);
//		}
//		
//		// wybieramy najbliższy cel
//		
//		ArrayList<Double> s = new ArrayList<Double>(dist);
//		Collections.sort(s);
//		int index = dist.indexOf(s.get(0));
//		target = opps.get(index);
		
		// losujemy cel
		
		int i;
		do {
			i = ran.nextInt(opps.size());
		}
		while(opps.get(i).equals(tank));
		target = opps.get(i);
		
		// losujemy kąt lufy
		
		int angle;
		int r = ran.nextInt(70);
		if(target.getX() < tank.getX()) 	angle = r + 95; 				// jeśli cel po lewej, losujemy kąt [90,180]
		else 								angle = r + 15;					// losujemy [0,90]
		
		// dla wylosowanego kąta obliczamy prędkość potrzebną dla trafienia (zakładając brak przeszkód na drodze)
		
		double a = Math.toRadians(angle);
		int x = target.getX() - tank.getX();
		int y = target.getY() - tank.getY();
		double v = (Math.sqrt(Tank.g)*x*(1/Math.cos(a)))/(Math.sqrt(2)*Math.sqrt(-y + x*Math.tan(a)));
		
		tank.setAngle((int) Math.round(angle + ran.nextGaussian()*8));
		tank.setVelocity((int) Math.round(v*2 + ran.nextGaussian()*5));
		
	}
	
}
