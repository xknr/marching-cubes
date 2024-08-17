package xknr.march.demo2;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import marchingcubes.CallbackMC;
import marchingcubes.MarchingCubes;
import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import xknr.march.p5.KeyCodes;
import xknr.march.p5.P5Base;
import xknr.march.util.SimplexNoise;

import static java.lang.Math.*;

public class P5Cubes extends P5Base
{	
	ArrayList<ArrayList<float[]>> results;
	
	float thres = 0.6f;

	public static String SHADER_PATH;
	public static final int canvasW = 1280, canvasH = 712;
	
	public static final float tileSz = 16;

	private PShader fogShader;
	private int W, H, D;
	private float noiseField[][][];
	
	boolean testMode = false;
	
	static {
		PJOGL.profile = 1; // original 2
	}
	
	int[][] table;
	
	public static void main(String[] args)
	{	
		// -9 derece
		
		System.out.println("main enter");
		
		double s = Math.sqrt(2.0);
		System.out.format("%.10f\n", Math.atan2(s/2, -(1+s)/10));
		System.out.format("%.10f\n", Math.toRadians(105));
		
		P5Cubes p5 = new P5Cubes();
		
		// Kludgy...
		String pkgPath = p5.getClass().getPackage().getName().replace(".", "/");
		SHADER_PATH = Paths.get("../", pkgPath).toString();
		System.out.format("SHADER_PATH = \"%s\"\n", SHADER_PATH);
		
		String className = p5.getClass().getCanonicalName();
		String[] pargs = {
			className
		};
		PApplet.runSketch(pargs, p5);
		
		System.out.println("main exit");
	}
	
	@Override
	public void settings() 
	{			
		smooth(8);
		size(canvasW, canvasH, P3D);		
	}
	
	@Override
	public void setup() 
	{
		frameRate(999);
		windowTitle(".");
		
		//background(0);

		//noLoop();
		//loadPixels();	
	}	
	
	private float rotx = 0;
	private float roty = 0;
	private float zoom = 0;

	@Override
	public void draw() 
	{		
		if (firstDraw) {		
			
			fogShader = loadShader(Paths.get(SHADER_PATH, "fog.glsl").toString());
			timing.reset();		
			// game.reset();
			firstDraw = false;
			
			zoom = 1.0f / 2.0f;
			
			if (testMode) {
				W = 16;
				H = 25;
				D = 16;
			} else {
				//W = H = D = 16;
				W = 50;
				H = 20;
				D = 40;				
			}
			
			noiseField = new float[D][H][W];
			
			makeTable();
		}
		
//		cont.gameUpdateControls();
//		game.gamePreUpdate();
//		game.draw();
		
		if (mousePressed && (mouseButton == LEFT)) {
			rotx -= (mouseY - pmouseY) * 0.01f; // TODO limit upper/lower
			roty += (mouseX - pmouseX) * 0.01f;
		}

		if (testMode)
		{			
			for(int z = 0; z < D; z++)
				for(int y = 0; y < H; y++)
					for(int x = 0; x < W; x++)			
						noiseField[z][y][x] = 0.2f;
			
			//_1 = 128, 32, 16, 64, 8, 2, 1, 4, 
			int[] _2A = {192, 160, 48, 80, 12, 10, 3, 5, 136, 34, 17, 68,}; 
			int[] _2B = {96, 144, 6, 9, 72, 130, 33, 20, 132, 40, 18, 65,}; 
			int[] _2C = {129, 36, 24, 66}; 
			int[] _3A = {200, 162, 49, 84, 140, 42, 19, 69, 168, 50, 81, 196, 138, 35, 21, 76, 224, 176, 112, 208, 14, 11, 7, 13}; 
			int[] _3B = {137, 38, 25, 70, 152, 98, 145, 100, 161, 52, 88, 194, 26, 67, 133, 44, 193, 164, 56, 82, 28, 74, 131, 37};
			int[] _3C = {73, 134, 41, 22, 148, 104, 146, 97,};
			int[] _4A = {15, 240, 51, 85, 204, 170,};
			int[] _4B = {77, 142, 43, 23, 212, 232, 178, 113, };
			int[] _4C = {153, 102, 165, 60, 90, 195, };
			int[] _4D = {197, 172, 58, 83, 92, 202, 163, 53, 139, 39, 29, 78, 184, 114, 209, 228, 177, 116, 216, 226, 27, 71, 141, 46, };
			int[] _4E = {225, 180, 120, 210, 30, 75, 135, 45, 201, 166, 57, 86, 156, 106, 147, 101, 169, 54, 89, 198, 154, 99, 149, 108, };
			int[] _4F = {150, 105, };

			
			int[] _5A1 = {244, 248, 242, 241, 79, 143, 47, 31, 206, 171, 55, 93, 236, 186, 115, 213, };
			int[] _5A2 = {59, 87, 205, 174, 179, 117, 220, 234, };
			
			//Arrays.sort(_5A);
			//System.out.println(Arrays.toString(_5A));


			//int[] _5B = {185, 118, 217, 230, 155, 103, 157, 110, 229, 188, 122, 211, 94, 203, 167, 61, };
			int[] _5B = {62, 91, 199, 173, 227, 181, 124, 218, 118, 217, 230, 185, 103, 157, 110, 155, 94, 203, 167, 61, 229, 188, 122, 211, };
			int[] _5C = {182, 121, 214, 233, 107, 151, 109, 158, };
			int[] _6A = {250, 243, 245, 252, 175, 63, 95, 207, 238, 187, 119, 221, };
			int[] _6B = {246, 249, 111, 159, 222, 235, 183, 125, 237, 190, 123, 215, };
			
			int[] _6C = {126, 219, 231, 189, };
			int[] _7 = {247, 253, 254, 251, 127, 223, 239, 191, };



			int y = 1;

			/*
			placeAll(_2A, 1, 1, 1);
			placeAll(_2B, 1, 4, 1); 
			placeAll(_2C, 1, 1, 13);
			placeAll(_3A, 1, 7, 1); 
			placeAll(_3B, 1, 10, 1); 
			placeAll(_3C, 1, 4, 13);
			*/
			
			/*
			placeAll(_4A, 1, 1, 1); 
			placeAll(_4B, 1, 1, 10);
			placeAll(_4C, 1, 4, 1);
			placeAll(_4D, 1, 7, 1); 
			placeAll(_4E, 1, 10, 1);
			placeAll(_4F, 1, 4, 10); 
			*/		

			placeAll(_5A1, 1, y, 1); y += 3; 
			placeAll(_5A2, 1, y, 1); y += 3;
			placeAll(_5B, 1, y, 1); y += 3;
			placeAll(_5C, 1, y, 1); y += 3;			
			
			placeAll(_6A, 1, y, 1); y += 3;
			placeAll(_6B, 1, y, 1); y += 3;
			placeAll(_6C, 1, y, 1); y += 3;
			placeAll(_7, 1, y, 1); y += 3;
			

		} else {
			genNoise(noiseField, W, H, D, timing.time);
			
			coverWithZeros(noiseField, W, H, D);
		}


        
        
        int zAxisOffset = 0;
        float[] scalarField = new float[W*H*D];
        int scalarFieldInd = 0;
        for(int z = 0; z < D; z++) {
            for(int y = 0; y < H; y++) {
            	for(int x = 0; x < W; x++) {
            		scalarField[scalarFieldInd++] = noiseField[z][y][x];
            	}
            }
        }
        final float[] finalScalarField = scalarField;

        results = new ArrayList<>();

        // Finished callback
        final CallbackMC callback = new CallbackMC() {
            @Override
            public void run() {
                results.add(getVertices());
            }
        };

        // Java...
        final int finalZAxisOffset = zAxisOffset;
        float[] voxSize = {1,1,1};
        float isoValue = thres;

        int[] size = {W,H,D};

        int nThreads = 1;
        // Thread work distribution
        int remainder = size[2] % nThreads;
        int segment = size[2] / nThreads;
        int i = 0;
        int segmentSize = (remainder-- > 0) ? segment + 1 : segment;
        final int paddedSegmentSize = (i != nThreads - 1) ? segmentSize + 1 : segmentSize;

        MarchingCubes.marchingCubesFloat(
    		finalScalarField, 
    		new int[]{size[0], size[1], paddedSegmentSize}, 
    		size[2], voxSize, isoValue, finalZAxisOffset, callback);
		
		drawScene();
		
		
		timing.endFrame();
//		cont.postUpdate();

		displayFrameDelta();		
	}


	public static void coverWithZeros(float[][][] f, int W, int H, int D) {
		
		float val = 0.0f;
		
		for(int d = 0; d < D; d++)
		for(int r = 0; r < H; r++)  
			f[d][r][0] = f[d][r][W-1] = val;

		for(int d = 0; d < D; d++)
		for(int c = 0; c < W; c++)
			f[d][0][c] = f[d][H-1][c] = val;

		for(int r = 0; r < H; r++) 
		for(int c = 0; c < W; c++)
			f[0][r][c] = f[D-1][r][c] = val;

	}

	private void placeAll(int[] arr, int x, int y, int z) {
		int p[] = new int[8];
		for(int i = 0; i < arr.length; i++) {
			int code = arr[i];
			calcCodeRev(code, p);
			for(int j = 0; j < 8; j++) {
				if (p[j] == 1) {
					int xi = j % 2;
					int yi = (j / 2) % 2;
					int zi = j / 4;
					noiseField[z+zi][y+yi][x+xi] = 0.8f;
				}
			}
				 
			x += 3;
			if (x >= W) {
				x = 1;
				z += 3;
				if (z >= D) {
					z = 1;
					y += 3;
				}
			}
		}
		
	}

	private void makeTable() 
	{
		table = new int[256][];
		
		// dark green surfaces: Counter clock wise
		
		placeAll(table, 128, new int[]{1, 9, 3}, "1");
		placeAll(table, 128+64, new int[]{3, 5, 11, 3, 11, 9}, "2A");
		placeAll(table, 64+32, new int[]{5, 11, 15, 5, 15, 7, 11, 1, 3, 11, 3, 15}, "2B");
		placeAll(table, 128+1, new int[]{1, 9, 3, 17, 25, 23}, "2C");
		placeAll(table, 128+64+8, new int[]{5, 21, 3, 5 , 19, 21, 5, 11, 19}, "3A");
		
		
		//placeAll(table, 64+4+2, new int[]{5,23,25,5,25,15,5,15,1,1,15,19,1,19,21}, "3B"); // this one doesn't produce all rotations
		placeAll(table, 128+8+1, new int[]{1, 17, 3, 1, 23, 17, 1, 19, 23, 3, 17, 25, 3, 25, 21}, "3B");

		placeAll(table, 64+8+1, new int[] {11, 19,23,1,5,17,1,17,25,1,25,21,1,21,9}, "3C");
		placeAll(table, 8+4+2+1, new int[] {9,11,17,9,17,15}, "4A"); 
		placeAll(table, 64+8+4+1, new int[] {1,5,17,1,17,25,1,25,21,1,21,9}, "4B");
		placeAll(table, 128+16+8+1, new int[] {1,19,23,1,23,5,7,25,21,7,21,3}, "4C");
		placeAll(table, 128+64+4+1, new int[] {3,5,17,3,17,25,3,25,9,9,25,19},"4D");
		placeAll(table, 128+64+32+1, new int[] {5,17,7,9,23,11,9,25,23,9,15,25},"4E");
		placeAll(table, 128+16+4+2, new int[] {3,7,15,5,1,11,23,25,17,21,19,9},"4F");
		placeAll(table, 128+64+32+16+4, new int[] {15,17,23,15,23,19,15,19,9},"5A1");
		placeAll(table, 32+16+8+2+1, new int[] {5,3,9,5,9,19,5,19,23},"5A2");
		
		
		//placeAll(table, 128+32+16+8+1, new int[] {15,25,21,5,1,19,5,19,23},"5B"); // misses some
		placeAll(table, 32 + 16+ 8 + 4 + 2, new int[] {3,11,5,3,9,11,25,17,23},"5B");
		
		placeAll(table, 128+32+16+4+2, new int[] {1,11,5,9,21,19,17,23,25},"5C");
		placeAll(table, 128+64+32+16+8+2, new int[] {11,25,17,11,19,25},"6A");
		placeAll(table, 128+64+32+16+4+2, new int[] {9,21,19,17,23,25},"6B");
		placeAll(table, 255-128-1, new int[] {1,3,9,17,23,25},"6C");
		placeAll(table, 255-8, new int[] {9,21,19},"7");
		//placeAll(table, , new int[] {},"");
		
		// 62 is missing		
		// 32 + 16+ 8 + 4 + 2
		// variant of 5B
		
		int count = 0;
		for(int i = 1; i < 256-1; i++)
			if (table[i] == null)
				count++;

		for(int i = 1; i < 256-1; i++)
			if (table[i] == null) {
				System.out.format("%d is unassigned\n", i);
			}

		
		System.out.format("null count = %d\n", count);
	}

	public static void placeAll(int[][] table, int code, int[] arr, String tag) {
		
		System.out.format("int[] _%s = {", tag);
		for(int zi = 0; zi < 3; zi++) {
			for(int mi = 0; mi < 2; mi++) {
				for(int ri = 0; ri < 4; ri++) 
				{
					if (table[code] == null) {
						//System.out.format("%d %s\n", code, Arrays.toString(arr));
						System.out.format("%d, ", code);
						table[code] = dup(arr);
					}
					code = rotateZCode(code);
					rotateZArr(arr);			
				}
				code = mirrorCode(code);
				mirrorArr(arr);
				
				// Reverse winding of all triangles.
				for(int i = 0; i < arr.length; i += 3) {
					int temp = arr[i+1];
					arr[i+1] = arr[i+2];
					arr[i+2] = temp;
				}
			}
			code = rotDiagCode(code);
			rotateDiagArr(arr);			
		}		
		System.out.println("};");
	}
	
	/*
	 * Around the big diagonal (0, 0, 0) - (1, 1, 1)
	 */
	public static int rotDiagCode(int code) {
		return rotateXCode(rotateZCode(code)); 
	}
	public static void rotateDiagArr(int[] arr) {
		rotateZArr(arr);
		rotateXArr(arr);
	}

	public static int mirrorCode(int code) {
		int p[] = new int[8];
		calcCodeRev(code, p);
		
		for(int i = 0; i < 4; i++) {
			int temp = p[i];
			p[i] = p[i + 4];
			p[i + 4] = temp;
		}
		
		return calcCode(p);
	}
	
	public static void calcCodeRev(int code, int[] p) {
 		for(int i = 0; i < 8; i++) 
 			p[i] = (code >> (7-i)) & 1;
	}

	public static int rotateXCode(int code) {
		int p[] = new int[8];
		calcCodeRev(code, p);
		int temp = p[0];
		p[0] = p[2];
		p[2] = p[6];
		p[6] = p[4];
		p[4] = temp;
		temp = p[1];
		p[1] = p[3];
		p[3] = p[7];
		p[7] = p[5];
		p[5] = temp;
		return calcCode(p);
	}	

	public static int rotateZCode(int code) {
		int p[] = new int[8];
		calcCodeRev(code, p);
		int temp = p[0];
		p[0] = p[1];
		p[1] = p[3];
		p[3] = p[2];
		p[2] = temp;
		temp = p[4];
		p[4] = p[5];
		p[5] = p[7];
		p[7] = p[6];
		p[6] = temp;
		return calcCode(p);
	}
	
	
	public static void rotateXArr(int[] arr) {
		float[] c = new float[3];
		for(int i = 0; i < arr.length; i++) {
			cvt(arr[i], c);
			rotX(c);
			arr[i] = cvtBack(c);
		}		
	}	

	public static void rotateZArr(int[] arr) {
		float[] c = new float[3];
		for(int i = 0; i < arr.length; i++) {
			cvt(arr[i], c);
			rotZ(c);
			arr[i] = cvtBack(c);
		}		
	}
	
	public static void mirrorArr(int[] arr) {
		float[] c = new float[3];
		for(int i = 0; i < arr.length; i++) {
			cvt(arr[i], c);			
			c[2] = 2 - c[2]; // Only z.			
			arr[i] = cvtBack(c);
		}		
		
	}



	public static void rotX(float[] c) {
		// (Don't touch x)

		c[1] -= 1;
		c[2] -= 1;
		
		float c1 = -c[1];
		c[1] = c[2];
		c[2] = c1;
		
		c[1] += 1;
		c[2] += 1;
	}

	public static void rotZ(float[] c) {
		// (Don't touch z)

		c[0] -= 1;
		c[1] -= 1;
		
		float c1 = -c[0];
		c[0] = c[1];
		c[1] = c1;
		
		c[0] += 1;
		c[1] += 1;
	}

	public static int[] dup(int[] arr) {
		int[] result = new int[arr.length];
		System.arraycopy(arr, 0, result, 0, arr.length);
		return result;
	}


	private void drawScene() 
	{
		background(200);
		
//		//lights();
//		//directionalLight(1, 1, 1, 1, 1, 1);
//		ambientLight(80, 80, 80);
//		directionalLight(255, 255, 255, -1, 0.3f, 0.4f);
//		directionalLight(255, 255, 255, 0.3f, 1.0f, -0.7f);

		pushMatrix();
		
		setupMatrices();


//		drawPoints();
		drawIsoSurface();
		
//		drawTriangles();
		
		popMatrix();
		
	}

	void drawTriangles() {
		strokeWeight(1.5f);
		//stroke(0);
		noStroke();
		fill(255);
		lights();
		noLights();
//		noFill();
//		stroke(0);

		float scale = 512;
		
		pushMatrix();
		translate(200, 200, 200);
		//rect(0, 0, 100, 100);

		
		beginShape(TRIANGLES);
		for(ArrayList<float[]> tri: results) {
			for(float[] v: tri) {
				//System.out.format("%s\n", Arrays.toString(v));
				vertex(v[0]*scale, v[1]*scale, v[2]*scale);				
			}
		}
		endShape();
		popMatrix();
		noLights();
		
	}

	private void setupMatrices() {
		float fov = PApplet.PI/3.0f;
		float cameraZ = (height/2.0f) / (float)Math.tan(fov/2.0);
		perspective(fov, (float)(width)/(float)(height), cameraZ/10.0f, cameraZ*10.0f);
		translate(width/2.0f, height/2.0f, 0);
		translate(0, 0, zoom * height);
		rotateX(rotx);
		rotateY(roty);
		
		translate(-(W-1) * tileSz / 2.0f, -(H-1) * tileSz / 2.0f, - (D-1) * tileSz / 2.0f);
	}

	private void drawPoints() 
	{
		shader(fogShader);

		noStroke();
		//strokeWeight(1.5f);

		
		for(int k = 0; k < D; k++) {
			for(int j = 0; j < H; j++) {
				for(int i = 0; i < W; i++) {
					pushMatrix();
					float x = i * tileSz;
					float y = j * tileSz;
					float z = k * tileSz;
					translate(x, y, z);
					float val = noiseField[k][j][i];
					//box(val * tileSz);
					float e = val * tileSz;
					
					e *= 0.25f;
					
					//float fogColor = map(k / (float)D, 0, 1, 0.5f, 0.8f);
					//fill(fogColor * 255);
					//fill(val * 255 * 2);
					
					rect(-e/2.0f, -e/2.0f, e, e);
					popMatrix();
				}
			}
		}
		
		
		//strokeWeight(1);
		resetShader();

	}

	private void drawIsoSurface() 
	{
		
		strokeWeight(1.5f);
		//stroke(0);
		noStroke();
		fill(255);
		
//		int xs[] = {1, 2, 1, 0, 0, 2, 2, 0, 1, 2, 1, 0};
//		int ys[] = {0, 1, 2, 1, 0, 0, 2, 2, 0, 1, 2, 1};
//		int zs[] = {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2};
		
		/*
		float vs[][] = {
				{1, 0, 0},
				{2, 1, 0},
				{1, 2, 0},
				{0, 1, 0},
				{0, 0, 1},
				{2, 0, 1},
				{2, 2, 1},
				{0, 2, 1},
				{1, 0, 2},
				{2, 1, 2},
				{1, 2, 2},
				{0, 1, 2},
			};
		*/
		float thres = 0.6f;

//		float h = tileSz * 0.5f;
//		float t = tileSz;

		PGL pgl = beginPGL();
		GL gl = ((PJOGL)pgl).gl;
		GL2 gl2 = ((PJOGL)pgl).gl.getGL2();

		
		boolean showOriginals = false;
		if (showOriginals ) 
		{
			int[] CULL_FACE_MODE = new int[1];
			gl.glGetIntegerv(GL.GL_CULL_FACE_MODE, CULL_FACE_MODE, 0);
	
			boolean CULL_FACE_ENABLED = gl.glIsEnabled(GL.GL_CULL_FACE);
			
			int[] FRONT_FACE = new int[1];
			gl.glGetIntegerv(GL.GL_FRONT_FACE, FRONT_FACE, 0);
			System.out.format("CULL_FACE_MODE = %d, CULL_FACE_ENABLED = %b, FRONT_FACE = %d\n",
					CULL_FACE_MODE[0],
					CULL_FACE_ENABLED,
					FRONT_FACE[0]
					);
			
		}
		
		if (isKeyDown(KeyCodes.SPACE)) {
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glCullFace(GL.GL_BACK);
			gl.glFrontFace(GL.GL_CCW);
		} else {
			gl.glDisable(GL.GL_CULL_FACE);
		}
			
		
		float[][][] nf = noiseField;
		
		
		float da[] = new float[3];
		float db[] = new float[3];
		float n[] = new float[3];
		
		float[] l0 = {1, 0.2f, 0.3f};
		float[] l1 = {-0.2f, -0.6f, 0.3f};
		float[] l2 = {0.2f, -0.3f, 0.8f};
		vecNormalize(l0);
		vecNormalize(l1);
		vecNormalize(l2);


		
		float cachedN[][] = new float[27][3]; 
		float cachedV[][] = new float[27][3]; 
		boolean[] isCached = new boolean[27];

		//float vs[][] = new float[3][3]; 
		int[] t = new int[8];
		
		float[][] norCache = new float[8][];
		
		for(int k = 0; k < D-1; k++) {
			for(int j = 0; j < H-1; j++) {
				for(int i = 0; i < W-1; i++) {
					
					
					for(int oi = 0; oi < 2; oi++)
					for(int ni = 0; ni < 2; ni++)
					for(int mi = 0; mi < 2; mi++)
						t[mi|ni<<1|oi<<2] = nf[k+oi][j+ni][i+mi] < thres ? 0 : 1;
					
					int code = calcCode(t);
					
					int[] corners = table[code];
					
					if (corners == null && code != 0 && code != 255) {						
						System.out.format("code %d\n", code);
						throw new RuntimeException();
						//continue;
					}
					
					if (corners == null)
						continue;

					Arrays.fill(isCached, false);
					Arrays.fill(norCache, null);

					gl2.glPushMatrix();
					gl2.glTranslatef(i*tileSz, j*tileSz, k*tileSz);

					gl2.glBegin(GL.GL_TRIANGLES);
					for (int m = 0; m < corners.length; m += 3) 
					{						
						for(int o = 0; o < 3; o++) 
						{
							int corner = corners[m+o];
							
							if (isCached[corner]) 
								continue; 
							
							isCached[corner] = true;
							
							cvt(corner, cachedV[corner]);

							interpolate(thres, nf, k, j, i, 
								corner, cachedV[corner], norCache, cachedN[corner]);
						}

//						vecSub(triv[1], triv[0], da);
//						vecSub(triv[2], triv[0], db);
//						vecCross(da, db, n);
//						vecNormalize(n);
//						//System.out.println(Arrays.toString(n));
						
//						if (false) {
//							float gray = 0;
//							gray += max(vecDot(l0, n), 0);
//							gray += Math.max(vecDot(l1, n), 0);
//							gray += max(vecDot(l2, n), 0);
//							gl2.glColor3f(gray, gray, gray);
//						} else {
//							gl2.glColor3f(n[0]*0.5f+0.5f, n[1]*0.5f+0.5f, n[2]*0.5f+0.5f);
//						}
						
						for(int o = 0; o < 3; o++) {
							int vind = corners[m+o];
							float[] nor = cachedN[vind];
							
							
							
//							gl2.glColor3f(
//								nor[0]*0.5f+0.5f, 
//								nor[1]*0.5f+0.5f, 
//								nor[2]*0.5f+0.5f);
							
							float gray = 0;
							gray += max(vecDot(l0, nor), 0);
							gray += max(vecDot(l1, nor), 0);
							gray += max(vecDot(l2, nor), 0);
							gray /= 3.0f;
							gl2.glColor3f(gray, gray, gray);

							
							float[] v = cachedV[vind];
							gl2.glVertex3f(
								v[0] * 0.5f * tileSz, 
								v[1] * 0.5f * tileSz, 
								v[2] * 0.5f * tileSz);
						}
					}
						
					gl2.glEnd();
				
					gl2.glPopMatrix();
				}
			}
		}
		
		gl.glFrontFace(GL.GL_CW); // Revert to processing default.

		endPGL(); 

		
		strokeWeight(1);
	}

	void interpolate(float thres, float[][][] nf, int k, int j, int i, int vt, float[] v, float[][] norCache, float[] nor) 
	{
		
		int comp;
		int ax, ay, az, bx, by, bz;
		
		ax = ay = az = bx = by = bz = 0;
		
		switch(vt) 
		{
			case 1:
//				a = nf[k][j][i]; 
//				b = nf[k][j][i+1];
				ax = ay = az = by = bz = 0; 
				bx = 1;
				comp = 0;
				break;
			case 7:
				ay++;
				bx++;
				by++;
//				a = nf[k][j+1][i]; 
//				b = nf[k][j+1][i+1];
				comp = 0;
				break;
			case 3:
//				a = nf[k][j][i];
//				b = nf[k][j+1][i];
				by++;
				comp = 1;
				break;
			case 5:
				ax++;
				bx++;
				by++;
//				a = nf[k][j][i+1];
//				b = nf[k][j+1][i+1];
				comp = 1;				
				break;
			case 9:
				bz++;
//				a = nf[k][j][i];
//				b = nf[k+1][j][i];
				comp = 2;				
				break;
			case 11:
				ax++;
				bx++;
				bz++;
//				a = nf[k][j][i+1];
//				b = nf[k+1][j][i+1];
				comp = 2;			
				break;
			case 15:
				ay++;
				by++;
				bz++;
//				a = nf[k][j+1][i];
//				b = nf[k+1][j+1][i];
				comp = 2;				
				break;
			case 17:
				ax++;
				ay++;
				bx++;
				by++;
				bz++;				
//				a = nf[k][j+1][i+1];
//				b = nf[k+1][j+1][i+1];
				comp = 2;
				break;
			case 1+18:
				az++;
				bx++;
				bz++;
//				a = nf[k+1][j][i]; 
//				b = nf[k+1][j][i+1];
				comp = 0;				
				break;
			case 7+18:
//				a = nf[k+1][j+1][i]; 
//				b = nf[k+1][j+1][i+1];
				ay++;
				az++;
				bx++;
				by++;
				bz++;
				comp = 0;				
				break;
			case 3+18:
//				a = nf[k+1][j][i];
//				b = nf[k+1][j+1][i];
				comp = 1;				
				az++;
				by++;
				bz++;				
				break;
			case 5+18:
//				a = nf[k+1][j][i+1];
//				b = nf[k+1][j+1][i+1];
				ax++;
				az++;
				bx++;
				by++;
				bz++;
				comp = 1;
				break;
			default:
				throw new RuntimeException();
		}
		
		int ai = ax | ay<<1 | az<<2;
		int bi = bx | by<<1 | bz<<2;
		
		if (norCache[ai] == null) 
			norCache[ai] = calcNormal(nf, i+ax, j+ay, k+az, new float[3], W, H, D);

		if (norCache[bi] == null) 
			norCache[bi] = calcNormal(nf, i+bx, j+by, k+bz, new float[3], W, H, D);

		float a = nf[k+az][j+ay][i+ax]; 
		float b = nf[k+bz][j+by][i+bx];

		float ratio = (thres - a) / (b - a);

		v[comp] = 2 * ratio;
		
		//ratio = 0.5f;

		nor[0] = (1-ratio)*norCache[ai][0] + ratio*norCache[bi][0];
		nor[1] = (1-ratio)*norCache[ai][1] + ratio*norCache[bi][1];
		nor[2] = (1-ratio)*norCache[ai][2] + ratio*norCache[bi][2];
		vecNormalize(nor);
		
	}
	

	public static float[] calcNormal(float[][][] nf, int x, int y, int z, float[] dst, int W, int H, int D) {
		dst[0] = nf[z][y][min(x+1, W-1)] - nf[z][y][max(x-1, 0)];
		dst[1] = nf[z][min(y+1, H-1)][x] - nf[z][max(y-1, 0)][x];
		dst[2] = nf[min(z+1, D-1)][y][x] - nf[max(z-1, 0)][y][x];		
		vecNormalize(dst);
		return dst;
	}

	public static int cvtBack(float[] arr) {
		int x = round(arr[0]);
		int y = round(arr[1]);
		int z = round(arr[2]);
		return z * 9 + y * 3 + x;		
	}
	
	public static void cvt(int i, float[] arr) {
		arr[0] = i % 3;
		arr[1] = (i / 3) % 3;
		arr[2] = i / 9;
	}

	public static int calcCode(int[] t) {
		assert t.length == 8;
		int code = 0;
		for(int i = 0; i < 8; i++) {
			code |= t[i] << (7-i);
		}
		return code;
		//int code = t[0] << 7 | t[1]<< 6 | t010 << 5 | t011 << 4 | t100 << 3 | t101 << 2 | t110 << 1 | t111;

	}

	public static float vecDot(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	public static void vecNormalize(float[] n) {
		final double x = n[0];
		final double y = n[1];
		final double z = n[2];
		double dsq = x * x + y * y + z * z;
		float r = 1 / (float)Math.sqrt(dsq);
		n[0] *= r;
		n[1] *= r;
		n[2] *= r;
	
	}

	public static void vecCross(float[] a, float[] b, float[] dst) {
		dst[0] = (a[1]*b[2])-(a[2]*b[1]);
		dst[1] = (a[2]*b[0])-(a[0]*b[2]);
		dst[2] = (a[0]*b[1])-(a[1]*b[0]);		
	}

	public static void vecSub(float[] a, float[] b, float[] dst) {
		dst[0] = a[0] - b[0];
		dst[1] = a[1] - b[1];
		dst[2] = a[2] - b[2];
		
	}

	public static void genNoise(float[][][] noiseField2, int W, int H, int D, float time) 
	{
		float noiseScale = H * 0.07f / 8; 
		//noiseScale = mouseX / (float)width;
		
		
		float NOISE_SPEED = 0.1f;
		float t = time * NOISE_SPEED;
		
		for(int d = 0; d < D; d++)
		for(int r = 0; r < H; r++) 
		{
			for(int c = 0; c < W; c++) 
			{								
				float x = c * noiseScale;
				float y = r * noiseScale;
				float z = d * noiseScale;
				
				double val;
				
				boolean useOctaves = true;
				if (useOctaves) 
				{
					val = SimplexNoise.noise(x * 0.5, y * 0.5, z * 0.5, t);
					val += 0.5 * SimplexNoise.noise(x * 1, y * 1, z * 0.5, t);
					val += 0.25 * SimplexNoise.noise(x * 2, y * 2, z * 0.5, t);
					val /= 1.75;
					val = val * 0.5 + 0.5;
					
					float mi = 0.25f, ma = 0.75f;
					val = (val - mi) / (ma - mi); 
				}
				else 
				{
					val = SimplexNoise.noise(x, y, t) * 0.5 + 0.5;
				}

				
				noiseField2[d][r][c] = (float)val;
			}
		}		
	}

	@Override
	public void keyPressed() {
		super.keyPressed();
//		cont.controllerOnKeyDown(keyCode);		
	}
	
	@Override
	public void mouseWheel(MouseEvent event) {
		float zoomAmount = 20 / 720.0f;
		float e = event.getCount();
		zoom -= e * zoomAmount;
		println(zoom);
		zoom = constrain(zoom, -0.15f, 0.75f);
	}
	
	private void displayFrameDelta() 
	{
		textSize(14);
		
		String textStr = String.format("%.3f", timing.frameDelta * 1000);
		
		int textX = 5, textY = 16;
		
		fill(0);		
		text(textStr, textX+1, textY-1);
		
		fill(255);		
		text(textStr, textX, textY);
	}
	
//	private Controller cont;
	private boolean firstDraw = true; 
	
}
