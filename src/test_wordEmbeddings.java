import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Shell;

import Lucene.Read_Xml;
import Lucene.Read_Xml.TextualData;
import Lucene.test_tfidf.TfIdf;
import Utils.Utilities;

public class test_wordEmbeddings {
	
	//stopwords to be removed
    private static List<String> stopwords = new ArrayList<String>();
    private static String commonWordsPath = "Lucene\\common_words";
    private static boolean removeStopwords = false;
    private static boolean AddZeroIfTermDoesNotExist = true;
    private static String xml_path = "Lucene\\clef2013\\";
    private static String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\clef2013\\Compound";
    
	public static void main(String[] args) throws Exception{

    	String stopwordsName = "withCommonWords";
    	String zeroIfTermNotExist = "_notExistedTermsDiscarded";

    	if(removeStopwords){
    		stopwordsName="NoCommonWords";
			stopwords = ReadStopwords(commonWordsPath, null);
    	}
    	if(AddZeroIfTermDoesNotExist)
    		zeroIfTermNotExist = "_zeroIfTermNotExists";
    	
		
		String train_dir = xml_path+"train_figures.xml";
        String test_dir = xml_path+"test_figures.xml";
		
        String test_file = xml_path+"embeddings_test"+zeroIfTermNotExist+"_"+stopwordsName+".txt";
		String train_file = xml_path+"embeddings_train"+zeroIfTermNotExist+"_"+stopwordsName+".txt";

		System.out.println(train_file);
		System.out.println(test_file);
		
		String typeFilePath = "E:\\leonidas\\word2vecTools\\types.txt";
		String embeddingsFilePath = "E:\\leonidas\\word2vecTools\\vectors.txt";
		
		System.out.println("loading embeddings...");
		List<double[]> embeddings = readEmbeddingsFromFile(embeddingsFilePath, null);
		List<String> words = readWordsFromFile(typeFilePath, null);
		
        System.out.println("extracting train features...");
		List<double[]> train_features = get_features(words,embeddings,train_dir,"train");
        Utilities.write2TXT(train_features,train_file, null);

        System.out.println("extracting test features...");
        List<double[]> test_features = get_features(words,embeddings,test_dir,"test");
        Utilities.write2TXT(test_features,test_file, null);
		
	}
	
	public static boolean containsSubArray(List<double[]> list, double[] subarray) {
		   for ( double[] arr : list ) {
			  if (Arrays.equals(arr, subarray)) {
		         return true;
		      }
		   }
		   return false;
		}
	
	
	public static void test_centroid(){
		
		
//		double[][] a = new double[3][4];
//		for (int i = 0; i < a.length; i++) {
//			for (int j = 0; j < a[i].length; j++) {
//				a[i][j] = (i + j  ) * 2;
//			}
//		}
//		for (int i = 0; i < a.length; i++) {
//			for (int j = 0; j < a[i].length; j++) {
//				System.out.print(a[i][j] +" ");
//			}
//			System.out.println();
//		}
//    	List<Double> idf_terms = new ArrayList<Double>();
//    	idf_terms.add(0.5);
//    	idf_terms.add(0.5);
//    	idf_terms.add(0.5);
//    	idf_terms.add(0.5);
//
//		System.out.println("---------------------");
//		double[] centr = getCentroid(a,idf_terms); 
//		for (int i = 0; i < centr.length; i++) {
//			System.out.print(centr[i] + " ");
//		}
		
	}
	
	public static void test_text() throws Exception{
		//processing doc number : 1803, doc  :SKIN AS A MARKER OF INTERNAL DISEASE: A CASE OF SARCOIDOSIS Chest X-ray
		String text = "SKIN AS A MARKER OF INTERNAL DISEASE: A CASE OF SARCOIDOSIS Chest X-ray";
		String typeFilePath = "E:\\leonidas\\word2vecTools\\types.txt";
		List<String> words = readWordsFromFile(typeFilePath, null);
		String[] docTerms = text.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
		for(String term : docTerms){
    		
    		if(words.contains(term.toLowerCase())){
    			
    			System.out.println("found term "+ term + " , index "+ words.indexOf(term.toLowerCase()));
    		}
    		
    	}

	}
	
	public static List<double[]> get_features(List<String> words, List<double[]> embeddings,
			String xml_dir, String mode) throws Exception{
		
		int total_words_found = 0;
		int total_words_not_found = 0;
		int currentDoc = 0;
		Map<String,Double> word_idf = new HashMap<String,Double>();
		
		List<double[]> features = new ArrayList<double[]>();

		Read_Xml xml = new Read_Xml();
		xml.ReadXml(xml_dir, null,databasePath,mode);
		
		List<TextualData> images = xml.getImages();
		List<String[]> AlltermDocsArray = getAllTermDocs(images);
		
		//each row contains one doc (image)    	
        for (TextualData image :  images) {
        	String doc = image.AllFields;
        	
        	String imagePath = image.category+"/"+image.id+".jpg";
        	
        	
//        	int total_words = total_words_not_found+total_words_found;
//        	System.out.println("processing doc number : "+currentDoc++ 
//        			+ " id : "+imagePath+" , found "+ total_words_found+", not found "+total_words_not_found+" out of "+ total_words +", doc  :"+ doc);
        	
        	
        	Map<String,double[]> Doc_word_embeddings = new HashMap<String,double[]>();
        	List<double[]> doc_term_embeddings = new ArrayList<double[]>();
        	//get individual terms of sentence
        	String[] docTerms = doc.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   
        	for(String term : docTerms){
        		String termLower = term.toLowerCase(); 
    			double[] term_embedding = new double[embeddings.get(0).length];
    			
    			if(removeStopwords){
    				//avoid stopwords
	            	if(stopwords.contains(termLower)){ 
	            		continue;
	            	}
            	}
    			
        		if(words.contains(termLower)){
        			//if term exists already in document continue
        			if(Doc_word_embeddings.containsKey(termLower)){
        				continue;
        			}
        			
        			//get embedding
        			term_embedding = embeddings.get(words.indexOf(termLower));
        			
        			double tf =  new TfIdf().tfCalculator(docTerms, termLower);
        			
        			double idf;
        			if(word_idf.containsKey(termLower)){
        				idf = word_idf.get(termLower);
        			}
        			else{
        				//calculate idf
        				idf = new TfIdf().idfCalculator(AlltermDocsArray, termLower);
        				word_idf.put(termLower, idf);
        			}
        			double tfidf = tf*idf; 
        			//multiply with idf
        			term_embedding = WeightArray(term_embedding, tfidf);
        			Doc_word_embeddings.put(termLower, term_embedding);
        			
        			//add to list
        			doc_term_embeddings.add(term_embedding);
        			
        			total_words_found++;
        		}
        		//TODO add caching mechanism
        		
        		//TODO find nearest word to centroid and idf with the final word 
        		//TODO Normalization L1,L2 ktlp TRYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
        		else{
        			
        			if(AddZeroIfTermDoesNotExist){
        				//add zeros
	            		Arrays.fill(term_embedding, 0);
	        			doc_term_embeddings.add(term_embedding);
        			}
        			total_words_not_found++;
        		}
        	}
        	double[] feature_vector = new double[embeddings.get(0).length];
        	if(doc_term_embeddings.size() == 0){
        		Arrays.fill(feature_vector, 0);
        	}else{
        		feature_vector = getCentroid(doc_term_embeddings.toArray(new double[doc_term_embeddings.size()][doc_term_embeddings.get(0).length]));
        	}
        	features.add(feature_vector);
        }
		System.out.println("words found : " +total_words_found +"  : not found "+ total_words_not_found);
		return features;
	}
	
	//get sum of each column and divide by columns size
	public static double[] getCentroid(double[][] doc_embeddings){
		int features_size = doc_embeddings[0].length;
		int images_size = doc_embeddings.length;
		double[] array = new double[features_size];
		
		for(int i =0; i< features_size;i++){
			double sum = 0;
			for (int j = 0; j < images_size; j++) {
				sum += doc_embeddings[j][i];
			}
			array[i]= sum/images_size;
		}
		return array;
	}

	//multiply array with a number when we want to combine two models
	 public static double[] WeightArray(double [] array , double weight)
	 {
		  double[] arr= new double[array.length];
		  for ( int i =0;i<array.length;i++)
			  arr[i]=array[i]*weight;
		   	   
		   return arr;
	 }
	
	public static List<String[]> getAllTermDocs(List<TextualData> images){
		List<String[]> termsDocsArray = new ArrayList<String[]>();
		for (TextualData image :  images) {
			String doc = image.AllFields;
			String[] docTerms = doc.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
			termsDocsArray.add(docTerms); 
		}
		return termsDocsArray;
	}
	
	public static List<String> readWordsFromFile(String filePath,Shell shell) throws Exception {

		   List<String> centroid = new ArrayList<String>();
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
					   centroid.add(line);				 
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
	
	public static List<double[]> readEmbeddingsFromFile(String filePath,Shell shell) throws Exception {

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
							  						  
						  while(tokenizedLine.hasMoreTokens()){
							   feature[count]=Double.parseDouble(tokenizedLine.nextToken());
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
	
	public static List<String> ReadStopwords(String filePath, Shell shell) throws IOException{
		
		List<String> stopwords = new ArrayList<String>();
		
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
					   stopwords.add(line.trim());
				   }
			   }catch(Exception e){
					Utilities.showMessage("Error reading file!", shell, true);
					e.printStackTrace();
					reader.close();
					return null;
			   }
			   try {
				   reader.close();
			   }catch (FileNotFoundException e) {
					Utilities.showMessage("Could not close File!", shell, true);
					e.printStackTrace();
					return null;
			   }
		
			   return stopwords;
		
	}
	
	
}
