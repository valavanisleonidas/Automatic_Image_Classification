package Clustering.KMeans;
import java.util.List;
import net.sf.javaml.core.kdtree.KDTree;


public class KDTreeImplementation {

//create kdTree with given centers
public static KDTree createTree(List<double[]> Centers)
{	
	KDTree tree= new KDTree(Centers.get(0).length);
	for (int i=0;i<Centers.size();i++)
		tree.insert(Centers.get(i),i++);
	
	return tree;
	
}

//returns nearest object of array centroid in tree
public static int SearchTree(double[] centroid,KDTree tree)
{
	Object nearestObject=tree.nearest(centroid);
	return Integer.parseInt(nearestObject.toString());
}

	
}
