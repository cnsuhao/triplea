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

import MainProgram.Map.MapData;
import MainProgram.Map.Player.Alliance;
import MainProgram.Map.Player.PlayerData;
import MainProgram.Map.Unit.UnitData;
import MainProgram.Walkthrough.UI.WalkthroughWindow;
import Resources.ResourceAddingWindow;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Stephen
 */
public class Segment2Step1ContentPanel_All extends BaseStepContentPanel {

    /** Creates new form BaseStepContentPanel */
    public Segment2Step1ContentPanel_All()
    {
        initComponents();
    }
    @Override
    public void SetWalkthroughWindow(WalkthroughWindow window)
    {
        m_walkthroughWindow = window;
    }
    boolean w_changePreset = false;
    @Override
    public boolean WaitForPanelClose()
    {
        if (GetMapData().GetMapPresetIndex() != -1)
        {
            int index = GetSelectedPresetIndex();
            if (GetMapData().GetMapPresetIndex() == index)
            {
                w_changePreset = false;
                return true;
            }
            else
            {
                int result = JOptionPane.showConfirmDialog(this, "If you change the map preset now, you will lose ALL map xml information entered after this step.\r\n\r\nAre you sure you want to change the map preset?", "Data Loss Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
                {
                    w_changePreset = false;
                    return false;
                }
                else if (result == JOptionPane.NO_OPTION)
                {
                    w_changePreset = false;
                    return true;
                }
                else
                {
                    w_changePreset = true;
                    return true;
                }
            }
        }
        w_changePreset = true;
        return true;
    }
    @Override
    public void InitControls()
    {
        switch (GetMapData().GetMapPresetIndex())
        {
            case -1:
            {
                v_revisedPreset.setSelected(true);
                break;
            }
            case 0:
            {
                v_minimapPreset.setSelected(true);
                break;
            }
            case 1:
            {
                v_revisedPreset.setSelected(true);
                break;
            }
            case 2:
            {
                v_WWIIv3Preset.setSelected(true);
                break;
            }
            case 3:
            {
                v_WWIIv4Preset.setSelected(true);
                break;
            }
            case 4:
            {
                v_NWOPreset.setSelected(true);
                break;
            }
            case 5:
            {
                v_emptyPreset.setSelected(true);
                break;
            }
        }
    }
    @Override
    public void ProcessControlContentToMapData()
    {
        if(!w_changePreset)
            return;

        int index = GetSelectedPresetIndex();
        if (GetMapData().GetMapPresetIndex() != index)
        {
            GetMapData().SetMapPresetIndex(index);
            ApplyPresetValues();
        }
    }
    private int GetSelectedPresetIndex()
    {
        int index = 0;
        if (v_minimapPreset.isSelected())
        {
            index = 0;
        }
        else if (v_revisedPreset.isSelected())
        {
            index = 1;
        }
        else if (v_WWIIv3Preset.isSelected())
        {
            index = 2;
        }
        else if (v_WWIIv4Preset.isSelected())
        {
            index = 3;
        }
        else if (v_NWOPreset.isSelected())
        {
            index = 4;
        }
        else
        {
            index = 5;
        }
        return index;
    }
    private BufferedImage ToBImage(Image image)
    {
        BufferedImage bimage = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
        Graphics m_graphics = bimage.createGraphics();
        m_graphics.drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this), this);
        return bimage;
    }
    private void ApplyPresetValues()
    {
        int preset = GetMapData().GetMapPresetIndex();
        if(preset == 0)
        {
            Alliance allies = new Alliance("Allies");
            Alliance axis = new Alliance("Axis");
            TreeMap<String, Alliance> alliesA = new TreeMap<String, Alliance>();
            alliesA.put("Allies", allies);
            TreeMap<String, Alliance> axisA = new TreeMap<String, Alliance>();
            axisA.put("Axis", axis);

            TreeMap<String, Alliance> alliances = new TreeMap<String, Alliance>();
            alliances.put("Axis", axis);
            alliances.put("Allies", allies);
            GetMapData().SetAlliances(alliances);

            TreeMap<String, PlayerData> players = new TreeMap<String, PlayerData>();
            players.put("Russians", new PlayerData("Russians", WebColor("993300"), 31, ToBImage(ResourceAddingWindow.GetImage("Russians.png")), alliesA, 0));
            players.put("Italians", new PlayerData("Italians", WebColor("236B8E"), 31, ToBImage(ResourceAddingWindow.GetImage("Italians.png")), axisA, 1));
            GetMapData().SetPlayersData(players);

            //TreeMap<String, UnitData> units = new TreeMap<String, UnitData>();
            //units.put("Infantry", new UnitData("Infantry", null, null, preset))
        }
        else if(preset == 1)
        {
            Alliance allies = new Alliance("Allies");
            Alliance axis = new Alliance("Axis");
            TreeMap<String, Alliance> alliesA = new TreeMap<String, Alliance>();
            alliesA.put("Allies", allies);
            TreeMap<String, Alliance> axisA = new TreeMap<String, Alliance>();
            axisA.put("Axis", axis);

            TreeMap<String, Alliance> alliances = new TreeMap<String, Alliance>();
            alliances.put("Axis", axis);
            alliances.put("Allies", allies);
            GetMapData().SetAlliances(alliances);

            TreeMap<String, PlayerData> players = new TreeMap<String, PlayerData>();
            players.put("Russians", new PlayerData("Russians", WebColor("af2828"), 24, ToBImage(ResourceAddingWindow.GetImage("Russians.png")), alliesA, 0));
            players.put("British", new PlayerData("British", WebColor("916400"), 30, ToBImage(ResourceAddingWindow.GetImage("British.png")), alliesA, 2));
            players.put("Americans", new PlayerData("Americans", WebColor("5a5a00"), 42, ToBImage(ResourceAddingWindow.GetImage("Americans.png")), alliesA, 4));
            players.put("Germans", new PlayerData("Germans", WebColor("5a5a5a"), 40, ToBImage(ResourceAddingWindow.GetImage("Germans.png")), axisA, 1));
            players.put("Japanese", new PlayerData("Japanese", WebColor("e6b42d"), 30, ToBImage(ResourceAddingWindow.GetImage("Japanese.png")), axisA, 3));
            GetMapData().SetPlayersData(players);
        }
        else if(preset == 2)
        {
            Alliance allies = new Alliance("Allies");
            Alliance axis = new Alliance("Axis");
            TreeMap<String, Alliance> alliesA = new TreeMap<String, Alliance>();
            alliesA.put("Allies", allies);
            TreeMap<String, Alliance> axisA = new TreeMap<String, Alliance>();
            axisA.put("Axis", axis);

            TreeMap<String, Alliance> alliances = new TreeMap<String, Alliance>();
            alliances.put("Axis", axis);
            alliances.put("Allies", allies);
            GetMapData().SetAlliances(alliances);

            TreeMap<String, PlayerData> players = new TreeMap<String, PlayerData>();
            players.put("Russians", new PlayerData("Russians", WebColor("af2828"), 24, ToBImage(ResourceAddingWindow.GetImage("Russians.png")), alliesA, 1));
            players.put("British", new PlayerData("British", WebColor("916400"), 31, ToBImage(ResourceAddingWindow.GetImage("British.png")), alliesA, 3));
            players.put("Americans", new PlayerData("Americans", WebColor("5a5a00"), 38, ToBImage(ResourceAddingWindow.GetImage("Americans.png")), alliesA, 6));
            players.put("Germans", new PlayerData("Germans", WebColor("5a5a5a"), 37, ToBImage(ResourceAddingWindow.GetImage("Germans.png")), axisA, 2));
            players.put("Japanese", new PlayerData("Japanese", WebColor("e6b42d"), 31, ToBImage(ResourceAddingWindow.GetImage("Japanese.png")), axisA, 0));
            players.put("Italians", new PlayerData("Italians", WebColor("394d86"), 10, ToBImage(ResourceAddingWindow.GetImage("Italians.png")), axisA, 4));
            players.put("Chinese", new PlayerData("Chinese", WebColor("4a7300"), 0, ToBImage(ResourceAddingWindow.GetImage("Chinese.png")), axisA, 5));
            GetMapData().SetPlayersData(players);
        }
        else if(preset == 3)
        {
            Alliance allies = new Alliance("Allies");
            Alliance axis = new Alliance("Axis");
            TreeMap<String, Alliance> alliesA = new TreeMap<String, Alliance>();
            alliesA.put("Allies", allies);
            TreeMap<String, Alliance> axisA = new TreeMap<String, Alliance>();
            axisA.put("Axis", axis);

            TreeMap<String, Alliance> alliances = new TreeMap<String, Alliance>();
            alliances.put("Axis", axis);
            alliances.put("Allies", allies);
            GetMapData().SetAlliances(alliances);

            TreeMap<String, PlayerData> players = new TreeMap<String, PlayerData>();
            players.put("Russians", new PlayerData("Russians", WebColor("af2828"), 24, ToBImage(ResourceAddingWindow.GetImage("Russians.png")), alliesA, 0));
            players.put("British", new PlayerData("British", WebColor("916400"), 30, ToBImage(ResourceAddingWindow.GetImage("British.png")), alliesA, 2));
            players.put("Americans", new PlayerData("Americans", WebColor("5a5a00"), 42, ToBImage(ResourceAddingWindow.GetImage("Americans.png")), alliesA, 4));
            players.put("Germans", new PlayerData("Germans", WebColor("5a5a5a"), 40, ToBImage(ResourceAddingWindow.GetImage("Germans.png")), axisA, 1));
            players.put("Japanese", new PlayerData("Japanese", WebColor("e6b42d"), 30, ToBImage(ResourceAddingWindow.GetImage("Japanese.png")), axisA, 3));
            GetMapData().SetPlayersData(players);
        }
        else if (preset == 4)
        {
            Alliance allies = new Alliance("Allies");
            Alliance axis = new Alliance("Axis");
            TreeMap<String, Alliance> alliesA = new TreeMap<String, Alliance>();
            alliesA.put("Allies", allies);
            TreeMap<String, Alliance> axisA = new TreeMap<String, Alliance>();
            axisA.put("Axis", axis);

            TreeMap<String, Alliance> alliances = new TreeMap<String, Alliance>();
            alliances.put("Axis", axis);
            alliances.put("Allies", allies);
            GetMapData().SetAlliances(alliances);

            TreeMap<String, PlayerData> players = new TreeMap<String, PlayerData>();
            players.put("Russians", new PlayerData("Russians", WebColor("993300"), 23, ToBImage(ResourceAddingWindow.GetImage("RussiansNWO.png")), alliesA, 1));
            players.put("British", new PlayerData("British", WebColor("9C661F"), 14, ToBImage(ResourceAddingWindow.GetImage("BritishNWO.png")), alliesA, 6));
            players.put("Americans", new PlayerData("Americans", WebColor("556B2F"), 40, ToBImage(ResourceAddingWindow.GetImage("AmericansNWO.png")), alliesA, 8));
            players.put("Germans", new PlayerData("Germans", WebColor("666666"), 60, ToBImage(ResourceAddingWindow.GetImage("GermansNWO.png")), axisA, 0));
            players.put("Italians", new PlayerData("Italians", WebColor("ffffff"), 30, ToBImage(ResourceAddingWindow.GetImage("ItaliansNWO.png")), axisA, 2));
            players.put("French", new PlayerData("French", WebColor("8B7355"), 6, ToBImage(ResourceAddingWindow.GetImage("French.png")), alliesA, 3));
            players.put("ColonialFrench", new PlayerData("ColonialFrench", WebColor("8B7765"), 0, ToBImage(ResourceAddingWindow.GetImage("ColonialFrench.png")), alliesA, 4));
            players.put("Finns", new PlayerData("Finns", WebColor("778899"), 6, ToBImage(ResourceAddingWindow.GetImage("Finns.png")), axisA, 5));
            players.put("Romanians", new PlayerData("Romanians", WebColor("7A8B8B"), 4, ToBImage(ResourceAddingWindow.GetImage("Romanians.png")), axisA, 7));
            GetMapData().SetPlayersData(players);
        }
        else
        {
            GetMapData().SetAlliances(new TreeMap<String, Alliance>());
            GetMapData().SetPlayersData(new TreeMap<String, PlayerData>());
        }
    }
    private Color WebColor(String str)
    {
        return Color.decode("#" + str);
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
        v_minimapPreset = new javax.swing.JRadioButton();
        v_revisedPreset = new javax.swing.JRadioButton();
        v_WWIIv3Preset = new javax.swing.JRadioButton();
        v_WWIIv4Preset = new javax.swing.JRadioButton();
        v_NWOPreset = new javax.swing.JRadioButton();
        v_emptyPreset = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        v_presetDescription = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        v_infoBox = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(723, 352));

        buttonGroup1.add(v_minimapPreset);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step1ContentPanel_All.class);
        v_minimapPreset.setText(resourceMap.getString("v_minimapPreset.text")); // NOI18N
        v_minimapPreset.setName("v_minimapPreset"); // NOI18N
        v_minimapPreset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        buttonGroup1.add(v_revisedPreset);
        v_revisedPreset.setSelected(true);
        v_revisedPreset.setText(resourceMap.getString("v_revisedPreset.text")); // NOI18N
        v_revisedPreset.setName("v_revisedPreset"); // NOI18N
        v_revisedPreset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        buttonGroup1.add(v_WWIIv3Preset);
        v_WWIIv3Preset.setText(resourceMap.getString("v_WWIIv3Preset.text")); // NOI18N
        v_WWIIv3Preset.setName("v_WWIIv3Preset"); // NOI18N
        v_WWIIv3Preset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        buttonGroup1.add(v_WWIIv4Preset);
        v_WWIIv4Preset.setText(resourceMap.getString("v_WWIIv4Preset.text")); // NOI18N
        v_WWIIv4Preset.setName("v_WWIIv4Preset"); // NOI18N
        v_WWIIv4Preset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        buttonGroup1.add(v_NWOPreset);
        v_NWOPreset.setText(resourceMap.getString("v_NWOPreset.text")); // NOI18N
        v_NWOPreset.setName("v_NWOPreset"); // NOI18N
        v_NWOPreset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        buttonGroup1.add(v_emptyPreset);
        v_emptyPreset.setText(resourceMap.getString("v_emptyPreset.text")); // NOI18N
        v_emptyPreset.setName("v_emptyPreset"); // NOI18N
        v_emptyPreset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                v_WWIIv4PresetStateChanged(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        v_presetDescription.setColumns(20);
        v_presetDescription.setEditable(false);
        v_presetDescription.setFont(resourceMap.getFont("v_presetDescription.font")); // NOI18N
        v_presetDescription.setLineWrap(true);
        v_presetDescription.setRows(5);
        v_presetDescription.setText(resourceMap.getString("v_presetDescription.text")); // NOI18N
        v_presetDescription.setWrapStyleWord(true);
        v_presetDescription.setFocusable(false);
        v_presetDescription.setName("v_presetDescription"); // NOI18N
        jScrollPane1.setViewportView(v_presetDescription);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        v_infoBox.setBackground(resourceMap.getColor("v_infoBox.background")); // NOI18N
        v_infoBox.setColumns(20);
        v_infoBox.setEditable(false);
        v_infoBox.setFont(resourceMap.getFont("v_infoBox.font")); // NOI18N
        v_infoBox.setLineWrap(true);
        v_infoBox.setRows(5);
        v_infoBox.setText(resourceMap.getString("v_infoBox.text")); // NOI18N
        v_infoBox.setWrapStyleWord(true);
        v_infoBox.setFocusable(false);
        v_infoBox.setName("v_infoBox"); // NOI18N
        jScrollPane2.setViewportView(v_infoBox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_minimapPreset)
                            .addComponent(v_revisedPreset)
                            .addComponent(v_WWIIv3Preset)
                            .addComponent(v_WWIIv4Preset)
                            .addComponent(v_NWOPreset)
                            .addComponent(v_emptyPreset))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(217, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(v_minimapPreset)
                        .addGap(7, 7, 7)
                        .addComponent(v_revisedPreset)
                        .addGap(7, 7, 7)
                        .addComponent(v_WWIIv3Preset)
                        .addGap(7, 7, 7)
                        .addComponent(v_WWIIv4Preset)
                        .addGap(7, 7, 7)
                        .addComponent(v_NWOPreset)
                        .addGap(7, 7, 7)
                        .addComponent(v_emptyPreset))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void v_WWIIv4PresetStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_v_WWIIv4PresetStateChanged
    {//GEN-HEADEREND:event_v_WWIIv4PresetStateChanged
        UpdateDescription();
    }//GEN-LAST:event_v_WWIIv4PresetStateChanged
    private void UpdateDescription()
    {
        if(v_minimapPreset.isSelected())
        {
            v_presetDescription.setText("This preset follows the rules and gameplay used in the minimap that is included with TripleA.");
        }
        else if(v_revisedPreset.isSelected())
        {
            v_presetDescription.setText("This preset follows the rules and gameplay used in the revised version of the WWII map series.");
        }
        else if(v_WWIIv3Preset.isSelected())
        {
            v_presetDescription.setText("This preset follows the rules and gameplay used in the WWIIv3 version of the WWII map series.");
        }
        else if(v_WWIIv4Preset.isSelected())
        {
            v_presetDescription.setText("This preset follows the rules and gameplay used in the WWIIv4 version of the WWII map series.");
        }
        else if(v_NWOPreset.isSelected())
        {
            v_presetDescription.setText("This preset follows the rules and gameplay used in the latest version of the NWO map series.");
        }
        else
        {
            v_presetDescription.setText("This preset lets you create your map from scratch.");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton v_NWOPreset;
    private javax.swing.JRadioButton v_WWIIv3Preset;
    private javax.swing.JRadioButton v_WWIIv4Preset;
    private javax.swing.JRadioButton v_emptyPreset;
    private javax.swing.JTextArea v_infoBox;
    private javax.swing.JRadioButton v_minimapPreset;
    private javax.swing.JTextArea v_presetDescription;
    private javax.swing.JRadioButton v_revisedPreset;
    // End of variables declaration//GEN-END:variables

}
