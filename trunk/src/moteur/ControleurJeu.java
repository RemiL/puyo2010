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
					partie.chargerPieceSuivante();
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
		
		zoneDeJeu = new ZoneDeJeu(capabilities, 400, 500);
		fenetrePrincipale = new FenetrePrincipale("Puyo Puyo 2010", 500, 600, zoneDeJeu);
		zoneDeJeu.addKeyListener(this);
		
		partie = new Partie();
		
		timerChute = new Timer();
		timerChute.schedule(new TimerChute(), 500, 500);
	}
	
	public static void main(String[] args)
	{
		ControleurJeu jeu = new ControleurJeu();
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (!partie.getPieceCourante().estCassee())
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT: // fl�che gauche
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().translationHorizontale(Plateau.GAUCHE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_RIGHT: // fl�che droite
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().translationHorizontale(Plateau.DROITE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_DOWN: // fl�che bas
					synchronized (partie.getPlateau())
					{
						partie.getPlateau().rotation(Plateau.HORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					}
				break;
				case KeyEvent.VK_UP: // fl�che haut
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
