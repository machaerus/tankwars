import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class GameLogic implements Runnable {

	private Thread gameplay;
	private ArrayList<Player> Players;
	private GamePanel GPanel;
	private Semaphore semaphore;
	private boolean running = false;
	private boolean gameOver = false;
	
	GameLogic(ArrayList<Player> pl, GamePanel gp, Semaphore semaphore) {
		Players = pl;
		GPanel = gp;
		this.semaphore = semaphore;
	}
	
	public void startGame() {
		running = true;
		gameplay = new Thread(this);
		gameplay.start();		
	}
	
	public void stopGame() {
		running = false;
	}
	
	public void gameOver() {
		gameOver = true;
	}

	public void run() {
		
		Player p;
		
		for(int i = 0; i < Players.size(); i++) {	// tworzymy czołg dla każdego gracza 
			p = Players.get(i);
			p.setTank(GPanel.addTank(p.getInitPos(),100,p));
			p.deployAI(GPanel);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		
		while(running) {
			if(!gameOver)
			for(int i = 0; i < Players.size(); i++) {
				p = Players.get(i);
				if(p.isAlive()) {
					
					// sprawdzamy, czy nie koniec
					
					int count = 0;
					for(int j = 0; j < Players.size(); j++) {
						if(Players.get(j).isAlive()) count++;
					}
					if(count == 1) GPanel.gameOver(p);
					else {
						GPanel.setCurrent(p);
						
						if(!p.isAI()) {
							try {
								semaphore.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							GPanel.useAI(p);
							try {
								semaphore.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			else GPanel.setCurrent(null);
		}
		
	}

}
