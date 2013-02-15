package ImageMatcher;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Handles File to BufferedImage validation and conversion
 * 
 * @author RobToombs
 *
 */
public class ImageHandler{
	
	private BufferedImage image = null;     // Handled Image
	private String imageType = "";
        private boolean validImg = false;       // Is Handled Image Valid?
	
	// List of valid image types (MUST BE LOWERCASE!)
	private ArrayList<String> VALID_TYPES = new ArrayList<String>() {{
		add("jpeg");
		add("png");
		add("gif");
	}};
	
	/**
	 * Constructor
	 * 
	 * Handles File Validation/Initializes Buffered Image
	 *  
	 * @param imageFile Image File
	 */
	public ImageHandler(File imageFile){
		// Open Image Reading Streams
		try {
			FileInputStream	fis = new FileInputStream(imageFile);	
			BufferedInputStream bis = new BufferedInputStream(fis);
			ImageInputStream iis = ImageIO.createImageInputStream(bis);
	
			// Grab the appropriate image reader for the input stream
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			
			// If it is a support ImageIO reader type (JPEG, GIF, PNG, BMP, WBMP)
			if(iter.hasNext()) {
				ImageReader reader = (ImageReader) iter.next();
				reader.setInput(iis);
				String fileType = reader.getFormatName().toLowerCase();

				// Validate reader type
				if(VALID_TYPES.contains(fileType)) {
					image = reader.read(0);	
                                        imageType = fileType;
					validImg = true;
				} else {
					System.out.println("Invalid image type @ " + imageFile.getAbsolutePath());
				}
			} else {
				System.out.println("Invalid image type @ " + imageFile.getAbsolutePath());
			}
			
			// Close the input streams
			iis.close();
			bis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file @ " + imageFile.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file @ " + imageFile.getAbsolutePath());
			e.printStackTrace();
		}
	}

	/**
	 * Use this to verify that the image being handled is existent/valid before using it
	 * 
	 * @return Does this image handle contain a valid image?
	 */
	public boolean isValidImg() {
		return validImg;
	}
	
	public void setValidImg(boolean validImg) {
		this.validImg = validImg;
	}
		
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
        
        public String getImageType(){
            return imageType;
        }

	/**
	 * FOR TESTING PURPOSES, CAN REMOVE AT ANY TIME
	 *  
	 * @param args
	 */
//	public static void main(String[] args) {
//		File file = new File(args[0]);
//		ImageHandler handler = new ImageHandler(file);
//		if(handler.isValidImg()) {
//			BufferedImage img = handler.getImage();
//			System.out.println("IMAGE WIDTH x HEIGHT DIMENSIONS -- " + img.getWidth() + " x " + img.getHeight());
//		}
//	}
}
