import java.util.List;

/**
 * Compares a pattern image to a source image
 */
public class ImageComparator {
	
	private ImageHandler sHandler; // Source Handler
	private ImageHandler pHandler; // Pattern Handler
	private String patternHash;    // Pattern PHash String

	/**
	 * CONSTRUCTOR
	 * 
	 * @param patternHandler -- Pattern Handler
	 * @param sourceHandler  -- Source Handler
	 * @param patternHash    -- Pattern String Hash
	 */
	public ImageComparator(ImageHandler patternHandler, ImageHandler sourceHandler, String patternHash){
		this.sHandler = sourceHandler;
		this.pHandler = patternHandler;
		this.patternHash = patternHash;
	}

	/**
	 * Kicks off the pattern and source image comparison
	 */
	public void compare() {
		// Get our initial set of potential top left corners.
		CornerManager cornerManager = new CornerManager(pHandler, sHandler);

		// Get the first batch of potential matches we want from the corner manager
		PotentialMatchManager potentialMatchManager = new PotentialMatchManager(cornerManager);

		// Get everything we consider to be a match
		MatchManager matchManager = getMatches(potentialMatchManager);

		// Print out our matches
		matchManager.printMatches();
	}

	/**
	 * Obtains a Match Manager which contains all of the valid matches 
	 * 
	 * @param potentialMatchManager -- Potential Match Manager 
	 * 
	 * @return a Match manager with all of the valid matches
	 */
	private MatchManager getMatches(PotentialMatchManager potentialMatchManager) {
		MatchManager matchManager = new MatchManager();
		boolean allExactMatches = true; // Keep batching while we have exact matches

		// Get our first batch of potential matches
		List<PotentialMatch> potentialMatches = potentialMatchManager.getNextBatch();

		// Keep pHashing as long as our best matches are exact matches
		while(allExactMatches && potentialMatches.size() > 0) {
			
			// Loop through our potential matches and check if they are matches
			for(PotentialMatch pm : potentialMatches) {
				int difference = PHash.getHammingDistance(patternHash, pm.getPHash());
				Match m = new Match(pHandler, sHandler, pm.getLocation(), difference);

				// If this is a small pattern, we have already compared all pixels and can just add it
				if(pHandler.isSmallImage()) {
					
					// If we are at the first index in the batch, add this match
					// With low pixel counts, the first average color difference is the match
					// even if the PHash is off
					if(potentialMatches.indexOf(pm) == 0) {
						matchManager.add(m);
					} else if(m.isExactMatch()) {
						matchManager.add(m);
					}
				} else if(m.isMatch()) {
					matchManager.add(m);
				}

				// Keep track of whether we have all exact matches or not
				allExactMatches &= (m.isExactMatch());
			}

			// Get the next batch of potential matches if we had all exact matches
			if(allExactMatches) {
				potentialMatches = potentialMatchManager.getNextBatch();
			}
		}

		return matchManager;
	}
	
}