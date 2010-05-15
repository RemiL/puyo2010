package graphique;

import javax.swing.JFrame;

public class FenetrePrincipale extends JFrame
{
	private static final long serialVersionUID = 4690329553231316904L;
	
	public FenetrePrincipale(String title, int width, int height)
	{
		super(title);
		setSize(width, height);
	}
}
