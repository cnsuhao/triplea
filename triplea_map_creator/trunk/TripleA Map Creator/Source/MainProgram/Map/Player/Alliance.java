/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Map.Player;

/**
 *
 * @author Stephen
 */
public class Alliance implements java.io.Serializable
{
    private String m_name = "";

    public Alliance()
    {
    }
    public Alliance(String name)
    {
        m_name = name;
    }
    public String GetName()
    {
        return m_name;
    }
}
