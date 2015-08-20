package PatternClimber;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class LittleWall implements ActionListener {

	/**
	 * Little Wall Rock Climbing Copyright 2009 Eric McCreath GNU LGPL
	 */

	final static Dimension dim = new Dimension(800, 600);
	final static XYPoint wallsize = new XYPoint(8.0, 6.0);

	JFrame jframe;
	GameComponent canvas;
	Wall wall;
	PlayerSpring player;
	Timer timer;

	public LittleWall() {
		jframe = new JFrame("Little Wall");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas = new GameComponent(dim);
		jframe.getContentPane().add(canvas);
		jframe.pack();
		jframe.setVisible(true);
	}

	public static void main(String[] args) throws InterruptedException {
		LittleWall lw = new LittleWall();
		lw.drawTitleScreen();
		lw.startRunningGame();
	}

	private void startRunningGame() {
		wall = new Wall(dim, wallsize);
		wall.draw(canvas.getBackgroundGraphics());
		player = new PlayerSpring(wall);
		canvas.addMouseMotionListener(player);
		canvas.addKeyListener(player);

		timer = new Timer(1000 / 15, this);
		timer.start();
	}

	private void drawTitleScreen() throws InterruptedException {
		Graphics2D bg = canvas.getBackgroundGraphics();
		bg.setColor(Color.white);
		bg.fillRect(0, 0, dim.width, dim.height);
		canvas.clearOffscreen();

		Graphics2D os = canvas.getOffscreenGraphics();
		os.setColor(Color.black);
		os.drawString("Little Wall Climbing", 100, 100);

		// add your code here to make the title screen more interesting.

		canvas.drawOffscreen();
//		UNCOMMENT TO EVOKE SLEEPING
//		Thread.sleep(1000);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			canvas.clearOffscreen();
			Graphics2D of = canvas.getOffscreenGraphics();
			player.draw(of);
			player.update(canvas, wall);
			canvas.drawOffscreen();
		}
	}
}
