import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SPIMS {

	// Specifies the desired number of comparison threads
	private final static int NUM_THREADS = 4;
	
	public static void main(String[] args) {

    	// Validate Input Parameters
		ParameterHandler ph = new ParameterHandler(args);
		ArrayList<File> patternImgs = ph.getPatterns();
		ArrayList<File> sourceImgs = ph.getSources();

		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		for(File pattern : patternImgs) {
			ComparisonThread ct = new ComparisonThread(pattern, sourceImgs);
			executor.execute(ct);
		}
		
		executor.shutdown();		
	}
}

