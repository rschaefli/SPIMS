package ImageMatcher;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MatchHandler {
	
	public List<Match> matches;
	
	public MatchHandler() {
		this.matches = new ArrayList<Match>();
	}
	
	public void add(Match match) {
		matches.add(match);
	}
	
	// Returns either all the exact matches,
	// or the lowest difference that is lower than the highest acceptable difference
	private List<Match> getMatchesToPrint() {
		ArrayList<Match> matchesToPrint = new ArrayList<Match>();
		Match bestNonExactMatch = new Match(null, null, new Point(0,0), 64);
		
		// Try and get all the exact matches. We may get none and continue
        // below to add the best non-exact match
		for(Match m : matches) {
			//If exact match print out
            if(m.difference == 0){
                matchesToPrint.add(m);
            } else if(m.difference != -1 &&
            		  m.difference < Match.HIGHEST_ACCEPTABLE_DIFFERENCE &&
            		  m.difference < bestNonExactMatch.difference) {
            	bestNonExactMatch = m;
            }
		}
		
		// Check if we have any matches to print.
		// If not, add our best non-exact match
		if(matchesToPrint.isEmpty()) {
			matchesToPrint.add(bestNonExactMatch);
		}
		
		return matchesToPrint;
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
