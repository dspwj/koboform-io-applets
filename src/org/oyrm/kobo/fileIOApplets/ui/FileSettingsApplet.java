package org.oyrm.kobo.fileIOApplets.ui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.constants.Constants;
import org.oyrm.kobo.fileIOApplets.helpers.PropertiesHelper;

public class FileSettingsApplet extends JApplet implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4002182192524909547L;
	
	/**
	 * Object that represents the java script in the browser, so we can pass the XML around
	 */
	JSObject jso = null;

	/**
	 * The field where the libraryLocation text is stored
	 */
	private JTextField libraryLocationTxtFld = new JTextField("text field", 15);
	
	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public FileSettingsApplet() throws HeadlessException {
		super();
	
	}
	
	public void init() {
		
		//setup the layouts
		SpringLayout layout = new SpringLayout();
		setLayout(layout);		

		//add label for library
		JLabel titleLabel = new JLabel("Library location:");
		add(titleLabel);
		//add text field for library		
		libraryLocationTxtFld.setText(PropertiesHelper.getProperty(Constants.PROPKEY_LIBRARY_DIR));
		libraryLocationTxtFld.setDisabledTextColor(Color.BLACK);
		libraryLocationTxtFld.setEnabled(false);
		libraryLocationTxtFld.setColumns(31);
		add(libraryLocationTxtFld);
		//add browse button for library location
		JButton libLocBrowseButton = new JButton("Browse");
		libLocBrowseButton.addActionListener(this);
		libLocBrowseButton.setActionCommand("browseLibrary");
		add(libLocBrowseButton);
		//save button
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("saveSettings");
		add(saveButton);
		//cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancelSettings");
		add(cancelButton);
		
		
		
		//setup spring manager
		//Setup label
		int topMargin = 100;
		layout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, titleLabel, topMargin, SpringLayout.NORTH, this);
		//setup text field
		layout.putConstraint(SpringLayout.WEST, libraryLocationTxtFld, 5, SpringLayout.EAST, titleLabel);
		layout.putConstraint(SpringLayout.NORTH, libraryLocationTxtFld, topMargin, SpringLayout.NORTH, this);
		//setup browse button
		layout.putConstraint(SpringLayout.WEST, libLocBrowseButton, 5, SpringLayout.EAST, libraryLocationTxtFld);
		layout.putConstraint(SpringLayout.NORTH, libLocBrowseButton, topMargin, SpringLayout.NORTH, this);
		//save button
		layout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, saveButton, 80, SpringLayout.SOUTH, titleLabel);
		//cancel button
		layout.putConstraint(SpringLayout.WEST, cancelButton, 5, SpringLayout.EAST, saveButton);
		layout.putConstraint(SpringLayout.NORTH, cancelButton, 80, SpringLayout.SOUTH, titleLabel);
		
		
		this.setSize(600, 340);
		
		//default to a null value
		jso = JSObject.getWindow(this);
		jso.setMember("updateSettings", "");
		
	}

	
	
	/**
	 * Saves the data to the file system.
	 */
	private void browseLibrary()
	{
		String lastUsedDirectory = PropertiesHelper.getProperty(Constants.PROPKEY_LIBRARY_DIR);
		JFileChooser chooser = new JFileChooser(lastUsedDirectory); 
		chooser.setDialogTitle("Choose Library Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		int retVal = chooser.showOpenDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			libraryLocationTxtFld.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		else
		{			
			//don't do anything if they choose cancel
		}
	}
	
	/**
	 * Short little method that saves the settings
	 */
	private void saveSettings()
	{
		PropertiesHelper.setProperty(Constants.PROPKEY_LIBRARY_DIR, this.libraryLocationTxtFld.getText());
		PropertiesHelper.saveProperties();
		JOptionPane.showMessageDialog(this, "Saved Settings", "Your settings have been saved", JOptionPane.INFORMATION_MESSAGE);
		
		//let the java script know we're done. and copy over the data
		try
		{
			//let the GWT know what the users settings are. At some point we'll use \n as delimters between key value pairs
			jso = JSObject.getWindow(this);
			jso.setMember("updateSettings", Constants.PROPKEY_LIBRARY_DIR +"\t"+ this.libraryLocationTxtFld.getText());
		}
		catch(Exception e)
		{
			System.err.println("Couldn't get a handle on the javascript object");
		}
		
		close();
	}
	
	/**
	 * Use this to close the dialog
	 */
	private void close()
	{
		try
		{
			jso = JSObject.getWindow(this);
			jso.call("settingsCloseDialog", null);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "JavaScript Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Couldn't get a handle on the javascript object");
		}
	}

	/**
	 * Not really needed but it's still here just in case
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {
		if("browseLibrary".equals(arg.getActionCommand()))
		{
			browseLibrary();
		}
		else if("saveSettings".equals(arg.getActionCommand()))
		{
			saveSettings();
		}
		else if("cancelSettings".equals(arg.getActionCommand()))
		{
			close();
		}
	}//end actionPerformed method
	
}//end class
