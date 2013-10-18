package org.oyrm.kobo.fileIOApplets.library;

import java.awt.HeadlessException;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;

public class WriteToFile extends JApplet{
	
	/**
	 * Just to keep us from having errors
	 */
	private static final long serialVersionUID = 4999663664355013191L;
	
	


	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public WriteToFile() throws HeadlessException {
		super();
	}
	
	/**
	 * So it seems we have to use the init method since a 
	 * call to a static method doesn't have the permissions 
	 * needed to write to a file
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
		
		//get the file data to write
		fileData = jso.getMember(Constants.fileDataStr).toString();
		
			
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
				errorMsg = "File given is a directory, cannot write to a directory";
				jso.setMember(Constants.fileDataStr, "");
				jso.setMember(Constants.errorMsgStr, errorMsg);
				jso.call("fileOperationComplete", null);
				return;
			}
			
			//try writing the contents to file
			PrintWriter printWriter = new PrintWriter(file, "UTF-8");
			printWriter.write(fileData);
			printWriter.flush();
			printWriter.close();
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
		jso.setMember(Constants.fileDataStr, "");
		jso.setMember(Constants.errorMsgStr, "");
		jso.call("fileOperationComplete", null);

				

	}//end init()
	
	
	


}
