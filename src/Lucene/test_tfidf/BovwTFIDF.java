package Lucene.test_tfidf;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tartarus.snowball.ext.PorterStemmer;

import Lucene.Read_Xml;
import Lucene.Read_Xml.TextualData;
import Lucene.stanford_parser;
import Utils.Utilities;

/**
 * Class to read documents
 *
 * 
 */
public class BovwTFIDF {

    //This variable will hold all terms of each document in an array.
    private List<String[]> termsDocsArray = new ArrayList<String[]>();
    //to hold all terms
    private List<String> allTerms = new ArrayList<String>();
    //tf-idf vector for each document
    private List<double[]> tfidfDocsVector = new ArrayList<double[]>();
    //stopwords to be removed
    private List<String> stopwords = new ArrayList<String>();
    
    private String commonWordsPath = "Lucene\\common_words";
	private String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2012";
    private static String xml_path = "Lucene\\clef2012\\";
	private PorterStemmer stem = new PorterStemmer();
	private static boolean removeStopwords = true;
	private static boolean porterStemming = false;
	private static boolean useStanfordNLP = false;
	private static boolean useTFIDF = true;
	private static String normalization = "L2"; // "L2" "L1"
	
	
    public static void main(String args[]) throws Exception{
    	extractTermFeatures();
    }
    
    
    
    // ---------------- false false false has the best results
    // keep stopwords , dont stem , dont use Stanford NLP.......
    public static void extractTermFeatures() throws Exception{
    	String Lucene_train = xml_path+"train_figures.xml";
        String Lucene_test = xml_path+"test_figures.xml";
    		
    	
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
    	
    	
    	String train_file = xml_path+"textual_train_"+TFIDF+"_"+normalization+"_Caption_"+stopwords+stemming+StanfordNLP+".txt";
    	String test_file =  xml_path+"textual_test_"+TFIDF+"_"+normalization+"_Caption_"+stopwords+stemming+StanfordNLP+".txt";
    	
    	System.out.println(train_file);
    	System.out.println(test_file);
    	
    	BovwTFIDF bovw = new BovwTFIDF();
        
        //------------------------------train file
        System.out.println("Parse train File");
        bovw.parseFiles(Lucene_train,"train");
        
        System.out.println("calculate train tfidf");
        bovw.tfIdfCalculator(); //calculates tfidf
        
        System.out.println("write to txt");
        List<double[]> tfidf_train =  bovw.getTfidfDocsVector();
        
        Utilities.write2TXT(tfidf_train, train_file, null);
        
        //---------------------------------test file
        System.out.println("Parse test File");
        bovw.parseFiles(Lucene_test,"test");
        
        System.out.println("calculate test tfidf");
        bovw.tfIdfCalculator(); //calculates tfidf
        
        List<double[]> tfidf_test =  bovw.getTfidfDocsVector();
        System.out.println("write to txt");
        Utilities.write2TXT(tfidf_test,test_file, null);
    
    }
	
	
	
    /**
     * Method to read files and store in array.
     * @param filePath : source file path
     * @throws Exception 
     */
    public void parseFiles(String index_dir,String mode) throws Exception {
    	termsDocsArray = new ArrayList<String[]>();
    	tfidfDocsVector = new ArrayList<double[]>();
    	
    	Read_Xml xml = new Read_Xml();
		xml.ReadXml(index_dir, null,databasePath,mode);
		
		if(removeStopwords){
			if(mode.equals("train"))
				stopwords = xml.ReadStopwords(commonWordsPath, null);
		}
				
		//each row contains one doc (image)    	
        for (TextualData image : xml.getImages()) {
           String doc = image.AllFields;
        	
           String[] tokenizedTerms;
           if(useStanfordNLP)
           	    tokenizedTerms = stanford_parser.tokenize(doc); //to get individual terms using stanford nlp
           else
        		tokenizedTerms = doc.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   //to get individual terms
            
            termsDocsArray.add(tokenizedTerms);   
            if(mode.equals("test"))
            	continue;
            
            for (String term : tokenizedTerms) {
            	term = preprocessTerm(term);
        		
            	if (allTerms.contains(term))  //avoid duplicate entry
                    continue;
            	if(removeStopwords){
	            	if(stopwords.contains(term)) //avoid stopwords
	                	continue;
            	}
        		allTerms.add(term);
            }
        }
        
        System.out.println("all terms are : "+allTerms.size());
        System.out.println("all docs are : "+termsDocsArray.size());
    }
    
    public String preprocessTerm(String term){
    	term = term.toLowerCase();
    	if(porterStemming) //stemming
    		term = stemWord(term.toLowerCase());
    	return term;
    	
    }
    
    
    public String stemWord(String term){
    	stem.setCurrent(term);
		stem.stem();
		return stem.getCurrent();
    }
    
    /**
     * Method to create termVector according to its tfidf score.
     */
    public void tfIdfCalculator() {
    	
		Map<String,Double> word_idf = new HashMap<String,Double>();
    	
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency        
        int counter =0;
        
        //for each document (image)
        for (String[] docTermsArray : termsDocsArray) {

        	if(counter%100 ==0)
        		System.out.println("extracted : "+counter+"/"+termsDocsArray.size());
        	counter++;
        	     
        	double[] tfidfvectors = new double[allTerms.size()];
        	Arrays.fill(tfidfvectors, 0);
        	
            //for each term
        	for(String term : docTermsArray){
        		term = preprocessTerm(term);
        		
        		if(removeStopwords){
	            	if(stopwords.contains(term)) //avoid stopwords
	                	continue;
            	}
        		
        		//if term exists in all terms
        		int index = allTerms.indexOf(term);
        		if(index == -1){
        			//System.out.println("Word "+ term +" does not exist in sentence "+ docTermsArray.toString());
        			continue;
        		}
        	
                tf =  new TfIdf().tfCalculator(docTermsArray, term);
                tfidf = tf;
                if(useTFIDF){
                	if(word_idf.containsKey(term)){
        				idf = word_idf.get(term);
        			}
        			else{
        				//calculate idf
        				idf = new TfIdf().idfCalculator(termsDocsArray, term);
        				word_idf.put(term, idf);
        			}
	                tfidf = tf * idf;
                }
                
                //System.out.println("term "+terms +" was FOUND in document: "+counter+" with TFIDF : "+tfidf + " tf :"+tf +" idf :"+idf);
                tfidfvectors[index] = tfidf;
        	}
        	//normalization
            tfidfvectors = Utilities.normalizeArray(tfidfvectors, normalization);
            tfidfDocsVector.add(tfidfvectors);  //storing document vectors;      
        }
    }
    
    public boolean ContainsTerm(String[] array,String term){
    	for(String _term : array){
    		if(_term.toLowerCase().equals(term.toLowerCase()))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Method to calculate cosine similarity between all the documents.
     */
    public void getCosineSimilarity() {
        for (int i = 0; i < tfidfDocsVector.size(); i++) {
            for (int j = 0; j < tfidfDocsVector.size(); j++) {
                System.out.println("between " + i + " and " + j + "  =  "
                               + new CosineSimilarity().cosineSimilarity
                                   (
                                     tfidfDocsVector.get(i), 
                                     tfidfDocsVector.get(j)
                                   )
                              );
            }
        }
    }

	public List<double[]> getTfidfDocsVector() {
		return tfidfDocsVector;
	}

	public List<String> getAllTerms() {
		return allTerms;
	}
}
