package org.oyrm.kobo.fileIOApplets.library;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;

public class SearchDirectory extends JApplet{
	
	/**
	 * Just to keep us from having errors
	 */
	private static final long serialVersionUID = 4999663664355013192L;
	
	


	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public SearchDirectory() throws HeadlessException {
		super();
	}
	
	/**
	 * We do all the work here, since for permissions reasons 
	 * we can't touch files except for when the applet is initialized.
	 */
	public void init() {
		
		//set errors to null, so no previous error will get picked up
		String errorMsg = "";
		String fileData = "";
		String fileName = "";
		
		//Object that represents the java script in the browser, so we can pass file data around
		JSObject jso = null;
		
		//get a handle on the JSO object, if we can't then error out and let the user know.
		try
		{
			jso = JSObject.getWindow(this);
		}
		catch(Exception e)
		{
			//since if JSO is busted we can't send error messages to GWT, so this is the way we'll send messages for the moment
			JOptionPane.showMessageDialog(this, "Error communicating with JavaScript", "The JSO JavaScript interface has an error: \r\n"+e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;					
		}
		
		//get the file name from the parameters
		try
		{
			fileName = getParameter("fileName");
		}
		catch(Exception e)
		{
			errorMsg = "No file name specified";
			jso.setMember(Constants.fileDataStr, "");
			jso.setMember(Constants.errorMsgStr, errorMsg);
			jso.call("fileOperationComplete", null);
			return;
		}
		
	
		//make sure we have a file name
		if(fileName == null || fileName.equals(""))
		{
			errorMsg = "No file name specified";
			jso.setMember(Constants.fileDataStr, "");
			jso.setMember(Constants.errorMsgStr, errorMsg);
			jso.call("fileOperationComplete", null);
			return;
		}
		
		
		
		//open the file
		File file = null;
		try
		{
			file = new File(fileName);
		}
		catch(Exception e)
		{
			jso.setMember(Constants.fileDataStr, "");
			jso.setMember(Constants.errorMsgStr, e.getMessage());
			jso.call("fileOperationComplete", null);
			return;
		}

		try
		{
			//is it not a directory?
			if(!file.isDirectory())
			{
				errorMsg = "File given, directory expected.";
				jso.setMember(Constants.fileDataStr, "");
				jso.setMember(Constants.errorMsgStr, errorMsg);
				jso.call("fileOperationComplete", null);
				return;
			}
			//init some stuff
			StringBuilder stringB = new StringBuilder();
			int i = 0;
			//get a list of files
			String files[] = file.list();
			for(String f : files)
			{
				i++;
				if(i>1){stringB.append("\n");}
				stringB.append(f);
			}
			//set the file data
			fileData = stringB.toString();		
		}
		catch(Exception e)
		{
			errorMsg = e.getMessage();
			jso.setMember(Constants.fileDataStr, "");
			jso.setMember(Constants.errorMsgStr, errorMsg);
			jso.call("fileOperationComplete", null);
			return;
		}
		
		//send the file contents to JavaScript		
		jso.setMember(Constants.fileDataStr, fileData);
		jso.setMember(Constants.errorMsgStr, "");
		jso.call("fileOperationComplete", null);

				

	}//end init()
	
	
	


}
