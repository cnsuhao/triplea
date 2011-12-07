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
import Utils.Images;
import Utils.PolygonFinder;
import Utils.Strings;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author Stephen
 */
public class Segment1Step5ContentPanel_All extends BaseStepContentPanel
{
    private BufferedImage m_image = null;
    private Graphics2D m_graphics = null;
    private TreeMap<String, TreeSet<String>> m_connections = new TreeMap<String, TreeSet<String>>();
    private TreeMap<String, List<Polygon>> GetPolygons()
    {
        return GetMapData().GetTPolygons();
    }
    private Segment1Step5ContentPanel_All GetThis()
    {
        return this;
    }
    /** Creates new form Segment1Step1ContentPanel */
    public Segment1Step5ContentPanel_All()
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

                        BufferedImage hitPointTest = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
                        Dimension offset = new Dimension(-(e.getX() - 5), -(e.getY() - 5));
                        Graphics2D g = hitPointTest.createGraphics();
                        g.setColor(Constants.SafeGreenColor);
                        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                        for (Entry<String, TreeSet<String>> entry : m_connections.entrySet())
                        {
                            String t1 = entry.getKey();
                            for (String t2 : entry.getValue())
                            {
                                Point p1 = GetMapData().GetTDefinitions().get(t1);
                                Point p2 = GetMapData().GetTDefinitions().get(t2);

                                g.drawLine(p1.x + offset.width, p1.y + offset.height, p2.x + offset.width, p2.y + offset.height);
                                if (hitPointTest.getRGB(e.getX() + offset.width, e.getY() + offset.height) == Constants.SafeGreenColor.getRGB())
                                {
                                    v_connectionTsLabel.setText("Connection: " + t1 + " to " + t2);
                                    return;
                                }
                            }
                        }
                        v_connectionTsLabel.setText("Connection: none to none");
                    }
                });
        v_scrollPanePanel.addMouseMotionListener(
                new MouseMotionAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        v_territoryLabel.setText("Territory: none");
                        v_connectionTsLabel.setText("Connection: none to none");
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
        VerifyConnections(GetMapData().GetTConnections());
        m_connections = GetMapData().GetTConnections();
        UpdateCount();
        SetImage(GetMapData().GetBaseMapImage().getImage());
        ScrollPaneToLastMapPos(v_scrollPane);
        ProgramSettings settings = ProgramSettings.LoadSettings();
        m_previewTool = settings.ToolPreviewWhileMovingMouse;
    }
    boolean m_previewTool = false;
    private void VerifyConnections(TreeMap<String, TreeSet<String>> connections)
    {
        MapData mapData = GetMapData();
        ArrayList<String> notFound = new ArrayList<String>();
        for (Entry<String, TreeSet<String>> entry : connections.entrySet())
        {
            if (!mapData.GetTDefinitions().containsKey(entry.getKey()))
            {
                notFound.add(entry.getKey());
            }
            for(String t : entry.getValue())
            {
                if (!mapData.GetTDefinitions().containsKey(t))
                {
                    notFound.add(entry.getKey());
                }
            }
        }
        if (notFound.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the territory connections refer to territories that no longer exist. Do you want to remove all of these connections from the connection list?", "Remove Invalid Connections", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : notFound)
                {
                    mapData.GetTConnections().remove(name);
                }
            }
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        GetMapData().SetTConnections(m_connections);
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

        v_scrollPane = new javax.swing.JScrollPane();
        v_scrollPanePanel = new javax.swing.JPanel();
        v_drawingLabel = new javax.swing.JLabel()
        {
            @Override
            public void paint(Graphics graphics)
            {
                Graphics2D g = (Graphics2D)graphics;
                g.drawImage(m_image, 0, 0, this);
                g.setFont(Constants.MapFont_Bold);

                g.setColor(Constants.SafeGreenColor);
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for(Entry<String, TreeSet<String>> entry : m_connections.entrySet())
                {
                    String t1 = entry.getKey();
                    for(String t2 : entry.getValue())
                    {
                        Point p1 = GetMapData().GetTDefinitions().get(t1);
                        Point p2 = GetMapData().GetTDefinitions().get(t2);

                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
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
                g.setColor(Color.black);
                for(String s : GetMapData().GetTDefinitions().keySet())
                {
                    g.drawString(s, GetMapData().GetTDefinitions().get(s).x + 10,GetMapData().GetTDefinitions().get(s).y + 5);
                }
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
                if(m_previewTool && m_t1 != null && GetMapData().GetTDefinitions().containsKey(m_t1))
                {
                    Point p = GetMapData().GetTDefinitions().get(m_t1);
                    g.setColor(Constants.SafeGreenColor);
                    g.drawLine(p.x, p.y, m_dlml.x, m_dlml.y);
                    g.fillOval(m_dlml.x - 5, m_dlml.y - 5, 10, 10);
                }
            }
        };
        v_toolbarPanel = new javax.swing.JPanel();
        v_autoFillButton = new javax.swing.JButton();
        v_clearScreenButton = new javax.swing.JButton();
        v_haveAutoFindOnlyAddCB = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        v_territoryLabel = new javax.swing.JLabel();
        v_connectionCountLabel = new javax.swing.JLabel();
        v_connectionTsLabel = new javax.swing.JLabel();
        v_lineWidthSpinner = new javax.swing.JSpinner();
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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment1Step5ContentPanel_All.class, this);
        v_autoFillButton.setAction(actionMap.get("AutoFillClicked")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment1Step5ContentPanel_All.class);
        v_autoFillButton.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_autoFillButton.setText(resourceMap.getString("v_autoFillButton.text")); // NOI18N
        v_autoFillButton.setToolTipText(resourceMap.getString("v_autoFillButton.toolTipText")); // NOI18N
        v_autoFillButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_autoFillButton.setName("v_autoFillButton"); // NOI18N
        v_toolbarPanel.add(v_autoFillButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 55, 55));

        v_clearScreenButton.setAction(actionMap.get("ClearScreen")); // NOI18N
        v_clearScreenButton.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_clearScreenButton.setText(resourceMap.getString("v_clearScreenButton.text")); // NOI18N
        v_clearScreenButton.setToolTipText(resourceMap.getString("v_clearScreenButton.toolTipText")); // NOI18N
        v_clearScreenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        v_clearScreenButton.setName("v_clearScreenButton"); // NOI18N
        v_toolbarPanel.add(v_clearScreenButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 3, 55, 55));

        v_haveAutoFindOnlyAddCB.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_haveAutoFindOnlyAddCB.setText(resourceMap.getString("v_haveAutoFindOnlyAddCB.text")); // NOI18N
        v_haveAutoFindOnlyAddCB.setToolTipText(resourceMap.getString("v_haveAutoFindOnlyAddCB.toolTipText")); // NOI18N
        v_haveAutoFindOnlyAddCB.setName("v_haveAutoFindOnlyAddCB"); // NOI18N
        v_haveAutoFindOnlyAddCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                HaveAutoFillOnlyAddClicked(evt);
            }
        });
        v_toolbarPanel.add(v_haveAutoFindOnlyAddCB, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 6, -1, -1));

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

        v_territoryLabel.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_territoryLabel.setText(resourceMap.getString("v_territoryLabel.text")); // NOI18N
        v_territoryLabel.setName("v_territoryLabel"); // NOI18N
        v_toolbarPanel.add(v_territoryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 7, -1, -1));

        v_connectionCountLabel.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_connectionCountLabel.setText(resourceMap.getString("v_connectionCountLabel.text")); // NOI18N
        v_connectionCountLabel.setName("v_connectionCountLabel"); // NOI18N
        v_toolbarPanel.add(v_connectionCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 24, -1, -1));

        v_connectionTsLabel.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_connectionTsLabel.setText(resourceMap.getString("v_connectionTsLabel.text")); // NOI18N
        v_connectionTsLabel.setName("v_connectionTsLabel"); // NOI18N
        v_toolbarPanel.add(v_connectionTsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, -1));

        v_lineWidthSpinner.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        v_lineWidthSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 700, 1));
        v_lineWidthSpinner.setToolTipText(resourceMap.getString("v_lineWidthSpinner.toolTipText")); // NOI18N
        v_lineWidthSpinner.setMinimumSize(new java.awt.Dimension(47, 25));
        v_lineWidthSpinner.setName("v_lineWidthSpinner"); // NOI18N
        v_toolbarPanel.add(v_lineWidthSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 28, 60, 25));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        v_toolbarPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 35, -1, -1));

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
    Boolean m_haveAutoFindOnlyAdd = false;
    private void UpdateCount()
    {
        int count = 0;
        for(TreeSet<String> to : m_connections.values())
        {
            for(String name : to)
            {
                count++;
            }
        }
        v_connectionCountLabel.setText("Connection Count: " + count);
    }
    private String m_t1 = null;
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
            if (m_t1 == null)
            {
                Polygon polygon = PolygonFinder.FindPolygon(this, m_image, evt.getPoint(), true, false);
                String tName = GetTerritoryClicked(evt.getPoint(), polygon, GetMapData().GetTPolygons());
                if(tName == null)
                    return;
                m_t1 = tName;
                m_dlml = evt.getPoint();
            }
            else
            {
                Polygon polygon = PolygonFinder.FindPolygon(this, m_image, evt.getPoint(), true, false);
                String tName = GetTerritoryClicked(evt.getPoint(), polygon, GetMapData().GetTPolygons());
                if (tName == null)
                {
                    return;
                }
                String key = Strings.GetFirstInAlpha(m_t1, tName);
                TreeSet<String> to = m_connections.get(key);
                m_connections.put(key, Strings.Combine(to, Strings.GetLastInAlpha(m_t1, tName)));
                UpdateCount();
                m_t1 = null;
                v_drawingLabel.update(v_drawingLabel.getGraphics());
            }
        }
        else if(v_drawingLabel.getCursor() != Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        {
            BufferedImage hitPointTest = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
            Dimension offset = new Dimension(-(evt.getX() - 5), -(evt.getY() - 5));
            Graphics2D g = hitPointTest.createGraphics();
            g.setColor(Constants.SafeGreenColor);
            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (Entry<String, TreeSet<String>> entry : m_connections.entrySet())
            {
                String t1 = entry.getKey();
                for (String t2 : entry.getValue())
                {
                    Point p1 = GetMapData().GetTDefinitions().get(t1);
                    Point p2 = GetMapData().GetTDefinitions().get(t2);

                    g.drawLine(p1.x + offset.width, p1.y + offset.height, p2.x + offset.width, p2.y + offset.height);
                    if (hitPointTest.getRGB(evt.getX() + offset.width, evt.getY() + offset.height) == Constants.SafeGreenColor.getRGB())
                    {
                        if (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this connection?", "Remove Connection", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION)
                        {
                            return;
                        }
                        m_connections.get(t1).remove(t2);
                        v_drawingLabel.update(v_drawingLabel.getGraphics());
                        UpdateCount();
                        return;
                    }
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

    private void HaveAutoFillOnlyAddClicked(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_HaveAutoFillOnlyAddClicked
    {//GEN-HEADEREND:event_HaveAutoFillOnlyAddClicked
        m_haveAutoFindOnlyAdd = v_haveAutoFindOnlyAddCB.isSelected();
    }//GEN-LAST:event_HaveAutoFillOnlyAddClicked
    Point m_dlml = null;
    private void v_drawingLabelMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_v_drawingLabelMouseMoved
    {//GEN-HEADEREND:event_v_drawingLabelMouseMoved
        if(m_t1 != null && GetMapData().GetTDefinitions().containsKey(m_t1))
        {
            m_dlml = evt.getPoint();
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }//GEN-LAST:event_v_drawingLabelMouseMoved
    private Point LimitedImagePoint(Point p)
    {
        Point r = new Point(p.x + v_scrollPane.getViewport().getWidth() < m_image.getWidth() ? p.x : m_image.getWidth() - 1 - v_scrollPane.getViewport().getWidth(),p.y + v_scrollPane.getViewport().getHeight() < m_image.getHeight() ? p.y : m_image.getHeight() - 1 - v_scrollPane.getViewport().getHeight());
        return new Point(r.x > 0 ? r.x : 0, r.y > 0 ? r.y : 0);
    }

    @Action
    public void ClearScreen()
    {
        if (JOptionPane.showConfirmDialog(m_walkthroughWindow, "All progress will be lost if you clear the territory connections.\r\n\r\nAre you sure you want to restart?", "Restart", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
        {
            m_connections.clear();
            UpdateCount();
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    @Action
    public void AutoFillClicked()
    {
        if (!m_haveAutoFindOnlyAdd)
        {
            if (!ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing || JOptionPane.showConfirmDialog(m_walkthroughWindow, "All of the territory connections will be cleared before performing the automatic polygon adding scan.\r\n\r\nAre you sure you want to run the auto-add scan?", "Perform Scan", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION)
                m_connections.clear();
            else
                return;
        }
        
        final int lineWidth = Integer.parseInt(v_lineWidthSpinner.getValue().toString());

        Runnable runner = new Runnable()
        {
            public void run()
            {
                for (Entry<String, List<Polygon>> entry : GetMapData().GetTPolygons().entrySet())
                {
                    for (Entry<String, List<Polygon>> entry2 : GetMapData().GetTPolygons().entrySet())
                    {
                        if (entry == entry2)
                            continue;
                        for (Polygon p : entry.getValue())
                        {
                            if (BackgroundTaskRunner.s_shouldStopBT)
                                return;
                            
                            Rectangle p1ir = Geometry.InflatedRect(p.getBounds(), lineWidth);
                            BufferedImage p1b = new BufferedImage(p1ir.width, p1ir.height, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D p1g = p1b.createGraphics();
                            FillAndDrawPolygonWideBorder(p1ir.getLocation(), p, p1g, lineWidth);
                            for (Polygon p2 : entry2.getValue())
                            {
                                Rectangle p2ir = Geometry.InflatedRect(p2.getBounds(), lineWidth);
                                if (!p1ir.intersects(p2ir))
                                    continue;
                                BufferedImage p2b = new BufferedImage(p2ir.width, p2ir.height, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D p2g = p2b.createGraphics();
                                FillAndDrawPolygonWideBorder(p2ir.getLocation(), p2, p2g, lineWidth);
                                if (Images.CheckForColoredIntPixels(p1ir.getLocation(), p1b, p2ir.getLocation(), p2b, Color.black))
                                    m_connections.put(Strings.GetFirstInAlpha(entry.getKey(), entry2.getKey()), Strings.Combine(m_connections.get(Strings.GetFirstInAlpha(entry.getKey(), entry2.getKey())), Strings.GetLastInAlpha(entry.getKey(), entry2.getKey())));
                            }
                        }
                    }
                }
            }
        };

        //Note: Even if the task is canceled half-way, the connects that were calculated will still be there (Update this eventually)
        BackgroundTaskRunner.runInBackground(this, "Calculating the territory connections...", runner, true);

        v_drawingLabel.update(v_drawingLabel.getGraphics());
        UpdateCount();
    }

    private void FillAndDrawPolygonWideBorder(Point pt, Polygon p, Graphics2D graphics, int lineWidth)
    {
        graphics.setColor(Color.black);
        graphics.setStroke(new BasicStroke(lineWidth));
        graphics.translate(-pt.x, -pt.y);
        graphics.drawPolyline(p.xpoints, p.ypoints, p.npoints);
        graphics.fillPolygon(p);
    }
    @SuppressWarnings("static-access")
    public void AlertKeyPressed(java.awt.event.KeyEvent e)
    {
        if (e.getKeyCode() == e.VK_ESCAPE)
        {
            m_t1 = null;
            v_drawingLabel.update(v_drawingLabel.getGraphics());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton v_autoFillButton;
    private javax.swing.JButton v_clearScreenButton;
    private javax.swing.JLabel v_connectionCountLabel;
    private javax.swing.JLabel v_connectionTsLabel;
    private javax.swing.JLabel v_drawingLabel;
    private javax.swing.JCheckBox v_haveAutoFindOnlyAddCB;
    private javax.swing.JSpinner v_lineWidthSpinner;
    private javax.swing.JScrollPane v_scrollPane;
    private javax.swing.JPanel v_scrollPanePanel;
    private javax.swing.JLabel v_territoryLabel;
    private javax.swing.JPanel v_toolbarPanel;
    // End of variables declaration//GEN-END:variables

}
