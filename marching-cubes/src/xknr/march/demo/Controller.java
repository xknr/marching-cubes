package xknr.march.demo;

import processing.core.PApplet;
import xknr.march.p5.KeyCodes;

public class Controller 
{	
	public Controller(P5Squares p5, GameMarch game) {
		this.game = game;
		this.p5 = p5;
	}

	public void postUpdate() {
		game.postUpdate();
	}

	public void controllerOnKeyDown(int keyCode) 
	{	
		
		boolean changed = false;
		
		if (keyCode == KeyCodes.UP) 
		{
			changed = game.tileWState().scaleUp();
		} 
		else if (keyCode == KeyCodes.DN) 
		{
			changed = game.tileWState().scaleDown();
		}

		if (changed) 
		{
			PApplet.println("tileW", game.tileWState().tileWReqToInt());
			
			game.reset();
		}
	}
	
	void gameUpdateControls() 
	{		
		final float thresChangeSpeed = 0.1f;
		
		if (p5.isKeyDown(KeyCodes.NUMPAD_MINUS)) {
			game.incThres(-thresChangeSpeed * p5.frameDelta());
		} else if (p5.isKeyDown(KeyCodes.NUMPAD_PLUS)) {
			game.incThres(+thresChangeSpeed * p5.frameDelta());
		}
		
		game.conf().useSplines = p5.isKeyDown(KeyCodes.F1);
		game.conf().drawTris = p5.isKeyDown(KeyCodes.SPACE);	
	}
	
	private P5Squares p5;
	private GameMarch game;
}

