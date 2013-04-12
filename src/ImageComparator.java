import java.util.List;

public class ImageComparator {
	
	private final static int BATCH_SIZE = 5;
	
	private ImageHandler sourceHandler;
	private ImageHandler patternHandler;
	private String patternHash;
	
	public ImageComparator(ImageHandler patternHandler, ImageHandler sourceHandler, String patternHash){
		this.sourceHandler = sourceHandler;
		this.patternHandler = patternHandler;
		this.patternHash = patternHash;
	}
	
    public void compare() {
        // Get our initial set of potential top left corners.
        CornerManager cornerManager = new CornerManager(patternHandler, sourceHandler);
        
        // Get the first batch of potential matches we want from the corner manager
        PotentialMatchManager potentialMatchManager = new PotentialMatchManager(cornerManager, BATCH_SIZE);
        
        // Get everything we consider to be a match
        MatchManager matchManager = getMatches(potentialMatchManager);
        
        // Print out our matches
        matchManager.printMatches();
    }
    
    // Given the pHash values of all our potential matches, pick out our final matches
    private MatchManager getMatches(PotentialMatchManager potentialMatchManager) {
        MatchManager matchManager = new MatchManager();
        boolean allExactMatches = true; // Keep batching while we have exact matches
        
        // Get our first batch of potential matches
        List<PotentialMatch> potentialMatches = potentialMatchManager.getNextBatch();
        
        // Here we handle detecting an arbitrary number of exact matches
        // As long as our best matches are exact matches, keep PHashing more potential matches
        while(allExactMatches && potentialMatches.size() > 0) {
        	// Loop through our potential matches and check if they are matches
        	for(PotentialMatch pm : potentialMatches) {
        		int difference = getHammingDistance(patternHash, pm.getPHash());
        		
        		//System.out.println("Point " + pm.getLocation().x + "," + pm.getLocation().y + " has PHash of " + difference);
        		
        		// Is this a potential match a match?
        		Match m = new Match(patternHandler, sourceHandler, pm.getLocation(), difference);
        		// If this is a small pattern, we have already compared all pixels and can just add it
                if(patternHandler.isSmallImage()) {
                	matchManager.add(m);
                } else if(m.isMatch()) {
                	matchManager.add(m);
                }
                
                // Keep track of whether we have all exact matches or not
                allExactMatches &= (difference == 0);
        	}
        	
        	//System.out.println("---------------------");
        	
        	// Get the next batch of potential matches if we had all exact matches
        	if(allExactMatches) {
        		potentialMatches = potentialMatchManager.getNextBatch();
        	}
        }
        
        return matchManager;
    }

    // Get the difference between 2 strings of equal length
    private int getHammingDistance(String one, String two) {
        if (one.length() != two.length()) {
            return -1;
        }
        int counter = 0;
        for (int i = 0; i < one.length(); i++) {
            if (one.charAt(i) != two.charAt(i)) {
                counter++;
            }
        }
        return counter;
    }
}