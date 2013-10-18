package org.oyrm.kobo.fileIOApplets.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DropMode;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import netscape.javascript.JSObject;

import org.oyrm.kobo.fileIOApplets.helpers.KoboFormItem;
import org.oyrm.kobo.fileIOApplets.helpers.KoboFormItemLibrary;
import org.oyrm.kobo.fileIOApplets.helpers.KoboItemTypes;

public class BlockSearchApplet extends JApplet implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4002182192524909547L;

	
	/**
	 * Object that represents the java script in the browser, so we can pass the XML around
	 */
	JSObject jso = null;
	
	/**Does the user want to look at blocks*/
	JCheckBox blockCheckBox = new JCheckBox();
	
	/**Does the user want to look at questions*/
	JCheckBox questionCheckBox = new JCheckBox();
	
	/**Does the user want to look at options*/
	JCheckBox optionsCheckBox = new JCheckBox();
	
	/**Does the user want to look at templates*/
	JCheckBox templateCheckBox = new JCheckBox();
	
	/**First search box*/
	JTextField firstSearchBox = new JTextField(30);
		
	/**Status Label*/
	JTextArea status = new JTextArea("", 2,42); 
	
	/**Results Table*/
	JTable resultsTable = new JTable() 
	{ 
		private static final long serialVersionUID = 5686588842260215380L;
		public boolean isCellEditable(int rowIndex, int colIndex){return false;}
	};
	
	/**Data model for the results table*/
	DefaultTableModel resultsModel = new DefaultTableModel();
	 
	/** Search results. So we can add them to the data to be sent to the browser*/
	ArrayList<KoboFormItem> searchResults = null;
	
	/**Items that we're going to send to the browser*/
	ArrayList<KoboFormItem> sendToBrowser = new ArrayList<KoboFormItem>();
	
	/**Results Table*/
	JTable toBrowserTable = new JTable() 
	{
		private static final long serialVersionUID = 7896502739485954480L;
		public boolean isCellEditable(int rowIndex, int colIndex){return false;}
	};
	
	/**Data model for the results table*/
	DefaultTableModel toBrowserModel = new DefaultTableModel();
	 
	/**
	 * Classic default constructor.
	 * @throws HeadlessException
	 */
	public BlockSearchApplet() throws HeadlessException {
		super();
	
	}
	
	/**
	 * Getter
	 * @return
	 */
	public ArrayList<KoboFormItem> getSearchResults()
	{
		return searchResults;
	}
	
	/**
	 * Getter
	 * @return
	 */
	public ArrayList<KoboFormItem> getSendToBrowser()
	{
		return sendToBrowser;
	}
	
	public void init() {
		
		
		//set the layout
		SpringLayout layout  = new SpringLayout();
		setLayout(layout);		
		//add all the GUI components
		//introduction
		JLabel titleLabel = new JLabel("Search Kobo Form Builder Library");		
		add(titleLabel);
		//pick what you're searching for
		JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
		add(separator1);
		
		JLabel typesLabel = new JLabel("What type of form compondent do you want to search for?");
		add(typesLabel);
		
		
		JLabel blockLabel = new JLabel("Blocks");
		add(blockLabel);
		add(blockCheckBox);
		
		JLabel questionLabel = new JLabel("Questions");
		add(questionLabel);
		add(questionCheckBox);
		
		JLabel optionsLabel = new JLabel("Options");
		add(optionsLabel);
		add(optionsCheckBox);
		
		JLabel templateLabel = new JLabel("Templates");
		add(templateLabel);
		add(templateCheckBox);
		
		//search text boxes
		JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		add(separator2);
		//what are the search terms
		JLabel searchLabel = new JLabel("Search for tags: ");
		add(searchLabel);
		add(firstSearchBox);
		JLabel searchExampleLabel = new JLabel("example: \"Earthquake\" ");
		add(searchExampleLabel);
		
		//search button stuff
		JButton searchButton = new JButton("search");
		searchButton.addActionListener(this);
		searchButton.setActionCommand("search");
		add(searchButton);
		
		//results table stuff
		JLabel searchResultsLabel = new JLabel("Search Results:");		
		add(searchResultsLabel);
		//add the table		
		resultsModel.addColumn("Name");
		resultsModel.addColumn("Type");
		resultsModel.addColumn("Tags");
		resultsTable.setModel(resultsModel);
		resultsTable.setAutoCreateRowSorter(true);
		resultsTable.setDragEnabled(true);
		resultsTable.setTransferHandler(new TableTransferHandler(this, true));
		resultsTable.setFillsViewportHeight(true);
		JScrollPane resultsTableScrollPane = new JScrollPane(resultsTable);
		resultsTableScrollPane.setPreferredSize(new Dimension(470,100));
		add(resultsTableScrollPane);
		
		//to browser table stuff
		JLabel toBrowserLabel = new JLabel("Import to current form:");		
		add(toBrowserLabel);
		//add the table		
		toBrowserModel.addColumn("Name");
		toBrowserModel.addColumn("Type");
		toBrowserModel.addColumn("Tags");
		toBrowserTable.setModel(toBrowserModel);
		toBrowserTable.setAutoCreateRowSorter(true);
		toBrowserTable.setDragEnabled(true);
		toBrowserTable.setTransferHandler(new TableTransferHandler(this, false));
		toBrowserTable.setDropMode(DropMode.INSERT_ROWS);
		toBrowserTable.setFillsViewportHeight(true);
		//toBrowserTable.setDropTarget(new DropTarget());
		JScrollPane toBrowserTableScrollPane = new JScrollPane(toBrowserTable);
		toBrowserTableScrollPane.setPreferredSize(new Dimension(470,100));
		add(toBrowserTableScrollPane);
		
		
		//add cancel and import buttons
		JButton importButton = new JButton("Import");
		importButton.addActionListener(this);
		importButton.setActionCommand("import");
		add(importButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		add(cancelButton);

		
		//add status
		JLabel statusLabel = new JLabel("Status:");
		add(statusLabel);		
		status.setEnabled(false);
		status.setDisabledTextColor(Color.black);
		JScrollPane statusScrollPane = new JScrollPane(status);
		add(statusScrollPane);
		
		//now configure the layout
		//layout the title
		layout.putConstraint(SpringLayout.WEST, titleLabel, 130, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.NORTH, this);
		//position the title for selecting data type
		layout.putConstraint(SpringLayout.WEST, typesLabel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, typesLabel, 40, SpringLayout.NORTH, titleLabel);
		
		//get the types that can be searched for
		String typesToShowStr = getParameter("typesToShow");
		int typesToShow = 255;
		if(typesToShowStr != null)
		{
			typesToShow = Integer.parseInt(typesToShowStr);
		}
		//now time for some good old bit masking, heck ya, brings a tear to my eye.
		int blockBitMask = 1;
		int questionBitMask = 2;
		int optionBitMask = 4;
		int templateBitMask = 8;
		
		//is the block check box shown or hidden
		int bitVal = typesToShow & blockBitMask;		
		if(bitVal != blockBitMask )
		{
			blockCheckBox.setEnabled(false);	
			blockLabel.setEnabled(false);
		}
		//question check box, yes/no
		bitVal = typesToShow & questionBitMask;		
		if(bitVal != questionBitMask )
		{
			questionCheckBox.setEnabled(false);	
			questionLabel.setEnabled(false);
		}
		//option check box, yes/no
		bitVal = typesToShow & optionBitMask;		
		if(bitVal != optionBitMask )
		{
			optionsCheckBox.setEnabled(false);	
			optionsLabel.setEnabled(false);
		}
		//template check box, yes/no
		bitVal = typesToShow & templateBitMask;		
		if(bitVal != templateBitMask )
		{
			templateCheckBox.setEnabled(false);	
			templateLabel.setEnabled(false);
		}
		
		
		
		
		//blocks checkbox
		layout.putConstraint(SpringLayout.WEST, blockCheckBox, 20, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, blockCheckBox, 20, SpringLayout.NORTH, typesLabel);
		//blocks label
		layout.putConstraint(SpringLayout.WEST, blockLabel, 5, SpringLayout.EAST, blockCheckBox);
		layout.putConstraint(SpringLayout.NORTH, blockLabel, 5, SpringLayout.NORTH, blockCheckBox);
		
		//question checkbox
		layout.putConstraint(SpringLayout.WEST, questionCheckBox, 20, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, questionCheckBox, 20, SpringLayout.NORTH, blockCheckBox);
		//question label
		layout.putConstraint(SpringLayout.WEST, questionLabel, 5, SpringLayout.EAST, questionCheckBox);
		layout.putConstraint(SpringLayout.NORTH, questionLabel, 5, SpringLayout.NORTH, questionCheckBox);
		
		//options checkbox
		layout.putConstraint(SpringLayout.WEST, optionsCheckBox, 120, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, optionsCheckBox, 20, SpringLayout.NORTH, typesLabel);
		//options label
		layout.putConstraint(SpringLayout.WEST, optionsLabel, 5, SpringLayout.EAST, optionsCheckBox);
		layout.putConstraint(SpringLayout.NORTH, optionsLabel, 5, SpringLayout.NORTH, optionsCheckBox);
		
		//template checkbox
		layout.putConstraint(SpringLayout.WEST, templateCheckBox, 120, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, templateCheckBox, 20, SpringLayout.NORTH, optionsCheckBox);
		//template label
		layout.putConstraint(SpringLayout.WEST, templateLabel, 5, SpringLayout.EAST, templateCheckBox);
		layout.putConstraint(SpringLayout.NORTH, templateLabel, 5, SpringLayout.NORTH, templateCheckBox);

		//search
		layout.putConstraint(SpringLayout.WEST, searchLabel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, searchLabel, 40, SpringLayout.NORTH, templateCheckBox);
		//search box
		layout.putConstraint(SpringLayout.WEST, firstSearchBox, 5, SpringLayout.EAST, searchLabel);
		layout.putConstraint(SpringLayout.NORTH, firstSearchBox, 0, SpringLayout.NORTH, searchLabel);
		//search example
		layout.putConstraint(SpringLayout.WEST, searchExampleLabel, 5, SpringLayout.EAST, searchLabel);
		layout.putConstraint(SpringLayout.NORTH, searchExampleLabel, 20, SpringLayout.NORTH, firstSearchBox);
		
		//search button
		layout.putConstraint(SpringLayout.WEST, searchButton, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, searchButton, 20, SpringLayout.NORTH, searchExampleLabel);
		
		//results label
		layout.putConstraint(SpringLayout.WEST, searchResultsLabel, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, searchResultsLabel, 40, SpringLayout.NORTH, searchButton);
		//results table
		layout.putConstraint(SpringLayout.WEST, resultsTableScrollPane, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, resultsTableScrollPane, 5, SpringLayout.SOUTH, searchResultsLabel);
		
		//to browser label
		layout.putConstraint(SpringLayout.WEST, toBrowserLabel, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, toBrowserLabel, 40, SpringLayout.SOUTH, resultsTableScrollPane);
		//to browser table
		layout.putConstraint(SpringLayout.WEST, toBrowserTableScrollPane, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, toBrowserTableScrollPane, 5, SpringLayout.SOUTH, toBrowserLabel);
		
		//import and cancel buttons
		layout.putConstraint(SpringLayout.WEST, importButton, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, importButton, 10, SpringLayout.SOUTH, toBrowserTableScrollPane);
		layout.putConstraint(SpringLayout.WEST, cancelButton, 100, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, cancelButton, 10, SpringLayout.SOUTH, toBrowserTableScrollPane);
		
		//status
		layout.putConstraint(SpringLayout.WEST, statusLabel, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, statusLabel, 10, SpringLayout.SOUTH, importButton);
		layout.putConstraint(SpringLayout.WEST, statusScrollPane, 10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, statusScrollPane, 20, SpringLayout.NORTH, statusLabel);
		
		this.setSize(500, 600);
		
		try
		{
			KoboFormItemLibrary.init();
		}
		catch(Exception exp)
		{
			JOptionPane.showMessageDialog(this, exp.getMessage(), "Error reading from library", JOptionPane.ERROR_MESSAGE);		
		}
		ArrayList<String> messages = KoboFormItemLibrary.getAndFlushMessages();
		String msg = "";
		for(String s : messages)
		{
			msg += s + "\r\n";
		}
		status.setText(msg);
		
	}

	
	
	/**
	 * Not really needed but it's still here just in case
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {
		if("search".equals(arg.getActionCommand()))
		{
			search();
		}
		else if("import".equals(arg.getActionCommand()))
		{
			importForms();
		}
		else if ("cancel".equals(arg.getActionCommand()))
		{
			close();
		}
	}//end actionPerformed method
	
	
	/**
	 * This is the method called to close this window
	 */
	private void close()
	{
		try
		{
			jso = JSObject.getWindow(this);			
			jso.call("importDataFromLibraryCloseDialog", null);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Couldn't get a handle on the javascript object");
		}
	}
	
	/**
	 * This method is used to send data through the JavaScriptVerse into our GWT application where
	 * computation can continue as normal
	 */
	private void importForms()
	{

		try
		{
			//loop over the data we've stored up and send it over, loop over the options first
			for(KoboFormItem kfi : sendToBrowser)
			{
				if(kfi.type != KoboItemTypes.options)
				{
					continue;
				}
				String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + kfi.data;
				jso = JSObject.getWindow(this);
				jso.setMember("itemXmlToLoad", xmlData);
				jso.call("importDataFromLibrary", null);
			}
			//now send the other types
			for(KoboFormItem kfi : sendToBrowser)
			{
				if(kfi.type == KoboItemTypes.options)
				{
					continue;
				}
				String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + kfi.data;
				jso = JSObject.getWindow(this);
				jso.setMember("itemXmlToLoad", xmlData);
				jso.call("importDataFromLibrary", null);
			}
			close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Couldn't get a handle on the javascript object");
		}

	}
	
	
	
	/**
	 * This is run to take the search terms and query the library returning data into the table
	 */
	private void search()
	{
		//search terms for tags
		String searchTerm = firstSearchBox.getText();
		ArrayList<String> searchTerms = new ArrayList<String>();
		searchTerms.add(searchTerm);
		
		//data types
		ArrayList<KoboItemTypes> types = new ArrayList<KoboItemTypes>();
		if(blockCheckBox.getModel().isSelected())
		{
			types.add(KoboItemTypes.block);
		}
		else if(questionCheckBox.getModel().isSelected())
		{
			types.add(KoboItemTypes.question);
		}
		else if(optionsCheckBox.getModel().isSelected())
		{
			types.add(KoboItemTypes.options);
		}
		else if(templateCheckBox.getModel().isSelected())
		{
			types.add(KoboItemTypes.template);
		}
		
		//make sure they selected something
		if(types.size() == 0)
		{
			JOptionPane.showMessageDialog(this, "You must specify at least one form component type when searching", "Missing type", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try
		{
			//run the search
			System.out.println("here");
			searchResults = KoboFormItemLibrary.Query(searchTerms, types, true);
			
			resultsModel.setRowCount(0);
			//put the results in the table
			for(KoboFormItem kfi : searchResults)
			{
				String name = kfi.name;
				String type = kfi.type.toString();
				String tags = kfi.getTagString();
				
				
				Object[] row = {name, type, tags};
				
				resultsModel.addRow(row);
			}
			
		}
		catch(Exception exp)
		{
			JOptionPane.showMessageDialog(this, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(this, exp.getMessage(), "Error searching library", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
