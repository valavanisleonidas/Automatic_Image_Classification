package ImageRepresentationModels.Mixed.FusionTypes;

import Classification.TuningSVM;
import Classification.TestImages;
import Classification.TrainClassifier;
import Utils.Statistics;
import Utils.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class LateFusion {
	private static TestImages testModel1;
	private static TestImages testModel2;
	
	private static List<Double> FinalProbabilities;
	private static List<Integer> FinalCategories;

	public static void main (String[] args) throws Exception{
		
		String Type = "exhaustive";  // exhaustive  , simpleWeight
		
		LateFusion latefusion = new LateFusion();
		
		String projectPath = "C:\\Users\\Leonidas\\workspace\\BachelorThesis\\Clef2";
		String dBSourcePath = "C:\\Users\\Leonidas\\Desktop\\clef2016\\databases\\Clef2013\\Compoundless";
		String args1[] = new String [11];
		// gboc train test
		args1[0] =  projectPath+"\\train_libSVM_2x2_CIELab_128.txt";
		args1[1] =  projectPath+"\\test_libSVM_2x2_CIELab_128.txt";
		// boc train test
		args1[2] =  projectPath+"\\phow_1000Train.txt";
		args1[3] =  projectPath+"\\phow_1000Test.txt";
		//models
		args1[4] =  projectPath+"\\modelGboc";
		args1[5] =  projectPath+"\\ModelBovw";
		//result files
		args1[6] =  projectPath+"\\ResultsGboc";
		args1[7] =  projectPath+"\\ResultsBovw";
	
		args1[8] =  projectPath;
		args1[9] =  dBSourcePath;
		//final result file
		args1[10] = projectPath+"\\ResultsLateFusion";
			
			if (Type.equals("exhaustive")){
				latefusion.performExhaustiveLateFusion(args1);
			}
			else{
				double weight = 0.5;
				latefusion.performLateFusion(args1,weight,null);
			}
	}
	
	private void performExhaustiveLateFusion(String[] args1) throws Exception{
		LateFusion latefusion = new LateFusion();

		double BestAccuracy=-1;
		double BestWeight=-1;
		
		//array with weights from 0 to 100 with step 0.1
		double[] weights = new double[101];
		for(int i=0;i<=100;i++)
			weights[i] = (double)i/100;
		
		
		
		for (int i = 0;i<weights.length;i++){
			System.out.println("weight :" +weights[i] );
			double acc = latefusion.performLateFusion(args1,weights[i],null);
			if (acc >= BestAccuracy){
				BestAccuracy =acc;
				BestWeight= weights[i];
				System.out.println("weight :" +weights[i]+" max with acc "+ BestAccuracy);
			}
			
		}
		System.out.println("Best found with weight :" +BestWeight+" , acc "+ BestAccuracy);
		
	}
	
	public double performLateFusion(String[] args,double weight,Shell shell) throws Exception{
		
		String trainPathModel1 = args[0] ,
				   testPathModel1 = args[1],
			       trainPathModel2 = args[2],
				   testPathModel2 = args[3],
				   ModelPath1 = args[4],
				   ModelPath2 = args[5],
				   ResultPath1 = args[6],
				   ResultPath2 = args[7],
				   projectPath= args[8],
				   DBSourcePath= args[9],
				   resultsFilePath= args[10],
				   command1 = null,
				   command2 = null;	
			
			double accuracy;
			double weightModel1,weightModel2 ;
			
			if( weight == 1 || weight == 0  ){
				weightModel1 = 1; 
				weightModel2 = 1;
			}
			else{
				weightModel1 = weight; 
				weightModel2 = 1 - weight;
			}

			
			// perfom tuning for models
//			String[] commands = FindBestParametersForModels(trainPathModel1,trainPathModel2,command1,command2,shell);
//			command1 = commands[0];
//			command2 = commands[1];
			
			
			command1= "-c 16 -g 16 -b 1 -q";
			command2 = "-c 4.0 -g 0.5 -b 1 -q";
	 		//if train files exist dont train
	 		if( !( new File(ModelPath1).exists() &&  new File(ModelPath2).exists() ) ){
		 		System.out.println("Training");
	 			trainModels(trainPathModel1, trainPathModel2, ModelPath1, ModelPath2, command1, command2, shell);
	 		}
	 		if( !( new File(ResultPath1).exists() &&  new File(ResultPath2).exists() ) ){
		 		System.out.println("Testing");
		 		testModels(testPathModel1, testPathModel2, ModelPath1, ModelPath2, ResultPath1, ResultPath2, shell);
	 		}
//	 		System.out.println("--------model 1 probs -----------");
//			for(int i =0; i<testModel1.getProbabilities().size();i++){
//				for(int j =0; j<testModel1.getProbabilities().get(i).length;j++){
//					System.out.print(testModel1.getProbabilities().get(i)[j]+" ");
//				}
//				System.out.println();
//			}
//	 		System.out.println("--------model 2 probs -----------");
//
//			for(int i =0; i<testModel2.getProbabilities().size();i++){
//				for(int j =0; j<testModel2.getProbabilities().get(i).length;j++){
//					System.out.print(testModel2.getProbabilities().get(i)[j]+" ");
//				}
//				System.out.println();
//			}
	 		
			//perform late fusion with probabilities
	 		lateFusion(weightModel1,weightModel2);
			
			writeResultsToFile(projectPath,resultsFilePath,shell);
			
			accuracy = writeStatisticsToFile(projectPath,DBSourcePath,resultsFilePath,shell);
			
//			for(int i =0 ; i<FinalProbabilities.size();i++){
//			System.out.println("image : "+i+" with proba : "+FinalProbabilities.get(i) +" category " + FinalCategories.get(i) );
//		}
			
			return accuracy;	
	}
	
	private String[] FindBestParametersForModels(String trainPathModel1,String trainPathModel2,
			String command1, String command2, Shell shlClassification) throws InterruptedException{
		
		// First Model
		TuningSVM bestParameters1=new TuningSVM(trainPathModel1,shlClassification);
		Thread bestParametersThread1 = new Thread(bestParameters1);
		bestParametersThread1.start();
 		
		//second Model
		TuningSVM bestParameters2=new TuningSVM(trainPathModel2,shlClassification);
		Thread bestParametersThread2 = new Thread(bestParameters2);
		bestParametersThread2.start();
			
		bestParametersThread1.join();
		bestParametersThread2.join();
		command1 = "-c "+(double)bestParameters1.getBestc()+" -g "+(double)bestParameters1.getBestg()+" -b 1 -q";
		command2 = "-c "+(double)bestParameters2.getBestc()+" -g "+(double)bestParameters2.getBestg()+" -b 1 -q";	

		System.out.println("command1:" +command1 +" acc: "+(double)bestParameters1.getBestcv());
		System.out.println("command2:" +command2 +" acc: "+(double)bestParameters2.getBestcv());

		return new String[] { command1, command2};
		
	}
	
	private void trainModels(String trainDataPath1,String trainDataPath2,String saveModelPathTrain1,String saveModelPathTrain2,
			String command1,String command2,Shell shlClassification) throws InterruptedException{
		
		TrainClassifier trainModel1=new TrainClassifier(command1,trainDataPath1, saveModelPathTrain1,shlClassification);
		Thread threadModel1 = new Thread(trainModel1);
		threadModel1.start();
		
		TrainClassifier trainModel2=new TrainClassifier(command2,trainDataPath2, saveModelPathTrain2,shlClassification);
		Thread threadModel2 = new Thread(trainModel2);
		threadModel2.start();
		
		threadModel1.join();
		threadModel2.join();
		
	}
	
	private void testModels(String testPathModel1,String testPathModel2,String ModelPath1,String ModelPath2,
			String resultFilePath1,String resultFilePath2,Shell shlClassification) throws InterruptedException{
		
		//for probabilities
		String command = "-b 1";
		//test images given with command
		testModel1=new TestImages(testPathModel1,ModelPath1,resultFilePath1,command,shlClassification);
		Thread threadingModel1 = new Thread(testModel1);
		threadingModel1.start();
		
		//test images given with command
		testModel2=new TestImages(testPathModel2,ModelPath2,resultFilePath2,command,shlClassification);
		Thread threadingModel2 = new Thread(testModel2);
		threadingModel2.start();
		
		threadingModel1.join();
		threadingModel2.join();

	}

	private void lateFusion(double weightModel1,double weightModel2){
		FinalProbabilities = new ArrayList<Double>();
		FinalCategories = new ArrayList<Integer>();
		int imageLength = testModel1.getProbabilities().size();
		int labelsLength = testModel1.getLabels().length;
		
		// for each image
		for(int i =0; i<imageLength;i++){
			double maxProbability = -1;
			int Category=-1;
			double score=-1;
			// for each probability in image
			for(int j =0;j<labelsLength;j++){
				score = weightModel1*testModel1.getProbabilities().get(i)[j] + weightModel2*testModel2.getProbabilities().get(i)[j]; 
			
				if(maxProbability<=score){
					maxProbability = score;
					Category=testModel1.getLabelsIndex(j);
				}
				
			}
			
			FinalProbabilities.add(maxProbability);
			FinalCategories.add(Category);
		}
		
		
	}
	
	private void writeResultsToFile(String projectPath,String resultsPath,Shell shell) throws IOException {
		 BufferedWriter writer = null;
		 File file=null;//to arxeio xml pou tha grapsoume
		
	    try {
		 	//to apothikeuei ston kainourgio fakelo tou project me katalixi to onoma tou project
	    	file = new File (resultsPath);
			
		}//se periptwsh pou den uparxei to arxeio st disko na emfanisw t adistoixo mhnuma
		catch  (NullPointerException e ){
			e.printStackTrace();
 			MessageDialog.openError(shell, "Error","File Not Found!");
		}
	  	try{
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))); // me parametro to f
	  	}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
 			MessageDialog.openError(shell, "Error","Error opening file!");
		}
	    for (int i =0;i<FinalProbabilities.size();i++){
	    	writer.write(FinalCategories.get(i)+" "+FinalProbabilities.get(i)+"\n");
	    }
		  	
		
	  	writer.close();
	}

	private double writeStatisticsToFile(String projectPath,String DBsource,String resultsFile,Shell shell) throws Exception{
		Statistics statistics = new Statistics();
		statistics.createMetrics(projectPath, DBsource, resultsFile,true, shell);
		return statistics.getAccuracy();
	}
	
	
	
}
