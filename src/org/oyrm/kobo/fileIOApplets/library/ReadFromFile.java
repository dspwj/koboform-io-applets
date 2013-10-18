package org.oyrm.kobo.fileIOApplets.library;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;

public class ReadFromFile extends JApplet{
	
	/**
	 * Just to keep us from having errors
	 */
	private static final long serialVersionUID = 4999663664355013190L;
	
	


	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public ReadFromFile() throws HeadlessException {
		super();
	}
	
	/**
	 * Don't really do much here, just for the sake of having it incase we do want to init some stuff
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
			//is it a directory?
			if(file.isDirectory())
			{
				errorMsg = "File given is a directory, cannot read a directory";
				jso.setMember(Constants.fileDataStr, "");
				jso.setMember(Constants.errorMsgStr, errorMsg);
				jso.call("fileOperationComplete", null);
				return;
			}
			
			//try reading the file contents
			Reader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader br = new BufferedReader(in);
			StringBuilder stringB = new StringBuilder();
			String tempStr = "";
			//loop over and read in all the lines
			while(null != (tempStr = br.readLine()))
			{
				stringB.append(tempStr);
			}
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
