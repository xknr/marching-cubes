package xknr.march.demo;

import java.util.List;

import xknr.march.algo.ArrListF;
import xknr.march.algo.ArrListI;
import xknr.march.algo.INeighData;
import xknr.march.algo.INeighData2;

public class SceneData 
{	
	public SceneData(ArrListI lineIndexData, 
			ArrListF vertexData, 
			ArrListI triIndexData, 
			//INeighData neigh
			INeighData2 neigh,
			List<Integer> lineStripStarts
			) {
		this.lineIndexData = lineIndexData;
		this.vertexData = vertexData;
		this.triIndexData = triIndexData;
		this.neigh = neigh;
		
		this.lineStripStarts = lineStripStarts;
	}
	
	public ArrListF vertexData;
	public ArrListI lineIndexData;
	
	public ArrListI triIndexData;
	//public INeighData neigh;
	public INeighData2 neigh;

	public List<Integer> lineStripStarts;
	
}