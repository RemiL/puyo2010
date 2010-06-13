package moteur;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Classe permettant de stocker les dix meilleurs scores enregistr�s pour
 * le jeu. Elle d�rive d'une table de hashage tri�e selon la valeur des
 * cl�s. Chaque cl� est un score et � chaque score correspond une liste
 * de noms de joueurs ayant atteint ce score.
 * La m�thode d'ajout s'assure que le nombre total de meilleurs scores
 * enregistr�s ne d�passe jamais 10.
 * @author R�mi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class MeilleursScores extends TreeMap<Integer, LinkedList<String>>
{
	private static final long serialVersionUID = -8934212542621817508L;
	
	/**
	 * Cr�e une nouvelle liste de meilleurs scores vide.
	 */
	public MeilleursScores()
	{
		super();
	}
	
	/**
	 * Retourne le nombre de meilleurs scores actuellement enregistr�s
	 * dans la liste.
	 */
	public int size()
	{
		int size = 0;
		
		for (LinkedList<String> list : this.values())
			size += list.size(); // Chaque cl� n'est pas reli�e � un seul meilleur score
		
		return size;
	}
	
	/**
	 * Ajoute le score fourni � la liste des meilleurs scores.
	 * S'il y a d�j� 10 meilleurs scores enregistr�s, le plus
	 * faible est enlev�. Cette m�thode ne v�rifie pas que le
	 * score pass� soit r�ellement un meilleur score.
	 * @param score le score � ajouter.
	 * @param nom le nom du joueur qui a atteint ce score.
	 */
	public void ajout(int score, String nom)
	{
		if (size() >= 10) // Si on atteint la limite
		{ // On supprime le score le plus faible
			if (firstEntry().getValue().size() == 1)
				remove(firstKey());
			else // (le plus ancien des plus faibles s'il y en a plusieurs)
				firstEntry().getValue().removeFirst();
		}
		
		if (!containsKey(score)) // Si on avait pas encore ce score,
			put(score, new LinkedList<String>()); // on cr�e la liste correspondante
		get(score).add(nom); // et on ajoute le nom de l'utilisateur.
	}
}
