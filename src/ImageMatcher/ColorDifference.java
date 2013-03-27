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
        return (redDifference + greenDifference + blueDifference) / 3;
    }
    
    public void addColorDifference(ColorDifference difference) {
        this.redDifference += difference.redDifference;
        this.greenDifference += difference.greenDifference;
        this.blueDifference += difference.blueDifference;
    }
}
