package xknr.march.algo;


/*
 * vType:
 * 
 *   1
 * 0   2 
 *   3
 */
public class MarchingSquaresSimple extends IMarchingSquares
{
	private static int vTypeX[] = {0, 1, 2, 1};
	private static int vTypeY[] = {1, 0, 1, 2};
	
	public static final int WE = 0;
	public static final int NO = 1;
	public static final int EA = 2;
	public static final int SO = 3;
	
	@Override
	public void execute(ScalarField field, float thres) 
	{		
		// For storing output:
		lineIndexData.clear();
		vertexData.clear();
			
		int[] verts = new int[4];

		float[] vs = new float[4];
		
		int[] prevUp = new int[field.cols - 1];
		for (int i = 0; i < prevUp.length; i++)
			prevUp[i] = NONE;

		for (int r = 0; r < field.rows - 1; r++) 
		{
			int prevLe = NONE;

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

				if (prevLe != NONE)
					verts[WE] = prevLe;

				if (prevUp[c] != NONE)
					verts[NO] = prevUp[c];

				for (int i = 0; i < lineList.length; i++)
				{
					int vType = lineList[i];

					if (verts[vType] == NONE) // If this vertex has not been calculated yet. 
					{
						final int x = vTypeX[vType], y = vTypeY[vType];
						final float fx = interpolateX(x, y, vs, thres);
						final float fy = interpolateY(x, y, vs, thres);
						verts[vType] = addNewVertex(vertexData, c + fx, r + fy);						
					}

					lineIndexData.add(verts[vType]);
				}

				prevLe = verts[EA];
				prevUp[c] = verts[SO];
			}
		}		
	}
	

//	public static int calcCode(float[] vs, float thres) 
//	{
//		return calcCode(vs[0], vs[1], vs[2], vs[3], thres);
//	}
	
	public static int calcCode(float v00, float v10, float v01, float v11, float thres) 
	{
		int i00 = (v00 < thres) ? 0 : 1; 
		int i10 = (v10 < thres) ? 0 : 1;
		int i01 = (v01 < thres) ? 0 : 1;
		int i11 = (v11 < thres) ? 0 : 1;
		 
		// i00 is least significant bit, i11 is most significant:
		// i00 --- i10    0 --- 1
		//  |       |     |     |  --> [3210] 
		// i01 --- i11    2 --- 3
		 
		return (i00) | (i10 << 1) | (i01 << 2) | (i11 << 3);
	}

	public static float interpolateY(int x0, int y0, float[] f, float thres)
	{
		// uncomment this to prevent interpolation
		//if (true) return y0 * 0.5f; 
		
		if (y0 == 1) 
		{			
			float v0, v1;
			
			if (x0 == 0) {
				v0 = f[0];
				v1 = f[2];
			} else {
				v0 = f[1];
				v1 = f[3];
			}
			
			return (thres - v0) / (v1 - v0);
		} else {
			return y0 * 0.5f;
		}
	}

	public static float interpolateX(int x0, int y0, float[] f, float thres) 
	{
		// uncomment this to prevent interpolation
		// if (true) return x0 * 0.5f; 
		
		if (x0 == 1) 
		{			
			float v0, v1;
			
			if (y0 == 0) 
			{
				v0 = f[0];
				v1 = f[1];
			}
			else 
			{
				v0 = f[2];
				v1 = f[3];
			}
			
			return (thres - v0) / (v1 - v0);
		}
		else 
		{
			return x0 * 0.5f;
		}
	}

	public static int addNewVertex(ArrListF li, float x, float y) 
	{
		int result = li.size / 2;
		li.add(x);
		li.add(y);
		return result;
	}

	static final int[][] edgeTable = {			
		{},               // 0
		{NO, WE},         // 1
		{EA, NO},         // 2
		{WE, EA},         // 3
		{WE, SO},         // 4 
		{NO, SO},         // 5
		{EA, NO, WE, SO}, // 6
		{EA, SO},         // 7
		{SO, EA},         // 8
		{NO, WE, SO, EA}, // 9
		{SO, NO},         // 10
		{SO, WE},         // 11
		{EA, WE},         // 12
		{NO, EA},         // 13
		{WE, NO},         // 14
		{},               // 15
	};

	public static final int NONE = -1;

}
