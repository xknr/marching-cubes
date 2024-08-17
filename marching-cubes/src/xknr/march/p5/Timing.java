package xknr.march.p5;

public class Timing 
{	
	public void reset() {
		startTime = System.nanoTime();
		time = 0;
		frameDelta = 1 / 60f; // cheat
	}

	public void endFrame() {
		float prevTime = time;
		time = (float) ((System.nanoTime() - startTime) / 1e9);
		frameDelta = time - prevTime;
	}
	
	public float time;
	public float frameDelta;
	public long startTime;
}