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
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
public class Segment1Step6ContentPanel_All extends BaseStepContentPanel
{
    private BufferedImage m_image = null;
    private Graphics2D m_graphics = null;
    private TreeMap<String, ArrayList<Point>> m_uPlacements = new TreeMap<String, ArrayList<Point>>();
    private String m_currentName = null;
    private List<Polygon> m_current = null;
    private Segment1Step6ContentPanel_All GetThis()
    {
        return this;
    }
    /** Creates new form Segment1Step1ContentPanel */
    public Segment1Step6ContentPanel_All()
    {
        initComponents();
        v_drawingLabel.addMouseMotionListener(
                new MouseMotionAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        String tName = GetTerritoryClicked(e.getPoint(), PolygonFinder.FindPolygon(GetThis(), m_image, e.getPoint(), false, false), GetMapData().GetTPolygons());
                        if(tName != null)
                            v_territoryLabel.setText("Territory: " + tName);
                        else
                            v_territoryLabel.setText("Territory: none");
                        
                        if(tName != null)
                            v_terDrawingPointsCountLabel.setText("Territory Drawing Locations: " + (m_uPlacements.containsKey(tName) ? Integer.toString(m_uPlacements.get(tName).size()) : "0"));
                    }
                });
        v_scrollPanePanel.addMouseMotionListener(
                new MouseMotionAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        v_territoryLabel.setText("Territory: none");
                        v_terDrawingPointsCountLabel.setText("Territory Drawing Locations: 0");
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
        VerifyPlacements(GetMapData().GetTUnitDrawPoints());
        m_uPlacements = GetMapData().GetTUnitDrawPoints();
        UpdateCount();
        SetImage(GetMapData().GetBaseMapImage().getImage());
        ScrollPaneToLastMapPos(v_scrollPane);
        ProgramSettings settings = ProgramSettings.LoadSettings();
        m_previewTool = settings.ToolPreviewWhileMovingMouse;
    }
    boolean m_previewTool = false;
    private void VerifyPlacements(TreeMap<String, ArrayList<Point>> placements)
    {
        MapData mapData = GetMapData();
        ArrayList<String> notFound = new ArrayList<String>();
        for (Entry<String, ArrayList<Point>> entry : placements.entrySet())
        {
            if (!mapData.GetTDefinitions().containsKey(entry.getKey()))
            {
                notFound.add(entry.getKey());
            }
        }
        if (notFound.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the unit drawing locations refer to territories that no longer exist. Do you want to remove them from the unit drawing locations list?", "Remove Invalid Unit Drawing Locations", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : notFound)
                {
                    mapData.GetTUnitDrawPoints().remove(name);
                }
            }
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        GetMapData().SetTUnitDrawPoints(m_uPlacements);
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
                Graphics2D g = (Graphics2D) graphics;
                g.drawImage(m_image, 0, 0, this);
                g.setFont(Constants.MapFont_Bold);

                g.setColor(Color.yellow);
                if(m_current != null)
                {
                    for(Polygon cur : m_current)
                    {
                        g.fillPolygon(cur);
                    }
                }
                g.setColor(Color.black);
                for(List<Polygon> polygons : GetMapData().GetTPolygons().values())
                {
                    for(Polygon item : polygons)
                    {
                        g.drawPolygon(item.xpoints, item.ypoints, item.npoints);
                    }
                }
                g.setColor(Constants.SafeGreenColor);
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (Entry<String, ArrayList<Point>> entry : m_uPlacements.entrySet())
                {
                    String t = entry.getKey();
                    for (Point p : entry.getValue())
                    {
                        g.drawRect(p.x, p.y, (int) (48 * GetMapData().GetUnitScale()), (int) (48 * GetMapData().GetUnitScale()));
                    }
                }
                g.setColor(Color.red);
                if(m_currentName != null && m_uPlacements.containsKey(m_currentName) && m_uPlacements.get(m_currentName).size() > 0)
                {
                    for (Point p : m_uPlacements.get(m_currentName))
                    {
                        g.drawRect(p.x, p.y, (int) (48 * GetMapData().GetUnitScale()), (int) (48 * GetMapData().GetUnitScale()));
                    }
                }
                g.setColor(Color.black);
                for (Point p : GetMapData().GetTDefinitions().values())
                {
                    g.fillOval(p.x - 5, p.y - 5, 10, 10);
                }
                g.setColor(Constants.SafeGreenColor);
                for (Point p : GetMapData().GetTDefinitions().values())
                {
                    g.fillOval(p.x - 3, p.y - 3, 6, 6);
                }
                g.setColor(Color.black);
                for (String s : GetMapData().GetTDefinitions().keySet())
                {
                    g.drawString(s, GetMapData().GetTDefinitions().get(s).x + 10, GetMapData().GetTDefinitions().get(s).y + 5);
                }
                if(m_previewTool)
                {
                    g.setColor(Color.red);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
                    g.drawRect(m_lastImageP.x, m_lastImageP.y, (int) (48 * GetMapData().GetUnitScale()), (int) (48 * GetMapData().GetUnitScale()));
                }
            }
        };
        v_toolbarPanel = new javax.swing.JPanel();
        v_autoFillButton = new javax.swing.JButton();
        v_clearScreenButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        v_territoryLabel = new javax.swing.JLabel();
        v_terDrawingPointsCountLabel = new javax.swing.JLabel();
        v_totalDrawingPointCountLabel = new javax.swing.JLabel();
        v_placementOverlapSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();

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
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                v_drawingLabelMouseMoved(evt);
            }
        });
        v_scrollPanePanel.add(v_drawingLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 400));

        v_scrollPane.setViewportView(v_scrollPanePanel);

        v_toolbarPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        v_toolbarPanel.setName("v_toolbarPanel"); // NOI18N
        v_toolbarPanel.setPreferredSize(new java.awt.Dimension(725, 62));
        v_toolbarPanel.setRequestFocusEnabled(false);
        v_toolbarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment1Step6ContentPanel_All.class, this);
        v_autoFillButton.setAction(actionMap.get("AutoFillClicked")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment1Step6ContentPanel_All.class);
        v_autoFillButton.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_autoFillButton.setText(resourceMap.getString("v_autoFillButton.text")); // NOI18N
        v_autoFillButton.setToolTipText(resourceMap.getString("v_autoFillButton.toolTipText")); // NOI18N
        v_autoFillButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_autoFillButton.setName("v_autoFillButton"); // NOI18N
        v_toolbarPanel.add(v_autoFillButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 55, 55));

        v_clearScreenButton.setAction(actionMap.get("ClearScreen")); // NOI18N
        v_clearScreenButton.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_clearScreenButton.setText(resourceMap.getString("v_clearScreenButton.text")); // NOI18N
        v_clearScreenButton.setToolTipText(resourceMap.getString("v_clearScreenButton.toolTipText")); // NOI18N
        v_clearScreenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_clearScreenButton.setName("v_clearScreenButton"); // NOI18N
        v_toolbarPanel.add(v_clearScreenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 3, 55, 55));

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

        v_territoryLabel.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_territoryLabel.setText(resourceMap.getString("v_territoryLabel.text")); // NOI18N
        v_territoryLabel.setName("v_territoryLabel"); // NOI18N
        v_toolbarPanel.add(v_territoryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 7, -1, -1));

        v_terDrawingPointsCountLabel.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_terDrawingPointsCountLabel.setText(resourceMap.getString("v_terDrawingPointsCountLabel.text")); // NOI18N
        v_terDrawingPointsCountLabel.setName("v_terDrawingPointsCountLabel"); // NOI18N
        v_toolbarPanel.add(v_terDrawingPointsCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 24, -1, -1));

        v_totalDrawingPointCountLabel.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_totalDrawingPointCountLabel.setText(resourceMap.getString("v_totalDrawingPointCountLabel.text")); // NOI18N
        v_totalDrawingPointCountLabel.setName("v_totalDrawingPointCountLabel"); // NOI18N
        v_toolbarPanel.add(v_totalDrawingPointCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, -1));

        v_placementOverlapSpinner.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        v_placementOverlapSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 20, 1));
        v_placementOverlapSpinner.setToolTipText(resourceMap.getString("v_placementOverlapSpinner.toolTipText")); // NOI18N
        v_placementOverlapSpinner.setMinimumSize(new java.awt.Dimension(47, 25));
        v_placementOverlapSpinner.setName("v_placementOverlapSpinner"); // NOI18N
        v_toolbarPanel.add(v_placementOverlapSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 10, 50, 25));

        jLabel1.setFont(resourceMap.getFont("v_clearScreenButton.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        v_toolbarPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 16, -1, -1));

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
    private void UpdateCount()
    {
        int count = 0;
        for(ArrayList<Point> pts : m_uPlacements.values())
        {
            for(Point pt : pts)
            {
                count++;
            }
        }
        v_totalDrawingPointCountLabel.setText("Total Drawing Points: " + count);
    }
    private String GetTerritoryClicked(Point point, Polygon clickPoly, TreeMap<String, List<Polygon>> possibles)
    {
        if (clickPoly != null && clickPoly.contains(point))
        {
            for (Entry<String, List<Polygon>> entry : possibles.entrySet())
            {
                for (Polygon poly : entry.getValue())
                {
                    if (poly.getBounds().equals(clickPoly.getBounds()) && poly.npoints == clickPoly.npoints && poly.contains(point))
                    {
                        return entry.getKey();
                    }
                }
            }
        }
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
                {
                    intersects = true;
                }
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
    private void v_drawingLabeldrawingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabeldrawingMouseReleased
    {//GEN-HEADEREND:event_v_drawingLabeldrawingMouseReleased
        if (evt.getButton() == evt.BUTTON1)
        {
            if (evt.isControlDown())
            {
                Polygon polygon = PolygonFinder.FindPolygon(this, m_image, evt.getPoint(), true, false);
                String tName = GetTerritoryClicked(evt.getPoint(), polygon, GetMapData().GetTPolygons());
                if(tName == null)
                    return;
                m_current = GetMapData().GetTPolygons().get(tName);
                m_currentName = tName;
                m_dlml = evt.getPoint();
                v_drawingLabel.update(v_drawingLabel.getGraphics());
            }
            else if(!evt.isControlDown() && m_current != null)
            {
                ArrayList<Point> oldPoints = m_uPlacements.get(m_currentName);
                if(oldPoints == null)
                    oldPoints = new ArrayList<Point>();
                oldPoints.add(evt.getPoint());
                m_uPlacements.put(m_currentName, oldPoints);
                v_drawingLabel.update(v_drawingLabel.getGraphics());
                UpdateCount();
            }
        }
        else if(v_drawingLabel.getCursor() != Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        {
            Rectangle mouseRect = new Rectangle(evt.getX(), evt.getY(), 1, 1);
            Point lastIntPt = null;
            Entry<String, ArrayList<Point>> lastIntEntry = null;
            for (Entry<String, ArrayList<Point>> entry : m_uPlacements.entrySet())
            {
                String t = entry.getKey();
                for(Point pt : entry.getValue())
                {
                    if(new Rectangle(pt.x,pt.y, GetUnitSpan(), GetUnitSpan()).intersects(mouseRect))
                    {
                        lastIntPt = pt;
                        lastIntEntry = entry;
                    }
                }
            }
            if (lastIntPt != null)
            {
                if (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this unit drawing location?", "Remove Placement Point", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION)
                {
                    return;
                }
                ArrayList<Point> newList = lastIntEntry.getValue();
                newList.remove(lastIntPt);
                lastIntEntry.setValue(newList);
                UpdateCount();
            }
        }
        v_drawingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_v_drawingLabeldrawingMouseReleased
    private int GetUnitSpan()
    {
        return (int) (48 * GetMapData().GetUnitScale()) - Integer.parseInt(v_placementOverlapSpinner.getValue().toString());
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
    Point m_lastImageP = new Point(0,0);
    private void v_drawingLabelMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabelMouseMoved
    {//GEN-HEADEREND:event_v_drawingLabelMouseMoved
        if (m_previewTool)
        {
            m_lastImageP = evt.getPoint();
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }//GEN-LAST:event_v_drawingLabelMouseMoved
    Point m_dlml = null;    private Point LimitedImagePoint(Point p)
    {
        Point r = new Point(p.x + v_scrollPane.getViewport().getWidth() < m_image.getWidth() ? p.x : m_image.getWidth() - 1 - v_scrollPane.getViewport().getWidth(),p.y + v_scrollPane.getViewport().getHeight() < m_image.getHeight() ? p.y : m_image.getHeight() - 1 - v_scrollPane.getViewport().getHeight());
        return new Point(r.x > 0 ? r.x : 0, r.y > 0 ? r.y : 0);
    }

    @Action
    public void ClearScreen()
    {
        if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "All progress will be lost if you clear the unit drawing locations.\r\n\r\nAre you sure you want to restart?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
        {
            m_uPlacements.clear();
            UpdateCount();
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    @Action
    public void AutoFillClicked()
    {
        if (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && JOptionPane.showConfirmDialog(m_walkthroughWindow, "All of the territory drawing locations will be cleared before performing the automatic unit placement adding scan.\r\n\r\nAre you sure you want to run the auto-add scan?", "Perform Scan", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION)
            return;

        m_uPlacements.clear();
        m_current = null;
        m_currentName = null;

        Runnable runner = new Runnable()
        {
            public void run()
            {
                Iterator<String> terrIter = GetMapData().GetTDefinitions().keySet().iterator();

                while (terrIter.hasNext())
                {
                    if(BackgroundTaskRunner.s_shouldStopBT)
                        return;

                    String name = terrIter.next();
                    ArrayList<Point> points;
                    List<Polygon> polygons = GetMapData().GetTPolygons().get(name);
                    List<String> children = Geometry.GetPolygonChildren(polygons, GetMapData().GetTPolygons());
                    if (children.size() > 0) //Water with islands inside it
                    {
                        Set<Polygon> containedPolygons = new HashSet<Polygon>();

                        for (String child : children)
                            containedPolygons.addAll(GetMapData().GetTPolygons().get(child));

                        points = getPlacementsStartingAtTopLeft(polygons, Geometry.GetRectOfPolygons(polygons), GetMapData().GetTDefinitions().get(name), containedPolygons);
                        m_uPlacements.put(name, points);
                    }
                    else
                    {
                        points = getPlacementsStartingAtMiddle(polygons, Geometry.GetRectOfPolygons(polygons), GetMapData().GetTDefinitions().get(name));
                        m_uPlacements.put(name, points);
                    }
                }
            }
        };

        //Note: Even if the task is canceled half-way, the placements that were calculated will still be there (Update this eventually)
        BackgroundTaskRunner.runInBackground(this, "Calculating the unit placement locations...", runner, true);

        v_drawingLabel.update(v_drawingLabel.getGraphics());
        UpdateCount();
    }
    ArrayList<Point> getPlacementsStartingAtMiddle(Collection<Polygon> countryPolygons, Rectangle bounding, Point center)
    {
        ArrayList<Rectangle2D> placementRects = new ArrayList<Rectangle2D>();
        ArrayList<Point> placementPoints = new ArrayList<Point>();

        Rectangle2D place = new Rectangle2D.Double(center.x, center.y, GetUnitSpan(), GetUnitSpan());
        int x = center.x - (GetUnitSpan() / 2);
        int y = center.y - (GetUnitSpan() / 2);
        int step = 1;

        for (int i = 0; i < 2 * Math.max(bounding.width, bounding.height); i++)
        {
            for (int j = 0; j < Math.abs(step); j++)
            {
                if (BackgroundTaskRunner.s_shouldStopBT)
                    return placementPoints;
                if (step > 0)
                    x++;
                else
                    x--;
                isPlacement(countryPolygons, Collections.<Polygon>emptySet(), placementRects, placementPoints, place, x, y);
            }

            for (int j = 0; j < Math.abs(step); j++)
            {
                if (BackgroundTaskRunner.s_shouldStopBT)
                    return placementPoints;
                if (step > 0)
                    y++;
                else
                    y--;
                isPlacement(countryPolygons, Collections.<Polygon>emptySet(), placementRects, placementPoints, place, x, y);
            }

            step = -step;

            if (step > 0)
                step++;
            else
                step--;
        }
        if (placementPoints.isEmpty())
        {
            int defaultx = center.x - (GetUnitSpan() / 2);
            int defaulty = center.y - (GetUnitSpan() / 2);
            placementPoints.add(new Point(defaultx, defaulty));
        }
        return placementPoints;
    }
    ArrayList<Point> getPlacementsStartingAtTopLeft(Collection<Polygon> countryPolygons,  Rectangle bounding, Point center,  Collection<Polygon> containedCountryPolygons)
    {
        ArrayList<Rectangle2D> placementRects = new ArrayList<Rectangle2D>();
        ArrayList<Point> placementPoints = new ArrayList<Point>();

        Rectangle2D place = new Rectangle2D.Double(center.x, center.y, GetUnitSpan(), GetUnitSpan());

        for(int x = bounding.x; x  < bounding.width + bounding.x; x++)
        {
            for (int y = bounding.y; y < bounding.height + bounding.y; y++)
            {
                if (BackgroundTaskRunner.s_shouldStopBT)
                    return placementPoints;
                isPlacement(countryPolygons, containedCountryPolygons, placementRects, placementPoints, place, x, y);
            }
        }
        if(placementPoints.isEmpty())
        {
            int defaultx =center.x - (GetUnitSpan() / 2);
            int defaulty = center.y - (GetUnitSpan() / 2);
            placementPoints.add(new Point(defaultx, defaulty));
        }
        return placementPoints;
    }

    private void isPlacement(Collection<Polygon> countryPolygons, Collection<Polygon> containedCountryPolygons, List<Rectangle2D> placementRects, List<Point> placementPoints, Rectangle2D place, int x, int y)
    {
        place.setFrame(x, y, GetUnitSpan(), GetUnitSpan());
        //One of the conditions: Make sure it is not in or intersects the contained country (island)
        if (containedIn(place, countryPolygons) && !intersectsOneOf(place, placementRects) && (!containedIn(place, containedCountryPolygons) && !intersectsOneOf(place, containedCountryPolygons)))
        {
            placementPoints.add(new Point((int) place.getX(), (int) place.getY()));
            Rectangle2D newRect = new Rectangle2D.Double();
            newRect.setFrame(place);
            placementRects.add(newRect);

        }
    }
    public boolean containedIn(Rectangle2D r, Collection<Polygon> shapes)
    {
        Iterator<Polygon> iter = shapes.iterator();
        while (iter.hasNext())
        {
            Shape item = iter.next();

            if(item.contains(r))
	    {
                return true;
	    }
        }
        return false;
    }
    public boolean intersectsOneOf(Rectangle2D r, Collection<? extends Shape> shapes)
    {
        if (shapes.isEmpty())
        {
            return false;
        }
        Iterator<? extends Shape> iter = shapes.iterator();
        while (iter.hasNext())
        {
            Shape item = iter.next();
            if (item.intersects(r))
            {
                return true;
            }
        }
        return false;
    }
    @SuppressWarnings("static-access")
    public void AlertKeyPressed(java.awt.event.KeyEvent e)
    {
        if (e.getKeyCode() == e.VK_ESCAPE)
        {
            m_current = null;
            m_currentName = null;
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton v_autoFillButton;
    private javax.swing.JButton v_clearScreenButton;
    private javax.swing.JLabel v_drawingLabel;
    private javax.swing.JSpinner v_placementOverlapSpinner;
    private javax.swing.JScrollPane v_scrollPane;
    private javax.swing.JPanel v_scrollPanePanel;
    private javax.swing.JLabel v_terDrawingPointsCountLabel;
    private javax.swing.JLabel v_territoryLabel;
    private javax.swing.JPanel v_toolbarPanel;
    private javax.swing.JLabel v_totalDrawingPointCountLabel;
    // End of variables declaration//GEN-END:variables

}
