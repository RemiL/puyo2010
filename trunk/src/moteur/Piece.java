package moteur;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe repr�sentant une pi�ce c'est-�-dire un assemblage de 2 ou 3 puyos.
 * Le nombre du puyo composant la pi�ce et sa forme si elle est compos�e de
 * 3 puyos sont choisis al�atoirement lors de la cr�ation de la pi�ce.
 * Une pi�ce fait correspondre � chaque puyo qui la compose une coordonn�e
 * dans le plateau de jeu. Elle est capable de donner ses coordonn�es minimales
 * et maximales dans le plateau de jeu et poss�de un puyo pivot qui sert de
 * centre pour les rotations de la pi�ce.
 * Elle sait � tout instant si elle est cass�e ou pas (c'est-�-dire, si un de
 * ses puyos est bloqu� et ne peut plus chuter).
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Piece extends HashMap<Puyo, Point>
{
	private static final long serialVersionUID = -6304397420328786766L;
	/** G�n�rateur de nombres al�atoires partag� par toutes les pi�ces */
	private static Random rand = new Random();
	/** Constante d�finissant une pi�ce form�e de 2 puyos */
	public static final int DOUBLE = 0;
	/** Constante d�finissant une pi�ce lin�aire form�e de 3 puyos */
	public static final int TRIPLE = 1;
	/** Constante d�finissant une pi�ce coud�e form�e de 3 puyos */
	public static final int COUDE = 2;
	/** La forme de la pi�ce */
	private int forme;
	/** Booleen indiquant si la pi�ce est cass�e ou non */
	private boolean cassee;
	/** Puyo constituant le centre de rotation de la pi�ce */
	private Puyo pivot;
	
	/**
	 * Cr�e une nouvelle pi�ce dont le nombre de puyos et la forme est
	 * choisie al�atoirement. A l'origine une pi�ce n'est pas cass�e.
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
	 * Clone la pi�ce de telle sorte que les Puyos contenus soient les m�mes que dans la pi�ce
	 * originale mais que les points associ�s soient des clones des originaux (copie semi-profonde).
	 * @return le clone de la pi�ce tel que les points associ�s aux Puyos soient aussi des clones.
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
	 * Retourne une chaine de caract�re repr�sentant la pi�ce.
	 * @return une chaine de caract�re repr�sentant la pi�ce.
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder("Pi�ce :");
		
		for (Map.Entry<Puyo, Point> paire : this.entrySet())
		{
			str.append("\n - "+paire.getKey()+" : "+paire.getValue());
		}
		
		return str.toString();
	}
	
	/**
	 * Retourne la forme de la pi�ce.
	 * @return un entier d�signant la forme de la pi�ce.
	 */
	public int getForme()
	{
		return forme;
	}
	
	/**
	 * Retourne la coordonn�e de la ligne minimale (la plus haute dans le
	 * plateau de jeu) de la pi�ce.
	 * @return la coordonn�e d�signant la ligne minimale de la pi�ce.
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
	 * Retourne la coordonn�e de la ligne maximale (la plus basse dans le
	 * plateau de jeu) de la pi�ce.
	 * @return la coordonn�e d�signant la ligne maximale de la pi�ce.
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
	 * Retourne la coordonn�e de la colonne minimale (la plus � gauche dans le
	 * plateau de jeu) de la pi�ce.
	 * @return la coordonn�e d�signant la colonne minimale de la pi�ce.
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
	 * Retourne la coordonn�e de la colonne maximale (la plus � droite dans le
	 * plateau de jeu) de la pi�ce.
	 * @return la coordonn�e d�signant la colonne maximale de la pi�ce.
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
	 * Permet de mettre � jour la pi�ce apr�s une translation verticale dans le
	 * plateau de jeu dans le sens indiqu�.
	 * Retourne une copie de la pi�ce telle qu'elle �tait avant la transformation.
	 * @param direction la direction de la translation (GAUCHE ou DROITE).
	 * @return une copie de la pi�ce telle qu'elle �tait avant la transformation
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
	 * Retourne le puyo servant de pivot pour les rotations de la pi�ce.
	 * @return le puyo pivot.
	 */
	public Puyo getPivot()
	{
		return pivot;
	}
	
	/**
	 * Retourne les coordonn�es dans le plateau de jeu du puyo servant
	 * de pivot pour les rotations de la pi�ce.
	 * @return les coordonn�es du puyo pivot.
	 */
	public Point getPointPivot()
	{
		return this.get(pivot);
	}
	
	/**
	 * Retourne vrai ou faux selon que la pi�ce soit cass�e ou non.
	 * @return une bool�en indiquant si la pi�ce est cass�e ou non.
	 */
	public boolean estCassee()
	{
		return cassee;
	}
	
	/**
	 * Permet de marquer la pi�ce comme cass�e.
	 */
	public void setCassee()
	{
		cassee = true;
	}
}