package math_ner.regexer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;


public class Regexer 
{
	
	private static  String TAG_NAME_MATH_VALUE_END = "<tag name=\"MATH\" value=\"end\"/>";
	private static  String TAG_NAME_MATH_VALUE_START = "<tag name=\"MATH\" value=\"start\"/>";	
	
	private static int port = 27017;
	private static String server = "localhost";
	private static String collectionname = "raw";
	private static String dbname ="math-ner";
	private static Boolean auth = false;
	private static String user ="";
	private static String pw ="";
	
    public static void main( String[] args )
    {
    	Mongo mongo;
    	LexicalizedParser lp = LexicalizedParser.loadModel(args[0]);
		try {
			if(args.length > 1) {
				Properties properties = new Properties();
				BufferedInputStream stream;
				stream = new BufferedInputStream(new FileInputStream(args[1]));
				properties.load(stream);
				stream.close();
				port = Integer.valueOf( properties.getProperty("port"));
				server = properties.getProperty("server");
				collectionname = properties.getProperty("collection");
				dbname =properties.getProperty("dbname");
				user = properties.getProperty("user");
				pw =properties.getProperty("pw");
				auth = Boolean.valueOf(properties.getProperty("auth"));
				}
			
			
			mongo = new Mongo(server,port);
			DB database = mongo.getDB(dbname);
			if (auth)
				database.authenticate(user, pw.toCharArray());
			DBCollection collection = database.getCollection(collectionname);
			DBObject query = new BasicDBObject("text", new BasicDBObject("$exists", true));
			 DBCursor cursor = collection.find(query);
			 while( cursor.hasNext() ) {
				 DBObject obj = cursor.next();
        
				       
				        DocumentPreprocessor dp= new DocumentPreprocessor(new StringReader((String) obj.get("text")));
				        for (List<HasWord> list : dp) {
				             Tree parse = lp.apply(list);
				             String s =Sentence.listToString(labelTree(parse));
				             //Write s to regexed
				            
						}
			 }
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
    
    
    public static ArrayList<Label> labelTree(Tree tree) {
    	ArrayList<Label> sentance = new ArrayList<Label>();
    	List<Tree> children = tree.getChildrenAsList();
    	for (Tree child : children) {
			if (child.isLeaf()) {
				sentance.addAll(child.yield());
			} else if(child.value().equals("NP")) {
				//Found a Nounphrase
				sentance.addAll(regexTree(child));
			} else {
				sentance.addAll(labelTree(child));
			}
		}
    	return sentance;
    	
    }


	private static ArrayList<Label> regexTree(Tree child) {
		// Scan for deeper NPs 
		
		if(checkNP(child)) {
				//Continue Labeling
				return labelTree(child);
		}
		
	
		ArrayList<Label> list=child.yield();
		String part = Sentence.listToString(list);
		
		if(regexNP(part)) {
			if(child.getChildrenAsList().get(0).value().equals("DT")) {
				//Trim Articles
				list.add(1, new Word(TAG_NAME_MATH_VALUE_START));
			} else {
				list.add(0, new Word(TAG_NAME_MATH_VALUE_START));
			}
			list.add(new Word(TAG_NAME_MATH_VALUE_END));
			System.out.println( Sentence.listToString(list));
		}
		
		return list;
	}


	private static boolean regexNP(String part) {
		// Simple Name Regex Matcher
		if(part.matches(".*[A-Z][a-z]{3}.*")) {
			return true;
		}
		return false;
	}


	private static boolean checkNP(Tree node) {
		if(node.isLeaf()) {
			return false;
		}
		List<Tree> children = node.getChildrenAsList();
		boolean NPpresent=false;
		for (Tree child : children) {
			if(child.value().equals("NP")) {
				return true;
			} else {
				NPpresent = checkNP(child) || NPpresent;
			}
		}
		return NPpresent;
	}
}
