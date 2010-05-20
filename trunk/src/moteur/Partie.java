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
	
	public Partie()
	{
		plateau = new Plateau();
		prochainesPieces = new LinkedBlockingQueue<Piece>(2);
		score = 0;
		difficulte = 0;
		combo = 0;
		
		pieceCourante = new Piece();
		plateau.ajouter(pieceCourante);
		
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

	public void chargerPieceSuivante()
	{
		pieceCourante = prochainesPieces.poll();
		plateau.ajouter(pieceCourante);
		prochainesPieces.add(new Piece());
	}
}
