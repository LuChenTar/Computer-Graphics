import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


public class ScreenSaverOGL implements GLEventListener {

	/**
	 * ScreenSaverOGL - this is a simple screen saver that uses JOGL2 
	 * Eric McCreath 2009, 2011, 2015
	 * 
	 * You need to include the jogl jar files (gluegen-rt.jar and jogl.jar). In
	 * eclipse use "add external jars" in Project->Properties->Libaries
	 * otherwise make certain they are in the class path.  In the current linux 
         * computers there files are in the /usr/share/java directory.
	 * 
         * If you are executing from the command line then something like:
         *   javac -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar ScreenSaverOGL.java
         *   java -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar ScreenSaverOGL
         * should work.
         *
         * On our lab machine you may also need to check you are using Java 7.  
         * You can run it directly using:
         *    /usr/lib/jvm/java-7-openjdk-amd64/bin/javac
         * and 
         *    /usr/lib/jvm/java-7-openjdk-amd64/bin/java
	 * 
	 */

	JFrame jf;
	GLCanvas canvas;
        GLProfile profile;
	GLCapabilities caps;
	Dimension dim = new Dimension(800, 600);
	FPSAnimator animator;

	float xpos;
	float xvel;

	public ScreenSaverOGL() {
		jf = new JFrame();
                profile = GLProfile.getDefault();
		caps = new GLCapabilities(profile);
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);
		canvas.requestFocusInWindow();
		jf.getContentPane().add(canvas);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.setPreferredSize(dim);
		jf.pack();
		animator = new FPSAnimator(canvas, 20);
		xpos = 100.0f;
		xvel = 1.0f;
		animator.start();
	}

	public static void main(String[] args) {
		new ScreenSaverOGL();
	}

       
           
        public void init(GLAutoDrawable dr) {  // set up openGL for 2D drawing
			GL2 gl2 = dr.getGL().getGL2();
			GLU glu = new GLU();
			GLUT glut = new GLUT();
			gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl2.glEnable(GL2.GL_DEPTH_TEST);
			gl2.glMatrixMode(GL2.GL_PROJECTION);
			gl2.glLoadIdentity();
			glu.gluPerspective(60.0, 1.0, 100.0, 1000.0);
			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();
			glu.gluLookAt(100.0, 100.0, 500.0, 100.0, 100.0, 25.0, 0.0, 1.0, 0.0);
		}


	public void display(GLAutoDrawable dr) {  // clear the screen and draw "Save the Screens"
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl2.glColor3f(1.0f, 0.0f, 0.0f);
		gl2.glRasterPos3f(xpos, 300.0f , 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "Save the Screens");

		gl2.glPushMatrix();
		gl2.glRotated(xpos, 0.0, 1.0, 0.0);

		//first polygon
		gl2.glColor3f(1.0f, 0.0f, 0.0f);
		Polygoncreator(gl2);

		//second polygon
		gl2.glTranslated(20.0, 20.0, 20.0);
		gl2.glColor3f(0.0f, 0.0f, 1.0f);
		Polygoncreator(gl2);

		//third polygon
		gl2.glTranslated(40.0, 40.0, 40.0);
		gl2.glColor3f(0.0f, 1.0f, 0.0f);
		Polygoncreator(gl2);

		//forth polygon
		gl2.glTranslated(60.0, 60.0, 60.0);
		gl2.glColor3f(1.0f, 1.0f, 1.0f);
		Polygoncreator(gl2);
		gl2.glPopMatrix();

		//vegemite matrix
		gl2.glPushMatrix();
		gl2.glRotated(xpos, 0.0, -20.0, 0.0);

		//first vegemite
		gl2.glTranslated(-300.0, 0.0, 0.0);
		vegemiteCreator(gl2);

		//second vegemite
		gl2.glTranslated(-100.0, 20.0, 0.0);
		vegemiteCreator(gl2);

		//third vegemite
		gl2.glTranslated(-100.0, 40.0, 0.0);
		vegemiteCreator(gl2);

		//forth vegemite
		gl2.glTranslated(-100.0, 40.0, 0.0);
		vegemiteCreator(gl2);
		gl2.glPopMatrix();

		gl2.glFlush();

		xpos += xvel;
		if (xpos > dim.getWidth())
			xpos = 0.0f;
	}

	private void Polygoncreator(GL2 gl2) {
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(0.0, 0.0, 0.0);
		gl2.glVertex3d(100.0, 0.0,0.0);
		gl2.glVertex3d(100.0, 50.0,0.0);
		gl2.glVertex3d(0.0, 50.0,0.0);
		gl2.glEnd();
	}

	private void vegemiteCreator(GL2 gl2) {
		gl2.glColor3f(1.0f, 1.0f, 0.0f);
		//vegemite container start
		//lid
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(140.0, 0.0, 0.0);
		gl2.glVertex3d(220.0, 0.0, 0.0);
		gl2.glVertex3d(220.0, 20.0, 0.0);
		gl2.glVertex3d(140.0, 20.0, 0.0);
		gl2.glEnd();

		//body
		gl2.glColor3f(0.627f, 0.314f, 0.067f);

		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(150.0, -10.0, 0.0);
		gl2.glVertex3d(145.0, -20.0, 0.0);
		gl2.glVertex3d(215.0, -20.0, 0.0);
		gl2.glVertex3d(210.0, -10.0 ,0.0);
		gl2.glVertex3d(210.0, 0.0, 0.0);
		gl2.glVertex3d(150.0, 0.0, 0.0);
		gl2.glEnd();

		//body image
		gl2.glColor3f(1.0f, 1.0f, 1.0f);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		try{
			File im = new File("vegemite.png");
			Texture t = TextureIO.newTexture(im, false);
		}catch(IOException e){
			e.printStackTrace();
		}
		gl2.glBegin(GL2.GL_QUADS);
		{
			gl2.glTexCoord3f(0.0f, 0.0f, 0.0f);
			gl2.glVertex3d(145.0, -20.0, 0.0);
			gl2.glTexCoord3f(1.0f, 0.0f, 0.0f);
			gl2.glVertex3d(145.0, -70, 0.0);
			gl2.glTexCoord3f(1.0f, 1.0f, 0.0f);
			gl2.glVertex3d(215.0, -70.0, 0.0);
			gl2.glTexCoord3f(0.0f, 1.0f, 0.0f);
			gl2.glVertex3d(215.0, -20.0, 0.0);
		};
		gl2.glEnd();
		gl2.glDisable(GL2.GL_TEXTURE_2D);

		//bottom
		gl2.glColor3f(0.627f, 0.314f, 0.067f);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(145.0, -70.0, 0.0);
		gl2.glVertex3d(150.0, -90.0, 0.0);
		gl2.glVertex3d(210.0, -90.0 ,0.0);
		gl2.glVertex3d(215.0, -70.0, 0.0);
		gl2.glEnd();
	}

        public void dispose( GLAutoDrawable glautodrawable ) {
        }
	
	public void reshape(GLAutoDrawable dr, int x, int y, int width,
			int height) {
	}
}
