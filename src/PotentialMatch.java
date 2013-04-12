import java.awt.Point;

/**
 * Represents a potential pattern->source image match
 */
public class PotentialMatch {

	private Point location;  // Match location
	private int width;       // Potential Match Width
	private int height;      // Potential Match Height
	private String pHash;    // Potential Match PHash
	
	/**
	 * CONSTRUCTOR
	 * 
	 * @param loc    -- Match location
	 * @param width  -- Match width
	 * @param height -- Match height
	 * @param pHash  -- Match pHash string
	 */
	public PotentialMatch(Point loc, int width, int height, String pHash) {
		this.location = loc;
		this.width = width;
		this.height = height;
		this.pHash = pHash;
	}
	
	/** ---------- GETTERS AND SETTERS ---------- */
	
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
	
	public String getPHash() {
		return pHash;
	}
}