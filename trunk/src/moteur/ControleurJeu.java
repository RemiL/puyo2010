package moteur;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.GLCapabilities;

import graphique.FenetrePrincipale;
import graphique.ZoneDeJeu;

/**
 * Classe impl�mentant le contr�leur du jeu. Cette classe permet de maintenir
 * la coh�rence entre l'�tat de la partie et l'affichage graphique correspondant.
 * Elle sert �galement � traiter les actions utilisateur en �coutant les �v�nements
 * claviers et � effectuer la boucle principale du jeu qui consiste � faire tomber
 * la pi�ce.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class ControleurJeu extends KeyAdapter
{
	/** La partie en cours */
	private Partie partie;
	/** La fen�tre principale du jeu */
	private FenetrePrincipale fenetrePrincipale;
	/** La zone de jeu */
	private ZoneDeJeu zoneDeJeu;
	/** Le timer permettant de g�rer la chute des pi�ces dans le plateau */
	private Timer timerChute;
	
	/**
	 * Classe impl�mentant l'action du timer, c'est � dire la chute
	 * des pi�ces. Cette classe est utilis�e par le timer.
	 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
	 *
	 */
	private class TimerChute extends TimerTask
	{
		int score;
		int ret;
		
		public TimerChute()
		{
			super();
		}
		
		/**
		 * M�thode lan��e par le timer apr�s chaque p�riode d'attente
		 * pour faire descendre la pi�ce d'une case.
		 */
		public void run()
		{
			synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
			{
				ret = partie.getPlateau().translationVerticale(partie.getPieceCourante());
				
				if (ret == Plateau.PIECE_VIDE) // Si la pi�ce ne peut plus descendre
				{
					zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
					attente(200);
					
					while ((score = partie.getPlateau().detruireBlocs()) != 0)
					{
						partie.ajoutCombo();
						if (partie.ajoutScore(score))
						{
							timerChute.cancel();
							timerChute = new Timer();
							timerChute.schedule(new TimerChute(), 0, 500 - partie.getDifficulte()*50);
						}
						zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
						partie.getPlateau().faireChuterPuyos();
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
					}
					partie.resetCombo();
					zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante()); // On charge la suivante
				}
				else if(ret == Plateau.PERDU)
				{
					zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), true);
					timerChute.cancel();
					partie = new Partie();
				}
				else
					zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
			}
		}
		
		private void attente(long ms)
		{
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Cr�e un nouveau contr�leur de jeu qui met en place
	 * une interface graphique et la partie correspondante
	 * et qui �coute les �v�nements claviers.
	 */
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
	
	/**
	 * M�thode principale, cr�e un nouveau contr�leur de jeu.
	 * @param args
	 */
	public static void main(String[] args)
	{
		ControleurJeu jeu = new ControleurJeu();
	}
	
	/**
	 * M�thode appell�e lorsque l'utilisateur presse une touche, permet
	 * d'effectuer l'action correspondant � l'appui de l'utilisateur.
	 */
	public void keyPressed(KeyEvent e)
	{
		if (!partie.estEnCours())
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER) // Lancement de la partie
			{
				partie.commencerPartie();
				zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante());
				zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
				zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
				
				timerChute = new Timer();
				timerChute.schedule(new TimerChute(), 500, 500);
			}
		}
		else if (partie.estEnCours() && e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			timerChute.cancel();
			partie = new Partie();
			zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
			zoneDeJeu.chargerPiecesSuivantes(partie.getPiecesSuivantes());
		}
		else if (partie.estEnPause() && e.getKeyCode() == KeyEvent.VK_PAUSE) // Reprise d'une partie mise en pause
		{
			timerChute = new Timer();
			timerChute.schedule(new TimerChute(), 0, 500);
			partie.reprendrePartie();
			zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
			
		}
		else if (!partie.estEnPause() && e.getKeyCode() == KeyEvent.VK_PAUSE) // Mise en pause
		{
			timerChute.cancel();
			partie.mettreEnPause();
			zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(),partie.estEnPause(), false);
		}
		else if (!partie.estEnPause() && !partie.getPieceCourante().estCassee())
		{ // On ne peut effectuer les actions que si la partie n'est pas en pause et que la pi�ce n'est pas cass�e.
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT: // fl�che gauche --> translation de la pi�ce vers la gauche
					synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
					{
						partie.getPlateau().translationHorizontale(Plateau.GAUCHE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
					}
				break;
				case KeyEvent.VK_RIGHT: // fl�che droite --> translation de la pi�ce vers la droite
					synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
					{
						partie.getPlateau().translationHorizontale(Plateau.DROITE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
					}
				break;
				case KeyEvent.VK_DOWN: // fl�che bas --> rotation dans le sens horaire
					synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
					{
						partie.getPlateau().rotation(Plateau.HORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
					}
				break;
				case KeyEvent.VK_UP: // fl�che haut --> rotation dans le sens antihoraire
					synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
					{
						partie.getPlateau().rotation(Plateau.ANTIHORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
					}
				break;
				case KeyEvent.VK_SPACE: // espace --> acc�l�rer la chute de la pi�ce
					synchronized (partie.getPlateau()) // On verrouille le plateau pour �tre s�r de ne pas avoir d'acc�s concurrent.
					{
						partie.getPlateau().translationVerticale(partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
					}
				break;
			}
		}
	}
}
