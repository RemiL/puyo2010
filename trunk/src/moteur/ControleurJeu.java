package moteur;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		/** Le score rapporté par une étape de destruction de blocs */
		private int score;
		/** Le code de retour correspondant à l'opération de translation */
		private int ret;
		
		/**
		 * Crée un nouveau timer non lancé.
		 */
		public TimerChute()
		{
			super();
		}
		
		/**
		 * Méthode lançée par le timer après chaque période d'attente
		 * pour faire descendre la pièce d'une case et effectuée le
		 * cycle de vérification permettant la détection de la défaite
		 * et des blocs à détruire.
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
					
					while ((score = partie.getPlateau().detruireBlocs()) != 0) // Tant qu'on a détruit un bloc au moins
					{
						partie.ajoutCombo(); // On augmente le compteur de combo
						if (partie.ajoutScore(score)) // On ajoute le score
						{ // si la difficulté a été modifiée par l'ajout du score, on l'applique.
							timerChute.cancel();
							timerChute = new Timer();
							timerChute.schedule(new TimerChute(), 0, 500 - partie.getDifficulte()*50);
						}
						// On met à jour le plateau et les infos de jeu.
						zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
						partie.getPlateau().faireChuterPuyos(); // On applique la gravité.
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
					}
					partie.resetCombo(); // On remet le compteur de combo à 0.
					// On met à jour le plateau et les infos de jeu.
					zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
					zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante()); // On charge la pièce suivante
				}
				else if(ret == Plateau.PERDU) // Si on a perdu
				{
					// On met à jour les infos de jeu.
					zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), true);
					timerChute.cancel(); // On arrête le jeu
					
					verifierMeilleursScores(partie.getScore()); // On vérifie les meilleurs scores.
					
					partie = new Partie(); // On prépare une nouvelle partie
				}
				else
					zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met à jour l'affichage
			}
		}
		
		/**
		 * Méthode permettant de faire attendre le timer pendant ms millisecondes.
		 * @param ms le nombre de millisecondes à patienter.
		 */
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
		zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
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
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) // On quitte l'application
		{
			fenetrePrincipale.dispose();
			System.exit(0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_F2 || e.getKeyCode() == KeyEvent.VK_M || e.getKeyCode() == KeyEvent.VK_H) // Affichage des meilleurs scores
		{
			if (partie.estEnCours() && !partie.estEnPause()) // On met la partie en pause si elle est en cours.
			{
				timerChute.cancel();
				partie.mettreEnPause();
				zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(),partie.estEnPause(), false);
			}
			
			fenetrePrincipale.afficheMeilleursScores(chargerMeilleursScores());
		}
		else if (e.getKeyCode() == KeyEvent.VK_F1) // Affichage de l'aide
		{
			if (partie.estEnCours() && !partie.estEnPause()) // On met la partie en pause si elle est en cours.
			{
				timerChute.cancel();
				partie.mettreEnPause();
				zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(),partie.estEnPause(), false);
			}
			
			fenetrePrincipale.afficheAide();
		}
		else if (!partie.estEnCours())
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
		else if (partie.estEnCours() && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) // Commencer une nouvelle partie
		{
			timerChute.cancel();
			partie = new Partie();
			zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
			zoneDeJeu.chargerPiecesSuivantes(partie.getPiecesSuivantes());
			zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
		}
		else if (partie.estEnCours() && e.getKeyCode() == KeyEvent.VK_ADD) // Augmente la difficulté
		{
			if (partie.augmenterDifficulte() && !partie.estEnPause())
			{ // On ne change le timer que si la difficulté a été vraiment modifiée et si la partie n'est pas actuellement en pause
				timerChute.cancel();
				timerChute = new Timer();
				timerChute.schedule(new TimerChute(), 0, 500 - partie.getDifficulte()*50);
			}
			zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
		}
		else if (partie.estEnPause() && e.getKeyCode() == KeyEvent.VK_PAUSE) // Reprise d'une partie mise en pause
		{
			timerChute = new Timer();
			timerChute.schedule(new TimerChute(), 0, 500 - partie.getDifficulte()*50);
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
	
	/**
	 * Méthode permettant de charger les meilleurs scores à partir du fichier
	 * de sauvegarde des scores (PPMS.dat). Si le fichier n'existe pas, les
	 * meilleurs scores retournés sont vides.
	 * @return les meilleurs scores sauvegardés
	 */
	private MeilleursScores chargerMeilleursScores()
	{
		MeilleursScores meilleursScores;
		try
		{ // On essaie de lire la sauvegarde
			ObjectInputStream deserialise = new ObjectInputStream(new FileInputStream("PPMS.dat"));
			meilleursScores = (MeilleursScores) deserialise.readObject();
			deserialise.close();
		} catch (Exception e)
		{ // En cas d'échec, on crée une nouvelle liste vide de meilleurs scores.
			meilleursScores = new MeilleursScores();
		}
		
		return meilleursScores;
	}
	
	/**
	 * Méthode permettant d'enregistrer les meilleurs scores dans le fichier
	 * de sauvegarde des scores (PPMS.dat).
	 * @param meilleursScores les meilleurs scores à sauvegarder.
	 */
	private void enregistrerMeilleursScores(MeilleursScores meilleursScores)
	{
		try
		{ // On essaie d'écrire la sauvegarde.
			ObjectOutputStream serialise = new ObjectOutputStream(new FileOutputStream("PPMS.dat"));
			serialise.writeObject(meilleursScores);
	        serialise.flush();
	        serialise.close();
		} catch (Exception e)
		{
			// En cas d'échec, on ne fait rien ...
		}
	}
	
	/**
	 * Méthode permettant de vérifier si le score fourni est un meilleur score.
	 * Si oui, l'utilisateur se voit demander son nom puis les meilleurs scores
	 * sont affichés.
	 * @param score
	 */
	private void verifierMeilleursScores(int score)
	{
		// On charge la liste des meilleurs scores.
		MeilleursScores meilleursScores = chargerMeilleursScores();
		
		// Si on n'a moins de 10 meilleurs scores sauvegardés ou si le nouveau score
		// est meilleur que le plus mauvais des meilleurs scores sauvegardés.
		if (meilleursScores.size() < 10 || meilleursScores.firstKey() < score)
		{
			// On ajoute le nouveau meilleur score
			meilleursScores.ajout(score, fenetrePrincipale.demandeNom(score));
			// et on affiche la liste des meilleurs scores.
			fenetrePrincipale.afficheMeilleursScores(meilleursScores);
			
			// On sauvegarde la liste modifiée.
			enregistrerMeilleursScores(meilleursScores);
		}
	}
}
