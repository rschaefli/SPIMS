/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageMatcher;

import java.awt.Point;

/**
 *
 * @author Dan
 */
public class PotentialMatch implements Comparable<PotentialMatch> {
    
    public Point point;
    public ColorDifference colorDifference;
    
    public PotentialMatch(Point point, ColorDifference colorDifference) {
        this.point = point;
        this.colorDifference = colorDifference;
    }
    
    public int compareTo(PotentialMatch potentialMatch) {
        int averageDifference = colorDifference.getAverageDifference();
        int potentialMatchAverageDifference =  potentialMatch.colorDifference.getAverageDifference();
        
        return  averageDifference < potentialMatchAverageDifference ? -1 : averageDifference > potentialMatchAverageDifference ? 1 : 0;
    }
    
}
