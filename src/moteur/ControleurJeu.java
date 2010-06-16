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
		/** Le score rapport� par une �tape de destruction de blocs */
		private int score;
		/** Le code de retour correspondant � l'op�ration de translation */
		private int ret;
		
		/**
		 * Cr�e un nouveau timer non lanc�.
		 */
		public TimerChute()
		{
			super();
		}
		
		/**
		 * M�thode lan��e par le timer apr�s chaque p�riode d'attente
		 * pour faire descendre la pi�ce d'une case et effectu�e le
		 * cycle de v�rification permettant la d�tection de la d�faite
		 * et des blocs � d�truire.
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
					
					while ((score = partie.getPlateau().detruireBlocs()) != 0) // Tant qu'on a d�truit un bloc au moins
					{
						partie.ajoutCombo(); // On augmente le compteur de combo
						if (partie.ajoutScore(score)) // On ajoute le score
						{ // si la difficult� a �t� modifi�e par l'ajout du score, on l'applique.
							timerChute.cancel();
							timerChute = new Timer();
							timerChute.schedule(new TimerChute(), 0, 500 - partie.getDifficulte()*50);
						}
						// On met � jour le plateau et les infos de jeu.
						zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
						partie.getPlateau().faireChuterPuyos(); // On applique la gravit�.
						zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone());
						attente(200);
					}
					partie.resetCombo(); // On remet le compteur de combo � 0.
					// On met � jour le plateau et les infos de jeu.
					zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
					zoneDeJeu.chargerPiecesSuivantes(partie.chargerPieceSuivante()); // On charge la pi�ce suivante
				}
				else if(ret == Plateau.PERDU) // Si on a perdu
				{
					// On met � jour les infos de jeu.
					zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), true);
					timerChute.cancel(); // On arr�te le jeu
					
					verifierMeilleursScores(partie.getScore()); // On v�rifie les meilleurs scores.
					
					partie = new Partie(); // On pr�pare une nouvelle partie
				}
				else
					zoneDeJeu.chargerPlateau((Plateau) partie.getPlateau().clone()); // On met � jour l'affichage
			}
		}
		
		/**
		 * M�thode permettant de faire attendre le timer pendant ms millisecondes.
		 * @param ms le nombre de millisecondes � patienter.
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
		zoneDeJeu.chargerInfo(partie.getScore(), partie.getCombo(), partie.getDifficulte(), partie.estEnCours(), partie.estEnPause(), false);
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
		else if (partie.estEnCours() && e.getKeyCode() == KeyEvent.VK_ADD) // Augmente la difficult�
		{
			if (partie.augmenterDifficulte() && !partie.estEnPause())
			{ // On ne change le timer que si la difficult� a �t� vraiment modifi�e et si la partie n'est pas actuellement en pause
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
	
	/**
	 * M�thode permettant de charger les meilleurs scores � partir du fichier
	 * de sauvegarde des scores (PPMS.dat). Si le fichier n'existe pas, les
	 * meilleurs scores retourn�s sont vides.
	 * @return les meilleurs scores sauvegard�s
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
		{ // En cas d'�chec, on cr�e une nouvelle liste vide de meilleurs scores.
			meilleursScores = new MeilleursScores();
		}
		
		return meilleursScores;
	}
	
	/**
	 * M�thode permettant d'enregistrer les meilleurs scores dans le fichier
	 * de sauvegarde des scores (PPMS.dat).
	 * @param meilleursScores les meilleurs scores � sauvegarder.
	 */
	private void enregistrerMeilleursScores(MeilleursScores meilleursScores)
	{
		try
		{ // On essaie d'�crire la sauvegarde.
			ObjectOutputStream serialise = new ObjectOutputStream(new FileOutputStream("PPMS.dat"));
			serialise.writeObject(meilleursScores);
	        serialise.flush();
	        serialise.close();
		} catch (Exception e)
		{
			// En cas d'�chec, on ne fait rien ...
		}
	}
	
	/**
	 * M�thode permettant de v�rifier si le score fourni est un meilleur score.
	 * Si oui, l'utilisateur se voit demander son nom puis les meilleurs scores
	 * sont affich�s.
	 * @param score
	 */
	private void verifierMeilleursScores(int score)
	{
		// On charge la liste des meilleurs scores.
		MeilleursScores meilleursScores = chargerMeilleursScores();
		
		// Si on n'a moins de 10 meilleurs scores sauvegard�s ou si le nouveau score
		// est meilleur que le plus mauvais des meilleurs scores sauvegard�s.
		if (meilleursScores.size() < 10 || meilleursScores.firstKey() < score)
		{
			// On ajoute le nouveau meilleur score
			meilleursScores.ajout(score, fenetrePrincipale.demandeNom(score));
			// et on affiche la liste des meilleurs scores.
			fenetrePrincipale.afficheMeilleursScores(meilleursScores);
			
			// On sauvegarde la liste modifi�e.
			enregistrerMeilleursScores(meilleursScores);
		}
	}
}
