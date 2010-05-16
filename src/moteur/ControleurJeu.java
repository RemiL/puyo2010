package moteur;

import javax.media.opengl.GLCapabilities;

import graphique.FenetrePrincipale;
import graphique.ZoneDeJeu;

public class ControleurJeu
{
	public static void main(String[] args) throws InterruptedException
	{
		Plateau plateau = new Plateau();
		Piece piece = new Piece();
		
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setDoubleBuffered(true);
		// Antialiasing FSAA 4x
		capabilities.setNumSamples(4);
		capabilities.setSampleBuffers(true);
		
		ZoneDeJeu zdj = new ZoneDeJeu(capabilities, 400, 500);
		new FenetrePrincipale("Puyo Puyo 2010", 500, 600, zdj);
		
		plateau.rafraichir(null, piece);
		
		plateau.translationVerticale(piece);
		plateau.translationVerticale(piece);
		plateau.translationVerticale(piece);
		zdj.chargerPlateau((Plateau) plateau.clone());
		Thread.sleep(1000);
		
		plateau.translationHorizontale(Plateau.DROITE, piece);
		zdj.chargerPlateau((Plateau) plateau.clone());
		Thread.sleep(1000);
		
		plateau.rotation(Plateau.HORAIRE, piece);
		zdj.chargerPlateau((Plateau) plateau.clone());
		Thread.sleep(1000);
		
		plateau.rotation(Plateau.HORAIRE, piece);
		zdj.chargerPlateau((Plateau) plateau.clone());
	}	
}
