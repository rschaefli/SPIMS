package ImageMatcher;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import ImageMatcher.ImageHandler.FILE_TYPE;

public class ImageComparator implements Comparator {
	
	public final int PIXEL_COLOR_ERROR_MARGIN = 5;
	
    @Override
    public void compare(ImageHandler patternHandler, ImageHandler sourceHandler) {
        PHash imageHash = new PHash();
        String patternHash = imageHash.getHash(patternHandler.getImage());
     
        int colorDifferenceMargin = 5;
        int pHashDistanceBuffer = 5;

        // Become more lenient when dealing with GIF files
        if (patternHandler.getType().equals(FILE_TYPE.GIF) || sourceHandler.getType().equals(FILE_TYPE.GIF)) {
            colorDifferenceMargin += 15;
            pHashDistanceBuffer += 45;
        }
        
        // Get our initial set of potential top left corners.
        ArrayList<Point> possibleTopLeftCorners = findPossibleTopLeftCorners(sourceHandler.getImage(), patternHandler.getImage());

        // Filter down possible top left corners
        possibleTopLeftCorners = getProbableTopLeftCorners(sourceHandler.getImage(), patternHandler.getImage(), possibleTopLeftCorners, 5);
        
        HashMap<Point, String> hashes = getPHashesOfLocations(sourceHandler.getImage(),
                possibleTopLeftCorners,
                patternHandler.getWidth(),
                patternHandler.getHeight());

        //Hash map for final matches which we will sort through to find
        //the lowest distance of the matches.
        Point locationOfLowestMatch = null;
        int lowestDifference = 100;

        // Get a map of Locations -> PHashes
        // Compare each and check if we have a match at that location
        for (Entry<Point, String> entry : hashes.entrySet()) {
            Point location = entry.getKey();
            String subimageHash = entry.getValue();
            int difference = getHammingDistance(patternHash, subimageHash);

            if (difference != -1 && difference < pHashDistanceBuffer && difference < lowestDifference) {
                locationOfLowestMatch = location;
                lowestDifference = difference;
            }
        }

        if (locationOfLowestMatch != null && lowestDifference < 15) {
            System.out.println(patternHandler.getName() + " matches " +
            				   sourceHandler.getName() + " at " +
            				   patternHandler.getWidth() + "x" + patternHandler.getHeight() +
            				   "+" + locationOfLowestMatch.x +
            				   "+" + locationOfLowestMatch.y);
        }

    }

    // Works in conjunction with getAxisColors to determine if there are potential
    // pattern image matches within a source image
    private ArrayList<Point> findPossibleTopLeftCorners(BufferedImage sourceImage, BufferedImage patternImage) {
        HashMap<Point, Color> patternImagePixels = getPixelColors(patternImage, 30);
        ArrayList<Point> possibleCorners = new ArrayList<Point>();

        for (int i = 0; i < sourceImage.getWidth() - patternImage.getWidth(); i++) {
            for (int j = 0; j < sourceImage.getHeight() - patternImage.getHeight(); j++) {
                Point curPixel = new Point(i, j);
            	
                // If we have a potential match, add origin to result
                if (isTopLeftCornerPotentialMatch(sourceImage, patternImage, patternImagePixels, curPixel)) {
                    possibleCorners.add(curPixel);
                }
            }
        }

        return possibleCorners;
    }
    
    // Determine if this top left corner in the source image is a potential match to the pattern image
    private boolean isTopLeftCornerPotentialMatch(BufferedImage sourceImage, BufferedImage patternImage,
    											  HashMap<Point, Color> patternImagePixels, Point loc) {
    	boolean isPotentialMatch = true;
        int offPixelsToAllow = 5;
    	int offPixelCount = 0;

        // Loop through the pixels and see if we have any matches
        for (Entry<Point, Color> entry : patternImagePixels.entrySet()) {
            Point p = entry.getKey();
            Color patternPixelColor = entry.getValue();

            Color sourcePixelColor = new Color(sourceImage.getRGB(loc.x + p.x, loc.y + p.y));

            if (!isColorCloseTo(patternPixelColor, sourcePixelColor)) {
                offPixelCount++;
            }

            isPotentialMatch = isPotentialMatch && (offPixelCount < offPixelsToAllow);
        }
        
        return isPotentialMatch; 
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
    private ArrayList<Point> getProbableTopLeftCorners(BufferedImage sourceImage, BufferedImage patternImage, ArrayList<Point> possibleTopLeftCorners, int max) {
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
    private HashMap<Point, Color> getPixelColors(BufferedImage image, int limit) {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        
        mainloop:
        for (int j = 0; j < image.getHeight() - 1; j++) {
            for (int i = 0; i < image.getWidth() - 1; i += 30) {
                Point curLocation = new Point(i, j);
                Color curPixel = new Color(image.getRGB(i, j));

                result.put(curLocation, curPixel);

                if (result.size() == limit) {
                    break mainloop;
                }
            }
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
    private HashMap<Point, String> getPHashesOfLocations(BufferedImage sourceImage, ArrayList<Point> locations, int patternWidth, int patternHeight) {
        HashMap<Point, String> hashes = new HashMap<Point, String>();

        for (int i = 0; i < locations.size(); i++) {
            hashes.put(locations.get(i), getPHashOfSubImage(sourceImage, locations.get(i), patternWidth, patternHeight));
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