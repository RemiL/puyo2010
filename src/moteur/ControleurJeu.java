package moteur;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.GLCapabilities;

import graphique.FenetrePrincipale;
import graphique.ZoneDeJeu;

public class ControleurJeu extends KeyAdapter
{
	private Partie partie;
	private FenetrePrincipale fenetrePrincipale;
	private ZoneDeJeu zoneDeJeu;
	private Timer timerChute;
		
	private class TimerChute extends TimerTask
	{
		public TimerChute()
		{
			super();
		}
		
		public void run()
		{
			synchronized (partie.getPlateau())
			{
				if (!partie.getPlateau().translationVerticale(partie.getPieceCourante()))
					zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante());
				zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
			}
		}
		
	}
	
	public ControleurJeu()
	{
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setDoubleBuffered(true);
		// Antialiasing FSAA 4x
		capabilities.setNumSamples(4);
		capabilities.setSampleBuffers(true);
		
		zoneDeJeu = new ZoneDeJeu(capabilities, 800, 600);
		fenetrePrincipale = new FenetrePrincipale("Puyo Puyo 2010", 800, 628, zoneDeJeu);
		zoneDeJeu.addKeyListener(this);
		
		partie = new Partie();
		zoneDeJeu.chargerPiecesSuivantes(partie.getPiecesSuivantes());
	}
	
	public static void main(String[] args)
	{
		ControleurJeu jeu = new ControleurJeu();
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (!partie.estEnCours())
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				partie.commencerPartie();
				zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante());
				
				timerChute = new Timer();
				timerChute.schedule(new TimerChute(), 500, 500);
			}
		}
		else if (partie.estEnPause() && e.getKeyCode() == KeyEvent.VK_PAUSE)
		{
			timerChute = new Timer();
			timerChute.schedule(new TimerChute(), 0, 500);
			partie.reprendrePartie();
		}
		else if (!partie.estEnPause() && e.getKeyCode() == KeyEvent.VK_PAUSE)
		{
			timerChute.cancel();
			partie.mettreEnPause();
		}
		else if (!partie.estEnPause() && !partie.getPieceCourante().estCassee())
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT: // flèche gauche
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().translationHorizontale(Plateau.GAUCHE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_RIGHT: // flèche droite
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().translationHorizontale(Plateau.DROITE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_DOWN: // flèche bas
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().rotation(Plateau.HORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_UP: // flèche haut
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().rotation(Plateau.ANTIHORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_SPACE: // espace
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().translationVerticale(partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
			}
		}
	}
}
