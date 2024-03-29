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
 * Classe d�rivant d'un GLCanvas et impl�mentant l'interface GLEventListener
 * permettant de cr�er la repr�sentation graphique de la zone de jeu en utilisant
 * OpenGL via JOGL pour l'affichage.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class ZoneDeJeu extends GLCanvas implements GLEventListener
{
	private static final long serialVersionUID = 2421808737959876690L;
	/** The GL unit (helper class). */
    private GLU glu;
    /** Permet d'effectuer la boucle principale d'affichage */
    private FPSAnimator animator;
    /** Le plateau actuellement affich� */
    private Plateau plateau;
    /** Les deux prochaines pi�ces */
    private Piece[] piecesSuivantes;
    /** Indique si une mise � jour du plateau est n�cessaire */
    private boolean majNecessairePlateau;
    /** Indique si une mise � jour de l'affichage des pi�ces suivantes est n�cessaire */
    private boolean majNecessairePiecesSuivantes;
    /** La display-list correspondant � un puyo */
    private int listePuyo;
    /** La display-list correspondant au plateau dans son ensemble */
    private int listePlateau;
    /** La display-list correspondant � l'affichage des pi�ces suivantes */
    private int listePiecesSuivantes;
    /** Le tableau contenant les display-lists correspondant aux chiffres */
    private int[] listesChiffres;
    /** La display-list correspondant � l'affichage de l'ic�ne pause */
    private int listePause;
    /** La display-list correspondant � l'affichage du message de fin */
    private int listePerdu;
    /** La display-list correspondant � l'affichage du message pour commencer */
    private int listeCommence;
    /** La texture permettant d'afficher l'image de fond */
    private int textureFond;
    /** La texture permettant d'afficher l'image de pause */
    private int texturePause;
    /** La texture permettant d'afficher le message de fin */
    private int texturePerdu;
    /** La texture permettant d'afficher le message pour commencer */
    private int textureCommence;
    /** Les textures de chiffre */
    private int[] texturesChiffres;
    /** Le score */
    private int score;
    /** Le dernier combo */
    private int combo;
    /** La difficult� */
    private int difficulte;
    /** La partie est d�marr�e */
    private boolean start;
    /** La partie est en pause */
    private boolean pause;
    /** La partie est perdu */
    private boolean perdu;
	
    /**
     * Cr�e une nouvelle zone de jeu de taille width sur height pixels utilisant
     * les capacit�s graphiques fournies.
     * @param capabilities les capacit�s graphiques � utiliser.
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
	 * M�thode appel�e apr�s l'initialisation du contexte OpenGL.
	 * Met en place les diff�rentes display-lists n�cessaires,
	 * charge la texture de fond et lance la boucle principale
	 * de l'affichage.
	 */
	public void init(GLAutoDrawable drawable)
	{
		glu = new GLU();
		
		texturesChiffres = new int[10];
		listesChiffres = new int[10];
		
		drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        // Display lists
		listePuyo = gl.glGenLists(16); 
		listePlateau = listePuyo + 1;
		listePiecesSuivantes = listePlateau + 1;
		
		for(int i=0; i<10; i++)
		{
			listesChiffres[i] = listePiecesSuivantes + i + 1;
		}
		
		listePause = listesChiffres[9] + 1;
		listePerdu = listePause + 1;
		listeCommence = listePerdu + 1;
		
		gl.glNewList(listePuyo, GL.GL_COMPILE);
			dessinerCercle(gl, 0, 0, 15);
		gl.glEndList();
		
		// Chargement de la texture de fond.
		textureFond = genTexture(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureFond);
        TextureReader.Texture textureFond = null;
        try {
            textureFond = TextureReader.readTexture("resources/fond.png");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        makeRGBTexture(gl, glu, textureFond, GL.GL_TEXTURE_2D, false);
        
        // Chargement de la texture de pause.
		texturePause = genTexture(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texturePause);
        TextureReader.Texture texturePause = null;
        try {
            texturePause = TextureReader.readTexture("resources/pause.png");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        makeRGBTexture(gl, glu, texturePause, GL.GL_TEXTURE_2D, false);
        
        // Chargement de la texture perdu.
		texturePerdu = genTexture(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texturePerdu);
        TextureReader.Texture texturePerdu = null;
        try {
            texturePerdu = TextureReader.readTexture("resources/perdu.png");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        makeRGBTexture(gl, glu, texturePerdu, GL.GL_TEXTURE_2D, false);
        
        // Chargement de la texture commence.
		textureCommence = genTexture(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureCommence);
        TextureReader.Texture textureCommence = null;
        try {
            textureCommence = TextureReader.readTexture("resources/commence.png");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        makeRGBTexture(gl, glu, textureCommence, GL.GL_TEXTURE_2D, false);
        
        // Chargement des textures de chiffre.        
        gl.glGenTextures(10, texturesChiffres, 0);

        for (int i=0; i<10; i++) {
            TextureReader.Texture texture = null;
            try {
				texture = TextureReader.readTexture("resources/"+i+".png");
			} catch (IOException e) {
				e.printStackTrace();
			}
            //Create Nearest Filtered Texture
            gl.glBindTexture(GL.GL_TEXTURE_2D, texturesChiffres[i]);

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            makeRGBTexture(gl, glu, texture, GL.GL_TEXTURE_2D, false);
        }
        
        faireListesChiffres(gl);
        faireListePause(gl);
        faireListePerdu(gl);
        faireListeCommence(gl);
        
        // Boucle principale d'affichage
        animator = new FPSAnimator(this, 60);
        animator.start();
	}
	
	/**
	 * Cr�er la display-list pour l'affichage du panneau pause
	 * @param gl
	 */
	private void faireListePause(GL gl) 
	{
		gl.glNewList(listePause, GL.GL_COMPILE);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, texturePause);
			gl.glBegin(GL.GL_QUADS);
				gl.glColor3f(1, 1, 1);
		        gl.glTexCoord2i(0, 0);
		        gl.glVertex2i(0, 0);
		        gl.glTexCoord2i(1, 0);
		        gl.glVertex2i(100, 0);
		        gl.glTexCoord2i(1, 1);
		        gl.glVertex2i(100, 100);
		        gl.glTexCoord2i(0, 1);
		        gl.glVertex2i(0, 100);
		    gl.glEnd();
		    gl.glDisable(GL.GL_TEXTURE_2D);
	    gl.glEndList();
	}
	
	/**
	 * Cr�er la display-list pour l'affichage du panneau perdu
	 * @param gl
	 */
	private void faireListePerdu(GL gl) 
	{
		gl.glNewList(listePerdu, GL.GL_COMPILE);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, texturePerdu);
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
		    gl.glDisable(GL.GL_TEXTURE_2D);
	    gl.glEndList();
	}
	
	/**
	 * Cr�er la display-list pour l'affichage du panneau commence
	 * @param gl
	 */
	private void faireListeCommence(GL gl)
	{
		gl.glNewList(listeCommence, GL.GL_COMPILE);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textureCommence);
			gl.glBegin(GL.GL_QUADS);
				gl.glColor3f(1, 1, 1);
		        gl.glTexCoord2i(0, 0);
		        gl.glVertex2i(0, 0);
		        gl.glTexCoord2i(1, 0);
		        gl.glVertex2i(100, 0);
		        gl.glTexCoord2i(1, 1);
		        gl.glVertex2i(100, 100);
		        gl.glTexCoord2i(0, 1);
		        gl.glVertex2i(0, 100);
		    gl.glEnd();
		    gl.glDisable(GL.GL_TEXTURE_2D);
	    gl.glEndList();
	}

	/**
	 * M�thode appel�e apr�s que la zone d'affichage ait �t� redimensionn�e,
	 * maintient un affichage coh�rent apr�s le redimensionnement.
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
	 * M�thode appel�e par la boucle d'affichage.
	 */
	public void display(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// Fond
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureFond);
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
	    gl.glDisable(GL.GL_TEXTURE_2D);
		
		if (majNecessairePlateau) // Si la mise � jour est n�cessaire
			faireListePlateau(gl); // on reconstruit la display-list du plateau
		if (majNecessairePiecesSuivantes) // Si la mise � jour est n�cessaire
			faireListePiecesSuivantes(gl); // on reconstruit la display-list de l'affichage des pi�ces suivantes
		
		// On affiche les pi�ces suivantes et le plateau
		gl.glCallList(listePiecesSuivantes);
		gl.glCallList(listePlateau);
		
		/*gl.glPushMatrix();
			gl.glColor3d(255.0/255.0, 230.0/255.0, 155.0/255.0);
			gl.glRasterPos2f(500, 70);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "SCORE : " + score);
			gl.glRasterPos2f(600, 70);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "COMBO : " + combo);
		gl.glPopMatrix();*/
		
		afficherInfo(gl);
	
		gl.glFlush();
	}
	
	/**
	 * Non utilis�e.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{

	}
	
	/**
	 * Dessine un cercle de centre de coordonn�es (cx, cy) et de rayon rayon.
	 * @param gl le contexte OpenGL.
	 * @param cx coordonn�e du centre sur l'axe des x.
	 * @param cy coordonn�e du centre sur l'axe des y.
	 * @param rayon rayon du cercle.
	 */
	private void dessinerCercle(GL gl, int cx, int cy, double rayon)
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
	 * n�cessite une mise � jour.
	 * @param plateau le plateau � charger.
	 */
	public void chargerPlateau(Plateau plateau)
	{
		this.plateau = plateau;
		majNecessairePlateau = true;
	}
	
	/**
	 * Cr�e une display-list correspondant au plateau actuellement stock�
	 * et indique que l'affichage du plateau n'a plus besoin d'�tre redessin�.
	 * @param gl le contexte OpenGL.
	 */
	private void faireListePlateau(GL gl)
	{
		majNecessairePlateau = false;
		Color couleur;
		int cx, cy = 400;
		
		gl.glNewList(listePlateau, GL.GL_COMPILE);
			gl.glPushMatrix();
				gl.glTranslatef(293, 4, 0);
				for (int i=3; i<Plateau.HAUTEUR; i++)
				{
					cx = 20;
					
					for (int j=0; j<Plateau.LARGEUR; j++)
					{
						if (!plateau.estLibre(i, j))
						{
							couleur = plateau.getCouleurPuyo(i, j);
							
							gl.glPushMatrix();
								// Affichage du puyo
								gl.glColor3f(couleur.getRed()/255, couleur.getGreen()/255, couleur.getBlue()/255);
								gl.glTranslated(cx, cy, 0);
								gl.glCallList(listePuyo);
								
								// Affichage des liens
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
										gl.glScaled(1, 0.5, 1);
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
	 * Charge les deux prochaines pi�ces et indique que l'affichage des pi�ces
	 * suivantes n�cessite une mise � jour.
	 * @param piecesSuivantes le tableau contenant les pi�ces suivantes.
	 */
	public void chargerPiecesSuivantes(Piece[] piecesSuivantes)
	{
		this.piecesSuivantes = piecesSuivantes;
		majNecessairePiecesSuivantes = true;
	}
	
	/**
	 * Cr�e une display-list correspondant aux pi�ces suivantes actuellement
	 * stock�es et indique que l'affichage des pi�ces suivantes n'a plus 
	 * besoin d'�tre redessin�.
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
				gl.glTranslated(570+15, 459+15, 0);
				
				for (Piece piece : piecesSuivantes)
				{
					gl.glPushMatrix();
						if (piece.getForme() != Piece.COUDE)
							gl.glTranslated(18, 0, 0);
						// Affichage des puyos composant la pi�ce
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
	
	/**
	 * Cr�e les display-lists permettant d'afficher les chiffres.
	 * @param gl le contexte OpenGL.
	 */
	private void faireListesChiffres(GL gl)
	{
		for(int i=0; i<10; i++)
		{
			gl.glNewList(listesChiffres[i], GL.GL_COMPILE);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glBindTexture(GL.GL_TEXTURE_2D, texturesChiffres[i]);
				gl.glBegin(GL.GL_QUADS);
					gl.glColor3f(1, 1, 1);
			        gl.glTexCoord2i(0, 0);
			        gl.glVertex2i(0, 0);
			        gl.glTexCoord2i(1, 0);
			        gl.glVertex2i(40, 0);
			        gl.glTexCoord2i(1, 1);
			        gl.glVertex2i(40, 40);
			        gl.glTexCoord2i(0, 1);
			        gl.glVertex2i(0, 40);
			    gl.glEnd();
			    gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glEndList();
		}
	}
	
	/**
	 * Permet l'affichage des infos de jeu en fonction de l'�tat de la partie.
	 * @param gl le contexte OpenGL.
	 */
	private void afficherInfo(GL gl)
	{
		if(!perdu)
		{
			// Affichage du score
			int scoreTmp = score;
			gl.glPushMatrix();
				gl.glTranslated(530, 150, 0);
				for(int i=4; i>=0; i--)
				{
					int unite = (int) (scoreTmp / Math.pow(10, i));
					scoreTmp -= unite * Math.pow(10, i);
					gl.glCallList(listesChiffres[unite]);
					gl.glTranslated(52, 0, 0);
				}
			gl.glPopMatrix();
			
			// Affichage du combo
			int comboTmp = combo;
			gl.glPushMatrix();
				gl.glTranslated(686, 88, 0);
				for(int i=1; i>=0; i--)
				{
					int unite = (int) (comboTmp / Math.pow(10, i));
					comboTmp -= unite * Math.pow(10, i);
					gl.glCallList(listesChiffres[unite]);
					gl.glTranslated(52, 0, 0);
				}
			gl.glPopMatrix();
			
			// Affichage de la difficult�
			int difficulteTmp = difficulte;
			
			gl.glPushMatrix();
				gl.glTranslated(686, 25, 0);
				for(int i=1; i>=0; i--)
				{
					int unite = (int) (difficulteTmp / Math.pow(10, i));
					difficulteTmp -= unite * Math.pow(10, i);
					gl.glCallList(listesChiffres[unite]);
					gl.glTranslated(52, 0, 0);
				}
			gl.glPopMatrix();
			
			// Affichage du symbole donnant l'�tat du jeu
			if(pause)
			{
				gl.glPushMatrix();
					gl.glTranslated(350, 160, 0);
					gl.glCallList(listePause);
				gl.glPopMatrix();
			}
			else if(!start)
			{
				gl.glPushMatrix();
					gl.glTranslated(350, 160, 0);
					gl.glCallList(listeCommence);
				gl.glPopMatrix();
			}
				
		}
		else // L'utilisateur a perdu la partie
		{
			gl.glPushMatrix();
				gl.glCallList(listePerdu);
				gl.glPushMatrix();
					gl.glTranslated(275, 324, 0);
					int scoreTmp2 = score;
					for(int i=4; i>=0; i--)
					{
						int unite = (int) (scoreTmp2 / Math.pow(10, i));
						scoreTmp2 -= unite * Math.pow(10, i);
						gl.glCallList(listesChiffres[unite]);
						gl.glTranslated(52, 0, 0);
					}
				gl.glPopMatrix();
				gl.glPushMatrix();
					int difficulteTmp2 = difficulte;
					gl.glTranslated(431, 262, 0);
					for(int i=1; i>=0; i--)
					{
						int unite = (int) (difficulteTmp2 / Math.pow(10, i));
						difficulteTmp2 -= unite * Math.pow(10, i);
						gl.glCallList(listesChiffres[unite]);
						gl.glTranslated(52, 0, 0);
					}
				gl.glPopMatrix();
			gl.glPopMatrix();
		}
	}
	
	/**
	 * Permet de mettre � jour l'affichage des infos de jeu.
	 * @param score le score actuel du joueur.
	 * @param combo le nombre de combos effectu�s.
	 * @param difficulte le niveau de difficult�.
	 * @param start indique si la partie est commenc�e.
	 * @param pause indique si la partie est en pause.
	 * @param perdu indique si la partie est perdue.
	 */
	public void chargerInfo(int score, int combo, int difficulte, boolean start, boolean pause, boolean perdu)
	{
		this.score = score;
		this.combo = combo;
		this.difficulte = difficulte + 1;
		this.start = start;
		this.pause = pause;
		this.perdu = perdu;
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
