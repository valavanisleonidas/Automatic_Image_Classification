package ImageRepresentationModels.BoVW.Descriptors;

import gr.iti.mklab.visual.extraction.AbstractFeatureExtractor;
import gr.iti.mklab.visual.extraction.SIFTExtractor;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import net.sf.javaml.core.kdtree.KDTree;
import Clustering.KMeans.KDTreeImplementation;


public class SiftFeatures implements Features {
	AbstractFeatureExtractor sift = new SIFTExtractor();

	public void extractFeatures(BufferedImage image, BufferedWriter siftWriter)  throws Exception {
		double[][] siftFeatures = sift.extractFeatures(image);
		for (int i = 0 ; i < siftFeatures.length ; i++) {
			for (int j = 0 ; j < siftFeatures[i].length; j++) {
				siftWriter.write(siftFeatures[i][j]+" ");
			}
			siftWriter.write("\n");
		} 
	}
	
public void extractFeatures(BufferedImage image,List<double[]> centroids)  throws Exception {
	
	double[][] siftFeatures = sift.extractFeatures(image);
	 for (int i = 0 ; i < siftFeatures.length ; i++) {
		 double[] features = new double[128] ;
		 for (int j = 0 ; j < siftFeatures[i].length; j++) {
			 double featurePoint= siftFeatures[i][j];
			 features[j] =featurePoint; 
		 }
		 centroids.add(features);
	 }
}

@Override
public double[] extract(BufferedImage image, KDTree tree, int clusterNum) throws Exception {
	
	List<double[]> centroids = new ArrayList<double[]>();
	double[][] siftFeatures = sift.extractFeatures(image);
	for (int i = 0 ; i < siftFeatures.length ; i++) {
		double[] features = new double[128] ;
		for (int j = 0 ; j < siftFeatures[i].length; j++) {
			double featurePoint= siftFeatures[i][j];
			features[j] =featurePoint; 
		}
		centroids.add(features);	
	}
	 
	double[] imgVocVector = new double[clusterNum];//num of clusters
	for ( int i =0;i<imgVocVector.length;i++)
		imgVocVector[i]=0;
		 
	//gia kathe ena feature trexe me ola ta centroids kai vres tin mikroteri apostasi
	for (int i = 0 ; i < centroids.size() ; i++) {
		int positionofMin= KDTreeImplementation.SearchTree(centroids.get(i),tree);
		imgVocVector[positionofMin]++;
	}
	return imgVocVector;
	
}

@Override
public double[] extract(BufferedImage bimage) {
	return null;
}

	
		
}
