package xknr.march.demo;

public class MSConf 
{
	public MSConf(boolean useSplines, boolean drawLines, 
			boolean drawTris, boolean drawFullQuads,
			boolean zeroOutEdges) {
		this.useSplines = useSplines;
		this.drawLines = drawLines;
		this.drawTris = drawTris;
		this.drawFullQuads = drawFullQuads;
		this.zeroOutEdges = zeroOutEdges;
	}

	public boolean useSplines, drawLines, drawTris, drawFullQuads;
	public boolean zeroOutEdges;
}