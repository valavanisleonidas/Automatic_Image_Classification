import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;

public class TestNoSet_toSet {

	public static void main(String[] args) throws Exception{
		String test_GT = "E:\\leonidas\\subfigureTestSetGT.txt";
		String path_to_read  = "E:\\leonidas\\clef2016\\SubfigureClassificationTest2016";
		String path_to_write= "E:\\leonidas\\test_clef";
		Map<String,String> golden_table =  readFromFile(test_GT,null);
		//for(Map.Entry<String, String> entry : golden_table.entrySet()){
		//	System.out.println(entry.getKey()+ " : "+entry.getValue());
		//}
		
		createSetofImages(golden_table,path_to_read, path_to_write);
		System.out.println(golden_table.size());
		
	}
	
	
	public static void createSetofImages(Map<String,String> golden_table,String path_to_read, String path_to_write) throws IOException {
		int counter = 0;
		for(Map.Entry<String, String> entry : golden_table.entrySet()){
			File source_folder = new File(path_to_read);
			File test_folder = new File(path_to_write+"\\"+entry.getValue());
			if(!test_folder.exists())
				test_folder.mkdirs();
			
			System.out.println(counter++);
			
			File sourceImage = new File(source_folder.getAbsolutePath()+"\\"+entry.getKey()+".jpg");
			File dest_image = new File(test_folder.getAbsolutePath()+"\\"+entry.getKey()+".jpg");
			
			org.apache.commons.io.FileUtils.copyFile(sourceImage, dest_image );
			
			
			//System.out.println(entry.getKey()+ " : "+entry.getValue());
			
			
		}
		
		
		
	}


	public static Map<String,String> readFromFile(String filePath,Shell shell) throws Exception {

			Map<String,String> map=new HashMap<String,String>();
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
							  
						 // System.out.println(tokenizedLine.countTokens());
						  
						  map.put(tokenizedLine.nextToken().toString(), tokenizedLine.nextToken().toString());
							   
							   		 
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
			   return map;
	}
	
	
	
	
}
