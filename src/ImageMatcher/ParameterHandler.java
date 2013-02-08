package ImageMatcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ParameterHandler {

	private List<File> patterns, sources;
	
	public void checkParameters(String[] args) throws Exception{

		checkAllFlagsExist(args);
		setFileLocations(args);		
	}
	
	private boolean checkAllFlagsExist(String[] args) throws Exception{
		
		if(args.length != 4){ throw new Exception("ERROR - Expected 4 Parameters");}
		if(!args[0].equals("-p")) {throw new Exception("ERROR - Missing Pattern Flag");}
		if(!args[2].equals("-s")) {throw new Exception("ERROR - Missing Source Flag");}
		
		return true;
	}
	
	private void setFileLocations(String[] args) throws Exception {
		if(args.length == 2) {
			patterns = args[0].equals("-s") ? obtainImagePath(args[1]) : obtainImagePath(args[3]);
			sources = args[0].equals("-p") ? obtainImagePath(args[1]) : obtainImagePath(args[3]);
		} else {
			throw new Exception("ERROR - Not Enough Arguments");
		}
	}
	
	private List<File> obtainImagePath(String location) throws Exception {
		List<File> imagePaths = new LinkedList<File>();

		File path = new File(location);
		if(path.isDirectory()) {
			//TODO YOU ARE A BONER. HANDLE DIRECTORIES HERE
		}else if(path.isFile()) {
			imagePaths.add(path);
		}else {
			throw new Exception("ERROR - Location Path is Trash Bro");
		}
		
		return imagePaths;
	}

	public List<File> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<File> patterns) {
		this.patterns = patterns;
	}

	public List<File> getSources() {
		return sources;
	}

	public void setSources(List<File> sources) {
		this.sources = sources;
	}
			
}
