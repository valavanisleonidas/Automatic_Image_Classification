package ImageRepresentationModels.Mixed;


import ImageRepresentationModels.ExtractFeatures;
import ImageRepresentationModels.BoC.BagOfColors;
import ImageRepresentationModels.BoVW.BoVW.BoVWModel;
import Utils.Utilities;

public class MixedBovwBoc implements ExtractFeatures{

	 private double weight1,weight2;
	 private BoVWModel bovw = new BoVWModel();
	 private BagOfColors boc = new BagOfColors();

	 //TODO CHECK BOC
public double[] extractImage(String imagePath,double[] imageFeature) throws Exception {

		System.out.println("mixed :"+imagePath);
		System.out.println("early Fusion");
		
	   
	 	//extract features of image
		double [] BoVW= bovw.extractImage(imagePath,null);	
	    double[] BagOfColors = boc.extractImage(imagePath,null);
	    
	    
	    //give weight to arrays 
	    BoVW = Utilities.weightModels(BoVW, weight1);
	    BagOfColors = Utilities.weightModels(BagOfColors,weight2);
	    //concatenate arrays
	    double join[] = Utilities.concat(BoVW, BagOfColors);    
	    return join;
	}

public 	BoVWModel getBovw(){
	return bovw;
}
public BagOfColors getBoc(){
	return boc;
}

public void setBovw(BoVWModel bovw) {
	this.bovw = bovw;
}
public void setBoc(BagOfColors boc) {
	this.boc = boc;
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
