package moteur;

import java.awt.Color;
import java.util.Random;

public class Puyo
{
	private static final Color[] couleurs = 
	{
		Color.MAGENTA,
		Color.RED,
		Color.GREEN,
		Color.BLACK
	};
	private Color couleur;
	private static Random rand = new Random();
	public static final int DROITE = 0;
	public static final int HAUT = 1;
	private boolean[] liens;
	
	public Puyo()
	{
		this(couleurs[rand.nextInt(couleurs.length)]);
	}
	
	public Puyo(Color couleur)
	{
		this.couleur = couleur;
		
		liens = new boolean[4];
		liens[DROITE] = liens[HAUT] = false;
	}
	
	public Color getCouleur()
	{
		return couleur;
	}
	
	public boolean getLien(int lien)
	{
		return liens[lien];
	}
	
	public void setLien(int lien)
	{
		liens[lien] = true;
	}
	
	public boolean equals(Object obj)
	{
		boolean egaux = false;
		
		if (obj instanceof Puyo)
		{
			Puyo puyo = (Puyo) obj;
			egaux = couleur.equals(puyo.getCouleur());
		}
		
		return egaux;
	}
}
