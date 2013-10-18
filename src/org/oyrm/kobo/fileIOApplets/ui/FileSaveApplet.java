package org.oyrm.kobo.fileIOApplets.ui;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;
import org.oyrm.kobo.fileIOApplets.helpers.PropertiesHelper;

public class FileSaveApplet extends JApplet implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4002182192524909547L;

	/**
	 * Name of the form.
	 */
	private String formName = null;
	
	/**
	 * What to display on the file save dialog
	 */
	private String saveTitle = null;
	
	/**
	 * Data to save to file
	 */
	private String dataToSave = null;
	
	/**
	 * Object that represents the java script in the browser, so we can pass the XML around
	 */
	JSObject jso = null;
	
	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public FileSaveApplet() throws HeadlessException {
		super();
	
	}
	
	public void init() {
		
		setLayout(new FlowLayout());		
		//get a handle on the javaScript object
		
		try
		{
			jso = JSObject.getWindow(this);
			dataToSave = (jso.getMember("formXmlToSave")).toString();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, "Something went wrong when interfacing with the JavaScript object:\r\n" + e.getMessage());			
			System.err.println("Something went wrong when interfacing with the JavaScript object:\r\n" + e.getMessage());
			dataToSave = "Something went wrong when interfacing with the JavaScript object:\r\n" + e.getMessage();
		}

		
		
		
		
		//get the name of the form
		formName = getParameter("formName");		
		//get the i18ned string for the save dialog
		saveTitle = getParameter("save");
		
		if(saveTitle == null)
		{
			saveTitle = "save";
		}
		
		if(formName == null || formName.length() == 0)
		{
			formName = "form";
		}

		//JLabel label = new JLabel("Data to Save: " + dataToSave);
		//add(label);
		JLabel titleLabel = new JLabel("Save form to disk.");
		add(titleLabel);

		
		JButton button = new JButton("Save");
		button.addActionListener(this);
		button.setActionCommand("save");
		add(button);
		
		saveFile();
	}

	
	/**
	 * Saves the data to the file system.
	 */
	private void saveFile()
	{
		String lastUsedDirectory = PropertiesHelper.getProperty(Constants.PROPKEY_LAST_DIR_SAVED_NAME);
		JFileChooser chooser = new JFileChooser(lastUsedDirectory); 
		chooser.setSelectedFile(new File(formName));
		chooser.setDialogTitle(saveTitle);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		int retVal = chooser.showSaveDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			try
			{
				if(file.exists())
				{
					retVal = JOptionPane.showConfirmDialog(this, file.getName()+ " already exists. Do you want to overwrite it?", 
							"Overwrite file?", JOptionPane.YES_NO_OPTION);
					if(retVal == JOptionPane.NO_OPTION)
					{
						return;
					}
				}
				String currentDirectory = file.getPath();
				PropertiesHelper.setProperty(Constants.PROPKEY_LAST_DIR_SAVED_NAME, currentDirectory);
				PropertiesHelper.saveProperties();

								
				
				PrintWriter printWriter = new PrintWriter(file, "UTF-8");
				printWriter.write(dataToSave);
				printWriter.flush();
				printWriter.close();
				
				
				
				
				//let the java script know we're done.
				try
				{
					jso = JSObject.getWindow(this);
					jso.setMember("formXmlToSave", "saved");
					jso.call("closeSaveToFileDialog", null);
				}
				catch(Exception e)
				{
					System.err.println("Couldn't get a handle on the javascript object");
				}
			
				
				
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error writing to file", JOptionPane.ERROR_MESSAGE);
				
			}
		}
		else
		{
			jso.setMember("formXmlToSave", "canceled");
			jso.call("closeSaveToFileDialog", null);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		if("save".equals(arg.getActionCommand()))
		{
			saveFile();
		}
	}//end actionPerformed method
	
	
}//end class
