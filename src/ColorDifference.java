import java.awt.Color;

/**
 * Represents the color difference between two Colors
 */
public class ColorDifference {
	
    private float difference; // sum of differences between red/green/blue values
    private int totalPixels;  // Total pixel colors compared
    
    /**
     * CONSTRUCTOR
     */
    public ColorDifference() {
    	this.difference = 0;
        this.totalPixels = 0;
    }
    
    /**
     * Obtain the current average difference
     * 
     * @return Total average color difference between all added colors,
     *         returns -1 if no colors have been added
     */
    public float getAverageDifference() {
    	if(totalPixels == 0) {
    		return -1;
    	} else {
    		return this.difference / totalPixels;
    	}
    }
    
    /**
     * Add the color differences between two more colors to the existing differences
     * 
     * @param c1 -- Color 1
     * @param c2 -- Color 2
     */
    public void addColorDifference(Color c1, Color c2) {
    	int r1 = c1.getRed();
        int r2 = c2.getRed();
        int r = Math.abs(r2 - r1); 
        
        int g1 = c1.getGreen();
        int g2 = c2.getGreen();
        int g = Math.abs(g2 - g1);
        
        int b1 = c1.getBlue();
        int b2 = c2.getBlue();
        int b = Math.abs(b2 - b1);
        
        this.difference += (r+g+b);
        this.totalPixels += 1;
    }
}