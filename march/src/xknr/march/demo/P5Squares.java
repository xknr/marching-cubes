package xknr.march.demo;

import processing.core.PApplet;
import xknr.march.p5.P5Base;

/*
 * Controls:
 * --------- 
 * up/down arrow: increase/decrease grid resolution
 * numpad +/-: increase/decrease threshold value
 * left/right mouse button: create hill/hole
 * F1: use spline 
 */
public class P5Squares extends P5Base
{	
	public static final int canvasW = 1600, canvasH = 800;
	
	static {
		// PJOGL.profile = 1; // original 2
	}
	
	public static void main(String[] args)
	{		
		System.out.println("main enter");		
		
		P5Squares p5 = new P5Squares();
		GameMarch game = new GameMarch(p5);
		Controller cont = new Controller(p5, game);
		p5.game = game;
		p5.cont = cont;
		
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
	

	@Override
	public void draw() 
	{				
		
		if (firstDraw) {
			timing.reset();		
			game.reset();
			firstDraw = false;			
		}
		
		cont.gameUpdateControls();
		game.gamePreUpdate();

		game.draw();
		
		timing.endFrame();
		cont.postUpdate();

		displayFrameDelta();		
	}

	@Override
	public void keyPressed() {
		super.keyPressed();
		cont.controllerOnKeyDown(keyCode);		
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
	
	private Controller cont;
	private boolean firstDraw = true; 
	
}
