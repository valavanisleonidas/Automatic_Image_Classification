/*
 * Main.java
 *
 * Created on 6 March 2006, 11:51
 *
 */

package Lucene.demo;


import Lucene.demo.search.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author Leonidas
 */
public class Main {
	public static final String index_path = "Lucene\\index";

	
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws Exception {
//    	search();
    	getTerms();
    }
    
    public static void search(){
    	
    	 try {
    	    	// build a lucene index
    	        System.out.println("Building Index");
    	        Indexer  indexer = new Indexer();
    	        indexer.buildIndex();
    	        System.out.println("build Index done");
    	       
    	        // perform search and retrieve the top N results
    	        int results = 10;
    	        System.out.println("performSearch");
    	        SearchEngine se = new SearchEngine();
    	        TopDocs topDocs = se.performSearch("Comp", results);

    	        System.out.println("Results found: " + topDocs.totalHits);
    	        ScoreDoc[] hits = topDocs.scoreDocs;
    	        for (int i = 0; i < hits.length; i++) {
    	            Document doc = se.getDocument(hits[i].doc);
    	            System.out.println(doc.get("ID")
    	                               + " (" + hits[i].score + ")");

    	        }
    	        System.out.println("performSearch done");
    	      } catch (Exception e) {
    	        System.out.println("Exception caught.\n");
    	      }
    	
    }

    public static void getTerms() throws Exception{
    	// build a lucene index
        System.out.println("Building Index");
        Indexer  indexer = new Indexer();
        indexer.buildIndex();
        System.out.println("build Index done");
    	
        Directory index = FSDirectory.open(new File(index_path));
        IndexReader reader = DirectoryReader.open(index);
        
        getTfidf(reader,"content");
        
        
    }
    
    
    //22053 words
    public static void getTfidf(IndexReader reader, String field) throws Exception {

    	scoreCalculator(reader, field); 
//    	scoreCalculator_2(reader, field); 
    }
    
    
    public static void scoreCalculator_2(IndexReader reader,String field) throws IOException 
    {	
    	  TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator(null);
          BytesRef term = null;
          TFIDFSimilarity tfidfSim = new DefaultSimilarity();
          int docCount = reader.numDocs();
            
          
          while ((term = termEnum.next()) != null) {
          	
              Term termInstance = new Term(field, term);
              // term and doc frequency in all documents
              long indexTf = reader.totalTermFreq(termInstance); 
              long indexDf = reader.docFreq(termInstance);       
              
              double tfidf = tfidfSim.tf(indexTf) * tfidfSim.idf(indexDf,docCount );
          
//              System.out.println("field:"+field+ ", term : "+term.utf8ToString() 
//              		+ ", iDF :" +tfidfSim.idf(indexDf,docCount )+ ", TF :" +tfidfSim.tf(indexTf) + " tfidf :" +tfidf );

          }
    	
    	
    }

    
    public static void scoreCalculator(IndexReader reader,String field) throws IOException 
	 { 
	         TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
	         Bits liveDocs = MultiFields.getLiveDocs(reader);
	         TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator(null);
	         BytesRef term;
	         int docCount = reader.numDocs();
	         
	         while ((term = termEnum.next()) != null) {
	             Term termInstance = new Term(field, term);
	             long indexDf = reader.docFreq(termInstance);      
	             DocsEnum docs = termEnum.docs(liveDocs,null);

	             while(docs.nextDoc() != DocsEnum.NO_MORE_DOCS) {
	                 double tfidf = tfidfSIM.tf(docs.freq()) * tfidfSIM.idf(indexDf , docCount);
//	                 System.out.println("tfidf :"+tfidf+" for term :"+term.utf8ToString());
	             }

	         }
	         
//	         while ((term = termEnum.next()) != null) {           
//	                        
//	             if (!termEnum.seekExact(term)) 
//	            	 continue;
//            	  
//            	 float idf = tfidfSIM.idf(termEnum.docFreq(), reader.numDocs());
//            	 
//    	         DocsEnum docsEnum = termEnum.docs(liveDocs, null);
//                 if (docsEnum == null) 
//                	 continue;
//                 
//                 int doc; 
//                 while((doc = docsEnum.nextDoc())!=DocIdSetIterator.NO_MORE_DOCS){
//                	 float tf = tfidfSIM.tf(docsEnum.freq());
//                	 float tfidf_score = tf*idf; 
//                	 System.out.println("term : "+ term.utf8ToString() +" tf : "+ tf + " idf : "+ idf +" tfidf_score : " + tfidf_score);
//                 }
//	                 
//	              
//	         }
	        
	 }
    
 
    
    
 
    
}
