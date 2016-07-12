import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;
import Utils.Image.ImageFilter;

public class test_normalization {
	
	public static void main (String[] args) throws Exception{
//		exampleTFIDF();
//		System.out.println("-----");
//		simpleNormExample();
//		System.out.println("-----");
//		complexNormExample();
		
		start();
	}
	
	private static void exampleTFIDF(){
		 // example array
		// 1 2 3 
		// 1 2 0
		
		//df :2 2 1
		
		//idf : 0.5945    0.5945    1.0000
		
		//result
		//0.5945348918918356 1.1890697837836712 3.0 
		//0.5945348918918356 1.1890697837836712 0.0 
		
		
		List<double[]> list = new ArrayList<double[]>();
		list.add(new double[]{ 1 ,2 ,3 });
		list.add(new double[]{ 1 ,2 ,0 });
		
		
		list = TFIDF(list);
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.get(i).length;j++){
				System.out.print(list.get(i)[j]+" ");
			}
			System.out.println();
		}
		
	}
	
	private static void simpleNormExample(){
		List<double[]> list = new ArrayList<double[]>();
		list.add(new double[]{1 ,1 ,1 ,1    });
		list.add(new double[]{ 1 ,1 ,1 ,1 });
		
		// norm 2 ,2 
		// results
		//0.5 0.5 0.5 0.5 
		//0.5 0.5 0.5 0.5 
		
		
		list = simpleNormalization(list);
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.get(i).length;j++){
				System.out.print(list.get(i)[j]+" ");
			}
			System.out.println();
		}
		
	}
	
	private static void complexNormExample(){
		List<double[]> list = new ArrayList<double[]>();
		list.add(new double[]{ 1 ,2 ,3     });
		list.add(new double[]{ 1 ,2 ,0 });
		
		
		list = complexNormalization(list);
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.get(i).length;j++){
				System.out.print(list.get(i)[j]+" ");
			}
			System.out.println();
		}
		
//		sqrt array 
//		1.0000    1.4142    1.7321
//		1.0000    1.4142         0
//		
//		---------------TFIDF ---------------
//		0.5945348918918356 0.8407993074174559 1.7320508075688772 
//		0.5945348918918356 0.8407993074174559 0.0 
//		
//		norm : 3.1673850068781686
//		norm : 1.4353341993092914
//		
//		---------------L1 norm ---------------
//		0.1877052807286664 0.2654553537355292 0.5468393655358044 
//		0.41421356237309503 0.5857864376269051 0.0 
		
	}
	
	public static void start() throws Exception{
		String dbsourcePathTrain= "C:\\Users\\l.valavanis\\Desktop\\Clef2013\\compoundLess\\TrainSet";
		String dbsourcePathTest= "C:\\Users\\l.valavanis\\Desktop\\Clef2013\\compoundLess\\TestSet";
		
		String featuresTrainPath = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\ColorCorrelogram-Sift-512-Lire-train-VisualVocabulary.txt";
		String featuresTestPath = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\ColorCorrelogram-Sift-512-Lire-test-VisualVocabulary.txt";
		
		String writeTFPathTrain = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\TF-ColorCorrelogram-Sift-512-Lire-train-VisualVocabulary.txt";
		String writeTFIDFPathTrain = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\TFIDF-ColorCorrelogram-Sift-512-Lire-train-VisualVocabulary.txt";
		
		String writeTFPathTest = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\TF-ColorCorrelogram-Sift-512-Lire-test-VisualVocabulary.txt";
		String writeTFIDFPathTest = "C:\\Users\\l.valavanis\\workspace\\BachelorThesis\\Clef_v2\\TFIDF-ColorCorrelogram-Sift-512-Lire-test-VisualVocabulary.txt";

		
		String normalizationType ="tfidf";  // simple , tfidf 
		
		List<double[]> featuresTrain = readFeaturesFromFile(featuresTrainPath);
		List<double[]> featuresTest= readFeaturesFromFile(featuresTestPath);
		
		if (normalizationType.equals("tf")){
			System.out.println("train");
			featuresTrain = simpleNormalization(featuresTrain);
			System.out.println("test");
			featuresTest = simpleNormalization(featuresTest);
		}
		else if (normalizationType.equals("tfidf")){
			System.out.println("train");
			featuresTrain = complexNormalization(featuresTrain);
			System.out.println("test");
			featuresTest = complexNormalization(featuresTest);
		}
		System.out.println("train file");
		//write features to array
		writeFeatures(featuresTrain, writeTFIDFPathTrain,dbsourcePathTrain ,null);
		System.out.println("testfile");
		writeFeatures(featuresTest, writeTFIDFPathTest,dbsourcePathTest , null);

	}
	
	private static void writeFeatures(List<double[]> features, String writePath,String dbPath, Shell shell) throws Exception {
		BufferedWriter writer = Utilities.openFilesForWriting(writePath, shell);
		boolean isLibsvm = true;
		List<Integer> categories = getCategoriesOfSet(dbPath);
		for (int i=0; i < features.size();i++){
			Utilities.writeHistogram(writer, categories.get(i).toString(), features.get(i),isLibsvm);
			
		}
		writer.close();
		
	}

	private static List<double[]> readFeaturesFromFile(String path) throws Exception{
		return Utilities.readFromFile(path, null);
	}
	
	private static List<double[]> simpleNormalization(List<double[]> features){		
		features = normalizeFeatures(features,"L2");
		return features;
	}
	
	private static List<Integer> getCategoriesOfSet(String testSet){
		List<Integer> Categories = new ArrayList<Integer>();
		String[] testFiles= ImageFilter.getDirFiles(testSet,Integer.MAX_VALUE,true );
		for(int i=0;i<testFiles.length;i++){	
				Categories.add(Integer.valueOf(Utilities.GetParentFolder(testFiles[i]).split("\\.")[1]));
		}
		
		return Categories;
	}

	private static List<double[]> complexNormalization(List<double[]> features){
		//sqrt of features
		features = sqrtFeatures(features);
		//TFIDF
		features = TFIDF(features);
		//L1 Norm
		features = normalizeFeatures(features,"L1");
		
		return features;
	}
	//wrapper for tfidf for list<double[]>
	private static List<double[]> TFIDF(List<double[]> features) {
		double[][] array = convertListToArray(features);
		
		array = TFIDF(array);
		
		features = convertArrayToList(array);
		return features;
	}
	
	private static double[][] TFIDF(double[][] features){
		// compute words frequency
		double[] DF = computeDF(features);
		// compute inverse words freq
		double[] IDF = computeIDF(DF,features.length);
	    // compute tfidf
		features = computeTFIDF(features,IDF);
		return features;
	}
	
	private static double[][] computeTFIDF(double[][] array, double[] idf) {		
		//TFIDF 
		//featureVector = featureVector.*(ones(rows,1)*IDF);
		for(int i=0;i<array.length;i++)
			for(int j=0;j<array[i].length;j++)
				array[i][j] *= idf[j]; 			
			
		return array;
	}

	private static double[] computeDF(double[][] array){
		int rows = array.length;
		int columns = array[0].length;
		double[] df = new double[columns];
		
		for(int i=0;i<columns;i++){
			double sum =0;
			for(int j=0;j<rows;j++){
				if(array[j][i]!=0)
					sum += 1;
			}
			df[i]=sum;
		//	System.out.println("df of column ::: "+sum);
		}
		
		return df;
	}
	
	private static double[] computeIDF(double[] df,int rows){		
		double[] idf = new double[df.length];
		
		//1+log(rows./(DF+1));
		for(int i=0;i<df.length;i++){
			idf[i] = 1 + Math.log( rows / ( df[i] + 1 ) );
	//		System.out.println("idf of column ::: "+idf[i]);
		}	
		return idf;
	}
	
	private static double[][] convertListToArray(List<double[]> features){
		double[][] array = new double[features.size()][features.get(0).length];
		
		for(int i=0;i<features.size();i++)
			for(int j=0;j<features.get(i).length;j++)
				array[i][j] = features.get(i)[j];
		
		return array;
	}

	private static List<double[]> convertArrayToList(double[][] array){
		List<double[]> list = new ArrayList<double[]>();	
		for(int i=0;i<array.length;i++)
			list.add(array[i]);
		
		return list;		
	}
	
	private static List<double[]> sqrtFeatures(List<double[]> features){
    	for(int i=0;i<features.size();i++)
    		features.set(i, sqrtArray(features.get(i)));
    	
    	return features;
	}
	
	private static double[] sqrtArray(double[] imgVocVector){
		double [] array = new double[imgVocVector.length];
    	for(int i=0;i<imgVocVector.length;i++){
    		if(imgVocVector[i]!=0)
    			array[i]= Math.sqrt(imgVocVector[i]);
    	}
    	return array;
	}
	
	//normalize features
	private static List<double[]> normalizeFeatures(List<double[]> features,String normalization){
		for (int i =0;i<features.size();i++)
			features.set(i, normalizeArray(features.get(i),normalization) );
		
		return features;
	}
	
	//normalize double array
	private static double[] normalizeArray(double[] imgVocVector,String normalization){
		double [] array = new double[imgVocVector.length];
		double norm=computeNorm(imgVocVector,normalization);
    	for(int i=0;i<imgVocVector.length;i++){
    		if(imgVocVector[i]!=0)
    			array[i]=imgVocVector[i]/norm;
    	}
    	return array;
	}
	
	//computes norm of double array
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
	
	
}
