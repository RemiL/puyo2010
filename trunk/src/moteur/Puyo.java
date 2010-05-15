package moteur;

import java.awt.Color;
import java.util.Random;

public class Puyo
{
	private static final Color[] couleurs = 
	{
		Color.CYAN,
		Color.YELLOW,
		Color.GREEN,
		Color.MAGENTA
	};
	private Color couleur;
	private static Random rand = new Random();
	
	public Puyo()
	{
		this(couleurs[rand.nextInt(couleurs.length)]);
	}
	
	public Puyo(Color couleur)
	{
		this.couleur = couleur;
	}
	
	public Color getColor()
	{
		return couleur;
	}
}
