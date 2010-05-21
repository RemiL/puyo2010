package moteur;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;

public class Plateau implements Cloneable
{
	public static final int GAUCHE = -1;
	public static final int DROITE = 1;
	public static final int HORAIRE = 1;
	public static final int ANTIHORAIRE = -1;
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
	
	protected Object clone()
	{
		Plateau clone = new Plateau();
		clone.tabPlateau = this.tabPlateau.clone();
		
		return clone;
	}
	
	public boolean estLibre(int i, int j)
	{
		return (tabPlateau[i][j] == null);
	}
	
	public Color getCouleurPuyo(int i, int j)
	{
		return tabPlateau[i][j].getCouleur();
	}
	
	public Puyo getPuyo(int i, int j)
	{
		return tabPlateau[i][j];
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
	
	public void ajouter(Piece piece)
	{
		this.rafraichir(null, piece);
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
	
	public boolean translationVerticale(Piece piece)
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
					{
						piece.remove(tabPlateau[i][j]);
						piece.setCassee();
						creerLiens(piece, i, j);
					}
				}
			}
		}
		
		return !piece.isEmpty();
	}
	
	public void rotation(int sens, Piece piece)
	{
		Point point, pointPivot = piece.getPointPivot();
		Piece anciennePiece = (Piece) piece.clone();
		boolean possible = true;
		int i, j;
		
		for (Map.Entry<Puyo, Point> paire : piece.entrySet())
		{
			point = paire.getValue();
			
			if (!pointPivot.equals(point))
			{
				i = sens * (point.y - pointPivot.y) + pointPivot.x;
				j = -sens * (point.x - pointPivot.x) + pointPivot.y;
				
				if ((i > 3 && i < HAUTEUR && j >= 0 && j < LARGEUR) && (tabPlateau[i][j] == null || piece.containsKey(tabPlateau[i][j])))
				{
					piece.get(paire.getKey()).setLocation(i, j);
				}
				else
				{
					possible = false;
					
					for (Puyo puyo : piece.keySet())
					{
						piece.put(puyo, anciennePiece.get(puyo));
					}
					
					break;
				}
			}
		}
		
		if (possible)
		{
			rafraichir(anciennePiece, piece);
		}
	}
	
	private void creerLiens(Piece piece, int i, int j)
	{
		if (i-1 > 3 && tabPlateau[i][j].equals(tabPlateau[i-1][j]))
		{
			tabPlateau[i][j].setLien(Puyo.HAUT);
		}
		if (i+1 < HAUTEUR && tabPlateau[i][j].equals(tabPlateau[i+1][j]))
		{
			tabPlateau[i+1][j].setLien(Puyo.HAUT);
		}
		if (j+1 < LARGEUR && tabPlateau[i][j].equals(tabPlateau[i][j+1]) && !piece.containsKey(tabPlateau[i][j+1]))
		{
			tabPlateau[i][j].setLien(Puyo.DROITE);
		}
		if (j-1 >= 0 && tabPlateau[i][j].equals(tabPlateau[i][j-1]) && !piece.containsKey(tabPlateau[i][j-1]))
		{
			tabPlateau[i][j-1].setLien(Puyo.DROITE);
		}
	}
}
