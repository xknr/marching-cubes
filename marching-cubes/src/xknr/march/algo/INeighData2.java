package xknr.march.algo;

public interface INeighData2 
{
	int getParent(int child);
	int getChild(int parent);
	void addEdge(int from, int to);
}
 