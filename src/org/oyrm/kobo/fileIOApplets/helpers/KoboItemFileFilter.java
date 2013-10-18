package org.oyrm.kobo.fileIOApplets.helpers;

import java.io.File;
import java.io.FileFilter;

public class KoboItemFileFilter implements FileFilter {

	private final String[] okFileExtensions = new String[] {".kfb"};
	
	@Override
	public boolean accept(File file) 
	{
		for (String extension : okFileExtensions)
		{
			if (file.getName().toLowerCase().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	  }
	

}
