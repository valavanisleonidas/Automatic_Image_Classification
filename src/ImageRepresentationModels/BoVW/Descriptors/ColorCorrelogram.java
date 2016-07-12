package ImageRepresentationModels.BoVW.Descriptors;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.util.List;

import net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram;
import net.sf.javaml.core.kdtree.KDTree;

public class ColorCorrelogram implements Features {
	
	
	@Override
	public void extractFeatures(BufferedImage image, BufferedWriter Writer)
			throws Exception {

		AutoColorCorrelogram color= new AutoColorCorrelogram();
		color.extract(image);
		double[] FeaturesImage = color.getDoubleHistogram();
		for(int i=0;i<FeaturesImage.length;i++)
			Writer.write(FeaturesImage[i]+" ");
			
		Writer.write("\n");
		
	}

	@Override
	public void  extractFeatures(BufferedImage image,List<double[]> centroids)throws Exception {
		
		AutoColorCorrelogram color= new AutoColorCorrelogram();
		color.extract(image);
		double[] FeaturesImage = color.getDoubleHistogram();
		centroids.add(FeaturesImage);
		
	}
	

	@Override
	public double[] extract(BufferedImage image, KDTree tree, int clusterNum)throws Exception {		
		AutoColorCorrelogram color= new AutoColorCorrelogram();
		color.extract(image);
		
		return color.getDoubleHistogram();
	}
	
	public double[] extract(BufferedImage image) {	
		AutoColorCorrelogram color= new AutoColorCorrelogram();
		color.extract(image);
		
		return color.getDoubleHistogram();
		
	}


}
