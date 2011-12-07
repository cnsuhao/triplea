/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package MainProgram.Map;

import MainProgram.Image.ImageUndoStep;
import MainProgram.Map.Player.Alliance;
import MainProgram.Map.Player.PlayerData;
import MainProgram.Map.Unit.UnitData;
import MainProgram.Map.Territory.TerritoryData;
import MainProgram.Map.Unit.UnitCollection;
import MainProgram.Walkthrough.WalkthroughData.WalkthroughData;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.ImageIcon;

/**
 *
 * @author Stephen
 */
public class MapData implements java.io.Serializable
{
    private WalkthroughData m_walkthroughData = null;
    private String m_mapName = "";
    private String m_mapVersion = "1.0";
    private ScrollWrapOption m_scrollWrapOption = ScrollWrapOption.None;
    private double m_unitScale = 1.0;
    private ImageIcon m_baseMapImage = null;
    private ArrayList<ImageUndoStep> m_baseMapImageBackups = new ArrayList<ImageUndoStep>();
    private int m_baseImageBackupsIndex = 0;
    //Remember, whenever the data within a map data component isn't needed in a certain treemap, use the name of the object instead. (Like for m_tOwners, String and String, for territory name and owner name)
    private TreeMap<String, Point> m_tDefinitions = new TreeMap<String, Point>();
    private TreeMap<String, List<Polygon>> m_tPolygons = new TreeMap<String, List<Polygon>>();
    private TreeMap<String, TreeSet<String>> m_tConnections = new TreeMap<String, TreeSet<String>>();
    private TreeMap<String, ArrayList<Point>> m_tUnitDrawPoints = new TreeMap<String, ArrayList<Point>>();
    private int m_mapPresetIndex = -1;
    private TreeMap<String, PlayerData> m_players = new TreeMap<String, PlayerData>();
    private TreeMap<String, UnitData> m_units = new TreeMap<String, UnitData>();
    private TreeMap<String, Alliance> m_alliances = new TreeMap<String, Alliance>();
    private TreeMap<String, TerritoryData> m_tData = new TreeMap<String, TerritoryData>();
    private TreeMap<String, UnitCollection> m_tUnitPlacements = new TreeMap<String, UnitCollection>();
    private TreeMap<String, MapProperty> m_playerBids = new TreeMap<String, MapProperty>(); //Stored as map property objects
    private boolean m_showTerritoryNames = true;
    private int m_minimapScalePercentage = 10;
    private TreeMap<String, MapProperty> m_mapProperties = new TreeMap<String, MapProperty>();
    private String m_mapNotes = "";

    public MapData(WalkthroughData walkthroughData)
    {
        m_walkthroughData = walkthroughData;
    }
    public WalkthroughData GetWalkthroughData()
    {
        return m_walkthroughData;
    }
    public String GetMapName()
    {
        return m_mapName;
    }
    public String GetMapVersion()
    {
        return m_mapVersion;
    }
    public void SetMapName(String mapName)
    {
        m_mapName = mapName;
    }
    public void SetMapVersion(String mapVersion)
    {
        m_mapVersion = mapVersion;
    }
    public void SetScrollWrapOption(ScrollWrapOption wrapType)
    {
        m_scrollWrapOption = wrapType;
    }
    public ScrollWrapOption GetScrollWrapOption()
    {
        return m_scrollWrapOption;
    }
    public void SetUnitScale(double scale)
    {
        m_unitScale = scale;
    }
    public double GetUnitScale()
    {
        return m_unitScale;
    }
    public ImageIcon GetBaseMapImage()
    {
        return m_baseMapImage;
    }
    public void SetBaseMapImage(ImageIcon image)
    {
        m_baseMapImage = image;
    }
    public ArrayList<ImageUndoStep> GetBaseImageBackups()
    {
        return m_baseMapImageBackups;
    }
    public int GetBaseImageBackupsIndex()
    {
        return m_baseImageBackupsIndex;
    }
    public void SetBaseImageBackups(ArrayList<ImageUndoStep> backups)
    {
        m_baseMapImageBackups = backups;
    }
    public void SetBaseImageBackupsIndex(int index)
    {
        m_baseImageBackupsIndex = index;
    }
    public void SetTDefininitions(TreeMap<String, Point> map)
    {
        m_tDefinitions = map;
    }
    public TreeMap<String, Point> GetTDefinitions()
    {
        return m_tDefinitions;
    }
    public void SetTPolygons(TreeMap<String, List<Polygon>> map)
    {
        m_tPolygons = map;
    }
    public TreeMap<String, List<Polygon>> GetTPolygons()
    {
        return m_tPolygons;
    }
    public void SetTConnections(TreeMap<String, TreeSet<String>> map)
    {
        m_tConnections = map;
    }
    public TreeMap<String, TreeSet<String>> GetTConnections()
    {
        return m_tConnections;
    }
    public void SetTUnitDrawPoints(TreeMap<String, ArrayList<Point>> map)
    {
        m_tUnitDrawPoints = map;
    }
    public TreeMap<String, ArrayList<Point>> GetTUnitDrawPoints()
    {
        return m_tUnitDrawPoints;
    }
    public int GetMapPresetIndex()
    {
        return m_mapPresetIndex;
    }
    public void SetMapPresetIndex(int index)
    {
        m_mapPresetIndex = index;
    }
    public TreeMap<String, PlayerData> GetPlayersData()
    {
        return m_players;
    }
    public void SetPlayersData(TreeMap<String, PlayerData> players)
    {
        m_players = players;
    }
    public TreeMap<String, Alliance> GetAlliances()
    {
        return m_alliances;
    }
    public void SetAlliances(TreeMap<String, Alliance> alliances)
    {
        m_alliances = alliances;
    }
    public TreeMap<String, UnitData> GetUnitsData()
    {
        return m_units;
    }
    public void SetUnitsData(TreeMap<String, UnitData> units)
    {
        m_units = units;
    }
    public void SetTData(TreeMap<String, TerritoryData> map)
    {
        m_tData = map;
    }
    public TreeMap<String, TerritoryData> GetTData()
    {
        return m_tData;
    }
    public void SetTUnitPlacements(TreeMap<String, UnitCollection> map)
    {
        m_tUnitPlacements = map;
    }
    public TreeMap<String, UnitCollection> GetTUnitPlacements()
    {
        return m_tUnitPlacements;
    }
    public void SetPlayerBids(TreeMap<String, MapProperty> map)
    {
        m_playerBids = map;
    }
    public TreeMap<String, MapProperty> GetPlayerBids()
    {
        return m_playerBids;
    }
    public void SetShowTerritoryNames(boolean showTerritoryNames)
    {
        m_showTerritoryNames = showTerritoryNames;
    }
    public boolean GetShowTerritoryNames()
    {
        return m_showTerritoryNames;
    }
    public void SetMinimapScalePercentage(int minimapScalePercentage)
    {
        m_minimapScalePercentage = minimapScalePercentage;
    }
    public int GetMinimapScalePercentage()
    {
        return m_minimapScalePercentage;
    }
    public void SetMapProperties(TreeMap<String, MapProperty> map)
    {
        m_mapProperties = map;
    }
    public TreeMap<String, MapProperty> GetMapProperties()
    {
        return m_mapProperties;
    }
    public void SetMapNotes(String notes)
    {
        m_mapNotes = notes;
    }
    public String GetMapNotes()
    {
        return m_mapNotes;
    }
}