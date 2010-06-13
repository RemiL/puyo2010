package moteur;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
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
	/** Constante indiquant que le jeu est perdu */
	public static final int PERDU = -1;
	/** Constante indiquant que tous les puyos de la pièce sont bloqués */
	public static final int PIECE_VIDE = 0;
	/** Constante indiquant que la pièce comporte toujours des puyos non bloqués */
	public static final int PIECE_NON_VIDE = 1;
	/** Tableau à deux dimensions représentant le plateau de jeu */
	private Puyo[][] tabPlateau;
	/** Les puyos à vérifier pour la destruction éventuelle de blocs */
	private ArrayList<Point> derniersPuyos;
	
	/**
	 * Crée un nouveau plateau de jeu vide.
	 */
	public Plateau()
	{
		tabPlateau = new Puyo[HAUTEUR][LARGEUR];
		derniersPuyos = new ArrayList<Point>();
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
	 * Retourne PERDU si un Puyo est bloqué hors de la zone de jeu,
	 * PIECE_VIDE si tous les puyos de la pièce sont bloqués ou
	 * PIECE_NON_VIDE s'il reste des puyos pouvant encore descendre.
	 * @param piece la pièce a déplacé.
	 * @return un entier indiquant si la pièce contient encore des puyos
	 * pouvant éventuellement encore descendre ou non ou si le jeu est perdu.
	 */
	public int translationVerticale(Piece piece)
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
						if(i < 3)
							return PERDU;
							
						piece.remove(tabPlateau[i][j]);
						derniersPuyos.add(new Point(i, j));
						piece.setCassee();
						creerLiens(piece, i, j);
					}
				}
			}
		}
		
		if (piece.isEmpty()) return PIECE_VIDE;
		else return PIECE_NON_VIDE;
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
			if (i-1 > 3)
			{
				tabPlateau[i][j].setLien(Puyo.HAUT, tabPlateau[i][j].equals(tabPlateau[i-1][j]));
			}
			if (i+1 < HAUTEUR && tabPlateau[i+1][j] != null)
			{
				tabPlateau[i+1][j].setLien(Puyo.HAUT, tabPlateau[i][j].equals(tabPlateau[i+1][j]));
			}
			if (j+1 < LARGEUR)
			{
				tabPlateau[i][j].setLien(Puyo.DROITE, tabPlateau[i][j].equals(tabPlateau[i][j+1]) && (piece == null || !piece.containsKey(tabPlateau[i][j+1])));
			}
			if (j-1 >= 0 && tabPlateau[i][j-1] != null)
			{
				tabPlateau[i][j-1].setLien(Puyo.DROITE, tabPlateau[i][j].equals(tabPlateau[i][j-1]) && (piece == null || !piece.containsKey(tabPlateau[i][j-1])));
			}
		}
	}
	
	/**
	 * Permet de détruire les blocs comportant plus de 4 puyos actuellement
	 * présent sur le plateau de jeu.
	 * Retourne le score rapporté par les destructions effectuées : 100 points
	 * par bloc de 4 puros détruit avec un bonus de 10 par puyo supplémentaire.
	 * @return le score rapporté par les destructions effectuées ou 0 si
	 * aucune destruction de bloc n'a été effectuée.
	 */
	public int detruireBlocs()
	{
		int score = 0;
		
		for(Point point : derniersPuyos) // Pour tous les puyos dernièrement placés ou déplacés dans le tableau
		{
			if(tabPlateau[point.x][point.y] != null) // S'il n'a pas déjà été supprimé
			{
				// On initialise une liste vide pour stocker les coordonnées des puyos d'un éventuel bloc
				ArrayList<Point> listePointsBloc = new ArrayList<Point>();
				// On remplit la liste.
				parcoursBloc(tabPlateau[point.x][point.y].getCouleur(), point.x, point.y, listePointsBloc);
				
				if(listePointsBloc.size() >= 4) // Si elle comporte au moins 4 puyos
				{
					score += 100 + (listePointsBloc.size()-4)*10; // On calcule le score	
					for(Point p : listePointsBloc) // et on supprime les puyos du plateau.
						tabPlateau[p.x][p.y] = null;
				}
			}
		}
		
		derniersPuyos.clear(); // On vide la liste des derniers puyos.
		
		return score;
	}
	
	/**
	 * Méthode permettant de parcourir récursivement le plateau à partir d'un point
	 * pour ajouter à la liste fournie les coordonnées des puyos voisins directs
	 * possédant la même couleur que celle fournie. A la fin de l'appel, la liste
	 * contient toutes coordonnées d'un bloc de puyos reliés entre eux.
	 * @param couleur la couleur du bloc recherché.
	 * @param i la coordonnée désignant la ligne considérée. 
	 * @param j la coordonnée désignant la colonne considérée. 
	 * @param listePointsBloc la liste contenant les points du bloc.
	 */
	private void parcoursBloc(Color couleur, int i, int j, ArrayList<Point> listePointsBloc) 
	{
		if(i >= 3 && i < HAUTEUR && j >= 0 && j < LARGEUR // Si on ne sort pas du plateau
				&& !listePointsBloc.contains(new Point(i, j)) // et que la casse n'a pas déjà été visitée
				&& tabPlateau[i][j] != null // et qu'il y a bien un puyo dans cette case
				&& tabPlateau[i][j].getCouleur().equals(couleur)) // et qu'il a la bonne couleur
		{
			listePointsBloc.add(new Point(i, j)); // On ajoute le puyo à la liste
			// et on relance la recherche recursivement sur ses voisins.
			parcoursBloc(couleur, i+1, j, listePointsBloc);
			parcoursBloc(couleur, i-1, j, listePointsBloc);
			parcoursBloc(couleur, i, j+1, listePointsBloc);
			parcoursBloc(couleur, i, j-1, listePointsBloc);
		}
	}
	
	/**
	 * Méthode permettant de reorganiser le plateau après la destruction
	 * de blocs en appliquant la gravité. Les puyos au-dessus d'une case
	 * vide tombent autant qu'ils le peuvent.
	 */
	public void faireChuterPuyos()
	{
		int di;
		
		// On parcourt le tableau de jeu à partir de la deuxième ligne en partant du bas
		for (int i=HAUTEUR-2; i>=3; i--)
		{
			for (int j=0; j<LARGEUR; j++)
			{
				if (tabPlateau[i][j] != null) // Si la case est occupée
				{
					di = 1;
					
					// On descend le puyo autant que possible
					while (i+di < HAUTEUR && tabPlateau[i+di][j] == null)
					{
						tabPlateau[i+di][j] = tabPlateau[i+di-1][j];
						tabPlateau[i+di-1][j] = null;
						di++;
					}
					
					if (di > 1) // Si on a déplacé le puyo
					{
						// On stocke ses coordonnées dans la liste des puyos récemment déplacés
						derniersPuyos.add(new Point(i+di-1, j));
						// On refait la création des liens du puyo
						creerLiens(null, i+di-1, j);
						// et de son ancien voisin.
						if (j > 0 && tabPlateau[i][j-1] != null)
							creerLiens(null, i, j-1);
					}
				}
					
			}
		}
	}
}
