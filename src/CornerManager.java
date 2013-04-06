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
	
	private static int PIXEL_COMPARISON_DEPTH = 15;
	private static int MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE = 40;
    
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
        
        // Look through all the pixels in the source image to identify potential corners with
        // the corners of our pattern image
        for (int i = 0; i <= sourceImage.getWidth() - patternImageHandler.getWidth(); i++) {
            for (int j = 0; j <= sourceImage.getHeight() - patternImageHandler.getHeight(); j++) {

            	addIfPotentialCorner(sourceImage, topLeftImageColors, i, j, potentialTopLeftCorners);
            	
            }
        }
    }

    // Compare pixels and if this corner is a potential match then add it to corners
	private void addIfPotentialCorner(BufferedImage sourceImage, HashMap<Point, Color> cornerImageColors, int i, int j, List<Corner> corners) {
		ColorDifference difference = new ColorDifference();
		boolean isPotentialCorner = true;
		
		//Loop through the pixels and see if we have any matches
		for (Entry<Point, Color> entry : cornerImageColors.entrySet()) {
		    Point p = entry.getKey();
		    Color patternPixelColor = entry.getValue();

		    Color sourcePixelColor = new Color(sourceImage.getRGB(p.x + i, p.y + j));
		    
		    difference.addColorDifference(patternPixelColor, sourcePixelColor);
		    
		    // If we ever breach the threshold, stop looking!
		    if (difference.getAverageDifference() > MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE) {
		    	break;
		    }
		}
		
		isPotentialCorner = difference.getAverageDifference() < MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE; 
		
//		if (i == 730 && j == 190) {
//			System.out.println("Potential match found at " + i + "," + j + " - " + difference.getAverageDifference());
//		}
		
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
        	for(int x=0;x<image.getWidth();x++){
        		for(int y=0;y<image.getHeight();y++) {
        			result.put(new Point(x,y), new Color(image.getRGB(x,y)));
        		}
        	}
        	
        } else {
        	// Go as many pixels deep as we specify.
            while(depth > 0) {
            	result.put(new Point(dirX * depth, dirY * depth),
            			   new Color(image.getRGB(startX + (dirX * depth), startY + (dirY * depth))));
            	
            	depth--;
            }
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
