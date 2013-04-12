import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Senior Project Image Matching Software
 */
public class SPIMS {
	
	public static void main(String[] args) {
		
    	// Validate Input Parameters
		ParameterHandler ph = new ParameterHandler(args);
		ArrayList<File> patternImgs = ph.getPatterns();
		ArrayList<File> sourceImgs = ph.getSources();

		// Kick off a thread pool of comparisons
		ExecutorService executor = Executors.newFixedThreadPool(Constants.NUM_THREADS);
		for(File pattern : patternImgs) {
			ComparisonThread ct = new ComparisonThread(pattern, sourceImgs);
			executor.execute(ct);
		}
		
		executor.shutdown();		
	}
}