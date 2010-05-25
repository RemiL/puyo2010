package graphique;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

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
}
