/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Project;

import java.io.Serializable;

/**
 *
 * @author Stephen
 */
public class RecentProjectInformation implements Serializable
{

    private String m_projectFilename = "";
    private String m_mapName = "";
    private String m_mapVersion = "";
    private String m_projectLocation = "";

    public RecentProjectInformation(String projectFilename, String mapName, String mapVersion, String projectLocation)
    {
        m_projectFilename = projectFilename;
        m_mapName = mapName;
        m_mapVersion = mapVersion;
        m_projectLocation = projectLocation;
    }
    public String GetProjectFilename()
    {
        return m_projectFilename;
    }
    public String GetMapName()
    {
        return m_mapName;
    }
    public String GetMapVersion()
    {
        return m_mapVersion;
    }
    public String GetProjectLocation()
    {
        return m_projectLocation;
    }
}
