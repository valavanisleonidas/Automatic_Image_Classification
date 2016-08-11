package Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TRAIN 5 first images for CLEF 2013 with compound

//ID :1297-9686-41-26-2
//classs :COMP
//title :Precision of genetic parameters and breeding values estimated in marker assisted BLUP genetic evaluation
//caption :Estimated (----) additive genetic variance and variance explained by one QTL in comparison to their simulated (----) parameters when phenotypic information is used for bulls and cows . Phenotypic information corresponds to daughter yield deviations (DYD) of bulls and yield deviations (YD) of cows; Figure 2a presents results for MA-BLUP models with a short depth of pedigrees (see text for details), whereas in Figure 2b deep pedigrees were used for MA-BLUP evaluations.
//ID :1297-9686-42-13-6
//classs :COMP
//title :Heritability of longevity in Large White and Landrace sows using continuous time and grouped data models
//caption :Posterior density curves for Landrace and Large White sows . wide black line: animal model; dashed line: sire-maternal grand sire model; thin black: thin black line: sire-maternal grand sire (dam within maternal grand sire mode; dark grey line: sire model; light grey line: sire-dam model.
//ID :1423-0127-16-113-4
//classs :COMP
//title :EEVD motif of heat shock cognate protein 70 contributes to bacterial uptake by trophoblast giant cells
//caption :Binding of TPR proteins to Hsc70 . ( A ) TPR proteins binding capacity for Hsc70 with or without EEVD or scrambled EEVD (sEEVD: CAPISEDGSGETV) peptides as measured by ELISA. Immunoplates were coated with TPR-Ba, TPR-Lm1, TPR-Lm2 proteins or BSA (Cont.) and then Hsc70 was added. Data are the averages of triplicate samples from three identical experiments, and the error bars represent standard deviations. Statistically significant differences between control and TPR proteins are indicated by asterisks (*, P<  0.01). ( B ) Interaction between TPR proteins and Hsc70 interferes with bacterial uptake by TG cells. Recombinant TPR proteins or BSA (Control) were added in the culture medium of TG cells at the indicated concentration and then bacteria were deposited onto TG cells. Data are the averages of triplicate samples from three identical experiments, and the error bars represent standard deviations. Statistically significant differences between control and TPR proteins are indicated by asterisks (*, P<  0.01). ( C )  B. abortus  and  L. monocytogenes  binding capacity for Hsc70 with or without TPR proteins as measured by ELISA. Bacteria or BSA were coated on immunoplates and then TPR-Ba, TPR-Lm1, TPR-Lm2 proteins or BSA (Cont.) was added. After that, Hsc70 was added. Bacterial binding to Hsc70 were inhibited by addition of TPR proteins. Data are the averages of triplicate samples from three identical experiments, and the error bars represent standard deviations. Statistically significant differences between control and TPR proteins are indicated by asterisks (*, P<  0.01).
//ID :1423-0127-16-17-6
//classs :COMP
//title :Characterization of dengue virus entry into HepG2 cells
//caption :Silencing of Clathrin heavy chain in HepG2 cells . Multiplex RT-PCR products for GAPDH or clathrin heavy chain (CHC) of HepG2 cells either mock transfected (A); transfected with siGFP (B); transfected with siCHC3 (C) or transfected with siCHC5 (D). Samples represent day 1 to 4 post transfection and transfections were undertaken independently in triplicate.
//ID :1423-0127-16-35-4
//classs :COMP
//title :Green tea extract supplement reduces D-galactosamine-induced acute liver injury by inhibition of apoptotic and proinflammatory signaling
//caption :A: The analysis of nuclear factor NF- κ B and AP-1 from the nuclear extracts of liver samples at 6 hr of D-GalN treatment were performed with a nuclear extraction kit and using a commercially Electrophoretic-Mobility Shift Assay . The induction of nuclear factor NF- κ B and AP-1 nuclear factors by D-GalN treatment is partly inhibited by green tea (GT) pretreatment. B:  Immunoblot analyses for specific antibodies to ICAM-1, and  β -actin were performed in livers taken at 6 hr after D-GalN treatment. Note the increased expression of ICAM-1 in response to D-GalN.  C:  The enhanced expressions of these specific proteins were significantly reduced by GT pretreatment. Equal protein loading was displayed by  β -actin. *  P<  0.05 vs. control value. #  P<  0.05 vs. D-GalN treatment.




import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;
import Utils.Image.ImageFilter;

public class Read_Xml {
	  
	private List<TextualData> images = new ArrayList<TextualData>();

	
	public static void main (String [] args) throws Exception{
		
		String train = "Lucene\\clef2011\\train_figures.xml";
        String test_dir = "Lucene\\clef2011\\test_figures.xml";
		String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2011";
		String mode = "test";
		Read_Xml xml = new Read_Xml();
		xml.ReadXml(test_dir, null,databasePath,mode);
		
		for (TextualData image : xml.images) {
			System.out.println("id :"+image.id +" caption :"+ image.AllFields);
		}
		for (TextualData image : xml.images) {
			if (image.id.equals("IJBI2008-763028.007"))
			System.out.println("id :"+image.id +" caption :"+ image.AllFields);
		}
		
		
//		System.out.println(xml.getAllFields().size());
//		xml.ReOrderImagesAccordingToDatabase("E:\\leonidas\\Clef2013\\Compound","test");
//		String commonWordsPath = "Lucene\\common_words";
//		List<String> stopwords = xml.ReadStopwords(commonWordsPath, null);
		
//		String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2016\\original";
//		String[] Files = ImageFilter.getDirFiles(Utilities.getTrainTest(new File(databasePath))[1],Integer.MAX_VALUE,true );
		
//		System.out.println(Files.length);
		
	}
	
	public List<String> ReadStopwords(String filePath, Shell shell) throws IOException{
			
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
	
	public void ReadXml(String filePath, Shell shell,String databasePath,String mode) throws Exception{
		ReadXml(filePath, shell);
		ReOrderImagesAccordingToDatabase(databasePath,mode);
	}
	
	public void ReadXml(String filePath, Shell shell) throws IOException{
		   BufferedReader reader = null;	    
		 	try {
		 		reader = new BufferedReader(new FileReader(new File(filePath)));
			} catch (FileNotFoundException e1) {
				Utilities.showMessage("Could not Read File!", shell, true);
				e1.printStackTrace();
				return;
			}
			String line=null;
			String id = "";
			String category = "";
			String title = "";
			String caption = "";
			boolean previousExists = false;
			   try {
				   while ((line = reader.readLine()) != null){
					   //we found a new image
					   if(line.trim().contains("<Figure>")){
						   id = "";
						   category = "";
						   title = "";
						   caption = "";
						   
						   TextualData image = new TextualData();
						   
						   line = reader.readLine();
						   if(line.contains("ID"))
						   {
							   id = getField(line,"ID",false,null); 
							   //ID
							   if(id.equals("end"))
								   continue;
							   image.id = id;
							   previousExists = true;
						   }
						   if(previousExists){
							   line = reader.readLine();
							   previousExists = false;
						   }
						   if(line.contains("CLASS"))
						   {
							   category = getField(line,"CLASS",false,null); 						   
							   //category
							   if(category.equals("end")){
								   continue;
							   }
							   image.category = category;
							   previousExists = true;
						   }
						
						   if(previousExists){
							   line = reader.readLine();
							   previousExists = false;
						   }
						   
						   if(line.contains("TITLE"))
						   {
							   title = getField(line,"TITLE",false,null); 
							   //title
							   if(title.equals("end")){
								   continue;
							   }
							   image.title = title;
							   previousExists = true;
						   }
						   //caption may be many lineeees 
						   //CLEF2013 : test image is one less because in line 2508 there is only class and no caption 
						   if(previousExists){
							   line = reader.readLine();
							   previousExists = false;
						   }
						  
						   
						   if(line.contains("CAPTION"))
						   {
							   caption = getField(line,"CAPTION",true,reader); 
							   if(caption.equals("end")){
								   image.AllFields = title;
								   continue;
							   }
							   image.caption = caption;
							   
							   previousExists = true;
						   }
						   image.AllFields = title+" "+ caption ;
							
						   images.add(image);
						   //System.out.println(image.id);
						   //System.out.println(id +" "+category +" "+title+" "+ caption);
					  }
				   }
			   }catch(Exception e){
					Utilities.showMessage("Error reading file!", shell, true);
					e.printStackTrace();
					reader.close();
		 			return;
			   }
			   try {
				   reader.close();
			   }catch (FileNotFoundException e) {
					Utilities.showMessage("Could not close File!", shell, true);
					e.printStackTrace();
		 			return;
			   }
		
		
		
	}

	public String getField(String line , String field,boolean checkBelowLines,BufferedReader reader) throws Exception{
		if(checkBelowLines){
			//if starter line does not contain field then return
			if(!line.contains("<"+field+">"))
				return "";
			
			//if it does contain end of field return normally
			if(line.contains("</"+field+">"))
				return line.replace("<"+field+">","").replace("</"+field+">", "");
			
			//if it contains end of image
			if(line.contains("</Figure>"))
				return "end";
			
			
			String caption=line.replace("<"+field+">","")+" ";
			
			while(!(line = reader.readLine()).contains("</"+field+">")){
				caption = caption + line.replace("</"+field+">", "");
			}	
			return caption;		
		}else{
			if(line.contains("</Figure>"))
				return "end";
			return line.contains(field)? line.replace("<"+field+">","").replace("</"+field+">", "") : "";
		}
	}
	
	public void ReOrderImagesAccordingToDatabase(String databasePath,String mode) throws Exception{
		String[] Files;
		if(mode.equals("test"))
			Files = ImageFilter.getDirFiles(Utilities.getTrainTest(new File(databasePath))[1],Integer.MAX_VALUE,true );
		else
			Files = ImageFilter.getDirFiles(Utilities.getTrainTest(new File(databasePath))[0],Integer.MAX_VALUE,true );
		
		for(int i =0;i<images.size();i++){
			
 			String imageName = Utilities.getName(Files[i]).replace(".jpg", "");
 			
 			int index = findIndexInCategory(imageName);
 			//System.out.println(imageName +" index found : "+index);
 			
 			//no need to change 
 			if(index == i) continue;
 			
 			if(index == -1){
 				System.out.println("file was not found :"+imageName);
 			}
 			swapElements(i,index);
 		}
	}
	
	//index in category : wrong index 
	private void swapElements(int index_to_change, int indexInCategory) {
		
		TextualData temp = images.get(indexInCategory);
		images.set(indexInCategory, images.get(index_to_change));
		images.set(index_to_change, temp);
	}

	private int findIndexInCategory(String imageName) {
		for(int i=0; i<images.size();i++){
			if(images.get(i).id.equals(imageName))
				return i;
		}	
		return -1;
	}

	public List<TextualData> getImages() {
		return images;
	}
	

	public void setImages(List<TextualData> images) {
		this.images = images;
	}

	public class TextualData{
		public String id,category,caption="",title="",AllFields="";
	}
	
	
	
	
}
