package ImageRepresentationModels.BoVW.BoVW;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;

import net.sf.javaml.core.kdtree.KDTree;
import Utils.Image.ImageFilter;
import Utils.Image.ImageUtility;
import Utils.Utilities;
import Utils.Image.ImageConverter;
import ImageRepresentationModels.ExtractFeatures;
import ImageRepresentationModels.BoVW.Descriptors.ColorCorrelogram;
import ImageRepresentationModels.BoVW.Descriptors.Features;
import Clustering.KMeans.KDTreeImplementation;

public class BoVWModel implements ExtractFeatures  {

	 private int clusterNum ;
	 private Features descriptor1,descriptor2;
	 private List<double[]>  descriptorCentroids1,descriptorCentroids2;
	 private String descriptorChoice,KMeansChoice,normalization;
	public double[] extractImage(String imagePath ,double[] imageFeature) throws Exception
	{
		double [] imgVocVector=null;
	
		//System.out.println("bovw :"+imagePath);
			
				
		if(descriptorChoice.equals("Sift") || descriptorChoice.equals("Surf")  ){
			KDTree tree=KDTreeImplementation.createTree(descriptorCentroids1);
			//extract histogram
			imgVocVector = extractImageHistogram(descriptor1,new File(imagePath),tree);
			
		}else if( descriptorChoice.equals("ColorCorrelogram") ){
			//convert the image to jpg if it is png because the descriptor does not support png images
			File imaging = ImageFilter.isPNG(new File(imagePath)) ? ImageConverter.PNGtoJPG(new File(imagePath)) : new File(imagePath);
			
			//extract histograms		
			imgVocVector = extractImageHistogram(descriptor1,imaging,null);
					
			if(ImageFilter.isPNG(new File(imagePath) ) )
				imaging.delete();
					
			
		}else if( descriptorChoice.equals("ColorCorrelogram-Sift") ){
			KDTree tree=KDTreeImplementation.createTree(descriptorCentroids1);
			//convert the image to jpg if it is png because the descriptor does not support png images
			File imaging = ImageFilter.isPNG(new File(imagePath)) ? ImageConverter.PNGtoJPG(new File(imagePath)) : new File(imagePath);
			
			//extract histograms		
			double[] imgVocVector1 = extractImageHistogram(descriptor1,imaging,tree);
			double[] correlogram = extractImageHistogram(new ColorCorrelogram(),imaging,tree);
			//join Arrays
			imgVocVector = Utilities.concat(correlogram,imgVocVector1);//num of clusters + 1024 for colorCorrelogram
			
					
			if(ImageFilter.isPNG(new File(imagePath) ) )
				imaging.delete();
					
					
		}else if (descriptorChoice.equals("Sift-Surf")){
			KDTree tree=KDTreeImplementation.createTree(descriptorCentroids1);
			//extract Histograms
			double[] imgVocVector1 = extractImageHistogram(descriptor1,new File(imagePath),tree);
			double[] imgVocVector2 = extractImageHistogram(descriptor2,new File(imagePath),KDTreeImplementation.createTree(descriptorCentroids2));
			//join Arrays
			imgVocVector = Utilities.concat(imgVocVector1,imgVocVector2);
			
		}
		
		return imgVocVector;	
	}
	//extract histogram of descriptor
	private double[] extractImageHistogram(Features descriptor, File image, KDTree tree) throws Exception{
		 //read image
		 BufferedImage Bimage = ImageUtility.getImage(image.getAbsolutePath());   
	     //extract histogram
		 double[] imgVocVector;
		 if (descriptorChoice.equals("ColorCorrelogram"))
			 imgVocVector = descriptor.extract(Bimage);
		 else	 
			 imgVocVector = descriptor.extract(Bimage,tree,clusterNum);
		 
		 //normalize array
		 imgVocVector=Utilities.normalizeArray(imgVocVector,normalization);
		 return imgVocVector;
	}
	

public String getDescriptorChoice(){
	return descriptorChoice;
}
public void setDescriptorChoice(String descriptorChoice) {
	this.descriptorChoice = descriptorChoice;
}
public void setClusterNum(int clusterNum) {
	this.clusterNum = clusterNum;
}
public int getClusterNum() {
	return clusterNum;
}
public void setDescriptorCentroids1(List<double[]> descriptorCentroids1) {
	this.descriptorCentroids1 = descriptorCentroids1;
}
public void setDescriptorCentroids2(List<double[]> descriptorCentroids2) {
	this.descriptorCentroids2 = descriptorCentroids2;
}
public void setKMeansChoice(String kMeansChoice) {
	KMeansChoice = kMeansChoice;
}
public String getKMeansChoice() {
	return KMeansChoice ;
}
public void setDescriptor1(Features descriptor1) {
	this.descriptor1 = descriptor1;
}
public void setDescriptor2(Features descriptor2) {
	this.descriptor2 = descriptor2;
}
public String getNormalization() {
	return normalization;
}
public void setNormalization(String normalization) {
	this.normalization = normalization;
}
	
}
