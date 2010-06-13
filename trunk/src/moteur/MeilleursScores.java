package moteur;

import java.util.LinkedList;
import java.util.TreeMap;

public class MeilleursScores extends TreeMap<Integer, LinkedList<String>>
{
	private static final long serialVersionUID = -8934212542621817508L;

	public MeilleursScores()
	{
		super();
	}
	
	public int size()
	{
		int size = 0;
		
		for (LinkedList<String> list : this.values())
			size += list.size();
		
		return size;
	}

	public void ajout(int score, String string)
	{
		if (size() >= 10)
		{
			if (firstEntry().getValue().size() == 1)
				remove(firstKey());
			else
				firstEntry().getValue().removeFirst();
		}
		
		if (!containsKey(score))
			put(score, new LinkedList<String>());
		get(score).add(string);
	}
}
