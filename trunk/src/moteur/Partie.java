package moteur;

import java.util.concurrent.LinkedBlockingQueue;

public class Partie
{
	private Plateau plateau;
	private LinkedBlockingQueue<Piece> prochainesPieces;
	private Piece pieceCourante;
	private int score;
	private int difficulte;
	private int combo;
	private boolean start, pause;
	
	public Partie()
	{
		plateau = new Plateau();
		prochainesPieces = new LinkedBlockingQueue<Piece>(2);
		score = 0;
		difficulte = 0;
		combo = 0;
		start = false;
		pause = false;
		
		prochainesPieces.add(new Piece());
		prochainesPieces.add(new Piece());
	}
	
	public Plateau getPlateau()
	{
		return plateau;
	}
	
	public Piece getPieceCourante()
	{
		return pieceCourante;
	}

	public Piece[] chargerPieceSuivante()
	{
		pieceCourante = prochainesPieces.poll();
		plateau.ajouter(pieceCourante);
		prochainesPieces.add(new Piece());
		
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	public Piece[] getPiecesSuivantes()
	{
		return prochainesPieces.toArray(new Piece[2]);
	}
	
	public void commencerPartie()
	{
		start = true;
	}
	
	public boolean estEnCours()
	{
		return start;
	}
	
	public void mettreEnPause()
	{
		pause = true;
	}
	
	public void reprendrePartie()
	{
		pause = false;
	}
	
	public boolean estEnPause()
	{
		return pause;
	}
}
