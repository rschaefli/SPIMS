package ImageMatcher;

import java.awt.Point;

public class PotentialMatch {

	private Point location;
	private int width;
	private int height;
	private double scaleApplied;
	
	public PotentialMatch(Point loc, int width, int height, double scaleApplied) {
		this.location = loc;
		this.width = width;
		this.height = height;
		this.scaleApplied = scaleApplied;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public double getScaleApplied() {
		return scaleApplied;
	}
	
	public void setScaleApplied(double scaleApplied) {
		this.scaleApplied = scaleApplied;
	}
	
}
