/**
 * Created by luchen on 23/08/2015.
 */

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class ScreenSaverOGL implements GLEventListener {

    /**
     * ScreenSaverOGL - this is a simple screen saver that uses JOGL2
     * Eric McCreath 2009, 2011, 2015
     * <p/>
     * You need to include the jogl jar files (gluegen-rt.jar and jogl.jar). In
     * eclipse use "add external jars" in Project->Properties->Libaries
     * otherwise make certain they are in the class path.  In the current linux
     * computers there files are in the /usr/share/java directory.
     * <p/>
     * If you are executing from the command line then something like:
     * javac -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar ScreenSaverOGL.java
     * java -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar ScreenSaverOGL
     * should work.
     * <p/>
     * On our lab machine you may also need to check you are using Java 7.
     * You can run it directly using:
     * /usr/lib/jvm/java-7-openjdk-amd64/bin/javac
     * and
     * /usr/lib/jvm/java-7-openjdk-amd64/bin/java
     */

    JFrame jf;
    GLCanvas canvas;
    GLProfile profile;
    GLCapabilities caps;
    Dimension dim = new Dimension(800, 600);
    FPSAnimator animator;

    float xpos;
    float xvel;
    float xrot = 5.0f;

    int texture;

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
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluOrtho2D(0.0, dim.getWidth(), 0.0, dim.getHeight());
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
    }


    public void display(GLAutoDrawable dr) {  // clear the screen and draw "Save the Screens"
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl2.glColor3f(1.0f, 0.0f, 0.0f);
        gl2.glRasterPos2f(xpos, 300.0f);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "Save the Screens");
/**
 *
 *Description: bouncing line, filled polygon drawing
 *
 *@author Lu Chen
 */

        gl2.glPointSize(1.0f);
        gl2.glBegin(GL.GL_POINTS);

        int samples = 3000;
        float dx = (200.0f - 0.0f) / (float)samples;
        float dy = (200.0f - 0.0f) / (float)samples;

        for( int i = 0; i < samples; i++ )
        {
            gl2.glVertex2f(xpos + i * dx, i * dy);
        }
        gl2.glEnd();//end drawing of points

        gl2.glBegin(GL2.GL_TRIANGLES);
        {
            //This triangle is drawn clockwise with blended colors
            gl2.glColor3f(0.0f, 1.0f, 0.0f);
            gl2.glVertex2f(xpos + 0.0f, 50.0f);

            gl2.glColor3f(1.0f, 0.0f, 0.0f);
            gl2.glVertex2f(xpos + 100.0f, 250.0f);

            gl2.glColor3f(0.0f, 0.0f, 1.0f);
            gl2.glVertex2f(xpos + 200.f, 50.0f);
        }
        gl2.glEnd();

        gl2.glColor3f(1.0f, 1.0f, 1.0f);
        gl2.glEnable(GL2.GL_TEXTURE_2D);
        try{
            File im = new File("galaxy.png");
            Texture t = TextureIO.newTexture(im, false);
            texture= t.getTextureObject(gl2);
        }catch(IOException e){
            e.printStackTrace();
        }
        gl2.glPushMatrix();
        gl2.glTranslatef(400.0f ,90.0f ,1.0f);
        gl2.glRotatef(xrot, 0.0f, 0.0f, 1.0f);
        gl2.glBegin(GL2.GL_QUADS);
        {
            gl2.glTexCoord2f(0.0f, 0.0f);
            gl2.glVertex2f(xpos + 0.0f, 300.0f);
            gl2.glTexCoord2f(1.0f, 0.0f);
            gl2.glVertex2f(xpos + 100.0f, 300.0f);
            gl2.glTexCoord2f(1.0f, 1.0f);
            gl2.glVertex2f(xpos + 100.0f, 400.0f);
            gl2.glTexCoord2f(0.0f, 1.0f);
            gl2.glVertex2f(xpos + 0.0f, 400.0f);
        };
        gl2.glEnd();
        gl2.glDisable(GL2.GL_TEXTURE_2D);
        gl2.glPopMatrix();



        gl2.glFlush();

        xrot+=0.5f;
        xpos += xvel;
        if (xpos > dim.getWidth())
            xpos = 0.0f;
    }

    public void dispose(GLAutoDrawable glautodrawable) {
    }

    public void reshape(GLAutoDrawable dr, int x, int y, int width,
                        int height) {
    }
}