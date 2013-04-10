import java.util.LinkedList;

public class PotentialMatchManager {
	
	private CornerManager cornerManager;
	private int matchCount;

	public PotentialMatchManager(CornerManager cornerManager, int matchCount) {
		this.cornerManager = cornerManager;
		this.matchCount = matchCount;
	}
		
	public LinkedList<PotentialMatch> findPotentialMatches() {
		LinkedList<PotentialMatch> potentialMatches = new LinkedList<PotentialMatch>();
		
		for(Corner topLeft : cornerManager.getBestTopLeftCorners(matchCount)) {
			
			//System.out.println(topLeft.getPoint().x + "," + topLeft.getPoint().y + " - " + topLeft.getColorDifference().getAverageDifference());
			
			potentialMatches.add(new PotentialMatch(topLeft.getPoint(),
													cornerManager.getPatternImageHandler().getImage().getWidth(),
													cornerManager.getPatternImageHandler().getImage().getHeight()));
		}
		
		//System.out.println("Total potential matches: " + potentialMatches.size());
		return potentialMatches;
	}
}