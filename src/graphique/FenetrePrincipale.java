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
	 * @return la chaine de caract�res entr�e par l'utilisateur, �ventuellement vide.
	 */
	public String demandeNom()
	{
		String nom;
		
		do {
			nom = JOptionPane.showInputDialog(this, "F�licitation, vous venez de faire un nouveau meilleur score !\nEntrez votre nom :", "Nouveau meilleur score", JOptionPane.QUESTION_MESSAGE);
		} while (nom == null);
		
		return nom;
	}
	
	/**
	 * Cr�e et affiche une boite de dialogue contenant la liste des meilleurs
	 * scores pass�e en param�tre.
	 * @param meilleursScores la liste des meilleurs scores � afficher.
	 */
	public void afficheMeilleuresScores(MeilleursScores meilleursScores)
	{
		StringBuilder msg = new StringBuilder();
		int i = meilleursScores.size();
		
		for (Entry<Integer, LinkedList<String>> entree : meilleursScores.entrySet())
		{
			for (String nom : entree.getValue())
			{
				msg.insert(0, i+") "+nom+" : "+entree.getKey()+"\n");
				i--;
			}
		}
		
		msg.insert(0, "Meilleurs scores :\n\n");
		
		JOptionPane.showMessageDialog(this, msg.toString(), "Meilleurs scores", JOptionPane.INFORMATION_MESSAGE);
	}
}
