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

package MainProgram.ProgramLaunchers;

import Global.ProgramSettings;
import MainProgram.Console.ErrorConsole;
import MainProgram.UI.MainMenu;
import java.util.logging.LogManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class ProgramLauncher2
{
    public static void main(String[] args)
    {
        setupLookAndFeel(); //We want the error console to look nice, so we init look and feel before it
        setupLogging();        

        handleCommandLineArgs(args);
        showMainFrame();
    }

    private static void showMainFrame()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MainMenu main = new MainMenu();
                main.setVisible(true);
                main.requestFocus();
                main.toFront();
            }
        });
    }

    public static final String DEBUG_MODE = "Debug Mode"; //Atm, only used for checking if argument handling works    
    /**
     * Move command line arguments to System.properties
     */
    private static void handleCommandLineArgs(String[] args)
    {
        String[] properties = new String[] {DEBUG_MODE};
        
        //If only 1 arg, it must be the DEBUG_MODE option. It might not start with the property name, but we use it anyway
        if(args.length == 1)
        {
            String value;
            if(args[0].startsWith(DEBUG_MODE))
                value = getValue(DEBUG_MODE);
            else
                value = args[0];
            System.setProperty(DEBUG_MODE, value);
            return;
        }
        
        boolean usagePrinted = false;
        for(int argIndex = 0; argIndex < args.length; argIndex++)
        {
            boolean found = false;
            for(int propIndex = 0; propIndex < properties.length; propIndex++)
            {
                if(args[argIndex].startsWith(properties[propIndex]))
                {
                    String value = getValue(args[argIndex]);
                    System.getProperties().setProperty(properties[propIndex], value);
                    System.out.println(properties[propIndex] + ":" + value );
                    found = true;
                }
            }
            if(!found)
            {
                if(!usagePrinted)
                {
                    usagePrinted = true;
                    usage();
                }
                System.out.println("Unrecogized argument: " + args[argIndex]);
            }
        }
    }

    private static void usage()
    {
        System.out.println("Arguments:\r\n"
                + "    debugmode=true\r\n"
                + "\r\n"
                + "Examples:\r\n"
                + "\r\n"
                + "To start the program in debug mode:\r\n"
                + "    debugmode=true"
                );
    }

    private static String getValue(String arg)
    {
        int index = arg.indexOf('=');
        if(index == -1)
            return "";
        return arg.substring(index + 1);
    }

    public static void setupLookAndFeel()
    {
        final ProgramSettings settings = ProgramSettings.LoadSettings();
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(settings.ProgramLookAndFeelClassName);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };   
        try
        {
            SwingUtilities.invokeAndWait(runner);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void setupLogging()
    {
        //Setup logging to read our logging.properties (in the data folder)
        try
        {
            //LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        //Found out this is all we really need
        ErrorConsole.getConsole().setupErrorStreamListening();
        //ErrorConsole.getConsole().setupOutputStreamListening();
        
        //System.setProperty("sun.awt.exception.handler", ErrorHandler.class.getName());
    }
}
