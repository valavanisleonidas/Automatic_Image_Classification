package Utils.Image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import Utils.Utilities;

public class ImageConverter {

	public static File PNGtoJPG(File source)
	{
		File dest=null;
		BufferedImage bufferedImage;
	
		try {
	 	  //read image file
		  bufferedImage = ImageIO.read(source);
	 	  // create a blank, RGB, same width and height, and a white background
		  BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		  newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
	  		
		  dest = new File (source.getParent()+"\\"+Utilities.getName(source.getName())+".jpg");
		  // write to jpeg file
		  ImageIO.write(newBufferedImage, "jpg", dest);
	 
		
		} catch (Exception e) {	}
	
		return dest;
		
	}
	
	
	
}
