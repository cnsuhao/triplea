/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;

/**
 *
 * @author Stephen
 */
public class Controls
{
    public static ArrayList<Component> FindAncestors4OrLessDeep(Component p)
    {
        ArrayList<Component> result = new ArrayList<Component>();
        if (p instanceof Container)
        {
            for (Component l1c : ((Container) p).getComponents())
            {
                result.add(l1c);
                if (l1c instanceof Container)
                {
                    for (Component l2c : ((Container) l1c).getComponents())
                    {
                        result.add(l2c);
                        if (l2c instanceof Container)
                        {
                            for (Component l3c : ((Container) l2c).getComponents())
                            {
                                result.add(l3c);
                                if (l3c instanceof Container)
                                {
                                    for (Component l4c : ((Container) l3c).getComponents())
                                    {
                                        result.add(l4c);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
