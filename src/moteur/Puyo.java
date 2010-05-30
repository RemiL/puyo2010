package moteur;

import java.awt.Color;
import java.util.Random;

/**
 * Classe représentant un puyo. Le puyo possède juste une couleur et sait
 * s'il possède un lien avec le puyo juste au dessus de lui ou le puyo
 * juste à sa droite. L'égalité entre deux puyos peut être testée : s'ils
 * ont la même couleur, ils sont considérés comme égaux.
 * A sa création le puyo peut prend une couleur aléatoire parmi un choix de quatre,
 * si sa couleur n'est pas indiqué.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Puyo
{
	/** Générateur de nombres aléatoires partagé par tous les puyos */
	private static Random rand = new Random();
	/** Couleurs possibles pour le puyo, utilisées pour le choix aléatoire d'une couleur */
	private static final Color[] couleurs = 
	{
		Color.MAGENTA,
		Color.RED,
		Color.GREEN,
		Color.BLACK
	};
	/** La couleur du puyo */
	private Color couleur;
	/** Constante définissant l'indice du lien vers la droite */
	public static final int DROITE = 0;
	/** Constante définissant l'indice du lien vers le haut */
	public static final int HAUT = 1;
	/** Tableaux permettant de stocker la présence des liens */
	private boolean[] liens;
	
	/**
	 * Crée un nouveau puyo avec une couleur aléatoire choisie parmi les
	 * quatre possibles et aucun lien avec ces voisins.
	 */ 
	public Puyo()
	{
		this(couleurs[rand.nextInt(couleurs.length)]);
	}
	
	/**
	 * Crée un nouveau possédant la couleur couleur et aucun lien avec ces voisins.
	 * @param couleur la couleur du puyo
	 */
	private Puyo(Color couleur)
	{
		this.couleur = couleur;
		
		liens = new boolean[2];
		liens[DROITE] = liens[HAUT] = false;
	}
	
	/**
	 * Retourne la couleur du puyo considéré.
	 * @return la couleur du puyo.
	 */
	public Color getCouleur()
	{
		return couleur;
	}
	
	/**
	 * Retourne vrai ou faux selon que le lien désigné par l'indice lien
	 * existe ou non.
	 * @param lien l'indice correspondant au lien voulu.
	 * @return un booleen indiquant l'existance du lien.
	 */
	public boolean getLien(int lien)
	{
		return liens[lien];
	}
	
	/**
	 * Permet d'indiquer que le lien désigné par l'indice lien existe.
	 * @param lien l'indice correspondant au lien.
	 */
	public void setLien(int lien)
	{
		liens[lien] = true;
	}
	
	/**
	 * Retourne vrai ou faux selon que le puyo soit égal à l'objet testé.
	 * Le résultat est vrai si l'objet testé est un puyo de couleur identique
	 * à ce puyo.
	 * @param obj l'objet à tester.
	 * @return un booleen indiquant l'égalité entre le puyo et l'objet.
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
