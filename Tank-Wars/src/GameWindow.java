import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = -2492342311106104752L;
	private static String title = "DER GEYERS SCHWARZE HAUFEN";
	private GamePanel gp;
	private StartScreen startScreen;
	private Semaphore panelSemaphore;
	private Random ran;

	GameWindow() {
		super(title);
		setLocation(50, 50);
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		ran = new Random();
		Game();
	}

	public void Game() {

		// GLOBALNE ZMIENNE

		ArrayList<Player> Players = new ArrayList<Player>();
		panelSemaphore = new Semaphore(0, true);

		////////////////////////////////////////////////////////////
		//// 													////
		//// 	   EKRAN STARTOWY Z WYBOREM GRACZY I OPCJI 		////
		//// 													////
		////////////////////////////////////////////////////////////

		startScreen = new StartScreen(panelSemaphore);

		setContentPane(startScreen);
		startScreen.setFocusable(true);
		startScreen.requestFocusInWindow();
		pack();
		setVisible(true);

		try {
			panelSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// losujemy pozycje początkowe

		int PlayersNum = 4;

		LinkedList<Integer> permutation = new LinkedList<Integer>();
		for (int i = 0; i < PlayersNum; i++) {
			permutation.add(i);
		}

		int[] tmp = new int[PlayersNum];
		int tmp_index;

		// tworzymy losową permutację indeksów

		for (int i = 0; i < PlayersNum; i++) {
			tmp_index = ran.nextInt(permutation.size());
			tmp[i] = permutation.get(tmp_index);
			permutation.remove(tmp_index);
		}

		int[] initPos = new int[PlayersNum];
		int[] positions = { 30, 80, 130, 170 };

		for (int i = 0; i < PlayersNum; i++) {
			initPos[tmp[i]] = positions[i];
		}

		// tworzymy graczy

		Player p1 = new Player(
				Messages.getString("Main.0"), initPos[0], "german", false, new AI_Veteran()); //$NON-NLS-1$
		Player p2 = new Player(
				Messages.getString("Main.1"), initPos[1], "soviet", true, new AI_Veteran()); //$NON-NLS-1$
		Player p3 = new Player(
				Messages.getString("Main.2"), initPos[2], "british", true, new AI_Veteran()); //$NON-NLS-1$
		Player p4 = new Player(
				Messages.getString("Main.3"), initPos[3], "polish", true, new AI_Veteran()); //$NON-NLS-1$

		Players.add(p1);
		Players.add(p2);
		Players.add(p3);
		Players.add(p4);

		// POCZATEK PĘTLI GRY

		////////////////////////////////////////////////////////////
		//// 													////
		//// 			TU ZACZYNA SIĘ WŁAŚCIWA GRA 			////
		//// 													////
		////////////////////////////////////////////////////////////

		// powtarzamy algorytm losowania pozycji (większość potrzebnych obiektów
		// już mamy)

		// losujemy pozycje początkowe

		permutation = new LinkedList<Integer>();
		for (int i = 0; i < PlayersNum; i++) {
			permutation.add(i);
		}

		// tworzymy losową permutację indeksów

		for (int i = 0; i < PlayersNum; i++) {
			tmp_index = ran.nextInt(permutation.size());
			tmp[i] = permutation.get(tmp_index);
			permutation.remove(tmp_index);
		}

		for (int i = 0; i < PlayersNum; i++) {
			initPos[tmp[i]] = positions[i];
		}

		// TODO: przydzielamy graczom nowe pozycje i maksymalne zdrowie

		// tworzymy semafor do synchronizacji wątków

		Semaphore semaphore = new Semaphore(1, true);

		// tworzymy grę

		try {
			gp = new GamePanel(semaphore);
		} catch (Exception e) {
			System.out.println("Blad podczas tworzenia GamePanel");
			e.printStackTrace();
		}

		GameLogic GLogic = new GameLogic(Players, gp, semaphore);
		gp.setLogic(GLogic);

		// dodajemy GamePanel do okna, co rozpoczyna grę

		setContentPane(gp);
		gp.setFocusable(true);
		gp.requestFocusInWindow();
		pack();
		setVisible(true);
		GLogic.startGame();

		// KONIEC RUNDY

		////////////////////////////////////////////////////////////
		//// 													////
		//// 		  EKRAN PUNKTACJI I ZAKUPU BRONI 			////
		//// 													////
		////////////////////////////////////////////////////////////

		// ...

		// TODO: ekran punktacji

		// KONIEC PĘTLI
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					System.exit(0);
				}
			}
			// } else if (e.getID() == KeyEvent.KEY_RELEASED) {
			// System.out.println("2test2");
			// } else if (e.getID() == KeyEvent.KEY_TYPED) {
			// System.out.println("3test3");
			// }
			return false;
		}
	}

	public void windowActivated(WindowEvent e) {
		gp.resumeGame();
	}

	public void windowDeactivated(WindowEvent e) {
		gp.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) {
		gp.resumeGame();
	}

	public void windowIconified(WindowEvent e) {
		gp.pauseGame();
	}

	public void windowClosing(WindowEvent e) {
		gp.stopGame();
	}

}
