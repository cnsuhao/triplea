/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Map.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.TreeMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Stephen
 */
public class UnitData implements Serializable
{
    private String m_name = "";
    private TreeMap<String, PlayerSpecificUnitData> m_pSUnitData = new TreeMap<String, PlayerSpecificUnitData>();
    private TreeMap<String,UnitProperty> m_properties = new TreeMap<String, UnitProperty>();

    public UnitData()
    {
    }
    public UnitData(String name, TreeMap<String, PlayerSpecificUnitData> pSUnitData, TreeMap<String, UnitProperty> properties)
    {
        m_name = name;
        m_properties = properties;
        m_pSUnitData = pSUnitData;
    }
    public void SetName(String name)
    {
        m_name = name;
    }
    public String GetName()
    {
        return m_name;
    }
    public TreeMap<String, UnitProperty> GetProperties()
    {
        return m_properties;
    }
    public void SetProperties(TreeMap<String, UnitProperty> properties)
    {
        m_properties = properties;
    }
    public TreeMap<String, PlayerSpecificUnitData> GetPlayerSpecificUnitData()
    {
        return m_pSUnitData;
    }
    public void SetPlayerSpecificUnitData(TreeMap<String, PlayerSpecificUnitData> pSUnitData)
    {
        m_pSUnitData = pSUnitData;
    }
}
