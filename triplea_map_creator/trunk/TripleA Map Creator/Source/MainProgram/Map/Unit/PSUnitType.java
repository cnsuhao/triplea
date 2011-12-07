/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainProgram.Map.Unit;

import java.io.Serializable;

/**
 * Player specific unit type. Data bundle for unitType and unitOwner.
 * @author Stephen
 */
public class PSUnitType implements Serializable
{
    public PSUnitType()
    {
    }
    public PSUnitType(String unitType, String owner)
    {
        m_unitType = unitType;
        m_owner = owner;
    }
    
    private String m_unitType = null;
    private String m_owner = null;
    
    public String GetUnitType()
    {
        return m_unitType;
    }
    
    public String GetOwner()
    {
        return m_owner;
    }
    
    @Override
    public String toString()
    {
        return GetUnitType() + " owned by " + GetOwner();
    }
    
    public static PSUnitType parseFromToString(String toString)
    {
        String[] sides = toString.split(" owned by ");
        return new PSUnitType(sides[0], sides[1]);
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof PSUnitType))
            return false;
        return hashCode() == obj.hashCode();
    }
}
