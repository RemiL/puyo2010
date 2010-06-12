package moteur;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.GLCapabilities;

import graphique.FenetrePrincipale;
import graphique.ZoneDeJeu;

/**
 * Classe implémentant le contrôleur du jeu. Cette classe permet de maintenir
 * la cohérence entre l'état de la partie et l'affichage graphique correspondant.
 * Elle sert également à traiter les actions utilisateur en écoutant les événements
 * claviers et à effectuer la boucle principale du jeu qui consiste à faire tomber
 * la pièce.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class ControleurJeu extends KeyAdapter
{
	/** La partie en cours */
	private Partie partie;
	/** La fenêtre principale du jeu */
	private FenetrePrincipale fenetrePrincipale;
	/** La zone de jeu */
	private ZoneDeJeu zoneDeJeu;
	/** Le timer permettant de gérer la chute des pièces dans le plateau */
	private Timer timerChute;
	
	/**
	 * Classe implémentant l'action du timer, c'est à dire la chute
	 * des pièces. Cette classe est utilisée par le timer.
	 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
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
		 * Méthode lançée par le timer après chaque période d'attente
		 * pour faire descendre la pièce d'une case.
		 */
		public void run()
		{
			synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
			{
				ret = partie.getPlateau().translationVerticale(partie.getPieceCourante());
				
				if (ret == Plateau.PIECE_VIDE) // Si la pièce ne peut plus descendre
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
					zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
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
	 * Crée un nouveau contrôleur de jeu qui met en place
	 * une interface graphique et la partie correspondante
	 * et qui écoute les événements claviers.
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
	 * Méthode principale, crée un nouveau contrôleur de jeu.
	 * @param args
	 */
	public static void main(String[] args)
	{
		ControleurJeu jeu = new ControleurJeu();
	}
	
	/**
	 * Méthode appellée lorsque l'utilisateur presse une touche, permet
	 * d'effectuer l'action correspondant à l'appui de l'utilisateur.
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
		{ // On ne peut effectuer les actions que si la partie n'est pas en pause et que la pièce n'est pas cassée.
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT: // flèche gauche --> translation de la pièce vers la gauche
					synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
					{
						partie.getPlateau().translationHorizontale(Plateau.GAUCHE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
					}
				break;
				case KeyEvent.VK_RIGHT: // flèche droite --> translation de la pièce vers la droite
					synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
					{
						partie.getPlateau().translationHorizontale(Plateau.DROITE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
					}
				break;
				case KeyEvent.VK_DOWN: // flèche bas --> rotation dans le sens horaire
					synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
					{
						partie.getPlateau().rotation(Plateau.HORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
					}
				break;
				case KeyEvent.VK_UP: // flèche haut --> rotation dans le sens antihoraire
					synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
					{
						partie.getPlateau().rotation(Plateau.ANTIHORAIRE, partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
					}
				break;
				case KeyEvent.VK_SPACE: // espace --> accélérer la chute de la pièce
					synchronized (partie.getPlateau()) // On verrouille le plateau pour être sûr de ne pas avoir d'accès concurrent.
					{
						partie.getPlateau().translationVerticale(partie.getPieceCourante());
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
					}
				break;
			}
		}
	}
}
