package MainProgram.UI;

import MainProgram.Console.ErrorConsole;
import MainProgram.ProgramLaunchers.ProgramLauncher;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class FileOpen 
{
    private final String LAST_OPEN_FOLDER = "Last Open Folder";
    
    private final String ERR_MSG_1 = "Warning! Could not load the file!";
    private File file = null;

    public FileOpen(Component parent, String title, final ArrayList<FileFilter> filters)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setCurrentDirectory(new File(Preferences.userNodeForPackage(FileOpen.class).get(LAST_OPEN_FOLDER, System.getProperties().getProperty("user.dir"))));
        for (FileFilter filter : filters)
        {
            chooser.addChoosableFileFilter(filter);
        }
        chooser.setFileFilter(filters.get(0));
        int result = chooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION)
        {            
            try
            {
                file = chooser.getSelectedFile(); //Get the file
                if (file.exists())
                    Preferences.userNodeForPackage(FileOpen.class).put(LAST_OPEN_FOLDER, file.getParent());
            }
            catch (Exception ex)
            {
                ErrorConsole.getConsole().appendError(ex);
                JOptionPane.showMessageDialog(null, ERR_MSG_1, "Warning!", JOptionPane.WARNING_MESSAGE);
                file = null;
            }
        }
    }
	
    public File getFile()
    {
        return file;
    }

    public String getFilePath()
    {
        if (file == null)
            return null;
        else
            return file.getPath();
    }
    
    public static FileFilter CreateFilter(final String description, final ArrayList<String> extensions)
    {
        FileFilter result = new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if (f.isDirectory())
                {
                    return true;
                }
                for (String ext : extensions)
                {
                    if (f.getName().toLowerCase().endsWith(ext.toLowerCase()))
                        return true;
                }
                return false;
            }

            @Override
            public String getDescription()
            {
                return description;
            }
        };
        return result;
    }
}

