/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Stephen
 */
public class Images
{
    public static Boolean CheckForColoredIntPixels(Point p1, BufferedImage i1, Point p2, BufferedImage i2, Color cColor)
    {
        for(int x = 0;x < i1.getWidth();x++)
        {
            if(x + p1.x < p2.x || x + p1.x > p2.x + (i2.getWidth() - 1))
                continue;
            for (int y = 0; y < i1.getHeight(); y++)
            {
                if(y + p1.y < p2.y || y + p1.y > p2.y + (i2.getHeight() - 1))
                    continue;
                int c1 = i1.getRGB(x, y);
                int c2 = i2.getRGB(x - (p2.x - p1.x), y - (p2.y - p1.y));
                if(c1 == cColor.getRGB() && c2 == cColor.getRGB())
                    return true;
            }
        }
        return false;
    }
}
