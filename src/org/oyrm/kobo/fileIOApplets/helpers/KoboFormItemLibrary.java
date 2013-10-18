package org.oyrm.kobo.fileIOApplets.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.oyrm.kobo.fileIOApplets.constants.Constants;

/**
 * The Library of form items
 * @author etherton
 *
 */
public class KoboFormItemLibrary {
	
	/** A list of all the items */
	private static ArrayList<KoboFormItem> items = null;
	
	/** A map of tags and the items that have the given tags */	
	private static HashMap<String, ArrayList<KoboFormItem>> tags = null;
	
	/** A map of types and the items that have the given types */
	private static HashMap<KoboItemTypes, ArrayList<KoboFormItem>> types = null;
	
	/** Store messages about how things are going here */
	private static ArrayList<String> messages = new ArrayList<String>();
	

	
	/**
	 * This method initialize everything
	 */
	public static void init() throws Exception
	{
		int found = 0;
		int loaded = 0;
		int erroredOut = 0;
		//initialize are lists and maps
		items = new ArrayList<KoboFormItem>();
		tags = new HashMap<String, ArrayList<KoboFormItem>>();
		types = new HashMap<KoboItemTypes, ArrayList<KoboFormItem>>();
		
		//get the location of the library
		String libraryDirectory = PropertiesHelper.getProperty(Constants.PROPKEY_LIBRARY_DIR);
		
		//Get a file object to the directory
		File dir = new File(libraryDirectory);
		//make sure the file exists
		if(!dir.exists())
		{
			throw new Exception("The directory the library is supposed to be in does not exists");
		}
		//make sure it's a directory
		if(!dir.isDirectory())
		{
			throw new Exception("The given directory for the library is not a directory");
		}
		
		//get the files in the library director
		File[] files = dir.listFiles(new KoboItemFileFilter());
		//loop over the files and parse them
		for(File f : files)
		{
			found++;
			try
			{
				KoboFormItem kfi = new KoboFormItem(f.getAbsolutePath());
				//add to our global list
				items.add(kfi);
				//add to the map for it's given type
				if(!types.containsKey(kfi.type))
				{
					types.put(kfi.type, new ArrayList<KoboFormItem>());
				}
				types.get(kfi.type).add(kfi);
				//now for each of the tags put those in our internal storage
				for(String tag : kfi.tags)
				{
					if(!tags.containsKey(tag))
					{
						tags.put(tag, new ArrayList<KoboFormItem>());
					}
					tags.get(tag).add(kfi);
				}
				loaded++;
			}
			catch( Exception exp)
			{
				messages.add(exp.getMessage());
				erroredOut++;
			}
		}
		
		messages.add("Items Found: " + found + " Successfuly Loaded: " + loaded + " unsuccessfully loaded: " + erroredOut);
		
	}//end init()
	
	
	/**
	 * This method gets the messages for the user's benefit
	 * @return
	 */
	public static ArrayList<String> getAndFlushMessages()
	{
		ArrayList<String> retVal = messages;
		messages = new ArrayList<String>();
		return retVal;
	}//end getAndFlushMessages
	
	
	/**
	 * This uses tags to search for form items
	 * @param tags
	 * @return
	 */
	public static ArrayList<KoboFormItem> Query(ArrayList<String> searchTags, ArrayList<KoboItemTypes> searchTypes, boolean isAnd) throws Exception
	{
		//check if we need to init things
		if(items == null)
		{
			init();
		}
		ArrayList<KoboFormItem> retVal = new ArrayList<KoboFormItem>();
		ArrayList<KoboFormItem> retVal0 = new ArrayList<KoboFormItem>();
		//loop over the types
		for(KoboItemTypes type : searchTypes)
		{
			if(types.containsKey(type))
			{
				retVal0.addAll(types.get(type));
			}
		}
		//loop over tags
		for(KoboFormItem kfi : retVal0)
		{
			if(isAnd)
			{
				if(kfi.hasTagsAnd(searchTags))
				{
					retVal.add(kfi);
				}
			}
			else
			{
				if(kfi.hasTagsOr(searchTags))
				{
					retVal.add(kfi);
				}
			}
		}
		
		return retVal;
	}//end query() method
	
}
