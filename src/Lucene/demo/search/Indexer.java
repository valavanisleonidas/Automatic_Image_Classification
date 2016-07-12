/*
 * Indexer.java
 *
 * Created on 6 March 2006, 13:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Lucene.demo.search;

import java.io.IOException;
import java.io.File;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field; 
import org.apache.lucene.document.StringField; 
import org.apache.lucene.document.TextField; 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import Lucene.Read_Xml;

/**
 *
 * @author John
 */
public class Indexer {
    

	public static final String FILES_TO_INDEX_DIRECTORY = "Lucene\\train_figures.xml";
	public static final String index_path = "Lucene\\index";
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    private IndexWriter indexWriter = null;
    
    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File(index_path));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
   }    
   
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
   }
    
    public void indexDocs() throws IOException {
    	
//    	Read_Xml xml = new Read_Xml();
//		xml.ReadXml(FILES_TO_INDEX_DIRECTORY, null);
//
//		IndexWriter writer = getIndexWriter(false);
//		for (int i = 0 ; i < xml.getCaption().size();i++){
////			System.out.println("Indexing image with id: " +xml.getID().get(i) );
////			System.out.println("Indexing image with title : " +xml.getTitle().get(i) );
//			
//			Document document = new Document();
//			
//			document.add(new StringField("ID", xml.getID().get(i), Field.Store.YES) );
//			document.add(new StringField("Title", xml.getTitle().get(i), Field.Store.YES) );
//			document.add(new StringField("Category", xml.getCategory().get(i), Field.Store.YES));
//			document.add(new StringField("Caption", xml.getCaption().get(i), Field.Store.YES));
//			
//			String fullSearchableText = xml.getID().get(i) + " " + xml.getCategory().get(i) + " "+
//			xml.getTitle().get(i) + xml.getCaption().get(i);
//	
//			document.add(new TextField("content", fullSearchableText, Field.Store.NO));
//
//	        writer.addDocument(document);
//		}
//    	
       
    }   
    
    public void buildIndex() throws IOException {
          
          // Erase existing index
          getIndexWriter(true);
          
          // Index all Accommodation entries
          indexDocs();              
          
          // close the index writer when done
          closeIndexWriter();
     }    
}
