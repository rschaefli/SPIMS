package ImageMatcher;

import java.io.File;
import java.util.ArrayList;

/**
 * Handles Input Parameter Validation
 */
public class ParameterHandler {

	private ArrayList<File> patterns = new ArrayList<File>();
	private ArrayList<File> sources = new ArrayList<File>();

	/**
	 * Constructor
	 *
	 * Handles Input Parameter Validation
	 *
	 * @param args Input Parameters
	 */
	public ParameterHandler(String[] args) {
		try {
			checkAllFlagsExist(args);
			setImagery(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Do all of the necessary parameter flags exist?
	 * 
	 * @param args Parameters
	 * @throws Exception Indicating a missing argument
	 */
	private void checkAllFlagsExist(String[] args) throws Exception {
		if (args.length != 4) {
			throw new Exception("ERROR - Expected 4 Parameters");
		}
		if (!args[0].equals("-p")) {
                    if(!args[0].equals("-pdir")){
                        throw new Exception("ERROR - Missing Pattern Flag");
                    }
		}
		if (!args[2].equals("-s")){
                    if(!args[2].equals("-sdir")){
			throw new Exception("ERROR - Missing Source Flag");
                    }
                }
	}

	/**
	 * Sets the pattern and source imagery files
	 * 
	 * @param args Parameters
	 * @throws Exception Indicates potential invalid path
	 */
	private void setImagery(String[] args) throws Exception {
		this.patterns = obtainImagery(args[1]);
		this.sources = obtainImagery(args[3]);
	}

	/**
	 * Obtains imagery files from a given path location
	 * 
	 * @param location Imagery location
	 * @return ArrayList of Imagery files
	 * @throws Exception Indicates an invalid location path
	 */
	private ArrayList<File> obtainImagery(String location) throws Exception {
		ArrayList<File> imagePaths = new ArrayList<File>();

		File path = new File(location);

		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for(File image : files) {
				// Only add paths in the given directory that represent a file
				if(image.isFile()) {
					imagePaths.add(image);
				}
			}
		} else if (path.isFile()) {
			imagePaths.add(path);
		} else {
			throw new Exception("ERROR - Location Path is invalid " + path);
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