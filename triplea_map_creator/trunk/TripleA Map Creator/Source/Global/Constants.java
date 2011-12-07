/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Global;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 *
 * @author Stephen
 */
public class Constants
{
    //Program Release Information
    public static final Version ProgramVersion = new Version(2, 0, 0, 1);
    public static final Boolean IsProgramStable = false;
    public static final Version TripleAVersionAvailableAtRelease = new Version(1, 3, 2, 1);
    
    //Colors
    public static Color DefaultWaterColor = GetRGBColor(174,226,251);
    public static Color FinishedSegmentBackColor = GetRGBColor(202, 230, 200);
    public static Color HighlightedSegmentBackColor = GetRGBColor(189, 209, 217);
    public static Color SelectedButtonBackColor = GetRGBColor(160,160,160);
    public static Color DefaultButtonBackColor = new Button().getBackground();
    public static Color SafeGreenColor = Color.green;

    //Fonts
    public static final Font MapFont = new Font("sansserif", Font.PLAIN, 12);
    public static final Font MapFont_Bold = new Font("sansserif", Font.BOLD, 12);
    
    //Others
    public final static int TILE_SIZE = 256;
    
    public static Color GetRGBColor(int r, int g, int b)
    {
        float[] hsbs = Color.RGBtoHSB(r, g, b, null);
        return Color.getHSBColor(hsbs[0], hsbs[1], hsbs[2]);
    }
    public static Color GetRGBColor(int color)
    {
        return Color.getColor("nothingtext", color);
    }
}