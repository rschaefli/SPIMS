package ImageMatcher;

import java.awt.Point;
import java.util.LinkedList;

public class PotentialMatchManager {

	private final int MATCH_COUNT = 1000;
	
	private CornerManager cornerManager;
	private double slope;

	public PotentialMatchManager(CornerManager cornerManager) {
		this.cornerManager = cornerManager;
		this.slope = cornerManager.getPatternImageHandler().getAspectRatio();
	}
		
	public LinkedList<PotentialMatch> findPotentialMatches() {
		LinkedList<PotentialMatch> matches = new LinkedList<PotentialMatch>();
		
		for(Corner topLeft : cornerManager.getBestTopLeftMatches(MATCH_COUNT)) {
			for(Corner botRight : cornerManager.getBestBottomRightMatches(MATCH_COUNT)) {
				if(onSlope(topLeft.getPoint(), botRight.getPoint())) {
					double scale = scaleFactor(topLeft.getPoint(), botRight.getPoint());
					int width = (int) scale * patternImg().getWidth();
					int height = (int) scale * patternImg().getHeight();
					
					matches.add(new PotentialMatch(topLeft.getPoint(), width, height, scale));
				}
			}
		}
		System.out.println(matches.size());
		return matches;
	}
	
	private boolean onSlope(Point p1, Point p2) {
        if(p1.x - p2.x == 0){
            return false;
        }
		double pointSlope = (double) (p1.y - p2.y) / (double) (p1.x - p2.x);
		return slope == pointSlope;
	}
	
	private double scaleFactor(Point p1, Point p2) {
		double dist = Point.distance(p1.x, p1.y, p2.x, p2.y);
		return dist / patternImg().getCornerDist();
	}
	
	private ImageHandler patternImg() {
		return cornerManager.getPatternImageHandler();
	}
	
}
