package xknr.march.demo;

import java.util.List;

import processing.core.PApplet;
import xknr.march.algo.ArrListF;
import xknr.march.algo.ArrListI;
import xknr.march.algo.HeightMap;
import xknr.march.algo.IMarchingSquares;
import xknr.march.algo.MarchingSquares;
import xknr.march.algo.MarchingSquaresSimple;
import xknr.march.algo.ScalarField;
import xknr.march.algo.Settings;
import xknr.march.algo.TileWState;
import xknr.march.p5.IGame;

public class GameMarch implements IGame
{	

	public GameMarch(P5Squares p5) {
		this.p5 = p5;
		this.draw = new Draw(this, p5);
		this.mousePow = new MousePower(p5);
	}

	public void reset() 
	{		
		tileW = tileWState.tileWReqToInt();
		tileH = tileW;
		
		int rows = (P5Squares.canvasH / tileH) + 1; 
		int cols = (P5Squares.canvasW / tileW) + 1;
		
		map = new HeightMap(p5, cols, rows, tileW, tileH, conf.genMapEveryFrame);
		
		if (!conf.genMapEveryFrame) 
		{
			float elapsed = p5.getElapsed();
			map.genMap(elapsed);
		}
	}
	
	public void gamePreUpdate() 
	{
		MSConf msConf = new MSConf(conf.useSplines, 
			conf.drawLines, 
			conf.drawTris, 
			conf.drawFullQuads, 
			Settings.ZERO_OUT_EDGES);

		map.gameUpdateScalarField(
				conf.addMouseField, 
				msConf.zeroOutEdges, 
				p5.getElapsed(), 
				p5.mouseX, 
				p5.mouseY, 
				mousePow.mousePower());

		if (algo == null) {
			boolean useSimple = false;
			if (useSimple) {
				algo = new MarchingSquaresSimple();
			} else {
				algo = new MarchingSquares(msConf);
			}
		}
		
		if (algo instanceof MarchingSquares)
			((MarchingSquares)algo).setConf(msConf);	

		algo.execute(map.fieldUsed(), thres);
	}

	public void draw() 
	{
		SceneData sceneData = 
			new SceneData(
				algo.lineIndexData, 
				algo.vertexData, 
				algo.triIndexData, 
				algo.neighData, 
				algo.lineStripStarts);
			
		draw.drawScene(sceneData);		
	}


	public int getCodeFromTile(int r, int c) {		
		return getCodeFromTile(map.fieldUsed(), thres, r, c);		
	}
	
	public static int getCodeFromTile(ScalarField field, float thres, int r, int c) 
	{
		float[][] values = field.values;
		
		float v00 = values[r][c];
		float v10 = values[r][c + 1];
		float v01 = values[r + 1][c];
		float v11 = values[r + 1][c + 1];
	
		return MarchingSquaresSimple.calcCode(v00, v10, v01, v11, thres);
	}
		

	public void postUpdate() {
		mousePow.update();		
	}

	
	
	
	
	
	
	
	
	private GameConf conf = new GameConf();
	public GameConf conf() {
		return conf;
	}
	
	private HeightMap map;
	public HeightMap map() {
		return map;
	}
	
	private float thres = 0.55f;
	public float thres() {
		return thres;
	}

	private int tileW, tileH;
	public int tileW() {
		return tileW;
	}
	public int tileH() {
		return tileH;
	}
	
	private P5Squares p5;
	private Draw draw;
	private IMarchingSquares algo;
	
	private MousePower mousePow;
	public MousePower mousePow() {
		return mousePow;
	}

	private TileWState tileWState = new TileWState(this);
	public TileWState tileWState() {
		return tileWState;
	}

	public void incThres(float delta) {
		thres += delta;
		thres = PApplet.constrain(thres, 0, 1);		
	}

}
