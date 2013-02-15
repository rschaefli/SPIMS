package ImageMatcher;

import java.io.File;

public class Main
{    
	// TEST RUNS:
	// 4th comparison - flower.gif to ac1000.jpg fails
	// Quite possibly because the pattern is right in the bottom left corner and we might
	// be doing some bad math and not checking that far
	
	// 6th comparison - rock.jpg to ar0800.jpg takes forever as in minutes
	// Waiting for 3 mins and it didnt terminate. What is it about this image that takes so long?
	
	// Same for 7 as for 6. My guess is it has to do with all the red in 6/greens in 7
	// Results in a bunch of potential top left corners, meaning that logic needs to be reworked
	
	// No match for 8
	// Note that both 4 and 8 deal with gifs and jpgs for either the source or the pattern
	// This quality loss may well be the issues and a conversion to the lowest quality denominator
	// might solve the issues
	
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
        
//            ColorConvertOp greyscaleConverter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//            greyscaleConverter.filter(img, img);
//            greyscaleConverter.filter(img2, img2);
        
        ImageComparator comparator = new ImageComparator();
        comparator.compare(patternHandler, sourceHandler);
    }
}
