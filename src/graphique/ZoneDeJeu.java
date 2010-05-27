package graphique;

import graphique.texture.TextureReader;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.Map;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;


import moteur.Piece;
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
    private Piece[] piecesSuivantes;
    private boolean majNecessairePlateau, majNecessairePiecesSuivantes;
    private int listePuyo, listePlateau, listePiecesSuivantes;
    private int texture;
	
	public ZoneDeJeu(GLCapabilities capabilities, int width, int height)
	{
		super(capabilities);
		this.setSize(width, height);
		
		this.addGLEventListener(this);
		
		majNecessairePlateau = false;
		majNecessairePiecesSuivantes = false;
	}
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		glu = new GLU();
		
		drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
		listePuyo = gl.glGenLists(3); 
		listePlateau = listePuyo + 1;
		listePiecesSuivantes = listePlateau + 1;
		
		gl.glNewList(listePuyo, GL.GL_COMPILE);
			dessinerCercle(gl, 0, 0, 15);
		gl.glEndList();
		
        animator = new FPSAnimator(this, 60);
        animator.start();
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        texture = genTexture(gl);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
        TextureReader.Texture texture = null;
        try {
            texture = TextureReader.readTexture("resources/fond.png");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        makeRGBTexture(gl, glu, texture, GL.GL_TEXTURE_2D, false);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
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
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		// Fond
		gl.glBegin(GL.GL_QUADS);
			gl.glColor3f(1, 1, 1);
	        gl.glTexCoord2f(0.0f, 0.0f);
	        gl.glVertex3f(0.0f, 0.0f, -1.0f);
	        gl.glTexCoord2f(1.0f, 0.0f);
	        gl.glVertex3f(800.0f, 0.0f, -1.0f);
	        gl.glTexCoord2f(1.0f, 1.0f);
	        gl.glVertex3f(800.0f, 600.0f, -1.0f);
	        gl.glTexCoord2f(0.0f, 1.0f);
	        gl.glVertex3f(0.0f, 600.0f, -1.0f);
	    gl.glEnd();
		
		if (majNecessairePlateau)
			faireListePlateau(gl);
		if (majNecessairePiecesSuivantes)
			faireListePiecesSuivantes(gl);
		
		gl.glCallList(listePiecesSuivantes);
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
		majNecessairePlateau = true;
	}
	
	public void faireListePlateau(GL gl)
	{
		majNecessairePlateau = false;
		Color couleur;
		int cx, cy = 400;
		
		gl.glNewList(listePlateau, GL.GL_COMPILE);
			gl.glPushMatrix();
				gl.glTranslatef(292, 3, 0);
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
			gl.glPopMatrix();
		gl.glEndList();
	}
	
	public void chargerPiecesSuivantes(Piece[] piecesSuivantes)
	{
		this.piecesSuivantes = piecesSuivantes;
		majNecessairePiecesSuivantes = true;
	}
	
	public void faireListePiecesSuivantes(GL gl)
	{
		majNecessairePiecesSuivantes = false;
		Color couleur;
		Point position;
		//int cx, cy = 400;
		
		gl.glNewList(listePiecesSuivantes, GL.GL_COMPILE);
			gl.glPushMatrix();
				gl.glTranslated(592, 467, 0);
				
				for (Piece piece : piecesSuivantes)
				{
					gl.glPushMatrix();
						if (piece.getForme() != Piece.COUDE)
							gl.glTranslated(18, 0, 0);
						for (Map.Entry<Puyo, Point> paire : piece.entrySet())
						{
							couleur = paire.getKey().getCouleur();
							position = paire.getValue();
							
							gl.glPushMatrix();
								gl.glColor3f(couleur.getRed()/255, couleur.getGreen()/255, couleur.getBlue()/255);
								gl.glTranslated((position.y - 2) * 35, (2 - position.x) * 35, 0);
								gl.glCallList(listePuyo);
							gl.glPopMatrix();
						}
					gl.glPopMatrix();
					gl.glTranslated(100, 0, 0);
				}
			gl.glPopMatrix();
		gl.glEndList();
			
	}
	
	private void makeRGBTexture(GL gl, GLU glu, TextureReader.Texture img, 
            int target, boolean mipmapped) {
        
        if (mipmapped) {
            glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(), 
                    img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        } else {
            gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), 
                    img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        }
    }

    private int genTexture(GL gl) {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }
}
