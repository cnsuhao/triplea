/*
 * Segment1Step1ContentPanel.java
 *
 * Created on May 14, 2010, 5:34:30 PM
 */

package MainProgram.Walkthrough.UI.StepContentPanels;

import Global.Constants;
import Global.ProgramSettings;
import MainProgram.Map.MapData;
import Threading.BackgroundTaskRunner;
import Utils.Geometry;
import Utils.PolygonFinder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author Stephen
 */
public class Segment1Step4ContentPanel_All extends BaseStepContentPanel {

    private BufferedImage m_image = null;
    private Graphics2D m_graphics = null;
    private TreeMap<String, List<Polygon>> m_polygons = new TreeMap<String, List<Polygon>>();
    /** Creates new form Segment1Step1ContentPanel */
    public Segment1Step4ContentPanel_All()
    {
        initComponents();
        v_drawingLabel.addMouseMotionListener(
                new MouseMotionAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        v_locationLabel.setText("Location: " + e.getX() + ", " + e.getY());
                        ArrayList<String> polygonsCovering = new ArrayList<String>();
                        for (Entry<String, List<Polygon>> entry : m_polygons.entrySet())
                        {
                            for (Polygon cur : entry.getValue())
                            {
                                if (cur.contains(e.getPoint()))
                                {
                                    polygonsCovering.add(entry.getKey());
                                }
                            }
                        }
                        if (polygonsCovering.size() == 1)
                        {
                            v_polygonTerritoryNameLabel.setText("Polygon Territory: " + polygonsCovering.get(0));
                        }
                        else if (polygonsCovering.size() > 1)
                        {
                            StringBuilder builder = new StringBuilder();
                            for (String cur : polygonsCovering)
                            {
                                builder.append(cur).append(", ");
                            }
                            //builder.delete(builder.length() - 2, 2);
                            v_polygonTerritoryNameLabel.setText("Polygon Territories: " + builder.toString().substring(0,builder.length() - 2));
                        }
                        else
                        {
                            v_polygonTerritoryNameLabel.setText("Polygon Territory: none");
                        }
                    }
                });
        v_scrollPanePanel.addMouseMotionListener(
                new MouseMotionAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        v_locationLabel.setText("Location: " + e.getX() + ", " + e.getY());
                        v_polygonTerritoryNameLabel.setText("Polygon Territory: none");
                    }
                });
    }
    @Override
    public boolean WaitForPanelClose()
    {
        return true;
    }
    @Override
    public void InitControls()
    {
        VerifyPolygons(GetMapData().GetTPolygons());
        m_polygons = GetMapData().GetTPolygons();
        UpdateCount();
        SetImage(GetMapData().GetBaseMapImage().getImage());
        ScrollPaneToLastMapPos(v_scrollPane);
    }
    private void VerifyPolygons(TreeMap<String, List<Polygon>> polygons)
    {
        MapData mapData = GetMapData();
        ArrayList<String> notFound = new ArrayList<String>();
        for (String name : polygons.keySet())
        {
            if (!mapData.GetTDefinitions().containsKey(name))
            {
                notFound.add(name);
            }
        }
        if (notFound.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the territory polygons refer to territories that no longer exist. Do you want to remove all of these polygons from the polygon list?", "Remove Invalid Polygons", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : notFound)
                {
                    mapData.GetTPolygons().remove(name);
                }
            }
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        GetMapData().SetTPolygons(m_polygons);
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
        m_graphics.drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this),  this);
        SetImage(bimage);
    }
    private List<Polygon> m_current;
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        v_scrollPane = new javax.swing.JScrollPane();
        v_scrollPanePanel = new javax.swing.JPanel();
        v_drawingLabel = new javax.swing.JLabel()
        {
            public void paint(Graphics graphics)
            {
                Graphics2D g = (Graphics2D)graphics;
                //super.paint(g);
                g.drawImage(m_image, 0,0, this);
                g.setFont(Constants.MapFont_Bold);
                if(m_islandMode)
                g.setColor(Color.red);
                else
                g.setColor(Color.yellow);
                for (List<Polygon> polygons : m_polygons.values())
                {
                    if(m_islandMode)
                    {
                        for(Polygon item : polygons)
                        {
                            g.drawPolygon(item.xpoints, item.ypoints, item.npoints);
                        }
                    }
                    else
                    {
                        for(Polygon item : polygons)
                        {
                            g.fillPolygon(item.xpoints, item.ypoints, item.npoints);
                        }
                    }

                }
                g.setColor(Color.red);
                if(m_current != null)
                {
                    Iterator<Polygon> currentIter = m_current.iterator();
                    while (currentIter.hasNext())
                    {
                        Polygon item = currentIter.next();
                        g.fillPolygon(item.xpoints, item.ypoints, item.npoints);
                    }
                }
                if(!m_islandMode)
                {
                    g.setColor(Color.black);
                    for(List<Polygon> polygons : m_polygons.values())
                    {
                        for(Polygon item : polygons)
                        {
                            g.drawPolygon(item.xpoints, item.ypoints, item.npoints);
                        }
                    }
                }
                g.setColor(Color.black);
                for(String s : GetMapData().GetTDefinitions().keySet())
                {
                    g.drawString(s, GetMapData().GetTDefinitions().get(s).x + 10,GetMapData().GetTDefinitions().get(s).y + 5);
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
        v_autoFillButton = new javax.swing.JButton();
        v_clearScreenButton = new javax.swing.JButton();
        v_islandModeCheckBox = new javax.swing.JCheckBox();
        v_useAdvancedModeCB = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        v_polygonCountLabel = new javax.swing.JLabel();
        v_locationLabel = new javax.swing.JLabel();
        v_polygonTerritoryNameLabel = new javax.swing.JLabel();

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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment1Step4ContentPanel_All.class, this);
        v_autoFillButton.setAction(actionMap.get("AutoFillClicked")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment1Step4ContentPanel_All.class);
        v_autoFillButton.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_autoFillButton.setText(resourceMap.getString("v_autoFillButton.text")); // NOI18N
        v_autoFillButton.setToolTipText(resourceMap.getString("v_autoFillButton.toolTipText")); // NOI18N
        v_autoFillButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_autoFillButton.setName("v_autoFillButton"); // NOI18N
        v_toolbarPanel.add(v_autoFillButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 55, 55));

        v_clearScreenButton.setAction(actionMap.get("ClearScreen")); // NOI18N
        v_clearScreenButton.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_clearScreenButton.setText(resourceMap.getString("v_clearScreenButton.text")); // NOI18N
        v_clearScreenButton.setToolTipText(resourceMap.getString("v_clearScreenButton.toolTipText")); // NOI18N
        v_clearScreenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_clearScreenButton.setName("v_clearScreenButton"); // NOI18N
        v_toolbarPanel.add(v_clearScreenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 3, 55, 55));

        v_islandModeCheckBox.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_islandModeCheckBox.setText(resourceMap.getString("v_islandModeCheckBox.text")); // NOI18N
        v_islandModeCheckBox.setToolTipText(resourceMap.getString("v_islandModeCheckBox.toolTipText")); // NOI18N
        v_islandModeCheckBox.setName("v_islandModeCheckBox"); // NOI18N
        v_islandModeCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                IslandModeCBChanged(evt);
            }
        });
        v_toolbarPanel.add(v_islandModeCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 10, -1, -1));

        v_useAdvancedModeCB.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_useAdvancedModeCB.setSelected(true);
        v_useAdvancedModeCB.setText(resourceMap.getString("v_useAdvancedModeCB.text")); // NOI18N
        v_useAdvancedModeCB.setToolTipText(resourceMap.getString("v_useAdvancedModeCB.toolTipText")); // NOI18N
        v_useAdvancedModeCB.setName("v_useAdvancedModeCB"); // NOI18N
        v_useAdvancedModeCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_useAdvancedModeCBIslandModeCBChanged(evt);
            }
        });
        v_toolbarPanel.add(v_useAdvancedModeCB, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 30, -1, -1));

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
            .addGap(0, 57, Short.MAX_VALUE)
        );

        v_toolbarPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 0, 4, 61));

        v_polygonCountLabel.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_polygonCountLabel.setText(resourceMap.getString("v_polygonCountLabel.text")); // NOI18N
        v_polygonCountLabel.setName("v_polygonCountLabel"); // NOI18N
        v_toolbarPanel.add(v_polygonCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 24, -1, -1));

        v_locationLabel.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_locationLabel.setText(resourceMap.getString("v_locationLabel.text")); // NOI18N
        v_locationLabel.setName("v_locationLabel"); // NOI18N
        v_toolbarPanel.add(v_locationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 7, -1, -1));

        v_polygonTerritoryNameLabel.setFont(resourceMap.getFont("v_polygonCountLabel.font")); // NOI18N
        v_polygonTerritoryNameLabel.setText(resourceMap.getString("v_polygonTerritoryNameLabel.text")); // NOI18N
        v_polygonTerritoryNameLabel.setName("v_polygonTerritoryNameLabel"); // NOI18N
        v_toolbarPanel.add(v_polygonTerritoryNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
            .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(v_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    Boolean m_islandMode = false;
    Boolean m_advancedMode = true;

    private Boolean currentPolygonContainsPoint(Point point)
    {
        for (Polygon p : m_current)
        {
            if (p.contains(point))
            {
                return true;
            }
        }
        return false;
    }
    private String getTName(List<Polygon> p)
    {
        String result = findFirstTName(p);
        result = Geometry.findNotSharedTName(result, p, m_polygons, GetMapData().GetTDefinitions());
        ArrayList<String> territories = new ArrayList<String>(GetMapData().GetTDefinitions().keySet());
        Collections.sort(territories);
        result = (String) JOptionPane.showInputDialog(this, "Assign the polygon to an existing territory:","Assign Polygon To Territory", JOptionPane.QUESTION_MESSAGE,new ImageIcon(),territories.toArray(),result);
        return result;
    }
    private String findFirstTName(List<Polygon> p)
    {
        for (Entry<String, Point> entry : GetMapData().GetTDefinitions().entrySet())
        {
            for (Polygon cur : p)
            {
                if (cur.contains(entry.getValue()))
                {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    private TreeMap<String, List<Polygon>> ClonedPolygonsWithResolvedIslandPolyGrabbersEtc(TreeMap<String, List<Polygon>> polygons)
    {
        TreeMap<String, List<Polygon>> result = new TreeMap<String, List<Polygon>>();
        for (Entry<String, List<Polygon>> entry : polygons.entrySet())
        {
            result.put(Geometry.findNotSharedTName(entry.getKey(), entry.getValue(), polygons, GetMapData().GetTDefinitions()), entry.getValue());
        }
        return result;
    }
    private int GetPolygonsCenterHoldCount(List<Polygon> polygons)
    {
        int count = 0;
        for(Polygon p : polygons)
        {
            for(Point pt : GetMapData().GetTDefinitions().values())
            {
                if(p.contains(pt))
                    count++;
            }
        }
        return count;
    }
    private void UpdateCount()
    {
        v_polygonCountLabel.setText("Polygon Count: " + m_polygons.size());
    }
    private void v_drawingLabeldrawingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseReleased
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseReleased
        if (evt.getButton() == evt.BUTTON1 || (m_lastPoint.x == evt.getXOnScreen() && m_lastPoint.y == evt.getYOnScreen()))
        {
            if (m_current != null && !evt.isControlDown() && currentPolygonContainsPoint(evt.getPoint()))
            {
                String name = getTName(m_current);
                if(name == null)
                {
                    m_current = null;
                    return;
                }
                m_polygons.put(name, m_current);
                UpdateCount();
                m_current = null;
            }
            else if (evt.getButton() != evt.BUTTON1 && v_drawingLabel.getCursor() != Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
            {
                if (evt.isShiftDown())
                {
                    for(Entry<String, List<Polygon>> polyc : m_polygons.entrySet())
                    {
                        for(Polygon poly : polyc.getValue())
                        {
                            if(poly.contains(evt.getPoint()))
                            {
                                if(ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the polygon for territory " + polyc.getKey() + "?", "Remove Polygon", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION)
                                {
                                    return;
                                }
                                m_polygons.remove(polyc.getKey());
                                break;
                            }
                        }
                    }
                }
                else
                {
                    Polygon result = PolygonFinder.FindPolygon(this, m_image, evt.getPoint(), m_advancedMode, true);
                    if (result == null)
                    {
                        return;
                    }
                    if (m_current == null || !evt.isControlDown())
                    {
                        m_current = new ArrayList<Polygon>();
                    }
                    if (m_current.size() == 0 || (!currentPolygonContainsPoint(evt.getPoint()) && evt.isControlDown()))
                    {
                        m_current.add(result);
                    }
                    v_drawingLabel.update(v_drawingLabel.getGraphics());
                }
            }
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

    private void v_useAdvancedModeCBIslandModeCBChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_v_useAdvancedModeCBIslandModeCBChanged
    {//GEN-HEADEREND:event_v_useAdvancedModeCBIslandModeCBChanged
        m_advancedMode = v_useAdvancedModeCB.isSelected();
    }//GEN-LAST:event_v_useAdvancedModeCBIslandModeCBChanged

    private void IslandModeCBChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_IslandModeCBChanged
    {//GEN-HEADEREND:event_IslandModeCBChanged
        m_islandMode = v_islandModeCheckBox.isSelected();
        v_drawingLabel.update(v_drawingLabel.getGraphics());
}//GEN-LAST:event_IslandModeCBChanged
    private Point LimitedImagePoint(Point p)
    {
        Point r = new Point(p.x + v_scrollPane.getViewport().getWidth() < m_image.getWidth() ? p.x : m_image.getWidth() - 1 - v_scrollPane.getViewport().getWidth(),p.y + v_scrollPane.getViewport().getHeight() < m_image.getHeight() ? p.y : m_image.getHeight() - 1 - v_scrollPane.getViewport().getHeight());
        return new Point(r.x > 0 ? r.x : 0, r.y > 0 ? r.y : 0);
    }

    @Action
    public void ClearScreen()
    {
        if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "All progress will be lost if you clear the territory polygons.\r\n\r\nAre you sure you want to restart?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
        {
            m_polygons.clear();
            UpdateCount();
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    public Segment1Step4ContentPanel_All GetThis()
    {
        return this;
    }
    @Action
    public void AutoFillClicked()
    {
        if (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && JOptionPane.showConfirmDialog(m_walkthroughWindow, "All of the territory polygons will be cleared before performing the automatic polygon adding scan.\r\n\r\nAre you sure you want to run the auto-add scan?", "Perform Scan", JOptionPane.YES_NO_CANCEL_OPTION) != 0)
            return;

        m_current = null;
        final ArrayList<String> errors = new ArrayList<String>();
        //Set x to 1 to signal a poly was used more than once
        final Point polyUsedTwiceSignal = new Point(-1, 0);
        
        Runnable runner = new Runnable()
        {
            public void run()
            {
                TreeMap<String, List<Polygon>> polygons = new TreeMap<String, List<Polygon>>();
                for (String name : GetMapData().GetTDefinitions().keySet())
                {
                    if (BackgroundTaskRunner.s_shouldStopBT)
                        return;
                    
                    Point cur = GetMapData().GetTDefinitions().get(name);
                    Polygon result = PolygonFinder.FindPolygonMulticheck(m_walkthroughWindow, m_image, cur, m_advancedMode, true);
                    if (result != null && !result.contains(cur))
                        result = PolygonFinder.FindPolygonMulticheck(m_walkthroughWindow, m_image, cur, false, false);
                    if (result != null && !result.contains(cur))
                        result = null;
                    if (result == null)
                    {
                        errors.add(name);
                        continue;
                    }
                    polygons.put(name, Collections.singletonList(result));
                }
                polygons = ClonedPolygonsWithResolvedIslandPolyGrabbersEtc(polygons);
                
                for (Entry<String, List<Polygon>> entry : polygons.entrySet())
                {
                    for (Entry<String, List<Polygon>> entry2 : polygons.entrySet())
                    {
                        if (BackgroundTaskRunner.s_shouldStopBT)
                                return;
                        if (entry != entry2 && Geometry.GetRectOfPolygons(entry2.getValue()).equals(Geometry.GetRectOfPolygons(entry.getValue())))
                            polyUsedTwiceSignal.x = 1; //Signal that poly was used twice
                    }
                    m_polygons.put(entry.getKey(), entry.getValue());
                }
            }
        };

        //Note: If the task is canceled half-way, the polygons will *not* be there
        BackgroundTaskRunner.runInBackground(this, "Calculating the territory polygons...", runner, true);
        
        UpdateCount();
        v_drawingLabel.update(v_drawingLabel.getGraphics());
        if(errors.size() > 0)
        {
            StringBuilder names = new StringBuilder();//a, b,
            for(String err : errors)
            {
                names.append(err).append(", ");
            }
            names.delete(names.length() - 2, names.length());
            if (errors.size() > 1)
            {
                names.delete(names.toString().lastIndexOf(", "), names.length());
                names.append(", and ").append(errors.get(errors.size() - 1));
            }
            JOptionPane.showMessageDialog(this, "There were errors when performing the automatic polygon finding method.\r\nThe following territories failed to obtain polygons: " + names.toString() + ".\r\n\r\nYou may be able to grab the polygon if you perform the process manually.\r\nAnother option is to smooth out the borders of the territories that failed by returning to the map drawing step. Then you can come back to this step and it will most likely work if smoothed correctly. (Don't leave any line jut-outs into the territory)\r\n\r\nYou can find more information about these and other solutions in the help contents. Whatever you do, make sure that these issues are resolved before continuing to the next step.", "Errors Occurred", JOptionPane.INFORMATION_MESSAGE);
        }
        if(polyUsedTwiceSignal.x == 1)
            JOptionPane.showMessageDialog(this, "It appears that some of the map territories are using identical polygons. This is most likely caused by running the auto-find scan with the \"Max Multi-Search Polygon Cycles\" option set too high in the program settings.\r\n\r\nThese issues should be resolved before continuing to the next step to ensure that the map will run correctly.", "Territories Sharing Polygons", JOptionPane.INFORMATION_MESSAGE);
    }
    @SuppressWarnings("static-access")
    public void AlertKeyPressed(java.awt.event.KeyEvent e)
    {
        if (e.getKeyCode() == e.VK_ESCAPE)
        {
            m_current = null;
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton v_autoFillButton;
    private javax.swing.JButton v_clearScreenButton;
    private javax.swing.JLabel v_drawingLabel;
    private javax.swing.JCheckBox v_islandModeCheckBox;
    private javax.swing.JLabel v_locationLabel;
    private javax.swing.JLabel v_polygonCountLabel;
    private javax.swing.JLabel v_polygonTerritoryNameLabel;
    private javax.swing.JScrollPane v_scrollPane;
    private javax.swing.JPanel v_scrollPanePanel;
    private javax.swing.JPanel v_toolbarPanel;
    private javax.swing.JCheckBox v_useAdvancedModeCB;
    // End of variables declaration//GEN-END:variables

}
