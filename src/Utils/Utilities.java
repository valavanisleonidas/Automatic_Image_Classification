package Utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ImageRepresentationModels.BoVW.Descriptors.ColorCorrelogram;
import ImageRepresentationModels.BoVW.Descriptors.Features;
import ImageRepresentationModels.BoVW.Descriptors.SiftFeatures;
import ImageRepresentationModels.BoVW.Descriptors.SurfFeatures;
import GUI.ProgressDialog;
import Utils.Image.ImageFilter;
import Utils.Image.ImageUtility;
import Utils.Image.ImageConverter;
import Utils.Image.ColorConversion.ColorSpace;

public class Utilities {

	//returns colorspace object from String
	public static ColorSpace findColorSpace(String colorspace){
		
		if(colorspace.contains("RGB"))
			return ColorSpace.RGB;          
	  	else if(colorspace.contains("HSV"))
	  		return ColorSpace.HSV;          
		else if(colorspace.contains("XYZ"))
			return ColorSpace.XYZ;          
		else if(colorspace.contains("CIELab"))
			return ColorSpace.CIELab;          
		else if(colorspace.contains("YCbCr"))
	  		return ColorSpace.YCbCr;      
		else
			return null;
	}
	//returns true if string is numeric
	public static boolean isNumeric(String str)  {  
		boolean isNumeric=true;
		  try  {  
		    Double.parseDouble(str);  
		  } catch(NumberFormatException nfe)  {  
		    return isNumeric=false;  
		  }  
		  return isNumeric;  
	}	
	
	public static boolean collectionFilesAreCorrect(String[] filenames,Shell shell){
		if(filenames[0]==null ){
			Utilities.showMessage("No train file found.\n Name collection folder containing train or give correct path", shell, true);
			return false;
	  	}
   		if(filenames[1]==null){
			Utilities.showMessage("No test file found.\n Name collection folder containing test or give correct path", shell, true);
			return false;
	  	}
   		return true;
		
	}
	
	//returns descriptor Object from string
	public static Features getDescriptor(String descriptorChoice){
		
		if ( descriptorChoice.equals("Sift") || descriptorChoice.equals("ColorCorrelogram-Sift") )
			return new SiftFeatures();
		else if ( descriptorChoice.equals("Surf") )
			return new SurfFeatures();
		else
			return new ColorCorrelogram();
		
				
	}
	//concatenates two arrayw
	public static double[] concat(double[] first, double[] second) {
		int firstCol = first.length;
		int secondCol = second.length;
		double[] join= new double[firstCol+secondCol];
			   
		for(int i =0 ; i <join.length;i++){
			join[i] = (i<firstCol)
					? first[i]
					: second[i-firstCol];
		}
		return join;
	}
	
	public static void writeHistogram(BufferedWriter writer,String category,double[] imgVocVector,boolean isLibSVM) throws Exception{
		 if (isLibSVM)
			 writeHistogramLibsvm(writer,category,imgVocVector);
		 else
			 writeFeatures(writer,category,imgVocVector);  
	}
	
	public static void writeHistogramLibsvm(BufferedWriter writer,String category,double[] imgVocVector)throws Exception{
		 int counterTXT=0;
	     writer.write(category +" ");
		
     	 for (int i = 0 ; i < imgVocVector.length ; i++){
			 counterTXT++;
			 if(imgVocVector[i]!=0)
				writer.write(counterTXT +":"+imgVocVector[i] + " ");
		 }
		 writer.write("\n");	
	}

	public static void writeFeatures(BufferedWriter writer,String category,double[] imgVocVector)throws Exception{
		 for (int i = 0 ; i < imgVocVector.length ; i++){
    		 writer.write(imgVocVector[i] + " ");
		 }
		 writer.write("\n");
		
	}
	
	
	public static List<double[]> readFromFile(String filePath,Shell shell) throws Exception {

		   List<double[]> centroid=new ArrayList<double[]>();
		   BufferedReader reader = null;	    
		 	try {
		 		reader = new BufferedReader(new FileReader(new File(filePath)));
			} catch (FileNotFoundException e1) {
				Utilities.showMessage("Could not Read File!", shell, true);
				e1.printStackTrace();

				return null;
			}
			
			String line=null;
			   try {
				   while ((line = reader.readLine()) != null){ 
						  int count = 0;
						  StringTokenizer tokenizedLine = new StringTokenizer(line," ");
						  double[] feature = new double[tokenizedLine.countTokens()];
							  
						  System.out.println(tokenizedLine.countTokens());
						  
						  while(tokenizedLine.hasMoreTokens()){
							   feature[count]=Integer.parseInt(tokenizedLine.nextToken());
							   count++;
						  }
						  centroid.add(feature);				 
				   }
				  
			   }catch(Exception e){
					Utilities.showMessage("Error reading file!", shell, true);
					e.printStackTrace();
		 			return null;
			   }
			   try {
				   reader.close();
			   }catch (FileNotFoundException e) {
					Utilities.showMessage("Could not close File!", shell, true);
					e.printStackTrace();

		 			return null;
			   }
			   return centroid;
	}
	
	public static double[] readIndexFeatureFromFile(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		
		if(line == null ) return null;
		
		System.out.println(line);
		
		StringTokenizer tokenizedLine = new StringTokenizer(line," ");
		double[] feature = new double[tokenizedLine.countTokens()];
		
		for(int i=0;i<tokenizedLine.countTokens();i++)
			feature[i]=Double.parseDouble(tokenizedLine.nextToken());
		
		return feature;
	} 
	
	
	//writes palete to path
	public static void writePalete2TXT(int [][] palete , String path,Shell shell) throws IOException{
		List<double[]> array = intArray2DoubleList(palete);
		write2TXT(array,path,shell);	
	}
	
	//writes palete to path
	public static void write2TXT(List<double[]> colors , String path,Shell shell) throws IOException{
		 BufferedWriter centroidWriter = null;
		 File centroidFile=null;
		
		 try{
			 centroidFile = new File (path);
		 }
		 catch(NullPointerException e ){
			 Utilities.showMessage("File Not Found!", shell, true);
			 e.printStackTrace();
		 }
		 try{
			 centroidWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(centroidFile))); 
		 }catch(FileNotFoundException ex){
			 Utilities.showMessage("Error opening file!", shell, true);
			 ex.printStackTrace();
		 }
				  	
		 for(int i=0;i<colors.size();i++){
			 for(int j=0;j<colors.get(i).length;j++){
				 centroidWriter.write(colors.get(i)[j]+" ");
			 }
			 centroidWriter.write("\n");
		 }
		 try {
			 centroidWriter.close();	
		 }catch (FileNotFoundException e) {
			 Utilities.showMessage("Could not close File!", shell, true);
			 return;
		 }
	}
	
	//writes terms to path
	public static void writeTerms(List<String> terms, String path) throws IOException{
		 BufferedWriter centroidWriter = null;
		 File centroidFile=null;
		
		 try{
			 centroidFile = new File (path);
		 }
		 catch(NullPointerException e ){
			 Utilities.showMessage("File Not Found!", null, true);
			 e.printStackTrace();
		 }
		 try{
			 centroidWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(centroidFile))); 
		 }catch(FileNotFoundException ex){
			 Utilities.showMessage("Error opening file!", null, true);
			 ex.printStackTrace();
		 }
				  	
		 for(int i=0;i<terms.size();i++){
		 	 
			 centroidWriter.write(terms.get(i));
			 centroidWriter.write("\n");
		 }
		 try {
			 centroidWriter.close();	
		 }catch (FileNotFoundException e) {
			 Utilities.showMessage("Could not close File!", null, true);
			 return;
		 }
	}
	
	
	public static BufferedWriter openFilesForWriting(String filePath,Shell shell) throws IOException{
		File file = null ;
	
		try {
			file = new File (filePath);
		}catch(NullPointerException e ){
			Utilities.showMessage("File Not Found!", shell, true);
			return null;
		}
		try{
			return  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))); 
		}catch(FileNotFoundException ex){
			Utilities.showMessage("Error opening file!", shell, true);
			return null;
		}
	}
		
	public static BufferedReader openFilesForReading(String filePath,Shell shell) throws IOException{
		
	 	try {
	 		return  new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e1) {
			Utilities.showMessage("Could not Read File!", shell, true);
			return null;
		}
		
	}
		
	//Comment color correlogram does not need cluster because it is one vector for each image
	public static void writeFeatures2TXT(String path,int sampleSize,String descriptorChoice,
		Features descriptor,BufferedWriter descriptorWriter, String kmeans,
		List<double[]> descriptorFeatures,Shell shell) throws Exception{
		try{
				int counter=0;
				final ProgressDialog progress = new ProgressDialog();
				
				Thread progressThread = new Thread(progress);
				progressThread.start();
				Thread.sleep(100); // POINT OF FOCUS
				//[0] : train  , [1] test
				String[] fileNames =Utilities.getTrainTest(new File(path));
				String[] sampleImages= ImageFilter.getDirFiles(fileNames[0],sampleSize, true);
				
				
				progress.setWholeImageSize(sampleImages.length);
				
				for(int i=0;i<sampleImages.length;i++){
					
						if( ( descriptorChoice.equals("ColorCorrelogram-Sift") || descriptorChoice.equals("ColorCorrelogram") )  
								&& ImageFilter.isPNG(new File(sampleImages[i].toString()))){
							
							File imaging= ImageConverter.PNGtoJPG(new File(sampleImages[i].toString()));
						
							System.out.println("converted  "+ imaging.getAbsolutePath());
							extractFeatures(imaging,descriptor,descriptorWriter,kmeans,descriptorFeatures);
					    	imaging.delete();
				
						}else{
							System.out.println(sampleImages[i].toString());
							extractFeatures(new File(sampleImages[i].toString()) ,descriptor,descriptorWriter
									,kmeans,descriptorFeatures);
						}
						progress.updateImageProcessed(++counter);  
						Thread.sleep(1); // POINT OF FOCUS
					
				}
				 try {
					 descriptorWriter.close();
				 }catch (FileNotFoundException e) {
					 Utilities.showMessage("Could not close File!", shell, true);
					 return;
				 }
				sampleImages=null;
				try{
					progress.terminate();
					progressThread.interrupt();
				}catch(Exception e){	}
		
		}catch(Exception e ){
			 Utilities.showMessage("Error extracting features", shell, true);
			e.printStackTrace();
		}
		
		
	}	
			
	public static void extractFeatures(File image,Features descriptor,
			BufferedWriter descriptorWriter, String kmeans,List<double[]> descriptorFeatures) throws Exception {
		////////////////Extracting Features of image and writing to file///////	
		BufferedImage Bimage = ImageUtility.getImage(image.getAbsolutePath()); //read picture	
		//extract features and write to file
		descriptor.extractFeatures(Bimage,descriptorWriter);
		
		if(kmeans.equals("Lire"))
			descriptor.extractFeatures(Bimage,descriptorFeatures);
		
	}
	
	  //-- Input-Output 
    public static int[][] binaryFileTo2DIntArray(String filePath) {
        int[][] ret = null;
        try {
            // Wrap the FileInputStream with a DataInputStream
            FileInputStream fileInput = new FileInputStream(filePath);
            DataInputStream dataIn = new DataInputStream(fileInput);
            // [0]<integer>: number of rows (integer)
            int rows = dataIn.readInt();
            // [1]<integer>: number of columns (integer)
            int cols = dataIn.readInt();
            // [...]<double>: array data
            ret = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    ret[i][j] = dataIn.readInt();
                }
            }
            //Close the file.
            dataIn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    //computes euclidean distance of two double arrays
	public static double euclideianDistance(double[] centroids, double[] features){
	    double Sum = 0.0;
	    for(int i=0;i<centroids.length;i++) {
	       Sum +=  Math.pow((centroids[i]-features[i]),2.0);
	    }
	    return Math.sqrt(Sum);
	}
	
	//normalize features
	private static List<double[]> normalizeFeatures(List<double[]> features,String normalization){
		for (int i =0;i<features.size();i++)
			features.set(i, normalizeArray(features.get(i),normalization) );
		
		return features;
	}
	
	//normalize double array
	public static double[] normalizeArray(double[] imgVocVector,String normalization){
		double [] array = new double[imgVocVector.length];
		double norm=computeNorm(imgVocVector,normalization);
    	for(int i=0;i<imgVocVector.length;i++){
    		if(imgVocVector[i]!=0)
    			array[i]=imgVocVector[i]/norm;
    	}
    	return array;
	}
	
	//computes norm of double array 
	//norms : L1 , L2
	private static double computeNorm(double[] imgVocVector,String normalization){
		double sum=0;
    	for(int i=0;i<imgVocVector.length;i++) {
    		if (normalization.equals("L2"))
    			sum+=imgVocVector[i]*imgVocVector[i];
    		else if (normalization.equals("L1"))
    			sum+=imgVocVector[i];
    	}
    	
    	if (normalization.equals("L2"))
    		return Math.sqrt(sum);	
    	// else L1
		return sum;
    		
	}
	
	//multiply array with a number when we want to combine two models
	 public static double[] weightModels(double [] array , double weight)
	  {
		  double[] arr= new double[array.length];
		  for ( int i =0;i<array.length;i++)
			  arr[i]=array[i]*weight;
		   	   
		   return arr;
	  }
	  
	//parses the path and gets the parent to get the category and type of image (test , Train )
	public static String GetParentFolder(String ParentPath){
		String returnPath=null;
		StringTokenizer path= new StringTokenizer(ParentPath,"\\");
		while(path.hasMoreElements()){
			if(path.countTokens()==3)
				return returnPath=path.nextToken()+"."+path.nextToken();
			else if (path.countTokens()==2)
				return returnPath=path.nextToken();
			else if (path.countTokens()==1)
				return ParentPath;
			else
				path.nextToken();
				
		}
		return returnPath;
	}
	
	public static Map<String,Integer> getSubFoldersOfFolder(String path){
		Map<String,Integer> categories = new HashMap<String,Integer>();
		
		File directory = new File(path);
		File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (int i =0;i<subdirs.length;i++) {
			categories.put(subdirs[i].getName(), i);
		}
		return categories;
	}
	
	public static String[] getSubFoldersNamesOfFolder(String path){
		
		File directory = new File(path);
		File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		String[] _subdirs = new String[subdirs.length];
		for (int i =0;i<subdirs.length;i++) {
			_subdirs[i] = subdirs[i].getAbsolutePath();
		}
		return _subdirs;
	}
	
	public static Map<String,Integer> getNumberOfImagesPerCategory(String path){
		Map<String,Integer> categories_numImages = new HashMap<String,Integer>();
		
		String[] categories = getSubFoldersNamesOfFolder(path);
		for (int i = 0; i < categories.length; i++) {
			
			int numbersOfCategory = new File(categories[i]).listFiles(new ImageFilter()).length;
			String imageName = getName(categories[i]);
			categories_numImages.put(imageName, numbersOfCategory);
		}
		return categories_numImages;
	}
	
	
	public static String[] getNameUntilTestTrain(String path){
	  	 
		StringTokenizer st = new StringTokenizer(path,"\\");
		String[] name=new String[2];
		while (st.hasMoreElements()) {
			if(st.countTokens()==3)
			{
				name[0]=st.nextToken()+"\\";
				name[1]=st.nextToken()+"\\"+st.nextToken();
				return name;
			}
			else
				st.nextToken();
		}
		return name;
	
	}
	
	//parse string path to get name of path
	public static String getName(String path) {
	      StringTokenizer st = new StringTokenizer(path,"\\");
	      String name=new String();
	      while (st.hasMoreElements()) {
	    	  name=(String) st.nextElement();
	      }
	      return name;
	}
	
	//return train and test path from 'DBdir' 
	public static String[] getTrainTest(File DBdir) throws Exception{
		  String[] fileNames = new String[2];
		  File[] files = DBdir.listFiles();
		  for (File filing : files){
		    if (filing.isDirectory()){
		    	if(filing.getName().toLowerCase().contains("train"))
		    		fileNames[0]=filing.getAbsolutePath();
		  		else if(filing.getName().toLowerCase().contains("test"))
		  			fileNames[1]=filing.getAbsolutePath();
		    }
		  }
		  return fileNames;

	  }
	
	
	//get image name ( px image.png   name : image) 
	public String getImageName(String path) {
		StringTokenizer st = new StringTokenizer(path,".");  	 	
 		return st.nextToken();
}
		
	
	//converts int array to list<double[]>
	 public static List<double[]> intArray2DoubleList(int [][] array){
	    List<double[]> list = new ArrayList<double[]>();
		for(int i=0;i<array.length;i++)
			list.add(new double[]{array[i][0],array[i][1],array[i][2]});
		 
		 return list;
		  
	 }

	
	 //starts connection with matlab
	public static void VLFeatMatlab(String[] commands) throws Exception{
		
		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		MatlabProxyFactoryOptions builder = new MatlabProxyFactoryOptions.Builder().setMatlabLocation("C:\\Program Files\\MATLAB\\MATLAB Production Server\\R2015a\\bin\\matlab.exe").setUsePreviouslyControlledSession(true).setHidden(false).build();
		factory = new MatlabProxyFactory(builder);
		
		
		for (int i=0;i<commands.length;i++){
			proxy.eval(commands[i]);
		}
		
		//Disconnect the proxy from MATLAB
		proxy.exit();
		proxy=null;
		factory=null;
		//proxy.disconnect();
	}
	

	//
	public static void showMessage(String message,Shell shell,boolean error) {
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	        	if(!error)
	        		MessageDialog.openInformation(shell, "Completed",message);
	        	else
		    		MessageDialog.openError(shell, "Error", message);
	        }
	    });
	}

}
