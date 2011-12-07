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

package MainProgram.Map.Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Stephen
 */
public class PlayerData implements java.io.Serializable
{    
    private String m_name = "";
    private Color m_color = null;
    private ImageIcon m_flag = null;
    private TreeMap<String, Alliance> m_joinedAlliances = new TreeMap<String, Alliance>();
    private int m_resources = 0;
    private int m_playerOrderIndex = 0;

    public PlayerData()
    {
    }
    public PlayerData(String name, Color color, int resources, BufferedImage flag, TreeMap<String, Alliance> jAlliances)
    {
        this(name, color, resources, flag, jAlliances, 0);
    }
    public PlayerData(String name, Color color, int resources, BufferedImage flag, TreeMap<String, Alliance> jAlliances, int orderIndex)
    {
        m_name = name;
        m_color = color;
        m_flag = new ImageIcon(flag);
        m_joinedAlliances = jAlliances;
        m_resources = resources;
        m_playerOrderIndex = orderIndex;
    }

    public String GetName()
    {
        return m_name;
    }
    public int GetResources()
    {
        return m_resources;
    }
    public Color GetColor()
    {
        return m_color;
    }
    public int GetOrderIndex()
    {
        return m_playerOrderIndex;
    }
    public void SetOrderIndex(int index)
    {
        m_playerOrderIndex = index;
    }
    public BufferedImage GetFlag()
    {
        if(m_flag.getImage() instanceof BufferedImage)
            return (BufferedImage)m_flag.getImage();
        Image image = m_flag.getImage();
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics m_graphics = bimage.createGraphics();
        m_graphics.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
        m_flag = new ImageIcon(bimage);
        return bimage;
    }
    public void SetFlag(BufferedImage image)
    {
        m_flag = new ImageIcon(image);
    }
    public TreeMap<String, Alliance> GetJoinedAlliances()
    {
        return m_joinedAlliances;
    }
}
