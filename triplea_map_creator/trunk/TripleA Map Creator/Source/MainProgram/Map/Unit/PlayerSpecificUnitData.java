/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Map.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 *
 * @author Stephen
 */
public class PlayerSpecificUnitData implements Serializable
{
    private boolean m_purchasable = false;
    private ImageIcon m_image = null;
    private int m_cost = 0;
    private String m_playerName = "";

    public PlayerSpecificUnitData()
    {
    }
    public PlayerSpecificUnitData(String playerName, boolean purchasable, BufferedImage image, int cost)
    {
        m_playerName = playerName;
        m_purchasable = purchasable;
        m_image = new ImageIcon(image);
        m_cost = cost;
    }
    public boolean GetPurchasable()
    {
        return m_purchasable;
    }
    public void SetPurchasable(boolean purchasable)
    {
        m_purchasable = purchasable;
    }
    public String GetPlayerName()
    {
        return m_playerName;
    }
    public void SetPlayerName(String playerName)
    {
        m_playerName = playerName;
    }
    public BufferedImage GetImage()
    {
        if(m_image.getImage() instanceof BufferedImage)
            return (BufferedImage)m_image.getImage();
        Image image = m_image.getImage();
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics m_graphics = bimage.createGraphics();
        m_graphics.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
        m_image = new ImageIcon(bimage);
        return bimage;
    }
    public void SetImage(BufferedImage image)
    {
        m_image = new ImageIcon(image);
    }
    public int GetCost()
    {
        return m_cost;
    }
    public void SetCost(int cost)
    {
        m_cost = cost;
    }
}
