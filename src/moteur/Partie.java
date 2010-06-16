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
	/** Contient les valeurs de changement de difficult� */
	private int[] changementsDifficulte;
	
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
		
		// On cr�e le tableau contenant les valeurs de changement de difficult�
		changementsDifficulte = new int[9];
		changementsDifficulte[0] = 1000;
		for (int i=1; i<9; i++)
		{
			changementsDifficulte[i] = (int) (changementsDifficulte[i-1] + 1000 * (1 + (2 * i) / 10.0));
		}
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
	
	/**
	 * Permet d'incr�menter le compteur de combo d'une unit�.
	 */
	public void  ajoutCombo()
	{
		combo++;
	}
	
	/**
	 * Permet de remettre � z�ro le compteur de combo.
	 */
	public void resetCombo()
	{
		combo=0;
	}
	
	/**
	 * Permet d'ajouter le score en prenant en compteur le nombre
	 * de combo effectu�. Le nouveau score est calcul� en ajoutant
	 * � l'ancien score le score fourni multipli� par le nombre de
	 * combos effectu�s et par un bonus d�pendant de la difficult�.
	 * Retourne vrai ou faux selon que l'ajout du score ait modifi�
	 * ou non le niveau de difficult�.
	 * @param score2 le score � ajouter.
	 * @return un boolean indiquant si l'ajout du score a modifi�
	 * le niveau de difficult�.
	 */
	public boolean ajoutScore(int score2)
	{
		// Ajout du score
		score += score2 * combo * (1 + difficulte / 10.0);
		
		// Calcul du niveau de difficult�
		int sauvDifficulte = difficulte;
		
		// On cherche la difficult� correspondant au score en limitant le niveau � 9.
		while (difficulte < 9 && changementsDifficulte[difficulte] <= score)
			difficulte++;
		
		// On v�rifie si le niveau a �t� modifi�.
		return (sauvDifficulte != difficulte);
	}
	
	/**
	 * Retourne le score actuel du joueur dans la partie.
	 * @return le score actuel.
	 */
	public int getScore()
	{
		return score;
	}
	
	/**
	 * Retourne le dernier nombre de combos effectu�s.
	 * @return le nombre de combos.
	 */
	public int getCombo()
	{
		return combo;
	}
	
	/**
	 * Retourne le niveau de difficult� actuel du jeu.
	 * @return un entier entre 0 et 9 d�finissant le niveau 
	 * de difficult� (du moins difficile au plus difficile).
	 */
	public int getDifficulte()
	{
		return difficulte;
	}
	
	/**
	 * Permet d'augmenter le niveau de difficult� du jeu. Si
	 * le niveau maximum avait d�j� �t� pr�c�demment atteint
	 * l'appel de cette m�thode n'a pas d'effet et elle retourne
	 * faux sinon elle retourne vrai.
	 * @return un booleen indiquant si la difficult� a �t� r�ellement
	 * modifi�e ou non.
	 */
	public boolean augmenterDifficulte() 
	{
		if (difficulte < 9)
		{
			difficulte++;
			return true;
		}
		else
			return false;
	}
}
