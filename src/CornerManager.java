import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Finds the potential pattern corners locations within the source image
 */
public class CornerManager {
		
	private List<Corner> potentialTopLeftCorners; // Potential top left corners
    private ImageHandler pHandler;                // Pattern Image Handler
    private ImageHandler sHandler;                // Source Image Handler
    
    /**
     * CONSTRUCTOR
     * 
     * Finds the potential top left corners on creation.
     * 
     * @param patternImageHandler -- Pattern Image Handler
     * @param sourceImageHandler  -- Source Image Handler
     */
    public CornerManager(ImageHandler patternImageHandler, ImageHandler sourceImageHandler) {
    	this.pHandler = patternImageHandler;
    	this.sHandler = sourceImageHandler;
    	this.potentialTopLeftCorners = new ArrayList<Corner>();
    	findPotentialCorners();
    }
    
    /**
     * Get a range of top left corners from the start index to the start+howMany index
     * 
     * NOTE: Corners must already be sorted at this point
     * 
     * @param start   -- Index to grab from
     * @param howMany -- Number of corners to grab
     * 
     * @return List of corners from start to start+howMany
     */
    public List<Corner> getRangeOfTopLeftCorners(int start, int howMany) {
    	if (start > potentialTopLeftCorners.size()) {
    		return new ArrayList<Corner>();
    	} else if (start+howMany > potentialTopLeftCorners.size()) {
            return potentialTopLeftCorners.subList(start, potentialTopLeftCorners.size());
        } else {
            return potentialTopLeftCorners.subList(start, start+howMany);
        }
    }
    
    /**
     * Create a list consisting of all the possible corner matches within the source image
     */
    private void findPotentialCorners() {
    	BufferedImage sImage = sHandler.getImage();
    	// Get the pixels from the pattern image we will use to identify potential corners
        HashMap<Point, Color> topLeftImageColors = getPixelColors();
        
        // Set up our best color difference so far to an initial (high) value
    	ColorDifference bestDifference = new ColorDifference();
    	bestDifference.addColorDifference(new Color(0,0,0), new Color(128,128,18));
        
    	// Compare source image pixels to pattern image corners to identify potential locations
        for (int x = 0; x <= sImage.getWidth() - pHandler.getWidth(); x++) {
            for (int y = 0; y <= sImage.getHeight() - pHandler.getHeight(); y++) {

            	// Get the color difference for this potential corner
        		ColorDifference colorDiff = getColorDifferenceForPotentialCorner(topLeftImageColors, x, y, bestDifference);
            	
            	// Add this point to our list of corners if it is a potential corner
            	addIfPotentialCorner(x, y, colorDiff, bestDifference);
            	
            	// Update our best color difference so far
            	if (colorDiff.getAverageDifference() < bestDifference.getAverageDifference()) {
            		bestDifference = colorDiff;
            	}      	
            }
        }
        
        // Finally sort the corners. We do this here to prevent sorting multiple
        // times in the case where getBestCorners is called more than once.
        Collections.sort(potentialTopLeftCorners);
    }

    /**
     * Compares pixels and if the corner is a potential match, then add it to the list of potential corners
     * @param x          -- Potential corner x location
     * @param y          -- Potential corner y location
     * @param cornerDiff -- Corner color difference
     * @param bestDiff   -- Current best corner color difference
     */
	private void addIfPotentialCorner(int x, int y, ColorDifference cornerDiff, ColorDifference bestDiff) {

		// Is a potential corner if it is better than or equal to the current best difference
		boolean isPotentialCorner = (cornerDiff.getAverageDifference() <= bestDiff.getAverageDifference());

		// Reduce the max average color difference on small image comparisons
		if(pHandler.isSmallImage()) {
			isPotentialCorner &= (cornerDiff.getAverageDifference() < Constants.MAX_SMALL_IMAGE_AVERAGE_COLOR_DIFFERENCE); 
		} else {
			isPotentialCorner &= (cornerDiff.getAverageDifference() < Constants.MAX_AVERAGE_COLOR_DIFFERENCE); 
		}
		
		// If we have a potential corner, add it to the list
        if (isPotentialCorner) {
        	potentialTopLeftCorners.add(new Corner(new Point(x, y), cornerDiff));
        }
	}
	
	/**
	 * Obtains the color difference of all our pattern image colors compared to 
	 * their respective source image colors
	 * 
	 * @param cornerImageColors -- Corner pixel to color mapping
	 * @param xOffset           -- Pixel X offset from pattern to source
	 * @param yOffset           -- Pixel Y offset from pattern to source
	 * @param bestDifference    -- The current best color difference
	 *  
	 * @return The color difference between the given top left corner pixel->color mapping
	 *         and the current source location being checked
	 */
	private ColorDifference getColorDifferenceForPotentialCorner(HashMap<Point, Color> cornerImageColors, 
			int xOffset, int yOffset, ColorDifference bestDifference) {
		
		BufferedImage sourceImage = sHandler.getImage();
		ColorDifference difference = new ColorDifference();
		
		// Loop through the pixels and compute the ColorDifference
		for (Entry<Point, Color> entry : cornerImageColors.entrySet()) {
		    Point p = entry.getKey();
		    Color patternPixelColor = entry.getValue();
		    
		    Color sourcePixelColor = new Color(sourceImage.getRGB(p.x + xOffset, p.y + yOffset));
		    
		    difference.addColorDifference(patternPixelColor, sourcePixelColor);
		    
		    // If we ever breach the threshold, stop looking!
		    if (difference.getAverageDifference() > bestDifference.getAverageDifference()) {
		    	break;
		    }
		}
		
		return difference;
	}
    
	/**
	 * Used to obtain the pixels along the diagonal of the pattern image.
	 * 
	 * Instead of just comparing the top left pixel of sub images, we grab less potential corners
	 * in the source image by checking more than 1 pixel back on the left side of the second row
	 * 
	 * @return Mapping of pixel locations to colors
	 */
    private HashMap<Point, Color> getPixelColors() {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        BufferedImage image = pHandler.getImage();
        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;

        int depth = Constants.PIXEL_COMPARISON_DEPTH - 1; // Subtract 1 so we search by index
        // If depth is too big for this image, get all pixels from image
        if(pHandler.isSmallImage()) {
        	for(int x=0;x<image.getWidth();x++){
        		for(int y=0;y<image.getHeight();y++) {
        			result.put(new Point(x,y), new Color(image.getRGB(x,y)));
        		}
        	}
        } else {
        	// Go as many pixels deep as we specify on all 4 corners
            while(depth >= 0) {
            	// Top left
            	result.put(new Point(depth, depth), new Color(image.getRGB(depth, depth)));
            	
            	// Top right
            	result.put(new Point(width-depth, depth), new Color(image.getRGB(width-depth, depth)));
            	
            	// Bottom left
            	result.put(new Point(depth, height-depth), new Color(image.getRGB(depth, height-depth)));
            	
            	// Bottom right
            	result.put(new Point(width-depth, height-depth), new Color(image.getRGB(width-depth, height-depth)));
            	
            	depth--;
            }
        }
        
        return result;
    }

	/** ---------- GETTERS ---------- */
    
	public ImageHandler getPatternImageHandler() {
		return pHandler;
	}

	public ImageHandler getSourceImageHandler() {
		return sHandler;
	}
}
