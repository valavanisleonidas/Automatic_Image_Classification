package Clustering;

import java.util.List;
import org.eclipse.swt.widgets.Shell;
import Clustering.KMeans.LireKmeans;
import Clustering.KMeans.VLFeatKmeans;
import Clustering.KMeans.kmeansInterface;
import Utils.Utilities;

public class clusteringBovwFactory implements Runnable {

	 private List<double[]> descriptorCentroids;
	 private String clusterDBPath, ProjectPath,comboKMeansChoice,descriptorChoice;
	 private int numOfClusters,SampleLimit;
	 private Shell shell;
		
	 @Override
	 public void run() 
	 {
		 kmeansInterface KMeans;
		 if(comboKMeansChoice.equals("VLFeat")){
			 KMeans = new VLFeatKmeans();
			    try {
			    	KMeans.StartKmeans (clusterDBPath , ProjectPath, numOfClusters, SampleLimit,descriptorChoice,shell);
					descriptorCentroids=KMeans.getCenters();
					Utilities.showMessage("VLFeat Clustering with descriptor : "+descriptorChoice+" ,clusters : "+numOfClusters +" "
			    			+ " completed successfully",shell,false);
					KMeans=null;
			    } catch (Exception e) {
					System.out.println(e);
			    	Utilities.showMessage("VLFeat Clustering with descriptor : "+descriptorChoice+" ,clusters : "
							+ ""+numOfClusters +" failed",shell,true);
			    	e.printStackTrace();
				}
			}
			else if (comboKMeansChoice.equals("Lire")){
				//LireKMeans
				KMeans = new LireKmeans();
				try {
					KMeans.StartKmeans (clusterDBPath , ProjectPath, numOfClusters, SampleLimit,descriptorChoice,shell);
					descriptorCentroids=KMeans.getCenters();
			    
					Utilities.showMessage("Lire Clustering with descriptor :"
							+ " "+descriptorChoice+" , clusters : "+numOfClusters +""
			    			+ " completed successfully",shell,false);
					KMeans=null;
				} catch (Exception e) {
					Utilities.showMessage("Lire Clustering with descriptor :"
							+ " "+descriptorChoice+" , clusters : "+numOfClusters +" failed",shell,true);
					e.printStackTrace();
				}
			}
	 }	
	
	public List<double[]> getDescriptorcentroids() {
		return descriptorCentroids;
	}
	public void setDescriptorcentroids(List<double[]> descriptorCentroids) {
		this.descriptorCentroids = descriptorCentroids;
	}
	public void setClusterDBPath(String clusterDBPath) {
		this.clusterDBPath = clusterDBPath;
	}
	public void setProjectPath(String projectPath) {
		ProjectPath = projectPath;
	}
	public String getComboKMeansChoice() {
		return comboKMeansChoice;
	}
	public void setComboKMeansChoice(String comboKMeansChoice) {
		this.comboKMeansChoice = comboKMeansChoice;
	}
	public String getDescriptorChoice() {
		return descriptorChoice;
	}
	public void setDescriptorChoice(String descriptorChoice) {
		this.descriptorChoice = descriptorChoice;
	}
	public int getNumOfClusters() {
		return numOfClusters;
	}
	public void setNumOfClusters(int numOfClusters) {
		this.numOfClusters = numOfClusters;
	}
	public void setSampleLimit(int sampleLimit) {
		SampleLimit = sampleLimit;
	}
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
}
