package ImageRepresentationModels.BoVW.Descriptors;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.sf.javaml.core.kdtree.KDTree;
import Clustering.KMeans.KDTreeImplementation;

import com.stromberglabs.jopensurf.*;

public class SurfFeatures implements Features {

	public void extractFeatures(BufferedImage image, BufferedWriter surfWriter) throws IOException {
		Surf surf= new Surf(image);
		List<SURFInterestPoint> points = surf.getFreeOrientedInterestPoints();
		 
		 for ( int i =0 ; i< points.size();i++)
		{
		   float[] descriptor=	points.get(i).getDescriptor();
		   for ( int j =0 ; j< descriptor.length;j++)
			{
			   surfWriter.write(descriptor[j]+" ");
			}
		   surfWriter.write("\n");
		}
		
	}
	
	public void extractFeatures(BufferedImage image,List<double[]> centroids)  throws Exception {

		Surf surf= new Surf(image);
		List<SURFInterestPoint> points = surf.getFreeOrientedInterestPoints();
		for ( int i =0 ; i< points.size();i++)
		{
			float[] descriptor=	points.get(i).getDescriptor();
			double[] features = new double[64] ;
			for ( int j =0 ; j< descriptor.length;j++)
					features[j]=descriptor[j];
			centroids.add(features);
		}
	}

	

	@Override
	public double[] extract(BufferedImage image, KDTree tree, int clusterNum)
			throws Exception {
	
		List<double[]> centroids = new ArrayList<double[]>();
		Surf surf= new Surf(image);
		List<SURFInterestPoint> points = surf.getFreeOrientedInterestPoints();
		for ( int i =0 ; i< points.size();i++)
		{
			float[] descriptor=	points.get(i).getDescriptor();
			double[] features = new double[64] ;
			for ( int j =0 ; j< descriptor.length;j++)
			   features[j]=descriptor[j];
		
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