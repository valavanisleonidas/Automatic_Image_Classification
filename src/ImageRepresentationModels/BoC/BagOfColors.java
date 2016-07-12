package ImageRepresentationModels.BoC;

import ImageRepresentationModels.ExtractFeatures;
import ImageRepresentationModels.BoC.BoCDescriptor;
import Utils.Image.ImageUtility;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import Utils.Utilities;


/**
 * 
 * @author Valavanis Leonidas
 */
public class BagOfColors implements ExtractFeatures{
	 private int NoOfColors;
	 private String colorSpace,normalization;
	 private int [][] palete;
	 private BoCDescriptor bocDescriptor= new BoCDescriptor();
  
   public double[] extractImage(String imagePath,double[] imageFeature) throws Exception
   {
 		//System.out.println("boc :"+imagePath);
	
		//get image
	    BufferedImage img = ImageUtility.getImage(imagePath);   
	    //extract image
	    double[] vec=getBocDescriptor().extract(img); 
	    //normalize array
	    
	    //vec=Utilities.normalizeArray(vec,normalization);
	  
	    
	    return vec;
   }
    

public int getNoOfColors(){
	return NoOfColors;
}
public int[][] getPalete() {
	return palete;
}
public void setPalete(int [][] palete) {
	this.palete = palete;
}
public void setNoOfColors(int noOfColors) {
	NoOfColors = noOfColors;
}
public void setColorspace(String colorspace) {
	this.colorSpace = colorspace;
}
public String getColorSpace(){
	return colorSpace;	
}
public BoCDescriptor getBocDescriptor() {
	return bocDescriptor;
}
public void setBocDescriptor(BoCDescriptor bocDescriptor) {
	this.bocDescriptor = bocDescriptor;
}


public String getNormalization() {
	return normalization;
}


public void setNormalization(String normalization) {
	this.normalization = normalization;
}

}
