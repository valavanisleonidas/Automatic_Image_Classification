package ImageRepresentationModels.BoC.LBOC;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;
import java.util.Map;

import Clustering.KMeans.KDTreeImplementation;
import ImageRepresentationModels.BoC.BoCLibrary;
import Utils.Utilities;
import Utils.Image.ColorConversion.ColorSpace;
import Utils.Image.ImageFilter;
import Utils.Image.ImageUtility;
import net.sf.javaml.core.kdtree.KDTree;

public class MainLBOC {
	
	private static ColorSpace cs = ColorSpace.HSV;
	private static int noOfVWords = 1024;
	private static int colors = 50;
	private static int resize = 256;
	private static int patches = 64;
	private static String normalization = "None";
	private static int sampleLimit = 15;
	
	private static String projectFolder = "Clef2011_LBOC\\";
	private static String databaseFolder = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2011";
	private static String featuresFormat = "Both";
	private static String Paletefile = projectFolder+"distinct_palete_"+colors+"_"+noOfVWords+"_"+cs.toString()+".dat";
	private static String outputPaleteName = projectFolder+"distinct_palete_"+colors+"_"+noOfVWords+"_"+cs.toString()+".txt";
	private static String dictionaryFile = projectFolder+"dictionary_"+colors+"_"+noOfVWords+"_"+cs.toString()+".dat";
	private static String outputDictionaryFile = projectFolder+"dictionary_"+colors+"_"+noOfVWords+"_"+cs.toString()+".txt";
	
	public static void main(String[] args) throws Exception{
		createLBOC();
	}

	private static void createLBOC() throws Exception {
		File train = new File(projectFolder+"\\train"+colors+"_"+noOfVWords+"_"+cs.toString()+"-LBOC.txt");
		File trainLibSVM = new File(projectFolder+"\\libsvm_train"+colors+"_"+noOfVWords+"_"+cs.toString()+"-LBOC.txt");
		
		File test = new File(projectFolder+"\\test"+colors+"_"+noOfVWords+"_"+cs.toString()+"-LBOC.txt");
		File testLibSVM = new File(projectFolder+"\\libsvm_test"+colors+"_"+noOfVWords+"_"+cs.toString()+"-LBOC.txt");
		
		BufferedWriter descriptorTrainWriter = Utilities.openFilesForWriting(train.getAbsolutePath(),null);
		BufferedWriter descriptorTrainLibSVMWriter = Utilities.openFilesForWriting(trainLibSVM.getAbsolutePath(),null);
		
		BufferedWriter descriptorTestWriter = Utilities.openFilesForWriting(test.getAbsolutePath(),null);
		BufferedWriter descriptorTestLibSVMWriter = Utilities.openFilesForWriting(testLibSVM.getAbsolutePath(),null);
		
		System.out.println(databaseFolder);
		////////////////////////////////////////////////////////////////////////////////////////////////
		//Create Dictionary
		////////////////////////////////////////////////////////////////////////////////////////////////
		// Get current time
		long start = System.currentTimeMillis();
		
		String[] fileNames =Utilities.getTrainTest(new File(databaseFolder));
   		if ( !Utilities.collectionFilesAreCorrect(fileNames,null) ) return ;
   		
   		Map<String,Integer> categories = Utilities.getSubFoldersOfFolder(fileNames[1]);
		
   		String[] sampleImages= ImageFilter.getDirFiles(fileNames[0],sampleLimit, true);
		int[][] palette;
		if(!new File(Paletefile).exists()){
			System.out.println("Creating Palette");
			palette = BoCLibrary.createPalette(Paletefile, sampleImages, 256, patches, colors, true, cs);
			Utilities.writePalete2TXT(palette, outputPaleteName,null);
		}
		else{
			System.out.println("Loading Palette");
			palette = ArrayIO.ints2DFromFile(Paletefile);
		}
		
		List<double[]> paletteList = Utilities.intArray2DoubleList(palette);
        KDTree tree=KDTreeImplementation.createTree(paletteList);
		
		Clusterer dictionary;
		if(!new File(dictionaryFile).exists()){
			System.out.println("Creating Dictionary");
		    dictionary = BoCLibrary.createDictionary(dictionaryFile, sampleImages, resize, patches, noOfVWords, palette, cs,tree);
		    Utilities.write2TXT(Clusterer.ConvertToDoubleList(dictionary.getClusters()),outputDictionaryFile,null);
		}
		else{
			System.out.println("Loading Dictionary");
			dictionary = new Clusterer(dictionaryFile, true);
		}
		
		for (int i = 0; i < 2; i++) {
			System.out.println(palette[i][0]+" "+palette[i][1]+" "+palette[i][2]+" ");
		}
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < dictionary.getClusters().get(0).getVector().length; j++) {
				System.out.print(dictionary.getClusters().get(i).getVector()[j] + " ");
			}
			System.out.println();
			
			
		}
        
        
        // 	Get elapsed time in milliseconds
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        // Get elapsed time in minutes
        float elapsedTimeMin = elapsedTimeMillis/(60*1000F);
        System.out.println(elapsedTimeMin);
		
        
        ////////////////////////////////////////////////////////////////////////////////////////////////
		//Create Feature Vectors
		////////////////////////////////////////////////////////////////////////////////////////////////
		
        // Get current time
 		start = System.currentTimeMillis();
		String[] testFiles= ImageFilter.getDirFiles(fileNames[1],Integer.MAX_VALUE,true );
   		String[] trainFiles= ImageFilter.getDirFiles(fileNames[0],Integer.MAX_VALUE,true );
		
		for (int i = 0; i < trainFiles.length; i++) {
			if(i%30 == 0){
				System.out.println("processing : "+i+" / "+trainFiles.length);
			}
			
			//get image
		    BufferedImage img = ImageUtility.getImage(trainFiles[i]); 
		    
			double[] imgVocVector = BoCLibrary.getLBoC(img, palette, dictionary, 256, patches, cs,tree);
			if(!normalization.equals("None"))
				imgVocVector=Utilities.normalizeArray(imgVocVector,normalization);
			
			String category=Utilities.GetParentFolder(trainFiles[i]).split("\\.")[1];
			category = categories.get(category).toString();
			
			//write to file 
			if(featuresFormat.equals("LibSVM Format"))
				Utilities.writeHistogram(descriptorTrainLibSVMWriter,category,imgVocVector,true);
			else if(featuresFormat.equals("Non-LibSVM Format"))
				Utilities.writeHistogram(descriptorTrainWriter,category,imgVocVector,false);
			//both
			else{
				Utilities.writeHistogram(descriptorTrainWriter,category,imgVocVector,false);
				Utilities.writeHistogram(descriptorTrainLibSVMWriter,category,imgVocVector,true);
			}
		}
		descriptorTrainLibSVMWriter.close();
		descriptorTrainWriter.close();
		for (int i = 0; i < testFiles.length; i++) {
			if(i%30 == 0){
				System.out.println("processing : "+i+" "+testFiles.length);
			}
			
			//get image
		    BufferedImage img = ImageUtility.getImage(testFiles[i]);   
		    double[] imgVocVector = BoCLibrary.getLBoC(img, palette, dictionary, 256, patches, cs,tree);
		    
		    if(!normalization.equals("None"))
		    	imgVocVector=Utilities.normalizeArray(imgVocVector,normalization);
		    
		    String category=Utilities.GetParentFolder(testFiles[i]).split("\\.")[1];
			category = categories.get(category).toString();
			
			//write to file 
			if(featuresFormat.equals("LibSVM Format"))
				Utilities.writeHistogram(descriptorTestLibSVMWriter,category,imgVocVector,true);
			else if(featuresFormat.equals("Non-LibSVM Format"))
				Utilities.writeHistogram(descriptorTestWriter,category,imgVocVector,false);
			//both
			else{
				Utilities.writeHistogram(descriptorTestWriter,category,imgVocVector,false);
				Utilities.writeHistogram(descriptorTestLibSVMWriter,category,imgVocVector,true);
			}
		    
		}
		
		descriptorTestLibSVMWriter.close();
		descriptorTestWriter.close();
		
		
		//Get elapsed time in milliseconds
        elapsedTimeMillis = System.currentTimeMillis()-start;
        // Get elapsed time in minutes
        elapsedTimeMin = elapsedTimeMillis/(60*1000F);
        System.out.println(elapsedTimeMin +" minutes");
		
	}
	

}
