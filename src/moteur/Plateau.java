package moteur;

import java.awt.Point;
import java.util.Map;

public class Plateau
{
	public static final int GAUCHE = -1;
	public static final int DROITE = 1;
	public static final int LARGEUR = 6;
	public static final int HAUTEUR = 15;
	
	private Puyo[][] tabPlateau;
	
	public Plateau()
	{
		tabPlateau = new Puyo[HAUTEUR][LARGEUR];
	}
	
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		for (int i=0; i<HAUTEUR; i++)
		{
			for (int j=0; j<LARGEUR; j++)
			{
				s.append(tabPlateau[i][j]);
				s.append(" ");
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
	public void rafraichir(Piece anciennePiece, Piece piece)
	{
		// On supprime l'ancienne pièce si elle existait
		if (anciennePiece != null)
		{
			for (Map.Entry<Puyo, Point> paire : anciennePiece.entrySet())
			{
				tabPlateau[paire.getValue().x][paire.getValue().y] = null;
			}
		}
		
		for (Map.Entry<Puyo, Point> paire : piece.entrySet())
		{
			tabPlateau[paire.getValue().x][paire.getValue().y] = paire.getKey();
		}
	}
	
	public void translationHorizontale(int direction, Piece piece)
	{
		if(direction == GAUCHE)
		{
			if(piece.getMinJ() > 0 && tabPlateau[piece.getMaxI()][piece.getMinJ()-1] == null)
				rafraichir(piece.translationHorizontale(direction), piece);
		}
		else
		{
			if(piece.getMaxJ() < LARGEUR-1 && tabPlateau[piece.getMaxI()][piece.getMaxJ()+1] == null)
				rafraichir(piece.translationHorizontale(direction), piece);
		}
	}
	
	public void translationVerticale(Piece piece)
	{
		for (int i=piece.getMaxI(); i>=piece.getMinI(); i--)
		{
			for (int j=piece.getMinJ(); j<=piece.getMaxJ(); j++)
			{
				if (tabPlateau[i][j] != null)
				{
					if (i+1 < HAUTEUR && tabPlateau[i+1][j] == null)
					{
						tabPlateau[i+1][j] = tabPlateau[i][j];
						tabPlateau[i][j] = null;
						piece.get(tabPlateau[i+1][j]).x++;
					}
					else // le puyo ne peut plus descendre, on le supprime de la forme
						piece.remove(tabPlateau[i][j]);
				}
			}
		}
	}
}
