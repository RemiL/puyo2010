package graphique;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.LinkedList;
import java.util.Map;
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

	public String demandeNom()
	{
		String nom;
		
		do {
			nom = JOptionPane.showInputDialog(this, "Félicitation, vous venez de faire un nouveau meilleur score !\nEntrez votre nom :", "Nouveau meilleur score", JOptionPane.QUESTION_MESSAGE);
		} while (nom == null);
		
		return nom;
	}
	
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
