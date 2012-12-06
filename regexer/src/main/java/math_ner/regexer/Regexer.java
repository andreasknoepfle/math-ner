package math_ner.regexer;

import java.io.IOException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Hello world!
 *
 */
public class Regexer 
{
	private static final String COLLECTION = "raw";
	private static final String DBNAME ="test";
	
    public static void main( String[] args )
    {
        System.out.println( "Starting Regexer..." );
        //AlgorithmRegexer algo_regex = new AlgorithmRegexer();
        
        try {
			MaxentTagger tagger = new MaxentTagger("/home/flyover/Downloads/stanford-postagger-2011-04-20/models/left3words-wsj-0-18.tagger");
			
			// The sample string
			 
			String sample = "This is a sample text";
			 
			// The tagged string
			 
			String tagged = tagger.tagString(sample);
			 
			// Output the result
			 
			System.out.println(tagged);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
