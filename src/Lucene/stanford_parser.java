package Lucene;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.WordToSentenceProcessor;

public class stanford_parser {
	//log a warning for none, the first, or all, 
	//and whether to delete them or to include them as single character tokens in the output: 
	//noneDelete, firstDelete, allDelete, noneKeep, firstKeep, allKeep.
    private final static TokenizerFactory<CoreLabel> tokenizerFactory =
    		PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true,untokenizable=noneKeep");
	
	public static void main(String[] args){
	

		String parg = "Interaction of rs12413112  SIRT1  genotype with the metabolic responses during a controlled 9-month"
				+ " lifestyle intervention . Rs12413112  SIRT1  genotype (GG): filled circles ( â—? ); X/A: opened circles ( â—‹ ). a) "
				+ "Response of plasma glucose determined during the fasting state. b) Response of insulin sensitivity estimated by the"
				+ " formula from Matsuda et al. [ 12 ]. c) Response of liver fat content, determined by magnetic resonance spectroscopy."
				+ " For statistical analyses, all data were log-transformed and adjusted for age, sex, follow-up-time, BMI at"
				+ " both baseline and follow-up and the corresponding variable (plasma glucose, insulin sensitivity or liver fat)"
				+ " measured at baseline. Data are presented as means+SEM (G/G: n = 152, X/A: n = 45 TULIP participants).";

		

//		String[] words = SentenceToWords(parg);

		
		String[] words = tokenize(parg);

		for(String word : words){
			System.out.println(word);
		}
	
		
	}
	
	public static String[] tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer = tokenizerFactory.getTokenizer(new StringReader(str));    
        List<CoreLabel> corelabels= tokenizer.tokenize();
        List<String> words= new ArrayList<String>();
		
		for (CoreLabel word: corelabels) {
				words.add(word.toString()) ;
		}
	
        
		return (String[]) words.toArray(new String[words.size()]);
	}
	
	public static List<String> wordsToSentences(String text){
		PTBTokenizer ptbt = new PTBTokenizer(new StringReader(text), new CoreLabelTokenFactory(), "ptb3Escaping=false");
		List<List<CoreLabel>> sents = (new WordToSentenceProcessor()).process(ptbt.tokenize());
		
		List<String> sentences = new ArrayList<String>();
		
		for (List<CoreLabel> sent : sents) {
			
			StringBuilder sb = new StringBuilder("");
			for (CoreLabel w : sent){
				sb.append(w + " ");
			}
			sentences.add(sb.toString());
		}
		return sentences;
	}

	
			
}

