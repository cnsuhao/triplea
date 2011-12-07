/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainProgram.Map.Unit;

import Utils.Others;
import Utils.Strings;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Stephen
 */
public class UnitCollection implements Serializable
{
    public UnitCollection()
    {
    }
    
    public UnitCollection(HashMap<PSUnitType, Integer> unitCounts)
    {
        m_unitCounts = unitCounts;
    }
    
    private HashMap<PSUnitType, Integer> m_unitCounts = new HashMap<PSUnitType, Integer>();    
    public HashMap<PSUnitType, Integer> GetUnits()
    {
        return m_unitCounts;
    }
    
    public int GetUnitOfTypeXCount(PSUnitType type)
    {
        if(m_unitCounts.containsKey(type))
            return m_unitCounts.get(type);
        else
            return 0;
    }
    
    public void SetUnitOfTypeXCount(PSUnitType type, int count)
    {
        m_unitCounts.put(type, count);
    }
    
    public void AddUnit(PSUnitType type)
    {
        int newCount = 1;
        if(m_unitCounts.containsKey(type))
            newCount = m_unitCounts.get(type) + 1;
        m_unitCounts.put(type, newCount);
    }
    
    public void AddUnits(PSUnitType type, int count)
    {
        int newCount = count;
        if(m_unitCounts.containsKey(type))
            newCount = m_unitCounts.get(type) + count;
        m_unitCounts.put(type, newCount);
    }
    
    public void RemoveUnit(PSUnitType type)
    {
        int newCount = 0;
        if(m_unitCounts.containsKey(type))
            newCount = m_unitCounts.get(type) - 1;
        m_unitCounts.put(type, newCount);
    }
    
    public void RemoveUnits(PSUnitType type, int count)
    {
        int newCount = 0;
        if(m_unitCounts.containsKey(type))
            newCount = m_unitCounts.get(type) - count;
        m_unitCounts.put(type, newCount);
    }
    
    @Override
    public String toString()
    {
        if (m_unitCounts.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        //builder.append("[");
        HashMap<String, List<PSUnitType>> unitsByOwner = new HashMap<String, List<PSUnitType>>();
        for(PSUnitType unitType : m_unitCounts.keySet())
        {
            for(int i = 0;i < m_unitCounts.get(unitType);i++)
                Others.AddObjToListValueForKeyInMap(unitsByOwner, unitType.GetOwner(), unitType);
        }

        int ownerIndex = 0;
        for(String owner : unitsByOwner.keySet())
        {
            int unitGroups = 0;
            String lastUnitType = null;
            int lastUnitTypeCount = 0;
            for(PSUnitType unitType : unitsByOwner.get(owner))
            {
                if(lastUnitType == null) //First unit
                {
                    unitGroups = 1;
                    lastUnitType = unitType.GetUnitType();
                    lastUnitTypeCount = 1;
                }
                else if(unitType.GetUnitType().equals(lastUnitType)) //Part of a group
                    lastUnitTypeCount++;
                else //End of last group, start of next
                {
                    if(unitGroups != 1) //If this is not the end of the first group
                        builder.append(", ");
                    if(lastUnitTypeCount == 1) //If the last group was only one unit
                        builder.append(lastUnitType);
                    else
                        builder.append(lastUnitTypeCount).append(" ").append(Strings.getPluralOfUnitName(lastUnitType));

                    lastUnitType = unitType.GetUnitType();
                    lastUnitTypeCount = 1;
                    unitGroups++;
                }
            }
            if (unitGroups > 1)
                builder.append(", and ");
            if (lastUnitTypeCount == 1)
                builder.append(lastUnitType).append(" owned by ").append(owner);
            else
                builder.append(lastUnitTypeCount).append(" ").append(Strings.getPluralOfUnitName(lastUnitType)).append(" owned by ").append(owner);
            
            if(ownerIndex < unitsByOwner.size() - 1)
                builder.append(", and ");
            ownerIndex++;
        }
        //builder.append("]");
        return builder.toString();
    }
    public String toString_OwnerDoesntMatter()
    {
        StringBuilder builder = new StringBuilder();
        for(PSUnitType type : m_unitCounts.keySet())
        {
            int count = m_unitCounts.get(type);
            if(count > 0)
                builder.append(count).append(" ").append(Utils.Strings.pluralizeUnitName(type.GetUnitType(), count)).append(", ");
        }
        if(builder.length() == 0)
            return "";
        return builder.toString().substring(0, builder.length() - 2);
    }
}
