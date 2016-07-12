package Lucene.test_tfidf;


import java.util.ArrayList;
import java.util.List;

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
public class DocumentParser {

    //This variable will hold all terms of each document in an array.
    private List<String[]> termsDocsArray = new ArrayList<String[]>();
    //to hold all terms
    private List<String> allTerms = new ArrayList<String>();
    //tf-idf vector for each document
    private List<double[]> tfidfDocsVector = new ArrayList<double[]>();
    //stopwords to be removed
    private List<String> stopwords = new ArrayList<String>();
    
    private String commonWordsPath = "Lucene\\common_words";
	private String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2016\\enriched";
	private PorterStemmer stem = new PorterStemmer();
	
    /**
     * Method to read files and store in array.
     * @param filePath : source file path
     * @throws Exception 
     */
    public void parseFiles(String index_dir,String mode,boolean removeStopwords,boolean porterStemming,boolean useStanfondNLP) throws Exception {
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
           if(useStanfondNLP)
           	    tokenizedTerms = stanford_parser.tokenize(doc); //to get individual terms using stanford nlp
           else
        		tokenizedTerms = doc.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   //to get individual terms
            
            termsDocsArray.add(tokenizedTerms);   
            if(mode.equals("test"))
            	continue;
            
            for (String term : tokenizedTerms) {
            	if(porterStemming) //stemming
            		term = stemWord(term.toLowerCase());
            	
        		
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
    
    public String stemWord(String term){
    	stem.setCurrent(term);
		stem.stem();
		return stem.getCurrent();
    }
    
    /**
     * Method to create termVector according to its tfidf score.
     */
    public void tfIdfCalculator(String normalization, boolean useTFIDF) {
    	
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency        
        int counter =1;
        //for each document (image)
        for (String[] docTermsArray : termsDocsArray) {
        	if(counter%100 ==0)
        		System.out.println("extracted : "+counter+"/"+termsDocsArray.size());
        	counter++;
        	
        	double[] tfidfvectors = new double[allTerms.size()];
            int count = 0;
            for (String terms : allTerms) {
            	
            	//if term is not in document then tf is zero and so is tfidf 
            	if(!ContainsTerm(docTermsArray,terms)){
            		//System.out.println("term "+terms +" was NOT FOUND in document: "+counter);
            		tfidfvectors[count] = 0.0;
            		count++;
                    continue;
            	}
            	
                tf =  new TfIdf().tfCalculator(docTermsArray, terms);
                if(useTFIDF){
	                idf = new TfIdf().idfCalculator(termsDocsArray, terms);
	                tfidf = tf * idf;
                }
                else{
                	tfidf = tf;
                }
                //System.out.println("term "+terms +" was FOUND in document: "+counter+" with TFIDF : "+tfidf + " tf :"+tf +" idf :"+idf);
                tfidfvectors[count] = tfidf;
                count++;
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
