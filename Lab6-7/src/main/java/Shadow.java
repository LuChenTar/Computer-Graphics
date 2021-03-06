import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Shadow implements GLEventListener, MouseMotionListener, MouseListener {

    /**
     * Shadow - this is a simple screen saver that uses JOGL2
     * Eric McCreath 2009, 2011, 2015
     * <p/>
     * You need to include the jogl jar files (gluegen-rt.jar and jogl.jar). In
     * eclipse use "add external jars" in Project->Properties->Libaries
     * otherwise make certain they are in the class path.  In the current linux
     * computers there files are in the /usr/share/java directory.
     * <p/>
     * If you are executing from the command line then something like:
     * javac -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar Shadow.java
     * java -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar Shadow
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
//    float time; // in seconds
    float angle;
    float xcamrot = 0.0f;
    Timer t1, t2;
    float distance, start, end;
    int starttime, endtime;
    boolean isSpinning = false;


    double angleAccel = 0, angleVelocity = 0, x = 0, dt = 0.1;

    float lightpos[] = {10.9f, 19.9f, 0.0f, 1.0f};


    public Shadow() {
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
        angle = 0.0f;
        animator = new FPSAnimator(canvas, 20);
        canvas.addMouseListener(this);
        animator.start();
    }

    public static void main(String[] args) {
        new Shadow();
    }


    public void init(GLAutoDrawable dr) {  // set up openGL for 2D drawing
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();
        gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl2.glEnable(GL2.GL_DEPTH_TEST);


        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(60.0, 1.0, 1.0, 50.0);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        glu.gluLookAt(5.0, 12.0, 6.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        gl2.glEnable(GL2.GL_LIGHTING);

        float ac[] = {0.2f, 0.2f, 0.2f, 1.0f};
        float dc[] = {1.0f, 1.0f, 1.0f, 1.0f};
        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ac, 0);

        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, dc, 0);

        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightpos, 0);
        gl2.glEnable(GL2.GL_LIGHT1);

    }

    public void dispose(GLAutoDrawable drawable) {

    }

    private static int index(int j, int i) {
        return j + 4 * i;
    }

    // multiply the current matrix with a projection matrix that will do the
    // shadow
    static void projectShadow(GL2 gl, float s[], float n[], float l[]) {
        float w, m;
        float mat[] = new float[4 * 4];

        w = (s[0] - l[0]) * n[0] + (s[1] - l[1]) * n[1] + (s[2] - l[2]) * n[2];
        m = l[0] * n[0] + l[1] * n[1] + l[2] * n[2];

        mat[index(0, 0)] = w + n[0] * l[0];
        mat[index(0, 1)] = n[1] * l[0];
        mat[index(0, 2)] = n[2] * l[0];
        mat[index(0, 3)] = -(w + m) * l[0];

        mat[index(1, 0)] = n[0] * l[1];
        mat[index(1, 1)] = w + n[1] * l[1];
        mat[index(1, 2)] = n[2] * l[1];
        mat[index(1, 3)] = -(w + m) * l[1];

        mat[index(2, 0)] = n[0] * l[2];
        mat[index(2, 1)] = n[1] * l[2];
        mat[index(2, 2)] = w + n[2] * l[2];
        mat[index(2, 3)] = -(w + m) * l[2];

        mat[index(3, 0)] = n[0];
        mat[index(3, 1)] = n[1];
        mat[index(3, 2)] = n[2];
        mat[index(3, 3)] = -m;

        gl.glMultMatrixf(mat, 0);

    }

    private void drawDoll (GL2 gl2, GLUT glut, float angle) {
        gl2.glPushMatrix();
        gl2.glRotated(angle, 0.0, 1.0, 0.0);

        gl2.glRotatef(-90, 1.0f, 0.0f, 0.0f);
        gl2.glTranslated(0.0, 0.0, 4.0);
        glut.glutSolidCone(1.0, 1.5, 6, 3);

        gl2.glPushMatrix();
        gl2.glTranslated(0.0, 0.0, 1.8);
        glut.glutSolidTorus(0.3, 0.5, 20, 20);
        gl2.glPopMatrix();

        gl2.glPopMatrix();
    }

    public void display(GLAutoDrawable dr) {  // clear the screen and draw "Save the Screens"
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        //draw simplified doll
        gl2.glEnable(GL2.GL_LIGHTING);

        gl2.glPushMatrix();
        gl2.glRotated(xcamrot, 0.0, 1.0, 0.0);

        gl2.glPushMatrix();
        float df[] = {0.3f, 0.3f, 1.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, df,
                0);
        drawDoll(gl2, glut, angle);
        // draw the shadow  -- the flattened doll
        gl2.glDisable(GL2.GL_LIGHTING);
        gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glPolygonOffset(-1.0f, -1.0f);

        float ground[] = {0.0f, -3.0f, 0.0f};
        float groundnormal[] = {0.0f, -1.0f, 0.0f};// normal unit vector to the ground plane

        gl2.glPushMatrix();
        gl2.glColor3d(0.0, 0.0, 0.0);
        projectShadow(gl2, ground, groundnormal, lightpos);
        //draw the doll normally
        drawDoll(gl2, glut, angle);
        gl2.glPopMatrix();
        gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        gl2.glPopMatrix();

        // draw the floor
        gl2.glEnable(GL2.GL_LIGHTING);
        float dff[] = {0.34f, 0.32f, 0.42f, 1.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,
                dff, 0);
        gl2.glPushMatrix();
        gl2.glTranslated(-1.0, 1.0, 0.0);
        gl2.glBegin(GL2.GL_POLYGON);
        gl2.glVertex3d(-8.0, -4.0, -8.0);
        gl2.glVertex3d(-8.0, -4.0, 8.0);
        gl2.glVertex3d(8.0, -4.0, 8.0);
        gl2.glVertex3d(8.0, -4.0, -8.0);
        gl2.glEnd();
        gl2.glPopMatrix();

        gl2.glFlush();

        // update the angle
        angle += 0.5f;
        if (angle > 360.0f)
            angle -= 360.0f;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }


    Float xcamrotLast;

    public void mouseDragged(MouseEvent me) {
        if (xcamrotLast != null)
            xcamrot += ((((float) me.getY()) / canvas.getHeight()) - xcamrotLast) * 360.0f;
        xcamrotLast = (((float) me.getY()) / canvas.getHeight());

        canvas.display();
        System.out.println("I am rotating");
    }

    public void mouseMoved(MouseEvent arg0) {
        xcamrotLast = null;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        start = (float) me.getX();
        starttime = (int) System.currentTimeMillis();
    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {
        end = (float) me.getX();
        endtime = (int) System.currentTimeMillis();

        System.out.println("speed" + Math.abs(end - start)/ Math.abs(endtime-starttime));
        System.out.println("time" + Math.abs(endtime-starttime) * Math.pow(10.0, -6.0));

        if (Math.abs(end - start)/ Math.abs(endtime-starttime) > 4E-7){
            isSpinning = true;
        }

        if (isSpinning) {

            double d = Math.abs(endtime-starttime) * Math.pow(10.0, -6.0);
            int duration = (int) d;

            t1 = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    angle += 30.0f;
                    System.out.println("delay");
                }
            });
            t1.start();

            Timer timer = new Timer(duration, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    t1.stop();
                    System.out.println("stop");
                    isSpinning = false;
                }
            });
            timer.start();
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }
}