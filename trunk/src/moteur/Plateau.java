package moteur;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;

/**
 * Classe représentant le plateau de jeu de 12 lignes sur 6 colonnes et
 * permettant la gestion des actions sur une pièce dans le plateau.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class Plateau implements Cloneable
{
	/** Constante désignant une translation vers la gauche */
	public static final int GAUCHE = -1;
	/** Constante désignant une translation vers la droite */
	public static final int DROITE = 1;
	/** Constante désignant une rotation dans le sens horaire */
	public static final int HORAIRE = 1;
	/** Constante désignant une rotation dans le sens antihoraire */
	public static final int ANTIHORAIRE = -1;
	/** Constante désignant la largueur du plateau de jeu dans la présentation interne */
	public static final int LARGEUR = 6;
	/** Constante désignant la hauteur du plateau de jeu dans la présentation interne */
	public static final int HAUTEUR = 15;
	/** Tableau à deux dimensions représentant le plateau de jeu */
	private Puyo[][] tabPlateau;
	
	/**
	 * Crée un nouveau plateau de jeu vide.
	 */
	public Plateau()
	{
		tabPlateau = new Puyo[HAUTEUR][LARGEUR];
	}
	
	/**
	 * Retourne une chaine de caractère représentant le tableau de jeu.
	 * @return une chaine de caractère représentant le tableau de jeu.
	 */
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
	
	/**
	 * Retourne une copie du plateau (les puyos placés dans le plateau
	 * ne sont par contre pas des copies).
	 * @return une copie du plateau.
	 */
	protected Object clone()
	{
		Plateau clone = new Plateau();
		clone.tabPlateau = this.tabPlateau.clone();
		
		return clone;
	}
	
	/**
	 * Retourne vrai ou faux selon que la case du plateau de coordonnées (i,j)
	 * soit libre ou non.
	 * @param i la coordonnée désignant la ligne considérée.
	 * @param j la coordonnée désignant la colonne considérée.
	 * @return un booleen indiquant si la case est libre ou non.
	 */
	public boolean estLibre(int i, int j)
	{
		return (tabPlateau[i][j] == null);
	}
	
	/**
	 * Retourne la couleur du puyo situé à la case (i,j) du plateau.
	 * Cette méthode ne doit pas être appelée sur une case vide.
	 * @param i la coordonnée désignant la ligne considérée.
	 * @param j la coordonnée désignant la colonne considérée.
	 * @return la couleur du puyo situé dans la case.
	 */
	public Color getCouleurPuyo(int i, int j)
	{
		return tabPlateau[i][j].getCouleur();
	}
	
	/**
	 * Retourne le puyo situé à la case (i,j) du plateau ou null
	 * si la case est vide.
	 * @param i la coordonnée désignant la ligne considérée.
	 * @param j la coordonnée désignant la colonne considérée.
	 * @return le puyo situé dans la case ou null si la case est vide.
	 */
	public Puyo getPuyo(int i, int j)
	{
		return tabPlateau[i][j];
	}
	
	/**
	 * Permet de déplacer rafraichir l'état d'une pièce dans le plateau après un déplacement
	 * quelconque. L'ancienne pièce est retirée du plateau si elle existe (état précédent de
	 * la pièce) et la pièce est placée dans le plateau.
	 * @param anciennePiece la pièce avant la transformation.
	 * @param piece la pièce dans son état actuel.
	 */
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
		// On place la pièce à son emplacement actuel
		for (Map.Entry<Puyo, Point> paire : piece.entrySet())
		{
			tabPlateau[paire.getValue().x][paire.getValue().y] = paire.getKey();
		}
	}
	
	/**
	 * Permet d'ajouter une pièce dans le plateau.
	 * @param piece la pièce à ajouter.
	 */
	public void ajouter(Piece piece)
	{
		this.rafraichir(null, piece);
	}
	
	/**
	 * Permet de translater dans le plateau de jeu la pièce fournie dans
	 * la direction indiquée. Si la translation n'est pas possible, elle
	 * n'est pas effectuée et la pièce n'est pas modifiée.
	 * @param direction la direction de la translation (GAUCHE ou DROITE).
	 * @param piece la pièce a translaté.
	 */
	public void translationHorizontale(int direction, Piece piece)
	{
		if (piece.getMaxI() > 2) // On attend que la pièce apparaisse dans la zone de jeu avant d'autoriser son déplacement
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
	}
	
	/**
	 * Permet de faire descendre la pièce fournie d'une case dans
	 * le plateau de jeu. Seuls les puyos de la pièce pour lesquels
	 * la chute est possible sont déplacés.
	 * Retourne faux tous les puyos de la pièce sont bloqués et vrai sinon.
	 * @param piece la pièce a déplacé.
	 * @return un booleen indiquant si la pièce contient encore des puyos
	 * pouvant éventuellement encore descendre.
	 */
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
	
	/**
	 * Permet d'effectuer dans le plateau de jeu une rotation de la pièce
	 * fournie de 90° dans le sens précisé. Si la rotation n'est pas possible,
	 * elle n'est pas effectuée et la pièce n'est pas modifiée. 
	 * @param sens le sens de la rotation (HORAIRE ou ANTIHORAIRE).
	 * @param piece la piece sur laquelle on veut effectuer la rotation.
	 */
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
	
	/**
	 * Permet de créer les liens pour le puyo situé à la case (i,j) du
	 * plateau de jeu en sachant que la pièce courante est piece afin
	 * d'éviter de créer des liens avec des puyos qui n'ont pas terminés
	 * leur chute.
	 * @param piece la pièce courante
	 * @param i la coordonnée désignant la ligne considérée. 
	 * @param j la coordonnée désignant la colonne considérée.
	 */
	private void creerLiens(Piece piece, int i, int j)
	{
		if (i > 2) // Pas de liens pour les puyos hors de la zone visible du plateau.
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
}
