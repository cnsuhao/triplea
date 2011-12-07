/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Global.ProgramSettings;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Stephen
 */
public class PolygonFinder
{
    public static Polygon FindPolygonMulticheck(Component parent, BufferedImage image, Point origPoint, Boolean useAdvancedMode, Boolean showErrors)
    {
        Polygon p = null;
        ProgramSettings settings = ProgramSettings.LoadSettings();
        int disFC = 1;
        int disCh = 0;
        int curShellProcessCount = 8;
        Point point = new Point(origPoint.x,origPoint.y);
        for(int i = 0;i < settings.MaxPolyMultiSearchCycles;i++)
        {
            if(i == 0)
            {
                disFC = 1;
                p = FindPolygon(parent, image, point, useAdvancedMode, false, 0);
                if(p != null && p.contains(point))
                    return p;
                continue;
            }
            else if(i == 1)
            {
                curShellProcessCount = 0;
                disFC = 2;
                disCh++;
                point = new Point(origPoint.x,origPoint.y - 2);
            }
            else if(curShellProcessCount >= (CalcPointsInShell(disFC) / 2))
            {
                curShellProcessCount = 0;
                disFC = disFC + 2;
                disCh++;
                point = new Point(origPoint.x, origPoint.y - disFC);
            }
            curShellProcessCount++;
            point = moved(point,GetDir(i, disFC, disCh), 2);
            p = FindPolygon(parent, image, point, useAdvancedMode, false, 0);
            if(p != null && p.contains(origPoint))
                return p;
        }
        FindPolygon(parent, image, point, useAdvancedMode, showErrors, 0);
        return p;
    }
    private static int GetDir(int count, int distance, int changes)
    {
        int otherShellCount = 1;
        int testDis = 0;
        otherShellCount++;
        for(int i = 0;i < changes - 1;i++)
        {
            testDis = testDis + 2;
            otherShellCount += CalcPointsInShell(distance);
        }
        int curShellCount = count - otherShellCount;
        int fullShellCount = CalcPointsInShell(distance);
        if(curShellCount < (fullShellCount / 4))
        {
            return 2;
        }
        else if(curShellCount < (fullShellCount / 2))
        {
            return 4;
        }
        else if(curShellCount < fullShellCount - (fullShellCount /  4))
        {
            return 6;
        }
        else
        {
            return 0;
        }
    }
    private static int CalcPointsInShell(int shellDistanceFromCenter)
    {
        int result = 0;
        if(shellDistanceFromCenter == 1)
            return 8;

        result += ((shellDistanceFromCenter * 2) - 1) * 2; //Both sides and bottom and top
        result += 4; //Corners
        return result;
    }
    public static Polygon FindPolygon(Component parent, BufferedImage image, Point point, Boolean useAdvancedMode, Boolean showErrors)
    {
        return FindPolygon(parent, image, point, useAdvancedMode, showErrors, 0);
    }
    public static Polygon FindPolygon(Component parent, BufferedImage image, Point point, Boolean useAdvancedMode, Boolean showErrors, int dir)
    {
        Polygon result;
        Point failedPoint = null;
        if (!useAdvancedMode)
        {
            int color = image.getRGB(point.x, point.y);
            Point startPoint = new Point(point.x, point.y);
            while (inBounds(image, moved(startPoint,dir).x,moved(startPoint,dir).y) && image.getRGB(startPoint.x, startPoint.y) == color)
            {
                move(startPoint, dir);
            }

            List<Point> points = new ArrayList<Point>(100);
            points.add(new Point(startPoint));

            int currentDirection = 2;
            Point currentPoint = new Point(startPoint);

            int iterCount = 0;

            while (!currentPoint.equals(startPoint) || points.size() == 1)
            {
                iterCount++;

                if (iterCount > 100000)
                {
                    if(showErrors)
                        JOptionPane.showMessageDialog(parent, "Failed to grab the polygon using simple methods. Failed at click point " + point.getX() + ", " + point.getY() + " and polygon point " + currentPoint.x + ", " + currentPoint.y + ".\r\n\r\nYou can retry with the advanced polygon grabbing mode which can be selected in the toolbar.");
                    return null;
                }

                int tempDirection;

                for (int i = 2; i >= -3; i--)   //was -4
                {
                    tempDirection = (currentDirection + i) % 8;

                    if (tempDirection < 0)
                    {
                        tempDirection += 8;
                    }

                    if (isOnEdge(tempDirection, currentPoint, image, color))
                    {
                        //if we need to change our course
                        if (i != 0)
                        {
                            points.add(currentPoint);
                            currentPoint = new Point(currentPoint);
                            move(currentPoint, tempDirection);
                            currentDirection = tempDirection;
                        } else
                        {
                            move(currentPoint, currentDirection);
                        }

                        break;

                    }//if

                }//for

            }//while

            int[] xpoints = new int[points.size()];
            int[] ypoints = new int[points.size()];
            int i = 0;

            Iterator<Point> iter = points.iterator();

            while (iter.hasNext())
            {
                Point item = iter.next();
                xpoints[i] = item.x;
                ypoints[i] = item.y;
                i++;
            }

            result = new Polygon(xpoints, ypoints, xpoints.length);
        } else
        {
            Boolean failed = false;

            int color = image.getRGB(point.x, point.y);
            Point startPoint = new Point(point.x, point.y);
            while (inBounds(image, startPoint.x, startPoint.y - 1) && image.getRGB(startPoint.x, startPoint.y) == color)
            {
                startPoint.y--;
            }

            List<Point> points = new ArrayList<Point>(100);
            points.add(new Point(startPoint));

            int currentDirection = 2;
            Point currentPoint = new Point(startPoint);

            int iterCount = 0;

            ArrayList<Split> splitPoints = new ArrayList<Split>();
            TreeMap<String, String> processed = new TreeMap<String, String>();

            while (!failed && (!currentPoint.equals(startPoint) || points.size() == 1))
            {
                iterCount++;

                if (iterCount > 100000)
                {
                    failed = true;
                    failedPoint = new Point(point.x,point.y);
                    break;
                }

                int tempDirection;
                Split split = new Split(currentPoint, new ArrayList<String>());
                for (int i = 2; i >= -3; i--)   //was -4
                {
                    tempDirection = (currentDirection + i) % 8;

                    if (tempDirection < 0)
                    {
                        tempDirection += 8;
                    }

                    if (isOnEdge(tempDirection, currentPoint, image, color) && !processed.containsKey(GetPString(moved(currentPoint, tempDirection))))
                    {
                        if (sP == null)
                        {
                            //if we need to change our course
                            if (i != 0)
                            {
                                points.add(currentPoint);
                                currentPoint = new Point(currentPoint);
                                setupMove(currentPoint, tempDirection);
                                currentDirection = tempDirection;
                            } else
                            {
                                setupMove(currentPoint, currentDirection);
                            }
                        } else
                        {
                            split.DirectionsToTry.add(Integer.toString(tempDirection));
                        }
                    }//if

                }//for
                if (sP != null)
                {
                    moveSetup();
                    if (split.DirectionsToTry.size() > 0)
                    {
                        processed.put(GetPString(split.SplitPoint), "");
                        splitPoints.add(split);
                    }
                } else
                {
                    if (splitPoints.size() > 0)
                    {
                        while (splitPoints.size() > 0 && splitPoints.get(splitPoints.size() - 1).DirectionsToTry.size() == 0)
                        {
                            splitPoints.remove(splitPoints.size() - 1);
                        }
                        if (splitPoints.size() > 0)
                        {
                            currentPoint = splitPoints.get(splitPoints.size() - 1).SplitPoint;
                            currentDirection = Integer.parseInt(splitPoints.get(splitPoints.size() - 1).DirectionsToTry.get(0));
                            splitPoints.get(splitPoints.size() - 1).DirectionsToTry.remove(0);
                        } else
                        {
                            failed = true;
                            failedPoint = new Point(point.x,point.y);
                            break;
                        }
                    } else
                    {
                        failed = true;
                        failedPoint = new Point(point.x,point.y);
                        break;
                    }
                }
            }//while
            if (failed)
            {
                color = image.getRGB(point.x, point.y);
                startPoint = new Point(point.x, point.y);
                while (inBounds(image, startPoint.x, startPoint.y - 1) && image.getRGB(startPoint.x, startPoint.y) == color)
                {
                    startPoint.y--;
                }
                points = new ArrayList<Point>(100);
                points.add(new Point(startPoint));
                currentDirection = 2;
                currentPoint = new Point(startPoint);

                iterCount = 0;

                while (!currentPoint.equals(startPoint) || points.size() == 1)
                {
                    iterCount++;

                    if (iterCount > 100000)
                    {
                        if(showErrors)
                            JOptionPane.showMessageDialog(parent, "Failed to grab the polygon using advanced methods(Overflow). Failed at click point " + point.getX() + ", " + point.getY() + " and polygon points " + currentPoint.x + ", " + currentPoint.y + " and " + failedPoint.x + ", " + failedPoint.y + ".\r\n\r\nIf you can't grab the polygon for this territory after many tries, try fixing any black jut-outs in the territory border and try again. You can also try clicking on different parts of the territory and hoping it works.");
                        return null;
                    }

                    int tempDirection;

                    for (int i = 2; i >= -3; i--)   //was -4
                    {
                        tempDirection = (currentDirection + i) % 8;

                        if (tempDirection < 0)
                        {
                            tempDirection += 8;
                        }

                        if (isOnEdge(tempDirection, currentPoint, image, color))
                        {
                            //if we need to change our course
                            if (i != 0)
                            {
                                points.add(currentPoint);
                                currentPoint = new Point(currentPoint);
                                move(currentPoint, tempDirection);
                                currentDirection = tempDirection;
                            } else
                            {
                                move(currentPoint, currentDirection);
                            }

                            break;

                        }//if

                    }//for

                }//while

                int[] xpoints = new int[points.size()];
                int[] ypoints = new int[points.size()];
                int i = 0;

                Iterator<Point> iter = points.iterator();

                while (iter.hasNext())
                {
                    Point item = iter.next();
                    xpoints[i] = item.x;
                    ypoints[i] = item.y;
                    i++;
                }

                result = new Polygon(xpoints, ypoints, xpoints.length);
            }

            int[] xpoints = new int[points.size()];
            int[] ypoints = new int[points.size()];
            int i = 0;

            Iterator<Point> iter = points.iterator();

            while (iter.hasNext())
            {
                Point item = iter.next();
                xpoints[i] = item.x;
                ypoints[i] = item.y;
                i++;
            }

            result = new Polygon(xpoints, ypoints, xpoints.length);
        }
        if(result.getBounds().width <= 0 || result.getBounds().height <= 0)
            return null;
        return result;
    }

    private static String GetPString(Point p)
    {
        return Integer.toString(p.x) + "," + Integer.toString(p.y);
    }

    private static Point GetSPoint(String s)
    {
        Point p = new Point(0, 0);
        p.x = Integer.parseInt(s.substring(0, s.indexOf(",")));
        p.y = Integer.parseInt(s.substring(s.indexOf(",") + 1), s.length() - (s.indexOf(",") + 1));
        return p;
    }

    private static class Split
    {

        public Split(Point point, ArrayList<String> directionsToTry)
        {
            SplitPoint = point;
            DirectionsToTry = directionsToTry;
        }
        public Point SplitPoint = null;
        public ArrayList<String> DirectionsToTry = null;
    }

    /**
    java.lang.boolean inBounds(java.lang.int, java.lang.int)

    Checks if the given x/y coordinate point is inbounds or not

    @param java.lang.int x   the x coordinate
    @param java.lang.int y   the y coordinate

    @return java.lang.boolean
     */
    private static boolean inBounds(Image image, int x, int y)
    {
        return x >= 0 && x < image.getWidth(null) && y >= 0 && y < image.getHeight(null);
    }
    /**
    move(java.awt.Point, java.lang.int)

    Moves to a specified direction

    Directions
    0 - North
    1 - North east
    2 - East
    3 - South east
    4 - South
    5 - South west
    6 - West
    7 - North west

    @param java.awt.Point p          the given point
    @param java.lang.int  direction  the specified direction to move
     */
    static Point sP = null;
    static int sD = -1;

    private static void setupMove(Point p, int direction)
    {
        sP = p;
        sD = direction;
    }

    private static void move(Point p, int direction)
    {
        if (direction < 0 || direction > 7)
        {
            throw new IllegalArgumentException("Not a direction :" + direction);
        }

        if (direction == 1 || direction == 2 || direction == 3)
        {
            p.x++;
        } else
        {
            if (direction == 5 || direction == 6 || direction == 7)
            {
                p.x--;
            }
        }

        if (direction == 5 || direction == 4 || direction == 3)
        {
            p.y++;
        } else
        {
            if (direction == 7 || direction == 0 || direction == 1)
            {
                p.y--;
            }
        }
    }
    private static Point moved(Point old, int direction)
    {
        return moved(old, direction, 1);
    }
    private static Point moved(Point old, int direction, int amount)
    {
        Point p = new Point(old.x, old.y);
        if (direction < 0 || direction > 7)
        {
            throw new IllegalArgumentException("Not a direction :" + direction);
        }

        if (direction == 1 || direction == 2 || direction == 3)
        {
            p.x += amount;
        } else
        {
            if (direction == 5 || direction == 6 || direction == 7)
            {
                p.x -= amount;
            }
        }

        if (direction == 5 || direction == 4 || direction == 3)
        {
            p.y += amount;
        } else
        {
            if (direction == 7 || direction == 0 || direction == 1)
            {
                p.y -= amount;
            }
        }
        return p;
    }

    private static void moveSetup()
    {
        if (sD < 0 || sD > 7)
        {
            throw new IllegalArgumentException("Not a direction :" + sD);
        }

        if (sD == 1 || sD == 2 || sD == 3)
        {
            sP.x++;
        } else
        {
            if (sD == 5 || sD == 6 || sD == 7)
            {
                sP.x--;
            }
        }

        if (sD == 5 || sD == 4 || sD == 3)
        {
            sP.y++;
        } else
        {
            if (sD == 7 || sD == 0 || sD == 1)
            {
                sP.y--;
            }
        }
        sP = null;
        sD = -1;
    }
    private static Point m_testPoint = new Point();  //used below

    /**
    java.lang.boolean isOnEdge(java.lang.int, java.awt.Point)

    Checks to see if the direction we're going is on the edge.
    At least thats what I can understand from this.

    @param java.lang.int  direction     the given direction
    @param java.awt.Point currentPoint  the current point

    @return java.lang.boolean
     */
    private static boolean isOnEdge(int direction, Point point, BufferedImage image, int color)
    {
        m_testPoint.setLocation(point);
        move(m_testPoint, direction);

        return m_testPoint.x <= 0 || m_testPoint.y <= 0
                || m_testPoint.y >= image.getHeight()
                || m_testPoint.x >= image.getWidth()
                || image.getRGB(m_testPoint.x, m_testPoint.y) != color;
    }
}
