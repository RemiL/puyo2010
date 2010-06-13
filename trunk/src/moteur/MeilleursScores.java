package moteur;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Classe permettant de stocker les dix meilleurs scores enregistrés pour
 * le jeu. Elle dérive d'une table de hashage triée selon la valeur des
 * clés. Chaque clé est un score et à chaque score correspond une liste
 * de noms de joueurs ayant atteint ce score.
 * La méthode d'ajout s'assure que le nombre total de meilleurs scores
 * enregistrés ne dépasse jamais 10.
 * @author Rémi Lacroix & Marie Nivet & Nicolas Poirier
 *
 */
public class MeilleursScores extends TreeMap<Integer, LinkedList<String>>
{
	private static final long serialVersionUID = -8934212542621817508L;
	
	/**
	 * Crée une nouvelle liste de meilleurs scores vide.
	 */
	public MeilleursScores()
	{
		super();
	}
	
	/**
	 * Retourne le nombre de meilleurs scores actuellement enregistrés
	 * dans la liste.
	 */
	public int size()
	{
		int size = 0;
		
		for (LinkedList<String> list : this.values())
			size += list.size(); // Chaque clé n'est pas reliée à un seul meilleur score
		
		return size;
	}
	
	/**
	 * Ajoute le score fourni à la liste des meilleurs scores.
	 * S'il y a déjà 10 meilleurs scores enregistrés, le plus
	 * faible est enlevé. Cette méthode ne vérifie pas que le
	 * score passé soit réellement un meilleur score.
	 * @param score le score à ajouter.
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
			put(score, new LinkedList<String>()); // on crée la liste correspondante
		get(score).add(nom); // et on ajoute le nom de l'utilisateur.
	}
}
