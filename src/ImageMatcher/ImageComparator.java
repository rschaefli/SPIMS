package ImageMatcher;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageComparator implements Comparator
{	
	public ImageComparator() {}
	
	@Override
	public void compare(ImageHandler patternHandler, ImageHandler sourceHandler) 
	{
        ImagePHash imageHash = new ImagePHash();
        String patternHash = "";
        try
        {
        	patternHash = imageHash.getHash(patternHandler.getImage());
        }
        catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int colorDifferenceMargin = 5;
        
        if(patternHandler.getImageType().equals("gif") || sourceHandler.getImageType().equals("gif")){
            colorDifferenceMargin+=15;
        }
        
        ArrayList<Point> possibleTopLeftCorners = findPossibleTopLeftCorners(sourceHandler.getImage(), patternHandler.getImage(),colorDifferenceMargin);
        
        HashMap<Point, String> hashes = getPHashesOfLocations(sourceHandler.getImage(),
        						      possibleTopLeftCorners,
                                                              patternHandler.getImage().getWidth(),
                                                              patternHandler.getImage().getHeight());
        
        // Get a map of Locations -> PHashes
        // Compare each and check if we have a match at that location
        for(Entry<Point, String> entry : hashes.entrySet())
        {       	
        	Point location = entry.getKey();
        	String subimageHash = entry.getValue();
        	int difference = getHammingDistance(patternHash, subimageHash);
        	
        	if(difference != -1 && difference < 50)
        	{
        		System.out.println("Possible Match!!");
                        System.out.println(difference);
        		System.out.println(location.x);
        		System.out.println(location.y);
        	}
        }
	}

	// Works in conjunction with getAxisColors to determine if there are potential
	// pattern image matches within a source image
	public static ArrayList<Point> findPossibleTopLeftCorners (BufferedImage sourceImage, BufferedImage patternImage, int errorMargin)
	{
		HashMap<Point, Color> patternImageColors = getPixelColors(patternImage, 20);
		ArrayList<Point> possibleCorners = new ArrayList<Point>();
		int offPixelsToAllow = 5;
		
		for(int i=0; i<=sourceImage.getWidth()-patternImage.getWidth(); i++){
			for(int j=0; j<=sourceImage.getHeight()-patternImage.getHeight(); j++){
				boolean isPotentialMatch = true;
				int offPixelCount = 0;

				//Loop through the pixels and see if we have any matches
                                for(Entry<Point, Color> entry : patternImageColors.entrySet()){
                                    Point p = entry.getKey();
                                    Color patternPixelColor = entry.getValue();
                                    
                                    Color sourcePixelColor = new Color(sourceImage.getRGB(i + p.x, j + p.y));
                                    
                                    if(!isColorCloseTo(patternPixelColor, sourcePixelColor, errorMargin)){
                                        offPixelCount++;
                                    }
                                    
                                    isPotentialMatch = isPotentialMatch && (offPixelCount < offPixelsToAllow);
                                 
                                }

				// If we have a potential match, add origin to result
				if(isPotentialMatch)
				{
					possibleCorners.add(new Point(i, j));
				}
			}
		}
		
		return possibleCorners;
	}
	
	// Get howManyPerAxis pixels down the X and Y axis
	// This is used to elaborate on just comparing the top left pixel of sub images
	// The goal is to get less potential matches in the source image by checking more than 1 pixel
	public static HashMap<String, ArrayList<Color>> getAxisColors(BufferedImage image, int howManyPerAxis)
	{
		HashMap<String, ArrayList<Color>> result = new HashMap<String, ArrayList<Color>>();
		ArrayList<Color> xs = new ArrayList<Color>();
		ArrayList<Color> ys = new ArrayList<Color>();
		
		// Get the number of pixels to get on the X and Y axis
		int pixelsForXAxis = image.getWidth() > howManyPerAxis ? howManyPerAxis : image.getWidth();
		int pixelsForYAxis = image.getHeight() > howManyPerAxis ? howManyPerAxis : image.getHeight();
                int pixelsForXYAxis = image.getHeight() > howManyPerAxis ? howManyPerAxis : image.getHeight();
      
		// Get the color values of pixels on the X axis
		for(int i=0;i<pixelsForXAxis;i++)
		{
			Color curPixel = new Color(image.getRGB(i, 0));
			xs.add(curPixel);
		}
		result.put("x", xs);
      
		// Get the color values of pixels on the Y axis
		// NOTE: we are double checking 0,0 - shouldnt be much of an issue
		for(int i=0;i<pixelsForYAxis;i++)
		{
			Color curPixel = new Color(image.getRGB(0, i));
			ys.add(curPixel);
		}
		result.put("y", ys);
     
		return result;
	}
        // back on the left side of the second row.
	// This is used to elaborate on just comparing the top left pixel of sub images
	// The goal is to get less potential matches in the source image by checking more than 1 pixel
        
        public static HashMap<Point, Color> getPixelColors(BufferedImage image, int howManyPerAxis){
            HashMap<Point, Color> result = new HashMap<Point, Color>();
            int spacesToSkip = 30;
                    
        // Get howManyPixels ammount of pixels. Start at the top left corner,
        // skip every 30 pixels. Once at the right side of the first row, start

            mainloop:
            for(int j = 0; j < image.getHeight()-1; j++){
                for(int i = 0; i < image.getWidth()-1;i+=spacesToSkip){
                    Point curLocation = new Point(i, j);
                    Color curPixel = new Color(image.getRGB(i, j));
                    
                    result.put(curLocation, curPixel);
                    
                    if(result.size() == spacesToSkip){
                        break mainloop;
                    }       
                }
            }
            return result;
        }
	
	// Check if color c1s RGB values are all within errorMargin difference
	// of c2s RGB values
	public static Boolean isColorCloseTo(Color c1, Color c2, int errorMargin)
	{
		int c1Red = c1.getRed();
      	int c2Red = c2.getRed();
      	boolean isRedInRange = (c1Red >= c2Red - errorMargin) && (c1Red <= c2Red + errorMargin);
      
      	int c1Green = c1.getGreen();
      	int c2Green = c2.getGreen();
      	boolean isGreenInRange = (c1Green >= c2Green - errorMargin) && (c1Green <= c2Green + errorMargin);
      
      	int c1Blue = c1.getBlue();
      	int c2Blue = c2.getBlue();
      	boolean isBlueInRange = (c1Blue >= c2Blue - errorMargin) && (c1Blue <= c2Blue + errorMargin);
      
      	return (isRedInRange && isGreenInRange) || (isRedInRange && isBlueInRange) || (isBlueInRange && isGreenInRange);
                //(isRedInRange && isGreenInRange && isBlueInRange);
	}
	
    public static String getPHashOfSubImage(BufferedImage sourceImage, Point location, int patternWidth, int patternHeight)
    {
        BufferedImage subImage = sourceImage.getSubimage(location.x, location.y, patternWidth, patternHeight);
        ImagePHash imageHasher = new ImagePHash();
        String hash = "";
        
        try
        {
        	hash = imageHasher.getHash(subImage);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hash;
    }
    
    public static HashMap<Point, String> getPHashesOfLocations(BufferedImage sourceImage, ArrayList<Point> locations, int patternWidth, int patternHeight)
    {
        HashMap<Point, String> hashes = new HashMap<Point, String>();
        
        for(int i = 0; i < locations.size(); i++ )
        {
        	hashes.put(locations.get(i), getPHashOfSubImage(sourceImage, locations.get(i), patternWidth, patternHeight));
        }
        
        return hashes;
    }
    
 // Get the difference between 2 strings of equal length
	public static int getHammingDistance(String one, String two)
    {
        if (one.length() != two.length())
        {
            return -1;
        }
        int counter = 0;
        for (int i = 0; i < one.length(); i++)
        {
            if (one.charAt(i) != two.charAt(i)) counter++;
        }
        return counter;
    }
}
