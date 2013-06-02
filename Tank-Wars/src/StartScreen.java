import java.applet.AudioClip;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;


public class StartScreen extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 6797146746644605909L;
	private Thread animator; 
	private volatile boolean running = false; 
	private static final int PWIDTH = 1000; 
	private static final int PHEIGHT = 592;
	private static final int speed = 100;
	private Graphics2D dbg;
	private Image dbImage = null;
	private Semaphore panelSemaphore;
	
	// czcionki
	
	private Font terminus;
	
	private boolean isSoundOn = false;
	

	StartScreen(Semaphore s) {
		super();
		panelSemaphore = s;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		
		// ładujemy czcionki
		
		InputStream fin = this.getClass().getResourceAsStream("/fonts/Terminus.ttf");
		  try {
			this.terminus = Font.createFont ( 
			    Font.PLAIN,
			    fin
			  ).deriveFont(16l);
		} catch (FontFormatException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
		
		
		setFocusable(true);
		requestFocus(); 
		//readyForTermination();

		// włącz obsługę klawiatury i myszki
		
		setControls();
	}
	
	
	public void addNotify()
	/*
	 * Wait for the JPanel to be added to the JFrame/JApplet before starting.
	 */
	{
		super.addNotify();
		launch();
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
	
	private void setControls() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				testPress(e.getX(), e.getY());
			}
		});
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
					switch(keyCode) {
							
					case KeyEvent.VK_SPACE:
						finish();
						break;
						
					default:
						break;									
					}
				}
		});
	}
	
	private void testPress(int x, int y)
	{
		// zrób coś
	}
	
	private void gameUpdate() {
		
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
	    
	    Font font = new Font("Monospaced",Font.PLAIN,13);
		dbg.setFont(terminus);
		dbg.setColor(Color.white);
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	    dbg.drawString("Pamiętaj: - Brudny czołg - Hańbą twojego batalionu!",100,100);
	    
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
	
	public void launch()
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} 
	
	private void finish() {
		running = false;
	}
	
	public void run()
	{
		running = true;
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen(); 
			try {
				Thread.sleep(speed); 
			} catch (InterruptedException ex) {}
		}
		panelSemaphore.release();
	}

}
