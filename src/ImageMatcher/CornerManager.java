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
    	setPossibleCorners(1);
        
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
    private void setPossibleCorners(int depth) {
    	BufferedImage patternImage = patternImageHandler.getImage();
    	BufferedImage sourceImage = sourceImageHandler.getImage();
    	// Get the pixels from the patter image we will use to identify potential matches
    	// with the source image below
        HashMap<Point, Color> topLeftImageColors = getPixelColors(patternImage, true, true, depth);
        HashMap<Point, Color> topRightImageColors = getPixelColors(patternImage, false, true, depth);
        HashMap<Point, Color> bottomLeftImageColors = getPixelColors(patternImage, true, false, depth);
        HashMap<Point, Color> bottomRightImageColors = getPixelColors(patternImage, false, false, depth);
        
        // Look through all the pixels in the source image to identify potential matches with
        // the four corners of our patter image
        for (int i = 0; i <= sourceImage.getWidth() - patternImage.getWidth(); i++) {
            for (int j = 0; j <= sourceImage.getHeight() - patternImage.getHeight(); j++) {
                addIfPotentialMatch(sourceImage, topLeftImageColors, i, j, potentialTopLeftMatches);
                addIfPotentialMatch(sourceImage, topRightImageColors, i, j, potentialTopRightMatches);
                addIfPotentialMatch(sourceImage, bottomLeftImageColors, i, j, potentialBottomLeftMatches);
                addIfPotentialMatch(sourceImage, bottomRightImageColors, i, j, potentialBottomRightMatches);
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
    private HashMap<Point, Color> getPixelColors(BufferedImage image, boolean searchRight, boolean searchDown, int depth) {
        HashMap<Point, Color> result = new HashMap<Point, Color>();
        
        int dirX = searchRight ? 1 : -1;
        int dirY = searchDown ? 1 : -1;
        int startX = searchRight ? 0 : image.getWidth() - 1;
        int startY = searchDown ? 0 : image.getHeight() - 1;
        
        // Add our corner pixel
        result.put(new Point(startX, startY), new Color(image.getRGB(startX, startY)));
        
        
        // THIS FUNCTION ONLY WORKS WITH A DEPTH OF 1 FOR NOW
        // ADDING TO THE DEPTH WILL RESULT IN SOME PIXELS BEING LEFT OUT THAT WE WANT TO INCLUDE
        while(depth > 0) {
        	startX += dirX;
        	result.put(new Point(startX , startY), new Color(image.getRGB(startX, startY)));
        	startX -= dirX;
        	startY += dirY;
        	result.put(new Point(startX , startY), new Color(image.getRGB(startX, startY)));
        	startX += dirX;
        	result.put(new Point(startX , startY), new Color(image.getRGB(startX, startY)));
        	
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
