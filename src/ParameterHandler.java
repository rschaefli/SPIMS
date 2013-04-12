import java.io.File;
import java.util.ArrayList;

/**
 * Handles Input Parameter Validation
 */
public class ParameterHandler {

	private ArrayList<File> patterns = new ArrayList<File>(); // Pattern File Locations
	private ArrayList<File> sources = new ArrayList<File>();  // Source File Locations

	/**
	 * CONSTRUCTOR
	 *
	 * Handles Input Parameter Validation
	 *
	 * @param args -- Input Parameters
	 */
	public ParameterHandler(String[] args) {
		checkAllFlagsExist(args);
		setImagery(args);
	}

	/**
	 * Do all of the necessary parameter flags exist?
	 * 
	 * @param args -- Parameters
	 */
	private void checkAllFlagsExist(String[] args) {
		if (args.length != 4) {
			System.err.println("ERROR - Expected 4 Parameters");
			System.exit(1);
		}

		if(!Constants.VALID_PATTERN_FLAGS.contains(args[0])) {
			System.err.println("ERROR - Invalid Pattern Flag");
			System.exit(1);
		}

		if(!Constants.VALID_SOURCE_FLAGS.contains(args[2])) {
			System.err.println("ERROR - Invalid Source Flag");
			System.exit(1);
		}
	}

	/**
	 * Sets the pattern and source imagery files
	 * 
	 * @param args -- Parameters
	 */
	private void setImagery(String[] args) {
		this.patterns = obtainImagery(args[1]);
		this.sources = obtainImagery(args[3]);
	}

	/**
	 * Obtains imagery files from a given path location
	 * 
	 * @param location -- Imagery location
	 * 
	 * @return ArrayList of Imagery files
	 */
	private ArrayList<File> obtainImagery(String location) {
		ArrayList<File> imagePaths = new ArrayList<File>();

		File path = new File(location);

		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for(File image : files) {
				if(image.isDirectory()) {
					System.err.println("ERROR - Subdirectories not supported @ " + image.getAbsolutePath());
					System.exit(1);
				}
				
				// Only add paths in the given directory that represent a file
				if(image.isFile()) {
					imagePaths.add(image);
				}
			}
		} else if (path.isFile()) {
			imagePaths.add(path);
		} else {
			System.err.println("ERROR - Location Path is invalid " + path);
			System.exit(1);
		}

		return imagePaths;
	}

	/** ---------- GETTERS AND SETTERS ---------- */

	public ArrayList<File> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<File> patterns) {
		this.patterns = patterns;
	}

	public ArrayList<File> getSources() {
		return sources;
	}

	public void setSources(ArrayList<File> sources) {
		this.sources = sources;
	}
}