import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


/*
 * DrawIt - 
 * Eric McCreath 2009
 */

public class DrawIt  implements Runnable {

	static final Dimension dim = new Dimension(800,600);
	
	JFrame jf;
	DrawArea da;
	JMenuBar bar;
	JMenu jmfile;
	JMenu jmFunction;
	JMenuItem jmiquit, jmiexport;
	JMenuItem jmSmudge, jmSparyPaint, jmFloodFill;
	ToolBar colorToolbar;
	ToolBar thicknessToolbar;
	ToolBar OpacityToolbar;

	public DrawIt() {
		SwingUtilities.invokeLater(this);
	}
	
	public void run() {
		jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		da = new DrawArea(dim,this);
		//da.setFocusable(true);
		jf.getContentPane().add(da, BorderLayout.LINE_START);

		// create a toolbar for color
		colorToolbar = new ToolBar(BoxLayout.Y_AXIS);
		colorToolbar.addbutton("Red", Color.RED);
		colorToolbar.addbutton("Blue", Color.BLUE);
		colorToolbar.addbutton("Green", Color.GREEN);
		colorToolbar.addbutton("Erase", Color.WHITE);
		jf.getContentPane().add(colorToolbar, BorderLayout.CENTER);

		// create a toolbar for thickness
		thicknessToolbar = new ToolBar(BoxLayout.Y_AXIS);
		thicknessToolbar.addbutton("Heavy", 10);
		thicknessToolbar.addbutton("Intermediate", 6);
		thicknessToolbar.addbutton("Light", 2);
		jf.getContentPane().add(thicknessToolbar, BorderLayout.LINE_END);

		// create a toolbar for opacity
		OpacityToolbar = new ToolBar(BoxLayout.Y_AXIS);
		OpacityToolbar.addbutton("Opaque", 1.0f);
		OpacityToolbar.addbutton("Semi-transparent", 0.1f);
		OpacityToolbar.addbutton("Transparent", 0.0f);
		jf.getContentPane().add(OpacityToolbar, BorderLayout.AFTER_LAST_LINE);

		// create some menus
		bar = new JMenuBar();
		jmfile = new JMenu("File");
		jmiexport = new JMenuItem("Export");
		jmfile.add(jmiexport);
		jmiexport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.export(new File("export.png"));
			}
		});

		jmiquit = new JMenuItem("Quit");
		jmfile.add(jmiquit);
		jmiquit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		bar.add(jmfile);
		jf.setJMenuBar(bar);

		jmFunction = new JMenu("Function");
		jmSmudge = new JMenuItem("Smudge");
		jmFunction.add(jmSmudge);
		jmSparyPaint = new JMenuItem("Spray Paint");
		jmFunction.add(jmSparyPaint);
		jmFloodFill = new JMenuItem("Area Flood Fill");
		jmFunction.add(jmFloodFill);
		bar.add(jmFunction);

		jf.pack();
		jf.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		DrawIt sc = new DrawIt();
	}
}
