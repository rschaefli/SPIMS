import java.awt.Color;

public class ColorDifference {
    private int difference;
    private int totalPixels;
    
    public ColorDifference() {
        this.totalPixels = 1;
    }
    
    public int getAverageDifference() {
        return this.difference / totalPixels;
    }
    
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
        
        this.difference += Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2));
        this.totalPixels += 1;
    }
}