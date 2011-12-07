/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Global;

import MainProgram.Console.ErrorConsole;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.LookAndFeel;

/**
 *
 * @author Stephen
 */
public class ProgramSettings implements Serializable
{
    public static File getMapCreatorFolder()
    {
        File userHome = new File(System.getProperties().getProperty("user.home"));
        //the default
        File rootDir;
        rootDir = new File(userHome, "TripleA Map Creator");
        return rootDir;
    }
    
    //General
    public int MaxAutoBackupSize = 10;    
    public Boolean ShowErrorConsoleWhenErrorOccurs = true;
    public Boolean ShowConfirmationBoxesForDataEditing = true;
    
    //Graphics
    public String ProgramLookAndFeelClassName = NimbusLookAndFeel.class.getName();
    public int ProgramLookAndFeelIndex = 1;
    public Boolean ToolPreviewWhileMovingMouse = true;
    
    //Advanced
    public int MaxJavaHeapSize = 750;
    public int StepHistoryMaxSize = 10;    
    public int MaxPolyMultiSearchCycles = 9;    
    
    private static ProgramSettings s_lastSettings = null;
    public static ProgramSettings LoadSettings()
    {
        if (s_lastSettings == null)
        {
            String loadLoc = new File(new File(getMapCreatorFolder(), "Settings").getPath() + File.separatorChar + "mainSettings.set").getPath();
            ProgramSettings result = new ProgramSettings();
            try
            {
                BufferedInputStream bufInputStream = null;
                ObjectInputStream inputStream = null;
                try
                {
                    bufInputStream = new BufferedInputStream(new FileInputStream(loadLoc));
                    inputStream = new ObjectInputStream(bufInputStream);

                    result = (ProgramSettings) inputStream.readObject();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    if (bufInputStream != null)
                    {
                        bufInputStream.close();
                    }
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable ex)
            {
                ex.printStackTrace(); //Print error to output, but do not append into ErrorConsole, as it itself uses the ProgramSettings class
            }
            s_lastSettings = result;
            return result;
        }
        else
            return s_lastSettings;
    }
    public static void SaveSettings(ProgramSettings settings)
    {
        s_lastSettings = settings;
        try
        {
            new File(getMapCreatorFolder(), "Settings").mkdirs();
            File file = new File(new File(getMapCreatorFolder(), "Settings").getPath() + File.separator + "mainSettings.set");
            file.createNewFile();
            String saveLoc = file.getPath();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream(25000);
            ObjectOutputStream outStream = new ObjectOutputStream(bytes);
            FileOutputStream fileStream = null;
            try
            {
                outStream.writeObject(settings);

                fileStream = new FileOutputStream(saveLoc);

                fileStream.write(bytes.toByteArray());
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                if (outStream != null)
                {
                    outStream.flush();
                    outStream.close();
                }
                if (fileStream != null)
                {
                    fileStream.flush();
                    fileStream.close();
                }
            }
        }
        catch (Throwable ex)
        {
            ex.printStackTrace(); //Print error to output, but do not append into ErrorConsole, as it itself uses the ProgramSettings class
        }
    }
}
