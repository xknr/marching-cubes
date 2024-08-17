package xknr.march.algo;

import java.util.Arrays;

public class NeighData2B implements INeighData2 
{
	public NeighData2B() { }

	@Override
	public int getParent(int child) {
		return parents[child];
	}

	@Override
	public int getChild(int parent) {
		return children[parent];
	}

//	int misscount = 0;
	
	@Override
	public void addEdge(int from, int to) 
	{
//		if (parents[to] != -1) {
////			System.out.format("addEdge(%d, %d), parents[%d] = %d, children[%d] = %d\n", from, to, 
////					to, parents[to], 
////					from, children[from]);
//			misscount++;
//		}

		if (parents[to] != -1 || children[from] != -1) {
			String msg = String.format("addEdge(%d, %d), parents[%d] = %d, children[%d] = %d\n", from, to, to, parents[to], from, children[from]);
			throw new RuntimeException(msg);
		}
		
		parents[to] = from;
		children[from] = to;
	}

	public NeighData2B alloc(int size, int defValue) {
		parents = new int[size];
		children = new int[size];
		Arrays.fill(parents, defValue);
		Arrays.fill(children, defValue);
		return this;
	}

	public int parents[];
	public int children[];
}
