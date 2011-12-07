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

package Utils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author Stephen
 */
public class Strings
{
    public static ArrayList<String> DuoSList(String s1, String s2)
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(s1);
        list.add(s1);
        return list;
    }
    public static TreeSet<String> Combine(TreeSet<String> old, String add)
    {
        if(old == null)
            old = new TreeSet<String>();
        old.add(add);
        return old;
    }
    public static TreeSet<String> Combine(TreeSet<String> old, ArrayList<String> toAdd)
    {
        if(old == null)
            old = new TreeSet<String>();
        for (String add : toAdd)
        {
            old.add(add);
        }
        return old;
    }
    public static String GetFirstInAlpha(String t1, String t2)
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(t1);
        list.add(t2);
        Collections.sort(list);
        return list.get(0);
    }
    public static String GetLastInAlpha(String t1, String t2)
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(t1);
        list.add(t2);
        Collections.sort(list);
        return list.get(1);
    }
    public static String RectTS(Rectangle rect)
    {
        return rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
    }
    public static Rectangle StringTR(String str)
    {
        StringTokenizer tokenizer = new StringTokenizer(str,",",false);
        return new Rectangle(Integer.parseInt(tokenizer.nextElement().toString()), Integer.parseInt(tokenizer.nextElement().toString()), Integer.parseInt(tokenizer.nextElement().toString()), Integer.parseInt(tokenizer.nextElement().toString()));
    }

    /**
     * Some exceptions to the default pluralization rules. (Which is... to add 's' :\)
     */
    private static Map<String, String> s_pluralizedUnitNames;
    static
    {
        s_pluralizedUnitNames = new HashMap<String, String>();
        s_pluralizedUnitNames.put("armour", "armour");
        s_pluralizedUnitNames.put("infantry", "infantry");
        s_pluralizedUnitNames.put("Infantry", "Infantry");
        s_pluralizedUnitNames.put("artillery", "artilleries");
        s_pluralizedUnitNames.put("factory", "factories");
    }
    public static String pluralizeUnitName(String unitName, int quantity)
    {
        if (quantity == -1 || quantity == 1)
            return unitName;
        return getPluralOfUnitName(unitName);
    }
    public static String getPluralOfUnitName(String unitName)
    {
        if (s_pluralizedUnitNames.containsKey(unitName))
            return s_pluralizedUnitNames.get(unitName);
        if (unitName.endsWith("man"))
            return unitName.substring(0, unitName.lastIndexOf("man")) + "men";

        return unitName + "s";
    }
    
    /**
     * Meant to duplicate the String.format method I used frequently in Microsoft Visual C#.
     * (The String.format method in java doesn't seem to replace {0} with the first argument, {1} with the second, etc.)
     */
    public static String Format(String message, Object... args)
    {
        int count = 0;
        for (Object obj : args)
        {
            message = message.replace("{".concat(Integer.toString(count)).concat("}"), "" + obj);
            count++;
        }
        return message;
    }
    
    /**
     * Meant to duplicate the String.format method I used frequently in Microsoft Visual C#.
     * (The String.format method in java doesn't seem to replace {0} with the first argument, {1} with the second, etc.)
     * This NL variant adds the end-of-line marker to the end of the message returned.
     */
    public static String Format_NL(String message, Object ... args)
    {
        return Format(message, args) + "\r\n";
    }
}