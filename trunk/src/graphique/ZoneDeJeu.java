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

/**
 * Classe dérivant d'un GLCanvas et implémentant l'interface GLEventListener
 * permettant de créer la représentation graphique de la zone de jeu en utilisant
 * OpenGL via JOGL pour l'affichage.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class ZoneDeJeu extends GLCanvas implements GLEventListener
{
	private static final long serialVersionUID = 2421808737959876690L;
	/** The GL unit (helper class). */
    private GLU glu;
    /** Permet d'effectuer la boucle principale d'affichage */
    private FPSAnimator animator;
    /** Le plateau actuellement affiché */
    private Plateau plateau;
    /** Les deux prochaines pièces */
    private Piece[] piecesSuivantes;
    /** Indique si une mise à jour du plateau est nécessaire */
    private boolean majNecessairePlateau;
    /** Indique si une mise à jour de l'affichage des pièces suivantes est nécessaire */
    private boolean majNecessairePiecesSuivantes;
    /** La display-list correspondant à un puyo */
    private int listePuyo;
    /** La display-list correspondant au plateau dans son ensemble */
    private int listePlateau;
    /** La display-list correspondant à l'affichage des pièces suivantes */
    private int listePiecesSuivantes;
    /** La texture permettant d'afficher l'image de fond */
    private int texture;
	
    /**
     * Crée une nouvelle zone de jeu de taille width sur height pixels utilisant
     * les capacités graphiques fournies.
     * @param capabilities les capacités graphiques à utiliser.
     * @param width largueur de la zone de jeu.
     * @param height hauteur de la zone de jeu.
     */
	public ZoneDeJeu(GLCapabilities capabilities, int width, int height)
	{
		super(capabilities);
		this.setSize(width, height);
		
		this.addGLEventListener(this);
		
		majNecessairePlateau = false;
		majNecessairePiecesSuivantes = false;
	}
	
	/**
	 * Méthode appelée après l'initialisation du contexte OpenGL.
	 * Met en place les différentes display-lists nécessaires,
	 * charge la texture de fond et lance la boucle principale
	 * de l'affichage.
	 */
	public void init(GLAutoDrawable drawable)
	{
		glu = new GLU();
		
		drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        // Display lists
		listePuyo = gl.glGenLists(3); 
		listePlateau = listePuyo + 1;
		listePiecesSuivantes = listePlateau + 1;
		
		gl.glNewList(listePuyo, GL.GL_COMPILE);
			dessinerCercle(gl, 0, 0, 15);
		gl.glEndList();
		
		// Chargement de la texture de fond.
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
        
        // Boucle principale d'affichage
        animator = new FPSAnimator(this, 60);
        animator.start();
	}
	
	/**
	 * Méthode appelée après que la zone d'affichage ait été redimensionnée,
	 * maintient un affichage cohérent après le redimensionnement.
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		final GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, width, 0.0, height); 
	}
	
	/**
	 * Méthode appelée par la boucle d'affichage.
	 */
	public void display(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		// Fond
		gl.glBegin(GL.GL_QUADS);
			gl.glColor3f(1, 1, 1);
	        gl.glTexCoord2i(0, 0);
	        gl.glVertex2i(0, 0);
	        gl.glTexCoord2i(1, 0);
	        gl.glVertex2i(800, 0);
	        gl.glTexCoord2i(1, 1);
	        gl.glVertex2i(800, 600);
	        gl.glTexCoord2i(0, 1);
	        gl.glVertex2i(0, 600);
	    gl.glEnd();
		
		if (majNecessairePlateau) // Si la mise à jour est nécessaire
			faireListePlateau(gl); // on reconstruit la display-list du plateau
		if (majNecessairePiecesSuivantes) // Si la mise à jour est nécessaire
			faireListePiecesSuivantes(gl); // on reconstruit la display-list de l'affichage des pièces suivantes
		
		// On affiche les pièces suivantes et le plateau
		gl.glCallList(listePiecesSuivantes);
		gl.glCallList(listePlateau);
		
		gl.glFlush();
	}
	
	/**
	 * Non utilisée.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{

	}
	
	/**
	 * Dessine un cercle de centre de coordonnées (cx, cy) et de rayon rayon.
	 * @param gl le contexte OpenGL.
	 * @param cx coordonnée du centre sur l'axe des x.
	 * @param cy coordonnée du centre sur l'axe des y.
	 * @param rayon rayon du cercle.
	 */
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
	
	/**
	 * Charge un plateau et indique que l'affichage du plateau
	 * nécessite une mise à jour.
	 * @param plateau le plateau à charger.
	 */
	public void chargerPlateau(Plateau plateau)
	{
		this.plateau = plateau;
		majNecessairePlateau = true;
	}
	
	/**
	 * Crée une display-list correspondant au plateau actuellement stocké
	 * et indique que l'affichage du plateau n'a plus besoin d'être redessiné.
	 * @param gl le contexte OpenGL.
	 */
	private void faireListePlateau(GL gl)
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
	
	/**
	 * Charge les deux prochaines pièces et indique que l'affichage des pièces
	 * suivantes nécessite une mise à jour.
	 * @param piecesSuivantes le tableau contenant les pièces suivantes.
	 */
	public void chargerPiecesSuivantes(Piece[] piecesSuivantes)
	{
		this.piecesSuivantes = piecesSuivantes;
		majNecessairePiecesSuivantes = true;
	}
	
	/**
	 * Crée une display-list correspondant aux pièces suivantes actuellement
	 * stockées et indique que l'affichage des pièces suivantes n'a plus 
	 * besoin d'être redessiné.
	 * @param gl le contexte OpenGL.
	 */
	private void faireListePiecesSuivantes(GL gl)
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
	
	private void makeRGBTexture(GL gl, GLU glu, TextureReader.Texture img, int target, boolean mipmapped)
	{
        if (mipmapped)
            glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(), 
                    			  img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        else
            gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), 
                    		img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
    }

    private int genTexture(GL gl)
    {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }
}
