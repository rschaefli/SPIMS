package ImageMatcher;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.List;

import ImageMatcher.ImageHandler.FILE_TYPE;

public class ImageComparator implements Comparator {
	
	private int PIXEL_COLOR_ERROR_MARGIN = 5;
	private int PHASH_DISTANCE_BUFFER = 5;
    private int AVERAGE_DIFFERENCE_BUFFER = 35;
	
	private ImageHandler sourceHandler;
	private ImageHandler patternHandler;
	private BufferedImage sourceImage;
	private BufferedImage patternImage;
	
	public ImageComparator(ImageHandler patternHandler, ImageHandler sourceHandler){
		this.sourceHandler = sourceHandler;
		this.patternHandler = patternHandler;
		this.sourceImage = sourceHandler.getImage();
		this.patternImage = patternHandler.getImage();
	}
	
    @Override
    public void compare() {
        PHash imageHash = new PHash();
        
        String patternHash = imageHash.getHash(patternImage);
     
        // Become more lenient when dealing with GIF files
        if (patternHandler.getType().equals(FILE_TYPE.GIF) || sourceHandler.getType().equals(FILE_TYPE.GIF)) {
            PIXEL_COLOR_ERROR_MARGIN += 30;
            PHASH_DISTANCE_BUFFER += 45;
            AVERAGE_DIFFERENCE_BUFFER += 15;
        }
        
        // Get our initial set of potential top left corners.
        PotentialMatchManager potentialMatchManager = findPossibleTopLeftCorners();

        // Filter down possible top left corners
        //possibleTopLeftCorners = getProbableTopLeftCorners(possibleTopLeftCorners, 5);
        
        // Get a map of Locations -> PHashes
        HashMap<Point, String> hashes = getPHashesOfLocations(sourceImage, potentialMatchManager.getBestMatches(25));
        // Pass off our results to the match handler
        MatchHandler matchHandler = new MatchHandler();
        for (Entry<Point, String> entry : hashes.entrySet()) {
            Point location = entry.getKey();
            String subimageHash = entry.getValue();
            int difference = getHammingDistance(patternHash, subimageHash);
            
            Match m = new Match(patternHandler, sourceHandler, location, difference);
            if(m.isMatch()) {
            	matchHandler.add(m);
            }
        }
        
        // Print out our matches
        matchHandler.printMatches();
    }

    // Works in conjunction with getAxisColors to determine if there are potential
    // pattern image matches within a source image
    private PotentialMatchManager findPossibleTopLeftCorners() {
        HashMap<Point, Color> patternImageColors = getPixelColors(patternImage);
        PotentialMatchManager potentialMatchManager = new PotentialMatchManager();
        int offPixelsToAllow = 5;

        for (int i = 0; i <= sourceImage.getWidth() - patternImage.getWidth(); i++) {
            for (int j = 0; j <= sourceImage.getHeight() - patternImage.getHeight(); j++) {
                boolean isPotentialMatch = true;
                int offPixelCount = 0;
                ColorDifference difference = new ColorDifference(0, 0, 0);

                //Loop through the pixels and see if we have any matches
                for (Entry<Point, Color> entry : patternImageColors.entrySet()) {
                    Point p = entry.getKey();
                    Color patternPixelColor = entry.getValue();

                    Color sourcePixelColor = new Color(sourceImage.getRGB(i + p.x, j + p.y));

                    if (!isColorCloseTo(patternPixelColor, sourcePixelColor)) {
                        offPixelCount++;
                    }
                    
                    difference.addColorDifference(differenceBetweenColors(patternPixelColor, sourcePixelColor));
                    
                    isPotentialMatch = isPotentialMatch && (offPixelCount < offPixelsToAllow);                   
                }

                // If we have a potential match, add origin to result
                if (isPotentialMatch) {
                    potentialMatchManager.add(new PotentialMatch(new Point(i, j), difference));
                }
            }
        }

        return potentialMatchManager;
    }
    
    // Filter down the list of top left corners by random choosing a pixel and checking
    // that it is close enough to the pattern image pixel
    private ArrayList<Point> filterPossibleTopLeftCorners(BufferedImage sourceImage, BufferedImage patternImage, ArrayList<Point> topLeftCorners){
    	ArrayList<Point> result = new ArrayList<Point>();
    	
    	// First we get a random pixel within the pattern image
    	Random r = new Random();
    	int randomX = r.nextInt(patternImage.getWidth());
    	int randomY = r.nextInt(patternImage.getHeight());
    	Color patternColor = new Color(patternImage.getRGB(randomX, randomY));
    	
    	// Now we get that same pixel relative to the top left corners
    	// of the source image.
    	for(Point p : topLeftCorners) {
    		Color sourceColor = new Color(sourceImage.getRGB(p.x + randomX, p.y + randomY));
    		if(isColorCloseTo(patternColor, sourceColor)) {
    			result.add(p);
    		}
    	}
    	
    	return result;
    }
    
    // Filter down a list of possible top left corners until there are less than max
    private ArrayList<Point> getProbableTopLeftCorners(ArrayList<Point> possibleTopLeftCorners, int max) {
    	ArrayList<Point> probableTopLeftCorners = possibleTopLeftCorners;
    	
    	// Filter down until we have <= max top left corners
    	while(probableTopLeftCorners.size() > max) {
    		probableTopLeftCorners = filterPossibleTopLeftCorners(sourceImage, patternImage, probableTopLeftCorners);
    	}
    	
    	return probableTopLeftCorners;
    }
    
    // Get howManyPerAxis pixels down the X and Y axis
    // This is used to elaborate on just comparing the top left pixel of sub images
    // The goal is to get less potential matches in the source image by checking more than 1 pixel
    private HashMap<Point, Color> getPixelColorsByAxis(BufferedImage image, int howManyPerAxis) {
    	HashMap<Point, Color> result = new HashMap<Point, Color>();

        // Bound the number of pixels to get on the X and Y axis
        int pixelsForXAxis = image.getWidth() > howManyPerAxis ? howManyPerAxis : image.getWidth();
        int pixelsForYAxis = image.getHeight() > howManyPerAxis ? howManyPerAxis : image.getHeight();

        // Get the color values of pixels on the X axis
        for (int i = 0; i < pixelsForXAxis; i++) {
            Color curPixel = new Color(image.getRGB(i, 0));
            result.put(new Point(i,0), curPixel);
        }

        // Get the color values of pixels on the Y axis
        // NOTE: we are double checking 0,0 - shouldnt be much of an issue
        for (int i = 0; i < pixelsForYAxis; i++) {
            Color curPixel = new Color(image.getRGB(0, i));
            result.put(new Point(0,i), curPixel);
        }

        return result;
    }

    
    // This is used to elaborate on just comparing the top left pixel of sub images
    // The goal is to get less potential matches in the source image by checking more than 1 pixel
    // back on the left side of the second row.
    private HashMap<Point, Color> getPixelColors(BufferedImage image) {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        
        if(image.getHeight() > 10 && image.getWidth() > 10){
            //Get the first five pixels starting from each corner
            //And going 5 up/down and 5 right/left
            
            //Starting top left corner, going out 5 down
            for(int i = 0; i < 1; i++){
                for(int j = 0; j < 6; j++){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
            //Starting top left corner, going out 5 right
            for(int i = 0; i < 6; i++){
                for(int j = 0; j < 1; j++){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
            //Starting bottom left corner, going out 5 up
            for(int i = 0; i < 1; i++){
                for(int j = image.getHeight() - 1; j > image.getHeight() - 6; j--){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
                        
            //Starting bottom left corner, going out 5 right
            for(int i = 0; i < 6; i++){
                for(int j = image.getHeight() -1; j > image.getHeight() - 2; j--){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
            //Starting bottom right corner, going out 5 left
            for(int i = image.getWidth() - 1; i > image.getWidth() - 6; i--){
                for(int j = image.getHeight() - 1; j > image.getHeight() - 2; j--){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
            //Starting bottom right corner, going out 5 up
            for(int i = image.getWidth() - 1; i > image.getWidth() - 2; i--){
                for(int j = image.getHeight() - 1; j > image.getHeight() - 6; j--){
                    result = storePixelColor(i, j, image, result);
                }
            }

            //Starting top right corner, going out 5 down
            for(int i = image.getWidth() - 1; i > image.getWidth() - 2; i--){
                for(int j = 0; j < 6; j++){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
            //Starting top right corner, going out 5 left
            for(int i = image.getWidth() - 1; i > image.getWidth() - 6; i--){
                for(int j = 0; j < 1; j++){
                    result = storePixelColor(i, j, image, result);
                }
            }
            
        }else if(image.getHeight() > 1 && image.getHeight() > 1){
            //Get just the four corners
            result = storePixelColor(0,0,image,result);
            result = storePixelColor(image.getWidth() - 1, 0,image,result);
            result = storePixelColor(image.getWidth() - 1, image.getHeight() - 1, image, result);
            result = storePixelColor(0, image.getHeight() - 1, image, result);
        }else{
            result = storePixelColor(0,0,image,result);
        }
        return result;
    }

    // Check if color c1s RGB values are all within errorMargin difference
    // of c2s RGB values
    private Boolean isColorCloseTo(Color c1, Color c2) {
        int c1Red = c1.getRed();
        int c2Red = c2.getRed();
        boolean isRedInRange = (c1Red >= c2Red - PIXEL_COLOR_ERROR_MARGIN) && (c1Red <= c2Red + PIXEL_COLOR_ERROR_MARGIN);

        int c1Green = c1.getGreen();
        int c2Green = c2.getGreen();
        boolean isGreenInRange = (c1Green >= c2Green - PIXEL_COLOR_ERROR_MARGIN) && (c1Green <= c2Green + PIXEL_COLOR_ERROR_MARGIN);

        int c1Blue = c1.getBlue();
        int c2Blue = c2.getBlue();
        boolean isBlueInRange = (c1Blue >= c2Blue - PIXEL_COLOR_ERROR_MARGIN) && (c1Blue <= c2Blue + PIXEL_COLOR_ERROR_MARGIN);

        return (isRedInRange && isGreenInRange) || (isRedInRange && isBlueInRange) || (isBlueInRange && isGreenInRange);
        //(isRedInRange && isGreenInRange && isBlueInRange);

    }
    
    private ColorDifference differenceBetweenColors(Color c1, Color c2){
        int r1 = c1.getRed();
        int r2 = c2.getRed();
        int diffR = Math.abs(r2 - r1);
        
        int b1 = c1.getBlue();
        int b2 = c2.getBlue();
        int diffB = Math.abs(b2 - b1);
        
        int g1 = c1.getGreen();
        int g2 = c2.getGreen();
        int diffG = Math.abs(g2 - g1);
        
        return new ColorDifference(diffR, diffG, diffB);
    }
    
        private String strinDifferenceBetweenColors(Color c1, Color c2){
        int r1 = c1.getRed();
        int r2 = c2.getRed();
        String diffR = Integer.toString(r2 - r1);
        
        int b1 = c1.getBlue();
        int b2 = c2.getBlue();
        String diffB = Integer.toString(b2 - b1);
        
        int g1 = c1.getGreen();
        int g2 = c2.getGreen();
        String diffG = Integer.toString(g2 - g1);
        
        return diffR + ", " + diffB + ", " + diffG;
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
    private HashMap<Point, String> getPHashesOfLocations(BufferedImage sourceImage, List<PotentialMatch> potentialMatches) {
        HashMap<Point, String> hashes = new HashMap<Point, String>();

        for (int i = 0; i < potentialMatches.size(); i++) {
            Point location = potentialMatches.get(i).point;
            hashes.put(location, getPHashOfSubImage(sourceImage, location, patternImage.getWidth(), patternImage.getHeight()));
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

    private HashMap<Point, Color> storePixelColor(int i, int j, BufferedImage image, HashMap<Point, Color> result) {
        Point curLocation = new Point(i, j);
        Color curPixel = new Color(image.getRGB(i, j));

        result.put(curLocation, curPixel);
        
        return result;
    }
}