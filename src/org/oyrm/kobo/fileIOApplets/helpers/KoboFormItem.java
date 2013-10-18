package org.oyrm.kobo.fileIOApplets.helpers;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Used to hold Kobo Form Items
 * Special thanks to totheriver.com for writing this article on XML parsing that I used to make this class
 * http://www.java-samples.com/showtutorial.php?tutorialid=152
 * @author etherton
 *
 */
public class KoboFormItem {

	/** What type of item is this*/
	public KoboItemTypes type;
	
	/** What's the weight of this item*/
	public int weight;
	
	/** The name of this item*/
	public String name;
	
	/**an array of tags**/
	public ArrayList<String> tags = new ArrayList<String>();
	
	/**The form data*/
	public String data;
	
	/**
	 * Consturctor from raw data types
	 * @param type
	 * @param weight
	 * @param name
	 * @param tags
	 * @param data
	 */
	public KoboFormItem(String type, int weight, String name, ArrayList<String> tags, String data)
	{
		this.type = KoboItemTypes.fromString(type);
		this.weight = weight;
		this.name = name;
		this.tags = tags;
		this.data = data;
	}
	
	
	/**
	 * Constructor from a file path
	 * @param file
	 */
	public KoboFormItem(String file) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		//Using factory get an instance of document builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//parse using builder to get DOM representation of the XML file
		Document dom = db.parse(file);
		
		//get the root element
		Element docEle = dom.getDocumentElement();
		
		//setup the element
		Element el = docEle;
		
		/*
		NodeList nl = docEle.getElementsByTagName("kobo_item");
		if(nl != null && nl.getLength() > 0)
		{
			for(int i = 0; i < nl.getLength(); i++) 
			{

				//get the employee element
				el = (Element)nl.item(i);
				break;
			}
		}
		*/
		
		if(el == null)
		{
			throw new Exception("Could not find \"kobo_item\" element in file. File is not a valid Kobo Form Item");
		}
		
		String name = getTextValue(el, "name");
		String type = el.getAttribute("type");
		int weight = Integer.parseInt(el.getAttribute("weight"));
		String data = getTextValue(el, "item_data");
		ArrayList<String> tags = new ArrayList<String>();
		//parse the tags
		NodeList tagNodelist = el.getElementsByTagName("tag");
		for(int i = 0; i < tagNodelist.getLength(); i++)
		{
			Element tagElement = (Element)tagNodelist.item(i);
			String tag = tagElement.getFirstChild().getNodeValue();
			tags.add(tag);
		}
		
		this.type = KoboItemTypes.fromString(type);
		this.weight = weight;
		this.name = name;
		this.tags = tags;
		this.data = data;

	}//end contructor from file
	
	
	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Used to see if an item has the given tag
	 * This is a soft search that just looks to see if part of the
	 * string is in there
	 * @param tag
	 * @return
	 */
	public boolean hasTag(String tag)
	{
		for(String t : tags)
		{
			if(t.toLowerCase().indexOf(tag.toLowerCase()) != -1)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used to figure out if the object has any one of the given tags
	 * @param tags
	 * @return
	 */
	public boolean hasTagsOr(ArrayList<String> tags)
	{
		for(String tag : tags)
		{
			if(hasTag(tag))
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Used to figure out if the object has all of the given tags
	 * @param tags
	 * @return
	 */
	public boolean hasTagsAnd(ArrayList<String> tags)
	{
		for(String tag : tags)
		{
			if(!hasTag(tag))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets a comma seperated list of strings
	 * @return
	 */
	public String getTagString()
	{
		int i = 0;
		String retVal = "";
		//populate the tags
		for(String t : tags)
		{
			i++;
			if(i > 1)
			{
				retVal += ",  ";
			}
			retVal += t;
		}
		return retVal;
	}
	
	/**
	 * Generates the search key
	 * @return
	 */
	public String getSearchKey()
	{
		return name + "\t" + type.toString() + "\t" + getTagString();
	}
}

