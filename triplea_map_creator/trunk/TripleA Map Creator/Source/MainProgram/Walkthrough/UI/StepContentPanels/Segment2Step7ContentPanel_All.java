/*
 * Segment1Step1ContentPanel.java
 *
 * Created on May 14, 2010, 5:34:30 PM
 */

package MainProgram.Walkthrough.UI.StepContentPanels;

import Global.Constants;
import MainProgram.Map.MapData;
import MainProgram.Map.Territory.TerritoryData;
import MainProgram.Map.Unit.PSUnitType;
import MainProgram.Map.Unit.UnitCollection;
import MainProgram.Map.Unit.UnitData;
import Utils.Geometry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.application.Action;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author Stephen
 */
public class Segment2Step7ContentPanel_All extends BaseStepContentPanel
{
    private BufferedImage m_image = null;
    private Graphics2D m_graphics = null;
    private TreeMap<String, UnitCollection> m_unitPlacements = new TreeMap<String, UnitCollection>();
    
    private String m_currentTer = null;
    
    public Segment2Step7ContentPanel_All()
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
    public int GetUnitWH()
    {
        return (int)(48 * GetMapData().GetUnitScale());
    }
    public Image ScaleUnitImage(MapData mapData, Image unitImage)
    {
        return unitImage.getScaledInstance(GetUnitWH(), GetUnitWH(), BufferedImage.SCALE_SMOOTH);
    }
    @Override
    public boolean WaitForPanelClose()
    {        
        return true;
    }
    @Override
    public void InitControls()
    {
        m_cachedUnitImages.clear();
        m_cachedUnitImages_Scaled.clear();
        VerifyUnitPlacements(GetMapData().GetTUnitPlacements());
        m_unitPlacements = GetMapData().GetTUnitPlacements();
        SetImage(GetMapData().GetBaseMapImage().getImage());
        ScrollPaneToLastMapPos(v_scrollPane);
    }
    private void VerifyUnitPlacements(TreeMap<String, UnitCollection> unitPlacements)
    {
        MapData mapData = GetMapData();
        ArrayList<String> problemTers = new ArrayList<String>();
        for (Entry<String, UnitCollection> tUnitPlacements : unitPlacements.entrySet())
        {
            String territory = tUnitPlacements.getKey();
            UnitCollection units = tUnitPlacements.getValue();
            if (!mapData.GetTDefinitions().containsKey(territory))
            {
                problemTers.add(territory);
                continue; //Don't double-add
            }
            
            for(PSUnitType unitType : units.GetUnits().keySet())
            {
                if(!mapData.GetUnitsData().containsKey(unitType.GetUnitType()) || !mapData.GetPlayersData().containsKey(unitType.GetOwner()))
                {
                    problemTers.add(territory);
                    break;
                }
            }
        }
        if (problemTers.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the unit placements refer to territories or units that no longer exist. Do you want to remove all of these territory unit placements(Clears each ter's units)?", "Remove Invalid Territory Unit Placements", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : problemTers)
                    mapData.GetTUnitPlacements().remove(name);
            }
        }
        
        //Now loop through the territories and set tUnitPlacements to a new UnitCollection instance wherever missing
        for(String ter : mapData.GetTDefinitions().keySet())
        {
            if(!unitPlacements.containsKey(ter))
                unitPlacements.put(ter, new UnitCollection());
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        if (m_currentTer != null)
        {
            UnitCollection newUnits = CreateUnitCollectionFromUnitsArea();
            m_unitPlacements.put(m_currentTer, newUnits);
        }
        GetMapData().SetTUnitPlacements(m_unitPlacements);
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
        m_graphics.drawImage(image, 0, 0, this);
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
                Graphics2D g = (Graphics2D) graphics;
                //First, draw map image
                g.drawImage(m_image, 0, 0, this);
                //Then, draw each territory's polygon using their owner's color
                for (Entry<String, List<Polygon>> entry : GetMapData().GetTPolygons().entrySet())
                {
                    String territory = entry.getKey();
                    TerritoryData tData = GetMapData().GetTData().get(territory);
                    if (tData == null)
                    continue;

                    List<Polygon> polygons = entry.getValue();
                    Color pColor = null;
                    if (tData.GetOwner() != null && GetMapData().GetPlayersData().containsKey(tData.GetOwner()))
                    pColor = GetMapData().GetPlayersData().get(tData.GetOwner()).GetColor();

                    if (pColor != null)
                    g.setColor(new Color(pColor.getRed(), pColor.getGreen(), pColor.getBlue(), m_ownerColorTransparency));
                    else
                    g.setColor(new Color(0, 0, 0, 0)); //Transparent, for water and unowned ters

                    if (territory.equals(m_currentTer))
                    g.setColor(Color.red); //We always color the selected ter red

                    for (Polygon item : polygons)
                    {
                        g.fillPolygon(item.xpoints, item.ypoints, item.npoints);
                    }
                }
                //Then loop through each ter and draw other stuff
                for (String ter : GetMapData().GetTDefinitions().keySet())
                {
                    Point center = GetMapData().GetTDefinitions().get(ter);
                    g.setColor(Color.black);
                    g.setFont(Constants.MapFont_Bold);
                    //First, draw territory name
                    g.drawString(ter, center.x + 10, center.y + 5);
                    //Now draw circle thing to show territory center
                    g.setColor(Color.black);
                    g.fillOval(center.x - 5, center.y - 5, 10, 10);
                    g.setColor(Constants.SafeGreenColor);
                    g.fillOval(center.x - 3, center.y - 3, 6, 6);

                    List<Point> unitDrawPoints = GetMapData().GetTUnitDrawPoints().get(ter);
                    Point nextOverflowDrawPoint = new Point(unitDrawPoints.get(0).x, unitDrawPoints.get(0).y);
                    Point overflowLineStartPoint = new Point(nextOverflowDrawPoint.x, nextOverflowDrawPoint.y + 52);
                    nextOverflowDrawPoint.translate(50, 0);

                    if (m_displayUnitsOnscreen)
                    {
                        UnitCollection units = m_unitPlacements.get(ter);
                        if (units == null)
                        continue;

                        HashMap<Point, Integer> countDrawPoints = new HashMap<Point, Integer>();

                        int drawnUnits = 0;
                        for (PSUnitType unitType : units.GetUnits().keySet())
                        {
                            String unitName = unitType.GetUnitType();
                            int count = units.GetUnitOfTypeXCount(unitType);
                            if(count < 1)
                            continue;
                            UnitData unitData = GetMapData().GetUnitsData().get(unitName);

                            String unitImageKey = unitType.GetOwner() + "|^|" + unitName;
                            if(!m_cachedUnitImages_Scaled.containsKey(unitImageKey))
                            {
                                ImageIcon grabbedImage = new ImageIcon(ScaleUnitImage(GetMapData(), unitData.GetPlayerSpecificUnitData().get(unitType.GetOwner()).GetImage()));
                                m_cachedUnitImages_Scaled.put(unitImageKey, grabbedImage);
                            }
                            ImageIcon unitImage = m_cachedUnitImages_Scaled.get(unitImageKey);

                            Point drawPoint = null;
                            if(unitDrawPoints.size() > drawnUnits)
                            drawPoint = unitDrawPoints.get(drawnUnits);
                            else
                            {
                                drawPoint = new Point(nextOverflowDrawPoint.x, nextOverflowDrawPoint.y);
                                g.setColor(Color.BLACK);
                                g.setStroke(new BasicStroke(3F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                g.drawLine(overflowLineStartPoint.x, overflowLineStartPoint.y, drawPoint.x + GetUnitWH(), drawPoint.y + GetUnitWH() + 4);
                                nextOverflowDrawPoint.translate(GetUnitWH() + 2, 0);
                            }

                            g.drawImage(unitImage.getImage(), drawPoint.x, drawPoint.y, this);
                            if(count > 1)
                            countDrawPoints.put(new Point(drawPoint.x + 12, drawPoint.y + GetUnitWH()), count);
                            drawnUnits++;
                        }
                        g.setColor(Color.white);
                        //Now draw unit counts
                        for(Point point : countDrawPoints.keySet())
                        {
                            g.drawString(Integer.toString(countDrawPoints.get(point)), point.x, point.y);
                        }
                    }
                }
            }
        };
        v_toolbarPanel = new javax.swing.JPanel();
        v_territoryUnitsPanel = new javax.swing.JPanel();
        v_leftToolbarArea = new javax.swing.JPanel();
        v_clearScreenButton = new javax.swing.JButton();
        v_displayUnitsOnscreen = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        v_ownerColorTransparency = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        v_territoryName = new javax.swing.JLabel();
        v_territoryUnitsScrollPane = new javax.swing.JScrollPane();
        v_territoryUnits = new javax.swing.JTextArea();
        v_clearTerUnits = new javax.swing.JButton();
        v_revert = new javax.swing.JButton();
        v_ok = new javax.swing.JButton();

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
        v_scrollPanePanel.add(v_drawingLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 300));

        v_scrollPane.setViewportView(v_scrollPanePanel);

        v_toolbarPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        v_toolbarPanel.setName("v_toolbarPanel"); // NOI18N
        v_toolbarPanel.setPreferredSize(new java.awt.Dimension(725, 62));
        v_toolbarPanel.setRequestFocusEnabled(false);
        v_toolbarPanel.setLayout(null);

        v_territoryUnitsPanel.setName("v_territoryUnitsPanel"); // NOI18N
        v_territoryUnitsPanel.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                v_territoryUnitsPanelAncestorResized(evt);
            }
        });
        v_territoryUnitsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
        v_toolbarPanel.add(v_territoryUnitsPanel);
        v_territoryUnitsPanel.setBounds(248, 3, 635, 190);

        v_leftToolbarArea.setName("v_leftToolbarArea"); // NOI18N
        v_leftToolbarArea.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment2Step7ContentPanel_All.class, this);
        v_clearScreenButton.setAction(actionMap.get("ClearScreen")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step7ContentPanel_All.class);
        v_clearScreenButton.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_clearScreenButton.setText(resourceMap.getString("v_clearScreenButton.text")); // NOI18N
        v_clearScreenButton.setToolTipText(resourceMap.getString("v_clearScreenButton.toolTipText")); // NOI18N
        v_clearScreenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_clearScreenButton.setName("v_clearScreenButton"); // NOI18N
        v_leftToolbarArea.add(v_clearScreenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 55, 55));

        v_displayUnitsOnscreen.setFont(resourceMap.getFont("v_terProduction.font")); // NOI18N
        v_displayUnitsOnscreen.setSelected(true);
        v_displayUnitsOnscreen.setText(resourceMap.getString("v_displayUnitsOnscreen.text")); // NOI18N
        v_displayUnitsOnscreen.setToolTipText(resourceMap.getString("v_displayUnitsOnscreen.toolTipText")); // NOI18N
        v_displayUnitsOnscreen.setName("v_displayUnitsOnscreen"); // NOI18N
        v_displayUnitsOnscreen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_displayUnitsOnscreenStateChanged(evt);
            }
        });
        v_leftToolbarArea.add(v_displayUnitsOnscreen, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 10, -1, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        v_leftToolbarArea.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 33, -1, 20));

        v_ownerColorTransparency.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_ownerColorTransparency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0%", "50%", "75%", "100%" }));
        v_ownerColorTransparency.setSelectedItem("100%");
        v_ownerColorTransparency.setName("v_ownerColorTransparency"); // NOI18N
        v_ownerColorTransparency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_ownerColorTransparencyActionPerformed(evt);
            }
        });
        v_leftToolbarArea.add(v_ownerColorTransparency, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 32, 60, -1));

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
            .addGap(0, 62, Short.MAX_VALUE)
        );

        v_leftToolbarArea.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 0, 4, 66));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(4, 68));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        v_leftToolbarArea.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 62, 237, 4));

        v_territoryName.setText(resourceMap.getString("v_territoryName.text")); // NOI18N
        v_territoryName.setName("v_territoryName"); // NOI18N
        v_leftToolbarArea.add(v_territoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 220, -1));

        v_territoryUnitsScrollPane.setBorder(null);
        v_territoryUnitsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        v_territoryUnitsScrollPane.setName("v_territoryUnitsScrollPane"); // NOI18N

        v_territoryUnits.setBackground(resourceMap.getColor("v_territoryUnits.background")); // NOI18N
        v_territoryUnits.setEditable(false);
        v_territoryUnits.setFont(resourceMap.getFont("v_territoryUnits.font")); // NOI18N
        v_territoryUnits.setLineWrap(true);
        v_territoryUnits.setRows(4);
        v_territoryUnits.setText(resourceMap.getString("v_territoryUnits.text")); // NOI18N
        v_territoryUnits.setWrapStyleWord(true);
        v_territoryUnits.setBorder(null);
        v_territoryUnits.setDisabledTextColor(resourceMap.getColor("v_territoryUnits.disabledTextColor")); // NOI18N
        v_territoryUnits.setEnabled(false);
        v_territoryUnits.setName("v_territoryUnits"); // NOI18N
        v_territoryUnitsScrollPane.setViewportView(v_territoryUnits);

        v_leftToolbarArea.add(v_territoryUnitsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 220, 60));

        v_clearTerUnits.setText(resourceMap.getString("v_clearTerUnits.text")); // NOI18N
        v_clearTerUnits.setToolTipText(resourceMap.getString("v_clearTerUnits.toolTipText")); // NOI18N
        v_clearTerUnits.setName("v_clearTerUnits"); // NOI18N
        v_clearTerUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_clearTerUnitsActionPerformed(evt);
            }
        });
        v_leftToolbarArea.add(v_clearTerUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 60, 30));

        v_revert.setText(resourceMap.getString("v_revert.text")); // NOI18N
        v_revert.setToolTipText(resourceMap.getString("v_revert.toolTipText")); // NOI18N
        v_revert.setEnabled(false);
        v_revert.setName("v_revert"); // NOI18N
        v_revert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_revertActionPerformed(evt);
            }
        });
        v_leftToolbarArea.add(v_revert, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 160, 80, 30));

        v_ok.setText(resourceMap.getString("v_ok.text")); // NOI18N
        v_ok.setToolTipText(resourceMap.getString("v_ok.toolTipText")); // NOI18N
        v_ok.setEnabled(false);
        v_ok.setName("v_ok"); // NOI18N
        v_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_okActionPerformed(evt);
            }
        });
        v_leftToolbarArea.add(v_ok, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 60, 30));

        v_toolbarPanel.add(v_leftToolbarArea);
        v_leftToolbarArea.setBounds(3, 3, 239, 190);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
            .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    
    private HashMap<String, ImageIcon> m_cachedUnitImages = new HashMap<String, ImageIcon>();
    private HashMap<String, ImageIcon> m_cachedUnitImages_Scaled = new HashMap<String, ImageIcon>();    
    private void UpdateUnitsArea(String territory)
    {
        MapData mapData = GetMapData();
        String terOwner = GetMapData().GetTData().get(territory).GetOwner();
        UnitCollection units = m_unitPlacements.get(territory);     
        
        List<Component> unitPanels = new ArrayList<Component>();
        
        List<String> playersWhoCanPutUnitsHere;
        if (terOwner == null || mapData.GetWalkthroughData().GetWalkthroughOptions().p_controlLevelOption == 2) //Water or neutral
            playersWhoCanPutUnitsHere = new ArrayList<String>(mapData.GetPlayersData().keySet());
        else
            playersWhoCanPutUnitsHere = Arrays.asList(terOwner);
        for (String player : playersWhoCanPutUnitsHere)
        {
            for (String unitName : GetMapData().GetUnitsData().keySet())
            {
                PSUnitType unitType = new PSUnitType(unitName, player);
                int count = units.GetUnitOfTypeXCount(unitType);
                UnitData unitData = GetMapData().GetUnitsData().get(unitName);

                String unitImageKey = player + "|^|" + unitName;
                if (!m_cachedUnitImages.containsKey(unitImageKey))
                {
                    ImageIcon grabbedImage = new ImageIcon(unitData.GetPlayerSpecificUnitData().get(player).GetImage());
                    m_cachedUnitImages.put(unitImageKey, grabbedImage);
                }
                ImageIcon unitImageIcon = m_cachedUnitImages.get(unitImageKey);

                JPanel unitPanel = new JPanel();
                unitPanel.setName(unitType.toString());
                unitPanel.setLayout(new AbsoluteLayout());
                unitPanel.setSize(48, 80);
                unitPanel.setMinimumSize(new Dimension(48, 80));
                unitPanel.setMaximumSize(new Dimension(48, 80));

                JLabel unitImageLabel = new JLabel(unitImageIcon);
                unitImageLabel.setLocation(0, 0);
                unitImageLabel.setSize(48, 48);
                unitImageLabel.setToolTipText(unitType.toString());
                unitPanel.add(unitImageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 48, 48));

                JSpinner unitCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
                unitCountSpinner.setLocation(0, 48);
                unitCountSpinner.setSize(48, 32);
                unitCountSpinner.addChangeListener(new ChangeListener()
                {
                    @Override
                    public void stateChanged(ChangeEvent evt)
                    {
                        v_revert.setEnabled(true);
                        v_ok.setEnabled(true);
                    }
                });
                unitPanel.add(unitCountSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 48, 48, 32));

                //Now set unit image and count
                unitImageLabel.setIcon(unitImageIcon);
                unitCountSpinner.getModel().setValue(count);

                unitPanels.add(unitPanel);
            }
        }
        
        v_territoryUnitsPanel.removeAll();
        for(Component unitPanel : unitPanels)
            v_territoryUnitsPanel.add(unitPanel);
    }
    
    private UnitCollection CreateUnitCollectionFromUnitsArea()
    {
        UnitCollection result = new UnitCollection();
        
        for(Component unitPanel : v_territoryUnitsPanel.getComponents())
        {
            PSUnitType unitType = PSUnitType.parseFromToString(unitPanel.getName());
            JSpinner spinner = ((JSpinner)((JPanel)unitPanel).getComponent(1));
            try{spinner.commitEdit();}catch(Exception ex){}
            int count = Integer.parseInt(spinner.getValue().toString());
            result.AddUnits(unitType, count);
        }
        
        return result;
    }
    
    private void v_drawingLabeldrawingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseReleased
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseReleased
        if (evt.getButton() == evt.BUTTON1 || (m_lastPoint.x == evt.getXOnScreen() && m_lastPoint.y == evt.getYOnScreen()))
        {
            String clickedTer = GetTerritoryClicked(evt.getPoint(), GetMapData().GetTPolygons());
            if(clickedTer == null)
                return; //For now, make users always have a territory selected
            if(m_currentTer != null)
            {
                UnitCollection newUnits = CreateUnitCollectionFromUnitsArea();
                m_unitPlacements.put(m_currentTer, newUnits);
            }
            m_currentTer = clickedTer;
            UpdateUnitsArea(m_currentTer);
            UpdateTerNameAndUnitsList();
            v_revert.setEnabled(false);
            v_ok.setEnabled(false); //Only have these enabled after user changes some unit count (event is triggered so this undoes incorrect enabling)
            v_drawingLabel.repaint();
        }
        v_drawingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_v_drawingLabeldrawingMouseReleased

    private void UpdateTerNameAndUnitsList()
    {
        v_territoryName.setText("Territory: " + m_currentTer);
        v_territoryUnits.setText("Units: " + m_unitPlacements.get(m_currentTer).toString());
    }
    
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

    private void v_displayUnitsOnscreenStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_v_displayUnitsOnscreenStateChanged
    {//GEN-HEADEREND:event_v_displayUnitsOnscreenStateChanged
        m_displayUnitsOnscreen = v_displayUnitsOnscreen.isSelected();
        v_drawingLabel.repaint();
    }//GEN-LAST:event_v_displayUnitsOnscreenStateChanged

    private void v_revertActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_revertActionPerformed
    {//GEN-HEADEREND:event_v_revertActionPerformed
        UpdateUnitsArea(m_currentTer);
        UpdateTerNameAndUnitsList();
        v_revert.setEnabled(false);
        v_ok.setEnabled(false);
    }//GEN-LAST:event_v_revertActionPerformed

    private void v_okActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_okActionPerformed
    {//GEN-HEADEREND:event_v_okActionPerformed
        UnitCollection newUnits = CreateUnitCollectionFromUnitsArea();
        m_unitPlacements.put(m_currentTer, newUnits);
        UpdateTerNameAndUnitsList();
        v_drawingLabel.repaint();
        v_revert.setEnabled(false);
        v_ok.setEnabled(false);
    }//GEN-LAST:event_v_okActionPerformed

    private void v_clearTerUnitsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_clearTerUnitsActionPerformed
    {//GEN-HEADEREND:event_v_clearTerUnitsActionPerformed
        ClearUnitsAreaUnitCounts();
        
        if (m_currentTer != null)
        {
            v_revert.setEnabled(true);
            v_ok.setEnabled(true);
        }
    }//GEN-LAST:event_v_clearTerUnitsActionPerformed

    private void ClearUnitsAreaUnitCounts()
    {
        for(Component unitPanel : v_territoryUnitsPanel.getComponents())
        {           
            JSpinner unitCountSpinner = (JSpinner)((JPanel)unitPanel).getComponent(1);
            unitCountSpinner.getModel().setValue(0);
        }
    }
    
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

    private void v_territoryUnitsPanelAncestorResized(java.awt.event.HierarchyEvent evt)//GEN-FIRST:event_v_territoryUnitsPanelAncestorResized
    {//GEN-HEADEREND:event_v_territoryUnitsPanelAncestorResized
        v_territoryUnitsPanel.setSize(v_toolbarPanel.getWidth() - 251, v_territoryUnitsPanel.getHeight());
        v_territoryUnitsPanel.validate();
    }//GEN-LAST:event_v_territoryUnitsPanelAncestorResized
    private boolean m_displayUnitsOnscreen = true;
    private int m_ownerColorTransparency = 255;
    private Point LimitedImagePoint(Point p)
    {
        Point r = new Point(p.x + v_scrollPane.getViewport().getWidth() < m_image.getWidth() ? p.x : m_image.getWidth() - 1 - v_scrollPane.getViewport().getWidth(),p.y + v_scrollPane.getViewport().getHeight() < m_image.getHeight() ? p.y : m_image.getHeight() - 1 - v_scrollPane.getViewport().getHeight());
        return new Point(r.x > 0 ? r.x : 0, r.y > 0 ? r.y : 0);
    }

    @Action
    public void ClearScreen()
    {
        if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "All progress will be lost if you clear the unit placements.\r\n\r\nAre you sure you want to restart?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
        {
            if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "Are you really, really sure you want to clear all unit placements on the entire map, losing all step progress?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
            {
                m_unitPlacements.clear();
                ClearUnitsAreaUnitCounts();
                v_drawingLabel.repaint();
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton v_clearScreenButton;
    private javax.swing.JButton v_clearTerUnits;
    private javax.swing.JCheckBox v_displayUnitsOnscreen;
    private javax.swing.JLabel v_drawingLabel;
    private javax.swing.ButtonGroup v_landWaterGroup;
    private javax.swing.JPanel v_leftToolbarArea;
    private javax.swing.JButton v_ok;
    private javax.swing.JComboBox v_ownerColorTransparency;
    private javax.swing.JButton v_revert;
    private javax.swing.JScrollPane v_scrollPane;
    private javax.swing.JPanel v_scrollPanePanel;
    private javax.swing.JLabel v_territoryName;
    private javax.swing.JTextArea v_territoryUnits;
    private javax.swing.JPanel v_territoryUnitsPanel;
    private javax.swing.JScrollPane v_territoryUnitsScrollPane;
    private javax.swing.JPanel v_toolbarPanel;
    // End of variables declaration//GEN-END:variables
}
