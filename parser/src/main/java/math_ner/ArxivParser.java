package math_ner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;


public class ArxivParser {

	private static int port = 27017;
	private static String server = "localhost";
	private static String collectionname = "raw";
	private static String dbname ="math-ner";
	private static Boolean auth = false;
	private static String user ="";
	private static String pw ="";
	
	private static final String DESCRIPTION = "description";
	private static final String SUBJECT = "subject";
	private static final String RECORD = "record";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in;
		if( args.length < 1)
		{
			System.err.println("Specify XML Input File");
			System.exit(-1);
		}
		
		System.out.println("Start importing....");
			
		Mongo mongo;
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
			
			in = new FileInputStream(args[0]);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			String description = null;
			String subject = null;
			int i=0;
			 while (eventReader.hasNext()) {
			        XMLEvent event = eventReader.nextEvent();

			        if (event.isStartElement()) {
			          StartElement startElement = event.asStartElement();
			          if (startElement.getName().getLocalPart().equals(DESCRIPTION)) {
			        	  if(description == null)
			        		  description = eventReader.getElementText();
			          }
			          if (startElement.getName().getLocalPart().equals(SUBJECT)) {
			        	  if(subject == null)
			        	  {
			        		  subject = eventReader.getElementText();
			        		 if(!subject.toLowerCase().contains("math")) {
			        			 subject=null;
			        		 }
			        	  }
			        	  
			          }
			          if (startElement.getName().getLocalPart().equals(RECORD)) {
			        	  description = null;
			        	  subject = null;
			          }
			        }
			        
			        if (event.isEndElement()) {
			            EndElement endElement = event.asEndElement();
			            if (endElement.getName().getLocalPart() == (RECORD)) {
			             	if(subject != null && subject.toLowerCase().contains("math")) {
			             		
			             		if(description != null) {
			             			BasicDBObject doc = new BasicDBObject("text", description).append("subject",subject);
			             			collection.insert(doc);
				             		description = null;
				             		subject = null;
				             		i++;
			             		}
			             	}
			            }
			          }
			          
			    }
			
			 System.out.println("Count "+i);
			 System.out.println("Finished Importing");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	      

	}

}
