package xknr.march.demo;

import java.util.List;

import processing.core.PApplet;
import xknr.march.algo.ArrListI;
import xknr.march.algo.NeighData2;
import xknr.march.algo.NeighData2B;

public class Draw 
{
	public Draw(GameMarch game, PApplet p5) 
	{
		this.game = game;
		this.p5 = p5;
	}
	
	public void drawScene(SceneData sceneData) 
	{
		p5.background(5, 15, 35);

		if (game.conf().drawCenters)
			drawCenters();

		if (game.conf().drawTris) 
		{			
			p5.noStroke();
			p5.fill(150, 100, 255, 127);
			
			if (game.conf().drawFullQuads) {
				if (game.conf().mergeQuads)
					drawFullQuads2();
				else
					drawFullQuads1();
			}

			// fill(50, 50, 50, 127); // hack to color differently

			drawTris(sceneData);			
		}				
		
		if (game.conf().drawLines) 
		{			
			if (game.conf().useSplines) {
				drawLinesWithSplines(sceneData);
			} else {
				drawLines(sceneData);
			}
		}
	}

	public void drawCenters() 
	{
		final int tileW = game.tileW(), tileH = game.tileH();
		final int rows = game.map().rows(), cols = game.map().cols();
		final float [][] values = game.map().fieldUsed().values;

		//stroke(0);
		p5.noStroke();
		
		p5.fill(20, 80, 150);
		p5.beginShape(PApplet.QUADS);

		boolean lock_to_full_size = false;

		for(int r = 0; r < rows; r++) 
		{
			for(int c = 0; c < cols; c++) 
			{
				float val = values[r][c];
				float d = val;

				if (lock_to_full_size)
					d = 1.0f;   
				
				
				d *= 0.95f; // Scale down to prevent ellipses/rects from 
				// touching one another.
				
				float dx = d * tileW;
				float dy = d * tileH;
				
//				boolean doEllipse = false;
//				if (doEllipse) {
//					ellipse(c * tileW, r * tileH, dx, dy);
//				} else {
//					rect(c * tileW-dx/2, r * tileH-dy/2, dx, dy);
//				}
				
				float x0 = c * tileW - dx / 2;
				float y0 = r * tileH - dy / 2;
				float x1 = x0 + dx;
				float y1 = y0 + dy;
				
				p5.vertex(x0, y0);
				p5.vertex(x1, y0);
				p5.vertex(x1, y1);
				p5.vertex(x0, y1);
			}
		}
		
		p5.endShape();
	}

	public void drawFullQuads1() 
	{
		final int rows = game.map().rows(), cols = game.map().cols();
		
		p5.beginShape(PApplet.QUADS);
		
		for(int r = 0; r < rows - 1; r++) {
			for(int c = 0; c < cols - 1 + 1; c++) {
				int code = c == cols - 1 ? -1 : game.getCodeFromTile(r, c);
				if (code == 15) {			
					emitQuadVertices1(game.thres(), r, c);
				}
			}
		}
		
		p5.endShape();		
	}
	
	public void drawFullQuads2() 
	{
		final int rows = game.map().rows(), cols = game.map().cols();

		p5.beginShape(PApplet.QUADS);
		for(int r = 0; r < rows - 1; r++) {
			int prev = -1;
			int start = 0;
			for(int c = 0; c < cols - 1 + 1; c++) {
				int code = c == cols - 1 ? -1 : game.getCodeFromTile(r, c);
					
				if (code == 15 && prev != 15) {			
					start = c;
				} else if (code != 15 && prev == 15) {					
					emitQuadVertices2(game.thres(), r, c, start);
				}
				prev = code;
			}
		}
		p5.endShape();		
	}

	public void emitQuadVertices1(float thres, int r, int c) 
	{
		final int tileW = game.tileW(), tileH = game.tileH(); 
		
		final float x0 = tileW * c;
		final float y0 = tileH * r;
		final float x1 = x0 + tileW;
		final float y1 = y0 + tileH;
		
		p5.vertex(x0, y0);
		p5.vertex(x1, y0);
		p5.vertex(x1, y1);
		p5.vertex(x0, y1);
	}
	
	public void emitQuadVertices2(float thres, int r, int c, int start) 
	{
		final int tileW = game.tileW(), tileH = game.tileH();
		
		// rect(tileW * c, tileH * r, tileW, tileH);
		
		final float x0 = tileW * start;
		final float y0 = tileH * r;
		final float x1 = tileW * c; //float x1 = tileW * Math.max(start, c-1);
		final float y1 = y0 + tileH;
		
		p5.vertex(x0, y0);
		p5.vertex(x1, y0);
		p5.vertex(x1, y1);
		p5.vertex(x0, y1);
	}

	public void drawTris(SceneData sceneData) 
	{		
		final int tileW = game.tileW(), tileH = game.tileH();		
		final float[] verts = sceneData.vertexData.arr;
		final ArrListI tris = sceneData.triIndexData; 
		
		p5.beginShape(PApplet.TRIANGLES);			
		
		for (int i = 0; i < tris.size;) 
		{
			int ind0 = tris.arr[i++] * 2;
			int ind1 = tris.arr[i++] * 2;
			int ind2 = tris.arr[i++] * 2;

			float x0 = tileW * verts[ind0++];
			float y0 = tileH * verts[ind0];
			float x1 = tileW * verts[ind1++];
			float y1 = tileH * verts[ind1];
			float x2 = tileW * verts[ind2++];
			float y2 = tileH * verts[ind2];

			p5.vertex(x0, y0);
			p5.vertex(x1, y1);
			p5.vertex(x2, y2);
		}
		
		p5.endShape();		
	}
	
	public void drawLines(SceneData sceneData) 
	{		
		final int tileW = game.tileW(), tileH = game.tileH();
			
		ArrListI lines = sceneData.lineIndexData;
		float[] verts = sceneData.vertexData.arr;
		
		p5.stroke(170, 180, 200);
		p5.strokeWeight(1.5f);
		p5.beginShape(PApplet.LINES);
		
		for(int i = 0; i < lines.size; )
		{
			int ind0 = lines.arr[i++];
			int ind1 = lines.arr[i++];
			
			float x0 = tileW * verts[ind0 * 2];
			float y0 = tileH * verts[ind0 * 2 + 1];
			float x1 = tileW * verts[ind1 * 2];
			float y1 = tileH * verts[ind1 * 2 + 1];
			
			p5.vertex(x0, y0);
			p5.vertex(x1, y1);
			
			//line(x0, y0, x1, y1);
		}
		
		p5.endShape();
		p5.strokeWeight(1);
	}
	
	
	public void drawLinesWithSplines(SceneData sceneData) 
	{		
		final int tileW = game.tileW(), tileH = game.tileH();
			
		float[] verts = sceneData.vertexData.arr;
		
		p5.stroke(170, 180, 200);
		p5.strokeWeight(1.5f);
		p5.noFill();
		
		for(int j = 0; j < sceneData.lineStripStarts.size(); j++)
		//for(int start = 0; start < sceneData.vertexData.size / 2; start++)
		{
			int start = sceneData.lineStripStarts.get(j);
			p5.beginShape();

			for(int curr = start; ; ) 
			{
				int child = ((NeighData2B)sceneData.neigh).children[curr];

				if (child == -1) 
					break;

				int ind0 = curr * 2;
				final float x0 = tileW * verts[ind0++];
				final float y0 = tileH * verts[ind0  ];				

				int ind1 = child * 2;
				final float x1 = tileW * verts[ind1++];
				final float y1 = tileH * verts[ind1  ];
				
				p5.vertex(x0, y0);
				p5.vertex(x1, y1);
				
				if (child == start) 
					break;

				curr = child;
			}
			
			p5.endShape();
		}
		p5.strokeWeight(1);
	}
	

	public void drawLinesWithSplines1(SceneData sceneData) 
	{		
		final int tileW = game.tileW(), tileH = game.tileH();
		
		final ArrListI lines = sceneData.lineIndexData;
		final float[] verts = sceneData.vertexData.arr;
		
		p5.stroke(170, 180, 200);
		p5.strokeWeight(1.5f);
		
		for(int i = 0; i < lines.size; ) 
		{
			int ind1 = lines.arr[i++];
			int ind2 = lines.arr[i++];
			
			//int ind0 = sceneData.neigh.otherNeighbor(ind1, ind2); 
			//int ind3 = sceneData.neigh.otherNeighbor(ind2, ind1); 
			int ind0 = sceneData.neigh.getParent(ind1); 
			int ind3 = sceneData.neigh.getChild(ind2); 
			ind0 = ind0 == -1 ? ind1 : ind0;
			ind3 = ind3 == -1 ? ind2 : ind3;

			float x1 = verts[ind1 * 2];
			float y1 = verts[ind1 * 2 + 1];
			float x2 = verts[ind2 * 2];
			float y2 = verts[ind2 * 2 + 1];

			float x0, y0, x3, y3;
			if (ind0 == -1) {
				x0 = x1;
				y0 = y1;
			} else {
				x0 = verts[ind0 * 2];
				y0 = verts[ind0 * 2 + 1];
			}
			if (ind3 == -1) {
				x3 = x2;
				y3 = y2;
			} else {
				x3 = verts[ind3 * 2];
				y3 = verts[ind3 * 2 + 1];
			}
			
			int numSlices = 6;
			
			p5.beginShape(PApplet.LINE_STRIP);
			
			p5.vertex(x1*tileW, y1*tileH);
			
			for(int p = 1; p < numSlices; p++) {
				float t = p / (float)numSlices;
				
				float x, y;
				if (game.conf().splinesAreLinear) {
					x = x1 + (x2-x1)*t;
					y = y1 + (y2-y1)*t;
				} else {
					x = (2 * x1) + (-x0 + x2) * t + (2*x0-5*x1+4*x2-x3)*t*t + (-x0+3*x1-3*x2+x3)*t*t*t;
					y = (2 * y1) + (-y0 + y2) * t + (2*y0-5*y1+4*y2-y3)*t*t + (-y0+3*y1-3*y2+y3)*t*t*t;
					x *= 0.5f;
					y *= 0.5f;
				}
				
				p5.vertex(x*tileW, y*tileH);
			}
			
			p5.vertex(x2*tileW, y2*tileH);
			
			p5.endShape();
		}
		
		p5.strokeWeight(1);
	}

	private PApplet p5;
	private GameMarch game;

}
