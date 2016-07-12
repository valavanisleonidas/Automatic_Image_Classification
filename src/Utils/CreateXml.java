package Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import Utils.Image.ImageFilter;

public class CreateXml {

	private BufferedWriter XMLwriter=null;
    private String PathOfKeywords;
	private Shell shell;
	
    public boolean CreateXML(String DBSourcePath,String xmlPath,String keywordsPath,Shell shell) throws Exception{
		this.shell=shell;
		PathOfKeywords=keywordsPath;
		
		File DBdir = new File(DBSourcePath);	 	
		
		XMLwriter = Utilities.openFilesForWriting(xmlPath, shell);
		
	  	XMLwriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");//arxika stoixeia xml
	  	XMLwriter.write("<corel5k>\n");//root element
		
	  	//if any folder is named with train or test those are the folders that the xml will take into consideration for the images
	  	//[0] : train  , [1] test
   		String[] filaNames =Utilities.getTrainTest(DBdir);
   		if(filaNames[0]==null && filaNames[1]==null){
	  		MessageDialog.openError(shell, "Error", "No train-test in images folder");
 			return false;
	  	}
   		XML(filaNames[1]);
  		XML(filaNames[0]);
		
  		XMLwriter.write("</corel5k>");
		XMLwriter.close(); 

	  	return true;
	}  	

    private void XML(String path) throws Exception{
    	String[] Files= ImageFilter.getDirFiles(path,Integer.MAX_VALUE,true );
  		for(int i=0;i<Files.length;i++){
  			WriteXML(new File(Files[i].toString())); // grapse sto xml
    		System.out.println(Files[i].toString());	
  		}
  		Files=null;
    	
    }
    

	private void WriteXML(File image) throws Exception {
		// write properties of each image
		XMLwriter.write("	<Image>\n");
		XMLwriter.write("		<ID>"+image.getName().split("\\.")[0]+"</ID>\n");
		XMLwriter.write("		<ImagePath>"+image.getAbsolutePath()+"</ImagePath>\n");

		String parent=Utilities.GetParentFolder(image.getAbsolutePath());
		XMLwriter.write("		<Type>"+parent.split("\\.")[0]+"</Type>\n");//get type of image
		XMLwriter.write("		<Category>"+parent.split("\\.")[1]+"</Category>\n");//get category of image
		
		//if there are keywords
		if(PathOfKeywords!=null)
				ReadKeywords(XMLwriter,image,PathOfKeywords);

		XMLwriter.write("	</Image>\n");		
	}
	
	//reads text with keywords and writes the category type etc of every image
	private void ReadKeywords(BufferedWriter writer, File image,String KeywordsPath) throws IOException{
		 BufferedReader in = null;
		 try {
				in = new BufferedReader(new FileReader(KeywordsPath));
		 } 
		 catch (FileNotFoundException e1) {
	 			MessageDialog.openError(shell, "Error","File Not Found!");
		 }
		 
		  writer.write("		<Keywords>");
		  String line=null;
		  try {
			   	while ((line = in.readLine()) != null) { 
				    StringTokenizer token=new StringTokenizer(line);
				  
				   if (token.nextToken().equals(image.getName().split("\\.")[0])){
					  
				   		while(token.hasMoreElements()){
				   			if(token.countTokens()!=1)
				   				writer.write(token.nextToken()+" ");
				   			else  
				   				writer.write(token.nextToken());
				   		}
					   	  break;
				   }
			   }
		   }
		   catch(Exception e){
	 			MessageDialog.openError(shell, "Error","Error reading File");
		   }
		   writer.write("</Keywords>\n");
		   in.close();
	}
	

	
	
}