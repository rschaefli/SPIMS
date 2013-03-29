/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageMatcher;

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
	
	private final static int PIXEL_COMPARISON_DEPTH = 1;
	
	private int PIXEL_COLOR_ERROR_MARGIN = 5;
	private int MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE = 75;
    
	private List<Corner> potentialTopLeftMatches;
	private List<Corner> potentialTopRightMatches;
	private List<Corner> potentialBottomLeftMatches;
	private List<Corner> potentialBottomRightMatches;
    private ImageHandler patternImageHandler;
    private ImageHandler sourceImageHandler;
    
    public CornerManager(ImageHandler patternImageHandler, ImageHandler sourceImageHandler) {
    	this.patternImageHandler = patternImageHandler;
    	this.sourceImageHandler = sourceImageHandler;
    	potentialTopLeftMatches = new ArrayList<Corner>();
    	potentialTopRightMatches = new ArrayList<Corner>();
    	potentialBottomLeftMatches = new ArrayList<Corner>();
    	potentialBottomRightMatches = new ArrayList<Corner>();
    	setPossibleCorners();
        
        // Become more lenient when dealing with GIF files
        if (this.patternImageHandler.getType().equals("gif") || this.sourceImageHandler.getType().equals("gif")) {
        	PIXEL_COLOR_ERROR_MARGIN += 30;
        }
    }
    
    // Get the best howMany potential matches
    public List<Corner> getBestTopLeftMatches(int howMany) {
        return getBestMatches(howMany, potentialTopLeftMatches);
    }
    public List<Corner> getBestTopRightMatches(int howMany) {
        return getBestMatches(howMany, potentialTopRightMatches);
    }
    public List<Corner> getBestBottomLeftMatches(int howMany) {
        return getBestMatches(howMany, potentialBottomLeftMatches);
    }
    public List<Corner> getBestBottomRightMatches(int howMany) {
        return getBestMatches(howMany, potentialBottomRightMatches);
    }
    
    // Get the best howMany potential matches from a list of potential matches
    public List<Corner> getBestMatches(int howMany, List<Corner> corners) {
        Collections.sort(corners);
        
        if (howMany > corners.size()) {
            return corners;
        }
        else {
            return corners.subList(0, howMany);
        }
    }
    
    // Works in conjunction with getAxisColors to determine if there are potential
    // pattern image matches within a source image
    private void setPossibleCorners() {
    	BufferedImage patternImage = patternImageHandler.getImage();
    	BufferedImage sourceImage = sourceImageHandler.getImage();
    	// Get the pixels from the patter image we will use to identify potential matches
    	// with the source image below
        HashMap<Point, Color> topLeftImageColors = getPixelColors(patternImage, true, true);
        HashMap<Point, Color> topRightImageColors = getPixelColors(patternImage, false, true);
        HashMap<Point, Color> bottomLeftImageColors = getPixelColors(patternImage, true, false);
        HashMap<Point, Color> bottomRightImageColors = getPixelColors(patternImage, false, false);
        
        // Look through all the pixels in the source image to identify potential matches with
        // the four corners of our patter image
        for (int i = 0; i < sourceImage.getWidth(); i++) {
            for (int j = 0; j < sourceImage.getHeight(); j++) {
            	if (i < sourceImage.getWidth() - PIXEL_COMPARISON_DEPTH &&
            		j < sourceImage.getHeight() - PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialMatch(sourceImage, topLeftImageColors, i, j, potentialTopLeftMatches);
            	}
            	if (i > PIXEL_COMPARISON_DEPTH &&
            		j < sourceImage.getHeight() - PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialMatch(sourceImage, topRightImageColors, i, j, potentialTopRightMatches);
            	}
            	if (i < sourceImage.getWidth() - PIXEL_COMPARISON_DEPTH &&
            		j > PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialMatch(sourceImage, bottomLeftImageColors, i, j, potentialBottomLeftMatches);
            	}
            	if (i > PIXEL_COMPARISON_DEPTH &&
            		j > PIXEL_COMPARISON_DEPTH) {
            		addIfPotentialMatch(sourceImage, bottomRightImageColors, i, j, potentialBottomRightMatches);
            	}
            }
        }
    }

	private void addIfPotentialMatch(BufferedImage sourceImage, HashMap<Point, Color> cornerImageColors, int i, int j, List<Corner> corners) {
		ColorDifference difference = new ColorDifference(0, 0, 0);
		boolean isPotentialMatch = true;
		
		//Loop through the pixels and see if we have any matches
		for (Entry<Point, Color> entry : cornerImageColors.entrySet()) {
		    Point p = entry.getKey();
		    Color patternPixelColor = entry.getValue();

		    Color sourcePixelColor = new Color(sourceImage.getRGB(p.x + i, p.y + j));

		    difference.addColorDifference(differenceBetweenColors(patternPixelColor, sourcePixelColor));
		}
		
		isPotentialMatch = difference.getAverageDifference() < MAX_ALLOWABLE_AVERAGE_COLOR_DIFFERENCE;  
		
		 // If we have a potential match, add to result
        if (isPotentialMatch) {
        	corners.add(new Corner(new Point(i, j), difference));
        }
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
    
    // This is used to elaborate on just comparing the top left pixel of sub images
    // The goal is to get less potential matches in the source image by checking more than 1 pixel
    // back on the left side of the second row.
    // NOTE:
    // For scaling, we no longer want to include the pattern widths and heights in the points.
    // Instead we just use relative points to what our corner would be, but get the correct
    // pixel color value from the pattern image
    private HashMap<Point, Color> getPixelColors(BufferedImage image, boolean searchRight, boolean searchDown) {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        
        int dirX = searchRight ? 1 : -1;
        int dirY = searchDown ? 1 : -1;
        int startX = searchRight ? 0 : image.getWidth() - 1;
        int startY = searchDown ? 0 : image.getHeight() - 1;
        
        // Add our corner pixel
        result.put(new Point(0, 0), new Color(image.getRGB(startX, startY)));
        
        int depth = PIXEL_COMPARISON_DEPTH;
        
        // THIS FUNCTION ONLY WORKS WITH A DEPTH OF 1 FOR NOW
        // ADDING TO THE DEPTH WILL RESULT IN SOME PIXELS BEING LEFT OUT THAT WE WANT TO INCLUDE
        while(depth > 0) {
        	result.put(new Point(dirX, 0), new Color(image.getRGB(startX + dirX, startY)));
        	result.put(new Point(0, dirY), new Color(image.getRGB(startX, startY + dirY)));
        	result.put(new Point(dirX , dirY), new Color(image.getRGB(startX + dirX, startY + dirY)));
        	
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
