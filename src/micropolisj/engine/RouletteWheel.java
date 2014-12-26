package dragonfin.contest;

import java.util.*;

public class RouletteWheel<T>
{
	class Entry<T>
	{
		T item;
		double fitnessStart;

		Entry(T item, double fitnessStart)
		{
			this.item = item;
			this.fitnessStart = fitnessStart;
		}
	}
	double fitnessSum;
	List< Entry<T> > entries;
	Random R = new Random();

	public RouletteWheel()
	{
		this.fitnessSum = 0;
		this.entries = new ArrayList< Entry<T> >();
	}

	public RouletteWheel(Random R)
	{
		this();
		this.R = R;
	}

	public void add(T item, double fitness)
	{
		assert fitness > 0;
		if (fitness > 0)
		{
			entries.add(new Entry<T>(item, fitnessSum));
			fitnessSum += fitness;
		}
	}

	int pick()
	{
		double r = R.nextDouble()*fitnessSum;
		int a = 0;
		int b = entries.size();
		while (a + 1 < b)
		{
			int i = (a+b)/2;
			Entry<T> e = entries.get(i);
			if (r < e.fitnessStart)
				b = i;
			else
				a = i;
		}
		return a;
	}

	public T next()
	{
		return entries.get(pick()).item;
	}

	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	public T remove()
	{
		int i = pick();
		Entry<T> e = entries.remove(i);
		double eFit = (i < entries.size() ? entries.get(i).fitnessStart : fitnessSum) - e.fitnessStart;

		for (int j = i; j < entries.size(); j++)
		{
			entries.get(i).fitnessStart -= eFit;
		}
		fitnessSum -= eFit;

		return e.item;
	}
}
