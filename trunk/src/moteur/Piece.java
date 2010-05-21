package moteur;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Piece extends HashMap<Puyo, Point>
{
	private static final long serialVersionUID = -6304397420328786766L;
	private static Random rand = new Random();
	private static final int DOUBLE = 0;
	private static final int TRIPLE = 1;
	private static final int COUDE = 2;
	private boolean cassee;
	
	private Puyo pivot;
	
	public Piece()
	{
		super();
		
		cassee = false;

		int forme = rand.nextInt(COUDE+1);
		
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
	
	public String toString()
	{
		StringBuilder str = new StringBuilder("Pièce :");
		
		for (Map.Entry<Puyo, Point> paire : this.entrySet())
		{
			str.append("\n - "+paire.getKey()+" : "+paire.getValue());
		}
		
		return str.toString();
	}
	
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

	public Piece translationHorizontale(int direction)
	{
		Piece anciennePiece = (Piece) this.clone();
		
		for (Point p : this.values())
		{
			p.y += direction;
		}
		
		return anciennePiece;
	}
	
	public Puyo getPivot()
	{
		return pivot;
	}
	
	public Point getPointPivot()
	{
		return this.get(pivot);
	}
	
	public boolean estCassee()
	{
		return cassee;
	}
	
	public void setCassee()
	{
		cassee = true;
	}
}