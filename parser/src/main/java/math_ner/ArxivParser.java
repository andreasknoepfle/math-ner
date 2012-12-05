package math_ner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

	private static final String COLLECTION = "raw";
	private static final String DESCRIPTION = "description";
	private static final String SUBJECT = "subject";
	private static final String RECORD = "record";
	private static final String DBNAME ="test";
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
			
		try {
			Mongo mongo = new Mongo();
			DB database = mongo.getDB(DBNAME);
			DBCollection collection = database.getCollection(COLLECTION);
			
			in = new FileInputStream(args[0]);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			String description = null;
			String subject = null;
			int i=0;
			 while (eventReader.hasNext()) {
			        XMLEvent event = eventReader.nextEvent();

			        if (event.isStartElement()) {
			          StartElement startElement = event.asStartElement();
			          if (startElement.getName().getLocalPart() == (DESCRIPTION)) {
			        	  if(description == null)
			        		  description = eventReader.getElementText();
			          }
			          if (startElement.getName().getLocalPart() == (SUBJECT)) {
			        	  subject = eventReader.getElementText();
			        	  
			          }
			        }
			        
			        if (event.isEndElement()) {
			            EndElement endElement = event.asEndElement();
			            if (endElement.getName().getLocalPart() == (RECORD)) {
			             	if(subject.toLowerCase().contains("math")) {
			             		i++;
			             		if(description != null) {
			             			BasicDBObject doc = new BasicDBObject("text", description).append("subject",subject);
			             			collection.insert(doc);
				             		description = null;
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
