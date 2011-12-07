/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Walkthrough.WalkthroughData;

/**
 *
 * @author Stephen
 */
public class WalkthroughOptions implements java.io.Serializable
{
    /**0=Simple, 1=Normal, 2=Advanced*/
    public int p_controlLevelOption = 0;
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof WalkthroughOptions))
            return false;
        return this.hashCode() == obj.hashCode();
    }
    @Override
    public int hashCode()
    {
        StringBuilder composite = new StringBuilder();
        composite.append(Integer.toString(p_controlLevelOption));
        return composite.toString().hashCode();
    }
}