package moteur;

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
		System.out.println(plateau);
		System.out.println(piece+"\n");
		plateau.translationVerticale(piece);
		System.out.println(plateau);
		System.out.println(piece+"\n");
	}	
}
