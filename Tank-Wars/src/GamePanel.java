import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private static final int PWIDTH = 1000; 
	private static final int PHEIGHT = 592;
	private static final int speed = 30;
	
	private Thread animator; 
	private volatile boolean running = false; 
	private volatile boolean gameOver = false; 
	//private boolean isPaused = false;
	private Graphics2D dbg;
	private Image dbImage = null;
	private GameLogic GLogic;
	private Semaphore semaphore;
	
	private BufferedImage background;
	private BufferedImage[] tankImage;
	private BufferedImage[] shellImage;
	private AudioClip fireSound;
	private AudioClip bellSound;
	private AudioClip vUp;
	private AudioClip vDown;
	private AudioClip aUp;
	private AudioClip aDown;
	
	private boolean isSoundOn = false;
	
	private Map map;
	private ArrayList<Tank> Tanks;
	private Tank currTank = null;
	private Tank lastTank = null;
	private LinkedList<Message> screenMessages;
	public LinkedList<Explosion> Explosions;	// możemy stworzyć dowolną ilość animacji, np. zależnie od broni
	
	
	GamePanel(Semaphore semaphore) throws Exception {
		this.semaphore = semaphore;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		
		// wczytywanie tła i obrazków
		
		tankImage = new BufferedImage[4];
		shellImage = new BufferedImage[9];
		
		try {
			background = ImageIO.read(this.getClass().getResource("/gfx/tank-wars-bg.jpg"));
			tankImage[0] = ImageIO.read(this.getClass().getResource("/gfx/soviet.png"));
			tankImage[1] = ImageIO.read(this.getClass().getResource("/gfx/german.png"));
			tankImage[2] = ImageIO.read(this.getClass().getResource("/gfx/british.png"));
			tankImage[3] = ImageIO.read(this.getClass().getResource("/gfx/polish.png"));
			shellImage[0] = ImageIO.read(this.getClass().getResource("/gfx/odlamkowy.png"));
			shellImage[1] = ImageIO.read(this.getClass().getResource("/gfx/odlamkowy-empty.png"));
			shellImage[2] = ImageIO.read(this.getClass().getResource("/gfx/odlamkowy-active.png"));
			shellImage[3] = ImageIO.read(this.getClass().getResource("/gfx/burzacy.png"));
			shellImage[4] = ImageIO.read(this.getClass().getResource("/gfx/burzacy-empty.png"));
			shellImage[5] = ImageIO.read(this.getClass().getResource("/gfx/burzacy-active.png"));
			shellImage[6] = ImageIO.read(this.getClass().getResource("/gfx/flara.png"));
			shellImage[7] = ImageIO.read(this.getClass().getResource("/gfx/flara-empty.png"));
			shellImage[8] = ImageIO.read(this.getClass().getResource("/gfx/flara-active.png"));
		} catch(IOException e) {
			System.out.println("Blad podczas wczytywania grafiki.");
		}
		
		// wczytywanie dźwięków
		
		fireSound = Applet.newAudioClip(this.getClass().getResource("/sounds/Centipede_Death.wav"));
		bellSound = Applet.newAudioClip(this.getClass().getResource("/sounds/Power On 001.wav"));
		vUp = Applet.newAudioClip(this.getClass().getResource("/sounds/Galaga_Tink03_looped.wav"));
		vDown = Applet.newAudioClip(this.getClass().getResource("/sounds/Galaga_Tink03_looped.wav"));
		aUp = Applet.newAudioClip(this.getClass().getResource("/sounds/Galaga_Tink03_looped.wav"));
		aDown = Applet.newAudioClip(this.getClass().getResource("/sounds/Galaga_Tink03_looped.wav"));
		
		// przygotowywanie planszy
		
		Tanks = new ArrayList<Tank>();
		screenMessages = new LinkedList<Message>(); 
		Explosions = new LinkedList<Explosion>();
		try {
			map = new Map(this);
		} catch (FileNotFoundException e) {
			System.out.println("Nie udalo sie zainicjalizowac mapy");
			e.printStackTrace();
		}
	
		setFocusable(true);
		requestFocus(); 
		//readyForTermination();

		// włącz obsługę klawiatury i myszki
		
		setControls();
	}
		
	public void setLogic(GameLogic gl) {
		GLogic = gl;
	} 
	
	public void addNotify()
	/*
	 * Wait for the JPanel to be added to the JFrame/JApplet before starting.
	 */
	{
		super.addNotify();
		startGame();
	}
	
	// Listener do wyłączania gry
	
//	private void readyForTermination() {
//		addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent e) {
//				int keyCode = e.getKeyCode();
//				if ((keyCode == KeyEvent.VK_ESCAPE)
//						|| (keyCode == KeyEvent.VK_Q)
//						|| (keyCode == KeyEvent.VK_END)
//						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
//					running = false;
//				}
//			}
//		});
//	} 
	
	// Listenery do obsługi gry
	
	private void setControls() {
				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						testPress(e.getX(), e.getY());
					}
				});
				addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						int keyCode = e.getKeyCode();
						if(currTank != null) {					// obsługujemy wejście pod warunkiem, że jest przypisany bieżący gracz 
							Player p = currTank.getPlayer();	// i nie jest graczem komputerowym
							if(!p.isAI())
							switch(keyCode) {
							
								case KeyEvent.VK_SPACE:
									if(p.getCurrAmmo() > 0) {
										p.decWeapon();
										map.fire(currTank);
										playSound(fireSound);
										lastTank = currTank;
										currTank = null;
										endOfTurn();
									}
									else {
										screenMessages.add(
											new Message(
												"Brak amunicji!", 
												70, 
												90,
												Color.white,
												Color.black,
												20,
												30
											)
										);
									}
									break;
									
								case KeyEvent.VK_UP:
									currTank.incVelocity();
									playSound(vUp);
									break;
									
								case KeyEvent.VK_DOWN:	
									currTank.decVelocity();
									playSound(vDown);
									break;
									
								case KeyEvent.VK_LEFT:
									currTank.incAngle();
									playSound(aUp);
									break;
									
								case KeyEvent.VK_RIGHT:
									currTank.decAngle();
									playSound(aDown);
									break;
									
								case KeyEvent.VK_H:
									for(int i = 0; i < Tanks.size(); i++) {
										Tank t = Tanks.get(i);
										screenMessages.add(
											new Message(
												"HP = "+t.getPlayer().getHP(), 
												t.getX() - 2, 
												t.getY() + 13,
												Color.white,
												Color.black,
												15
											)
										);
									}
									break;

								case KeyEvent.VK_N:
									for(int i = 0; i < Tanks.size(); i++) {
										Tank t = Tanks.get(i);
										screenMessages.add(
											new Message(
												t.getPlayer().getName(), 
												t.getX() - 2, 
												t.getY() + 13,
												Color.white,
												Color.black,
												20
											)
										);
									}
									break;
									
								case KeyEvent.VK_PERIOD:
									p.nextWeapon();
									String s = p.getWeapon(p.getCurrWeapon()).getName()+": "+p.getCurrAmmo();
									FontMetrics fm = dbg.getFontMetrics();
									Rectangle2D rect = fm.getStringBounds(s, dbg);
									double w = rect.getWidth();
									screenMessages.add(
											new Message(
												s,
												Map.w - (8 + (int)Math.round(w/Particle.size)), 
												103,
												Color.white,
												Color.black,
												20
											)
									);
									break;
									
								case KeyEvent.VK_A:
									for(int i = 0; i < shellImage.length/3; i++) {
										screenMessages.add(
												new Message(
													Integer.toString(p.getAmmo(i)),
													133 + 8*i, 
													103,
													Color.white,
													Color.black,
													20
												)
										);
									}
									break;
									
								default:
									break;									
							}
						}
					}
				});
	}

	private void testPress(int x, int y)
	{
		if (!gameOver) {
			// zrób coś
		}
	}
	
	/******************************************************************/
	
	public Tank addTank(int x, int y, Player pl) {
		Tank t = new Tank(x,y,pl);
		Tanks.add(t);
		map.spawnTank(t);
		return t;
	}
	
	public void checkTanks(int x0, int y0, Shell shell) {
		
		// a, b - współrzędne czołgu w Particles
		// x0, y0 - współrzędne centrum eksplozji w pikselach
		
		int range = shell.getRange();
		double d;
		Tank t;
		int a, b;
//		int pos = 30;
		for(int i = 0; i < Tanks.size(); i++) {
			t = Tanks.get(i);
			a = t.getX() + 3;
			b = t.getY() + 3;
//			screenMessages.add(new Message("tank", a, b, Color.white, Color.black, 100));
//			screenMessages.add(new Message(
//					"explosion", 
//					Math.round(x0/Particle.size), 
//					Map.h - Math.round(y0/Particle.size), 
//					Color.white, 
//					Color.red, 
//					1000));
			d = Map.dist(x0, y0, a * Particle.size, (Map.h - b) * Particle.size);
//			screenMessages.add(new Message(t.getPlayer().getName()+" d = "+d, 20, pos, Color.white, Color.black, 2000));
//			pos += 7;
			if(d < range/2)			t.hit(shell.destruction());
			else if(d < range) 		t.hit(shell.destruction()/2);
			
			if(t.getPlayer().getHP() <= 0) {
				Explosions.add(new Explosion(map, t.getX()+3, t.getY()+3, new Shell(30,30,30,50,20,"Eksplozja czołgu")));
				map.purgeTank(t);
				Tanks.remove(t);
				if(t.equals(currTank)) {
					lastTank = currTank;
					currTank = null;
					semaphore.release();
				}
			}
		}
	}
	
	public ArrayList<Tank> getTanks() {
		return Tanks;
	}
	
	public void setCurrent(Player player) {
		if(player != null) currTank = player.getTank();
		else {
			lastTank = currTank;
			currTank = null;
		}
	}
	
	public void useAI(Player player) {
		player.useAI(map.getGrid());
		player.decWeapon();
		map.fire(currTank);
		playSound(fireSound);
		lastTank = currTank;
		currTank = null;
		endOfTurn();
	}
	
	private void endOfTurn() {			// pozwalamy opuścić semafor dopiero wtedy, 
										// gdy skończyły się już eksplozje (i ich skutki) 
		if(Explosions.isEmpty()) {
			semaphore.release();
		}
		else {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			endOfTurn();
		}
		
	}
	
	private void gameUpdate() {
		map.applyGravity();
	}
	
	private void gameRender()
	{
		if (dbImage == null) { // tworzyme bufor
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				dbg = (Graphics2D) dbImage.getGraphics();
		}
		
		// czyścime tło
		
		dbg.setColor(Color.blue);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		dbg.drawImage(background, 0, 0, null);
		
		// rysujeme elementy giery
		
		map.draw(dbg);		
	    
	    for(int i = 0; i < Tanks.size(); i++) {
	    	Tank t = Tanks.get(i);
	    	map.drawGun(t,dbg);
	    	
	    	int nation = 0;
	    	String n = t.getPlayer().getNation();
	    	
	    	/*
	    	switch(t.getPlayer().getNation()) {
	    	case "soviet":
	    		nation = 0;
	    		break;
	    	case "german":
	    		nation = 1;
	    		break;
	    	case "british":
	    		nation = 2;
	    		break;
	    	case "polish":
	    		nation = 3;
	    		break;
	    	}
	    	*/
	    	
	    	if		(n == "soviet")		nation = 0;
	    	else if (n == "german")		nation = 1;
	    	else if (n == "british")	nation = 2;
	    	else if (n == "polish")		nation = 3;
	    	
	    	dbg.drawImage(tankImage[nation],t.getX()*Particle.size,(Map.h-t.getY()-7)*Particle.size, null); 
	    }
	    
	    for(int i = 0; i < Explosions.size(); i++) {
	    	Explosion e = Explosions.get(i);
	    	e.draw(dbg);
	    }
	    
	    Font font = new Font("Monospaced",Font.PLAIN,13);
	    
	    Tank t;
	    if(currTank != null) t = currTank;
	    else t = lastTank;
	    
	    if(t != null) {
			dbg.setFont(font);
			dbg.setColor(Color.white);
			dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			dbg.drawString(t.getPlayer().getName(), 50, 50);
			dbg.drawString("HP = "+t.getPlayer().getHP(), 250, 50);
			dbg.drawString("v = "+t.getVelocity()+", a = "+t.getAngle(), 50, 70);
			
			// rysujeme ikonki broni
			
			int pos = 860;
			for(int i = 0; i < shellImage.length/3; i++) {
				if(t.getPlayer().getAmmo(i) == 0)
					dbg.drawImage(shellImage[i*3 + 1], pos + 40*i, 35, null);
				else if(t.getPlayer().getCurrWeapon() == i)
					dbg.drawImage(shellImage[i*3 + 2], pos + 40*i, 35, null);
				else
					dbg.drawImage(shellImage[i*3], pos + 40*i, 35, null);
			}
	    }
	    
	    for(int i = 0; i < screenMessages.size(); i++) {
	    	Message m = screenMessages.get(i);
	    	m.show(dbg);
	    }
	    
	    for(int i = 0; i < screenMessages.size(); i++) {
	    	Message m = screenMessages.get(i);
	    	if(m.isDone()) screenMessages.remove(m);
	    }
	    
	    for(int i = 0; i < Explosions.size(); i++) {
	    	Explosion e = Explosions.get(i);
	    	if(e.isDone()) Explosions.remove(e);
	    }
	    
	    dbg.setFont(font);
	    dbg.setColor(Color.white);
	    
	    // statystyki na potrzeby debugowania
	    
//	    dbg.drawString("Messages: "+screenMessages.size(), 500, 50);
//	    dbg.drawString("Explosions: "+Explosions.size(), 500, 75);
	    
	}
	
	private void playSound(AudioClip sound) {
		if(isSoundOn) sound.play();
	}
	
	private void paintScreen()
	{
		Graphics2D g;
		try {
			g = (Graphics2D) this.getGraphics(); 
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync(); 
			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} 
	
	public void paintComponent(Graphics2D g) {
		super.paintComponent(g);
		if (dbImage != null)
			g.drawImage(dbImage, 0, 0, null);
	}
	
	private void startGame()
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} 
	
	public void resumeGame()
	{
		//isPaused = false;
	}

	public void pauseGame()
	{
		//isPaused = true;
	}

	public void stopGame()
	{
		GLogic.stopGame();
		running = false;
	}
	
	public void gameOver(Player p) {
		screenMessages.add(
			new Message(
				p.getName()+" wygrał w gre jebany.", 
				30, 
				90,
				Color.white,
				Color.black,
				23,
				500
			)
		);
		GLogic.gameOver();
		gameOver = true;
	}
	
	public void run()
	{
		running = true;
		playSound(bellSound);
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen(); 
			try {
				Thread.sleep(speed); 
			} catch (InterruptedException ex) {}
		}
		System.exit(0); // 
	}

}
