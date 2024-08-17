package xknr.march.algo;

import xknr.march.demo.GameMarch;
import xknr.march.util.Mth;

public class TileWState 
{	
	public TileWState(GameMarch game) {
		
		this.game = game;
		// Find an initial value which is minValue * growth ^ k where k is integer.
		int targetTileW = 20;
		double ratio = Mth.log(tileWScaleFactor, targetTileW / (double)minTileW);
		tileWRequested = Math.pow(tileWScaleFactor, (int)ratio) * minTileW;		
	}
	
	public int tileWReqToInt() {
		int tileW = (int)tileWRequested;
		tileW = Math.max(tileW, minTileW);
		return tileW;
	}

	/**
	 * @return Returns whether there was a valid change.
	 */
	public boolean scaleUp() {
		
		int origTileW = game.tileW();
		
		tileWRequested *= tileWScaleFactor;
		
		return tileWReqToInt() != origTileW;
	}

	/**
	 * @return Returns whether there was a valid change.
	 */
	public boolean scaleDown() 
	{
		int origTileW = game.tileW();

		double tileWRequestedOrig = tileWRequested;
		tileWRequested /= tileWScaleFactor;
		if (tileWRequested < minTileW) {
			tileWRequested = tileWRequestedOrig;
		}

		return tileWReqToInt() != origTileW;
	}
	
	private final GameMarch game;
	private static final float tileWScaleFactor = 1.5f;
	private static final int minTileW = 4; 
	private double tileWRequested;
}