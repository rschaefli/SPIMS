import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CornerManager {
	
	private static int PIXEL_COMPARISON_DEPTH = 3;		// Note that we go down to 0, so 3 deep
														// actually means 4 on each corner
	private static int MAX_AVERAGE_COLOR_DIFFERENCE = 40;
    
	private List<Corner> potentialTopLeftCorners;
    private ImageHandler patternImageHandler;
    private ImageHandler sourceImageHandler;
    
    public CornerManager(ImageHandler patternImageHandler, ImageHandler sourceImageHandler) {
    	this.patternImageHandler = patternImageHandler;
    	this.sourceImageHandler = sourceImageHandler;
    	potentialTopLeftCorners = new ArrayList<Corner>();
    	setPossibleCorners();
    }
    
    // Get a range of top left corners from index start to start+howMany
    // At this point, corners must already be sorted
    public List<Corner> getRangeOfTopLeftCorners(int start, int howMany) {
    	if (start > potentialTopLeftCorners.size()) {
    		return new ArrayList<Corner>();
    	}
    	else if (start+howMany > potentialTopLeftCorners.size()) {
            return potentialTopLeftCorners.subList(start, potentialTopLeftCorners.size());
        }
        else {
            return potentialTopLeftCorners.subList(start, start+howMany);
        }
    }
    
    // Sets all the possible corners that could be a match in the source image
    private void setPossibleCorners() {
    	BufferedImage sourceImage = sourceImageHandler.getImage();
    	// Get the pixels from the pattern image we will use to identify potential corners
        HashMap<Point, Color> topLeftImageColors = getPixelColors();
        
        // Look through all the pixels in the source image to identify potential corners with
        // the corners of our pattern image
        for (int i = 0; i <= sourceImage.getWidth() - patternImageHandler.getWidth(); i++) {
            for (int j = 0; j <= sourceImage.getHeight() - patternImageHandler.getHeight(); j++) {

            	addIfPotentialCorner(topLeftImageColors, i, j, potentialTopLeftCorners);
            	
            }
        }
        
        // Finally sort the corners. We do this here to prevent sorting multiple
        // times in the case where getBestCorners is called more than once.
        Collections.sort(potentialTopLeftCorners);
    }

    // Compare pixels and if this corner is a potential match then add it to corners
	private void addIfPotentialCorner(HashMap<Point, Color> cornerImageColors, int i, int j, List<Corner> corners) {
		// Get the color difference for this potential corner
		ColorDifference colorDifferenceForCorner = getColorDifferenceForPotentialCorner(cornerImageColors, i, j);

		boolean isPotentialCorner = colorDifferenceForCorner.getAverageDifference() < MAX_AVERAGE_COLOR_DIFFERENCE; 
		
		// If we have a potential corner, add to result
        if (isPotentialCorner) {
        	corners.add(new Corner(new Point(i, j), colorDifferenceForCorner));
        }
	}
	
	// Gets the color difference of all our pattern image colors compared to their
	// respective source image colors
	private ColorDifference getColorDifferenceForPotentialCorner(HashMap<Point, Color> cornerImageColors, int i, int j) {
		BufferedImage sourceImage = sourceImageHandler.getImage();
		ColorDifference difference = new ColorDifference();
		
		//Loop through the pixels and see if we have any matches
		for (Entry<Point, Color> entry : cornerImageColors.entrySet()) {
		    Point p = entry.getKey();
		    Color patternPixelColor = entry.getValue();

		    Color sourcePixelColor = new Color(sourceImage.getRGB(p.x + i, p.y + j));
		    
		    difference.addColorDifference(patternPixelColor, sourcePixelColor);
		    
		    // If we ever breach the threshold, stop looking!
		    if (difference.getAverageDifference() > MAX_AVERAGE_COLOR_DIFFERENCE) {
		    	break;
		    }
		}
		
		return difference;
	}
    
    // This is used to elaborate on just comparing the top left pixel of sub images
    // The goal is to get less potential corners in the source image by checking more than 1 pixel
    // back on the left side of the second row.
    // Get pixels along the diagonal of the pattern image
    private HashMap<Point, Color> getPixelColors() {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        BufferedImage image = patternImageHandler.getImage();
        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;

        int depth = PIXEL_COMPARISON_DEPTH;
        // If depth is too big for this image, get all pixels from image
        if(image.getWidth() < PIXEL_COMPARISON_DEPTH * 2 ||
           image.getHeight() < PIXEL_COMPARISON_DEPTH * 2) {
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

	public ImageHandler getPatternImageHandler() {
		return patternImageHandler;
	}

	public ImageHandler getSourceImageHandler() {
		return sourceImageHandler;
	}
}
