
import java.util.ArrayList;
import java.util.List;

import Lucene.Read_Xml;
import Lucene.Read_Xml.TextualData;
import Utils.Utilities;
import Utils.Image.ImageFilter;

public class GetCaptionForEnriched {

	 public static void main(String[] args) throws Exception {
		 List<textData> data = getRelatedCaptions();
		  
		 ExtractTextualData.writeXML("train.xml", data, "trainFigures");
		 
	 }

	public static List<textData> getRelatedCaptions() throws Exception {
		
		List<textData> related_images = new ArrayList<textData>();
		
		String train_captions_xml = "Lucene\\clef2013\\train_figures.xml";
		String databasePath = "C:\\Users\\leonidas\\Desktop\\libsvm\\databases\\Clef2016\\enriched\\TrainSet";
		
	     Read_Xml xml = new Read_Xml();
	     //i have all captions for 2013 train images
		 xml.ReadXml(train_captions_xml, null);
		 List<TextualData> images = xml.getImages();
		 
		 
		 
		 //i want all train images of Clef 2016
		 String[] trainImages = ImageFilter.getDirFiles(databasePath,Integer.MAX_VALUE,true );
		 int counter = 0;
		 //find all train images of 2013 in 2016 database
		 for(String image : trainImages){
			 String imageName = Utilities.getName(image);
			 imageName = imageName.replace(".jpg", ""); 
			 
			 int index = containsItem(images,imageName);
			 if(index != -1){
				 textData related_image = new textData();
				 related_image.imageName = imageName;
				 related_image.category = images.get(index).category;
				 related_image.caption = images.get(index).title + " "+images.get(index).caption;
				 
				 related_images.add(related_image);
				 System.out.println("found image 2013 in 2016 :"+ imageName);
				 counter++;
			 }
		 }
		 System.out.println(counter);
		 return related_images;
	}
	
	public static int containsItem(List<TextualData> images , String item){
		
		for (int i =0;i<images.size();i++){
			
			if(images.get(i).id.equals(item)){
				return i;
			}
		}
		return -1;
		
	}
	
}


