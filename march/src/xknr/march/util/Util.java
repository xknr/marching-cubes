package xknr.march.util;

import java.util.ArrayList;

public class Util {

	public static void ensureSize(ArrayList<Integer> li, int req, int fillValue) 
	{
		li.ensureCapacity(req);
		
		int need = req - li.size();
		
		for(int i = 0; i < need; i++)
		{
			li.add(fillValue);
		}
	}

}
