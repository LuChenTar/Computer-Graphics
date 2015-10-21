import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.nio.IntBuffer;

public class RubixCube implements GLEventListener, MouseMotionListener {

    /**
     * ShaderLighting - this is a simple example of drawing a teapot using a shader to
     * do Phong shading.
     * Eric McCreath 2009, 2011, 2015
     * <p/>
     * You need to include the jogl jar files (gluegen-rt.jar and jogl.jar). In
     * eclipse use "add external jars" in Project->Properties->Libaries
     * otherwise make certain they are in the class path. In the current linux
     * computers there files are in the /usr/share/java directory.
     * <p/>
     * If you are executing from the command line then something like: javac -cp
     * .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar
     * Shadow.java java -cp
     * .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-2.2.4-rt.jar
     * Shadow should work.
     * <p/>
     * On our lab machine you may also need to check you are using Java 7. You
     * can run it directly using: /usr/lib/jvm/java-7-openjdk-amd64/bin/javac
     * and /usr/lib/jvm/java-7-openjdk-amd64/bin/java
     */

    JFrame jf;
    GLCanvas canvas;
    GLProfile profile;
    GLCapabilities caps;
    Dimension dim = new Dimension(800, 600);
    FPSAnimator animator;

    int shaderprogram, vertexshader, fragshader;

    float ycamrot = 0.0f;
    float lightdis = 1.0f;
    float time; // in seconds
    float cycletime = 10.0f;
    static int framerate = 60;
    float lightpos[] = {50.0f, 100.0f, 200.0f, 1.0f};


    public RubixCube() {
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
        animator = new FPSAnimator(canvas, framerate);
        canvas.addMouseMotionListener(this);
        time = 0.0f;
        animator.start();
    }

    public static void main(String[] args) {
        new RubixCube();
    }

    // based on shaders from https://www.opengl.org/sdk/docs/tutorials/ClockworkCoders/lighting.php
    static final String vertstr[] = {
            "varying vec3 N;\n" +
                    "varying vec3 v;\n" +
                    "void main(void)  \n" +
                    "	{     \n" +
                    "	   v = vec3(gl_ModelViewMatrix * gl_Vertex);    \n" +
                    "	   N = normalize(gl_NormalMatrix * gl_Normal);\n" +
                    "	   gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;  \n" +
                    "	}\n"};

    static int vlens[] = new int[1];
    static int flens[] = new int[1];

    static final String fragstr[] = {
            "varying vec3 N;\n" +
                    "varying vec3 v; \n" +
                    "void main (void)  \n" +
                    "{  \n" +
                    "   vec3 L = normalize(gl_LightSource[0].position.xyz - v);   \n" +
                    "   vec3 E = normalize(-v); \n" +  // as we are in view co-ordinates the viewer is at (0,0,0)
                    "   vec3 R = normalize(-reflect(L,N));  \n" +
//		"   vec3 H = normalize(L+E);  \n" +
                    "   vec4 Iamb = gl_FrontLightProduct[0].ambient;    \n" +
                    "   vec4 Idiff = gl_FrontLightProduct[0].diffuse * max(dot(N,L), 0.0);\n" +
                    "   Idiff = clamp(Idiff, 0.0, 1.0);     \n" +
                    "   vec4 Ispec = gl_FrontLightProduct[0].specular \n" +
                    "                * pow(max(dot(R,E),0.0),gl_FrontMaterial.shininess);\n" +
//		"                * pow(max(dot(H,N),0.0),0.3*gl_FrontMaterial.shininess);\n" +
                    "   Ispec = clamp(Ispec, 0.0, 1.0); \n" +
                    "   gl_FragColor = Iamb + Idiff + Ispec;    \n" +
                    "}\n"};

    // gl_FrontLightModelProduct.sceneColor +

    public void init(GLAutoDrawable dr) { // set up openGL for 2D drawing
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        // setup and load the vertex and fragment shader programs
        gl2.glEnable(GL2.GL_DEPTH_TEST);

        shaderprogram = gl2.glCreateProgram();

        vertexshader = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
        vlens[0] = vertstr[0].length();
        gl2.glShaderSource(vertexshader, 1, vertstr, vlens, 0);
        gl2.glCompileShader(vertexshader);
        checkok(gl2, vertexshader, GL2.GL_COMPILE_STATUS);
        gl2.glAttachShader(shaderprogram, vertexshader);

        fragshader = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        flens[0] = fragstr[0].length();
        gl2.glShaderSource(fragshader, 1, fragstr, flens, 0);
        gl2.glCompileShader(fragshader);
        checkok(gl2, fragshader, GL2.GL_COMPILE_STATUS);
        gl2.glAttachShader(shaderprogram, fragshader);

        gl2.glLinkProgram(shaderprogram);

        checkok(gl2, shaderprogram, GL2.GL_LINK_STATUS);

        gl2.glValidateProgram(shaderprogram);
        checkok(gl2, shaderprogram, GL2.GL_VALIDATE_STATUS);

        gl2.glUseProgram(shaderprogram);

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glShadeModel(GL2.GL_SMOOTH);

        gl2.glEnable(GL2.GL_DEPTH_TEST);

        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(80.0, 1.0, 50.0, 3000.0);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        glu.gluLookAt(0.0, 80.0, 500.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);


    }

    private void checkok(GL2 gl2, int program, int type) {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl2.glGetProgramiv(program, type, intBuffer);
        if (intBuffer.get(0) != GL.GL_TRUE) {
            int[] len = new int[1];
            gl2.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, len, 0);
            if (len[0] != 0) {

                byte[] errormessage = new byte[len[0]];
                gl2.glGetProgramInfoLog(program, len[0], len, 0, errormessage,
                        0);
                System.err.println("problem\n" + new String(errormessage));
                canvas.destroy();
                jf.dispose();
                System.exit(0);
            }
        }
    }

    public void display(GLAutoDrawable dr) { // clear the screen and draw
        // "Save the Screens"
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);


        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glPushMatrix();
        gl2.glRotated(ycamrot, 1.0, 0.0, 0.0);


        // set up light 1

        //gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, 0.0f);
        //gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, 0.0f);
        //gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, 0.001f);
        float ac[] = {0.2f, 0.2f, 0.2f, 1.0f};
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ac, 0);
        gl2.glEnable(GL2.GL_LIGHT0);
        float dc[] = {3.0f, 3.0f, 3.0f, 1.0f};
        float sc[] = {3.0f, 3.0f, 3.0f, 1.0f};
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightpos, 0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, dc, 0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sc, 0);


        gl2.glPushMatrix();   // draw the spinning cube with a particular material
        gl2.glRotated(time * 20.0f, 0.1, 1.0, 0.0);
        float df[] = {0.0f, 0.2f, 1.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, df, 0);
        float sf[] = {1.0f, 1.0f, 1.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, sf, 0);
        gl2.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 120.0f);

        // cube drawing
        OneSlice(glut, gl2);
        gl2.glTranslated(-55, -55, 55);

        float df2[] = {1.0f, 0.0f, 0.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, df2, 0);
        OneSlice(glut, gl2);

        float df3[] = {0.0f, 1.0f, 0.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, df3, 0);
        gl2.glTranslated(-55, -55, 55);
        OneSlice(glut, gl2);

        gl2.glPopMatrix();
        //end of cube drawing


        gl2.glPushMatrix();
        float dfs[] = {1.0f, 1.0f, 0.0f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, dfs, 0);
        gl2.glTranslated(lightpos[0], lightpos[1], lightpos[2]);
        gl2.glPopMatrix();

        gl2.glDisable(GL2.GL_LIGHTING);
        gl2.glPopMatrix();

        gl2.glFlush();

        time += 1.0f / framerate;
//        if (time > cycletime) time = 0.0f;
    }

    private void OneSlice(GLUT glut, GL2 gl2) {
        glut.glutSolidCube(50);

        for (int i=0; i < 2; i++) {
            gl2.glTranslated(55, 0, 0);
            glut.glutSolidCube(50);
        }
        for (int i=0; i < 2; i++) {
            gl2.glTranslated(0, 55, 0);
            glut.glutSolidCube(50);
        }
        for (int i=0; i < 2; i++) {
            gl2.glTranslated(-55, 0, 0);
            glut.glutSolidCube(50);
        }
        gl2.glTranslated(0, -55, 0);
        glut.glutSolidCube(50);

        gl2.glTranslated(55, 0, 0);
        glut.glutSolidCube(50);
    }

    public void dispose(GLAutoDrawable glautodrawable) {
    }

    public void reshape(GLAutoDrawable dr, int x, int y, int width, int height) {
    }

    Float ycamrotLast, lightdisLast;

    public void mouseDragged(MouseEvent me) {
        if (ycamrotLast != null)
            ycamrot += ((((float) me.getY()) / canvas.getHeight()) - ycamrotLast) * 360.0f;
        ycamrotLast = (((float) me.getY()) / canvas.getHeight());


        if (lightdisLast != null)
            lightdis += ((((float) me.getX()) / canvas.getWidth()) - lightdisLast) * 10.0f;
        lightdisLast = (((float) me.getX()) / canvas.getWidth());


        lightpos[0] = lightdis * 50.0f;
        lightpos[1] = lightdis * 100.0f;
        lightpos[2] = lightdis * 200.0f;

        canvas.display();
    }

    public void mouseMoved(MouseEvent arg0) {
        ycamrotLast = null;
        lightdisLast = null;
    }

}
