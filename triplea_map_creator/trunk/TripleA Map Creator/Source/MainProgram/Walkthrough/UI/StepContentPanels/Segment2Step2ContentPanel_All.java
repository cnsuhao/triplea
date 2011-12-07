/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BaseStepContentPanel.java
 *
 * Created on May 14, 2010, 5:31:50 PM
 */

package MainProgram.Walkthrough.UI.StepContentPanels;

import Global.ProgramSettings;
import MainProgram.Console.ErrorConsole;
import MainProgram.Map.MapData;
import MainProgram.Map.Player.Alliance;
import MainProgram.Map.Player.PlayerData;
import MainProgram.UI.FileOpen;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.BaseSegmentListPanel;
import MainProgram.Walkthrough.UI.WalkthroughWindow;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;

/**
 *
 * @author Stephen
 */
public class Segment2Step2ContentPanel_All extends BaseStepContentPanel {

    /** Creates new form BaseStepContentPanel */
    public Segment2Step2ContentPanel_All()
    {
        initComponents();
        v_playerList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(m_lastSelectedItem > -1)
                {
                    m_players.remove(m_playerBeingEdited);
                    m_players.put(v_playerName.getText(), CreatePlayerFromDataBox());
                    int index = v_playerList.getSelectedIndex();
                    UpdatePlayerList();
                    v_playerList.setSelectedIndex(index);
                }
                UpdatePlayerDataBox();
                m_lastSelectedItem = v_playerList.getSelectedIndex();
            }
        });
    }
    private int m_lastSelectedItem = -1;
    private void UpdatePlayerDataBox()
    {
        if (v_playerList.getSelectedIndex() > -1 && m_players.containsKey(v_playerList.getSelectedValue().toString()))
        {
            SetEnabledPlayerDataBox(true);
            PopulatePlayerDataBox(m_players.get(v_playerList.getSelectedValue().toString()));
        }
        else
        {
            SetEnabledPlayerDataBox(false);
            ClearPlayerDataBox();
        }
    }
    private String m_playerBeingEdited = "";
    private void PopulatePlayerDataBox(PlayerData player)
    {
        m_playerBeingEdited = player.GetName();
        v_playerName.setText(player.GetName());
        v_playerResources.setValue(player.GetResources());
        v_playerColorPreview.setBackground(player.GetColor());
        v_flagIconPreviewPanelLabel.setIcon(new ImageIcon(player.GetFlag()));
        List<Integer> indices = new ArrayList<Integer>();
        int index = 0;
        for (Alliance alliance : m_alliances.values())
        {
            if (player.GetJoinedAlliances().containsValue(alliance))
            {
                indices.add(index);
            }
            index++;
        }
        Object[] iArray = indices.toArray();
        int[] nArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++)
        {
            nArray[i] = Integer.parseInt(iArray[i].toString());
        }
        v_allianceList.setSelectedIndices(nArray);
    }
    private TreeMap<String, PlayerData> m_players = new TreeMap<String, PlayerData>();
    private TreeMap<String, Alliance> m_alliances = new TreeMap<String, Alliance>();
    @Override
    public void SetWalkthroughWindow(WalkthroughWindow window)
    {
        m_walkthroughWindow = window;
    }
    @Override
    public boolean WaitForPanelClose()
    {        
        return true;
    }
    @Override
    public void InitControls()
    {
        m_players = GetMapData().GetPlayersData();
        m_alliances = GetMapData().GetAlliances();

        SetEnabledPlayerDataBox(false);
        ClearPlayerDataBox();

        UpdatePlayerList();
    }
    private void SetEnabledPlayerDataBox(boolean enable)
    {
        v_playerName.setEnabled(enable);
        v_playerResources.setEnabled(enable);
        v_allianceList.setEnabled(enable);
        v_flag_browse.setEnabled(enable);
        v_selectPlayerColorButton.setEnabled(enable);
        v_allianceRemoveButton.setEnabled(enable);
        v_allianceAddButton.setEnabled(enable);
        jLabel5.setEnabled(enable);
        jLabel2.setEnabled(enable);
        jLabel6.setEnabled(enable);
        jLabel4.setEnabled(enable);
        jLabel7.setEnabled(enable);
    }
    private void UpdatePlayerList()
    {
        List<Object> list = new ArrayList<Object>();
        for (PlayerData player : m_players.values())
        {
            list.add(player.GetName());
        }
        v_playerList.setListData(list.toArray());
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        if (m_lastSelectedItem > -1 && m_playerBeingEdited.trim().length() > 0 && m_players.containsKey(m_playerBeingEdited))
        {
            m_players.remove(m_playerBeingEdited);
            m_players.put(v_playerName.getText(), CreatePlayerFromDataBox());
        }
        GetMapData().SetPlayersData(m_players);
        GetMapData().SetAlliances(m_alliances);
    }
    @Override
    public MapData GetMapData()
    {
        return m_walkthroughWindow.GetProject().GetMapData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        v_playerList = new javax.swing.JList();
        v_removeButton = new javax.swing.JButton();
        v_addButton = new javax.swing.JButton();
        v_playerDataGroupBox = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        v_flagIconPreview = new javax.swing.JPanel();
        v_flagIconPreviewPanelLabel = new javax.swing.JLabel();
        v_flag_browse = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        v_playerName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        v_playerColorPreview = new javax.swing.JPanel();
        v_selectPlayerColorButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        v_playerResources = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        v_allianceList = new javax.swing.JList();
        v_allianceAddButton = new javax.swing.JButton();
        v_allianceRemoveButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(723, 352));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step2ContentPanel_All.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        v_playerList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        v_playerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        v_playerList.setName("v_playerList"); // NOI18N
        jScrollPane1.setViewportView(v_playerList);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment2Step2ContentPanel_All.class, this);
        v_removeButton.setAction(actionMap.get("RemovePlayer")); // NOI18N
        v_removeButton.setText(resourceMap.getString("v_removeButton.text")); // NOI18N
        v_removeButton.setName("v_removeButton"); // NOI18N
        v_removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_removeButtonActionPerformed(evt);
            }
        });

        v_addButton.setAction(actionMap.get("NewPlayer")); // NOI18N
        v_addButton.setText(resourceMap.getString("v_addButton.text")); // NOI18N
        v_addButton.setName("v_addButton"); // NOI18N
        v_addButton.setNextFocusableComponent(v_playerName);
        v_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_addButtonActionPerformed(evt);
            }
        });

        v_playerDataGroupBox.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("v_playerDataGroupBox.border.title"))); // NOI18N
        v_playerDataGroupBox.setName("v_playerDataGroupBox"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        v_flagIconPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        v_flagIconPreview.setName("v_flagIconPreview"); // NOI18N
        v_flagIconPreview.setPreferredSize(new java.awt.Dimension(32, 32));
        v_flagIconPreview.setLayout(null);

        v_flagIconPreviewPanelLabel.setText(resourceMap.getString("v_flagIconPreviewPanelLabel.text")); // NOI18N
        v_flagIconPreviewPanelLabel.setName("v_flagIconPreviewPanelLabel"); // NOI18N
        v_flagIconPreview.add(v_flagIconPreviewPanelLabel);
        v_flagIconPreviewPanelLabel.setBounds(1, 1, 32, 32);

        v_flag_browse.setAction(actionMap.get("SelectPlayerFlag")); // NOI18N
        v_flag_browse.setText(resourceMap.getString("v_flag_browse.text")); // NOI18N
        v_flag_browse.setName("v_flag_browse"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        v_playerName.setText(resourceMap.getString("v_playerName.text")); // NOI18N
        v_playerName.setEnabled(false);
        v_playerName.setName("v_playerName"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        v_playerColorPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        v_playerColorPreview.setName("v_playerColorPreview"); // NOI18N
        v_playerColorPreview.setPreferredSize(new java.awt.Dimension(32, 32));

        javax.swing.GroupLayout v_playerColorPreviewLayout = new javax.swing.GroupLayout(v_playerColorPreview);
        v_playerColorPreview.setLayout(v_playerColorPreviewLayout);
        v_playerColorPreviewLayout.setHorizontalGroup(
            v_playerColorPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        v_playerColorPreviewLayout.setVerticalGroup(
            v_playerColorPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        v_selectPlayerColorButton.setAction(actionMap.get("SelectPlayerColor")); // NOI18N
        v_selectPlayerColorButton.setText(resourceMap.getString("v_selectPlayerColorButton.text")); // NOI18N
        v_selectPlayerColorButton.setName("v_selectPlayerColorButton"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        v_playerResources.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        v_playerResources.setEnabled(false);
        v_playerResources.setName("v_playerResources"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        v_allianceList.setEnabled(false);
        v_allianceList.setName("v_allianceList"); // NOI18N
        jScrollPane2.setViewportView(v_allianceList);

        v_allianceAddButton.setAction(actionMap.get("CreateNewAlliance")); // NOI18N
        v_allianceAddButton.setText(resourceMap.getString("v_allianceAddButton.text")); // NOI18N
        v_allianceAddButton.setName("v_allianceAddButton"); // NOI18N
        v_allianceAddButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                v_allianceAddButtonFocusLost(evt);
            }
        });

        v_allianceRemoveButton.setAction(actionMap.get("RemoveAlliance")); // NOI18N
        v_allianceRemoveButton.setText(resourceMap.getString("v_allianceRemoveButton.text")); // NOI18N
        v_allianceRemoveButton.setName("v_allianceRemoveButton"); // NOI18N

        javax.swing.GroupLayout v_playerDataGroupBoxLayout = new javax.swing.GroupLayout(v_playerDataGroupBox);
        v_playerDataGroupBox.setLayout(v_playerDataGroupBoxLayout);
        v_playerDataGroupBoxLayout.setHorizontalGroup(
            v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_playerResources, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addComponent(v_allianceRemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_allianceAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_playerName, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(v_flagIconPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                    .addComponent(v_playerColorPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(v_flag_browse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(v_selectPlayerColorButton))))))
                .addContainerGap())
        );
        v_playerDataGroupBoxLayout.setVerticalGroup(
            v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addComponent(v_playerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(v_flagIconPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(v_flag_browse)))
                        .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(v_playerColorPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(v_selectPlayerColorButton)))
                        .addGap(18, 18, 18))
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel4)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(27, 27, 27)))
                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(v_playerResources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(v_playerDataGroupBoxLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(v_playerDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(v_allianceAddButton)
                    .addComponent(v_allianceRemoveButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(98, 98, 98)
                            .addComponent(jLabel1))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(v_removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(v_addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_playerDataGroupBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(v_removeButton)
                            .addComponent(v_addButton)))
                    .addComponent(v_playerDataGroupBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    private void v_removeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_removeButtonActionPerformed
    {//GEN-HEADEREND:event_v_removeButtonActionPerformed

    }//GEN-LAST:event_v_removeButtonActionPerformed
    private void ClearPlayerDataBox()
    {
        v_playerName.setText("");
        v_playerResources.setValue(0);
        v_playerColorPreview.setBackground(v_flagIconPreview.getBackground());
        v_flagIconPreviewPanelLabel.setIcon(new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)));
        List<Object> list = new ArrayList<Object>();
        for (Alliance alliance : m_alliances.values())
        {
            list.add(alliance.GetName());
        }
        v_allianceList.setListData(list.toArray());
    }
    @Action
    private void v_addButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_addButtonActionPerformed
    {//GEN-HEADEREND:event_v_addButtonActionPerformed

    }//GEN-LAST:event_v_addButtonActionPerformed

    private void v_allianceAddButtonFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_v_allianceAddButtonFocusLost
    {//GEN-HEADEREND:event_v_allianceAddButtonFocusLost
        if(evt.getOppositeComponent() != v_allianceRemoveButton)
            ((BaseSegmentListPanel)m_walkthroughWindow.v_walkthroughStepsPanel.getComponents()[0]).FocusOnFirstButton();
    }//GEN-LAST:event_v_allianceAddButtonFocusLost
    private PlayerData CreatePlayerFromDataBox()
    {
        try{v_playerResources.commitEdit();}catch(Exception ex){}
        TreeMap<String, Alliance> joinedA = new TreeMap<String, Alliance>();
        for (int i = 0; i < v_allianceList.getModel().getSize(); i++)
        {
            if (v_allianceList.isSelectedIndex(i))
                joinedA.put(v_allianceList.getModel().getElementAt(i).toString(), m_alliances.get(v_allianceList.getModel().getElementAt(i).toString()));
        }
        PlayerData player = new PlayerData(v_playerName.getText(), v_playerColorPreview.getBackground(), Integer.parseInt(v_playerResources.getValue().toString()), (BufferedImage)((ImageIcon)v_flagIconPreviewPanelLabel.getIcon()).getImage(), joinedA);
        return player;
    }
    private String RetrieveInputString(String message, Object initialValue)
    {
        String result = JOptionPane.showInputDialog(null, message, initialValue);
        return result;
    }
    private boolean confirm(String question)
    {
        int rVal = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(this), question, "Question", JOptionPane.OK_CANCEL_OPTION);
        return rVal == JOptionPane.OK_OPTION;
    }

    @Action
    public void SelectPlayerColor()
    {
        if (!m_players.containsKey(v_playerList.getSelectedValue().toString()))
        {
            return;
        }

        PlayerData player = m_players.get(v_playerList.getSelectedValue().toString());
        Color color = JColorChooser.showDialog(this, "Please select the territory ownership color for player '" + player.GetName() + "'", player.GetColor());
        if (color == null)
        {
            return;
        }
        v_playerColorPreview.setBackground(color);
    }

    @Action
    public void SelectPlayerFlag()
    {
        try
        {
            if (!m_players.containsKey(v_playerList.getSelectedValue().toString()))
            {
                return;
            }
            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
            ArrayList<String> extensions = new ArrayList<String>();
            extensions.add(".png");
            extensions.add(".bmp");
            extensions.add(".jpg");
            extensions.add(".jpeg");
            extensions.add(".gif");
            filters.add(FileOpen.CreateFilter("Supported Image Files", extensions));
            FileOpen open = new FileOpen(this, "Please select the flag image for the player '" + v_playerList.getSelectedValue().toString() + "'", filters);
            if(open.getFile() == null || !open.getFile().exists())
                return;
            BufferedImage image = ImageIO.read(open.getFile());
            
            //The code below crops the image to 32 by 32 pixels in size.
            BufferedImage bimage = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
            Graphics m_graphics = bimage.createGraphics();
            m_graphics.drawImage(image, 0, 0, 32, 32, Color.WHITE, this);

            v_flagIconPreviewPanelLabel.setIcon(new ImageIcon(image));
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
    }

    @Action
    public void CreateNewAlliance()
    {
        String name = RetrieveInputString("Please enter the name of the new alliance:", "Axis");
        if(name == null)
            return;
        Alliance alliance = new Alliance(name);
        if(m_alliances.containsKey(name) && !confirm("Another alliance exists with the same name.\r\n\r\nDo you want to overwrite it?"))
            return;
        m_alliances.put(name, alliance);
        UpdateAllianceList();
    }
    private void UpdateAllianceList()
    {
        List<Object> list = new ArrayList<Object>();
        for (Alliance alliance : m_alliances.values())
        {
            list.add(alliance.GetName());
        }
        v_allianceList.setListData(list.toArray());
    }

    @Action
    public void RemoveAlliance()
    {
        if(!m_alliances.containsKey(v_allianceList.getSelectedValue().toString()) || (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && !confirm("Are you sure you want to delete this alliance?")))
            return;
        m_alliances.remove(v_allianceList.getSelectedValue().toString());
        UpdateAllianceList();
    }

    @Action
    public void RemovePlayer()
    {
        if (!m_players.containsKey(v_playerList.getSelectedValue().toString()) || (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && !confirm("Are you sure you want to delete this player?")))
            return;
        m_players.remove(v_playerList.getSelectedValue().toString());
        m_lastSelectedItem = -1;
        UpdatePlayerList();
        ClearPlayerDataBox();
    }

    @Action
    public void NewPlayer()
    {
        String name = RetrieveInputString("Please enter the name of the new player:", "Americans");
        if (name == null)
            return;
        PlayerData player = new PlayerData(name, Color.WHITE, 0, new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB), new TreeMap<String, Alliance>());
        if(m_players.containsKey(name) && !confirm("Another player exists with the same name.\r\n\r\nDo you want to overwrite it?"))
            return;
        m_players.put(name, player);
        UpdatePlayerList();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton v_addButton;
    private javax.swing.JButton v_allianceAddButton;
    private javax.swing.JList v_allianceList;
    private javax.swing.JButton v_allianceRemoveButton;
    private javax.swing.JPanel v_flagIconPreview;
    private javax.swing.JLabel v_flagIconPreviewPanelLabel;
    private javax.swing.JButton v_flag_browse;
    private javax.swing.JPanel v_playerColorPreview;
    private javax.swing.JPanel v_playerDataGroupBox;
    private javax.swing.JList v_playerList;
    private javax.swing.JTextField v_playerName;
    private javax.swing.JSpinner v_playerResources;
    private javax.swing.JButton v_removeButton;
    private javax.swing.JButton v_selectPlayerColorButton;
    // End of variables declaration//GEN-END:variables

}
