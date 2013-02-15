package ImageMatcher;

import java.io.File;

public class Main
{    
	// TEST RUNS:
	// 4th comparison - flower.gif to ac1000.jpg fails
	// Might have to do with quality, not sure
	
	// 6 (rock.jpg, ar0800.jpg) took 20 seconds
	
	// 7 (tree.jpg, aa0010.jpg) took 6 seconds
	
	// For everything else a match was detected at the correct top left location
	
	public static void main(String[] args)
	{
        File file = new File("C:\\Patterns\\flower.gif");
        File file2 = new File("C:\\Sources\\ac1000.jpg");

		// Check the parameters and load them into the parameter object
//		ParameterHandler pa = new ParameterHandler();
//		try
//		{
//			//Checks the input is properly formed
//			pa.checkParameters(args);
//		}
//		catch(Exception e)
//		{
//			System.out.println("ERROR - There was an error parsing the inputs \n" + e.toString());
//		}
//		
//		// Now we can get the list of patterns and sources
//		List<File> patternImages = pa.getPatterns();
//		List<File> sourceImages = pa.getSources();
    	
		// Now create ImageHandlers
		// For multiple inputs this needs to be put in a loop over patterns and sources
		// For now, just get the first of each input
//		ImageHandler patternHandler = new ImageHandler(patternImages.get(0));
//		ImageHandler sourceHandler = new ImageHandler(sourceImages.get(0));
		ImageHandler patternHandler = new ImageHandler(file);
		ImageHandler sourceHandler = new ImageHandler(file2);
        
        ImageComparator comparator = new ImageComparator();
        comparator.compare(patternHandler, sourceHandler);
    }
}
