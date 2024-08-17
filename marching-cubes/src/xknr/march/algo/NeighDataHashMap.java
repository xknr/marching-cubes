package xknr.march.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeighDataHashMap implements INeighData 
{
	public NeighDataHashMap() {
		m = new HashMap<Integer, List<Integer>>();
	}
	
	@Override
	public int otherNeighbor(int ind1, int ind2) 
	{
		List<Integer> entry = m.get(ind1);
		if (entry == null)
			throw new RuntimeException(String.format("ind1 %d has no entry", ind1));
		for(int v : entry) {
			if (v != ind2)
				return v;
		}
		return -1;
	}
	
	@Override
	public void addNeighbor(int index0, int index1)
	{
		List<Integer> entry = m.get(index0);
		if (entry == null) {
			entry = new ArrayList<Integer>();
			m.put(index0, entry);
		}
		if (entry.size() >= 2)
			throw new RuntimeException("cannot add more than 2 neighbors");
		entry.add(index1);
		
//		if (n1.size() <= index0 || n1.get(index0) == -1) {
//			while(n1.size() <= index0)
//				n1.add(-1);			
//			n1.set(index0, index1);
//		} else {
//			while(n2.size() <= index0)
//				n2.add(-1);			
//			n2.set(index0, index1);
//		}
	}

	private Map<Integer, List<Integer>> m;
}