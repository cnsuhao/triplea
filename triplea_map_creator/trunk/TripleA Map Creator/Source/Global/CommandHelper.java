/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

import java.io.InputStream;

/**
 *
 * @author Stephen
 */
public class CommandHelper
{
    public static Process executeCommandAndGetResultingProcess(String command)
    {
        Process p = null;
        try
        {
            p = new ProcessBuilder(command).start();
        }
        catch (Exception ex)
        {
            try
            {
                p = Runtime.getRuntime().exec(command);
            }
            catch (Exception ex2)
            {
            }
        }
        return p;
    }
    
    public static InputStream executeCommandAndGetInputStream(String command)
    {
        Process p = null;
        try
        {
            p = new ProcessBuilder(command).start();
        }
        catch (Exception ex)
        {
            try
            {
                p = Runtime.getRuntime().exec(command);
            }
            catch (Exception ex2)
            {
            }
        }
        if(p == null)
            return null;
        try
        {
            InputStream in = p.getInputStream();
            return in;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
