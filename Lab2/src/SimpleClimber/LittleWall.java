package SimpleClimber;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LittleWall implements ActionListener {

	/**
	 * Little SimpleClimber.Wall Rock Climbing Copyright 2009 Eric McCreath GNU LGPL
	 */

	final static Dimension dim = new Dimension(800, 600);
	final static XYPoint wallsize = new XYPoint(8.0, 6.0);

	JFrame jframe;
	GameComponent canvas;
	Wall wall;
	PlayerSpring player;
	Timer timer;

	public LittleWall() {
		jframe = new JFrame("Little SimpleClimber.Wall");
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
		BufferedImage img =null;
		Graphics2D bg = canvas.getBackgroundGraphics();
		bg.setColor(Color.white);
		bg.fillRect(0, 0, dim.width, dim.height);
		canvas.clearOffscreen();

		Graphics2D os = canvas.getOffscreenGraphics();
		os.setColor(Color.black);

		/**
		*
		*Description: An 'interesting' large font for the title changed.
		 *
		*@author Lu Chen
		*/
		os.setFont(new Font("TimesRoman", Font.BOLD, 40));
		os.drawString("Little SimpleClimber.Wall Climbing", 100, 100);

		/**
		*
		*Description: Game play instructions added.
		*
		*@author Lu Chen
		*/
		os.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		os.drawString("Game instruction:", 150, 130);
		os.drawString("Press A for left arm, Press S for right arm" , 160, 150);
		os.drawString("Press Z for left leg, Press X for right arm" , 170, 170);
		os.drawString("Move mouse, climb up" , 200, 190);

		/**
		*
		 *Description: Smiling logo that I have constructed added.
		 *
		 *@author Lu Chen
		 */
		os.fill(new Ellipse2D.Double(60.0, 10.0, 4.0, 4.0));
		os.fill(new Ellipse2D.Double(135.0, 10.0, 4.0, 4.0));
		for (double u = 0.0; u < 1.0; u += 0.001) {
			double x = (80.0) * u + 60.0;
			double y = (-120.0) * u * u + (120.0) * u + 20.0;
			os.fillRect((int) x, (int) y, 1, 1);
		}

		/**
		*
		*Description: Image added, resized and rotated
		*
		*@author Lu Chen
		*/

		try
		{
			img = ImageIO.read(new File("image/climbing.png"));
		}
		catch ( IOException exc )
		{
			exc.printStackTrace();
		}
		AffineTransform trans = new AffineTransform();
//		do the translate first
		trans.translate(20,20);
		trans.rotate( Math.toRadians(45));
		trans.scale(0.3, 0.3);
		os.drawImage(img, trans, null);

		canvas.drawOffscreen();
//		UNCOMMET THIS TO EVOKE SLEEPING
		Thread.sleep(1000);
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
