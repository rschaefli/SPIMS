import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages and organizes potential matches based on match pHashes
 */
public class PotentialMatchManager {
	
	private CornerManager cornerManager; // Manages match corners
	private int currentCornerIndex;      // Index of current corner to work with

	/**
	 * CONSTRUCTOR
	 * 
	 * @param cornerManager -- Corner Manager
	 */
	public PotentialMatchManager(CornerManager cornerManager) {
		this.cornerManager = cornerManager;
		this.currentCornerIndex = 0;
	}
	
	/**
	 * Returns the next batch of size BATCH_SIZE potential matches
	 * 
	 * @return List of Potential Matches
	 */
	public List<PotentialMatch> getNextBatch() {
		ArrayList<PotentialMatch> potentialMatches = new ArrayList<PotentialMatch>();
		
		
		for(Corner topLeft : cornerManager.getRangeOfTopLeftCorners(currentCornerIndex, Constants.BATCH_SIZE)) {
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
		currentCornerIndex += Constants.BATCH_SIZE;
		
		return potentialMatches;
	}
	
	/**
	 * Obtain a pHash of a sub-image from the source image
	 * 
	 * @param sImage  -- Source image
	 * @param loc     -- Top left sub-image location
	 * @param pWidth  -- Pattern width
	 * @param pHeight -- Pattern height
	 * 
	 * @return String pHash of sub-image
	 */
    private String getPHashOfSubImage(BufferedImage sImage, Point loc, int pWidth, int pHeight) {
        BufferedImage subImage = sImage.getSubimage(loc.x, loc.y, pWidth, pHeight);
        String hash = PHash.createHash(subImage);
        
        return hash;
    }
}