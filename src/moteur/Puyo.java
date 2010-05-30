package moteur;

import java.awt.Color;
import java.util.Random;

/**
 * Classe repr�sentant un puyo. Le puyo poss�de juste une couleur et sait
 * s'il poss�de un lien avec le puyo juste au dessus de lui ou le puyo
 * juste � sa droite. L'�galit� entre deux puyos peut �tre test�e : s'ils
 * ont la m�me couleur, ils sont consid�r�s comme �gaux.
 * A sa cr�ation le puyo peut prend une couleur al�atoire parmi un choix de quatre,
 * si sa couleur n'est pas indiqu�.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Puyo
{
	/** G�n�rateur de nombres al�atoires partag� par tous les puyos */
	private static Random rand = new Random();
	/** Couleurs possibles pour le puyo, utilis�es pour le choix al�atoire d'une couleur */
	private static final Color[] couleurs = 
	{
		Color.MAGENTA,
		Color.RED,
		Color.GREEN,
		Color.BLACK
	};
	/** La couleur du puyo */
	private Color couleur;
	/** Constante d�finissant l'indice du lien vers la droite */
	public static final int DROITE = 0;
	/** Constante d�finissant l'indice du lien vers le haut */
	public static final int HAUT = 1;
	/** Tableaux permettant de stocker la pr�sence des liens */
	private boolean[] liens;
	
	/**
	 * Cr�e un nouveau puyo avec une couleur al�atoire choisie parmi les
	 * quatre possibles et aucun lien avec ces voisins.
	 */ 
	public Puyo()
	{
		this(couleurs[rand.nextInt(couleurs.length)]);
	}
	
	/**
	 * Cr�e un nouveau poss�dant la couleur couleur et aucun lien avec ces voisins.
	 * @param couleur la couleur du puyo
	 */
	private Puyo(Color couleur)
	{
		this.couleur = couleur;
		
		liens = new boolean[2];
		liens[DROITE] = liens[HAUT] = false;
	}
	
	/**
	 * Retourne la couleur du puyo consid�r�.
	 * @return la couleur du puyo.
	 */
	public Color getCouleur()
	{
		return couleur;
	}
	
	/**
	 * Retourne vrai ou faux selon que le lien d�sign� par l'indice lien
	 * existe ou non.
	 * @param lien l'indice correspondant au lien voulu.
	 * @return un booleen indiquant l'existance du lien.
	 */
	public boolean getLien(int lien)
	{
		return liens[lien];
	}
	
	/**
	 * Permet d'indiquer que le lien d�sign� par l'indice lien existe.
	 * @param lien l'indice correspondant au lien.
	 */
	public void setLien(int lien)
	{
		liens[lien] = true;
	}
	
	/**
	 * Retourne vrai ou faux selon que le puyo soit �gal � l'objet test�.
	 * Le r�sultat est vrai si l'objet test� est un puyo de couleur identique
	 * � ce puyo.
	 * @param obj l'objet � tester.
	 * @return un booleen indiquant l'�galit� entre le puyo et l'objet.
	 */
	public boolean equals(Object obj)
	{
		boolean egaux = false;
		
		if (obj instanceof Puyo)
		{
			Puyo puyo = (Puyo) obj;
			egaux = couleur.equals(puyo.getCouleur());
		}
		
		return egaux;
	}
}
