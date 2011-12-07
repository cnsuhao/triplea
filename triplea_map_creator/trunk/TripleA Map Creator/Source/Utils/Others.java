/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Stephen
 */
public class Others
{
    /**
     * Creates a deep copy of the object supplied.
     */
    public static Object cloneObject(Object obj)
    {        
        try
        {
            ByteArrayOutputStream pool = new ByteArrayOutputStream(1000);
            
            ObjectOutputStream output = new ObjectOutputStream(pool);
            output.writeObject(obj);
            output.close();
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pool.toByteArray());
            
            ObjectInputStream input = new ObjectInputStream(inputStream);
            inputStream.close();
            Object result = input.readObject();
            input.close();
            
            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }        
    }
    
    
    
    
    
    
    
    public static Color HexToColor(String hex)
    {
        // Hex to color
        int intValue = Integer.parseInt(hex, 16);
        return new Color(intValue);
    }

    public static String ColorToHex(Color color)
    {
        //Color to hex
        String rgb = Integer.toHexString(color.getRGB());
        rgb = rgb.substring(2, rgb.length());
        return rgb;
    }
    
    
    
    
    
    
    
    
    public static List ToList(Collection collection)
    {
        return new ArrayList(collection);
    }
    public static List ToList(Object[] array)
    {
        return Arrays.asList(array);
    }
    public static Object[] ToArray(Object ... toSmashIntoArray)
    {
        return toSmashIntoArray;
    }
    /**
     * First, determines if <code>map</code> contains <code>key</code>.
     * If it does, it retrieves the value(which is expected to be a List) of <code>key</code> in <code>map</code>, adds <code>obj</code> to the list,
     * and puts the updated list back into <code>map</code> using <code>key</code> as the key.
     * If it doesn't, it creates a new list, adds <code>obj</code> to the list, and puts the new list into <code>map</code> using <code>key</code> as the key.
     */
    public static void AddObjToListValueForKeyInMap(HashMap map, Object key, Object obj)
    {
        AddObjectsToListValueForKeyInMap(map, key, Collections.singletonList(obj));
    }
    
    /**
     * Same as AddObjToListValueForKeyInMap except that it does adds a list of objects instead of one object.
     */
    public static void AddObjectsToListValueForKeyInMap(HashMap map, Object key, List objs)
    {
        if (map.containsKey(key))
        {
            List<Object> newList = (List<Object>) map.get(key);
            newList.addAll(objs);
            map.put(key, newList);
        }
        else
        {
            List<Object> newList = new ArrayList<Object>();
            newList.addAll(objs);
            map.put(key, newList);
        }
    }

    /**
     * Same as AddObjectsToListValueForKeyInMap except that this method assumes the hashmap has HashSet values instead of List values
     */
    public static void AddObjectsToHashSetValueForKeyInMap(HashMap map, Object key, List objs)
    {
        if (map.containsKey(key))
        {
            HashSet<Object> newList = (HashSet<Object>) map.get(key);
            newList.addAll(objs);
            map.put(key, newList);
        }
        else
        {
            HashSet<Object> newList = new HashSet<Object>();
            newList.addAll(objs);
            map.put(key, newList);
        }
    }
}
