import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Dan
 */
public class CornerManager {
	
	private final static int PIXEL_COMPARISON_DEPTH = 10;
	private final static int MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE = 50;
    
	private List<Corner> potentialTopLeftCorners;
	private List<Corner> potentialTopRightCorners;
	private List<Corner> potentialBottomLeftCorners;
	private List<Corner> potentialBottomRightCorners;
    private ImageHandler patternImageHandler;
    private ImageHandler sourceImageHandler;
    
    public CornerManager(ImageHandler patternImageHandler, ImageHandler sourceImageHandler) {
    	this.patternImageHandler = patternImageHandler;
    	this.sourceImageHandler = sourceImageHandler;
    	potentialTopLeftCorners = new ArrayList<Corner>();
    	potentialTopRightCorners = new ArrayList<Corner>();
    	potentialBottomLeftCorners = new ArrayList<Corner>();
    	potentialBottomRightCorners = new ArrayList<Corner>();
    	setPossibleCorners();
    }
    
    // Get the best howMany potential corners
    public List<Corner> getBestTopLeftCorners(int howMany) {
        return getBestCorners(howMany, potentialTopLeftCorners);
    }
    public List<Corner> getBestTopRightCorners(int howMany) {
        return getBestCorners(howMany, potentialTopRightCorners);
    }
    public List<Corner> getBestBottomLeftCorners(int howMany) {
        return getBestCorners(howMany, potentialBottomLeftCorners);
    }
    public List<Corner> getBestBottomRightCorners(int howMany) {
        return getBestCorners(howMany, potentialBottomRightCorners);
    }
    
    // Get the best howMany potential corners from a list of potential corners
    public List<Corner> getBestCorners(int howMany, List<Corner> corners) {
        Collections.sort(corners);
        
        if (howMany > corners.size()) {
            return corners;
        }
        else {
            return corners.subList(0, howMany);
        }
    }
    
    // Sets all the possible corners that could be a match in the source image
    private void setPossibleCorners() {
    	BufferedImage sourceImage = sourceImageHandler.getImage();
    	// Get the pixels from the pattern image we will use to identify potential corners
        HashMap<Point, Color> topLeftImageColors = getPixelColors(true, true);
        HashMap<Point, Color> topRightImageColors = getPixelColors(false, true);
        HashMap<Point, Color> bottomLeftImageColors = getPixelColors(true, false);
        HashMap<Point, Color> bottomRightImageColors = getPixelColors(false, false);
        
        // Look through all the pixels in the source image to identify potential corners with
        // the corners of our pattern image
        for (int i = 0; i < sourceImage.getWidth(); i++) {
            for (int j = 0; j < sourceImage.getHeight(); j++) {
            	if (i < sourceImage.getWidth() - PIXEL_COMPARISON_DEPTH &&
            		j < sourceImage.getHeight() - PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialCorner(sourceImage, topLeftImageColors, i, j, potentialTopLeftCorners);
            	}
            	if (i > PIXEL_COMPARISON_DEPTH &&
            		j < sourceImage.getHeight() - PIXEL_COMPARISON_DEPTH) {
            		//addIfPotentialCorner(sourceImage, topRightImageColors, i, j, potentialTopRightCorners);
            	}
            	if (i < sourceImage.getWidth() - PIXEL_COMPARISON_DEPTH &&
            		j > PIXEL_COMPARISON_DEPTH) {
            		//addIfPotentialCorner(sourceImage, bottomLeftImageColors, i, j, potentialBottomLeftCorners);
            	}
            	if (i > PIXEL_COMPARISON_DEPTH &&
            		j > PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialCorner(sourceImage, bottomRightImageColors, i, j, potentialBottomRightCorners);
            	}
            }
        }
    }

    // Compare pixels and if this corner is a potential match then add it to corners
	private void addIfPotentialCorner(BufferedImage sourceImage, HashMap<Point, Color> cornerImageColors, int i, int j, List<Corner> corners) {
		ColorDifference difference = new ColorDifference(0, 0, 0);
		boolean isPotentialCorner = true;
		
		//Loop through the pixels and see if we have any matches
		for (Entry<Point, Color> entry : cornerImageColors.entrySet()) {
		    Point p = entry.getKey();
		    Color patternPixelColor = entry.getValue();

		    Color sourcePixelColor = new Color(sourceImage.getRGB(p.x + i, p.y + j));
		    
		    difference.addColorDifference(patternPixelColor, sourcePixelColor);
		}
		
		isPotentialCorner = difference.getAverageDifference() < MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE;  
		
		 // If we have a potential corner, add to result
        if (isPotentialCorner) {
        	corners.add(new Corner(new Point(i, j), difference));
        }
	}
    
    // This is used to elaborate on just comparing the top left pixel of sub images
    // The goal is to get less potential corners in the source image by checking more than 1 pixel
    // back on the left side of the second row.
    // Get pixels along the diagonal of the pattern image
    private HashMap<Point, Color> getPixelColors(boolean searchRight, boolean searchDown) {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        BufferedImage image = patternImageHandler.getImage();
        
        int dirX = searchRight ? 1 : -1;
        int dirY = searchDown ? 1 : -1;
        int startX = searchRight ? 0 : image.getWidth() - 1;
        int startY = searchDown ? 0 : image.getHeight() - 1;
        
        // Add our starting corner pixel
        // When depth is 0 we are still getting 1 pixel to compare
        result.put(new Point(0, 0), new Color(image.getRGB(startX, startY)));

        int depth = PIXEL_COMPARISON_DEPTH;
        // If depth is too big for this image, get the highest possible depth we can use
        if(image.getWidth() < PIXEL_COMPARISON_DEPTH ||
           image.getHeight() < PIXEL_COMPARISON_DEPTH) {
        	depth = image.getWidth() < image.getHeight() ? image.getWidth() : image.getHeight();
        }
        
        // Go as many pixels deep as we specify.
        while(depth > 0) {
        	result.put(new Point(dirX * depth, dirY * depth),
        			   new Color(image.getRGB(startX + (dirX * depth), startY + (dirY * depth))));
        	
        	depth--;
        }
        
        return result;
    }

	public ImageHandler getPatternImageHandler() {
		return patternImageHandler;
	}

	public ImageHandler getSourceImageHandler() {
		return sourceImageHandler;
	}
}
