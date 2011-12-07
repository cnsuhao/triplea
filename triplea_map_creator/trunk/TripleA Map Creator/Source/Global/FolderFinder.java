/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

import MainProgram.ProgramLaunchers.ProgramLauncher;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 *
 * @author Stephen
 */
public class FolderFinder
{
    public static File getProgramLaunchFolder()
    {
        File runtimeFolder = new File(System.getProperties().getProperty("user.dir"));        
        return runtimeFolder;
    }
    
    public static File getProgramRuntimeFolder_Simple()
    {
        File runtimeFolder = new File(FolderFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath());      
        return runtimeFolder;
    }
    
    public static File getProgramRuntimeFolder()
    {
        //Find the url of our class
        String path = FolderFinder.class.getResource("FolderFinder.class").getPath();
        
        File f = new File(path);
        for (int i = 0; i < 100; i++)
        {
            if (new File(f, "TripleA Map Creator.jar").exists())
                return f;
            if(f.getParentFile() == null)
                break; //Give up
            f = f.getParentFile();
        }
        
        return new File(path).getParentFile().getParentFile();
    }
    
    public static File getProgramRuntimeFolder_Advanced()
    {
        //We know that the class file is in a directory one above the Global root folder
        //So navigate up from the class file, and we have root.
        
        //Find the url of our class
        URL url = FolderFinder.class.getResource("FolderFinder.class");
        
        //We want to move up 1 directory for each package
        int moveUpCount = FolderFinder.class.getName().split("\\.").length + 1;
        
        String fileName = url.getFile();
        try
        {
            //Deal with spaces in the file name which would be url encoded
            fileName  = URLDecoder.decode(fileName, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

	    //We are in a jar file
        if (fileName.indexOf("TripleA Map Creator.jar!") != -1)
        {
            String subString = fileName.substring("file:/".length() - (ProgramLauncher.isWindows() ? 0 : 1), fileName.indexOf("TripleA Map Creator.jar!") - 1);

            File f = new File(subString);

            if (!f.exists())
                return getProgramRuntimeFolder();
            return f;
        }
        else
        {
            File f = new File(fileName);

            for (int i = 0; i < moveUpCount; i++)
            {
                f = f.getParentFile();
            }

            if (!f.exists() || !new File(f, "TripleA Map Creator.jar").exists())
                return getProgramRuntimeFolder();
            return f;
        }
    }
    
    public static File getProgramDataFolder()
    {
        File userHome = new File(System.getProperties().getProperty("user.home"));
        File dataFolder = new File(userHome, "TripleA Map Creator");
        return dataFolder;
    }

    public static File getTripleAParentRootFolder()
    {
        String javaBinPath = System.getProperties().getProperty("sun.boot.library.path");
        File programFilesFolder = new File(javaBinPath.substring(0, javaBinPath.indexOf("Java")));

        File tripleAParentRootFolder = new File(programFilesFolder, "TripleA");
        if(tripleAParentRootFolder.exists())
            return tripleAParentRootFolder;
        
        //Hmmm... Try with ' (x86)' added
        tripleAParentRootFolder = new File(tripleAParentRootFolder.getPath().replace("Program Files", "Program Files (x86)"), "TripleA");
        if(tripleAParentRootFolder.exists())
            return tripleAParentRootFolder;
        
        //Try other stuff
        tripleAParentRootFolder = new File(System.getenv("ProgramFiles"), "TripleA");
        if(tripleAParentRootFolder.exists())
            return tripleAParentRootFolder;
        
        //Now try hard-coded folders
        tripleAParentRootFolder = new File("C:\\Program Files\\TripleA");
        if(tripleAParentRootFolder.exists())
            return tripleAParentRootFolder;
        
        tripleAParentRootFolder = new File("C:\\Program Files (x86)\\TripleA");
        if(tripleAParentRootFolder.exists())
            return tripleAParentRootFolder;
        
        return null;
    }
    
    public static File getTripleADataFolder()
    {
        File userHome = new File(System.getProperties().getProperty("user.home"));
        File dataFolder = new File(userHome, "triplea");
        return dataFolder;
    }
    
    public static File getTripleAMapsFolder()
    {
        return new File(getTripleADataFolder(), "maps");
    }
}
