package ImageMatcher;

import java.io.File;

public class Main
{    
	// TEST RUNS:
	// 4th comparison - flower.gif to ac1000.jpg fails
	// Might have to do with quality, not sure
    
        // 1 (black.jpg, bb0001.jpg took 5 seconds, correct location)
    
        // 3 (cliff.png, an0300.jpg took 4 seconds, correct location)
    
        // 4 (flower.gif, ac1000.jpg took 6 seconds, location slightly wrong)
    
        // 5 (nature.jpg, hh0021.jpg took 4 seconds, correct location)
	
	// 6 (rock.jpg, ar0800.jpg) took 4 seconds, correct location)
	
	// 7 (tree.jpg, aa0010.jpg) took 6 seconds, correct location)
    
    
    
    
	
	// For everything else a match was detected at the correct top left location
	
	public static void main(String[] args)
	{
        File file = new File("C:\\Patterns\\ranch.jpg");
        File file2 = new File("C:\\Sources\\ai0059.gif");

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
