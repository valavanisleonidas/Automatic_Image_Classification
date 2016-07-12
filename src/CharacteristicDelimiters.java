import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Lucene.Read_Xml;
import Lucene.Read_Xml.TextualData;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class CharacteristicDelimiters {
	
	public static void main(String[] args) throws Exception{
		
//		 String caption = "A. A, A.... (a) (A) ( a ) ( A ) ";
//		 String letter = "A";
//		 String pattern =String.format("(\\( ?(%s|%s) ?\\))|( ?(%s.|%s.) ?)|( ?(%s,|%s,) ?)", letter,letter.toLowerCase()) ;
//
//		  // Create a Pattern object
//		  Pattern r = Pattern.compile(pattern);
//		
//		  // Now create matcher object.
//		  Matcher m = r.matcher(caption);
//		  if (m.find( )) {
//			 
//		      String replaceStr = "A_Figure";
//		      caption = m.replaceAll(replaceStr);
//		      System.out.println(caption);
//		  }
		
		
		
		String filePath = "C:\\Users\\leonidas\\Eclipse\\ImageClassification\\Lucene\\clef2016\\original\\test_figures.xml";
		Read_Xml xml = new Read_Xml();
		xml.ReadXml(filePath, null);
		List<TextualData> images = xml.getImages();
		
		System.out.println(images.size());
//		for(int i =0 ; i<ids.size();i++){
//			System.out.println(ids.get(i) +" : "+captions.get(i));
//		}
		List<subfigureCaption> subfigureCaptions = performCharacteristicDelimiters(images);		
		List<textData> data = new ArrayList<textData>();
		int counter = 0;
		for(subfigureCaption multilabel : subfigureCaptions){
			//image name category caption
			
			for(int i =0;i<multilabel.subfigures.size();i++ ){
				if(isInTest(images,multilabel.subfigures.get(i))){
					counter++;
//					System.out.println(" image does not exist: image name : "+ multilabel.subfigures.get(i) +" of parent :"+multilabel.parentCompoundId );
				}
				
				
				if(alreadyExists(data,multilabel.subfigures.get(i))){
					System.out.println("lalal111 : image name : "+ multilabel.subfigures.get(i) +" of parent :"+multilabel.parentCompoundId );
					//System.out.println(multilabel.subfiguresId_captions.get(multilabel.subfigures.get(i)));
				}
				
				//System.out.println("sentence : "+i +", caption : " + multilabel.subfiguresId_captions.get(multilabel.subfigures.get(i)));
				textData subfig_Image = new textData();
				subfig_Image.imageName = multilabel.subfigures.get(i);
				subfig_Image.category = multilabel.category;
				
				String cap = multilabel.subfiguresId_captions.get(subfig_Image.imageName);
				subfig_Image.caption = cap !=null ? cap : multilabel.caption;
				data.add(subfig_Image);
			}
			
		}
		System.out.println(counter +"  SIZEEEEE "+data.size() + " counter : ");
		ExtractTextualData.writeXML("Lucene\\clef2016\\original\\test_charDelim_Test1.xml", data, "testFigures");
	}
	
	private static boolean alreadyExists(List<textData> data, String imageID) {
		for(textData image : data){
			if(image.imageName.equals(imageID)){
				return true;
			}
			
		}
		return false;
	}

	public static boolean isInTest(List<TextualData> images , String imageName){
		for(TextualData image : images){
			if(image.id.equals(imageName)){
				return true;
			}
		}
		return false;
		
	}
	
	
	public static List<subfigureCaption> performCharacteristicDelimiters(List<TextualData> images){
		
		List<subfigureCaption> subfigure_captions = new ArrayList<subfigureCaption>();
		for(int i =0 ; i<images.size();i++){
			String imageName = images.get(i).id;
			String parentName = null;
			//remove last '-' to get parent and match image name to list to get the corresponding caption
			//get parent
			int endIndex = imageName.lastIndexOf("-");
			if(endIndex!= -1){
				parentName = imageName.substring(0, endIndex);
			}
			//for train
			if(hasToHandleExceptionalCases_Train(imageName)){
//				parentName = handleExceptionalCases_Train(imageName);
			}
			//for test
			if(hasToHandleExceptionalCases_Test(imageName)){
				parentName = handleExceptionalCases_Test(imageName);
				//System.out.println("parent name : "+parentName);
			}
			
			
//			else{
//				System.out.println("Image with id : "+imageName+" was not found!");
//				System.exit(0);
//			}
			
			
			
			if(ParentItemExists(subfigure_captions,parentName)){
				//System.out.println("Continue for parent :"+parentName);
				continue;
			}
			
			
			subfigureCaption subfigure = new subfigureCaption();
			
			subfigure.parentCompoundId = parentName;
			subfigure.caption = images.get(i).caption;
			subfigure.category = images.get(i).category;
			//find how many subfigures exist with the same parent
			List<String> subfigures = getSubfiguresOfParent(images,parentName);
			//check subfigures
			if(subfigures.size() == 0 ) {
				//System.out.println("parent " + parentName + "  "+ imageName +"   0000");
			}
			
			subfigure.subfigures.addAll(subfigures);
			subfigure_captions.add(subfigure);

			//System.out.println(subfigure.parentCompoundId+" : "+subfigures.size());
		
		}
		
		
		
		System.out.println(subfigure_captions.size());
		
		//preprocess captions
		subfigure_captions = preprocessCaptions(subfigure_captions);
		
		//extract captions
		subfigure_captions = extractSubFigureCaptions(subfigure_captions);
		int counter = 0;
		for(subfigureCaption multilabel : subfigure_captions){
			
			if(multilabel.isOk != 1){
				counter++;
				continue;
			}
			
			//System.out.println("Parent Id : "+ multilabel.parentCompoundId);
			for(int i =0;i<multilabel.subfigures.size();i++ ){
//				System.out.println("sentence : "+i +", caption : " + multilabel.subfiguresId_captions.get(multilabel.subfigures.get(i)));
		
			}
			
		}
		//System.out.println("Not ok : "+counter);
		return subfigure_captions;
	}
	
	
	public static List<subfigureCaption> extractSubFigureCaptions(List<subfigureCaption> subfigure_captions){
		
		String[] letter = new String[]{"A_Figure", "B_Figure", "C_Figure", "D_Figure", "E_Figure", "F_Figure", "G_Figure", "H_Figure", "I_Figure", "J_Figure", "K_Figure"
				, "l_Figure", "M_Figure", "N_Figure", "O_Figure", "P_Figure" , "Q_Figure", "R_Figure" , "S_Figure" , "T_Figure",
				"U_Figure" , "V_Figure" , "W_Figure" , "X_Figure" , "Y_Figure" , "Z_Figure"};
		int counter = 0;
		
		//for each parent
		for(subfigureCaption multilabel : subfigure_captions){
//			if(handleExceptionCaptions(multilabel.parentCompoundId))
//				continue;
			
//			if(!multilabel.caption.contains(letter[0]))
//				System.out.println(counter++);
			
			//System.out.println();
			//for each subfigure
			for(int i =0;i<multilabel.subfigures.size();i++ ){
				String caption = multilabel.caption;
				
				String subfigure_caption = "";
				
				// if no A_Figure found
				//put for subfigure caption whole Compound caption
				if(!caption.contains(letter[0])){
					//System.out.println("NO A_FIGURE FOUND FOR IMAGE : "+multilabel.subfigures.get(i) );
					subfigure_caption = caption;
					multilabel.subfiguresId_captions.put(multilabel.subfigures.get(i), subfigure_caption);
					continue;
				}
				
				//if (letter_caption) exists
				if(caption.contains(letter[i])){
					
					//title is always until first letter_Figure
					int FinishOfTitleIndex = caption.indexOf("_Figure") < 2 ? caption.indexOf("_Figure") : caption.indexOf("_Figure") - 2   ;
					//get title of image ( until A_Figure)
					String title = caption.substring(0, FinishOfTitleIndex);
					
					//start of wanted letter_Figure 
					int startIndexOfSubfigureSentence = caption.indexOf(letter[i]) + 9;
					
					//get from current letter_Figure the next _Figure independently from letter					
					int endIndex = startIndexOfSubfigureSentence + caption.substring(startIndexOfSubfigureSentence  ,caption.length()  ).indexOf("_Figure")   ;
					
					//if last item or next not found then caption is until the end 
					if(multilabel.subfigures.size() == i + 1 || endIndex == -1 ){
						subfigure_caption = title + " " + caption.substring(startIndexOfSubfigureSentence , caption.length());
						multilabel.subfiguresId_captions.put(multilabel.subfigures.get(i), subfigure_caption);
						continue;
					}
//					System.out.println("CAPTION : "+caption);
					
					
					
					subfigure_caption = title + " " + caption.substring(startIndexOfSubfigureSentence -1  , endIndex );
					
//					System.out.println(letter[i] +"  subfigure image: "+multilabel.subfigures.get(i)+ ",  caption: "+subfigure_caption+"  : "+startIndexOfSubfigureSentence 
//							+" "+endIndex );
					multilabel.isOk = 1;
					multilabel.subfiguresId_captions.put(multilabel.subfigures.get(i), subfigure_caption);
					
					
					
				}
				else{
					//System.out.println("not found figure "+letter[i]+" for image :" + multilabel.subfigures.get(i));
				}
				
				//System.out.println(multilabel.subfigures.get(i));
				
			}	
		}
		return subfigure_captions;
	}
	
	private static boolean handleExceptionCaptions(String imageId) {
		if(imageId.equals("1471-213X-8-2-7"))
			return true;
		else if(imageId.equals("1471-2229-10-287-5"))
			return true;
		else if(imageId.equals("1476-4598-7-34-3"))
			return true;
		return false;
	}


	public static List<subfigureCaption> preprocessCaptions(List<subfigureCaption> subfigure_captions){
		//pre-process text
		for(subfigureCaption multilabel : subfigure_captions){
			String caption = regexpCaption(multilabel.caption);
			
			multilabel.caption = caption;
			//System.out.println(multilabel.parentCompoundId +" size of sub :" +multilabel.subfigures.size() +" caption : "+multilabel.caption);
			//System.out.println("regexped caption : "+caption);
		}
		//System.out.println(subfigure_captions.size());		
		return subfigure_captions;
	}
	
	public static void StanfordnlpCaptions(){
		
		
		String paragraph = "My 1st sentence. “Does it work for questions?” My third sentence.";
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
		   String sentenceString = Sentence.listToString(sentence);
		   sentenceList.add(sentenceString.toString());
		}

		for (String sentence : sentenceList) {
		   System.out.println(sentence);
		}
	}
	
	
	public static String regexpCaption(String caption){
		 // String to be scanned to find the pattern.
		
		String[] letter = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "l", "M", "N", "O", "P" , "Q" , "R" , "S" , "T" , "U" , "V" , "W" , "X" , "Y" , "Z" };	
		 

		for(String let : letter)
		{
				
		  String pattern =String.format("\\( ?(%s|%s) ?\\)", let,let.toLowerCase()) ;

		  // Create a Pattern object
		  Pattern r = Pattern.compile(pattern);
		
		  // Now create matcher object.
		  Matcher m = r.matcher(caption);
		  if (m.find( )) {
			 
		      String replaceStr = let+"_Figure";
		      caption = m.replaceAll(replaceStr);
		      
		  } else {
//		     System.out.println("NO MATCH");
		  }
		}
		return caption;
	}
	
	public static List<String> getSubfiguresOfParent(List<TextualData> images, String parentName) {
		List<String> subfigures = new ArrayList<String>();
		for (int i =0;i<images.size();i++){
			
			
			
			if(images.get(i).id.contains(parentName+"-") || images.get(i).id.contains(parentName+"_")  ){
				if(images.get(i).id.equals("IJBI2010-308627-003-8")){
					
					System.out.println(images.get(i).id + "   going in  parent name  "+parentName);
					
				}
				subfigures.add(images.get(i).id);
			}
		}
		Collections.sort(subfigures);
		return subfigures;

	}
	
	public static boolean ParentItemExists(List<subfigureCaption> list , String item){
		for (int i =0;i<list.size();i++){
			if(list.get(i).parentCompoundId.equals(item)){
				return true;
			}
		}
		return false;
	}

	public static int ItemExists(List<String> list , String item){
		int counter = 0;
		for (int i =0;i<list.size();i++){
			if(list.get(i).equals(item)){
				counter++;
			}
		}
		return counter;
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
	
	
}

class subfigureCaption{
	
	String parentCompoundId;
	String caption;
	String category;
	int isOk = 0;
	List<String> subfigures = new ArrayList<String>();
	//each row has one subfigure with the corresponding caption 
	Map<String,String> subfiguresId_captions = new HashMap<String,String>();
	
}

