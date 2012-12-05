package math_ner;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;

import edu.stanford.nlp.annotation.Annotator;

public class Tagger {
	private static JTextPane tp=null; 
	private static final String COLLECTION = "raw";
	private static final String DBNAME ="test";
	public static String returnText= null;
	private final static Object lock = new Object();
	private static JFrame frame;
	
	public static void main(String[] args) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB database = mongo.getDB(DBNAME);
			DBCollection collection = database.getCollection(COLLECTION);
			DBObject query = new BasicDBObject("annotated", new BasicDBObject("$exists", false));
			 DBCursor cursor = collection.find(query);
			 while( cursor.hasNext() ) {
				 DBObject obj = cursor.next();
				 openEditor(args,(String)obj.get("text"));
				synchronized (lock) {
					while(returnText==null){
						lock.wait();
					}
					
				}
				obj.put("annotated", returnText);
				collection.save(obj);
				returnText=null;
			 }
			 
			  
			    
			 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void openEditor(String[] args,String content) throws NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		
		WindowListener l = new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		    	  frame.setVisible(false);
		    	  synchronized (lock) {
		    		  
		    		  returnText = tp.getText();
			    	  lock.notify();
		    	  }
		    	 
		    	  
		    	  
		      }
		    };
		    frame = new Annotator(args[0], l);
		   
		    Field textpane = Annotator.class.
		            getDeclaredField("tp");

		    textpane.setAccessible(true);
		    tp=(JTextPane) textpane.get(frame);
		    tp.setText(content);
		    frame.pack();
		    frame.setVisible(true);
		    
		 
		    
	}
}
