import java.awt.Point;

/**
 *
 * @author Dan
 */
public class Corner implements Comparable<Corner> {
    
    private Point point;
    private ColorDifference colorDifference;
    
    public Corner(Point point, ColorDifference colorDifference) {
        this.point = point;
        this.colorDifference = colorDifference;
    }
    
    public int compareTo(Corner potentialMatch) {
        int averageDifference = colorDifference.getAverageDifference();
        int potentialMatchAverageDifference =  potentialMatch.colorDifference.getAverageDifference();
        
        return  averageDifference < potentialMatchAverageDifference ? -1 : averageDifference > potentialMatchAverageDifference ? 1 : 0;
    }

	public Point getPoint() {
		return point;
	}

	public ColorDifference getColorDifference() {
		return colorDifference;
	}
     
}
