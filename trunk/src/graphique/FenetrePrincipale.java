package graphique;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import moteur.MeilleursScores;

/**
 * Classe dérivant d'une JFrame et représentant la fenêtre principale de l'application.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class FenetrePrincipale extends JFrame
{
	private static final long serialVersionUID = 4690329553231316904L;
	
	/**
	 * Crée une fenêtre de jeu avec le titre fourni, de taille width sur height
	 * contenant la zone de jeu fournie.
	 * @param title le titre de la fenêtre
	 * @param width la largueur de la fenêtre.
	 * @param height la hauteur de la fenêtre.
	 * @param zdj la zone de jeu à placer dans la fenêtre.
	 */
	public FenetrePrincipale(String title, int width, int height, ZoneDeJeu zdj)
	{
		super(title);
		this.setSize(width, height);
		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		pane.add(zdj, BorderLayout.CENTER);
		
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		zdj.requestFocus();
	}
	
	/**
	 * Crée et affiche une boite de dialogue permettant de demander à l'utilisateur
	 * son nom après qu'il ait fait un nouveau meilleur score.
	 * Retourne la chaine entrée par l'utilisateur.
	 * @param score le score du joueur
	 * @return la chaine de caractères entrée par l'utilisateur, éventuellement vide.
	 */
	public String demandeNom(int score)
	{
		String nom;
		
		nom = JOptionPane.showInputDialog(this, "Félicitation, vous venez de faire un nouveau meilleur score ("+score+") !\nEntrez votre nom :", "Nouveau meilleur score", JOptionPane.QUESTION_MESSAGE);
		if (nom == null) // L'utilisateur a fermé la fenêtre ...
			nom = "";
		
		return nom;
	}
	
	/**
	 * Crée et affiche une boite de dialogue contenant la liste des meilleurs
	 * scores passée en paramètre.
	 * @param meilleursScores la liste des meilleurs scores à afficher.
	 */
	public void afficheMeilleursScores(MeilleursScores meilleursScores)
	{
		StringBuilder msg = new StringBuilder();
		int i = meilleursScores.size();
		
		if (i != 0)
		{
			for (Entry<Integer, LinkedList<String>> entree : meilleursScores.entrySet())
			{
				for (String nom : entree.getValue())
				{
					msg.insert(0, i+") "+nom+" : "+entree.getKey()+"\n");
					i--;
				}
			}
		}
		else
			msg.insert(0, "Aucun meilleur score enregistré pour le moment !");
		
		msg.insert(0, "Meilleurs scores :\n\n");
		
		JOptionPane.showMessageDialog(this, msg, "Meilleurs scores", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Crée et affiche une boite de dialogue contenant une aide succinct pour
	 * le jeu avec la règle du jeu et les commandes à utiliser.
	 */
	public void afficheAide()
	{
		StringBuilder msg = new StringBuilder("Règle du jeu :\n\nLe but du jeu est de former des blocs de 4 puyos ou plus, ");
		msg.append("une fois formés ces blocs disparaissent de la zone de jeu\net rapportent des points. La partie est perdue ");
		msg.append("si les puyos débordent de la zone de jeu par le haut de l'écran.\nLa difficulté du jeu augmente progressivement ");
		msg.append("lorsque le score augmente, ce qui se traduit par une accélération\nde la chute des pièces.\n\n");
		
		msg.append("Commandes :\n\n");
		msg.append(" - F1 : affiche ce message.\n");
		msg.append(" - F2 ou h ou m : affiche les meilleurs scores enregistrés.\n");
		msg.append(" - Entrée : lance la partie.\n");
		msg.append(" - Retour arrière : réinitialise la partie.\n");
		msg.append(" - Pause : met la partie en pause ou reprend la partie mise en pause.\n");
		msg.append(" - + : Augmente la difficulté.\n");
		msg.append(" - Barre d'espace : accélère la chute de la pièce.\n");
		msg.append(" - Flèches gauche / droite : déplace la pièce d'une case vers la gauche ou la droite.\n");
		msg.append(" - Flèches haut / bas : effectue une rotation antihoraire / horaire de la pièce.");
		
		JOptionPane.showMessageDialog(this, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
	}
}
