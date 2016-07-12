import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.print.DocFlavor.STRING;

import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;
import Utils.Image.ImageFilter;

public class ExtractTextualData {

	public static void main(String[] args) throws Exception{
//		String file_to_read = "E:\\leonidas\\CompoundFigureDetectionTest2016-Captions.txt";
//		String path_to_read_images  = "E:\\leonidas\\clef2016\\TestSet";
//		
		String file_to_read = "E:\\leonidas\\CompoundFigureDetectionTraining2016-Captions.txt";
		String path_to_read_images  = "E:\\leonidas\\clef2016\\SubfigureClassificationTr_aining2016";
		
		List<textData> textual_data =  readFromFile(file_to_read,null);
		//for(textData image : textual_data){
		//	System.out.println("image name "+image.imageName+" , caption "+image.caption);
		//}
		
		//theloume na diavasoume tis dikes mas eikones subfigure kai na tis kanoume match me tis eikones tou compound
		List<textData> finaldata = getDataForOurImages(path_to_read_images,textual_data);

//		for(textData image : finaldata){
//		//	System.out.println("image name "+image.imageName+" , parent name : "+image.parentName+" , category "+image.category+"  , caption "+image.caption);
//
//			if(image.caption.equals(""))
//				break;
//		}
		String filePath = "E:\\leonidas\\train_figures.xml";
		String rootElement = "TrainFigures";
		writeXML(filePath,finaldata,rootElement);
		
	
	}


	public static void writeXML(String filePath, List<textData> finaldata,String rootElement) throws Exception {
		
		BufferedWriter writer = Utilities.openFilesForWriting(filePath, null);
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");//arxika stoixeia xml
		writer.write("<"+rootElement+">\n");//root element
				  	
		 for(textData image : finaldata){
			 
			 writer.write("<Figure>\n");
			 writer.write("<ID>"+image.imageName+"</ID>\n");
			 writer.write("<CLASS>"+image.category+"</CLASS>\n");
			 writer.write("<CAPTION>"+image.caption+"</CAPTION>\n");
			 writer.write("</Figure>\n");
			 
		 }
		 writer.write("</"+rootElement+">\n");
		 
		 
		 
		 try {
			 writer.close();	
		 }catch (FileNotFoundException e) {
			 Utilities.showMessage("Could not close File!", null, true);
			 return;
		 }
		
	}


	public static List<textData> getDataForOurImages(String Dirpath, List<textData> allTextualData) {
		List<textData> dataSetImages = new ArrayList<textData>();
		
		String[] fileNames = ImageFilter.getDirFiles(Dirpath,Integer.MAX_VALUE,true );
		for(int i=0; i < fileNames.length;i++){
			textData data = new textData();
			
			String imageName = Utilities.getName(fileNames[i]).replace(".jpg", "");
			String category=Utilities.GetParentFolder(fileNames[i]).split("\\.")[1];
			data.imageName = imageName;
			data.category = category;
			
			//remove last '-' to get parent and match image name to list to get the corresponding caption
			//get parent
			int endIndex = imageName.lastIndexOf("-");
			if(endIndex!= -1){
				data.parentName = imageName.substring(0, endIndex);
			}
			
			//for test
			if(hasToHandleExceptionalCases_Test(data.imageName)){
			//	data.parentName = handleExceptionalCases_Test(data.imageName);
			}
//			//for train
			if(hasToHandleExceptionalCases_Train(data.imageName)){
				data.parentName = handleExceptionalCases_Train(data.imageName);
			}
			
			data.caption = findCaption(data.parentName,allTextualData);
			if(data.caption.equals("")){
				System.out.println("image name "+data.imageName+" , parent name : "+data.parentName+" , category "+data.category+"  , caption "+data.caption);
			}
			
			dataSetImages.add(data);
			
			//System.out.println("image name "+data.imageName+" , parent name : "+data.parentName+" , category "+data.category+"  , caption "+data.caption);
		}
		
		return dataSetImages;
		
	}
	
	public static boolean hasToHandleExceptionalCases_Test(String imageName){
		if(imageName.equals("IJBI2010-308627-003-8"))
			return true;
		else if(imageName.equals("IJBI2010-535329-1.015") || imageName.equals("IJBI2010-535329-2.015") || imageName.equals("IJBI2010-535329-5.015"))
			return true;
		else if(imageName.equals("1471-2105-6-S2-S11-7_1") || imageName.equals("1471-2105-6-S2-S11-7_2") || imageName.equals("1471-2105-6-S2-S11-7_3") || 
				imageName.equals("1471-2105-6-S2-S11-7_4") || imageName.equals("1471-2105-6-S2-S11-7_5") || imageName.equals("1471-2105-6-S2-S11-7_6") ||
				imageName.equals("1471-2105-6-S2-S11-7_7") || imageName.equals("1471-2105-6-S2-S11-7_8") || imageName.equals("1471-2105-6-S2-S11-7_9") ||
				imageName.equals("1471-2105-6-S2-S11-7_10"))
			return true;
		else if(imageName.equals("IJBI2010-535329-3.015") ||  imageName.equals("IJBI2010-535329-4.015"))
			return true;
		
		return false;
	}
	
	public static String handleExceptionalCases_Test(String imageName){
		if(imageName.equals("IJBI2010-308627-003-8"))
			return "IJBI2010-308627.003";
		else if(imageName.equals("IJBI2010-535329-1.015") || imageName.equals("IJBI2010-535329-2.015") || imageName.equals("IJBI2010-535329-5.015"))
			return "IJBI2010-535329.015";
		else if(imageName.equals("1471-2105-6-S2-S11-7_1") || imageName.equals("1471-2105-6-S2-S11-7_2") || imageName.equals("1471-2105-6-S2-S11-7_3") || 
				imageName.equals("1471-2105-6-S2-S11-7_4") || imageName.equals("1471-2105-6-S2-S11-7_5") || imageName.equals("1471-2105-6-S2-S11-7_6") ||
				imageName.equals("1471-2105-6-S2-S11-7_7") || imageName.equals("1471-2105-6-S2-S11-7_8") || imageName.equals("1471-2105-6-S2-S11-7_9") ||
				imageName.equals("1471-2105-6-S2-S11-7_10"))
			return "1471-2105-6-S2-S11-7";
		else if(imageName.equals("IJBI2010-535329-3.015") ||  imageName.equals("IJBI2010-535329-4.015"))
			return "IJBI2010-535329.015";
		
		return null;
	}
	
	public static boolean hasToHandleExceptionalCases_Train(String imageName){
		if(imageName.equals("DRP2011-927852-001-2"))
			return true;
		
		
		return false;
	}
	
	public static String handleExceptionalCases_Train(String imageName){
		if(imageName.equals("DRP2011-927852-001-2"))
			return "DRP2011-927852.001";
		
		
		return null;
	}
	
	
	public static String findCaption(String parentName, List<textData> allTextualData ){
		
		for(textData data : allTextualData){
			if(data.imageName.equals(parentName)){
				return data.caption;
				
			}
				
		}
		
		return "";
	}


	public static List<textData> readFromFile(String filePath,Shell shell) throws Exception {

		List<textData> images=new ArrayList<textData>();
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
						  StringTokenizer tokenizedLine = new StringTokenizer(line,"	");
						  
						  textData image = new textData();
						  image.imageName = tokenizedLine.nextToken().toString();
						  image.caption = tokenizedLine.nextToken().toString();
						  
						  images.add(image);
						  	   
							   		 
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
			   return images;
	}
	
	
	
	
}

class textData{
	String imageName;
	String parentName;
	String caption;
	String category;
	
	
}
