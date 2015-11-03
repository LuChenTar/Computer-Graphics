package Freehand;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/*
 * Freehand.DrawArea - a simple JComponent for drawing.  The "offscreen" BufferedImage is
 * used to draw to,  this image is then used to paint the component.
 * Eric McCreath 2009 2015
 */

public class DrawArea extends JComponent implements MouseMotionListener,
		MouseListener {

	private BufferedImage offscreen;
	Dimension dim;
	DrawIt drawit;
	/**
	 *
	 *Description: arraylist for collecting point while mouse moving
	 *
	 *@author Lu Chen
	 */
	ArrayList<Point> currentList = new ArrayList<Point>();
	float alpha;
	int x1, x2, y1, y2;

	public DrawArea(Dimension dim, DrawIt drawit) {
		this.setPreferredSize(dim);
		offscreen = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_RGB);
		this.dim = dim;
		this.drawit = drawit;
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		clearOffscreen();
	}

	public void clearOffscreen() {
		Graphics2D g = offscreen.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, dim.width, dim.height);
		repaint();
	}

	public Graphics2D getOffscreenGraphics() {
		return offscreen.createGraphics();
	}

	public void drawOffscreen() {
		repaint();
	}

	protected void paintComponent(Graphics g) {
		g.drawImage(offscreen, 0, 0, null);
	}

	public void mouseDragged(MouseEvent m) {
		/**
		 *
		 *Description: Main method to draw the line
		 *
		 *@author Lu Chen
		 */
		if(currentList != null) {
			currentList.add(m.getPoint());
		}
		alpha = (Float) drawit.OpacityToolbar.getSelectCommand();
		Graphics2D g = offscreen.createGraphics();
		if(currentList.size() >= 1) {
			for(int i = 0; i < currentList.size() - 1; i++) {
				x1 = currentList.get(i).x;
				y1 = currentList.get(i).y;
				x2 = currentList.get(i + 1).x;
				y2 = currentList.get(i + 1).y;
			}
			//drawing
			if(!drawit.colorToolbar.getSelectCommand().equals((Object) Color.WHITE)) {
				//set up the graphics color
				if (drawit.colorToolbar.getSelectCommand().equals((Object) Color.RED)) {
					g.setColor(new Color(1, 0, 0, alpha));
				} else if (drawit.colorToolbar.getSelectCommand().equals((Object) Color.BLUE)) {
					g.setColor(new Color(0, 0, 1, alpha));
				} else if (drawit.colorToolbar.getSelectCommand().equals((Object) Color.GREEN)) {
					g.setColor(new Color(0, 1, 0, alpha));
				}
				//set up the painting brush
				//normal painting brush with different thickness
				if (((Integer) drawit.thicknessToolbar.getSelectCommand() < 11)) {
					g.setStroke(new BasicStroke((Integer) drawit.thicknessToolbar.getSelectCommand()));
					g.drawLine(x1, y1, x2, y2);
					//spray painter
				} else if ((Integer) drawit.thicknessToolbar.getSelectCommand() == 13) {
					sprayPaint(x1, y1, g);
					//smudge
				}else if((Integer) drawit.thicknessToolbar.getSelectCommand() == 12) {

				}else if((Integer) drawit.thicknessToolbar.getSelectCommand() == 14) {
					floodFill(x1, y1, g);
				}
				//erasing
			}else if(drawit.colorToolbar.getSelectCommand().equals((Object) Color.WHITE)) {
				g.setColor(Color.white);
				g.fillRect(x1, y1, 12, 12);
			}
		}
//		g.fill(new Ellipse2D.Double(m.getX() - 1.0, m.getY() - 1.0, 2.0, 2.0));
		drawOffscreen();
	}

	public void mouseMoved(MouseEvent m) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 *
	 *Description: collecting points while dragging
	 *
	 *@author Lu Chen
	 */
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			currentList = new ArrayList<Point>();
			currentList.add(e.getPoint());
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			currentList.add(e.getPoint());
			currentList = null;
		}
//		System.out.println("released");

	}

	private void smudge() {

	}

	private void sprayPaint(int x, int y, Graphics2D g) {
		for (int i = 0; i < 50; i++) {
			Random rand = new Random();
			Random rand2 = new Random();
			int randomx = rand.nextInt(20);
			int randomy = rand2.nextInt(20);
			g.drawOval(x + randomx, y + randomy, 1, 1);
		}
	}

	private void floodFill(int x, int y, Graphics2D g) {
//		System.out.println(offscreen.getRGB(x, y));
//		base case
		System.out.println("I am painting");
		while(offscreen.getRGB(x, y) == -1) {
			g.drawRect(x, y, 1, 1);
			floodFill(x + 1,y, g);
			floodFill(x - 1,y, g);
			floodFill(x ,y + 1, g);
			floodFill(x ,y - 1, g);
		}
	}

	public void export(File file) {
		try {
			ImageIO.write(offscreen, "png", file);
		} catch (IOException e) {
			System.out.println("problem saving file");
		}
	}
}
