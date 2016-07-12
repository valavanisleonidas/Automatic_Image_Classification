package Clustering.KMeans;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

public interface kmeansInterface {

  public void StartKmeans(String filePath, String ProjectPath,int ClustersNum
		  ,int SampleSize,String descriptorChoice, Shell shell) throws Exception;

	public List<double[]> getCenters();
}
