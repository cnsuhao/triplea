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

package MainProgram.Map.Territory;

/**
 *
 * @author Stephen
 */
public class TerritoryData implements java.io.Serializable
{    
    private String m_name = "";
    private boolean m_water = false;
    private boolean m_impassable = false;
    private boolean m_victoryCity = false;
    private boolean m_blockadeZone = false;
    
    private String m_owner = null;
    private String m_capitalTo = null;
    private int m_production = 0;

    public TerritoryData()
    {
    }
    public TerritoryData(String name, boolean water, boolean impassable, boolean victoryCity, boolean blockadeZone, String owner, String capitalTo, int production)
    {
        m_name = name;
        m_water = water;
        m_impassable = impassable;
        m_victoryCity = victoryCity;
        m_blockadeZone = blockadeZone;
        
        m_owner = owner;
        m_capitalTo = capitalTo;
        m_production = production;
    }

    public String GetName()
    {
        return m_name;
    }
    
    public void SetName(String name)
    {
        m_name = name;
    }
    
    public boolean IsWater()
    {
        return m_water;
    }
    
    public void SetWater(boolean water)
    {
        m_water = water;
    }    
    
    public boolean IsImpassable()
    {
        return m_impassable;
    }
    
    public void SetImpassable(boolean impassable)
    {
        m_impassable = impassable;
    }
    
    public boolean IsVictoryCity()
    {
        return m_victoryCity;
    }
    
    public void SetVictoryCity(boolean vc)
    {
        m_victoryCity = vc;
    }
    
    public boolean IsBlockadeZone()
    {
        return m_blockadeZone;
    }
    
    public void SetBlockadeZone(boolean blockadeZone)
    {
        m_blockadeZone = blockadeZone;
    }
    
    public String GetOwner()
    {
        return m_owner;
    }
    
    public void SetOwner(String owner)
    {
        m_owner = owner;
    }
    
    public String GetCapitalTo()
    {
        return m_capitalTo;
    }
    
    public void SetCapitalTo(String capitalTo)
    {
        m_capitalTo = capitalTo;
    }
    
    public int GetProduction()
    {
        return m_production;
    }
    
    public void SetProduction(int production)
    {
        m_production = production;
    }
}
