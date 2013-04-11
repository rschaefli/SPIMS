import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class MatchManager {
	
	public List<Match> matches;
	
	public MatchManager() {
		this.matches = new ArrayList<Match>();
	}
	
	public void add(Match match) {
		matches.add(match);
	}
	
	// Returns either all the exact matches,
	// or the lowest difference that is lower than the highest acceptable difference
	private List<Match> getMatchesToPrint() {
		ArrayList<Match> matchesToPrint = new ArrayList<Match>();
		Match bestNonExactMatch = null;
		
		// Try and get all the exact matches. We may get none and continue
		// below to add the best non-exact match
		for(Match m : matches) {
			// If exact match print out
	        if(m.difference == 0){
	        	// This is the only case where we will have multiple matches
	        	// Therefore we only need to check for overlap in matches here.
	        	if(!isOverlappingMatch(m, matchesToPrint)) {
	        		matchesToPrint.add(m);
	        	}
	        // Else we check to set our best acceptable non-exact match
	        } else {
	        	if(bestNonExactMatch == null) {
	        		bestNonExactMatch = m;
	        	} else if(m.difference < bestNonExactMatch.difference) {
	        		bestNonExactMatch = m;
	        	}
	        }
		}
		
		// Check if we have any matches to print.
		// If not, add our best non-exact match
		if(matchesToPrint.isEmpty() && bestNonExactMatch != null) {
			matchesToPrint.add(bestNonExactMatch);
		}
		
		return matchesToPrint;
	}
	
	// Check this match with the other matches we will print out to
	// identify whether it is a unique match with < 50% overlap.
	public boolean isOverlappingMatch(Match m, List<Match> matchesToPrint) {
		
		for(Match confirmedMatch : matchesToPrint) {
			// -1 because contains will not return true if a pixel is on the edge of the rectangle
			Rectangle r = new Rectangle(confirmedMatch.location.x-1,
										confirmedMatch.location.y-1, 
										confirmedMatch.patternHandler.getWidth()/2, 
										confirmedMatch.patternHandler.getHeight()/2);
			
			return r.contains(m.location);
		}
		
		return false;
	}
	
	// Print out our final list of matches
	public void printMatches() {
		List<Match> matchesToPrint = getMatchesToPrint();
		
		for(Match m : matchesToPrint) {
			printMatch(m.patternHandler, m.sourceHandler, m.location);
		}
	}
	
	// Format and print a match to standard output
    private void printMatch(ImageHandler patternHandler, ImageHandler sourceHandler, Point location){
        System.out.println(patternHandler.getName() + " matches " +
            				   sourceHandler.getName() + " at " +
            				   patternHandler.getWidth() + "x" + patternHandler.getHeight() +
            				   "+" + location.x +
            				   "+" + location.y);
    }
}
