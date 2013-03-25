package ImageMatcher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Handles File to BufferedImage validation and conversion
 */
public class ImageHandler {

	private BufferedImage image = null;     // Handled Image
	private boolean validImg = false;       // Is Handled Image Valid?
	private String type = null;			    // Image Type			
	private String name = "";				// Image Name
	private final ArrayList<String> VALID_TYPES = new ArrayList<String>() {{
		add("gif"); 
		add("jpeg");
		add("png");
	}};
	/**
	 * Constructor
	 *
	 * Handles File Validation/Initializes Buffered Image
	 *
	 * @param imageFile Image File
	 */
	public ImageHandler(File imageFile) {
		// Open Image Reading Streams
		try {
			FileInputStream fis = new FileInputStream(imageFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ImageInputStream iis = ImageIO.createImageInputStream(bis);

			// Grab the appropriate image reader for the input stream
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

			// If it is a support ImageIO reader type (JPEG, GIF, PNG, BMP, WBMP)
			if (iter.hasNext()) {
				ImageReader reader = (ImageReader) iter.next();
				reader.setInput(iis);
				String fileType = reader.getFormatName().toLowerCase();

				// Validate reader type
				if (VALID_TYPES.contains(fileType)) {
					image = reader.read(0);
					name = imageFile.getName();
					validImg = true;
					type = fileType;
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
		} catch (IOException e) {
			System.out.println("Error reading file @ " + imageFile.getAbsolutePath());
		}
	}

	public void convertToGIF() {
		try {
			if(!type.equals("gif")) {
				BufferedImage gif = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				ImageOutputStream ios = ImageIO.createImageOutputStream(gif);
				ImageIO.write(image, "gif", ios);
				image = gif;
				type = "gif";
			}
		} catch (IOException e) {
			System.out.println("Error converting to GIF");
		}
	}
	
	public void convertToGreyScale() {		
		BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = gray.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		image = gray;
	}
	
	/** ---------- GETTERS AND SETTERS ---------- */
	
	/**
	 * Use this to verify that the image being handled is existent/valid before
	 * using it
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

	public int getHeight() {
		return image.getHeight();
	}

	public int getWidth() {
		return image.getWidth();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

}