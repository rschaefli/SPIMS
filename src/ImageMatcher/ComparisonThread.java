package ImageMatcher;

import java.io.File;
import java.util.ArrayList;

/**
 * Handles single pattern to multiple source imagery comparisons
 */
public class ComparisonThread extends Thread {

	private ImageHandler patternImg = null;					 // Pattern Image
	private ArrayList<File> sources = new ArrayList<File>(); // File paths for source imagery
	private boolean threadFinished = false;                  // Is this thread finished?
	private int index = 0;									 // Represents this thread's index
	
	/**
	 * Constructor
	 *  
	 * @param pattern Pattern Image File
	 * @param sources Source Image Files
	 * @param index Thread Index
	 */
	public ComparisonThread(File pattern, ArrayList<File> sources, int index) {		
		/*
		 * We don't turn source image files into ImageHandlers right off the bat to save resource space
		 * in the scenario that there is a lot of source imagery. Instead, we convert the file paths into
		 * ImageHandlers as we do a comparison in the run() method.
		 */
		this.sources = sources; 
		this.patternImg = new ImageHandler(pattern);
		this.index = index;
	}
	
	/**
	 * Begins the pattern image -> source imagery comparisons
	 */
	public void run() {
		System.out.println("Beginning comparisons on thread " + index + " with " + patternImg.getName());
		if(patternImg.isValidImg()) {
			for(File image : sources) {
				ImageHandler ih = new ImageHandler(image);
				
				if(ih.isValidImg()) {
					ImageComparator ic = new ImageComparator(patternImg, ih);
					ic.compare();
				}
			}
		}
		threadFinished = true;
		System.out.println("Ending comparisons on thread " + index);
	}


	/** ---------- GETTERS ---------- */
	
	public boolean isThreadFinished() {
		return threadFinished;
	}
	
	public int getIndex() {
		return index;
	}

}
