package xknr.march.algo;

public class ScalarField 
{
	public ScalarField(int rows, int cols) 
	{
		this.rows = rows;
		this.cols = cols;
		
		this.values = new float[rows][cols];
	}

	public int rows, cols;
	public float[][] values;
}
