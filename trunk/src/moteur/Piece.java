package moteur;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe représentant une pièce c'est-à-dire un assemblage de 2 ou 3 puyos.
 * Le nombre du puyo composant la pièce et sa forme si elle est composée de
 * 3 puyos sont choisis aléatoirement lors de la création de la pièce.
 * Une pièce fait correspondre à chaque puyo qui la compose une coordonnée
 * dans le plateau de jeu. Elle est capable de donner ses coordonnées minimales
 * et maximales dans le plateau de jeu et possède un puyo pivot qui sert de
 * centre pour les rotations de la pièce.
 * Elle sait à tout instant si elle est cassée ou pas (c'est-à-dire, si un de
 * ses puyos est bloqué et ne peut plus chuter).
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Piece extends HashMap<Puyo, Point>
{
	private static final long serialVersionUID = -6304397420328786766L;
	/** Générateur de nombres aléatoires partagé par toutes les pièces */
	private static Random rand = new Random();
	/** Constante définissant une pièce formée de 2 puyos */
	public static final int DOUBLE = 0;
	/** Constante définissant une pièce linéaire formée de 3 puyos */
	public static final int TRIPLE = 1;
	/** Constante définissant une pièce coudée formée de 3 puyos */
	public static final int COUDE = 2;
	/** La forme de la pièce */
	private int forme;
	/** Booleen indiquant si la pièce est cassée ou non */
	private boolean cassee;
	/** Puyo constituant le centre de rotation de la pièce */
	private Puyo pivot;
	
	/**
	 * Crée une nouvelle pièce dont le nombre de puyos et la forme est
	 * choisie aléatoirement. A l'origine une pièce n'est pas cassée.
	 */
	public Piece()
	{
		super();
		
		cassee = false;

		forme = rand.nextInt(COUDE+1);
		
		switch(forme)
		{
			case DOUBLE:
				this.put(new Puyo(), new Point(1, 2));
				pivot = new Puyo();
				this.put(pivot, new Point(2, 2));
				break;
			case TRIPLE:
				this.put(new Puyo(), new Point(0, 2));
				pivot = new Puyo();
				this.put(pivot, new Point(1, 2));
				this.put(new Puyo(), new Point(2, 2));
				break;
			case COUDE:
				this.put(new Puyo(), new Point(1, 2));
				pivot = new Puyo();
				this.put(pivot, new Point(2, 2));
				this.put(new Puyo(), new Point(2, 3));
				break;
		}
	}
	
	/**
	 * Clone la pièce de telle sorte que les Puyos contenus soient les mêmes que dans la pièce
	 * originale mais que les points associés soient des clones des originaux (copie semi-profonde).
	 * @return le clone de la pièce tel que les points associés aux Puyos soient aussi des clones.
	 */
	public Object clone()
	{
		Piece clone = (Piece) super.clone();
		
		for (Puyo puyo : this.keySet())
		{
			clone.put(puyo, (Point) clone.get(puyo).clone());
		}
		
		return clone;
	}
	
	/**
	 * Retourne une chaine de caractère représentant la pièce.
	 * @return une chaine de caractère représentant la pièce.
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder("Pièce :");
		
		for (Map.Entry<Puyo, Point> paire : this.entrySet())
		{
			str.append("\n - "+paire.getKey()+" : "+paire.getValue());
		}
		
		return str.toString();
	}
	
	/**
	 * Retourne la forme de la pièce.
	 * @return un entier désignant la forme de la pièce.
	 */
	public int getForme()
	{
		return forme;
	}
	
	/**
	 * Retourne la coordonnée de la ligne minimale (la plus haute dans le
	 * plateau de jeu) de la pièce.
	 * @return la coordonnée désignant la ligne minimale de la pièce.
	 */
	public int getMinI()
	{
		int minI = Integer.MAX_VALUE;
		
		for (Point p : this.values())
		{
			if (minI > p.x)
				minI = p.x;
		}
		
		return minI;
	}
	
	/**
	 * Retourne la coordonnée de la ligne maximale (la plus basse dans le
	 * plateau de jeu) de la pièce.
	 * @return la coordonnée désignant la ligne maximale de la pièce.
	 */
	public int getMaxI()
	{
		int maxI = Integer.MIN_VALUE;
		
		for (Point p : this.values())
		{
			if (maxI < p.x)
				maxI = p.x;
		}
		
		return maxI;
	}
	
	/**
	 * Retourne la coordonnée de la colonne minimale (la plus à gauche dans le
	 * plateau de jeu) de la pièce.
	 * @return la coordonnée désignant la colonne minimale de la pièce.
	 */
	public int getMinJ()
	{
		int minJ = Integer.MAX_VALUE;
		
		for (Point p : this.values())
		{
			if (minJ > p.y)
				minJ = p.y;
		}
		
		return minJ;
	}
	
	/**
	 * Retourne la coordonnée de la colonne maximale (la plus à droite dans le
	 * plateau de jeu) de la pièce.
	 * @return la coordonnée désignant la colonne maximale de la pièce.
	 */
	public int getMaxJ()
	{
		int maxJ = Integer.MIN_VALUE;
		
		for (Point p : this.values())
		{
			if (maxJ < p.y)
				maxJ = p.y;
		}
		
		return maxJ;
	}
	
	/**
	 * Permet de mettre à jour la pièce après une translation verticale dans le
	 * plateau de jeu dans le sens indiqué.
	 * Retourne une copie de la pièce telle qu'elle était avant la transformation.
	 * @param direction la direction de la translation (GAUCHE ou DROITE).
	 * @return une copie de la pièce telle qu'elle était avant la transformation
	 */
	public Piece translationHorizontale(int direction)
	{
		Piece anciennePiece = (Piece) this.clone();
		
		for (Point p : this.values())
		{
			p.y += direction;
		}
		
		return anciennePiece;
	}
	
	/**
	 * Retourne le puyo servant de pivot pour les rotations de la pièce.
	 * @return le puyo pivot.
	 */
	public Puyo getPivot()
	{
		return pivot;
	}
	
	/**
	 * Retourne les coordonnées dans le plateau de jeu du puyo servant
	 * de pivot pour les rotations de la pièce.
	 * @return les coordonnées du puyo pivot.
	 */
	public Point getPointPivot()
	{
		return this.get(pivot);
	}
	
	/**
	 * Retourne vrai ou faux selon que la pièce soit cassée ou non.
	 * @return une booléen indiquant si la pièce est cassée ou non.
	 */
	public boolean estCassee()
	{
		return cassee;
	}
	
	/**
	 * Permet de marquer la pièce comme cassée.
	 */
	public void setCassee()
	{
		cassee = true;
	}
}