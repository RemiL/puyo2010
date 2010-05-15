package moteur;

import javax.media.opengl.GLCapabilities;

import graphique.FenetrePrincipale;
import graphique.ZoneDeJeu;

public class ControleurJeu
{
	public static void main(String[] args)
	{
		Plateau plateau = new Plateau();
		Piece piece = new Piece();
		
		System.out.println(plateau);
		System.out.println(piece+"\n");
		
		plateau.rafraichir(null, piece);
		
		System.out.println(plateau);
		
		plateau.translationHorizontale(Plateau.GAUCHE, piece);
		plateau.translationHorizontale(Plateau.GAUCHE, piece);
		System.out.println(plateau);
		System.out.println(piece+"\n");
		plateau.translationVerticale(piece);plateau.translationVerticale(piece);
		plateau.translationVerticale(piece);plateau.translationVerticale(piece);
		System.out.println(plateau);
		System.out.println(piece+"\n");
		plateau.rotation(Plateau.HORAIRE, piece);
		System.out.println(plateau);
		System.out.println(piece+"\n");
		
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setDoubleBuffered(true);
		new FenetrePrincipale("Puyo Puyo 2010", 500, 600, new ZoneDeJeu(capabilities, 400, 500));
	}	
}
