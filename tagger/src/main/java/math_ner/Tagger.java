package math_ner;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import edu.stanford.nlp.annotation.Annotator;

public class Tagger {
	
	private static JTextPane tp=null; 
	
	private static int port = 27017;
	private static String server = "localhost";
	private static String collectionname = "raw";
	private static String dbname ="math-ner";
	private static Boolean auth = false;
	private static String user ="";
	private static String pw ="";
	
	public static String returnText= null;
	private final static Object lock = new Object();
	private static JFrame frame;
	
	public static void main(String[] args) {
		Mongo mongo;
		try {
			if(args.length > 2) {
				Properties properties = new Properties();
				BufferedInputStream stream;
				stream = new BufferedInputStream(new FileInputStream(args[2]));
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
				obj.put("annotateUser", args[1]);
				
				collection.save(obj);
				System.out.println(obj.get("_id"));
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
		} catch (IOException e) {
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
