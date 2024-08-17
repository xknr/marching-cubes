package xknr.march.algo;

import java.util.List;

public abstract class IMarchingSquares 
{
	public abstract void execute(ScalarField field, float thres);
	
	public ArrListI lineIndexData = new ArrListI(10);
	public ArrListF vertexData = new ArrListF(10);
	public ArrListI triIndexData;
	//public INeighData neighData;
	public INeighData2 neighData;
	
	public List<Integer> lineStripStarts;

	public static final int NONE = -1;
}
