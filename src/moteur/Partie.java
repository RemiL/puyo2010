package moteur;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe représentant une partie, c'est-à-dire un plateau de jeu,
 * un niveau de difficulté, un score, la pièce en train de chuter
 * et les deux pièces à venir.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Partie
{
	/** Le plateau de jeu */
	private Plateau plateau;
	/** Une file contenant les deux prochaines pièces */
	private LinkedBlockingQueue<Piece> prochainesPieces;
	/** La pièce en cours de chute */
	private Piece pieceCourante;
	/** Le score courant */
	private int score;
	/** Le niveau de difficulté */
	private int difficulte;
	/** Le nombre d'enchaînements effectués */
	private int combo;
	/** Indique si la partie est commencée ou non */
	private boolean start;
	/** Indique si la partie est mise en pause ou non */
	private boolean pause;
	
	/**
	 * Crée une nouvelle partie non commencée avec un plateau vide
	 * et deux pièces en attente.
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
	 * Retourne le plateau correspondant à la partie.
	 * @return le plateau de la partie.
	 */
	public Plateau getPlateau()
	{
		return plateau;
	}
	
	/**
	 * Retourne la pièce actuellement en cours de chute.
	 * @return la pièce en chute.
	 */
	public Piece getPieceCourante()
	{
		return pieceCourante;
	}
	
	/**
	 * Prend la première pièce de la file d'attente pour l'utiliser
	 * comme pièce courante et ajoute une nouvelle pièce en fin de
	 * file pour maintenir le nombre de pièces en attente à 2.
	 * Retourne un tableau contenant les deux pièces en attente
	 * après le chargement de la pièce suivante.
	 * @return un tableau contenant les deux pièces en attente.
	 */
	public Piece[] chargerPieceSuivante()
	{
		pieceCourante = prochainesPieces.poll();
		plateau.ajouter(pieceCourante);
		prochainesPieces.add(new Piece());
		
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	/**
	 * Retourne les deux pièces actuellement en attente dans un
	 * tableau.
	 * @return un tableau contenant les deux pièces en attente.
	 */
	public Piece[] getPiecesSuivantes()
	{
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	/**
	 * Permet d'indiquer que la partie est commencée.
	 */
	public void commencerPartie()
	{
		start = true;
	}
	
	/**
	 * Retourne vrai ou faux selon que la partie soit en cours ou non
	 * @return un booléen indiquant si la partie est en cours.
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
	 * @return un booléen indiquant si la partie est en pause.
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
