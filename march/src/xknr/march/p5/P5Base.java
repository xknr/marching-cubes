package xknr.march.p5;

import processing.core.PApplet;

public class P5Base extends PApplet 
{
	@Override
	public void keyPressed() {
		keyboard.onKeyDown(keyCode);
	}
	
	@Override
	public void keyReleased() {
		keyboard.onKeyUp(keyCode);
	}
	
	@Override
	public void mousePressed() {		
		mouse.onButtonDown(mouseButton);
	}

	@Override
	public void mouseReleased() {		
		mouse.onButtonUp(mouseButton);
	}
	
	public boolean isMouseDown(int mouseButton) {
		return mouse.isMouseDown(mouseButton);
	}
	
	public boolean isKeyDown(int keyCode) {
		return keyboard.isKeyDown(keyCode);
	}

	
	protected IGame game;
	protected Timing timing = new Timing();

	private static final boolean printPressedKeys = false; 
	private static final boolean printPressedMouseButtons = false;
	
	protected Mouse mouse = new Mouse(this, printPressedMouseButtons);
	protected Keyboard keyboard = new Keyboard(this, printPressedKeys);

	public float getElapsed() {
		return timing.time;		
	}

	public float frameDelta() {
		return timing.frameDelta;
	}
}
