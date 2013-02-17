package ImageMatcher;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

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

		// Check to make sure there is the correct ammount of arguments
		// and the correct flags
		try {
			Boolean correctArguments = ph.checkAllFlagsExist(args);
		} catch (Exception ex) {
			System.out.println(ex);
			System.exit(1);
		}
		// Compare the patterns and images
		ImageComparator comp = new ImageComparator();
		File pFile = new File(args[1]);
		File sFile = new File(args[3]);

		if (pFile.isDirectory()) {
			String[] patterns = pFile.list();
			for (String p : patterns) {

				ImageHandler pattern = new ImageHandler(new File(
						pFile.getAbsolutePath() + File.separatorChar + p));

				if (sFile.isDirectory()) {
					String[] sources = sFile.list();
					for (String s : sources) {

						ImageHandler source = new ImageHandler(new File(
								sFile.getAbsolutePath() + File.separatorChar
								+ s));
						comp.compare(pattern, source);

					}

				} else {
					ImageHandler source = new ImageHandler(sFile);
					comp.compare(pattern, source);
				}
			}
		} else {
			ImageHandler pattern = new ImageHandler(pFile);
			if (sFile.isDirectory()) {
				String[] sources = sFile.list();
				for (String s : sources) {

					ImageHandler source = new ImageHandler(new File(
							sFile.getAbsolutePath() + File.separatorChar + s));
					comp.compare(pattern, source);

				}

			} else {
				ImageHandler source = new ImageHandler(sFile);
				comp.compare(pattern, source);
			}
		}

	}
}
