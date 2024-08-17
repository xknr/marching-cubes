package xknr.march.demo;

public class MousePower 
{
	public MousePower(P5Squares p5) {
		this.p5 = p5;
	}
	
	public void update() {
		// Mouse power easing code:
		float minMousePower = -10;
		float maxMousePower = 10;
		float targetMousePower;
		float mousePowerEase = 0.1f;
		if (p5.isMouseDown(P5Squares.LEFT)) {
			mousePowerEase = 0.2f;
			targetMousePower = maxMousePower;
		} else if (p5.isMouseDown(P5Squares.RIGHT)) {
			mousePowerEase = 0.2f;
			targetMousePower = minMousePower;
		} else {
			targetMousePower = 0;
			mousePowerEase = 0.5f;
		}
		mousePower += (targetMousePower - mousePower) * mousePowerEase;
		mousePower = P5Squares.constrain(mousePower, minMousePower, maxMousePower);
	}
	
	private float mousePower;
	public float mousePower() {
		return mousePower;
	}
	
	private P5Squares p5;
}