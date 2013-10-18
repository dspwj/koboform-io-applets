package org.oyrm.kobo.fileIOApplets.ui;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.oyrm.kobo.fileIOApplets.helpers.KoboFormItem;

/*
 * TableTransferHandler.java is used by the 1.4 ExtendedDnDDemo.java example.
 */

class TableTransferHandler extends StringTransferHandler {
	

  public TableTransferHandler(BlockSearchApplet bsa, boolean isSourceTable) 
  {
		super(bsa,isSourceTable);
  }

private static final long serialVersionUID = 9068709233373264213L;

  private int[] rows = null;

  private int addIndex = -1; //Location where items were added

  private int addCount = 0; //Number of items added.

  protected String exportString(JComponent c) {
    JTable table = (JTable) c;
    rows = table.getSelectedRows();
    int colCount = table.getColumnCount();

    StringBuffer buff = new StringBuffer();

    for (int i = 0; i < rows.length; i++) {
      for (int j = 0; j < colCount; j++) {
        Object val = table.getValueAt(rows[i], j);
        buff.append(val == null ? "" : val.toString());
        if (j != colCount - 1) {
          buff.append("\t");
        }
      }
      if (i != rows.length - 1) {
        buff.append("\n");
      }
    }

    return buff.toString();
  }

  protected void importString(JComponent c, String str) {
    JTable target = (JTable) c;
    DefaultTableModel model = (DefaultTableModel) target.getModel();
    int index = target.getSelectedRow();

    //Prevent the user from dropping data back on itself.
    //For example, if the user is moving rows #4,#5,#6 and #7 and
    //attempts to insert the rows after row #5, this would
    //be problematic when removing the original rows.
    //So this is not allowed.
    if (rows != null && index >= rows[0] - 1
        && index <= rows[rows.length - 1]) {
      rows = null;
      return;
    }

    int max = model.getRowCount();
    if (index < 0) {
      index = max;
    } else {
      index++;
      if (index > max) {
        index = max;
      }
    }
    addIndex = index;
    String[] values = str.split("\n");
    addCount = values.length;
    int colCount = target.getColumnCount();
    for (int i = 0; i < values.length && i < colCount; i++) 
    {
    	String rowStr = values[i];
    	//check and see which way we're moving things
    	if(!isSourceTable) //dropping on the toBrowser side
    	{
    		//so first we need to find this item in the search results
    		KoboFormItem kfi = null;
    		for(KoboFormItem temp : bsa.getSearchResults())
    		{
    			if(temp.getSearchKey().equals(rowStr))
    			{
    				kfi = temp;
    				break;    				
    			}
    		}
    		if(kfi != null) //should never equal null, but just in case
    		{
    			ArrayList<KoboFormItem> sendToBrowser = bsa.getSendToBrowser();
    			sendToBrowser.add(kfi);
    		}
    	}
    	else //removing from the send to browser list
    	{
    		ArrayList<KoboFormItem> sendToBrowser = bsa.getSendToBrowser();
    		int j = 0;
    		for(KoboFormItem temp : sendToBrowser)
    		{
    			if(temp.getSearchKey().equals(rowStr))
    			{
    				sendToBrowser.remove(j);
    				break;    				
    			}
    			j++;
    		}
    	}
    	//either way do the add to the table
    	model.insertRow(index++, rowStr.split("\t"));
    }
  }

  protected void cleanup(JComponent c, boolean remove) {
    JTable source = (JTable) c;
    if (remove && rows != null) {
      DefaultTableModel model = (DefaultTableModel) source.getModel();

      //If we are moving items around in the same table, we
      //need to adjust the rows accordingly, since those
      //after the insertion point have moved.
      if (addCount > 0) {
        for (int i = 0; i < rows.length; i++) {
          if (rows[i] > addIndex) {
            rows[i] += addCount;
          }
        }
      }
      for (int i = rows.length - 1; i >= 0; i--) {
        model.removeRow(rows[i]);
      }
    }
    rows = null;
    addCount = 0;
    addIndex = -1;
  }
}
