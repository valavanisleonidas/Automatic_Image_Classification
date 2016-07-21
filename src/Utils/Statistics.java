package Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import Utils.Image.ImageFilter;


public class Statistics {
	private List<File> testImages = new ArrayList<File>();
	private List<String> realCategory = new ArrayList<String>();
	private List<String> predictedCategory = new ArrayList<String>();
	private List<Integer> probabilities = new ArrayList<Integer>();
	private Map<String,Integer> categories = new HashMap<String,Integer>();
	private double[] FP,TP,precision,recall,F1 ;
	private double accuracy,macroF1;
	private Shell shell;
	private Map<String,Integer> categories_2_numbers;
	
	
	public class ImageResult{
		public String ImageName,RealCategory,PredictedCategory;
	}
	
	public void createMetrics(String projectPath, String DBsource,String resultsFile,boolean hasTrueLabels,Shell shell) throws Exception{
		this.shell=shell;
	
		
		//[0] : train  , [1] test
		String[] fileNames =Utilities.getTrainTest(new File(DBsource));
		String testSet = fileNames[1];
		if(testSet==null) return;
		
		if(hasTrueLabels){	
			categories_2_numbers = Utilities.getSubFoldersOfFolder(testSet);
			categories = Utilities.getNumberOfImagesPerCategory(testSet);
		}
		
		readTestImages(testSet,hasTrueLabels);
		readResults(resultsFile);
		
		if(hasTrueLabels){	 
			 extractMetrics();
			 writeStatisticsToFile(projectPath);
		}
	}
	
	private void readTestImages(String testSet,boolean hasTrueLabels){
		String[] testFiles= ImageFilter.getDirFiles(testSet,Integer.MAX_VALUE,true );
		for(int i=0;i<testFiles.length;i++){
			testImages.add(new File(testFiles[i]));
			if (hasTrueLabels){
				String category=Utilities.GetParentFolder(testFiles[i]).split("\\.")[1];
				realCategory.add(category);
			}
		}
	}

private void extractMetrics(){
	 FP = new double[categories.size()];
	 TP = new double[categories.size()];
	 precision = new double[categories.size()];
	 recall = new double[categories.size()];
	 F1 = new double[categories.size()];

	 //initialize arrays
	 Arrays.fill(FP,0);
	 Arrays.fill(TP,0);
	 Arrays.fill(recall,0);
	 Arrays.fill(precision,0);
	 Arrays.fill(F1,0);
	
	//calculate True positive and false positive 
	for (int i =0 ;i <testImages.size();i++){
		String _predictedCategory = predictedCategory.get(i);
		String _realCategory = categories_2_numbers.get(realCategory.get(i)).toString();
	  	if(_predictedCategory.equals(_realCategory))
	  		TP[Integer.valueOf(_predictedCategory)]++;
	  	else
	  		FP[Integer.valueOf(_predictedCategory)]++;
 	}
	
  	for (String _category: categories.keySet())   {
  		int category = categories_2_numbers.get(_category);
  		int sizeOfCategory = categories.get(_category);
		recall[category]= TP[category]/sizeOfCategory;

		if( (TP[category] + FP[category])!=0)
			precision[category]=TP[category]/ (TP[category] + FP[category]);
		else
			precision[category]=0;

  	} 
 	F1=computeF1Metric(recall,precision);
  	macroF1= computeSum(F1)/categories.size();
  	accuracy = computeSum(TP)/testImages.size(); 
  	System.out.println("accuracy : "+accuracy);
  	
}

private void writeStatisticsToFile(String projectPath) throws IOException {
	 BufferedWriter statisticsWriter = null;
	 File statisticsFile=null;//to arxeio xml pou tha grapsoume
	
	    try {
		 	//to apothikeuei ston kainourgio fakelo tou project me katalixi to onoma tou project
	    	statisticsFile = new File (projectPath+"\\statistics.txt");
			
		}//se periptwsh pou den uparxei to arxeio st disko na emfanisw t adistoixo mhnuma
		catch  (NullPointerException e ){
			e.printStackTrace();
 			MessageDialog.openError(shell, "Error","File Not Found!");
		}
	  	try{
		statisticsWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statisticsFile))); // me parametro to f
	  	}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
 			MessageDialog.openError(shell, "Error","Error opening file!");
		}
	  
	  	statisticsWriter.write("The columns below are : Category, Size of Category, True Positive,	False Positive,	Precision, Recall, F1\n");
	  	
		for (Entry<String, Integer> entry : categories.entrySet()){
			String _category = entry.getKey();
	  		int category = categories_2_numbers.get(_category);
	  		Integer sizeOfCategory = entry.getValue();
	  		statisticsWriter.write(category+"	"+" "+sizeOfCategory+"	  "+TP[category] +"	 "+FP[category]+" 	"
	  			+Math.floor(precision[category] * 1e5) / 1e5+"		"+Math.floor(recall[category] * 1e5) / 1e5+"	"
	  					+ "	"+Math.floor(F1[category] * 1e5)/ 1e5+"\n" );
	  		
	  	} 
	  	statisticsWriter.write("macroF1 :"+Math.floor(macroF1 * 1e5)/ 1e5 +"\n");
	  	statisticsWriter.write("Accuracy :"+ Math.floor(accuracy * 1e5)/ 1e5 +"\n");
	
	  	statisticsWriter.close();
}

private double[] computeF1Metric(double [] recall, double[]precision){
	double[] F1 = new double[categories.size()];
	for(int i=0;i<recall.length;i++){	
		if(precision[i] + recall[i] != 0)
			F1[i]= 2 * (recall[i] * precision[i] ) / ( recall[i] + precision[i] ); 
  	}
	return F1;
	
}

private double computeSum(double[] array){
	double sum=0;
	for(double i:array)
	  sum+=i;
	return sum;
}

	private void readResults(String filePath) throws IOException {
		   BufferedReader reader = null;
		   int max=Integer.MIN_VALUE;
		 	try {
		 		reader = new BufferedReader(new FileReader(new File(filePath)));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				
			}
			String line=null;
			   try {
				   while ((line = reader.readLine()) != null){
					   if(line.contains("labels")) 
						   continue;
					   
					   max=Integer.MIN_VALUE;
					
					   StringTokenizer tokens = new StringTokenizer(line," ");
					   String category =  String.valueOf( (int) Double.parseDouble(tokens.nextToken()) ) ;
					   
					   //add category
					   predictedCategory.add( category );
					   if(tokens.countTokens()>=1){
						   while(tokens.hasMoreTokens()){
							  double probability= Math.floor( (Double.parseDouble(tokens.nextToken())) * 1e5) / 1e5 ;
							  probability*=10000;
							  if (max<probability)
								  max=(int)probability;
						   }
						    //add probability
						   	probabilities.add(max);
					   }
					   
				   }
			   }
			   catch(Exception e)  {   e.printStackTrace();   }
			   try  { reader.close();  }
			   catch (FileNotFoundException e)   {  e.printStackTrace();  }
	}

public int getImagesSize() {
	return testImages.size();
}
public File getImage(int index) {
	return testImages.get(index);
}
public String getPredictedCategory(int index) {
	String _category = "Not Found";
	for (String category : categories_2_numbers.keySet()) {
	      if ( categories_2_numbers.get(category) == Integer.valueOf(predictedCategory.get(index)) ) {
	        return category;
	      }
	}
	
	return _category;
}

public String getRealCategory(int index) {
	return realCategory.get(index);
}
public double getAccuracy() {
	return (Math.floor(accuracy * 1e5)/ 1e5)*100;
}
public int getProbability(int index) {
	return probabilities.get(index);
}	
public int getProbabilitySize() {
	return probabilities.size();
}	
public int getCategoriesSize() {
	return categories.size();
}	
public double getPrecisionBinary() {
	return (Math.floor(precision[1] * 1e5)/ 1e5)*100 ;
}
public double getRecallBinary() {
	return  (Math.floor(recall[1] * 1e5)/ 1e5)*100;
}
public int getTruePositiveBinary() {
	return (int)TP[1];
}
public int getTrueNegativeBinary() {
	return (int)TP[0];
}
public int getFalsePositiveBinary() {
	return (int)FP[1];
}
public int getFalseNegativeBinary() {
	return (int)FP[0];
}
public int getPositiveBinary() {
	return (int)(TP[1]+FP[0]);
}
public int getNegativeBinary() {
	return (int)(TP[0]+FP[1]);
}

}
