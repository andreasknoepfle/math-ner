package math_ner;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class Query {

	private static int port = 27017;
	private static String server = "localhost";
	private static String collectionname = "raw";
	private static String dbname ="math-ner";
	private static Boolean auth = false;
	private static String user ="";
	private static String pw ="";

public static void main(String[] args) {
	Mongo mongo;
	try {
		Properties queryprops = new Properties();
		BufferedInputStream stream;
		stream = new BufferedInputStream(new FileInputStream(args[0]));
		queryprops.load(stream);
		stream.close();
		if(args.length > 1) {
			Properties properties = new Properties();
			
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
		String dbfield = queryprops.getProperty("data");
		DBObject query = new BasicDBObject(dbfield, new BasicDBObject("$exists", true));
		DBCursor cursor = collection.find(query);
		 
		 // NER or Regexer
		 Boolean ner = Boolean.valueOf(queryprops.getProperty("ner"));
		 
		 FileWriter testfile = new FileWriter(queryprops.getProperty("testfile"));
		 BufferedWriter out_testfile = new BufferedWriter(testfile);

		 FileWriter trainfile = new FileWriter(queryprops.getProperty("trainfile"));
		 BufferedWriter  out_trainfile = new BufferedWriter(trainfile);
		 
		 
		 // For spitting 50-50
		 int count_user1= 0;
		 int count_user2= 0;
		 
		 while( cursor.hasNext() ) {
			 DBObject obj = cursor.next();
			 if(ner) {
				 if(obj.get("annotateUser").equals(queryprops.getProperty("user"))) {
					 if(count_user1%2==0) {
						 out_trainfile.write((String)obj.get(dbfield));
					 } else {
						 out_testfile.write((String)obj.get(dbfield));
					 }
					 count_user1++;
				 }
				 if(obj.get("annotateUser").equals(queryprops.getProperty("user2"))) {
					 if(count_user2%2==0) {
						 out_trainfile.write((String)obj.get(dbfield));
					 } else {
						 out_testfile.write((String)obj.get(dbfield));
					 }
					 count_user2++;
				 }
			 } else {
				 if(obj.get("annotateUser").equals(queryprops.getProperty("user"))) {
						out_testfile.write((String)obj.get(dbfield));
						out_trainfile.write((String)obj.get(queryprops.getProperty("data2")));
				 }
				 
			 }
				 
		 }
		 
		 out_testfile.close();
		 out_trainfile.close();
		 
		  
		    
		 
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
}
