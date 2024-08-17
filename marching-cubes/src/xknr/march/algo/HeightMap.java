package xknr.march.algo;

import processing.core.PApplet;
import xknr.march.util.SimplexNoise;

public class HeightMap 
{

	public static final float NOISE_SPEED = 0.04f; 

	public HeightMap(PApplet noiseP5, 
			int cols, int rows, 
			int tileW, int tileH, 
			boolean genMapEveryFrame)
	{
		this.noiseP5 = noiseP5;
		this.rows = rows;
		this.cols = cols;
		this.tileW = tileW;
		this.tileH = tileH;
		this.genMapEveryFrame = genMapEveryFrame;
		fieldNoise = new ScalarField(rows, cols);
		
		if (genMapEveryFrame) {
			fieldFinal = fieldNoise;
		} else {
			fieldFinal = new ScalarField(rows, cols);
		}
	}

	public void genMap(float time) 
	{		
		boolean useP5Noise = false;
		
		if (useP5Noise) {
			noiseP5.noiseSeed(2);		
			noiseP5.noiseDetail(1);
		}
		
		float noiseScale = tileH * 0.1f / 8; 
		//noiseScale = mouseX / (float)width;
		
		
		float t = time * NOISE_SPEED;
		
		for(int r = 0; r < rows; r++) 
		{
			for(int c = 0; c < cols; c++) 
			{								
				float x = c * noiseScale;
				float y = r * noiseScale;
				
				double val;
				
				if (useP5Noise) 
				{
					val = noiseP5.noise(x, y, t) * 2;
				}
				else
				{	
					boolean useOctaves = true;
					if (useOctaves) 
					{
						val = SimplexNoise.noise(x * 0.5, y * 0.5, t);
						val += 0.5 * SimplexNoise.noise(x * 1, y * 1, t);
						val += 0.25 * SimplexNoise.noise(x * 2, y * 2, t);
						val /= 1.75;
						val = val * 0.5 + 0.5;
					}
					else 
					{
						val = SimplexNoise.noise(x, y, t) * 0.5 + 0.5;
					}
				}	
				fieldNoise.values[r][c] = (float)val;
			}
		}
	}
	
	public void gameUpdateScalarField(boolean addMouseField, boolean zeroOutEdges, float time, int mouseX, int mouseY, float mousePower) 
	{
		if (genMapEveryFrame)
			genMap(time);

		if (addMouseField) {
			addMouse(fieldNoise, fieldFinal, mouseX, mouseY, mousePower);
			fieldUsed = fieldFinal; // Choose target
		} else {
			fieldUsed = fieldNoise;
		}
		
		if (zeroOutEdges)
			surroundWithZeros(fieldUsed());
	}

	public void addMouse(ScalarField src, ScalarField dst, float mouseX_, float mouseY_, float mousePower) 
	{
//		float mx = mouseX / (float)tileW;
//		float my = mouseY / (float)tileH;
	
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				
				float dx = (mouseX_ - c * tileW);
				float dy = (mouseY_ - r * tileH);
				float dsq = dx * dx + dy * dy;
				
				double val = src.values[r][c];
				val += 1000 * mousePower / (dsq + .05f);					
				val = Math.min(Math.max(val, 0.0), 1.0);
				dst.values[r][c] = (float)val;
			}
		}		
	}

	public static void surroundWithZeros(ScalarField field) 
	{		
		for(int r = 0; r < field.rows; r++) {
			field.values[r][0] = 0;
			field.values[r][field.cols-1] = 0;
		}
		for(int c = 1; c < field.cols - 1; c++) {
			field.values[0][c] = 0;
			field.values[field.rows-1][c] = 0;
		}
	}

	private int rows, cols;
	public int rows() {
		return rows;
	}
	public int cols() {
		return cols;
	}
	
	private int tileW, tileH;
	
	private ScalarField fieldNoise; // Generate noise target.
	private ScalarField fieldFinal; // Target after noise + mouse.
	private ScalarField fieldUsed; // Assign whichever field we will to use.
	public ScalarField fieldUsed() {
		return fieldUsed;		
	}

	private boolean genMapEveryFrame;
	private PApplet noiseP5;
}
