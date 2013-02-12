package ImageMatcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class HistogramComparator implements Comparator{

	public enum Colors {Red, Green, Blue};
	BufferedImage Porno;
	BufferedImage Tits;
	int buffer = 50;
	
	@Override
	public void compare(ImageHandler pattern, ImageHandler source) {	
		
		int sHeight = source.getImage().getHeight();
		int sWidth = source.getImage().getWidth();
		int pWidth = pattern.getImage().getWidth();
		int pHeight = pattern.getImage().getHeight();
		
		Porno = convertToGrayScale(pattern.getImage());
		Tits = convertToGrayScale(source.getImage());
		
		int patternBlue = new Color( Porno.getRGB(0, 0)).getBlue();
		
		for(int y = 0; y < sHeight; y++){
			for(int x = 0; x < sWidth; x++){
				//System.out.println("Going through the Routine");
				int sourceBlue = new Color(Tits.getRGB(x, y)).getBlue();
				
				if(Math.abs(sourceBlue - patternBlue ) < buffer){
					//System.out.println("Checking that Pussy " + x + "," +y);
					if(checkPussy(x,y,pWidth,pHeight,sWidth,sHeight)){
						System.out.println("Match found starting at " + x + "," + y);
						//System.exit(0);
					}
					
				}
			}
		}
					
	}
	
	public static BufferedImage convertToGrayScale(BufferedImage image) {
		  BufferedImage result = new BufferedImage(
		            image.getWidth(),
		            image.getHeight(),
		            BufferedImage.TYPE_BYTE_GRAY);
		  Graphics g = result.getGraphics();
		  g.drawImage(image, 0, 0, null);
		  g.dispose();
		  return result;
	}
	
	public boolean checkPussy(int startX, int startY, int pWidth, int pHeight, int sWidth, int sHeight){
		for(int y = 0; y < pHeight; y++){
			for(int x = 0; x < pWidth; x++){
				//System.out.println("Inside" + x + "," +y);
				int pBlue = new Color(Porno.getRGB(x, y)).getBlue();
				//System.out.println("Getting Source Blue: " + (startX + x) + "," + (startY + y));
				if((startX + x) >= sWidth || (startY + y) >= sHeight){ return false;}
				int sBlue = new Color(Tits.getRGB(startX + x, startY + y)).getBlue();
				
				if(Math.abs(pBlue - sBlue) > buffer){
					//System.out.println("False alarm");
					return false;
				}
				//System.out.println(x);
			}
		}
		return true;
	}
	

}
