package xknr.march.algo;

import java.util.ArrayList;
import java.util.List;

import xknr.march.util.Util;

public class NeighData2 implements INeighData2 
{
	public NeighData2() {
	}

	@Override
	public int getParent(int child) {
		Integer result = parents.get(child);
		return result == null ? -1 : result;
	}

	@Override
	public int getChild(int parent) {
		Integer result = children.get(parent);
		return result == null ? -1 : result;
	}

	@Override
	public void addEdge(int from, int to) 
	{
		int req = Math.max(from, to) + 1;
		
		Util.ensureSize(parents, req, -1);
		Util.ensureSize(children, req, -1);

		parents.set(to, from);
		children.set(from, to);
	}

	private ArrayList<Integer> parents = new ArrayList<Integer>();
	private ArrayList<Integer> children = new ArrayList<Integer>();
	
}