/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Global;

import MainProgram.Console.ErrorConsole;
import MainProgram.Project.RecentProjectInformation;
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
import java.util.ArrayList;

/**
 *
 * @author Stephen
 */
public class ProgramMemory implements Serializable
{
    public ArrayList<RecentProjectInformation> RecentProjects = new ArrayList<RecentProjectInformation>();

    public static File getMapCreatorFolder()
    {
        File userHome = new File(System.getProperties().getProperty("user.home"));
        //the default
        File rootDir;
        rootDir = new File(userHome, "TripleA Map Creator");
        return rootDir;
    }
    public static ProgramMemory LoadMemory()
    {
        String loadLoc = new File(new File(getMapCreatorFolder(), "Memory").getPath() + File.separatorChar + "mainMemory.mem").getPath();
        ProgramMemory result = new ProgramMemory();
        try
        {
            BufferedInputStream bufInputStream = null;
            ObjectInputStream inputStream = null;
            try
            {
                bufInputStream = new BufferedInputStream(new FileInputStream(loadLoc));
                inputStream = new ObjectInputStream(bufInputStream);

                result = (ProgramMemory) inputStream.readObject();
            }
            catch (IOException ex)
            {
                ErrorConsole.getConsole().appendError(ex);
            }
            catch (ClassNotFoundException ex)
            {
                ErrorConsole.getConsole().appendError(ex);
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
        catch (IOException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
        return result;
    }
    public static void SaveMemory(ProgramMemory memory)
    {
        try
        {
            new File(getMapCreatorFolder(), "Memory").mkdirs();
            File file = new File(new File(getMapCreatorFolder(), "Memory").getPath() + File.separator + "mainMemory.mem");
            file.createNewFile();
            String saveLoc = file.getPath();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream(25000);
            ObjectOutputStream outStream = new ObjectOutputStream(bytes);
            FileOutputStream fileStream = null;
            try
            {
                outStream.writeObject(memory);

                fileStream = new FileOutputStream(saveLoc);

                fileStream.write(bytes.toByteArray());
            }
            catch (FileNotFoundException ex)
            {
                ErrorConsole.getConsole().appendError(ex);
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
        catch (IOException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
    }
}
