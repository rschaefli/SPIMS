import java.awt.Point;

/**
 * Represents a match of the pattern image within the source image
 */
public class Match {
	
	public ImageHandler patternHandler; // Pattern Handler
	public ImageHandler sourceHandler;  // Source Handler
	public Point location;              // Match Location
	public int difference;              // PHashed Hamming difference 
	
	/**
	 * CONSTRUCTOR
	 * 
	 * @param patternHandler -- Pattern Handler
	 * @param sourceHandler  -- Source Handler
	 * @param location       -- Match Location (Top left pixel location)
	 * @param difference     -- PHashed Hamming Difference
	 */
	public Match(ImageHandler patternHandler, ImageHandler sourceHandler, Point location, int difference) {
		this.patternHandler = patternHandler;
		this.sourceHandler = sourceHandler;
		this.location = location;
		this.difference = difference;
	}
	
	/**
	 * @return Is the match within the acceptable hamming distance difference?
	 */
	public boolean isMatch() {
		return difference < Constants.HIGHEST_ACCEPTABLE_DIFFERENCE;
	}
	
	/**
	 * @return Is the match an exact match?
	 */
	public boolean isExactMatch() {
		return difference == 0;
	}
}