package org.oyrm.kobo.fileIOApplets.ui;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;
import org.oyrm.kobo.fileIOApplets.helpers.PropertiesHelper;

public class FileOpenApplet extends JApplet implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4002182192524909547L;

	/**
	 * What to put on the title of the file open dialog
	 */
	private String openTitle = null;
	
	/**
	 * Data to save to file
	 */
	private String xmlData = null;
	
	/**
	 * Object that represents the java script in the browser, so we can pass the XML around
	 */
	JSObject jso = null;
		
	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public FileOpenApplet() throws HeadlessException {
		super();
	
	}
	
	public void init() {
		
		
		//get the i18ned string for the open dialog
		openTitle = getParameter("open");
		
		setLayout(new FlowLayout());		
		
	
		JLabel titleLabel = new JLabel("Open form from disk.");
		add(titleLabel);

		JButton button = new JButton("Open");
		button.addActionListener(this);
		button.setActionCommand("open");
		add(button);
		
		openFile();
	}

	
	
	/**
	 * Saves the data to the file system.
	 */
	private void openFile()
	{
		String lastUsedDirectory = PropertiesHelper.getProperty(Constants.PROPKEY_LAST_DIR_OPENED_NAME);
		JFileChooser chooser = new JFileChooser(lastUsedDirectory); 
		chooser.setDialogTitle(openTitle);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		int retVal = chooser.showOpenDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();

			try
			{
				if(!file.exists())
				{
					JOptionPane.showMessageDialog(this, "File Doesn't Exists", "The file you selected: '"+ file.getName()+"' doesn't exists.", JOptionPane.ERROR_MESSAGE);
						return;					
				}
				
				String currentDirectory = file.getPath();
				PropertiesHelper.setProperty(Constants.PROPKEY_LAST_DIR_OPENED_NAME, currentDirectory);
				PropertiesHelper.saveProperties();
				
				Reader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader br = new BufferedReader(in);
				StringBuilder text = new StringBuilder();
				String tempStr = null;
				while(null != (tempStr = br.readLine()))
				{
					text.append(tempStr);
				}
				xmlData = text.toString();
				
							
				
				//let the java script know we're done. and copy over the data
				try
				{
					jso = JSObject.getWindow(this);
					jso.setMember("formXmlToLoad", xmlData);					
					jso.call("closeOpenFromFileDialog", null);
				}
				catch(Exception e)
				{
					System.err.println("Couldn't get a handle on the javascript object");
				}
				
				
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error Opening from file", JOptionPane.ERROR_MESSAGE);
				
			}
		}
		else
		{
			//din't want to open a file after all
			//let the java script know we're done. and copy over the data
			try
			{
				jso = JSObject.getWindow(this);
				jso.setMember("formXmlToLoad", "**NULL**");
				jso.call("closeOpenFromFileDialog", null);
			}
			catch(Exception e)
			{
				System.err.println("Couldn't get a handle on the javascript object");
			}
		}
	}

	/**
	 * Not really needed but it's still here just in case
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {
		if("open".equals(arg.getActionCommand()))
		{
			openFile();
		}
	}//end actionPerformed method
	
}
