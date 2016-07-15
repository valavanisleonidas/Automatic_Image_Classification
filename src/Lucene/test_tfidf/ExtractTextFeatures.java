
package Lucene.test_tfidf;

import java.util.List;

import Utils.Utilities;


public class ExtractTextFeatures {
    
    /**
     * Main method
     * @param args
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception{
//    	example();
    	extractTermFeatures();
    }
    
    
    
    // ---------------- false false false has the best results
    // keep stopwords , dont stem , dont use Stanford NLP.......
    public static void extractTermFeatures() throws Exception{
    	String Lucene_train = "Lucene\\clef2016\\enriched\\train_figures.xml";
    	String Lucene_test = "Lucene\\clef2016\\enriched\\test_figures.xml";
    	
    	boolean removeStopwords = true;
    	boolean porterStemming = false;
    	boolean useStanfordNLP = false;
    	boolean useTFIDF = true;
    	String normalization = "L2"; // "L2" "L1"
    	String TFIDF = "TF";
    	String stopwords = "withCommon";
    	String stemming = "";
    	String StanfordNLP = "";
    	if(useTFIDF )
    		TFIDF = "TFIDF";
    	if(removeStopwords)
    		stopwords="withoutCommon";
    	if(porterStemming)
    		stemming = "_stem";
    	if(useStanfordNLP)
    		StanfordNLP = "_stan";
    	
    	
    	String train_file = "Lucene\\enriched_clef2016_"+TFIDF+"_"+normalization+"_Caption_"+stopwords+stemming+StanfordNLP+"_textual_train.txt";
    	String test_file =  "Lucene\\enriched_clef2016_"+TFIDF+"_"+normalization+"_Caption_"+stopwords+stemming+StanfordNLP+"_textual_test.txt";
    	
    	System.out.println(train_file);
//    	System.out.println(test_file);
//    	
//    	DocumentParser parser = new DocumentParser();
//        
//        //------------------------------train file
//        System.out.println("Parse train File");
//        parser.parseFiles(Lucene_train,"train",removeStopwords,porterStemming,useStanfordNLP);
//        
//        System.out.println("calculate train tfidf");
//        parser.tfIdfCalculator(normalization,useTFIDF); //calculates tfidf
//        
//        System.out.println("write to txt");
//        List<double[]> tfidf_train =  parser.getTfidfDocsVector();
//        
//        Utilities.write2TXT(tfidf_train, train_file, null);
//        
//        //---------------------------------test file
//        System.out.println("Parse test File");
//        parser.parseFiles(Lucene_test,"test",removeStopwords,porterStemming,useStanfordNLP);
//        
//        System.out.println("calculate test tfidf");
//        parser.tfIdfCalculator(normalization,useTFIDF); //calculates tfidf
//        
//        List<double[]> tfidf_test =  parser.getTfidfDocsVector();
//        System.out.println("write to txt");
//        Utilities.write2TXT(tfidf_test,test_file, null);
        
  	
    }
    
    public static void example() throws Exception{
    	
//    	DocumentParser parser = new DocumentParser();
//    	boolean useTFIDF = true;
//    	String normalization = "L2";
//        
//        //------------------------------train file
//        System.out.println("Parse File");
//        parser.parseFiles("Lucene\\train_figures_1.xml","train",false,false,false);
//        
//        System.out.println("calculate tfidf");
//        parser.tfIdfCalculator(normalization,useTFIDF); //calculates tfidf
//        
//        System.out.println("tfidf vector size : " +parser.getTfidfDocsVector().size() +"  tfidf word vector size " +parser.getTfidfDocsVector().get(0).length );
//        
//        List<double[]> tfidf_train = parser.getTfidfDocsVector();
//        
//        
//        //---------------------------------test file
//        
//        parser.parseFiles("Lucene\\test_figures_1.xml","test",false,false,false);
//        
//        System.out.println("calculate tfidf");
//        parser.tfIdfCalculator(normalization,useTFIDF); //calculates tfidf
//        List<double[]> tfidf_test = parser.getTfidfDocsVector();
//
//        
//        for(String terms : parser.getAllTerms()){
//        	System.out.print(terms + " ");
//        }
//        System.out.println();
//        for(double[] vec : tfidf_train){
//        	for(double feature : vec)
//        	{
//        		System.out.print(feature+" ");
//        	}
//        	System.out.println();
//        }
//        System.out.println("-----------TEST-------------------------");
//        for(double[] vec : tfidf_test){
//        	for(double feature : vec)
//        	{
//        		System.out.print(feature+" ");
//        	}
//        	System.out.println();
//        }
//        
//        
//        System.out.println("get cosine");
//        parser.getCosineSimilarity(); //calculated cosine similarity   
//    	
    	
    }
    
}
