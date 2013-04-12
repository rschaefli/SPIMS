import java.awt.Point;

/**
 * Represents a potential corner location
 */
public class Corner implements Comparable<Corner> {
    
    private Point point;                     // Top left location
    private ColorDifference colorDifference; // Corner color difference
    
    /**
     * CONSTRUCTOR
     * 
     * @param point           -- Top left location
     * @param colorDifference -- Color difference between the pattern
     *                           image and source image at this location
     */
    public Corner(Point point, ColorDifference colorDifference) {
        this.point = point;
        this.colorDifference = colorDifference;
    }
    
    /**
     * Compares corners based on their average color difference
     */
    public int compareTo(Corner potentialMatch) {
        float averageDifference = colorDifference.getAverageDifference();
        float potentialMatchAverageDifference =  potentialMatch.colorDifference.getAverageDifference();
        
        return  averageDifference < potentialMatchAverageDifference ? -1 : averageDifference > potentialMatchAverageDifference ? 1 : 0;
    }

    /** ---------- GETTERS ---------- */
    
	public Point getPoint() {
		return point;
	}

	public ColorDifference getColorDifference() {
		return colorDifference;
	}
}