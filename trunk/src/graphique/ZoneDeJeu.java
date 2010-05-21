package graphique;

import java.awt.Color;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import moteur.Plateau;
import moteur.Puyo;

import com.sun.opengl.util.FPSAnimator;

public class ZoneDeJeu extends GLCanvas implements GLEventListener
{
	private static final long serialVersionUID = 2421808737959876690L;
	/** The GL unit (helper class). */
    private GLU glu;
    private FPSAnimator animator;
    private Plateau plateau;
    private boolean majNecessaire;
    private int listePuyo, listePlateau;
	
	public ZoneDeJeu(GLCapabilities capabilities, int width, int height)
	{
		super(capabilities);
		this.setSize(width, height);
		
		this.addGLEventListener(this);
		
		majNecessaire = false;
	}
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		glu = new GLU();
		
		drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
		listePuyo = gl.glGenLists(2); 
		listePlateau = listePuyo + 1;
		
		gl.glNewList(listePuyo, GL.GL_COMPILE);
			dessinerCercle(gl, 0, 0, 15);
		gl.glEndList();
		
        animator = new FPSAnimator(this, 60);
        animator.start();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		final GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, width, 0.0, height); 
	}
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		if (majNecessaire)
			faireListePlateau(gl);
		
		gl.glCallList(listePlateau);
		
		gl.glFlush();
	}
	
	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		// TODO Auto-generated method stub

	}
	
	public void dessinerCercle(GL gl, int cx, int cy, double rayon)
	{
		double x, y;
		
		gl.glBegin(GL.GL_POLYGON);
			for (double theta = 0; theta < 2*Math.PI; theta += Math.PI/100)
			{
				x = (cx + (Math.sin(theta) * rayon));
				y = (cy + (Math.cos(theta) * rayon));
			    gl.glVertex2d(x,y);
			}
		gl.glEnd();
	}
	
	public void chargerPlateau(Plateau plateau)
	{
		this.plateau = plateau;
		majNecessaire = true;
	}
	
	public void faireListePlateau(GL gl)
	{
		majNecessaire = false;
		Color couleur;
		int cx, cy = 400;
		
		gl.glNewList(listePlateau, GL.GL_COMPILE);
			for (int i=3; i<Plateau.HAUTEUR; i++)
			{
				cx = 20;
				
				for (int j=0; j<Plateau.LARGEUR; j++)
				{
					if (!plateau.estLibre(i, j))
					{
						couleur = plateau.getCouleurPuyo(i, j);
						
						gl.glPushMatrix();
							gl.glColor3f(couleur.getRed()/255, couleur.getGreen()/255, couleur.getBlue()/255);
							gl.glTranslated(cx, cy, 0);
							gl.glCallList(listePuyo);
						
							if(plateau.getPuyo(i, j).getLien(Puyo.HAUT))
							{
								gl.glPushMatrix();
									gl.glTranslated(0, 35/2, 0);
									gl.glScaled(0.5, 1, 1);
									gl.glCallList(listePuyo);
								gl.glPopMatrix();
							}
							if(plateau.getPuyo(i, j).getLien(Puyo.DROITE))
							{
								gl.glPushMatrix();
									gl.glTranslated(35/2, 0, 0);
									gl.glScaled(1, 0.6, 1);
									gl.glCallList(listePuyo);
								gl.glPopMatrix();
							}
						gl.glPopMatrix();
					}
					
					cx += 35;
				}
				
				cy -= 35;
			}
		gl.glEndList();
	}
}
