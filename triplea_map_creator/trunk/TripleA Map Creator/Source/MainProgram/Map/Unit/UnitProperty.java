/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Map.Unit;

import java.io.Serializable;

/**
 *
 * @author Stephen
 */
public class UnitProperty implements Serializable
{
    //Maintain compatability with old versions.
    //This only works when the actual class data is the same.
    //In other words, this lets you change class methods without breaking compatiblity.
    static final long serialVersionUID = 2214934547011326477L;
    
    public UnitProperty()
    {
    }
    public UnitProperty(String name, Object value)
    {
        m_name = name;
        m_value = value;
    }
    private String m_name = "";
    private Object m_value = null;
    public String GetName()
    {
        return m_name;
    }
    public void SetName(String name)
    {
        m_name = name;
    }
    public Object GetValue()
    {
        return m_value;
    }
    public void SetValue(Object value)
    {
        m_value = value;
    }
}
