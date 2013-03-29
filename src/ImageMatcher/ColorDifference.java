/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageMatcher;

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
        return (getRedDifference() + getGreenDifference() + getBlueDifference()) / 3;
    }
    
    public void addColorDifference(ColorDifference difference) {
        this.redDifference += difference.getRedDifference();
        this.greenDifference += difference.getGreenDifference();
        this.blueDifference += difference.getBlueDifference();
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
