package ImageMatcher;

import java.io.File;

// For everything else a match was detected at the correct top left location
public class Main {

	// TEST RUNS:
	// 4th comparison - flower.gif to ac1000.jpg fails
	// Might have to do with quality, not sure
	// 1 (black.jpg, bb0001.jpg took 5 seconds, correct location)
	// 3 (cliff.png, an0300.jpg took 4 seconds, correct location)
	// 4 (flower.gif, ac1000.jpg took 6 seconds, location slightly wrong)
	// 5 (nature.jpg, hh0021.jpg took 4 seconds, correct location)
	// 6 (rock.jpg, ar0800.jpg) took 4 seconds, correct location)
	// 7 (tree.jpg, aa0010.jpg) took 6 seconds, correct location)
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Parameter Helper to help check inputs
		ParameterHandler ph = new ParameterHandler();

		// Check to make sure there is the correct amount of arguments
		// and the correct flags
		try {
			ph.checkAllFlagsExist(args);
		} catch (Exception ex) {
			System.out.println(ex);
			System.exit(1);
		}
		// Compare the patterns and images
		ImageComparator comp = new ImageComparator();
		File pFile = new File(args[1]);
		File sFile = new File(args[3]);
		String[] patterns = new String[1];
		String[] sources = new String[1];
		
		// Get a list of pattern files
		if(pFile.isDirectory())
		{
			patterns = pFile.list();
			for(int i=0; i<patterns.length; i++)
			{
				patterns[i] = pFile.getAbsolutePath() + File.separatorChar + patterns[i];
			}
		}
		else
		{
			patterns[0] = pFile.getAbsolutePath();
		}
		
		// Get a list of source files
		if(sFile.isDirectory())
		{
			sources = sFile.list();
			for(int i=0; i<sources.length; i++)
			{
				sources[i] = sFile.getAbsolutePath() + File.separatorChar + sources[i];
			}
		}
		else
		{
			sources[0] = sFile.getAbsolutePath();
		}
		
		// Compare all our patterns with all our sources
		for(String p : patterns)
		{
			ImageHandler pattern = new ImageHandler(new File(p));
			
			for(String s : sources)
			{
				ImageHandler source = new ImageHandler(new File(s));
				
				comp.compare(pattern, source);
			}
		}
	}
}
