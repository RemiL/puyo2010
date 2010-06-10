package moteur;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe repr�sentant une partie, c'est-�-dire un plateau de jeu,
 * un niveau de difficult�, un score, la pi�ce en train de chuter
 * et les deux pi�ces � venir.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Partie
{
	/** Le plateau de jeu */
	private Plateau plateau;
	/** Une file contenant les deux prochaines pi�ces */
	private LinkedBlockingQueue<Piece> prochainesPieces;
	/** La pi�ce en cours de chute */
	private Piece pieceCourante;
	/** Le score courant */
	private int score;
	/** Le niveau de difficult� */
	private int difficulte;
	/** Le nombre d'encha�nements effectu�s */
	private int combo;
	/** Indique si la partie est commenc�e ou non */
	private boolean start;
	/** Indique si la partie est mise en pause ou non */
	private boolean pause;
	
	/**
	 * Cr�e une nouvelle partie non commenc�e avec un plateau vide
	 * et deux pi�ces en attente.
	 */
	public Partie()
	{
		plateau = new Plateau();
		prochainesPieces = new LinkedBlockingQueue<Piece>(2);
		score = 0;
		difficulte = 0;
		combo = 0;
		start = false;
		pause = false;
		
		prochainesPieces.add(new Piece());
		prochainesPieces.add(new Piece());
	}
	
	/**
	 * Retourne le plateau correspondant � la partie.
	 * @return le plateau de la partie.
	 */
	public Plateau getPlateau()
	{
		return plateau;
	}
	
	/**
	 * Retourne la pi�ce actuellement en cours de chute.
	 * @return la pi�ce en chute.
	 */
	public Piece getPieceCourante()
	{
		return pieceCourante;
	}
	
	/**
	 * Prend la premi�re pi�ce de la file d'attente pour l'utiliser
	 * comme pi�ce courante et ajoute une nouvelle pi�ce en fin de
	 * file pour maintenir le nombre de pi�ces en attente � 2.
	 * Retourne un tableau contenant les deux pi�ces en attente
	 * apr�s le chargement de la pi�ce suivante.
	 * @return un tableau contenant les deux pi�ces en attente.
	 */
	public Piece[] chargerPieceSuivante()
	{
		pieceCourante = prochainesPieces.poll();
		plateau.ajouter(pieceCourante);
		prochainesPieces.add(new Piece());
		
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	/**
	 * Retourne les deux pi�ces actuellement en attente dans un
	 * tableau.
	 * @return un tableau contenant les deux pi�ces en attente.
	 */
	public Piece[] getPiecesSuivantes()
	{
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	/**
	 * Permet d'indiquer que la partie est commenc�e.
	 */
	public void commencerPartie()
	{
		start = true;
	}
	
	/**
	 * Retourne vrai ou faux selon que la partie soit en cours ou non
	 * @return un bool�en indiquant si la partie est en cours.
	 */
	public boolean estEnCours()
	{
		return start;
	}
		
	/**
	 * Permet d'indiquer que la partie est actuellement mise en pause.
	 */
	public void mettreEnPause()
	{
		pause = true;
	}
	
	/**
	 * Permet d'enlever l'indication que la partie est actuellement
	 * mise en pause.
	 */
	public void reprendrePartie()
	{
		pause = false;
	}
	
	/**
	 * Retourne vrai ou faux selon que la partie soit actuellement
	 * mise en pause ou non.
	 * @return un bool�en indiquant si la partie est en pause.
	 */
	public boolean estEnPause()
	{
		return pause;
	}
	
	public void  ajoutCombo()
	{
		combo++;
	}
	
	public void resetCombo()
	{
		combo=0;
	}

	public void ajoutScore(int score2)
	{
		score += combo * score2;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getCombo()
	{
		return combo;
	}
}
