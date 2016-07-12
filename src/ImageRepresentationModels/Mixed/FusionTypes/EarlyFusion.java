package ImageRepresentationModels.Mixed.FusionTypes;

import Utils.Utilities;

public class EarlyFusion {
	
	private double[] earlyFusion(double[] BoVW, double[] BagOfColors, double weight){
		
		
		//give weight to arrays 
	    BoVW = Utilities.weightModels(BoVW, weight);
	    BagOfColors = Utilities.weightModels(BagOfColors,1-weight);
	    //concatenate arrays
	    double join[] = Utilities.concat(BoVW, BagOfColors);
		return join;
		
	}
	
}
