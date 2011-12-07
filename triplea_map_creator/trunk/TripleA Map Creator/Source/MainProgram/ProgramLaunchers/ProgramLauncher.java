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

import LauncherStub.LauncherStub;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Window;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * The main class of the application. Note that this is not directly called by th exe and jar, but through LauncherStub.
 */
public class ProgramLauncher
{
    public static Image getProgramIcon(Window frame)
    {
        Image img = null;
        try
        {
            img = new ImageIcon(ProgramLauncher.class.getResource("x32.png")).getImage();

            MediaTracker tracker = new MediaTracker(frame);
            tracker.addImage(img, 0);

            tracker.waitForAll();
        }
        catch (Exception ex)
        {
            System.out.println("Error loading program icon.");
        }
        return img;
    }
    
    public static boolean isWindows()
    {
        return System.getProperties().getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

    public static boolean isMac()
    {
        return System.getProperties().getProperty("os.name").toLowerCase().indexOf("mac") != -1;
    }
    
    /**
     * Get version number of Java VM.
     *
     * @author NeKromancer
     */
    private static void checkJavaVersion()
    {
        String version = System.getProperties().getProperty("java.version");
        boolean v12 = version.indexOf("1.2") != -1;
        boolean v13 = version.indexOf("1.3") != -1;
        boolean v14 = version.indexOf("1.4") != -1;
        boolean v15 = version.indexOf("1.5") != -1;
        
        if (v12 || v13 || v14 || v15)
        {
            JOptionPane.showMessageDialog(null, "TripleA Map Creator requires a java runtime greater than or equal to 6.0.\nPlease download a newer version of java from http://java.sun.com/", "Java Outdated", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    public static void main(String[] args)
    {
        //Signal success to launcher stub
        Preferences.userNodeForPackage(LauncherStub.class).putBoolean(LauncherStub.LAUNCH_SUCCEEDED, true);
        
        //Make sure Java is new enough to run this program
        checkJavaVersion();

        ProgramLauncher2.main(args);
    }
}
