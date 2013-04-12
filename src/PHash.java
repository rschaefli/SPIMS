import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * Used to obtain a 64-bit binary hash string based off of supplied Buffered Images
 */
public class PHash {

	private static final int SIZE_32 = 32; // 32x32 Image 
	private static final int SIZE_8 = 8;   // 8x8 Image

	/**
	 * Creates a 64-bit binary string based representing the given image
	 * @param img Image to obtain a hash of
	 * @return 64-bit binary string representing image
	 */
	public static String createHash(BufferedImage img) {

		// Reduce the image size to a 32x32 to simplify the DCT (discrete cosine transformation) computation
		img = resize(img);

		// Reduce the image to grayscale
		ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		colorConvert.filter(img, img);
		
		// Obtain the blue values for each pixel in a 32x32 img area
		int[][] blueValues = new int[SIZE_32][SIZE_32];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				blueValues[x][y] = (img.getRGB(x, y)) & 0xff;
			}
		}

		// Separate the image into a collection of frequencies and scalars by applying a 32x32 DCT
		// to the blue pixel values
		double[][] dctVals = getDCT(blueValues);

		// Grab the top-top left 8x8 DCT vals from the 32x32 DCT as these are the lowest frequencies in the picture
		// Obtain the average value from the DCT values minus the first term because it is the DC coefficient and will
		// throw off the average of the other values because it is generally very different
		double total = 0;
		for (int x = 0; x < SIZE_8; x++) {
			for (int y = 0; y < SIZE_8; y++) {
				total += dctVals[x][y];
			}
		}
		total -= dctVals[0][0];
		double avg = total / (double) ((SIZE_8 * SIZE_8) - 1);
		
		// Create our 64bit hash bit string based on whether or not the DCT values lie above
		// or below the average low-frequency DCT values. This gives us a rough relative scale
		// of the frequencies to the average which wont vary as long as the image structure 
		// does not change
		String hash = "";
		for (int x = 0; x < SIZE_8; x++) {
			for (int y = 0; y < SIZE_8; y++) {
				if (x != 0 && y != 0) {
					hash += (dctVals[x][y] > avg?"1":"0");
				}
			}
		}

		return hash;
	}
	
	/**
	 * Applies a discrete cosine transformation on a given 2D array of pixel color values
	 * @param f 32x32 2D float array representing a 32x32 image's pixel color values
	 * @return DCT array
	 */
	private static double[][] getDCT(int[][] f) {

		double[][] dctVals = new double[SIZE_32][SIZE_32];
		
		for (int u=0;u<SIZE_32;u++) {
			for (int v=0;v<SIZE_32;v++) {
				
				double sum = 0;
				
				for (int i=0;i<SIZE_32;i++) {
					for (int j=0;j<SIZE_32;j++) {
						
						sum+= Math.cos(((2*i+1)/(2.0*SIZE_32))*u*Math.PI) *
							  Math.cos(((2*j+1)/(2.0*SIZE_32))*v*Math.PI) *
							  (f[i][j]);
					}
				}
				
				double cu = u == 0 ? 1 / Math.sqrt(2.0) : 1;
				double cv = v == 0 ? 1 / Math.sqrt(2.0) : 1;

				sum*= (cu * cv / 4.0);
				dctVals[u][v] = sum;
			}
		}
		
		return dctVals;
	}

	/**
	 * Resizes the given image to a 32x32 image
	 * @param image Image to resize
	 * @return resized image
	 */
	private static BufferedImage resize(BufferedImage image) {
		BufferedImage resizedImage = new BufferedImage(SIZE_32, SIZE_32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, SIZE_32, SIZE_32, null);
		g.dispose();
		return resizedImage;
	}
	
	/**
	 * Obtain the difference between two pHashed strings
	 * @param one Hashed String 1
	 * @param two Hashed String 2
	 * @return Number of differences between the two strings
	 */
	public static int getHammingDistance(String one, String two) {
		if (one.length() != two.length()) {
			return -1;
		}
		int counter = 0;
		for (int i = 0; i < one.length(); i++) {
			if (one.charAt(i) != two.charAt(i)) {
				counter++;
			}
		}
		return counter;
	}
}