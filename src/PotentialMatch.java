import java.awt.Point;

public class PotentialMatch {

	private Point location;
	private int width;
	private int height;
	
	public PotentialMatch(Point loc, int width, int height) {
		this.location = loc;
		this.width = width;
		this.height = height;
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
}
