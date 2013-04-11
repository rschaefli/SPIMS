import java.awt.Point;

public class Match {
	
	private final static int HIGHEST_ACCEPTABLE_DIFFERENCE = 5;
	
	public ImageHandler patternHandler;
	public ImageHandler sourceHandler;
	public Point location;
	public int difference;
	
	public Match(ImageHandler patternHandler, ImageHandler sourceHandler, Point location, int difference) {
		this.patternHandler = patternHandler;
		this.sourceHandler = sourceHandler;
		this.location = location;
		this.difference = difference;
	}
	
	public boolean isMatch() {
		return difference < HIGHEST_ACCEPTABLE_DIFFERENCE;
	}
}