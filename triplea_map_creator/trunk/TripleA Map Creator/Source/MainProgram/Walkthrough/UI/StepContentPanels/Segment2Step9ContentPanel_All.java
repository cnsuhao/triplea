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
import MainProgram.Map.MapProperty;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Stephen
 */
public class Segment2Step9ContentPanel_All extends BaseStepContentPanel
{
    public Segment2Step9ContentPanel_All()
    {
        initComponents();
    }
    
    //Btw, for very simple data, its okay if we read/write from map data directly, without local variables like m_playerBids
    private TreeMap<String, MapProperty> m_playerBids = new TreeMap<String, MapProperty>();  
    
    @Override
    public boolean WaitForPanelClose()
    {
        return true;
    }
    
    @Override
    public void InitControls()
    {
        VerifyPlayerBids(GetMapData().GetPlayerBids());
        m_playerBids = GetMapData().GetPlayerBids();
        InitPlayerBidsArea(m_playerBids);
        v_showTerritoryNames.setSelected(GetMapData().GetShowTerritoryNames());
        v_minimapScale.setValue(GetMapData().GetMinimapScalePercentage());
    }
    
    private void InitPlayerBidsArea(TreeMap<String, MapProperty> playerBids)
    {
        int index = 0;
        for(String player : GetMapData().GetPlayersData().keySet())
        {
            JPanel panel = new JPanel();
            panel.setName(player);
            panel.setLayout(new GridLayout(1, 0));
            
            JLabel label = new JLabel(player + "'s Bid: ");
            panel.add(label);
            
            JSpinner spinner = new JSpinner();
            int val = 0;
            if(playerBids.containsKey(player))
                val = Integer.parseInt(playerBids.get(player).GetValue().toString());
            spinner.setModel(new SpinnerNumberModel(val, 0, 5000, 1));
            panel.add(spinner);
            
            JCheckBox checkbox = new JCheckBox(" Editable (In TripleA)");
            if(playerBids.containsKey(player))
                checkbox.setSelected(playerBids.get(player).IsEditable());
            panel.add(checkbox);
            
            v_playerBidsHolderPanel.add(panel, new GridBagConstraints(0, index, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            index++;
        }
        
        //We use this helper panel to get the other controls to layout properly (at the top left)
        JPanel layoutHelper = new JPanel();
        layoutHelper.setName("layoutHelper");
        v_playerBidsHolderPanel.add(layoutHelper, new GridBagConstraints(1, index + 1, 1, 1, 99, 99, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }
    
    private void VerifyPlayerBids(TreeMap<String, MapProperty> playerBids)
    {
        MapData mapData = GetMapData();
        ArrayList<String> problemPlayerBids = new ArrayList<String>();
        for (Entry<String, MapProperty> entry : playerBids.entrySet())
        {
            String name = entry.getKey();
            MapProperty bid = entry.getValue();
            
            if(!mapData.GetPlayersData().containsKey(bid.GetName()))
                problemPlayerBids.add(name);
        }
        if (problemPlayerBids.size() > 0)
        {
            if (JOptionPane.showConfirmDialog(this, "Some of the player bids refer to players that no longer exist. Do you want to remove these player bids?", "Reset Invalid Player Bids", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                for (String name : problemPlayerBids)
                    mapData.GetPlayerBids().remove(name);
            }
        }
        
        //Now loop through the players and set player's bid to default value wherever missing
        for(String name : mapData.GetPlayersData().keySet())
        {
            if(!playerBids.containsKey(name))
                playerBids.put(name, new MapProperty(name, 0, true));
        }
    }
    
    @Override
    public void ProcessControlContentToMapData()
    {
        try{v_minimapScale.commitEdit();}catch (Exception ex){}
        
        m_playerBids = CreatePlayerBidsFromBidsPanel(); //This isn't normally how we do it, but in this case it's okay
        GetMapData().SetPlayerBids(m_playerBids);
        GetMapData().SetShowTerritoryNames(v_showTerritoryNames.isSelected());
        GetMapData().SetMinimapScalePercentage(Integer.parseInt(v_minimapScale.getValue().toString()));
    }
    
    private TreeMap<String, MapProperty> CreatePlayerBidsFromBidsPanel()
    {
        TreeMap<String, MapProperty> result = new TreeMap<String, MapProperty>();
        
        for (Component component : v_playerBidsHolderPanel.getComponents())
        {
            if(component.getName().equals("layoutHelper"))
                continue; //We use this helper panel to get the other controls to layout properly (at the top left)
            JPanel panel = (JPanel)component;
            String playerName = panel.getName();

            JSpinner spinner = (JSpinner) panel.getComponent(1);
            try{spinner.commitEdit();}catch(Exception ex){}
            int value = Integer.parseInt(spinner.getValue().toString());

            JCheckBox checkbox = (JCheckBox) panel.getComponent(2);
            result.put(playerName, new MapProperty(playerName, value, checkbox.isSelected()));
        }

        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        v_playerBidsScrollPane = new javax.swing.JScrollPane();
        v_playerBidsHolderPanel = new javax.swing.JPanel();
        v_othersScrollPane = new javax.swing.JScrollPane();
        v_othersHolderPanel = new javax.swing.JPanel();
        v_showTerritoryNames = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        v_minimapScale = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        v_previewMinimap = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(Segment2Step9ContentPanel_All.class);
        v_playerBidsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("v_playerBidsScrollPane.border.title"))); // NOI18N
        v_playerBidsScrollPane.setName("v_playerBidsScrollPane"); // NOI18N

        v_playerBidsHolderPanel.setMaximumSize(new java.awt.Dimension(287, 32767));
        v_playerBidsHolderPanel.setName("v_playerBidsHolderPanel"); // NOI18N
        v_playerBidsHolderPanel.setLayout(new java.awt.GridBagLayout());
        v_playerBidsScrollPane.setViewportView(v_playerBidsHolderPanel);

        v_othersScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("v_othersScrollPane.border.title"))); // NOI18N
        v_othersScrollPane.setName("v_othersScrollPane"); // NOI18N

        v_othersHolderPanel.setMaximumSize(new java.awt.Dimension(287, 32767));
        v_othersHolderPanel.setName("v_othersHolderPanel"); // NOI18N
        v_othersHolderPanel.setPreferredSize(new java.awt.Dimension(25, 25));

        v_showTerritoryNames.setSelected(true);
        v_showTerritoryNames.setText(resourceMap.getString("v_showTerritoryNames.text")); // NOI18N
        v_showTerritoryNames.setName("v_showTerritoryNames"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        v_minimapScale.setModel(new javax.swing.SpinnerNumberModel(10, 1, 50, 1));
        v_minimapScale.setName("v_minimapScale"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        v_previewMinimap.setText(resourceMap.getString("v_previewMinimap.text")); // NOI18N
        v_previewMinimap.setToolTipText(resourceMap.getString("v_previewMinimap.toolTipText")); // NOI18N
        v_previewMinimap.setName("v_previewMinimap"); // NOI18N
        v_previewMinimap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                v_previewMinimapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout v_othersHolderPanelLayout = new javax.swing.GroupLayout(v_othersHolderPanel);
        v_othersHolderPanel.setLayout(v_othersHolderPanelLayout);
        v_othersHolderPanelLayout.setHorizontalGroup(
            v_othersHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_othersHolderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(v_othersHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(v_showTerritoryNames)
                    .addGroup(v_othersHolderPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(v_minimapScale, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_previewMinimap)))
                .addContainerGap())
        );
        v_othersHolderPanelLayout.setVerticalGroup(
            v_othersHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_othersHolderPanelLayout.createSequentialGroup()
                .addComponent(v_showTerritoryNames)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(v_othersHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(v_previewMinimap)
                    .addComponent(jLabel2)
                    .addComponent(v_minimapScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(250, Short.MAX_VALUE))
        );

        v_othersScrollPane.setViewportView(v_othersHolderPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(v_playerBidsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_othersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(v_othersScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .addComponent(v_playerBidsScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void v_previewMinimapActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_v_previewMinimapActionPerformed
    {//GEN-HEADEREND:event_v_previewMinimapActionPerformed
        try{v_minimapScale.commitEdit();}catch (Exception ex){}
        float scale = (float)(Integer.parseInt(v_minimapScale.getValue().toString()) / 100.0F);
        
        Dimension fullsize = new Dimension(GetMapData().GetBaseMapImage().getIconWidth(), GetMapData().GetBaseMapImage().getIconHeight());
        Image scaledMinimap = GetMapData().GetBaseMapImage().getImage().getScaledInstance((int)(fullsize.width * scale), (int)(fullsize.height * scale), BufferedImage.SCALE_SMOOTH);
        
        JLabel label = new JLabel(new ImageIcon(scaledMinimap));
        
        JOptionPane.showMessageDialog(m_walkthroughWindow, label, "Minimap Preview", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_v_previewMinimapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSpinner v_minimapScale;
    private javax.swing.JPanel v_othersHolderPanel;
    private javax.swing.JScrollPane v_othersScrollPane;
    private javax.swing.JPanel v_playerBidsHolderPanel;
    private javax.swing.JScrollPane v_playerBidsScrollPane;
    private javax.swing.JButton v_previewMinimap;
    private javax.swing.JCheckBox v_showTerritoryNames;
    // End of variables declaration//GEN-END:variables
}
