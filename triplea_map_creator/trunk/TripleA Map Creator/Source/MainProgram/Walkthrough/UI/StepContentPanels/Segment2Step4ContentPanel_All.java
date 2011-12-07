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
import MainProgram.Map.Player.PlayerData;
import MainProgram.Map.Unit.PlayerSpecificUnitData;
import MainProgram.Map.Unit.UnitData;
import MainProgram.Map.Unit.UnitProperty;
import MainProgram.UI.FileOpen;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.BaseSegmentListPanel;
import MainProgram.Walkthrough.UI.WalkthroughWindow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;
import org.jdesktop.application.Action;

/**
 *
 * @author Stephen
 */
public class Segment2Step4ContentPanel_All extends BaseStepContentPanel {

    /** Creates new form BaseStepContentPanel */
    public Segment2Step4ContentPanel_All()
    {
        initComponents();
        v_unitList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(m_lastSelectedItem > -1)
                {
                    m_units.remove(m_unitBeingEdited);
                    m_units.put(v_unitName.getText(), CreateUnitFromDataBox());
                    int index = v_unitList.getSelectedIndex();
                    UpdateUnitList();
                    v_unitList.setSelectedIndex(index);
                }
                UpdateUnitDataBox();
                UpdateUnitPropertyBox();
                m_lastSelectedItem = v_unitList.getSelectedIndex();
            }
        });
        v_transportCapacity.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                v_transportCapacityValue.setEnabled(v_transportCapacity.isSelected());
            }
        });
        v_transportCost.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                v_transportCostValue.setEnabled(v_transportCost.isSelected());
            }
        });
        v_carrierCapacity.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                v_carrierCapacityValue.setEnabled(v_carrierCapacity.isSelected());
            }
        });
        v_carrierCost.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                v_carrierCostValue.setEnabled(v_carrierCost.isSelected());
            }
        });
    }
    private int m_lastSelectedItem = -1;
    TreeMap<String, PlayerSpecificUnitData> m_playerSpecificUnitDataToSaveForUnit = new TreeMap<String, PlayerSpecificUnitData>();
    private void UpdateUnitDataBox()
    {
        if (v_unitList.getSelectedIndex() > -1 && m_units.containsKey(v_unitList.getSelectedValue().toString()))
        {
            SetEnabledUnitDataBox(true);
            PopulateUnitDataBox(m_units.get(v_unitList.getSelectedValue().toString()));
        }
        else
        {
            SetEnabledUnitDataBox(false);
            ClearUnitDataBox();
        }
    }
    private void UpdateUnitPropertyBox()
    {
        if (v_unitList.getSelectedIndex() > -1 && m_units.containsKey(v_unitList.getSelectedValue().toString()))
        {
            SetEnabledUnitPropertyBox(true);
            PopulateUnitPropertyBox(m_units.get(v_unitList.getSelectedValue().toString()).GetProperties());
        }
        else
        {
            SetEnabledUnitPropertyBox(false);
            ClearUnitPropertyBox();
        }
    }
    private String m_unitBeingEdited = "";
    private String m_playerWhosUnitDataIsBeingEdited = null;
    private void PopulateUnitDataBox(final UnitData unit)
    {
        m_unitBeingEdited = unit.GetName();
        v_unitName.setText(unit.GetName());
        List<String> players = new ArrayList<String>();
        for(PlayerData player : GetMapData().GetPlayersData().values())
        {
            players.add(player.GetName());
        }

        if(m_playerWhosUnitDataIsBeingEdited == null)
            m_playerWhosUnitDataIsBeingEdited = ((PlayerData)GetMapData().GetPlayersData().values().toArray()[0]).GetName();

        m_playerSpecificUnitDataToSaveForUnit = unit.GetPlayerSpecificUnitData();

        for(String playerName : players)
        {
            if(!m_playerSpecificUnitDataToSaveForUnit.containsKey(playerName))
                m_playerSpecificUnitDataToSaveForUnit.put(playerName, new PlayerSpecificUnitData(playerName, true, new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB), 0));
        }

        PopulatePlayerSpecificUnitDataBoxForPlayer(m_playerWhosUnitDataIsBeingEdited);

        v_playerSpecificUnitDataPlayerCB.setModel(new DefaultComboBoxModel(players.toArray()));
        v_playerSpecificUnitDataPlayerCB.setSelectedItem(m_playerWhosUnitDataIsBeingEdited);
        if(v_playerSpecificUnitDataPlayerCB.getItemListeners().length > 0)
            v_playerSpecificUnitDataPlayerCB.removeItemListener(v_playerSpecificUnitDataPlayerCB.getItemListeners()[0]);
        v_playerSpecificUnitDataPlayerCB.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                SavePlayerSpecificUnitDataFromBoxToVariable();
                m_playerWhosUnitDataIsBeingEdited = e.getItem().toString();
                PopulatePlayerSpecificUnitDataBoxForPlayer(e.getItem().toString());
            }
        });
    }
    private void PopulatePlayerSpecificUnitDataBoxForPlayer(String player)
    {
        if(!m_playerSpecificUnitDataToSaveForUnit.containsKey(player))
            m_playerSpecificUnitDataToSaveForUnit.put(player, new PlayerSpecificUnitData(player, true, new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB), 0));

        PlayerSpecificUnitData pSUnitData = m_playerSpecificUnitDataToSaveForUnit.get(player);
        v_purchasable.setSelected(pSUnitData.GetPurchasable());
        v_unitBuyCost.setValue(pSUnitData.GetCost());
        v_unitImagePreviewPanelLabel.setIcon(new ImageIcon(pSUnitData.GetImage()));
    }
    private void PopulateUnitPropertyBox(TreeMap<String, UnitProperty> props)
    {
        if(props.size() == 0)
            props = CreatePropertiesFromPropertyBox();

        v_attackValue.setValue(props.get("attack").GetValue());
        v_defenseValue.setValue(props.get("defense").GetValue());
        v_movementValue.setValue(props.get("movement").GetValue());
        v_transportCapacityValue.setValue(props.get("transportCapacity").GetValue());
        v_transportCostValue.setValue(props.get("transportCost").GetValue());
        v_carrierCapacityValue.setValue(props.get("carrierCapacity").GetValue());
        v_carrierCostValue.setValue(props.get("carrierCost").GetValue());

        v_transportCapacity.setSelected((Integer)props.get("transportCapacity").GetValue() != 0);
        v_transportCost.setSelected((Integer)props.get("transportCost").GetValue() != 0);
        v_carrierCapacity.setSelected((Integer)props.get("carrierCapacity").GetValue() != 0);
        v_carrierCost.setSelected((Integer)props.get("carrierCost").GetValue() != 0);

        //Enable value spinners for selected properties.
        v_transportCapacityValue.setEnabled(v_transportCapacity.isSelected());
        v_transportCostValue.setEnabled(v_transportCost.isSelected());
        v_carrierCapacityValue.setEnabled(v_carrierCapacity.isSelected());
        v_carrierCostValue.setEnabled(v_carrierCost.isSelected());
        //Enable value spinners for selected properties.

        v_isAir.setSelected((Boolean)props.get("isAir").GetValue());
        v_isSea.setSelected((Boolean)props.get("isSea").GetValue());

        v_isInfantry.setSelected((Boolean)props.get("isInfantry").GetValue());
        v_artillery.setSelected((Boolean)props.get("artillery").GetValue());
        v_isFactory.setSelected((Boolean)props.get("isFactory").GetValue());
        v_isAA.setSelected((Boolean)props.get("isAA").GetValue());
        v_isDestroyer.setSelected((Boolean)props.get("isDestroyer").GetValue());
        v_isSub.setSelected((Boolean)props.get("isSub").GetValue());

        v_canBlitz.setSelected((Boolean)props.get("canBlitz").GetValue());
        v_isTwoHit.setSelected((Boolean)props.get("isTwoHit").GetValue());
        v_isMechanized.setSelected((Boolean)props.get("isMechanized").GetValue());
        v_artillerySupportable.setSelected((Boolean)props.get("artillerySupportable").GetValue());
        v_isParatroop.setSelected((Boolean)props.get("isParatroop").GetValue());
        v_isStrategicBomber.setSelected((Boolean)props.get("isStrategicBomber").GetValue());
        v_canBombard.setSelected((Boolean)props.get("canBombard").GetValue());
    }
    private TreeMap<String, UnitData> m_units = new TreeMap<String, UnitData>();
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
        VerifyUnitsData(GetMapData().GetUnitsData());
        m_units = GetMapData().GetUnitsData();

        SetEnabledUnitDataBox(false);
        ClearUnitDataBox();

        UpdateUnitList();
    }
    private void VerifyUnitsData(TreeMap<String, UnitData> units)
    {
        MapData mapData = GetMapData();
        ArrayList<String> notFound = new ArrayList<String>();
        for (UnitData unit : units.values())
        {
            for (String playerName : unit.GetPlayerSpecificUnitData().keySet())
            {
                if (!mapData.GetPlayersData().containsKey(playerName))
                {
                    notFound.add(playerName);
                }
            }
            break;
        }
        if (notFound.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the player-specific unit data packages refer to players that no longer exist. Do you want to remove all the unit data packages that reference missing players?", "Remove Invalid Unit Data Packages", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (UnitData unit : units.values())
                {
                    for (String name : notFound)
                    {
                        unit.GetPlayerSpecificUnitData().remove(name);
                    }
                }
            }
        }
    }
    private void SetEnabledUnitDataBox(boolean enable)
    {
        v_unitName.setEnabled(enable);
        v_purchasable.setEnabled(enable);
        v_unitBuyCost.setEnabled(enable);
        v_unitImage_browse.setEnabled(enable);
        v_playerSpecificUnitDataPlayerCB.setEnabled(enable);
        jLabel2.setEnabled(enable);
        jLabel3.setEnabled(enable);
        jLabel4.setEnabled(enable);
        jLabel5.setEnabled(enable);
        jLabel6.setEnabled(enable);
    }
    private void SetEnabledUnitPropertyBox(boolean enable)
    {
        for (Component component : v_unitPropertiesControlHolderPanel.getComponents())
        {
            component.setEnabled(enable);
        }
        for (Component component : v_ap1.getComponents())
        {
            component.setEnabled(enable);
        }
        for (Component component : v_ap2.getComponents())
        {
            component.setEnabled(enable);
        }
    }
    private void UpdateUnitList()
    {
        List<Object> list = new ArrayList<Object>();
        for (UnitData unit : m_units.values())
        {
            list.add(unit.GetName());
        }
        v_unitList.setListData(list.toArray());
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        if (m_lastSelectedItem > -1 && m_unitBeingEdited.trim().length() > 0 && m_units.containsKey(m_unitBeingEdited))
        {
            m_units.remove(m_unitBeingEdited);
            m_units.put(v_unitName.getText(), CreateUnitFromDataBox());
        }
        GetMapData().SetUnitsData(m_units);
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
        v_unitList = new javax.swing.JList();
        v_removeButton = new javax.swing.JButton();
        v_addButton = new javax.swing.JButton();
        v_unitDataGroupBox = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        v_unitName = new javax.swing.JTextField();
        v_playerSpecificUnitDataPlayerCB = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        v_unitImagePreview = new javax.swing.JPanel();
        v_unitImagePreviewPanelLabel = new javax.swing.JLabel();
        v_unitImage_browse = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        v_unitBuyCost = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        v_purchasable = new javax.swing.JCheckBox();
        v_unitPropertiesControlsSP = new javax.swing.JScrollPane();
        v_unitPropertiesHolderPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        v_unitPropertiesControlHolderPanel = new javax.swing.JPanel();
        v_canBlitz = new javax.swing.JCheckBox();
        v_artillery = new javax.swing.JCheckBox();
        v_isAir = new javax.swing.JCheckBox();
        v_isStrategicBomber = new javax.swing.JCheckBox();
        v_isDestroyer = new javax.swing.JCheckBox();
        v_isTwoHit = new javax.swing.JCheckBox();
        v_canBombard = new javax.swing.JCheckBox();
        v_isSea = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        v_isSub = new javax.swing.JCheckBox();
        v_isAA = new javax.swing.JCheckBox();
        v_isFactory = new javax.swing.JCheckBox();
        v_isInfantry = new javax.swing.JCheckBox();
        v_isParatroop = new javax.swing.JCheckBox();
        v_artillerySupportable = new javax.swing.JCheckBox();
        v_isMechanized = new javax.swing.JCheckBox();
        v_ap1 = new javax.swing.JPanel();
        v_carrierCapacityValue = new javax.swing.JSpinner();
        v_transportCapacityValue = new javax.swing.JSpinner();
        v_carrierCostValue = new javax.swing.JSpinner();
        v_transportCostValue = new javax.swing.JSpinner();
        v_carrierCost = new javax.swing.JCheckBox();
        v_transportCapacity = new javax.swing.JCheckBox();
        v_carrierCapacity = new javax.swing.JCheckBox();
        v_transportCost = new javax.swing.JCheckBox();
        v_ap2 = new javax.swing.JPanel();
        v_attack = new javax.swing.JLabel();
        v_defense = new javax.swing.JLabel();
        v_movement = new javax.swing.JLabel();
        v_defenseValue = new javax.swing.JSpinner();
        v_attackValue = new javax.swing.JSpinner();
        v_movementValue = new javax.swing.JSpinner();

        setMinimumSize(new java.awt.Dimension(858, 352));
        setPreferredSize(new java.awt.Dimension(858, 352));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step4ContentPanel_All.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        v_unitList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        v_unitList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        v_unitList.setName("v_unitList"); // NOI18N
        jScrollPane1.setViewportView(v_unitList);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(Segment2Step4ContentPanel_All.class, this);
        v_removeButton.setAction(actionMap.get("RemoveUnit")); // NOI18N
        v_removeButton.setText(resourceMap.getString("v_removeButton.text")); // NOI18N
        v_removeButton.setName("v_removeButton"); // NOI18N
        v_removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_removeButtonActionPerformed(evt);
            }
        });

        v_addButton.setAction(actionMap.get("NewUnit")); // NOI18N
        v_addButton.setText(resourceMap.getString("v_addButton.text")); // NOI18N
        v_addButton.setName("v_addButton"); // NOI18N
        v_addButton.setNextFocusableComponent(v_unitName);
        v_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_addButtonActionPerformed(evt);
            }
        });

        v_unitDataGroupBox.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("v_unitDataGroupBox.border.title"))); // NOI18N
        v_unitDataGroupBox.setName("v_unitDataGroupBox"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        v_unitName.setText(resourceMap.getString("v_unitName.text")); // NOI18N
        v_unitName.setEnabled(false);
        v_unitName.setName("v_unitName"); // NOI18N

        v_playerSpecificUnitDataPlayerCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3" }));
        v_playerSpecificUnitDataPlayerCB.setName("v_playerSpecificUnitDataPlayerCB"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel1.setName("jPanel1"); // NOI18N

        v_unitImagePreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        v_unitImagePreview.setName("v_unitImagePreview"); // NOI18N
        v_unitImagePreview.setPreferredSize(new java.awt.Dimension(32, 32));
        v_unitImagePreview.setLayout(null);

        v_unitImagePreviewPanelLabel.setText(resourceMap.getString("v_unitImagePreviewPanelLabel.text")); // NOI18N
        v_unitImagePreviewPanelLabel.setName("v_unitImagePreviewPanelLabel"); // NOI18N
        v_unitImagePreview.add(v_unitImagePreviewPanelLabel);
        v_unitImagePreviewPanelLabel.setBounds(1, 1, 48, 48);

        v_unitImage_browse.setAction(actionMap.get("SelectUnitImage")); // NOI18N
        v_unitImage_browse.setText(resourceMap.getString("v_unitImage_browse.text")); // NOI18N
        v_unitImage_browse.setName("v_unitImage_browse"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        v_unitBuyCost.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        v_unitBuyCost.setEnabled(false);
        v_unitBuyCost.setName("v_unitBuyCost"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        v_purchasable.setText(resourceMap.getString("v_purchasable.text")); // NOI18N
        v_purchasable.setName("v_purchasable"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_unitBuyCost, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(v_unitImagePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_unitImage_browse, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                        .addComponent(v_purchasable)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(v_purchasable))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel3))
                            .addComponent(v_unitImagePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(v_unitImage_browse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(v_unitBuyCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout v_unitDataGroupBoxLayout = new javax.swing.GroupLayout(v_unitDataGroupBox);
        v_unitDataGroupBox.setLayout(v_unitDataGroupBoxLayout);
        v_unitDataGroupBoxLayout.setHorizontalGroup(
            v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_unitDataGroupBoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_unitDataGroupBoxLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(v_unitDataGroupBoxLayout.createSequentialGroup()
                        .addGroup(v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(v_unitDataGroupBoxLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(v_unitName, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, v_unitDataGroupBoxLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(v_playerSpecificUnitDataPlayerCB, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))))
        );
        v_unitDataGroupBoxLayout.setVerticalGroup(
            v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_unitDataGroupBoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(v_unitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(v_unitDataGroupBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(v_playerSpecificUnitDataPlayerCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        v_unitPropertiesControlsSP.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("v_unitPropertiesControlsSP.border.title"))); // NOI18N
        v_unitPropertiesControlsSP.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        v_unitPropertiesControlsSP.setEnabled(false);
        v_unitPropertiesControlsSP.setName("v_unitPropertiesControlsSP"); // NOI18N

        v_unitPropertiesHolderPanel.setName("v_unitPropertiesHolderPanel"); // NOI18N
        v_unitPropertiesHolderPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        jScrollPane2.setEnabled(false);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        v_unitPropertiesControlHolderPanel.setEnabled(false);
        v_unitPropertiesControlHolderPanel.setName("v_unitPropertiesControlHolderPanel"); // NOI18N

        v_canBlitz.setText(resourceMap.getString("v_canBlitz.text")); // NOI18N
        v_canBlitz.setEnabled(false);
        v_canBlitz.setName("v_canBlitz"); // NOI18N
        v_canBlitz.setNextFocusableComponent(v_isTwoHit);

        v_artillery.setText(resourceMap.getString("v_artillery.text")); // NOI18N
        v_artillery.setEnabled(false);
        v_artillery.setName("v_artillery"); // NOI18N
        v_artillery.setNextFocusableComponent(v_isFactory);

        v_isAir.setText(resourceMap.getString("v_isAir.text")); // NOI18N
        v_isAir.setEnabled(false);
        v_isAir.setName("v_isAir"); // NOI18N
        v_isAir.setNextFocusableComponent(v_isSea);

        v_isStrategicBomber.setText(resourceMap.getString("v_isStrategicBomber.text")); // NOI18N
        v_isStrategicBomber.setEnabled(false);
        v_isStrategicBomber.setName("v_isStrategicBomber"); // NOI18N
        v_isStrategicBomber.setNextFocusableComponent(v_canBombard);

        v_isDestroyer.setText(resourceMap.getString("v_isDestroyer.text")); // NOI18N
        v_isDestroyer.setEnabled(false);
        v_isDestroyer.setName("v_isDestroyer"); // NOI18N
        v_isDestroyer.setNextFocusableComponent(v_isSub);

        v_isTwoHit.setText(resourceMap.getString("v_isTwoHit.text")); // NOI18N
        v_isTwoHit.setEnabled(false);
        v_isTwoHit.setName("v_isTwoHit"); // NOI18N
        v_isTwoHit.setNextFocusableComponent(v_isMechanized);

        v_canBombard.setText(resourceMap.getString("v_canBombard.text")); // NOI18N
        v_canBombard.setEnabled(false);
        v_canBombard.setName("v_canBombard"); // NOI18N
        v_canBombard.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                v_canBombardFocusLost(evt);
            }
        });

        v_isSea.setText(resourceMap.getString("v_isSea.text")); // NOI18N
        v_isSea.setEnabled(false);
        v_isSea.setName("v_isSea"); // NOI18N
        v_isSea.setNextFocusableComponent(v_isInfantry);

        jSeparator2.setName("jSeparator2"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        v_isSub.setText(resourceMap.getString("v_isSub.text")); // NOI18N
        v_isSub.setEnabled(false);
        v_isSub.setName("v_isSub"); // NOI18N
        v_isSub.setNextFocusableComponent(v_canBlitz);

        v_isAA.setText(resourceMap.getString("v_isAA.text")); // NOI18N
        v_isAA.setEnabled(false);
        v_isAA.setName("v_isAA"); // NOI18N
        v_isAA.setNextFocusableComponent(v_isDestroyer);

        v_isFactory.setText(resourceMap.getString("v_isFactory.text")); // NOI18N
        v_isFactory.setEnabled(false);
        v_isFactory.setName("v_isFactory"); // NOI18N
        v_isFactory.setNextFocusableComponent(v_isAA);

        v_isInfantry.setText(resourceMap.getString("v_isInfantry.text")); // NOI18N
        v_isInfantry.setEnabled(false);
        v_isInfantry.setName("v_isInfantry"); // NOI18N
        v_isInfantry.setNextFocusableComponent(v_artillery);

        v_isParatroop.setText(resourceMap.getString("v_isParatroop.text")); // NOI18N
        v_isParatroop.setEnabled(false);
        v_isParatroop.setName("v_isParatroop"); // NOI18N
        v_isParatroop.setNextFocusableComponent(v_isStrategicBomber);

        v_artillerySupportable.setText(resourceMap.getString("v_artillerySupportable.text")); // NOI18N
        v_artillerySupportable.setEnabled(false);
        v_artillerySupportable.setName("v_artillerySupportable"); // NOI18N
        v_artillerySupportable.setNextFocusableComponent(v_isParatroop);

        v_isMechanized.setText(resourceMap.getString("v_isMechanized.text")); // NOI18N
        v_isMechanized.setEnabled(false);
        v_isMechanized.setName("v_isMechanized"); // NOI18N
        v_isMechanized.setNextFocusableComponent(v_artillerySupportable);

        v_ap1.setName("v_ap1"); // NOI18N
        v_ap1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        v_carrierCapacityValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_carrierCapacityValue.setEnabled(false);
        v_carrierCapacityValue.setName("v_carrierCapacityValue"); // NOI18N
        v_carrierCapacityValue.setNextFocusableComponent(v_carrierCost);
        v_ap1.add(v_carrierCapacityValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 46, 59, 25));

        v_transportCapacityValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_transportCapacityValue.setEnabled(false);
        v_transportCapacityValue.setName("v_transportCapacityValue"); // NOI18N
        v_transportCapacityValue.setNextFocusableComponent(v_transportCost);
        v_ap1.add(v_transportCapacityValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 0, 59, 25));

        v_carrierCostValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_carrierCostValue.setEnabled(false);
        v_carrierCostValue.setName("v_carrierCostValue"); // NOI18N
        v_carrierCostValue.setNextFocusableComponent(v_isAir);
        v_ap1.add(v_carrierCostValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 69, 59, 25));

        v_transportCostValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_transportCostValue.setEnabled(false);
        v_transportCostValue.setName("v_transportCostValue"); // NOI18N
        v_transportCostValue.setNextFocusableComponent(v_carrierCapacity);
        v_ap1.add(v_transportCostValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 23, 59, 25));

        v_carrierCost.setText(resourceMap.getString("v_carrierCost.text")); // NOI18N
        v_carrierCost.setEnabled(false);
        v_carrierCost.setName("v_carrierCost"); // NOI18N
        v_carrierCost.setNextFocusableComponent(v_carrierCostValue);
        v_ap1.add(v_carrierCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 72, -1, 25));

        v_transportCapacity.setText(resourceMap.getString("v_transportCapacity.text")); // NOI18N
        v_transportCapacity.setEnabled(false);
        v_transportCapacity.setName("v_transportCapacity"); // NOI18N
        v_transportCapacity.setNextFocusableComponent(v_transportCapacityValue);
        v_ap1.add(v_transportCapacity, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 25));

        v_carrierCapacity.setText(resourceMap.getString("v_carrierCapacity.text")); // NOI18N
        v_carrierCapacity.setEnabled(false);
        v_carrierCapacity.setName("v_carrierCapacity"); // NOI18N
        v_carrierCapacity.setNextFocusableComponent(v_carrierCapacityValue);
        v_ap1.add(v_carrierCapacity, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 48, -1, 25));

        v_transportCost.setText(resourceMap.getString("v_transportCost.text")); // NOI18N
        v_transportCost.setEnabled(false);
        v_transportCost.setName("v_transportCost"); // NOI18N
        v_transportCost.setNextFocusableComponent(v_transportCostValue);
        v_ap1.add(v_transportCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 24, -1, 25));

        v_ap2.setName("v_ap2"); // NOI18N
        v_ap2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        v_attack.setText(resourceMap.getString("v_attack.text")); // NOI18N
        v_attack.setEnabled(false);
        v_attack.setName("v_attack"); // NOI18N
        v_ap2.add(v_attack, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, 59, 25));

        v_defense.setText(resourceMap.getString("v_defense.text")); // NOI18N
        v_defense.setEnabled(false);
        v_defense.setName("v_defense"); // NOI18N
        v_ap2.add(v_defense, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 36, 59, 25));

        v_movement.setText(resourceMap.getString("v_movement.text")); // NOI18N
        v_movement.setEnabled(false);
        v_movement.setName("v_movement"); // NOI18N
        v_ap2.add(v_movement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 59, 70, 25));

        v_defenseValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_defenseValue.setEnabled(false);
        v_defenseValue.setName("v_defenseValue"); // NOI18N
        v_defenseValue.setNextFocusableComponent(v_movementValue);
        v_ap2.add(v_defenseValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 36, 59, 25));

        v_attackValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_attackValue.setEnabled(false);
        v_attackValue.setName("v_attackValue"); // NOI18N
        v_attackValue.setNextFocusableComponent(v_defenseValue);
        v_ap2.add(v_attackValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 13, 59, 25));

        v_movementValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        v_movementValue.setEnabled(false);
        v_movementValue.setName("v_movementValue"); // NOI18N
        v_movementValue.setNextFocusableComponent(v_transportCapacity);
        v_ap2.add(v_movementValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 59, 59, 25));

        javax.swing.GroupLayout v_unitPropertiesControlHolderPanelLayout = new javax.swing.GroupLayout(v_unitPropertiesControlHolderPanel);
        v_unitPropertiesControlHolderPanel.setLayout(v_unitPropertiesControlHolderPanelLayout);
        v_unitPropertiesControlHolderPanelLayout.setHorizontalGroup(
            v_unitPropertiesControlHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_unitPropertiesControlHolderPanelLayout.createSequentialGroup()
                .addGroup(v_unitPropertiesControlHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(v_unitPropertiesControlHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(v_ap2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(v_ap1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(v_unitPropertiesControlHolderPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(v_unitPropertiesControlHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .addComponent(v_isAir, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isSea, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .addComponent(v_isInfantry, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_artillery, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isFactory, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isAA, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isDestroyer, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isSub, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .addComponent(v_canBlitz, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isTwoHit, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isMechanized, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_artillerySupportable, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isParatroop, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_isStrategicBomber, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_canBombard, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        v_unitPropertiesControlHolderPanelLayout.setVerticalGroup(
            v_unitPropertiesControlHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_unitPropertiesControlHolderPanelLayout.createSequentialGroup()
                .addComponent(v_ap2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_ap1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(v_isAir)
                .addComponent(v_isSea)
                .addGap(7, 7, 7)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(v_isInfantry)
                .addComponent(v_artillery)
                .addComponent(v_isFactory)
                .addComponent(v_isAA)
                .addComponent(v_isDestroyer)
                .addGap(2, 2, 2)
                .addComponent(v_isSub)
                .addGap(7, 7, 7)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(v_canBlitz)
                .addComponent(v_isTwoHit)
                .addGap(2, 2, 2)
                .addComponent(v_isMechanized)
                .addComponent(v_artillerySupportable)
                .addComponent(v_isParatroop)
                .addComponent(v_isStrategicBomber)
                .addComponent(v_canBombard)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(v_unitPropertiesControlHolderPanel);

        javax.swing.GroupLayout v_unitPropertiesHolderPanelLayout = new javax.swing.GroupLayout(v_unitPropertiesHolderPanel);
        v_unitPropertiesHolderPanel.setLayout(v_unitPropertiesHolderPanelLayout);
        v_unitPropertiesHolderPanelLayout.setHorizontalGroup(
            v_unitPropertiesHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
        );
        v_unitPropertiesHolderPanelLayout.setVerticalGroup(
            v_unitPropertiesHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
        );

        v_unitPropertiesControlsSP.setViewportView(v_unitPropertiesHolderPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(v_removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(v_addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_unitDataGroupBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_unitPropertiesControlsSP, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(v_unitPropertiesControlsSP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .addComponent(v_unitDataGroupBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_removeButton)
                            .addComponent(v_addButton))
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    private void v_removeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_removeButtonActionPerformed
    {//GEN-HEADEREND:event_v_removeButtonActionPerformed

    }//GEN-LAST:event_v_removeButtonActionPerformed
    private void ClearUnitDataBox()
    {
        v_unitName.setText("");
        v_purchasable.setSelected(false);
        v_unitBuyCost.setValue(0);
        v_unitImagePreviewPanelLabel.setIcon(new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)));
        v_playerSpecificUnitDataPlayerCB.setModel(new DefaultComboBoxModel(new String[] {""}));
        v_playerSpecificUnitDataPlayerCB.setSelectedIndex(0);
    }
    private void ClearUnitPropertyBox()
    {
        v_attackValue.setValue(0);
        v_defenseValue.setValue(0);
        v_movementValue.setValue(0);
        v_transportCapacityValue.setValue(0);
        v_transportCostValue.setValue(0);
        v_carrierCapacityValue.setValue(0);
        v_carrierCostValue.setValue(0);

        v_transportCapacity.setSelected(false);
        v_transportCapacityValue.setEnabled(false);
        v_carrierCapacity.setSelected(false);
        v_carrierCapacityValue.setEnabled(false);

        v_isAir.setSelected(false);
        v_isSea.setSelected(false);

        v_isInfantry.setSelected(false);
        v_isSea.setSelected(false);
        v_isFactory.setSelected(false);
        v_isAA.setSelected(false);
        v_isDestroyer.setSelected(false);
        v_isSub.setSelected(false);

        v_canBlitz.setSelected(false);
        v_isTwoHit.setSelected(false);
        v_isMechanized.setSelected(false);
        v_artillerySupportable.setSelected(false);
        v_isParatroop.setSelected(false);
        v_isStrategicBomber.setSelected(false);
        v_canBombard.setSelected(false);
    }
    @Action
    private void v_addButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_addButtonActionPerformed
    {//GEN-HEADEREND:event_v_addButtonActionPerformed

    }//GEN-LAST:event_v_addButtonActionPerformed

    private void v_canBombardFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_v_canBombardFocusLost
    {//GEN-HEADEREND:event_v_canBombardFocusLost
        if(evt.getOppositeComponent() != v_isStrategicBomber)
            ((BaseSegmentListPanel)m_walkthroughWindow.v_walkthroughStepsPanel.getComponents()[0]).FocusOnFirstButton();
    }//GEN-LAST:event_v_canBombardFocusLost
    private UnitData CreateUnitFromDataBox()
    {
        SavePlayerSpecificUnitDataFromBoxToVariable();
        TreeMap<String, UnitProperty> properties = CreatePropertiesFromPropertyBox();
        UnitData unit = new UnitData(v_unitName.getText(), m_playerSpecificUnitDataToSaveForUnit, properties);
        return unit;
    }
    private void SavePlayerSpecificUnitDataFromBoxToVariable()
    {
        try{v_unitBuyCost.commitEdit();}catch(Exception ex){}
        PlayerSpecificUnitData pSUnitData = new PlayerSpecificUnitData(m_playerWhosUnitDataIsBeingEdited, v_purchasable.isSelected(), (BufferedImage)((ImageIcon)v_unitImagePreviewPanelLabel.getIcon()).getImage(), Integer.parseInt(v_unitBuyCost.getValue().toString()));
        m_playerSpecificUnitDataToSaveForUnit.put(m_playerWhosUnitDataIsBeingEdited, pSUnitData);
    }
    /**
     * This is a hack, that is used at the end to find default unit attachments, so we don't have to write all unit attachments to xml.
     * Note that this method only ensures the return of default properties if the panel has not been used by the user.
     */
    public TreeMap<String, UnitProperty> GetDefaultUnitProperties()
    {
        return CreatePropertiesFromPropertyBox();
    }
    private TreeMap<String, UnitProperty> CreatePropertiesFromPropertyBox()
    {
        TreeMap<String, UnitProperty> result = new TreeMap<String, UnitProperty>();

        result.put("attack", new UnitProperty("attack", v_attackValue.getValue()));
        result.put("defense", new UnitProperty("defense", v_defenseValue.getValue()));
        result.put("movement", new UnitProperty("movement", v_movementValue.getValue()));

        result.put("transportCapacity", new UnitProperty("transportCapacity", v_transportCapacity.isSelected() ? v_transportCapacityValue.getValue() : 0));
        result.put("transportCost", new UnitProperty("transportCost", v_transportCost.isSelected() ? v_transportCostValue.getValue() : 0));
        result.put("carrierCapacity", new UnitProperty("carrierCapacity", v_carrierCapacity.isSelected() ? v_carrierCapacityValue.getValue() : 0));
        result.put("carrierCost", new UnitProperty("carrierCost", v_carrierCost.isSelected() ? v_carrierCostValue.getValue() : 0));

        result.put("isAir", new UnitProperty("isAir", v_isAir.isSelected()));
        result.put("isSea", new UnitProperty("isSea", v_isSea.isSelected()));

        result.put("isInfantry", new UnitProperty("isInfantry", v_isInfantry.isSelected()));
        result.put("artillery", new UnitProperty("artillery", v_artillery.isSelected()));
        result.put("isFactory", new UnitProperty("isFactory", v_isFactory.isSelected()));
        result.put("isAA", new UnitProperty("isAA", v_isAA.isSelected()));
        result.put("isDestroyer", new UnitProperty("isDestroyer", v_isDestroyer.isSelected()));
        result.put("isSub", new UnitProperty("isSub", v_isSub.isSelected()));

        result.put("canBlitz", new UnitProperty("canBlitz", v_canBlitz.isSelected()));
        result.put("isTwoHit", new UnitProperty("isTwoHit", v_isTwoHit.isSelected()));
        result.put("isMechanized", new UnitProperty("isMechanized", v_isMechanized.isSelected()));
        result.put("artillerySupportable", new UnitProperty("artillerySupportable", v_artillerySupportable.isSelected()));
        result.put("isParatroop", new UnitProperty("isParatroop", v_isParatroop.isSelected()));
        result.put("isStrategicBomber", new UnitProperty("isStrategicBomber", v_isStrategicBomber.isSelected()));
        result.put("canBombard", new UnitProperty("canBombard", v_canBombard.isSelected()));

        return result;
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
    public void SelectUnitImage()
    {
        try
        {
            if (!m_units.containsKey(v_unitList.getSelectedValue().toString()))
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
            FileOpen open = new FileOpen(this, "Please select the image for the unit '" + v_unitList.getSelectedValue().toString() + "'", filters);
            if(open.getFile() == null || !open.getFile().exists())
                return;
            BufferedImage image = ImageIO.read(open.getFile());
            
            //The code below crops the image to 32 by 32 pixels in size.
            BufferedImage bimage = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
            Graphics m_graphics = bimage.createGraphics();
            m_graphics.drawImage(image, 0, 0, 32, 32, this);

            v_unitImagePreviewPanelLabel.setIcon(new ImageIcon(image));
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
    }
    @Action
    public void RemoveUnit()
    {
        if (!m_units.containsKey(v_unitList.getSelectedValue().toString()) || (ProgramSettings.LoadSettings().ShowConfirmationBoxesForDataEditing && !confirm("Are you sure you want to delete this unit?")))
            return;
        m_units.remove(v_unitList.getSelectedValue().toString());
        m_lastSelectedItem = -1;
        UpdateUnitList();
        ClearUnitDataBox();
    }

    @Action
    public void NewUnit()
    {
        String name = RetrieveInputString("Please enter the name of the new unit:", "");
        if (name == null)
            return;
        UnitData unit = new UnitData(name, new TreeMap<String, PlayerSpecificUnitData>(), new TreeMap<String, UnitProperty>());
        if(m_units.containsKey(name) && !confirm("Another unit exists with the same name.\r\n\r\nDo you want to overwrite it?"))
            return;
        m_units.put(name, unit);
        UpdateUnitList();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton v_addButton;
    private javax.swing.JPanel v_ap1;
    private javax.swing.JPanel v_ap2;
    private javax.swing.JCheckBox v_artillery;
    private javax.swing.JCheckBox v_artillerySupportable;
    private javax.swing.JLabel v_attack;
    private javax.swing.JSpinner v_attackValue;
    private javax.swing.JCheckBox v_canBlitz;
    private javax.swing.JCheckBox v_canBombard;
    private javax.swing.JCheckBox v_carrierCapacity;
    private javax.swing.JSpinner v_carrierCapacityValue;
    private javax.swing.JCheckBox v_carrierCost;
    private javax.swing.JSpinner v_carrierCostValue;
    private javax.swing.JLabel v_defense;
    private javax.swing.JSpinner v_defenseValue;
    private javax.swing.JCheckBox v_isAA;
    private javax.swing.JCheckBox v_isAir;
    private javax.swing.JCheckBox v_isDestroyer;
    private javax.swing.JCheckBox v_isFactory;
    private javax.swing.JCheckBox v_isInfantry;
    private javax.swing.JCheckBox v_isMechanized;
    private javax.swing.JCheckBox v_isParatroop;
    private javax.swing.JCheckBox v_isSea;
    private javax.swing.JCheckBox v_isStrategicBomber;
    private javax.swing.JCheckBox v_isSub;
    private javax.swing.JCheckBox v_isTwoHit;
    private javax.swing.JLabel v_movement;
    private javax.swing.JSpinner v_movementValue;
    private javax.swing.JComboBox v_playerSpecificUnitDataPlayerCB;
    private javax.swing.JCheckBox v_purchasable;
    private javax.swing.JButton v_removeButton;
    private javax.swing.JCheckBox v_transportCapacity;
    private javax.swing.JSpinner v_transportCapacityValue;
    private javax.swing.JCheckBox v_transportCost;
    private javax.swing.JSpinner v_transportCostValue;
    private javax.swing.JSpinner v_unitBuyCost;
    private javax.swing.JPanel v_unitDataGroupBox;
    private javax.swing.JPanel v_unitImagePreview;
    private javax.swing.JLabel v_unitImagePreviewPanelLabel;
    private javax.swing.JButton v_unitImage_browse;
    private javax.swing.JList v_unitList;
    private javax.swing.JTextField v_unitName;
    private javax.swing.JPanel v_unitPropertiesControlHolderPanel;
    private javax.swing.JScrollPane v_unitPropertiesControlsSP;
    private javax.swing.JPanel v_unitPropertiesHolderPanel;
    // End of variables declaration//GEN-END:variables

}
