import java.io.File;
import java.util.ArrayList;

/**
 * Handles single pattern to multiple source imagery comparisons
 */
public class ComparisonThread implements Runnable {

	private ImageHandler patternImg = null;					 // Pattern Image
	private ArrayList<File> sources = new ArrayList<File>(); // File paths for source imagery

	/**
	 * CONSTRUCTOR
	 *  
	 * @param pattern -- Pattern Image File
	 * @param sources -- Source Image Files
	 * @param index   -- Thread Index
	 */
	public ComparisonThread(File pattern, ArrayList<File> sources) {		
		/*
		 * We don't turn source image files into ImageHandlers right off the bat to save resource space
		 * in the scenario that there is a lot of source imagery. Instead, we convert the file paths into
		 * ImageHandlers as we do a comparison in the run() method.
		 */
		this.sources = sources; 
		this.patternImg = new ImageHandler(pattern);
	}

	/**
	 * Begins the pattern image -> source imagery comparisons
	 */
	@Override
	public void run() {
		String patternHash = PHash.createHash(patternImg.getImage());

		for(File image : sources) {
			ImageHandler sourceImg = new ImageHandler(image);
			ImageComparator ic = new ImageComparator(patternImg, sourceImg, patternHash);
			ic.compare();  
		}
	}

}