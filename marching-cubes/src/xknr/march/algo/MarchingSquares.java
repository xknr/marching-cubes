package xknr.march.algo;

import java.util.ArrayList;

import xknr.march.demo.MSConf;


public class MarchingSquares extends MarchingSquaresSimple
{
	/*
	 * 
	 * When noiseDetail(1) is one (one octave), noise(x, y) generates
	 * values in [0, 0.5] (inclusive?) 
	 * 
	 * The enclosed area is meant to be denser samples.
	 * Hence, it gets "false" when value < threshold. 
	 */
	
	// 0, 1 -> 0
	// 1, 0 -> 1
	// 2, 1 -> 2
	// 1, 2 -> 3
	
	//    1 
	//  0   2
	//    3
	
	// 0 1 2      1
	// 3   5    3   5
	// 6 7 8      7
	
	public MarchingSquares(MSConf conf) {
		this.conf = conf;
	}
	
	@Override
	public void execute(ScalarField field, float thres) 
	{		
		// For storing output:
		lineIndexData.clear();
		vertexData.clear();
		
		if (triIndexData == null) 
			triIndexData = new ArrListI(10);
		triIndexData.clear();
		
			
		int[] verts = new int[9];

		float[] vs = new float[4];
		
		int[] prev1 = new int[field.cols - 1];
		for (int i = 0; i < prev1.length; i++)
			prev1[i] = NONE;

		int[] prev2 = new int[field.cols - 1];
		for (int i = 0; i < prev2.length; i++)
			prev2[i] = NONE;

		for (int r = 0; r < field.rows - 1; r++) 
		{
			int prev3 = NONE;
			int prev0 = NONE, prev6 = NONE;

			for (int c = 0; c < field.cols - 1; c++) 
			{
				final float v00 = vs[0] = field.values[r][c];
				final float v10 = vs[1] = field.values[r][c + 1];
				final float v01 = vs[2] = field.values[r + 1][c];
				final float v11 = vs[3] = field.values[r + 1][c + 1];

				int code = calcCode(v00, v10, v01, v11, thres);

				int[] lineList = edgeTable[code];

				// Mark all vertices as not calculated.
				for (int i = 0; i < verts.length; i++)
					verts[i] = NONE;

				if (prev3 != NONE)
					verts[3] = prev3;

				if (prev1[c] != NONE)
					verts[1] = prev1[c];

				for (int i = 0; i < lineList.length; i++)
				{
					int vType = lineList[i];

					if (verts[vType] == NONE) // If this vertex has not been calculated yet. 
					{
						final int x = vType % 3, y = vType / 3;
						final float fx = interpolateX(x, y, vs, thres);
						final float fy = interpolateY(x, y, vs, thres);
						verts[vType] = addNewVertex(vertexData, c + fx, r + fy);						
					}


					if (conf.drawLines) 
						lineIndexData.add(verts[vType]);
				}

				if (conf.drawTris) 
				{
					if (!conf.drawFullQuads || code != 15)
					{
						// Fill in corner coordinates only when used:
						// These bits indicate whether
						// the corner is used as a vertex in
						// one of the triangles.
						// Corners are "contained" if encoded as 1.

						if ((code & 1) != 0) {
							set0(verts, r, c, prev0);
						}
						
						if ((code & 2) != 0) {
							set2(verts, r, c, prev2);
						}
						
						if ((code & 4) != 0) {
							set6(verts, r, c, prev6);
						}
						
						if ((code & 8) != 0) {
							set8(verts, r, c);
						}

						final int[] tr = triTable[code];
						
						for (int i = 0; i < tr.length;) 
						{
							final int ind0 = verts[tr[i++]];
							final int ind1 = verts[tr[i++]];
							final int ind2 = verts[tr[i++]];
							triIndexData.add(ind0);
							triIndexData.add(ind1);
							triIndexData.add(ind2);
						}
					}

					prev0 = verts[2];
					prev6 = verts[8];
					prev2[c] = verts[8];
				}

				prev3 = verts[5];
				prev1[c] = verts[7];
			}
		}		
		
		if (conf.useSplines) 
		{
			//System.out.println("new frame");
			
			int defValue = -1;
			// TODO don't realloc for a new frame
			neighData = new NeighData2B().alloc(vertexData.size / 2, defValue);
			
			for(int i = 0; i < lineIndexData.size; ) {
				int v0 = lineIndexData.arr[i++];
				int v1 = lineIndexData.arr[i++];
				neighData.addEdge(v0, v1);
			}
			
			//System.out.format("missing %d\n", ((NeighData2B)neighData).misscount);
			
			lineStripStarts = new ArrayList<Integer>();
			
			boolean[] checked = new boolean[vertexData.size / 2];
			
			for(int i = 0; i < checked.length; i++) 
			{
				if (checked[i]) 
					continue;				
				
				int start = findRoot(defValue, checked, i);
				
				if (start == -1) {
					lineStripStarts.add(i);
				} else {
					markChildren(checked, i);
					lineStripStarts.add(start);
				}
			}

		}
	}

	private void markChildren(boolean[] checked, int i) {
		int curr = i;
		while(curr != -1) {
			checked[curr] = true;
			curr = ((NeighData2B)neighData).children[curr];
		}
	}

	int findRoot(int defValue, boolean[] checked, int i) 
	{
		int curr = i;
		
		while(true) 
		{
			checked[curr] = true;
			
			int parent = ((NeighData2B)neighData).parents[curr];
			
			if (parent == i) 
				return -1;
				
			if (parent == defValue)
				return curr;
			
			curr = parent;					
		}
	}

	private void set8(int[] verts, int r, int c) {
		verts[8] = addNewVertex(vertexData, (c + 1), (r + 1));
	}

	private void set6(int[] verts, int r, int c, int prev6) {
		verts[6] = prev6 == NONE
			? addNewVertex(vertexData, (c), (r + 1))
			: prev6;
	}

	private void set2(int[] verts, int r, int c, int[] prev2) {
		verts[2] = prev2[c] == NONE
			? addNewVertex(vertexData, (c + 1), (r))
			: prev2[c];
	}

	private void set0(int[] verts, int r, int c, int prev0) {
		verts[0] = prev0 == NONE
			? addNewVertex(vertexData, c, r)
			: prev0;
	}


	
	
	private MSConf conf;

	public void setConf(MSConf conf) {
		this.conf = conf;		
	}
	
	
	
	
	public static final int NW = 0;
	public static final int NO = 1;
	public static final int NE = 2;
	public static final int WE = 3;
	public static final int EA = 5;
	public static final int SW = 6;
	public static final int SO = 7;
	public static final int SE = 8;
	
	static final int[][] triTable = {
		{},                                   // 0
		{NW, NO, WE},                         // 1
		{EA, NO, NE},                         // 2
		{NW, NE, EA, NW, EA, WE},             // 3 // This one looks correct after checking
		{WE, SO, SW},                         // 4
		{NW, NO, SO, NW, SO, SW},             // 5
		{NO, NE, EA, WE, SO, SW},             // 6
		{NW, NE, EA, NW, EA, SO, NW, SO, SW}, // 7
		{EA, SE, SO},                         // 8
		{NW, NO, WE, EA, SE, SO},             // 9
		{NO, NE, SE, NO, SE, SO},             // 10
		{NW, NE, SE, NW, SE, SO, NW, SO, WE}, // 11
		{WE, EA, SE, WE, SE, SW},             // 12
		{NW, NO, EA, NW, EA, SE, NW, SE, SW}, // 13
		{NO, NE, SE, NO, SE, SW, NO, SW, WE}, // 14
		{NW, NE, SE, NW, SE, SW},             // 15						
	};
	
	static final int[][] edgeTable = {			
		{},     // 0
		{NO, WE}, // 1
		{EA, NO}, // 2
		{EA, WE}, // 3 // Noted: Corrected this line.
		{WE, SO}, // 4 
		{NO, SO}, // 5
		{EA, NO, WE, SO},
		{EA, SO}, // 7
		{SO, EA}, // 8
		{NO, WE, SO, EA},
		{SO, NO}, // 10
		{SO, WE}, // 11
		{WE, EA}, // 12 
		{NO, EA}, // 13
		{WE, NO}, // 14
		{},     // 15
	};
}
