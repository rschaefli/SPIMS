import java.awt.Color;

/**
 *
 * @author Dan
 */
public class ColorDifference {
    private int redDifference;
    private int greenDifference;
    private int blueDifference;
    
    public ColorDifference(int redDifference, int greenDifference, int blueDifference) {
        this.redDifference = redDifference;
        this.greenDifference = greenDifference;
        this.blueDifference = blueDifference;
    }
    
    public int getAverageDifference() {
        return (this.redDifference + this.greenDifference + this.blueDifference) / 3;
    }
    
    public void addColorDifference(Color c1, Color c2) {
    	int r1 = c1.getRed();
        int r2 = c2.getRed();
        this.redDifference += Math.abs(r2 - r1);
        
        int g1 = c1.getGreen();
        int g2 = c2.getGreen();
        this.greenDifference += Math.abs(g2 - g1);
        
        int b1 = c1.getBlue();
        int b2 = c2.getBlue();
        this.blueDifference += Math.abs(b2 - b1);
    }

    /**
     * @return the redDifference
     */
    public int getRedDifference() {
        return redDifference;
    }

    /**
     * @return the greenDifference
     */
    public int getGreenDifference() {
        return greenDifference;
    }

    /**
     * @return the blueDifference
     */
    public int getBlueDifference() {
        return blueDifference;
    }
    
    
}
