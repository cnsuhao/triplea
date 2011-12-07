/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainProgram.Map;

import java.io.Serializable;

/**
 *
 * @author Stephen
 */
public class MapProperty implements Serializable
{
    public MapProperty()
    {
    }
    public MapProperty(String name, Object value, boolean editable)
    {
        m_name = name;
        m_value = value;
        m_editable = editable;
    }
    
    private String m_name = null;
    private Object m_value = null;
    private boolean m_editable = false;
    
    public String GetName()
    {
        return m_name;
    }
    
    public Object GetValue()
    {
        return m_value;
    }
    
    public boolean IsEditable()
    {
        return m_editable;
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder().append("Name: ").append(m_name).append(", Value: ").append(m_value.toString()).append(", Editable: ").append(m_editable).toString();
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof MapProperty))
            return false;
        return hashCode() == other.hashCode();
    }
}
