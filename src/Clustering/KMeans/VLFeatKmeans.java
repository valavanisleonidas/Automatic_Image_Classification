package Clustering.KMeans;

import java.io.BufferedWriter;
import java.util.*;
import org.eclipse.swt.widgets.Shell;
import ImageRepresentationModels.BoVW.Descriptors.Features;
import Utils.Utilities;


public class VLFeatKmeans  implements kmeansInterface{

    private Features descriptor;
	private BufferedWriter descriptorWriter;
	private List <double[]> descriptorCenters;
	private Shell vlShell;
	
	public VLFeatKmeans()
	{
		descriptorWriter = null;	
		descriptorCenters=new ArrayList<double[]>();	
	}
	 
public void StartKmeans(String clusterDBPath, String projectPath,int clustersNum,int sampleSize, String descriptorChoice
			 , Shell shell) throws Exception{
		 vlShell=shell;
		 
		//calls matlab with command from getMatlabCommand
		Utilities.VLFeatMatlab(new String[] {"cd([ pwd '\\matlab_files' ])" , getMatlabCommand(descriptorChoice,sampleSize,clusterDBPath,clustersNum,projectPath) } );
		//read centroids from file
		descriptorCenters= Utilities.readFromFile(projectPath+"\\VLFeatKMeans-"+descriptorChoice+"-"+clustersNum+"-Centroids.txt",vlShell);
		
	 }
	 
private String getMatlabCommand(String descriptorChoice, int sampleSize , String clusterDBPath,int clustersNum
			 ,String projectPath) throws Exception{
		
		 if(descriptorChoice.equals("Sift") || descriptorChoice.equals("ColorCorrelogram-Sift") || descriptorChoice.equals("Surf")){
			  descriptor =Utilities.getDescriptor(descriptorChoice);
			 
			  descriptorWriter = Utilities.openFilesForWriting(projectPath+"\\VLFeatSample"+descriptorChoice+"Features.txt",
					  vlShell);
			 
			  Utilities.writeFeatures2TXT(clusterDBPath,sampleSize,descriptorChoice,descriptor,descriptorWriter
					  ,"",null,vlShell);
			  
			 return "extractingVW(" +clustersNum+" , '"+projectPath+"' , '"+descriptorChoice+"')";
		 }
		 else if (descriptorChoice.equals("Dense Sift"))
			return  "DSIFTClustering('"+projectPath+"' , "+clustersNum+"  , '"+clusterDBPath+"' , "+sampleSize+"); ";
		 else 	// if (descriptorChoice.equals("Phow"))
			return "PHOWClustering('"+projectPath+"' , "+clustersNum+" , '"+clusterDBPath+"' , "+sampleSize+" ); ";
				 
	 }


public List<double[]> getCenters(){
	return descriptorCenters;
}
	
	
}
