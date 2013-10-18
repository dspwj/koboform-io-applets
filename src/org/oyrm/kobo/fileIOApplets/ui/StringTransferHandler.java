package org.oyrm.kobo.fileIOApplets.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/*
 * StringTransferHandler.java is used by the 1.4 ExtendedDnDDemo.java example.
 */

abstract class StringTransferHandler extends TransferHandler {

  
  private static final long serialVersionUID = 7332662967194107760L;
  
  protected BlockSearchApplet bsa = null;
  
  protected boolean isSourceTable = false;
  
  public StringTransferHandler(BlockSearchApplet bsa, boolean isSourceTable)
  {
	  this.bsa = bsa;
	  this.isSourceTable = isSourceTable;
  }
  protected abstract String exportString(JComponent c);

  protected abstract void importString(JComponent c, String str);

  protected abstract void cleanup(JComponent c, boolean remove);

  protected Transferable createTransferable(JComponent c) {
    return new StringSelection(exportString(c));
  }

  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  public boolean importData(JComponent c, Transferable t) {
    if (canImport(c, t.getTransferDataFlavors())) {
      try {
        String str = (String) t.getTransferData(DataFlavor.stringFlavor);
        importString(c, str);
        return true;
      } catch (UnsupportedFlavorException ufe) {
      } catch (IOException ioe) {
      }
    }

    return false;
  }

  protected void exportDone(JComponent c, Transferable data, int action) {
    cleanup(c, action == MOVE);
  }

  public boolean canImport(JComponent c, DataFlavor[] flavors) {

	//don't let the user drop on self
    for (int i = 0; i < flavors.length; i++) {
      if (DataFlavor.stringFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
}
