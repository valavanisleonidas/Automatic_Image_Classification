package Clustering.KMeans;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Shell;
import ImageRepresentationModels.BoVW.Descriptors.Features;
import Utils.Utilities;
import net.semanticmetadata.lire.utils.cv.KMeans;

public class LireKmeans implements kmeansInterface {

    private Features descriptor;
	private BufferedWriter descriptorWriter ;
	private List<double[]> descriptorFeatures;
	private List <double[]> descriptorCenters;
	private Shell lireShell;

public LireKmeans(){
	descriptorFeatures= new ArrayList<double[]>();	
	descriptorWriter = null; 	
}
	
public void StartKmeans(String clusterDBPath ,String projectPath,int clustersNum,int sampleSize, String descriptorChoice, Shell shell) throws Exception{
	lireShell=shell;
	//get descriptor Object
	descriptor =Utilities.getDescriptor(descriptorChoice);
	//open file for writing lire  features
	
	
	descriptorWriter = Utilities.openFilesForWriting(projectPath+"\\LireSample-"+descriptorChoice+"-"+clustersNum+"-Features.txt",
			 lireShell);
	 
	 Utilities.writeFeatures2TXT(clusterDBPath,sampleSize,descriptorChoice,descriptor,descriptorWriter
			  ,"Lire",descriptorFeatures,lireShell);	
	
	 
		System.out.println("descriptor nameee : "+ descriptorFeatures.size());
		System.out.println("descriptor nameee : "+ descriptorFeatures.get(0).length);

	//cluster
	KMeans mean=new KMeans(descriptorFeatures, clustersNum);
	//get centers 
	descriptorCenters= mean.getMeans();	
	//write centers to txt
	Utilities.write2TXT(descriptorCenters, projectPath+"\\LireKMeans-"+descriptorChoice+"-"
			+clustersNum+"-Centroids.txt",lireShell);
}


	
public List<double[]> getCenters(){
	return descriptorCenters;	
}

}
