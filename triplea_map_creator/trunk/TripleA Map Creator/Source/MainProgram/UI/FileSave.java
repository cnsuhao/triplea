/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.UI;

import java.awt.Component;
import java.io.*;
import java.util.prefs.Preferences;
import javax.swing.*;

public class FileSave
{
    private final String LAST_SAVE_FOLDER = "Last Save Folder";
    
    private File file = null;
    public FileSave(Component parent, String title, String extension, String extDescription)
    {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(title);
        chooser.setCurrentDirectory(new File(Preferences.userNodeForPackage(FileSave.class).get(LAST_SAVE_FOLDER, System.getProperties().getProperty("user.dir"))));
        m_extDescription = extDescription;
        m_extString = extension;
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
        {
            public boolean accept(File f)
            {
                return f.getPath().endsWith(FileSave.m_extString) || f.isDirectory();
            }
            public String getDescription()
            {
                return FileSave.m_extDescription;
            }
        });

        //Show the file chooser dialog
        int result = chooser.showSaveDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            file = new File(chooser.getSelectedFile().getPath());
            Preferences.userNodeForPackage(FileSave.class).put(LAST_SAVE_FOLDER, file.getParent());
        }
    }
    private static String m_extString = "";
    private static String m_extDescription = "";

	public File getFile()
	{
		return file;
	}

	public String getFilePath()
	{
		if(file == null)
			return null;
		else
			return file.getPath();
	}
}
