/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Project;

import Global.Constants;
import Global.ProgramMemory;
import Global.Version;
import MainProgram.Console.ErrorConsole;
import MainProgram.Map.MapData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Stephen
 */
public class Project implements java.io.Serializable
{
    private ProjectInformation m_projectInfo = null;
    private MapData m_mapData = null;
    public Project(MapData data, ProjectInformation info)
    {
        m_projectInfo = info;
        m_mapData = data;
    }
    public ProjectInformation GetProjectInfo()
    {
        return m_projectInfo;
    }
    public MapData GetMapData()
    {
        return m_mapData;
    }
    private static RecentProjectInformation CreateProjectInfo(String saveLoc, Project project)
    {
        String fileName = " " + new File(saveLoc).getName().trim();
        RecentProjectInformation info = new RecentProjectInformation(fileName.substring(0,fileName.length() - 5).trim(), project.m_mapData.GetMapName(), project.m_mapData.GetMapVersion(), saveLoc);
        return info;
    }
    @SuppressWarnings("static-access")
    public static void SaveProject(Project project, String saveLoc)
    {
        SaveProject(project, saveLoc, true);
    }
    @SuppressWarnings("static-access")
    public static void SaveProject(Project project, String saveLoc, Boolean addToRecentProjects)
    {
        try
        {            
            if (addToRecentProjects)
            {
                project.GetProjectInfo().SaveLocation = saveLoc;
                FileOutputStream stream = new FileOutputStream(saveLoc);
                SaveProject(project, stream);
                try
                {
                    stream.close();
                }
                catch (IOException ex)
                {
                    ErrorConsole.getConsole().appendError(ex);
                }
                ProgramMemory memory = ProgramMemory.LoadMemory();
                ArrayList<RecentProjectInformation> newList = new ArrayList<RecentProjectInformation>();
                for (RecentProjectInformation info : memory.RecentProjects)
                {
                    if (!info.GetProjectLocation().equals(saveLoc))
                    {
                        newList.add(info);
                    }
                }

                memory.RecentProjects = newList;
                memory.RecentProjects.add(0, CreateProjectInfo(saveLoc, project));
                memory.SaveMemory(memory);
            }
            else //Must be auto-backup
            {
                FileOutputStream stream = new FileOutputStream(saveLoc);
                SaveProject(project, stream);
                try
                {
                    stream.close();
                }
                catch (IOException ex)
                {
                    //ErrorConsole.getConsole().appendError(ex);
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
    }
    @SuppressWarnings("static-access")
    public static void SaveProject(Project project, OutputStream outputStream)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(25000);
        try
        {
            ObjectOutputStream objStream = new ObjectOutputStream(bytes);
            try
            {
                objStream.writeObject(Constants.ProgramVersion);
                objStream.writeObject(project);

                outputStream.write(bytes.toByteArray());
            }
            catch (FileNotFoundException ex)
            {
                ErrorConsole.getConsole().appendError(ex);
            }
            finally
            {
                if (objStream != null)
                {
                    objStream.flush();
                    objStream.close();
                }
            }
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
    }
    public static Project LoadProject(String loadLoc)
    {
        try
        {
            FileInputStream stream = new FileInputStream(loadLoc);
            Project result = LoadProject(stream);
            stream.close();
            return result;
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
            return null;
        }
    }
    public static Project LoadProject(InputStream inputStream)
    {
        Project result = null;
        try
        {
            ObjectInputStream objStream = new ObjectInputStream(inputStream);

            Version v = (Version)objStream.readObject();
            if(!v.equals(Constants.ProgramVersion))
            {
                int choice = JOptionPane.showConfirmDialog(null, "The map project you are trying to load was saved using a different version of the map creator. (Version " + v.toString() + ")\r\n\r\nDo you want the program to attempt to load it?","Wrong Version", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice != JOptionPane.YES_OPTION)
                    return null;
            }
            result = (Project)objStream.readObject();
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
        return result;
    }
    public Project Cloned()
    {
        try
        {
            ByteArrayOutputStream sink = new ByteArrayOutputStream(10000);
            SaveProject(this, sink);
            sink.close();
            ByteArrayInputStream source = new ByteArrayInputStream(sink.toByteArray());
            sink = null;
            return LoadProject(source);
        }
        catch (IOException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
            return null;
        }
    }
    public int GetProjectBytesSize()
    {
        try
        {
            ByteArrayOutputStream sink = new ByteArrayOutputStream(10000);
            SaveProject(this, sink);
            int count = sink.size();
            sink.close();
            sink = null;
            return count;
        }
        catch (IOException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
            return -1;
        }
    }
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Project))
        {
            return false;
        }
        if (((Project) obj).GetProjectBytesSize() != this.GetProjectBytesSize())
        {
            return false;
        }

        return true;
    }
}