package ImageRepresentationModels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;
import Utils.Image.ImageFilter;
import GUI.ProgressDialog;
import ImageRepresentationModels.BoC.BagOfColors;
import ImageRepresentationModels.BoVW.BoVW.BoVWModel;
import ImageRepresentationModels.GBoC.GBocDescriptor;
import ImageRepresentationModels.Mixed.MixedBovwBoc;
import ImageRepresentationModels.Mixed.MixedGbocBovw;
import ImageRepresentationModels.Mixed.FusionTypes.LateFusion;

//factory for extracting features using a model
public class IRMFactory implements Runnable {

	private BufferedWriter descriptorTrainWriter1,descriptorTrainWriter2, descriptorTestWriter1,descriptorTestWriter2;
	private BufferedReader graphBocReaderTest, graphBocReaderTrain;
	private int threadCounter;
	private String dBSourcePath,projectPath,model,descriptorChoice,KMeansChoice,FusionType,featuresFormat;
	private Shell shell;
	private final ProgressDialog progressBar = new ProgressDialog();
	final boolean writeToFile=true;
	String[] testFiles,trainFiles;
	//initialize objects
	private ExtractFeatures extractFeatures;
	private BoVWModel bovw = new BoVWModel();
	private BagOfColors boc = new BagOfColors();
	private GBocDescriptor gboc = new GBocDescriptor();
	private MixedBovwBoc mixedBovwBoc = new MixedBovwBoc();
	private MixedGbocBovw mixedgBocBovw = new MixedGbocBovw();
	//categories with numeric representation for each one
	private Map<String,Integer> categories;
	 
	@Override
	public void run() {
		try {
			if( startFactory() )
				Utilities.showMessage("Model : "+model+" , Descriptor Choice : "+descriptorChoice+" , KMeans : "+KMeansChoice+" completed succesfully", shell, false);
			
		}catch (Exception e) {
			Utilities.showMessage("Model "+model+" , Descriptor Choice : "+descriptorChoice+" , KMeans : "+KMeansChoice+" failed", shell, true);
			e.printStackTrace();
		}
	}

	public boolean startFactory() throws Exception{
		
		descriptorTrainWriter1=null;
		descriptorTestWriter1=null;
		descriptorTrainWriter2=null;
		descriptorTestWriter2=null;
		threadCounter=1;
		
		//counter for progressBar thread
		File dir = new File(dBSourcePath);
		
	   	//[0] : train  , [1] test
   		String[] fileNames =Utilities.getTrainTest(dir);
   		if ( !Utilities.collectionFilesAreCorrect(fileNames,shell) ) return false;
   	
   		categories = Utilities.getSubFoldersOfFolder(fileNames[1]);
   		
   		testFiles= ImageFilter.getDirFiles(fileNames[1],Integer.MAX_VALUE,true );
   		trainFiles= ImageFilter.getDirFiles(fileNames[0],Integer.MAX_VALUE,true );
   	  	
   		
   		//create thread
   		Thread progress = new Thread(progressBar);
   		progress.start();
   		Thread.sleep(100);
   		progressBar.setWholeImageSize(testFiles.length+trainFiles.length);
      	 
   		if(model.equals("Bag of Visual Words")){
   			extractBoVW(false);
			
   		}else if(model.equals("Bag of Colors")) {
   			extractBoC(false);

   		}else if(model.equals("Graph BoC")){
   			runGBoc();
   		}else if(model.equals("Mixed (Bovw-Boc)")){
   			
   			if(FusionType.equals("Late Fusion")){
   				extractMixedBovwBocLate();
   	   		}
   			else{
   				extractMixedBovwBocEarly();
	   		}
   		}else if(model.equals("Mixed (GraphBoc-Bovw)")){
   	   		if(FusionType.equals("Late Fusion")){
   	   			extractMixedGBoCBovwLate();
   	   		}
   	   		else{
   	   			if(!extractMixedGBoCBovwEarly())
	   	   			return false;		
   	   		}
   		}

   		extractFeatures=null;
   		trainFiles=null;
   		testFiles=null;

   		try{

   			progressBar.terminate();
  	   		progress.interrupt();
  	   		progress.stop();
  			
  			//if gboc is executed writer is not required so it would throw an exception as null
  			if( (descriptorTestWriter1 == null) && (descriptorTrainWriter1 == null) ) return false;
  	  		
  			//if gboc is executed writer is not required so it would throw an exception as null
  			if(featuresFormat.equals("Both") && (descriptorTestWriter2 == null) && (descriptorTrainWriter2 == null) ) return false;
  			
  			// if is mixed and late fusion we want the files before the end so descriptors will be closed already
  			if ( !(model.contains("Mixed")  && FusionType.equals("Late Fusion") )  ){
  				descriptorTestWriter1.close();
	  			descriptorTrainWriter1.close();
	  			if(featuresFormat.equals("Both") ){
	  				descriptorTestWriter2.close();
		  			descriptorTrainWriter2.close();
	  			}
	  			
  			}

   		}catch(Exception e){
   	   			e.printStackTrace();
   	   			return false;
   		}
  		return true;
   	
  	}
	
	public void extractImages(String[] fileSet,BufferedWriter descriptorWriter1,BufferedWriter descriptorWriter2,BufferedReader reader) throws Exception{
		
		for(int i=0;i<fileSet.length;i++){
			progressBar.updateImageProcessed(threadCounter++);  
			double[] imgVocVector;
			String imagePath = fileSet[i].toString();
			
			double [] graphBocFeature = reader == null ? null : Utilities.readIndexFeatureFromFile(reader);
			
			imgVocVector = extractFeatures.extractImage(imagePath,graphBocFeature);
			
			String category=Utilities.GetParentFolder(imagePath).split("\\.")[1];
			category = categories.get(category).toString();
			
			//write to file 
			if(featuresFormat.equals("LibSVM Format"))
				Utilities.writeHistogram(descriptorWriter1,category,imgVocVector,true);
			else if(featuresFormat.equals("Non-LibSVM Format"))
				Utilities.writeHistogram(descriptorWriter1,category,imgVocVector,false);
			//both
			else{
				Utilities.writeHistogram(descriptorWriter1,category,imgVocVector,false);
				Utilities.writeHistogram(descriptorWriter2,category,imgVocVector,true);
			}
			
		}
		fileSet=null;
		
	}
	
	public void extractBoVW(boolean closeDescriptors) throws Exception{
		String libsvm = "";
		if(featuresFormat.equals("LibSVM Format")){
			libsvm = "LibSVM_";
		}
		
		File train = new File(projectPath+"\\"+libsvm+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"
				+getBovw().getKMeansChoice()+"-train-VisualVocabulary.txt");
		
		File test = new File(projectPath+"\\"+libsvm+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"
				  +getBovw().getKMeansChoice()+"-test-VisualVocabulary.txt");
		
		if (featuresFormat.equals("LibSVM Format") && train.exists() && test.exists()){
			Utilities.showMessage("Bovw "+libsvm+" "+bovw.getDescriptorChoice()+" files already exists!", shell, false);
			
			return;
		}
		//open files for writing
		descriptorTrainWriter1 = Utilities.openFilesForWriting(train.getAbsolutePath(),shell);
		descriptorTestWriter1 = Utilities.openFilesForWriting(test.getAbsolutePath(),shell);
		
		
		//if Both features formats
		if(featuresFormat.equals("Both")){
			File train_LibSVM = new File(projectPath+"\\LibSVM_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"
					+getBovw().getKMeansChoice()+"-train-VisualVocabulary.txt");
			
			File test_LibSVM = new File(projectPath+"\\LibSVM_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"
					  +getBovw().getKMeansChoice()+"-test-VisualVocabulary.txt");
			
			if (train_LibSVM.exists() && test_LibSVM.exists()){
				Utilities.showMessage("Bovw libsvm "+bovw.getDescriptorChoice()+" files already exists!", shell, false);
				return;
			}
			//open files for writing
			descriptorTrainWriter2 = Utilities.openFilesForWriting(train_LibSVM.getAbsolutePath(),shell);
			descriptorTestWriter2 = Utilities.openFilesForWriting(test_LibSVM.getAbsolutePath(),shell);
			
			
		}
		
		//set object for interface
		extractFeatures=bovw;
		
		//extract feautures using bovw
	    extractImages(testFiles,descriptorTestWriter1,descriptorTestWriter2,null);
	    extractImages(trainFiles,descriptorTrainWriter1,descriptorTrainWriter2,null);	
	    
	    if(closeDescriptors){
	    	descriptorTestWriter1.close();
		    descriptorTrainWriter1.close();
		    //if Both features formats
			if(featuresFormat.equals("Both")){
				descriptorTestWriter2.close();
			    descriptorTrainWriter2.close();
				
			}
		    
	    }
	    
	}
	
	public void extractBoC(boolean closeDescriptors)throws Exception{
		String libsvm = "";
		if(featuresFormat.equals("LibSVM Format")){
			libsvm = "LibSVM_";
		}
		
		String kmeansChoice = KMeansChoice.split("-").length < 2 ? 
							  KMeansChoice.split("-")[0] :  
							  KMeansChoice.split("-")[1]; 
							
		File train = new File(projectPath+"\\BagOfColors-"+libsvm+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())
		+"-"+kmeansChoice+"-train-VisualVocabulary.txt");
		
		File test = new File(projectPath+"\\BagOfColors-"+libsvm+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())
		+"-"+kmeansChoice+"-test-VisualVocabulary.txt");
		
		if (train.exists() && test.exists()){
			Utilities.showMessage("Boc  "+libsvm+" files already exists!", shell, false);
			return;
		}
		
		
		//open files for writing
		descriptorTrainWriter1=	Utilities.openFilesForWriting(train.getAbsolutePath(), shell);
		descriptorTestWriter1 = Utilities.openFilesForWriting(test.getAbsolutePath(), shell);
		
		//if Both features formats
		if(featuresFormat.equals("Both")){
			File train_libsvm = new File(projectPath+"\\LibSVM_BagOfColors-"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())
			+"-"+kmeansChoice+"-train-VisualVocabulary.txt");
			
			File test_libsvm = new File(projectPath+"\\LibSVM_BagOfColors-"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())
			+"-"+kmeansChoice+"-test-VisualVocabulary.txt");
			
			if (train_libsvm.exists() && test_libsvm.exists()){
				Utilities.showMessage("Boc libsvm files already exists!", shell, false);
				return;
			}
			//open files for writing
			descriptorTrainWriter2 = Utilities.openFilesForWriting(train_libsvm.getAbsolutePath(),shell);
			descriptorTestWriter2 = Utilities.openFilesForWriting(test_libsvm.getAbsolutePath(),shell);
			
			
		}
		
			
		//set object for interface
		extractFeatures=boc;
		
		long startTime = System.currentTimeMillis();
		//extract feautures using boc
	    extractImages(testFiles,descriptorTestWriter1,descriptorTestWriter2,null);
	    extractImages(trainFiles,descriptorTrainWriter1,descriptorTrainWriter2,null);
	    long endTime = System.currentTimeMillis();
        System.out.println("It took " + (endTime - startTime) + " milliseconds");
	    
	    if(closeDescriptors){
		    descriptorTestWriter1.close();
			descriptorTrainWriter1.close();
			 //if Both features formats
			if(featuresFormat.equals("Both")){
				descriptorTestWriter2.close();
			    descriptorTrainWriter2.close();
				
			}
	    }
	}
		
	public void runGBoc() throws Exception{
		String libsvm = "";
		if(featuresFormat.equals("LibSVM Format")){
			libsvm = "LibSVM_";
		}
		String gbocFeaturesTrainFile = projectPath+"\\train_"+libsvm+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"
	   			+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt";
		
		String gbocFeaturesTestFile =projectPath+"\\test_"+libsvm+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"
		   			+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt";
		
		if(new File(gbocFeaturesTestFile).exists() && new File(gbocFeaturesTrainFile).exists()){
			Utilities.showMessage("GBoC files already exists!", shell, false);
			return;
		}
 	    
		gboc.setFeaturesFormat(featuresFormat);
		//if not create feature vectors using boc
		gboc.createFeatureVectors();
	}
	
	public void extractMixedBovwBocEarly() throws Exception{
		//open files for writing
		descriptorTrainWriter1 = Utilities.openFilesForWriting(projectPath+"\\EarlyFusionmixedBovwBoC-"+mixedBovwBoc.getBovw().getDescriptorChoice()+"-"
				+mixedBovwBoc.getBovw().getClusterNum()+"-"+mixedBovwBoc.getBoc().getNoOfColors()+"-"+mixedBovwBoc.getBoc().getColorSpace()+"-"+mixedBovwBoc.getBovw().getKMeansChoice()
				+"-"+getMixedBovwBoc().getWeight1()+"-train-VisualVocabulary.txt",shell);
		descriptorTestWriter1 = Utilities.openFilesForWriting(projectPath+"\\EarlyFusionmixedBovwBoC-"+mixedBovwBoc.getBovw().getDescriptorChoice()+"-"
				 +mixedBovwBoc.getBovw().getClusterNum()+"-"+mixedBovwBoc.getBoc().getNoOfColors()+"-"+mixedBovwBoc.getBoc().getColorSpace()+"-"+mixedBovwBoc.getBovw().getKMeansChoice()
				 +"-"+getMixedBovwBoc().getWeight1()+"-test-VisualVocabulary.txt",shell);
		
		//if Both features formats
		if(featuresFormat.equals("Both")){
			//open files for writing
			descriptorTrainWriter2 = Utilities.openFilesForWriting(projectPath+"\\LibSVM_EarlyFusionmixedBovwBoC-"+mixedBovwBoc.getBovw().getDescriptorChoice()+"-"
					+mixedBovwBoc.getBovw().getClusterNum()+"-"+mixedBovwBoc.getBoc().getNoOfColors()+"-"+mixedBovwBoc.getBoc().getColorSpace()+"-"+mixedBovwBoc.getBovw().getKMeansChoice()
					+"-"+getMixedBovwBoc().getWeight1()+"-train-VisualVocabulary.txt",shell);
			descriptorTestWriter2 = Utilities.openFilesForWriting(projectPath+"\\LibSVM_EarlyFusionmixedBovwBoC-"+mixedBovwBoc.getBovw().getDescriptorChoice()+"-"
					 +mixedBovwBoc.getBovw().getClusterNum()+"-"+mixedBovwBoc.getBoc().getNoOfColors()+"-"+mixedBovwBoc.getBoc().getColorSpace()+"-"+mixedBovwBoc.getBovw().getKMeansChoice()
					 +"-"+getMixedBovwBoc().getWeight1()+"-test-VisualVocabulary.txt",shell);
		
		}
		
		
		
		//set object for interface
		extractFeatures=mixedBovwBoc;
		
		//extract feautures using bovw-boc
		extractImages(testFiles,descriptorTestWriter1,descriptorTestWriter2,null);
		extractImages(trainFiles,descriptorTrainWriter1,descriptorTrainWriter2,null);
	}
	
	public void extractMixedBovwBocLate() throws Exception{
			extractBoVW(true);
			
			threadCounter=1;
			extractBoC(true);

			LateFusion latefusion = new LateFusion();

			String args[] = new String [11];
  			// bovw train test
  			args[0] =  projectPath+"\\"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice()+"-train-VisualVocabulary.txt";
  			args[1] =  projectPath+"\\"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice()+"-test-VisualVocabulary.txt";
  			// boc train test
  			args[2] =  projectPath+"\\BagOfColors-"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())+"-"+KMeansChoice.split("-")[1]+"-train-VisualVocabulary.txt";
			args[3] =  projectPath+"\\BagOfColors-"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())+"-"+KMeansChoice.split("-")[1]+"-test-VisualVocabulary.txt";
			//models
			args[4] =  projectPath+"\\ModelBoVW_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice();
  			args[5] =  projectPath+"\\ModelBoC_"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())+"-"+KMeansChoice.split("-")[1];
  			//result files
  			args[6] =  projectPath+"\\ResultsBoVW_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice();
			args[7] =  projectPath+"\\ResultsBoC_"+getBoc().getNoOfColors()+"-"+Utilities.findColorSpace(getBoc().getColorSpace())+"-"+KMeansChoice.split("-")[1];
			
  			args[8] =  projectPath;
  			args[9] =  dBSourcePath;
  			//final result file
  			args[10] = projectPath+"\\ResultsLateFusionMixedBovwBoC-"+mixedBovwBoc.getBovw().getDescriptorChoice()+"-"
				+mixedBovwBoc.getBovw().getClusterNum()+"-"+mixedBovwBoc.getBoc().getNoOfColors()+"-"+mixedBovwBoc.getBoc().getColorSpace()+"-"+mixedBovwBoc.getBovw().getKMeansChoice()
				+"-"+getMixedBovwBoc().getWeight1();
			
			
			latefusion.performLateFusion(args,getMixedBovwBoc().getWeight1(),shell);
	}
	
	public boolean extractMixedGBoCBovwEarly() throws Exception{
		//run gboc
  	   		runGBoc();
  	   		
  	   		//open files for writing
			descriptorTrainWriter1 = Utilities.openFilesForWriting(projectPath+"\\EarlyFusionmixedGraphBovw-"+mixedgBocBovw.getBovw().getDescriptorChoice()
					+"-"+mixedgBocBovw.getBovw().getClusterNum()+"-"+getGboc().getNumberOfColors()+"-"+getGboc().getColorSpace()+"-"+mixedgBocBovw.getBovw().getKMeansChoice()
					+"-"+getMixedgBocBovw().getWeight1()+"-train-VisualVocabulary.txt", shell);
			
			descriptorTestWriter1 = Utilities.openFilesForWriting(projectPath+"\\EarlyFusionmixedGraphBovw-"+mixedgBocBovw.getBovw().getDescriptorChoice()
					+"-"+mixedgBocBovw.getBovw().getClusterNum()+"-"+getGboc().getNumberOfColors()+"-"+getGboc().getColorSpace()+"-"+mixedgBocBovw.getBovw().getKMeansChoice()
					+"-"+getMixedgBocBovw().getWeight1()+"-test-VisualVocabulary.txt",shell);
			
			
			//if Both features formats
			if(featuresFormat.equals("Both")){
				//open files for writing
				descriptorTrainWriter2 = Utilities.openFilesForWriting(projectPath+"\\LibSVM_EarlyFusionmixedGraphBovw-"+mixedgBocBovw.getBovw().getDescriptorChoice()
						+"-"+mixedgBocBovw.getBovw().getClusterNum()+"-"+getGboc().getNumberOfColors()+"-"+getGboc().getColorSpace()+"-"+mixedgBocBovw.getBovw().getKMeansChoice()
						+"-"+getMixedgBocBovw().getWeight1()+"-train-VisualVocabulary.txt", shell);
				
				descriptorTestWriter2 = Utilities.openFilesForWriting(projectPath+"\\LibSVM_EarlyFusionmixedGraphBovw-"+mixedgBocBovw.getBovw().getDescriptorChoice()
						+"-"+mixedgBocBovw.getBovw().getClusterNum()+"-"+getGboc().getNumberOfColors()+"-"+getGboc().getColorSpace()+"-"+mixedgBocBovw.getBovw().getKMeansChoice()
						+"-"+getMixedgBocBovw().getWeight1()+"-test-VisualVocabulary.txt",shell);
				
			}
			
			//set object for interface
			extractFeatures=mixedgBocBovw;
			
			
			graphBocReaderTest = Utilities.openFilesForReading(projectPath+"\\test_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"
			+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt", shell);
			
			
			//extract feautures using gboc-bovw
			extractImages(testFiles,descriptorTestWriter1,descriptorTestWriter2,graphBocReaderTest);
			graphBocReaderTrain = Utilities.openFilesForReading(projectPath+"\\train_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"
		   			+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt", shell);
			
			
			//extract feautures using gboc-bovw
			extractImages(trainFiles,descriptorTrainWriter1,descriptorTrainWriter2,graphBocReaderTrain);
		
			
		 try {
			graphBocReaderTest.close();
			graphBocReaderTrain.close();
			}catch (FileNotFoundException e) {
			Utilities.showMessage("Could not close File!", shell, true);
 			return false;
	   }
		 return true;
	}
	
	public void extractMixedGBoCBovwLate() throws Exception{
	
		Thread Bovw = new Thread (new Runnable(){
  	    		
  	    		public void run(){
   	   			try {
					extractBoVW(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  	    		}
  	    		
  	    	});
  	    	Bovw.start();
  	    	Thread gboc = new Thread (new Runnable(){
	    		
	    		public void run(){
	   			try {
	   				runGBoc();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		}
	    		
	    	});
  	    	gboc.start();
  	    	
  	    	Bovw.join();
  	    	gboc.join();	
	   	   			
   		LateFusion latefusion = new LateFusion();
  		
  			String args[] = new String [11];
			//GBoC train test
  			args[0] =  projectPath+"\\train_libSVM_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt";
			args[1] =  projectPath+"\\test_libSVM_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors() +".txt";
			//Bovw train test
			args[2] =  projectPath+"\\"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice()+"-train-VisualVocabulary.txt";
  			args[3] =  projectPath+"\\"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice()+"-test-VisualVocabulary.txt";
			//models
  			args[4] =  projectPath+"\\ModelGBoC_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors();
			args[5] =  projectPath+"\\ModelBoVW_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice();
			
  			args[6] =  projectPath+"\\ResultsGBoC_"+ getGboc().getNumberOfBlocks() +"x"+ getGboc().getNumberOfBlocks()+"_"+ getGboc().getColorSpace() +"_"+  getGboc().getNumberOfColors();
			args[7] =  projectPath+"\\ResultsBoVW_"+getBovw().getDescriptorChoice()+"-"+getBovw().getClusterNum()+"-"+getBovw().getKMeansChoice();
			
			args[8] =  projectPath;
			args[9] =  dBSourcePath;
			args[10] = projectPath+"\\ResultsLateFusionMixedGraphBovw-"+mixedgBocBovw.getBovw().getDescriptorChoice()
					+"-"+mixedgBocBovw.getBovw().getClusterNum()+"-"+getGboc().getNumberOfColors()+"-"+getGboc().getColorSpace()+"-"+mixedgBocBovw.getBovw().getKMeansChoice()
					+"-"+getMixedgBocBovw().getWeight1();
  			
  			
  			
  			latefusion.performLateFusion(args,getMixedgBocBovw().getWeight1() , shell);
  			
		
	}
	
	public void setDBSourcePath(String dBSourcePath) {
		this.dBSourcePath = dBSourcePath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void setDescriptorChoice(String descriptorChoice) {
		this.descriptorChoice = descriptorChoice;
	}
	public void setKMeansChoice(String kMeansChoice) {
		KMeansChoice = kMeansChoice;
	}
	public BoVWModel getBovw() {
		return bovw;
	}
	public void setBovw(BoVWModel bovw) {
		this.bovw = bovw;
	}
	public BagOfColors getBoc() {
		return boc;
	}
	public void setBoc(BagOfColors boc) {
		this.boc = boc;
	}
	public GBocDescriptor getGboc() {
		return gboc;
	}
	public void setGboc(GBocDescriptor gboc) {
		this.gboc = gboc;
	}
	public MixedBovwBoc getMixedBovwBoc() {
		return mixedBovwBoc;
	}
	public void setMixedBovwBoc(MixedBovwBoc mixedBovwBoc) {
		this.mixedBovwBoc = mixedBovwBoc;
	}
	public MixedGbocBovw getMixedgBocBovw() {
		return mixedgBocBovw;
	}
	public void setMixedgBocBovw(MixedGbocBovw mixedgBocBovw) {
		this.mixedgBocBovw = mixedgBocBovw;
	}

	public String getFusionType() {
		return FusionType;
	}

	public void setFusionType(String fusionType) {
		FusionType = fusionType;
	}

	public String getFeaturesFormat() {
		return featuresFormat;
	}

	public void setFeaturesFormat(String featuresFormat) {
		this.featuresFormat = featuresFormat;
	}
	  	
}
