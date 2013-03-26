/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Dan
 */
public class PotentialMatchManager {
    
    private List<PotentialMatch> potentialMatches;
    
    public PotentialMatchManager() {
        potentialMatches = new ArrayList<PotentialMatch>();
    }
    
    public void addPotentialMatch(PotentialMatch potentialMatch) {
        potentialMatches.add(potentialMatch);
        
    }
    
    public List<PotentialMatch> getBestMatches(int howMany) {
        Collections.sort(potentialMatches);
        
        if (howMany > potentialMatches.size()) {
            return potentialMatches;
        }
        else {
            return potentialMatches.subList(0, howMany);
        }
    }
}
