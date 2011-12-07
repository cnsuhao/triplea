/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LauncherStub;

import Global.CommandHelper;
import Global.FolderFinder;
import Global.ProgramSettings;
import MainProgram.ProgramLaunchers.ProgramLauncher;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 * This launcher stub is directly called by the exe and bat files. This class then reads the user-set java max heap size and applies it to the new java process it launches
 * @author Stephen
 */
public class LauncherStub
{
    public static String LAUNCH_SUCCEEDED = "Launch succeeded";
    
    public static void main(String[] args)
    {
        File jarFile = new File(FolderFinder.getProgramRuntimeFolder_Advanced() + "\\TripleA Map Creator.jar");
        //I added this catch so the program can be used even when this launch system fails
        if(!jarFile.exists())
        {
            ProgramLauncher.main(args);
            return;
        }
        
        String command = "java";

        boolean mjhsSet = false;
        for (String arg : args)
        {
            command = command + " " + arg;
            if (arg.contains("-Xmx"))
                mjhsSet = true;
        }
        if (!mjhsSet)
            command = command + " -Xmx" + ProgramSettings.LoadSettings().MaxJavaHeapSize + "m";

        command = command + " -cp \"" + jarFile.getPath() + "\" MainProgram.ProgramLaunchers.ProgramLauncher";
        //JOptionPane.showMessageDialog(null, command);
        
        Preferences.userNodeForPackage(LauncherStub.class).putBoolean(LAUNCH_SUCCEEDED, false);
        CommandHelper.executeCommandAndGetResultingProcess(command);
        try
        {
            Thread.sleep(2500);
            if (Preferences.userNodeForPackage(LauncherStub.class).getBoolean(LAUNCH_SUCCEEDED, false) == true)
                System.exit(0); //Success
            else
            {
                JOptionPane.showMessageDialog(null, "There was not enough memory to launch with the settings you provided(Max Java Heap Size: " + ProgramSettings.LoadSettings().MaxJavaHeapSize + ").\r\n\r\nThe map creator will be relaunched without the memory settings applied.", "Not Enough Memory", JOptionPane.INFORMATION_MESSAGE);
                ProgramLauncher.main(args);
                System.exit(0); //Success
            }
        }
        catch (Exception ex)
        {
        }
    }
}
