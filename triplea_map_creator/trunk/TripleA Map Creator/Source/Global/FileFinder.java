/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

import MainProgram.UI.FileOpen;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Stephen
 */
public class FileFinder
{
    /**
     * By the way, selectedTripleAFolder can be null.
     */
    public static File FindTripleAJarOrExeFile(Component parent, File selectedTripleAFolder)
    {
        File tripleaJarOrExeFile = null;

        File binFolder = new File(selectedTripleAFolder, "bin");
        if (binFolder != null && binFolder.exists())
            tripleaJarOrExeFile = new File(binFolder, "triplea.jar");

        if (tripleaJarOrExeFile == null || !tripleaJarOrExeFile.exists())
            tripleaJarOrExeFile = new File(selectedTripleAFolder, "triplea.exe");

        if (tripleaJarOrExeFile == null || !tripleaJarOrExeFile.exists())
        {
            JOptionPane.showMessageDialog(parent, "Unable to locate the TripleA.exe or TripleA.jar file. Please find it for us using the dialog that will be shown next.\r\n\r\nMake sure that the file you find is the actual program, not a shortcut or link to it.", "Unable To Locate TripleA", JOptionPane.INFORMATION_MESSAGE);
            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
            filters.add(FileOpen.CreateFilter("Java JAR Files (TripleA.jar, specifically)", new ArrayList<String>(Arrays.asList(".jar"))));
            filters.add(FileOpen.CreateFilter("Program EXE Files (TripleA.exe, specifically)", new ArrayList<String>(Arrays.asList(".exe"))));
            FileOpen opener = new FileOpen(parent, "Locate TripleA.exe or TripleA.jar, actual file's please", filters);

            tripleaJarOrExeFile = opener.getFile();
        }

        return tripleaJarOrExeFile;
    }
    
    public static File FindChangelogFile()
    {
        File runtimeFolder = FolderFinder.getProgramRuntimeFolder_Advanced();

        File folder = new File(runtimeFolder.getPath());
        for (int i = 0; i < 100; i++)
        {
            if (new File(folder, "Change Log.txt").exists())
                return new File(folder, "Change Log.txt");
            if(folder.getParentFile() == null)
                break; //Give up
            folder = folder.getParentFile();
        }
        
        //It should be in the folder about the runtime folder, so return that if search failed
        return folder.getParentFile();
    }
}
