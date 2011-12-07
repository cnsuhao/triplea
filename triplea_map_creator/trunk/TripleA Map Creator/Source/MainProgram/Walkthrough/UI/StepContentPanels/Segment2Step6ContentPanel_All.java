/*
 * Segment1Step1ContentPanel.java
 *
 * Created on May 14, 2010, 5:34:30 PM
 */

package MainProgram.Walkthrough.UI.StepContentPanels;

import Global.Constants;
import MainProgram.Map.MapData;
import MainProgram.Map.Territory.TerritoryData;
import Utils.Geometry;
import Utils.Others;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author Stephen
 */
public class Segment2Step6ContentPanel_All extends BaseStepContentPanel
{
    private BufferedImage m_image = null;
    private Graphics2D m_graphics = null;
    private TreeMap<String, TerritoryData> m_data = new TreeMap<String, TerritoryData>();
    
    private String m_currentTer = null;
    
    /** Creates new form Segment2Step6ContentPanel */
    public Segment2Step6ContentPanel_All()
    {
        initComponents();
    }
    private String GetTerritoryClicked(Point point, TreeMap<String, List<Polygon>> possibles)
    {
        String smallest = null;
        Rectangle sB = new Rectangle(0,0, 9999999,9999999);
        for(Entry<String, List<Polygon>> entry : possibles.entrySet())
        {
            Boolean intersects = false;
            Rectangle combined = null;
            for(Polygon poly : entry.getValue())
            {
                if(combined == null)
                    combined = poly.getBounds();
                else
                    combined = Geometry.GetRectangleWithExtenderPoint(combined, poly.getBounds());
                if(poly.contains(point))
                    intersects = true;
            }
            if(intersects)
            {
                if(sB.width * sB.height > combined.width * combined.height)
                {
                    smallest = entry.getKey();
                    sB = combined;
                }
            }
        }
        return smallest;
    }
    public static TerritoryData TrimTData(TerritoryData fullData)
    {
        if(fullData == null)
            return null;
        TerritoryData result = (TerritoryData)Others.cloneObject(fullData);
        if(fullData.IsWater())
        {
            result.SetCapitalTo(null);
            result.SetImpassable(false);
            result.SetProduction(0);
            result.SetVictoryCity(false);
        }
        else
        {
            result.SetBlockadeZone(false);
        }
        
        return result;
    }
    @Override
    public boolean WaitForPanelClose()
    {        
        return true;
    }
    @Override
    public void InitControls()
    {
        VerifyData(GetMapData().GetTData());
        m_data = GetMapData().GetTData();
        SetImage(GetMapData().GetBaseMapImage().getImage());
        ScrollPaneToLastMapPos(v_scrollPane);
    }
    private void VerifyData(TreeMap<String, TerritoryData> data)
    {
        MapData mapData = GetMapData();
        ArrayList<String> problemTers = new ArrayList<String>();
        for (Entry<String, TerritoryData> tData : data.entrySet())
        {
            String owner = tData.getValue().GetOwner();
            String capitalTo = tData.getValue().GetCapitalTo();
            if (!mapData.GetTDefinitions().containsKey(tData.getKey()) || (owner != null && !mapData.GetPlayersData().containsKey(owner)) || (capitalTo != null && !mapData.GetPlayersData().containsKey(capitalTo)))
                problemTers.add(tData.getKey());
        }
        if (problemTers.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the data refers to territories or players that no longer exist. Do you want to remove all of these territory data assignments?", "Remove Invalid Territory Data", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : problemTers)
                    mapData.GetTData().remove(name);
            }
        }
        
        //Now loop through the territories and set tData to a new instance wherever missing
        for(String ter : mapData.GetTDefinitions().keySet())
        {
            if(!data.containsKey(ter))
                data.put(ter, new TerritoryData());
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        if (m_currentTer != null)
        {
            TerritoryData newData = CreateTDataFromDataEnterArea();
            m_data.put(m_currentTer, newData);
        }
        GetMapData().SetTData(m_data);
        m_walkthroughWindow.GetProject().GetProjectInfo().LastMapDisplayAreaPoint = v_scrollPane.getViewport().getViewPosition();
    }
    private void SetImage(BufferedImage image)
    {
        m_image = image;
        m_graphics = image.createGraphics();
        m_graphics.setBackground(Color.white);
        AbsoluteLayout layout = (AbsoluteLayout) v_scrollPanePanel.getLayout();
        layout.removeLayoutComponent(v_drawingLabel);
        layout.addLayoutComponent(v_drawingLabel, new AbsoluteConstraints(new Point(0, 0), new Dimension(image.getWidth(), image.getHeight())));
        v_scrollPanePanel.setLayout(layout);
        v_drawingLabel.setIcon(new ImageIcon(image));
        //v_drawingLabel.update(v_drawingLabel.getGraphics());
    }
    private void SetImage(Image image)
    {
        BufferedImage bimage = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
        m_graphics = bimage.createGraphics();
        m_graphics.drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this), this);
        SetImage(bimage);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        v_landWaterGroup = new javax.swing.ButtonGroup();
        v_scrollPane = new javax.swing.JScrollPane();
        v_scrollPanePanel = new javax.swing.JPanel();
        v_drawingLabel = new javax.swing.JLabel()
        {
            public void paint(Graphics graphics)
            {
                Graphics2D g = (Graphics2D)graphics;
                //super.paint(g);
                g.drawImage(m_image, 0,0, this);
                g.setFont(Constants.MapFont);
                for (Entry<String, List<Polygon>> entry : GetMapData().GetTPolygons().entrySet())
                {
                    String territory = entry.getKey();
                    TerritoryData tData = TrimTData(m_data.get(territory));
                    if (tData == null)
                    continue;

                    List<Polygon> polygons = entry.getValue();
                    Color pColor = null;
                    if(tData.GetOwner() != null && GetMapData().GetPlayersData().containsKey(tData.GetOwner()))
                    pColor = GetMapData().GetPlayersData().get(tData.GetOwner()).GetColor();

                    if(pColor != null)
                    g.setColor(new Color(pColor.getRed(), pColor.getGreen(), pColor.getBlue(), m_ownerColorTransparency));
                    else
                    g.setColor(new Color(0,0,0,0)); //Transparent, for water and unowned ters

                    if(territory.equals(m_currentTer))
                    g.setColor(Color.red); //We always color the selected ter red

                    for (Polygon item : polygons)
                    g.fillPolygon(item.xpoints, item.ypoints, item.npoints);
                }
                g.setColor(Color.black);
                for(String ter : GetMapData().GetTDefinitions().keySet())
                {
                    Point center = GetMapData().GetTDefinitions().get(ter);
                    if (m_displayInfoOnscreen)
                    {
                        TerritoryData tData = TrimTData(m_data.get(ter));
                        if (tData == null)
                        continue;
                        int fontHeight = (int)g.getFont().getStringBounds(ter, g.getFontRenderContext()).getHeight();

                        String s1 = "+" + Integer.toString(tData.GetProduction()) + " " + (!tData.IsWater() ? "Land" : "Water") + " owned by " + tData.GetOwner();
                        g.drawString(s1, center.x + 10, center.y + 5 + fontHeight);
                        String s2 = (tData.IsImpassable() ? "Impassable, " : "") + (tData.IsVictoryCity() ? "VC, " : "") + (tData.IsBlockadeZone() ? "Blockade Zone, " : "") + (tData.GetCapitalTo() != null ? "Capital To: " + tData.GetCapitalTo() + ", ": "");
                        s2 = s2.substring(0, Math.max(s2.length() - 2, 0));
                        g.drawString(s2, center.x + 10, center.y + 5 + fontHeight + fontHeight);
                    }
                }
                g.setFont(Constants.MapFont_Bold);
                for(String ter : GetMapData().GetTDefinitions().keySet())
                {
                    Point center = GetMapData().GetTDefinitions().get(ter);
                    g.drawString(ter, center.x + 10, center.y + 5);
                }
                g.setColor(Color.black);
                for(Point p : GetMapData().GetTDefinitions().values())
                {
                    g.fillOval(p.x - 5, p.y - 5, 10, 10);
                }
                g.setColor(Constants.SafeGreenColor);
                for(Point p : GetMapData().GetTDefinitions().values())
                {
                    g.fillOval(p.x - 3, p.y - 3, 6, 6);
                }
            }
        };
        v_toolbarPanel = new javax.swing.JPanel();
        v_clearScreenButton = new javax.swing.JButton();
        v_displayInfoOnscreen = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        v_territoryDataPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        v_terOwnerCM = new javax.swing.JComboBox();
        v_terIsImpassable = new javax.swing.JCheckBox();
        v_terIsVictoryCity = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        v_terCapitalToCB = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        v_terProduction = new javax.swing.JSpinner();
        v_terIsBlockadeZone = new javax.swing.JCheckBox();
        v_terIsLand = new javax.swing.JRadioButton();
        v_terIsWater = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        v_ownerColorTransparency = new javax.swing.JComboBox();

        v_scrollPane.setAutoscrolls(true);
        v_scrollPane.setName("v_scrollPane"); // NOI18N

        v_scrollPanePanel.setAutoscrolls(true);
        v_scrollPanePanel.setMinimumSize(new java.awt.Dimension(0, 0));
        v_scrollPanePanel.setName("v_scrollPanePanel"); // NOI18N
        v_scrollPanePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        v_drawingLabel.setName("v_drawingLabel"); // NOI18N
        v_drawingLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                v_drawingLabeldrawingMouseDown(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                v_drawingLabeldrawingMouseReleased(evt);
            }
        });
        v_drawingLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                v_drawingLabeldrawingMouseDragged(evt);
            }
        });
        v_scrollPanePanel.add(v_drawingLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 400));

        v_scrollPane.setViewportView(v_scrollPanePanel);

        v_toolbarPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        v_toolbarPanel.setName("v_toolbarPanel"); // NOI18N
        v_toolbarPanel.setPreferredSize(new java.awt.Dimension(725, 62));
        v_toolbarPanel.setRequestFocusEnabled(false);
        v_toolbarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment2Step6ContentPanel_All.class, this);
        v_clearScreenButton.setAction(actionMap.get("ClearScreen")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step6ContentPanel_All.class);
        v_clearScreenButton.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_clearScreenButton.setText(resourceMap.getString("v_clearScreenButton.text")); // NOI18N
        v_clearScreenButton.setToolTipText(resourceMap.getString("v_clearScreenButton.toolTipText")); // NOI18N
        v_clearScreenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_clearScreenButton.setName("v_clearScreenButton"); // NOI18N
        v_clearScreenButton.setNextFocusableComponent(v_displayInfoOnscreen);
        v_toolbarPanel.add(v_clearScreenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 69, 69));

        v_displayInfoOnscreen.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_displayInfoOnscreen.setSelected(true);
        v_displayInfoOnscreen.setText(resourceMap.getString("v_displayInfoOnscreen.text")); // NOI18N
        v_displayInfoOnscreen.setToolTipText(resourceMap.getString("v_displayInfoOnscreen.toolTipText")); // NOI18N
        v_displayInfoOnscreen.setName("v_displayInfoOnscreen"); // NOI18N
        v_displayInfoOnscreen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_displayInfoOnscreenStateChanged(evt);
            }
        });
        v_toolbarPanel.add(v_displayInfoOnscreen, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 10, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(4, 68));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        v_toolbarPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 0, 4, 74));

        v_territoryDataPanel.setName("v_territoryDataPanel"); // NOI18N
        v_territoryDataPanel.setOpaque(false);

        jLabel2.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        v_terOwnerCM.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terOwnerCM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        v_terOwnerCM.setName("v_terOwnerCM"); // NOI18N

        v_terIsImpassable.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terIsImpassable.setText(resourceMap.getString("v_terIsImpassable.text")); // NOI18N
        v_terIsImpassable.setName("v_terIsImpassable"); // NOI18N

        v_terIsVictoryCity.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terIsVictoryCity.setText(resourceMap.getString("v_terIsVictoryCity.text")); // NOI18N
        v_terIsVictoryCity.setName("v_terIsVictoryCity"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        v_terCapitalToCB.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terCapitalToCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        v_terCapitalToCB.setName("v_terCapitalToCB"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        v_terProduction.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terProduction.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        v_terProduction.setName("v_terProduction"); // NOI18N

        v_terIsBlockadeZone.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terIsBlockadeZone.setText(resourceMap.getString("v_terIsBlockadeZone.text")); // NOI18N
        v_terIsBlockadeZone.setEnabled(false);
        v_terIsBlockadeZone.setName("v_terIsBlockadeZone"); // NOI18N

        v_landWaterGroup.add(v_terIsLand);
        v_terIsLand.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terIsLand.setSelected(true);
        v_terIsLand.setText(resourceMap.getString("v_terIsLand.text")); // NOI18N
        v_terIsLand.setName("v_terIsLand"); // NOI18N
        v_terIsLand.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_terIsLandStateChanged(evt);
            }
        });

        v_landWaterGroup.add(v_terIsWater);
        v_terIsWater.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_terIsWater.setText(resourceMap.getString("v_terIsWater.text")); // NOI18N
        v_terIsWater.setName("v_terIsWater"); // NOI18N

        javax.swing.GroupLayout v_territoryDataPanelLayout = new javax.swing.GroupLayout(v_territoryDataPanel);
        v_territoryDataPanel.setLayout(v_territoryDataPanelLayout);
        v_territoryDataPanelLayout.setHorizontalGroup(
            v_territoryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_territoryDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_territoryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_territoryDataPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_terOwnerCM, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_terCapitalToCB, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_terProduction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(v_territoryDataPanelLayout.createSequentialGroup()
                        .addComponent(v_terIsLand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_terIsWater)
                        .addGap(18, 18, 18)
                        .addComponent(v_terIsImpassable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_terIsVictoryCity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_terIsBlockadeZone)))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        v_territoryDataPanelLayout.setVerticalGroup(
            v_territoryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_territoryDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_territoryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(v_terIsLand)
                    .addComponent(v_terIsWater)
                    .addComponent(v_terIsImpassable)
                    .addComponent(v_terIsVictoryCity)
                    .addComponent(v_terIsBlockadeZone))
                .addGap(12, 12, 12)
                .addGroup(v_territoryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(v_terOwnerCM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(v_terCapitalToCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(v_terProduction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        v_toolbarPanel.add(v_territoryDataPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 590, 73));

        jLabel1.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        v_toolbarPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 33, -1, 20));

        v_ownerColorTransparency.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_ownerColorTransparency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0%", "50%", "75%", "100%" }));
        v_ownerColorTransparency.setSelectedItem("100%");
        v_ownerColorTransparency.setName("v_ownerColorTransparency"); // NOI18N
        v_ownerColorTransparency.setNextFocusableComponent(v_terIsLand);
        v_ownerColorTransparency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_ownerColorTransparencyActionPerformed(evt);
            }
        });
        v_toolbarPanel.add(v_ownerColorTransparency, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 33, 70, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
            .addComponent(v_toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    Point m_origScrollPoint = null;
    Point m_lastPoint = new Point(0,0);
    private void v_drawingLabeldrawingMouseDown(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseDown
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseDown
        if (evt.getButton() == evt.BUTTON1)
        {
            m_origScrollPoint = null;
        }
        else
        {
            m_origScrollPoint = v_scrollPane.getViewport().getViewPosition();
            m_lastPoint = evt.getLocationOnScreen();
        }
}//GEN-LAST:event_v_drawingLabeldrawingMouseDown
    private void UpdateDataEnterArea(String territory)
    {
        TerritoryData tData = m_data.get(territory);        
        if(tData.IsWater())
            v_terIsWater.doClick();
        else
            v_terIsLand.doClick();
        v_terIsImpassable.setSelected(tData.IsImpassable());
        v_terIsVictoryCity.setSelected(tData.IsVictoryCity());
        
        List<String> players = new ArrayList<String>(GetMapData().GetPlayersData().keySet());
        Collections.sort(players);
        players.add("None/Neutral");
        v_terOwnerCM.setModel(new DefaultComboBoxModel(players.toArray()));
        v_terCapitalToCB.setModel(new DefaultComboBoxModel(players.toArray()));
        
        v_terOwnerCM.setSelectedItem(tData.GetOwner());
        if(tData.GetOwner() == null)
            v_terOwnerCM.setSelectedIndex(players.size() - 1);
        v_terCapitalToCB.setSelectedItem(tData.GetCapitalTo());
        if(tData.GetCapitalTo() == null)
            v_terCapitalToCB.setSelectedIndex(players.size() - 1);
        v_terProduction.setValue(tData.GetProduction());
    }
    
    private TerritoryData CreateTDataFromDataEnterArea()
    {
        try{v_terProduction.commitEdit();}catch(Exception ex){}
        String owner = null;
        if(v_terOwnerCM.getSelectedItem() != null && !v_terOwnerCM.getSelectedItem().toString().equals("None/Neutral"))
            owner = v_terOwnerCM.getSelectedItem().toString();
        String capitalTo = null;
        if(v_terCapitalToCB.getSelectedItem() != null && !v_terCapitalToCB.getSelectedItem().toString().equals("None/Neutral"))
            capitalTo = v_terCapitalToCB.getSelectedItem().toString();
        
        TerritoryData result = new TerritoryData(m_currentTer, v_terIsWater.isSelected(), v_terIsImpassable.isSelected(), v_terIsVictoryCity.isSelected(), v_terIsBlockadeZone.isSelected(), owner, capitalTo, Integer.parseInt(v_terProduction.getValue().toString()));
        
        return result;
    }
    
    private void v_drawingLabeldrawingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseReleased
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseReleased
        if (evt.getButton() == evt.BUTTON1 || (m_lastPoint.x == evt.getXOnScreen() && m_lastPoint.y == evt.getYOnScreen()))
        {
            if(m_currentTer != null)
            {
                TerritoryData newData = CreateTDataFromDataEnterArea();
                m_data.put(m_currentTer, newData);
            }
            m_currentTer = GetTerritoryClicked(evt.getPoint(), GetMapData().GetTPolygons());
            UpdateDataEnterArea(m_currentTer);
            v_drawingLabel.repaint();
        }
        v_drawingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_v_drawingLabeldrawingMouseReleased

    private void v_drawingLabeldrawingMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseDragged
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseDragged
        if(m_lastPoint == null) //Mouse held from another step
            return;
        if (m_origScrollPoint != null && evt.getButton() != evt.BUTTON1 && (v_drawingLabel.getWidth() > v_scrollPane.getWidth() || v_drawingLabel.getHeight() > v_scrollPane.getHeight()))
        {
            v_drawingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            v_scrollPane.getViewport().setViewPosition(LimitedImagePoint(new Point(m_origScrollPoint.x + (m_lastPoint.x - evt.getXOnScreen()), m_origScrollPoint.y + (m_lastPoint.y - evt.getYOnScreen()))));
        }
}//GEN-LAST:event_v_drawingLabeldrawingMouseDragged
    private boolean m_displayInfoOnscreen = true;
    private int m_ownerColorTransparency = 255;
    private void v_displayInfoOnscreenStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_v_displayInfoOnscreenStateChanged
    {//GEN-HEADEREND:event_v_displayInfoOnscreenStateChanged
        m_displayInfoOnscreen = v_displayInfoOnscreen.isSelected();
    }//GEN-LAST:event_v_displayInfoOnscreenStateChanged

    private void v_terIsLandStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_v_terIsLandStateChanged
    {//GEN-HEADEREND:event_v_terIsLandStateChanged
        v_terIsImpassable.setEnabled(v_terIsLand.isSelected());
        v_terIsVictoryCity.setEnabled(v_terIsLand.isSelected());
        v_terIsBlockadeZone.setEnabled(!v_terIsLand.isSelected());
        
        v_terOwnerCM.setEnabled(v_terIsLand.isSelected());
        v_terCapitalToCB.setEnabled(v_terIsLand.isSelected());
        v_terProduction.setEnabled(v_terIsLand.isSelected());
    }//GEN-LAST:event_v_terIsLandStateChanged

    private void v_ownerColorTransparencyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_ownerColorTransparencyActionPerformed
    {//GEN-HEADEREND:event_v_ownerColorTransparencyActionPerformed
        int newTransparency = 0;
        switch (v_ownerColorTransparency.getSelectedIndex())
        {
            case 0:
            {
                newTransparency = 0;
                break;
            }
            case 1:
            {
                newTransparency = 128;
                break;
            }
            case 2:
            {
                newTransparency = 192;
                break;
            }
            default:
            {
                newTransparency = 255;
                break;
            }
        }
        if(newTransparency != m_ownerColorTransparency)
        {
            m_ownerColorTransparency = newTransparency;
            v_drawingLabel.repaint();
        }
    }//GEN-LAST:event_v_ownerColorTransparencyActionPerformed
    private Point LimitedImagePoint(Point p)
    {
        Point r = new Point(p.x + v_scrollPane.getViewport().getWidth() < m_image.getWidth() ? p.x : m_image.getWidth() - 1 - v_scrollPane.getViewport().getWidth(),p.y + v_scrollPane.getViewport().getHeight() < m_image.getHeight() ? p.y : m_image.getHeight() - 1 - v_scrollPane.getViewport().getHeight());
        return new Point(r.x > 0 ? r.x : 0, r.y > 0 ? r.y : 0);
    }

    @Action
    public void ClearScreen()
    {
        if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "All progress will be lost if you clear the territory data.\r\n\r\nAre you sure you want to restart?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
        {
            m_data.clear();
            v_drawingLabel.repaint();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton v_clearScreenButton;
    private javax.swing.JCheckBox v_displayInfoOnscreen;
    private javax.swing.JLabel v_drawingLabel;
    private javax.swing.ButtonGroup v_landWaterGroup;
    private javax.swing.JComboBox v_ownerColorTransparency;
    private javax.swing.JScrollPane v_scrollPane;
    private javax.swing.JPanel v_scrollPanePanel;
    private javax.swing.JComboBox v_terCapitalToCB;
    private javax.swing.JCheckBox v_terIsBlockadeZone;
    private javax.swing.JCheckBox v_terIsImpassable;
    private javax.swing.JRadioButton v_terIsLand;
    private javax.swing.JCheckBox v_terIsVictoryCity;
    private javax.swing.JRadioButton v_terIsWater;
    private javax.swing.JComboBox v_terOwnerCM;
    private javax.swing.JSpinner v_terProduction;
    private javax.swing.JPanel v_territoryDataPanel;
    private javax.swing.JPanel v_toolbarPanel;
    // End of variables declaration//GEN-END:variables
}
