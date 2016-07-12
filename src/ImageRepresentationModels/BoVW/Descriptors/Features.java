package ImageRepresentationModels.BoVW.Descriptors;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.util.List;

import net.sf.javaml.core.kdtree.KDTree;

public interface Features {

	public void extractFeatures(BufferedImage image,BufferedWriter Writer) throws  Exception;
	public void  extractFeatures(BufferedImage image,List<double[]> centroids)  throws Exception; 
	public double[]  extract(BufferedImage image, KDTree tree, int clusterNum)  throws Exception;
	public double[] extract(BufferedImage bimage); 

}
