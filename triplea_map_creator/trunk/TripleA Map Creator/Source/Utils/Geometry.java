/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author Stephen
 */
public class Geometry
{
    public static ArrayList<String> GetPolygonChildren(List<Polygon> polys, TreeMap<String, List<Polygon>> polyCol)
    {
        ArrayList<String> result = new ArrayList<String>();
        for (Polygon poly : polys)
        {
            for (Entry<String, List<Polygon>> entry : polyCol.entrySet())
            {
                for (Polygon p : entry.getValue())
                {
                    if (doesPolyContainPoly(poly, p))
                    {
                        result.add(entry.getKey());
                    }
                }
            }
        }
        return result;
    }
    public static Boolean doesPolyContainPoly(Polygon p1, Polygon p2)
    {
        Point p;
        for(int i = 0; i < p2.npoints;i++)
        {
            p = new Point(p2.xpoints[i],p2.ypoints[i]);
            if(!p1.contains(p))
                return false;
        }
        return true;
    }
    public static String findNotSharedTName(String orig, List<Polygon> tPolygons, TreeMap<String,List<Polygon>> polygons, TreeMap<String, Point> territories)
    {
        for (Entry<String, Point> entry : territories.entrySet())
        {
            for (Polygon p : tPolygons)
            {
                if (p.contains(entry.getValue()))
                {
                    Boolean isMultiUsed = false;
                    for (List<Polygon> cur : polygons.values())
                    {
                        for (Polygon curP : cur)
                        {
                            Boolean isSelf = false;
                            for (Polygon p2 : tPolygons)
                            {
                                if (cur == p2)
                                {
                                    isSelf = true;
                                    continue;
                                }
                            }
                            if (!isSelf && curP.contains(entry.getValue()))
                            {
                                isMultiUsed = true;
                                continue;
                            }
                        }
                    }
                    if (!isMultiUsed)
                    {
                        return entry.getKey();
                    }
                }
            }
        }
        return orig;
    }
    public static Rectangle InflatedRect(Rectangle base, int amount)
    {
        return new Rectangle(base.x - amount, base.y - amount, base.width + (amount * 2), base.height + (amount * 2));
    }
    public static Polygon GetFirstIntPoly(Point point, TreeMap<String, List<Polygon>> possibles)
    {
        for (Entry<String, List<Polygon>> entry : possibles.entrySet())
        {
            for (Polygon poly : entry.getValue())
            {
                if (poly.contains(point))
                {
                    return poly;
                }
            }
        }
        return null;
    }
    public static Rectangle GetRectOfPolygons(List<Polygon> polygons)
    {
        Rectangle rect = polygons.get(0).getBounds();
        for(Polygon poly : polygons)
        {
            rect = GetRectangleWithExtenderPoint(rect, poly.getBounds());
        }
        return rect;
    }
    public static Rectangle GetRectangleWithExtenderPoint(Rectangle base, Rectangle rect)
    {
        Rectangle result = new Rectangle(rect.getLocation(), rect.getSize());
        result = GetRectangleWithExtenderPoint(result, new Point(rect.x, rect.y));
        result = GetRectangleWithExtenderPoint(result, new Point(rect.x + rect.width, rect.y));
        result = GetRectangleWithExtenderPoint(result, new Point(rect.x + rect.width, rect.y + rect.height));
        result = GetRectangleWithExtenderPoint(result, new Point(rect.x, rect.y + rect.height));
        return result;
    }
    public static Rectangle GetRectangleWithExtenderPoint(Rectangle base, Point p)
    {
        int lx = 0;
        int ly = 0;
        int hx = 0;
        int hy = 0;
        lx = Math.min(base.x, p.x);
        hx = Math.max(base.x + base.width, p.x);
        ly = Math.min(base.y, p.y);
        hy = Math.max(base.y + base.height, p.y);
        return new Rectangle(lx, ly, hx - lx + 1, hy - ly + 1);
    }
}
