import java.util.ArrayList;

/**
 * Contains the majority of constants used throughout the classes
 */
public class Constants {

	// Set number of comparison threads to number of cores
	public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	
	// Valid handled pattern parameter flags
	@SuppressWarnings("serial")
	public static final ArrayList<String> VALID_PATTERN_FLAGS = new ArrayList<String>() {{
		add("-p");
		add("-pdir");
		add("--pdir");
	}};

	// Valid handled source parameter flags
	@SuppressWarnings("serial")
	public static final ArrayList<String> VALID_SOURCE_FLAGS = new ArrayList<String>() {{
		add("-s");
		add("-sdir");
		add("--sdir");
	}};
	
	// Valid handled image types
	@SuppressWarnings("serial")
	public static final ArrayList<String> VALID_TYPES = new ArrayList<String>() {{
		add("gif"); 
		add("jpeg");
		add("png");
	}};
	
	// The number of pixels per corner that are compared
	public static final int PIXEL_COMPARISON_DEPTH = 15; 
	
	// Where to stop checking for additional pixels
	public static final int MAX_AVERAGE_COLOR_DIFFERENCE = 100;
	
	// The maximum of color differences to check for when dealing with small imagery
    public static final int MAX_SMALL_IMAGE_AVERAGE_COLOR_DIFFERENCE = 50;
    
	// What is considered a small image based on the set PIXEL_COMPARISON_DEPTH
	public static final int SMALL_IMAGE_SIZE = (int) Math.pow(2 * PIXEL_COMPARISON_DEPTH, 2);
	
	// Highest acceptable difference in match pHash hamming distances
	public static final int HIGHEST_ACCEPTABLE_DIFFERENCE = 5;

	// Batch size of corners to deal with at a time
	public static final int BATCH_SIZE = 5;
	
}
