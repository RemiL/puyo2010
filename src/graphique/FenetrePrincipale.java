package graphique;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import moteur.MeilleursScores;

/**
 * Classe d�rivant d'une JFrame et repr�sentant la fen�tre principale de l'application.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class FenetrePrincipale extends JFrame
{
	private static final long serialVersionUID = 4690329553231316904L;
	
	/**
	 * Cr�e une fen�tre de jeu avec le titre fourni, de taille width sur height
	 * contenant la zone de jeu fournie.
	 * @param title le titre de la fen�tre
	 * @param width la largueur de la fen�tre.
	 * @param height la hauteur de la fen�tre.
	 * @param zdj la zone de jeu � placer dans la fen�tre.
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
	 * Cr�e et affiche une boite de dialogue permettant de demander � l'utilisateur
	 * son nom apr�s qu'il ait fait un nouveau meilleur score.
	 * Retourne la chaine entr�e par l'utilisateur.
	 * @param score le score du joueur
	 * @return la chaine de caract�res entr�e par l'utilisateur, �ventuellement vide.
	 */
	public String demandeNom(int score)
	{
		String nom;
		
		nom = JOptionPane.showInputDialog(this, "F�licitation, vous venez de faire un nouveau meilleur score ("+score+") !\nEntrez votre nom :", "Nouveau meilleur score", JOptionPane.QUESTION_MESSAGE);
		if (nom == null) // L'utilisateur a ferm� la fen�tre ...
			nom = "";
		
		return nom;
	}
	
	/**
	 * Cr�e et affiche une boite de dialogue contenant la liste des meilleurs
	 * scores pass�e en param�tre.
	 * @param meilleursScores la liste des meilleurs scores � afficher.
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
			msg.insert(0, "Aucun meilleur score enregistr� pour le moment !");
		
		msg.insert(0, "Meilleurs scores :\n\n");
		
		JOptionPane.showMessageDialog(this, msg, "Meilleurs scores", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Cr�e et affiche une boite de dialogue contenant une aide succinct pour
	 * le jeu avec la r�gle du jeu et les commandes � utiliser.
	 */
	public void afficheAide()
	{
		StringBuilder msg = new StringBuilder("R�gle du jeu :\n\nLe but du jeu est de former des blocs de 4 puyos ou plus, ");
		msg.append("une fois form�s ces blocs disparaissent de la zone de jeu\net rapportent des points. La partie est perdue ");
		msg.append("si les puyos d�bordent de la zone de jeu par le haut de l'�cran.\nLa difficult� du jeu augmente progressivement ");
		msg.append("lorsque le score augmente, ce qui se traduit par une acc�l�ration\nde la chute des pi�ces.\n\n");
		
		msg.append("Commandes :\n\n");
		msg.append(" - F1 : affiche ce message.\n");
		msg.append(" - F2 ou h ou m : affiche les meilleurs scores enregistr�s.\n");
		msg.append(" - Entr�e : lance la partie.\n");
		msg.append(" - Retour arri�re : r�initialise la partie.\n");
		msg.append(" - Pause : met la partie en pause ou reprend la partie mise en pause.\n");
		msg.append(" - + : Augmente la difficult�.\n");
		msg.append(" - Barre d'espace : acc�l�re la chute de la pi�ce.\n");
		msg.append(" - Fl�ches gauche / droite : d�place la pi�ce d'une case vers la gauche ou la droite.\n");
		msg.append(" - Fl�ches haut / bas : effectue une rotation antihoraire / horaire de la pi�ce.");
		
		JOptionPane.showMessageDialog(this, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
	}
}
