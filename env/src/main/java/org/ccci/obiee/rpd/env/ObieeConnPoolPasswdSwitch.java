package org.ccci.obiee.rpd.env;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ObieeConnPoolPasswdSwitch 
{
	public static void main(String[] args) throws IOException 
	{	    
	    Properties props = new Properties();

		 try 
		 {
		     //Not used yet
		     URL url = ObieeConnPoolPasswdSwitch.class.getClassLoader().getResource("ObieeRpdXmlFiles.properties");
		     props.load(url.openStream());
		     //Not used yet
		     
		     mergeRpdCredentialsWithChangesXML(retrieveConnPoolCreds(props));
		 } 
		 catch (IOException e)
		 {			
			 System.err.println( "Caught an IOException: "  + e.getMessage());
			    throw new IOException(e);			 
		 }				
	}

	private static Map<String, String> retrieveConnPoolCreds(Properties props) 
	{
	    Map<String,String> mapConnectionPoolUsers = new HashMap<String,String>();
	    
	    try {

	    	Document doc = getRpdXMLDocument(new File("C:\\files\\OracleBIAnalyticsAppsStage.xml"));	    	
	    	
	    	doc.getDocumentElement().normalize();			 
	    	NodeList nList = doc.getElementsByTagName("ConnectionPool");

	    	for (int temp = 0; temp < nList.getLength(); temp++) 
	    	{
	    		Node nNode = nList.item(temp);

	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	    		{
	    			Element eElement = (Element) nNode;
	    			mapConnectionPoolUsers.put(eElement.getAttribute("parentName") ,eElement.getAttribute("password") );
	    		}
	    	}
	    	
	    	
	    } 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    
	    return mapConnectionPoolUsers;
	}
  
	private static void mergeRpdCredentialsWithChangesXML(Map<String,String> map)
	{	
		try {
			
			StreamResult result = new StreamResult(new File("C:\\files\\OracleBIAnalyticsApps.xml"));
			Document xmlDoc = getRpdXMLDocument(new File("C:\\files\\OracleBIAnalyticsAppsTest.xml"));				
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
						
			xmlDoc.getDocumentElement().normalize();			 
			NodeList nList = xmlDoc.getElementsByTagName("ConnectionPool");
						
			for (int temp = 0; temp < nList.getLength(); temp++) 
			{
				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					for(Map.Entry<String, String>entry: map.entrySet())		
					{			
						if( eElement.getAttribute("parentName").equals(entry.getKey()) )
						{		
							eElement.setAttribute(eElement.getAttribute("password"), entry.getValue());							
						}					
					}
				} 
			}
			
			transformer.transform(new DOMSource(xmlDoc), result);
			//System.out.println("Done");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	private static Document getRpdXMLDocument(File fXmlFile) throws ParserConfigurationException, SAXException, IOException 
	{		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		return doc;
	}
	
}


