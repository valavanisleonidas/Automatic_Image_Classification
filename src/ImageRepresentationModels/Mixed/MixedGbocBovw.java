package ImageRepresentationModels.Mixed;


import Utils.Utilities;
import ImageRepresentationModels.ExtractFeatures;
import ImageRepresentationModels.BoVW.BoVW.BoVWModel;

public class MixedGbocBovw implements ExtractFeatures{

	private double weight1,weight2;
	private BoVWModel bovw = new BoVWModel();
	
	
public double[] extractImage(String imagePath, double[] graphBocImageFeatures) throws Exception {

	System.out.println("mixed gBoc-bovw:"+imagePath);
	System.out.println("early Fusion");

	
	double [] BoVW= getBovw().extractImage(imagePath,null);	
	//give weight to arrays 
    graphBocImageFeatures = Utilities.weightModels(graphBocImageFeatures,weight1);
    BoVW = Utilities.weightModels(BoVW,weight2 );
    System.out.println(graphBocImageFeatures.length);
    System.out.println(BoVW.length);
    //concatenate arrays
    double join[] = Utilities.concat(graphBocImageFeatures, BoVW);
    System.out.println(join.length);
   
    return join;
}

public 	BoVWModel getBovw(){
	return bovw;
}
public void setBovw(BoVWModel bovw) {
	this.bovw = bovw;
}
public double getWeight1() {
	return weight1;
}
public void setWeight1(double weight1) {
	if( weight1 == 1 || weight1 == 0 ){
		this.weight1 = 1; 
		this.weight2 = 1;
	}
	else{
		this.weight1 = weight1;
		this.weight2 = 1 - weight1;
	}
	
	this.weight1 = weight1;
}
public double getWeight2() {
	return weight2;
}
public void setWeight2(double weight2) {
	this.weight2 = weight2;
}
}
