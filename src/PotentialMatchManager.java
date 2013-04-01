import java.awt.Point;
import java.util.LinkedList;

public class PotentialMatchManager {

	private final int MATCH_COUNT = 100;
	
	private CornerManager cornerManager;
	private double slope;

	public PotentialMatchManager(CornerManager cornerManager) {
		this.cornerManager = cornerManager;
		this.slope = cornerManager.getPatternImageHandler().getAspectRatio();
	}
		
	public LinkedList<PotentialMatch> findPotentialMatches() {
		LinkedList<PotentialMatch> potentialMatches = new LinkedList<PotentialMatch>();
		
		for(Corner topLeft : cornerManager.getBestTopLeftCorners(MATCH_COUNT)) {
			for(Corner botRight : cornerManager.getBestBottomRightCorners(MATCH_COUNT)) {
				if(onSlope(topLeft.getPoint(), botRight.getPoint())) {
					double scale = scaleFactor(topLeft.getPoint(), botRight.getPoint());
					int width = (int) (scale * patternImg().getWidth());
					int height = (int) (scale * patternImg().getHeight());
                    
					//System.out.println(topLeft.getPoint().x + "," + topLeft.getPoint().y + " - " + width + "," + height + " - " + scale);
					
					potentialMatches.add(new PotentialMatch(topLeft.getPoint(), width, height, scale));
				}
			}
		}
		
		//System.out.println("Total potential matches: " + potentialMatches.size());
		return potentialMatches;
	}
	
	private boolean onSlope(Point p1, Point p2) {
        if(p1.x - p2.x == 0) {
            return false;
        }
        // Plus one for matching bottom right!
        // TODO: Modify this if p2 is some corner other than bottom right
		double pointSlope = (double) (p1.y - (p2.y + 1)) / (double) (p1.x - (p2.x + 1));
		return slope == pointSlope;
	}
	
	private double scaleFactor(Point p1, Point p2) {
		// Plus one for matching bottom right!
        // TODO: Modify this if p2 is some corner other than bottom right
		double dist = Point.distance(p1.x, p1.y, p2.x + 1, p2.y + 1);
		return dist / patternImg().getCornerDist();
	}
	
	private ImageHandler patternImg() {
		return cornerManager.getPatternImageHandler();
	}
	
}
