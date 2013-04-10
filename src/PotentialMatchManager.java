import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PotentialMatchManager {
	
	private CornerManager cornerManager;
	private int batchSize;
	private int currentCornerIndex;

	public PotentialMatchManager(CornerManager cornerManager, int batchSize) {
		this.cornerManager = cornerManager;
		this.batchSize = batchSize;
		this.currentCornerIndex = 0;
	}
	
	// Will return the next batch of size batchSize potential matches
	// PHash will be computed directly here
	public List<PotentialMatch> getNextBatch() {
		ArrayList<PotentialMatch> potentialMatches = new ArrayList<PotentialMatch>();
		
		for(Corner topLeft : cornerManager.getRangeOfTopLeftCorners(currentCornerIndex, batchSize)) {
			
			//System.out.println(topLeft.getPoint().x + "," + topLeft.getPoint().y + " - " + topLeft.getColorDifference().getAverageDifference());
			
			String potentialMatchPHash = getPHashOfSubImage(cornerManager.getSourceImageHandler().getImage(),
															topLeft.getPoint(),
															cornerManager.getPatternImageHandler().getImage().getWidth(),
															cornerManager.getPatternImageHandler().getImage().getHeight());
			
			potentialMatches.add(new PotentialMatch(topLeft.getPoint(),
													cornerManager.getPatternImageHandler().getImage().getWidth(),
													cornerManager.getPatternImageHandler().getImage().getHeight(),
													potentialMatchPHash));
		}
		
		// Increment our new currentCornerIndex
		currentCornerIndex += batchSize;
		
		return potentialMatches;
	}
	
	// Get a the PHash of a subimage with top left corner at location
    // and a size of patternWidth x patternHeight
    private String getPHashOfSubImage(BufferedImage sourceImage, Point location, int patternWidth, int patternHeight) {
        BufferedImage subImage = sourceImage.getSubimage(location.x, location.y, patternWidth, patternHeight);
        PHash imageHasher = new PHash();
        String hash = imageHasher.getHash(subImage);
        
        return hash;
    }
	
	
}