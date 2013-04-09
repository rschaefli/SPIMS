import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class ImageComparator {
	
	private ImageHandler sourceHandler;
	private ImageHandler patternHandler;
	private BufferedImage sourceImage;
	private BufferedImage patternImage;
	private String patternHash;
	
	public ImageComparator(ImageHandler patternHandler, ImageHandler sourceHandler, String patternHash){
		this.sourceHandler = sourceHandler;
		this.patternHandler = patternHandler;
		this.sourceImage = sourceHandler.getImage();
		this.patternImage = patternHandler.getImage();
		this.patternHash = patternHash;
	}
	
    public void compare() {
        // Get our initial set of potential top left corners.
        CornerManager cornerManager = new CornerManager(patternHandler, sourceHandler);
        
        PotentialMatchManager potentialMatchManager = new PotentialMatchManager(cornerManager);
        
        // Get a map of Locations -> PHashes
        HashMap<Point, String> hashes = getPHashesOfLocations(sourceImage, potentialMatchManager.findPotentialMatches());
        // Pass off our results to the match handler
        MatchManager matchManager = new MatchManager();
        for (Entry<Point, String> entry : hashes.entrySet()) {
            Point location = entry.getKey();
            String subimageHash = entry.getValue();
            int difference = getHammingDistance(patternHash, subimageHash);
            
            Match m = new Match(patternHandler, sourceHandler, location, difference);
            if(m.isMatch()) {
            	//System.out.println("Match found @ " + location.x + "," + location.y + "- " + difference);
            	matchManager.add(m);
            }
        }
        
        // Print out our matches
        matchManager.printMatches();
    }
    
    
    // Get a the PHash of a subimage with top left corner at location
    // and a size of patternWidth x patternHeight
    private String getPHashOfSubImage(BufferedImage sourceImage, Point location, int patternWidth, int patternHeight) {
        BufferedImage subImage = sourceImage.getSubimage(location.x, location.y, patternWidth, patternHeight);
        PHash imageHasher = new PHash();
        String hash = imageHasher.getHash(subImage);
        
        return hash;
    }
    
    // Get the PHashes of from a list of locations representing the top left corners of
    // potential matches within the source image
    private HashMap<Point, String> getPHashesOfLocations(BufferedImage sourceImage, LinkedList<PotentialMatch> potentialMatches) {
        HashMap<Point, String> hashes = new HashMap<Point, String>();

        for (int i = 0; i < potentialMatches.size(); i++) {
            Point location = potentialMatches.get(i).getLocation();
            hashes.put(location, getPHashOfSubImage(sourceImage, location, potentialMatches.get(i).getWidth(), potentialMatches.get(i).getHeight()));
        }

        return hashes;
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
