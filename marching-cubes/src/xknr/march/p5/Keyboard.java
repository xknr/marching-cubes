package xknr.march.p5;

import java.util.HashSet;
import java.util.Set;

import processing.core.PApplet;

public class Keyboard 
{
	public Keyboard(PApplet p5, boolean printPressed) {
		this.p5 = p5;
		this.printPressed = printPressed;
	}

	
	public boolean isKeyDown(int keyCode) {		
		return pressedKeys.contains(keyCode);
	}

	public void onKeyDown(int keyCode) {
		if (printPressed) { 
			System.out.format("key down: %d\n", keyCode);
		}
		pressedKeys.add(keyCode);		
	}
	
	public void onKeyUp(int keyCode) {
		if (pressedKeys.contains(keyCode))
			pressedKeys.remove(keyCode);				
		
	}

	private PApplet p5;
	private Set<Integer> pressedKeys = new HashSet<Integer>();
	private boolean printPressed;
}
