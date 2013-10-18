package org.oyrm.kobo.fileIOApplets.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.oyrm.kobo.fileIOApplets.constants.Constants;
import org.oyrm.kobo.fileIOApplets.ui.FileSettingsApplet;

/**
 * This is a static class that makes it easier to save and record
 * properties
 * @author etherton
 *
 */
public class PropertiesHelper {
	
	/**
	 * Properties object
	 */
	private static Properties applicationProps = null;
	
	/**
	 * The default properties in case the user hasn't set a property yet
	 */
	private static Properties defaultProps = null;
	
	/**
	 * Called when the user wants to flush the changes to file
	 * 
	 */
	
	public static void saveProperties()
	{
		FileOutputStream out;
		try {
			File configDir = new File(System.getProperty("user.home")
					+ File.separator + Constants.CONFIG_STORAGEDIR);
			if (!configDir.exists()) {
				configDir.mkdir();
			}
			File propsFile = new File(configDir, Constants.CONFIG_PROPSFILE);
			if (!propsFile.exists()) {
				propsFile.createNewFile();
			}
			out = new FileOutputStream(propsFile);
			applicationProps.store(out, "Saved Application Instance");
			out.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when the user wants to update a property
	 */
	public static void setProperty(String key, String value)
	{
		//if the properties haven't been initialized, initialize them
		if(applicationProps == null)
		{
			init();
		}
		applicationProps.setProperty(key, value);
	}
	
	
	/**
	 * Called when the user wants to get a property
	 */
	public static String getProperty(String key)
	{
		//if the properties haven't been initialized, initialize them
		if(applicationProps == null)
		{
			init();
		}
		
		if(applicationProps.containsKey(key))
		{
			return applicationProps.getProperty(key);
		}
		else //default to the defaults
		{
			return defaultProps.getProperty(key);
		}
	}
	
	/**
	 * The method you call to init everything
	 */
	private static void init()
	{		//setup the properties for this applet. mostly just what directory
		//we were last browsing for files
		applicationProps = new Properties();
	
		try {
			// create and load default properties
			defaultProps = new Properties();
			InputStream in = FileSettingsApplet.class.getClassLoader().getResourceAsStream(Constants.CONFIG_PROPSRESOURCE);
			defaultProps.load(in);
			in.close();
			
			File propFile = new File(System.getProperty("user.home")
					+ File.separator + Constants.CONFIG_STORAGEDIR
					+ File.separator + Constants.CONFIG_PROPSFILE);
			
			File propStorage = new File(propFile.getParent());
			if (!propStorage.exists()) {
				propStorage.mkdir();
			}
			
			if (propFile.exists()) {
				FileInputStream fin = new FileInputStream(propFile);
				applicationProps.load(fin);
				fin.close();
			}
			Enumeration<?> pnames = applicationProps.propertyNames();
			while (pnames.hasMoreElements()) {
				Object key = pnames.nextElement();
				System.setProperty(
						(String)key, 
						(String)applicationProps.getProperty((String) key));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
