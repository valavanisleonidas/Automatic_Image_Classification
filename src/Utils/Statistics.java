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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import Utils.Image.ImageFilter;

/*
 * 
 * Requirements to successfully create statistics for a collection of Images :
 * 
 * 1) The name of the classes MUST be integer (0,1,2 etc ) and not strings ( 3D etc)!
 * 
 * 2) The numeration of the classes MUST start with 0 OR 1!
 * 
 * 
*/

public class Statistics {
	private List<File> testImages = new ArrayList<File>();
	private List<Integer> realCategory = new ArrayList<Integer>();
	private List<Integer> predictedCategory = new ArrayList<Integer>();
	private List<Integer> probabilities = new ArrayList<Integer>();
	private HashMap<Integer,Integer> categories = new HashMap<Integer,Integer>();
	private double[] FP,TP,precision,recall,F1 ;
	private double accuracy,macroF1;
	private Shell shell;
	
	
public void createMetrics(String projectPath, String DBsource,String resultsFile,boolean hasTrueLabels,Shell shell) throws Exception{
	this.shell=shell;

	
	//[0] : train  , [1] test
	String[] filaNames =Utilities.getTrainTest(new File(DBsource));
	if(filaNames[1]==null) return;
	
	readTestImages(filaNames[1],hasTrueLabels);
	readResults(resultsFile);
	
	if(hasTrueLabels){	 
		 extractMetrics();
		 writeStatisticsToFile(projectPath);
	}
}
	
private void readTestImages(String testSet,boolean hasTrueLabels){
		int countClasses=1;
		String[] testFiles= ImageFilter.getDirFiles(testSet,Integer.MAX_VALUE,true );
		for(int i=0;i<testFiles.length;i++){
			testImages.add(new File(testFiles[i]));
			
			if (hasTrueLabels){
				int category=Integer.valueOf(Utilities.GetParentFolder(testFiles[i]).split("\\.")[1]);
				
				if(!categories.containsKey((category)))
					countClasses=1;
				categories.put(category, countClasses++);
				realCategory.add(category);
			}
			
		}
}

private void extractMetrics(){
	 FP = new double[categories.size()+1];
	 TP = new double[categories.size()+1];
	 precision = new double[categories.size()+1];
	 recall = new double[categories.size()+1];
	 F1 = new double[categories.size()+1];
	 
  	 //initialize arrays
  	 for(int i=0;i<categories.size();i++){
  		 FP[i]=0;
  		 TP[i]=0;
  		 recall[i]=0;
  		 precision[i]=0;
  		 F1[i]=0;
  	 }
   	for (int i =0 ;i <testImages.size();i++){
  		if(predictedCategory.get(i)!=realCategory.get(i))
  			FP[predictedCategory.get(i)]++;
  		else
  			TP[predictedCategory.get(i)]++;
 	}
  	for (Entry<Integer, Integer> entry : categories.entrySet())   {
  		Integer category = entry.getKey();
  		Integer sizeOfCategory = entry.getValue();
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
	  	
		for (Entry<Integer, Integer> entry : categories.entrySet()){
	  		Integer category = entry.getKey();
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
	 double[] F1 = new double[categories.size()+1];

	for(int i=0;i<recall.length;i++){	
		if(precision[i] + recall[i] != 0)
			F1[i]= 2 * (recall[i] * precision[i] ) / ( recall[i] + precision[i] ); 
  	}
	return F1;
	
}

private double computeSum(double[] array){
	double sum=0;
	for(int i =0;i<array.length;i++)
		sum +=array[i];
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
					   max=Integer.MIN_VALUE;
					
					   StringTokenizer tokens = new StringTokenizer(line," ");
					   String number =tokens.nextToken();
					   if(Utilities.isNumeric( number ))
					   {
						   //add category
						   predictedCategory.add( (int) Double.parseDouble(number) );
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
public int getPredictedCategory(int index) {
	return predictedCategory.get(index);
}
public int getRealCategory(int index) {
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
