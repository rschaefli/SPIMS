package ImageMatcher;

import java.io.File;
import java.util.ArrayList;

public class SPIMS {

	// Specifies the desired number of comparison threads
	private final static int NUM_THREADS = 1;
	
	public static void main(String[] args) {

		// Validate Input Parameters
		ParameterHandler ph = new ParameterHandler(args);
		ArrayList<File> patternImgs = ph.getPatterns();
		ArrayList<File> sourceImgs = ph.getSources();

		// Initialize and begin NUM_THREADS comparison threads
		ComparisonThread[] compThreads = new ComparisonThread[5];
		for(int i = 0; i < NUM_THREADS; i++) {
			if(patternImgs.size() > 0) {
				ComparisonThread ct = new ComparisonThread(patternImgs.get(0), sourceImgs, i);
				ct.run();
				compThreads[i] = ct;
				patternImgs.remove(0);
			}
		}

		// Continue running pattern comparisons as the initial threads finish
		while(patternImgs.size() != 0) {
			for(ComparisonThread ct : compThreads) {
				if(ct.isThreadFinished()) {
					int index = ct.getIndex();
					ComparisonThread newCT = new ComparisonThread(patternImgs.get(0), sourceImgs, index);
					newCT.run();
					compThreads[index] = newCT;
					patternImgs.remove(0);
					break; // Break out to verify that we still have pattern images left
				}
			}
		}
	
	}
	
}
