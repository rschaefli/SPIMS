import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the printing and filtering of matches
 */
public class MatchManager {

	public List<Match> matches; // The actual matches

	/**
	 * CONSTRUCTOR
	 */
	public MatchManager() {
		this.matches = new ArrayList<Match>();
	}

	/**
	 * Adds a match to the list of matches
	 * 
	 * @param match -- Match to add
	 */
	public void add(Match match) {
		matches.add(match);
	}

	/**
	 * Returns either all the exact matches or a list of matches within the 
	 * acceptable difference threshold
	 * 
	 * @return All valid matches
	 */
	private List<Match> getMatchesToPrint() {
		ArrayList<Match> matchesToPrint = new ArrayList<Match>();
		Match bestNonExactMatch = null;

		// Attempt to obtain all the exact matches
		for(Match m : matches) {
			// Print exact matches
			if(m.difference == 0){
				// Check for overlapping matches
				if(!isOverlappingMatch(m, matchesToPrint)) {
					matchesToPrint.add(m);
				}
			} else if(bestNonExactMatch == null || m.difference < bestNonExactMatch.difference) {
				bestNonExactMatch = m;
			}
		}

		// Check if we have any matches to print, otherwise, add our best non-exact match
		if(matchesToPrint.isEmpty() && bestNonExactMatch != null) {
			matchesToPrint.add(bestNonExactMatch);
		}

		return matchesToPrint;
	}

	/**
	 * Checks if the given match is a unique match with less than 50% match
	 * to match overlap
	 * 
	 * @param match          -- Match to test for overlap
	 * @param matchesToPrint -- List of all matches
	 * 
	 * @return Does the match overlap any of the other matches within the list?
	 */
	private boolean isOverlappingMatch(Match match, List<Match> matchesToPrint) {

		for(Match confirmedMatch : matchesToPrint) {
			// -1 because contains will not return true if a pixel is on the edge of the rectangle
			Rectangle r = new Rectangle(confirmedMatch.location.x-1,
					confirmedMatch.location.y-1, 
					confirmedMatch.patternHandler.getWidth()/2, 
					confirmedMatch.patternHandler.getHeight()/2);

			return r.contains(match.location);
		}

		return false;
	}

	/**
	 * Print out the final list of matches
	 */
	public void printMatches() {
		List<Match> matchesToPrint = getMatchesToPrint();

		for(Match m : matchesToPrint) {
			printMatch(m.patternHandler, m.sourceHandler, m.location);
		}
	}

	/**
	 * Format and print a match to standard output
	 * 
	 * @param pHandler -- Pattern Handler
	 * @param sHandler -- Source Handler
	 * @param loc      -- Match Location
	 */
	private void printMatch(ImageHandler pHandler, ImageHandler sHandler, Point loc){
		System.out.println(pHandler.getName() + " matches " +
				sHandler.getName() + " at " +
				pHandler.getWidth() + "x" + pHandler.getHeight() +
				"+" + loc.x +
				"+" + loc.y);
	}
}
