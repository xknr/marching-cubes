package xknr.march.p5;

import java.util.HashSet;
import java.util.Set;

import xknr.march.demo.P5Squares;

public class Mouse 
{	
	public Mouse(P5Base p5, boolean printPressed) {
		this.p5 = p5;
		this.printPressed = printPressed;
	}

	public boolean isMouseDown(int mouseButton) {		
		return pressedMouseButtons.contains(mouseButton);
	}

	public void onButtonDown(int mouseButton) {
		if (printPressed) {
			System.out.format("mouse down: %d\n", mouseButton);
		}
		pressedMouseButtons.add(mouseButton);		
	}

	public void onButtonUp(int mouseButton) {
		if (pressedMouseButtons.contains(mouseButton))
			pressedMouseButtons.remove(mouseButton);		
	}

	private Set<Integer> pressedMouseButtons = new HashSet<Integer>();

	private boolean printPressed;
	private P5Base p5;
}
