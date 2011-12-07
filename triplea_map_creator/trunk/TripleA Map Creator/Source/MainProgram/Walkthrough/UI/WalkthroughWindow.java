/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*
 * WalkthroughWindow.java
 *
 * Created on May 11, 2010, 8:12:02 PM
 */

package MainProgram.Walkthrough.UI;

import Global.BrowserLauncher;
import Global.Constants;
import Global.Enums;
import Global.FileFinder;
import Global.FolderFinder;
import Global.ProgramSettings;
import Global.Version;
import MainProgram.Console.ErrorConsole;
import MainProgram.Map.MapData;
import MainProgram.Map.MapDataExporter;
import MainProgram.ProgramLaunchers.ProgramLauncher;
import MainProgram.Project.Project;
import MainProgram.Project.ProjectInformation;
import MainProgram.UI.FileOpen;
import MainProgram.UI.FileSave;
import MainProgram.UI.MainMenu;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.BaseSegmentListPanel;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.Segment1StepListPanel;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.Segment2StepListPanel;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.Segment3StepListPanel;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.Segment4StepListPanel;
import MainProgram.Walkthrough.UI.SegmentStepListPanels.Segment5StepListPanel;
import MainProgram.Walkthrough.UI.StepContentPanels.*;
import MainProgram.Walkthrough.WalkthroughData.Instructions.InstructionSet;
import MainProgram.Walkthrough.WalkthroughData.WalkthroughData;
import MainProgram.Walkthrough.WalkthroughData.WalkthroughOptions;
import MainProgram.Walkthrough.WalkthroughStep;
import Threading.BackgroundTaskRunner;
import Utils.Controls;
import Utils.FileSystem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/**
 *
 * @author Stephen
 */
public class WalkthroughWindow extends javax.swing.JFrame
{
    /** Creates new form WalkthroughWindow */
    public WalkthroughWindow(MainMenu mainMenu, Enums.LaunchType launchType, Project project)
    {
        ErrorConsole.SetWalkthroughWindow(this);

        m_mainMenu = mainMenu;
        m_launchType = launchType;
        m_project = project;
        m_autoBackupTimer.start();

        initComponents();        
        setIconImage(ProgramLauncher.getProgramIcon(this));

        UpdateSegmentControls();

        //ShowPanelForStep(project.GetMapData().GetWalkthroughData().GetCurrentStep()); //Added elsewhere... Unnecessary
        //ShowStepListPanelsForSegment(project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment()); //Added elsewhere... Unnecessary
        if(launchType == launchType.ContinueProject)
            InitLoadedMapStepInstructions();
        else
            UpdateNewStepInstructions();
    }
    
    private MainMenu m_mainMenu = null;
    private Enums.LaunchType m_launchType = null;
    private Project m_project = null;
    private InstructionSet m_currentInstructionSet = null;
    private Timer m_autoBackupTimer = new Timer(300000, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ErrorConsole.getConsole().SetSilenced(Exception.class);
            ((BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0)).ProcessControlContentToMapData();
            ErrorConsole.getConsole().SetSilenced(null);
            if(IsThereDifInProject())
            {
                m_lastProject = m_project.Cloned();
                CreateMapBackup();
            }
        }
    });
    
    public MainMenu GetMainMenu()
    {
        return m_mainMenu;
    }
    Project m_makeBackupProject = new Project(new MapData(new WalkthroughData(new WalkthroughOptions(), new WalkthroughStep(-1, -1))), new ProjectInformation());
    Project m_lastProject = null; //Set to m_makeBackupProject
    private Boolean IsThereDifInProject()
    {
        if(m_lastProject == m_makeBackupProject)
        {
            return true;
        }
        if(m_lastProject == null)
        {
            m_lastProject = m_project.Cloned();
            return false;
        }
        return !m_lastProject.equals(m_project);
    }
    private void CreateMapBackup()
    {
        File folder = new File(FolderFinder.getProgramDataFolder(),"Auto Backups");
        folder.mkdirs();
        ProgramSettings settings = ProgramSettings.LoadSettings();
        File[] fileArray = folder.listFiles();
        List<File> fileList = new ArrayList<File>(fileArray.length);
        for (int i = 0; i < fileArray.length; i++)
        {
            fileList.add(fileArray[i]);
        }
        Collections.sort(fileList, new Comparator<File>()
        {
            public int compare(File o1, File o2)
            {
                if(o1.lastModified() < o2.lastModified())
                    return -1;
                else if(o1.lastModified() > o2.lastModified())
                    return 1;
                else
                    return 0;
            }
        });
        int index = 0;
        for (File file : fileList)
        {
            if (settings.MaxAutoBackupSize < fileList.size() - index)
            {
                file.delete();
            }
            else
                break;
            index++;
        }
        if(settings.MaxAutoBackupSize > 0)
            Project.SaveProject(m_project, new File(folder, GetNextBackupName(folder)).getPath(), false);
    }
    private String GetNextBackupName(File folder)
    {
        int highest = -1;
        if (folder != null)
        {
            if (folder.list() != null && folder.list().length > 0)
            {
                for (File file : folder.listFiles())
                {
                    if (file.getName().startsWith("Map_Backup_"))
                    {
                        try
                        {
                            String name = file.getName();
                            int num = Integer.parseInt(name.substring(11, name.indexOf(".tmap")).trim());
                            if (num > highest)
                            {
                                highest = num;
                            }
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                }
            }
        }
        if (highest != -1)
            return "Map_Backup_" + Integer.toString(highest + 1) + ".tmap";
        else
            return "Map_Backup_1.tmap";
    }    
    KeyAdapter keyPressed = new KeyAdapter()
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            super.keyPressed(e);
            if (v_stepContentHolderPanel.getComponentCount() > 0)
            {
                ((BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0)).AlertKeyPressed(e);
            }
        }
    };
    public void AddKeyListenersToControlAnd4DownAndMainControls(Container c)
    {
        ArrayList<Component> list = Controls.FindAncestors4OrLessDeep(c);
        list.add(v_mapBaseSegmentButton);
        list.add(v_mapXmlSegmentButton);
        list.add(v_mapExtrasSegmentButton);
        list.add(v_mapFinalizationSegmentButton);
        list.add(v_mapReleaseSegmentButton);
        list.add(v_continueOrNextButton);
        for(Component cur : list)
        {
            cur.removeKeyListener(keyPressed);
            cur.addKeyListener(keyPressed);
        }
    }

    public boolean SetCurrentStep(WalkthroughStep step)
    {
        if (v_stepContentHolderPanel.getComponentCount() > 0)
        {
            BaseStepContentPanel oldPanel = (BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0);
            boolean moveAwayFromCurrentStep = oldPanel.WaitForPanelClose();
            if (moveAwayFromCurrentStep == false)
                return false;
        }

        m_lastProject = m_makeBackupProject;
        if (m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment() != step.GetSegment())
        {
            m_project.GetMapData().GetWalkthroughData().SetCurrentStep(step);
            UpdateSegmentControls();
        }
        else
        {
            m_project.GetMapData().GetWalkthroughData().SetCurrentStep(step);
        }
        ShowPanelForStep(step);
        UpdateNewStepInstructions();
        if(v_walkthroughStepsPanel.getComponentCount() > 0)
            ((BaseSegmentListPanel)v_walkthroughStepsPanel.getComponent(0)).UpdateControls();
        
        if (step.GetSegment() == 4 && step.GetStep() == 2)
        {
            v_continueOrNextButton.setText("Finished!");
            v_continueOrNextButton.setEnabled(false);
        }
        else
        {
            v_continueOrNextButton.setEnabled(true);
            if (!m_currentInstructionSet.AreThereInstructionsAtIndex(m_currentInstructionSet.GetNextInstructionsIndex()))
                v_continueOrNextButton.setText("Next Step");
            else
                v_continueOrNextButton.setText("Continue");
        }
        
        //If we navigate to the last step in a segment, enable the next segment button at the top
        if (!StepExists(IncrementedStep(m_project.GetMapData().GetWalkthroughData().GetCurrentStep())) && m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment() != 4)
            GetSegmentButton(m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment() + 1).setEnabled(true);

        return true;
    }
    private void ShowStepListPanelsForSegment(int segment)
    {
        if (v_walkthroughStepsPanel.getComponentCount() > 0)
        {
            v_walkthroughStepsPanel.remove(0);
        }

        BaseSegmentListPanel newPanel = GetStepListPanelForSegment(segment);
        newPanel.UpdateControls();
        AddKeyListenersToControlAnd4DownAndMainControls(newPanel);
        v_walkthroughStepsPanel.add(newPanel, new AbsoluteConstraints(new Point(0,0)));
        v_walkthroughStepsPanel.validate();        
    }
    public WalkthroughStep GetCurrentStep()
    {
        return m_project.GetMapData().GetWalkthroughData().GetCurrentStep();
    }
    public WalkthroughStep GetFarthestStep()
    {
        return m_project.GetMapData().GetWalkthroughData().GetFarthestStepReached();
    }
    private BaseSegmentListPanel GetStepListPanelForSegment(int segment)
    {
        switch(segment)
        {
            case 0:
            {
                return new Segment1StepListPanel(this);
            }
            case 1:
            {
                return new Segment2StepListPanel(this);
            }
            case 2:
            {
                return new Segment3StepListPanel(this);
            }
            case 3:
            {
                return new Segment4StepListPanel(this);
            }
            case 4:
            {
                return new Segment5StepListPanel(this);
            }
        }
        return null;
    }
    private void ShowPanelForStep(WalkthroughStep step)
    {
        if (v_stepContentHolderPanel.getComponentCount() > 0)
        {
            try
            {
                BaseStepContentPanel oldPanel = (BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0);
                oldPanel.ProcessControlContentToMapData();
                v_stepContentHolderPanel.remove(0);
            }
            catch (Exception ex)
            {
                ErrorConsole.getConsole().appendError(ex);
                int result = JOptionPane.showConfirmDialog(this, "An error has occured while trying to save the current step's data.\r\n"
                        + "\r\n"
                        + "Would you like to force navigation away from the current step. (The data you just entered may be lost)", "Force Navigation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                if (result == JOptionPane.YES_OPTION)
                    v_stepContentHolderPanel.remove(0);
                else
                    return;
            }
        }

        BaseStepContentPanel newPanel = GetStepContentPanelForStep(step);
        newPanel.SetWalkthroughWindow(this);
        newPanel.InitControls();
        AddKeyListenersToControlAnd4DownAndMainControls(newPanel);
        v_stepContentHolderPanel.add(newPanel);
        v_stepContentHolderPanel.validate();
    }
    public Project GetProject()
    {
        return m_project;
    }
    private BaseStepContentPanel GetStepContentPanelForStep(WalkthroughStep step)
    {
        BaseStepContentPanel result = null;
        String panelName = "";
        if(m_project.GetMapData().GetWalkthroughData().GetWalkthroughOptions().p_controlLevelOption == 0)
            panelName = "Segment" + (step.GetSegment() + 1) + "Step" + (step.GetStep() + 1) + "ContentPanel_Simplified";
        else if(m_project.GetMapData().GetWalkthroughData().GetWalkthroughOptions().p_controlLevelOption == 1)
            panelName = "Segment" + (step.GetSegment() + 1) + "Step" + (step.GetStep() + 1) + "ContentPanel_Normal";
        else
            panelName = "Segment" + (step.GetSegment() + 1) + "Step" + (step.GetStep() + 1) + "ContentPanel_Advanced";

        panelName = "MainProgram.Walkthrough.UI.StepContentPanels." + panelName;
        try
        {
            result = (BaseStepContentPanel)Class.forName(panelName).newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            try
            {
                panelName = "MainProgram.Walkthrough.UI.StepContentPanels." + "Segment" + (step.GetSegment() + 1) + "Step" + (step.GetStep() + 1) + "ContentPanel_All";
                result = (BaseStepContentPanel) Class.forName(panelName).newInstance();
            }
            catch (ClassNotFoundException ex2)
            {
                ErrorConsole.getConsole().appendError(ex2);
            }
            catch (InstantiationException ex2)
            {
                ErrorConsole.getConsole().appendError(ex2);
            }
            catch (IllegalAccessException ex2)
            {
                ErrorConsole.getConsole().appendError(ex2);
            }
        }
        catch (InstantiationException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
        catch (IllegalAccessException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
        return result;
    }
    public Boolean SendGMessage(Object obj)
    {
        if (obj instanceof String)
        {
            String s = obj.toString();
            if (s.equals("WOptionsUpdate"))
            {
                ShowPanelForStep(GetCurrentStep());
                return true;
            }
            else if (s.equals("ExportMapToFolder"))
            {
                //Export...
                return true;
            }
        }
        return false;
    }
    private void UpdateNewStepInstructions()
    {
        InstructionSet instructionSet = m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetInstructionSetForStep(m_project.GetMapData().GetWalkthroughData().GetWalkthroughOptions());
        if(instructionSet == null)
        {
            ErrorConsole.getConsole().append("There was an error displaying the map step instructions. Step: " + m_project.GetMapData().GetWalkthroughData().GetCurrentStep());
            instructionSet = WalkthroughStep.s_instructionsFailedToLoadMessage;
            m_project.GetMapData().GetWalkthroughData().SetNextInstructionsIndex(1);
        }
        v_instructionsTextBox.setText(instructionSet.ReadNextInstructionPortion());
        m_project.GetMapData().GetWalkthroughData().SetNextInstructionsIndex(instructionSet.GetNextInstructionsIndex());

        m_currentInstructionSet = instructionSet;
        getScrollTask().execute();
    }
    private void InitLoadedMapStepInstructions()
    {
        InstructionSet instructionSet = m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetInstructionSetForStep(m_project.GetMapData().GetWalkthroughData().GetWalkthroughOptions());
        if(instructionSet == null)
        {
            ErrorConsole.getConsole().append("There was an error displaying the map step instructions. Step: " + m_project.GetMapData().GetWalkthroughData().GetCurrentStep());
            instructionSet = WalkthroughStep.s_instructionsFailedToLoadMessage;
            m_project.GetMapData().GetWalkthroughData().SetNextInstructionsIndex(1);
        }
        instructionSet.SetNextInstructionsIndex(m_project.GetMapData().GetWalkthroughData().GetNextInstructionsIndex() - 1);
        v_instructionsTextBox.setText(instructionSet.ReadNextInstructionPortion());
        if (GetCurrentStep().GetSegment() == 4 && GetCurrentStep().GetStep() == 2)
        {
            v_continueOrNextButton.setText("Finished!");
            v_continueOrNextButton.setEnabled(false);
        }
        else
        {
            v_continueOrNextButton.setEnabled(true);
            if (!instructionSet.AreThereInstructionsAtIndex(m_project.GetMapData().GetWalkthroughData().GetNextInstructionsIndex()))
                v_continueOrNextButton.setText("Next Step");
        }

        m_currentInstructionSet = instructionSet;
        getScrollTask().execute();
    }
    public Task getScrollTask()
    {
        return new ScrollTask(org.jdesktop.application.Application.getInstance());
    }
    private class ScrollTask extends org.jdesktop.application.Task<Object, Void> {
        ScrollTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AsdfsafTask fields, here.
            super(app);
        }
        @Override
        protected Object doInBackground()
        {
            return null;
        }
        @Override protected void succeeded(Object result)
        {
            v_instructionsScrollPane.getViewport().setViewPosition(new Point(0,0));
        }
    }
    public void UpdateSegmentControls()
    {
        int fSeg = m_project.GetMapData().GetWalkthroughData().GetFarthestStepReached().GetSegment();
        int curSeg = m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment();
        v_mapBaseSegmentButton.setEnabled(fSeg > -1);
        v_mapXmlSegmentButton.setEnabled(fSeg > 0);
        v_mapExtrasSegmentButton.setEnabled(fSeg > 1);
        v_mapFinalizationSegmentButton.setEnabled(fSeg > 2);
        v_mapReleaseSegmentButton.setEnabled(fSeg > 3);

        v_mapBaseSegmentButton.setBackground(GetConstantColorFromSegmentIndex(0, curSeg, fSeg));
        v_mapXmlSegmentButton.setBackground(GetConstantColorFromSegmentIndex(1, curSeg, fSeg));
        v_mapExtrasSegmentButton.setBackground(GetConstantColorFromSegmentIndex(2, curSeg, fSeg));
        v_mapFinalizationSegmentButton.setBackground(GetConstantColorFromSegmentIndex(3, curSeg, fSeg));
        v_mapReleaseSegmentButton.setBackground(GetConstantColorFromSegmentIndex(4, curSeg, fSeg));
    }

    private Color GetConstantColorFromSegmentIndex(int buttonIndex, int segmentIndex, int far)
    {
        if(segmentIndex == buttonIndex)
            return Constants.HighlightedSegmentBackColor;
        else if(far <= buttonIndex)
            return Constants.DefaultButtonBackColor;
        else
            return Constants.FinishedSegmentBackColor;
    }

    private void MakeWalkthroughSegmentListPanelSmallSize()
    {
        v_mapBaseSegmentButton.setLocation(new Point((int)(30 * .75), 7));
        v_mapBaseSegmentButton.setSize((int)(150 * .85), 30);

        v_mapXmlSegmentButton.setLocation(new Point((int)(210 * .75), 7));
        v_mapXmlSegmentButton.setSize((int)(150 * .85), 30);

        v_mapExtrasSegmentButton.setLocation(new Point((int)(390 * .75), 7));
        v_mapExtrasSegmentButton.setSize((int)(150 * .85), 30);

        v_mapFinalizationSegmentButton.setLocation(new Point((int)(570 * .75), 7));
        v_mapFinalizationSegmentButton.setSize((int)(150 * .85), 30);

        v_mapReleaseSegmentButton.setLocation(new Point((int)(750 * .75), 7));
        v_mapReleaseSegmentButton.setSize((int)(150 * .85), 30);
    }
    private void MakeWalkthroughSegmentListPanelLargeSize()
    {
        v_mapBaseSegmentButton.setLocation(new Point(30, 7));
        v_mapBaseSegmentButton.setSize(150, 30);

        v_mapXmlSegmentButton.setLocation(new Point(210, 7));
        v_mapXmlSegmentButton.setSize(150, 30);

        v_mapExtrasSegmentButton.setLocation(new Point(390, 7));
        v_mapExtrasSegmentButton.setSize(150, 30);

        v_mapFinalizationSegmentButton.setLocation(new Point(570, 7));
        v_mapFinalizationSegmentButton.setSize(150, 30);

        v_mapReleaseSegmentButton.setLocation(new Point(750, 7));
        v_mapReleaseSegmentButton.setSize(150, 30);
    }
    private boolean m_isSegListSmall = false;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        v_walkthroughSegmentListPanel = new javax.swing.JPanel();
        v_mapBaseSegmentButton = new javax.swing.JButton();
        v_mapFinalizationSegmentButton = new javax.swing.JButton();
        v_mapReleaseSegmentButton = new javax.swing.JButton();
        v_mapXmlSegmentButton = new javax.swing.JButton();
        v_mapExtrasSegmentButton = new javax.swing.JButton();
        v_walkthroughStepsPanel = new javax.swing.JPanel();
        v_controlAndContentPanelHolder = new javax.swing.JPanel();
        v_instructionsAndContinueHolderPanel = new javax.swing.JPanel();
        v_continueOrNextButton = new javax.swing.JButton();
        v_instructionsPanel = new javax.swing.JPanel();
        v_instructionsScrollPane = new javax.swing.JScrollPane();
        v_instructionsTextBox = new javax.swing.JTextArea();
        v_stepContentHolderPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(WalkthroughWindow.class);
        setTitle(resourceMap.getString("WalkthroughMainPanel.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(750, 500));
        setName("WalkthroughMainPanel"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                WindowShown(evt);
            }
        });

        v_walkthroughSegmentListPanel.setName("v_walkthroughSegmentListPanel"); // NOI18N
        v_walkthroughSegmentListPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                WakthroughSegmentListResize(evt);
            }
        });
        v_walkthroughSegmentListPanel.setLayout(null);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(WalkthroughWindow.class, this);
        v_mapBaseSegmentButton.setAction(actionMap.get("NavigateToMapBaseSegment")); // NOI18N
        v_mapBaseSegmentButton.setText(resourceMap.getString("v_mapBaseSegmentButton.text")); // NOI18N
        v_mapBaseSegmentButton.setMinimumSize(null);
        v_mapBaseSegmentButton.setName("v_mapBaseSegmentButton"); // NOI18N
        v_mapBaseSegmentButton.setNextFocusableComponent(v_mapXmlSegmentButton);
        v_walkthroughSegmentListPanel.add(v_mapBaseSegmentButton);
        v_mapBaseSegmentButton.setBounds(30, 7, 150, 30);

        v_mapFinalizationSegmentButton.setAction(actionMap.get("NavigateToMapFinalizationSegment")); // NOI18N
        v_mapFinalizationSegmentButton.setText(resourceMap.getString("v_mapFinalizationSegmentButton.text")); // NOI18N
        v_mapFinalizationSegmentButton.setMinimumSize(null);
        v_mapFinalizationSegmentButton.setName("v_mapFinalizationSegmentButton"); // NOI18N
        v_mapFinalizationSegmentButton.setNextFocusableComponent(v_mapReleaseSegmentButton);
        v_walkthroughSegmentListPanel.add(v_mapFinalizationSegmentButton);
        v_mapFinalizationSegmentButton.setBounds(570, 7, 150, 30);

        v_mapReleaseSegmentButton.setAction(actionMap.get("NavigateToMapReleaseSegment")); // NOI18N
        v_mapReleaseSegmentButton.setText(resourceMap.getString("v_mapReleaseSegmentButton.text")); // NOI18N
        v_mapReleaseSegmentButton.setMinimumSize(null);
        v_mapReleaseSegmentButton.setName("v_mapReleaseSegmentButton"); // NOI18N
        v_walkthroughSegmentListPanel.add(v_mapReleaseSegmentButton);
        v_mapReleaseSegmentButton.setBounds(750, 7, 150, 30);

        v_mapXmlSegmentButton.setAction(actionMap.get("NavigateToMapXmlSegment")); // NOI18N
        v_mapXmlSegmentButton.setText(resourceMap.getString("v_mapXmlSegmentButton.text")); // NOI18N
        v_mapXmlSegmentButton.setMinimumSize(null);
        v_mapXmlSegmentButton.setName("v_mapXmlSegmentButton"); // NOI18N
        v_mapXmlSegmentButton.setNextFocusableComponent(v_mapExtrasSegmentButton);
        v_walkthroughSegmentListPanel.add(v_mapXmlSegmentButton);
        v_mapXmlSegmentButton.setBounds(210, 7, 150, 30);

        v_mapExtrasSegmentButton.setAction(actionMap.get("NavigateToMapExtrasSegment")); // NOI18N
        v_mapExtrasSegmentButton.setText(resourceMap.getString("v_mapExtrasSegmentButton.text")); // NOI18N
        v_mapExtrasSegmentButton.setMinimumSize(null);
        v_mapExtrasSegmentButton.setName("v_mapExtrasSegmentButton"); // NOI18N
        v_mapExtrasSegmentButton.setNextFocusableComponent(v_mapFinalizationSegmentButton);
        v_walkthroughSegmentListPanel.add(v_mapExtrasSegmentButton);
        v_mapExtrasSegmentButton.setBounds(390, 7, 150, 30);

        v_walkthroughStepsPanel.setAutoscrolls(true);
        v_walkthroughStepsPanel.setName("v_walkthroughStepsPanel"); // NOI18N
        v_walkthroughStepsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        v_controlAndContentPanelHolder.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        v_controlAndContentPanelHolder.setName("v_controlAndContentPanelHolder"); // NOI18N
        v_controlAndContentPanelHolder.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                v_controlAndContentPanelHolderComponentResized(evt);
            }
        });

        v_instructionsAndContinueHolderPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        v_instructionsAndContinueHolderPanel.setName("v_instructionsAndContinueHolderPanel"); // NOI18N
        v_instructionsAndContinueHolderPanel.setPreferredSize(new java.awt.Dimension(733, 115));

        v_continueOrNextButton.setAction(actionMap.get("ContinueButtonClick")); // NOI18N
        v_continueOrNextButton.setText(resourceMap.getString("v_continueOrNextButton.text")); // NOI18N
        v_continueOrNextButton.setName("v_continueOrNextButton"); // NOI18N

        v_instructionsPanel.setAlignmentX(0.0F);
        v_instructionsPanel.setName("v_instructionsPanel"); // NOI18N

        v_instructionsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        v_instructionsScrollPane.setName("v_instructionsScrollPane"); // NOI18N

        v_instructionsTextBox.setColumns(20);
        v_instructionsTextBox.setEditable(false);
        v_instructionsTextBox.setFont(resourceMap.getFont("v_instructionsTextBox.font")); // NOI18N
        v_instructionsTextBox.setLineWrap(true);
        v_instructionsTextBox.setRows(4);
        v_instructionsTextBox.setText(resourceMap.getString("v_instructionsTextBox.text")); // NOI18N
        v_instructionsTextBox.setWrapStyleWord(true);
        v_instructionsTextBox.setFocusable(false);
        v_instructionsTextBox.setName("v_instructionsTextBox"); // NOI18N
        v_instructionsScrollPane.setViewportView(v_instructionsTextBox);

        javax.swing.GroupLayout v_instructionsPanelLayout = new javax.swing.GroupLayout(v_instructionsPanel);
        v_instructionsPanel.setLayout(v_instructionsPanelLayout);
        v_instructionsPanelLayout.setHorizontalGroup(
            v_instructionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_instructionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );
        v_instructionsPanelLayout.setVerticalGroup(
            v_instructionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_instructionsPanelLayout.createSequentialGroup()
                .addComponent(v_instructionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout v_instructionsAndContinueHolderPanelLayout = new javax.swing.GroupLayout(v_instructionsAndContinueHolderPanel);
        v_instructionsAndContinueHolderPanel.setLayout(v_instructionsAndContinueHolderPanelLayout);
        v_instructionsAndContinueHolderPanelLayout.setHorizontalGroup(
            v_instructionsAndContinueHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, v_instructionsAndContinueHolderPanelLayout.createSequentialGroup()
                .addComponent(v_instructionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_continueOrNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        v_instructionsAndContinueHolderPanelLayout.setVerticalGroup(
            v_instructionsAndContinueHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_continueOrNextButton, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
            .addComponent(v_instructionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        v_stepContentHolderPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        v_stepContentHolderPanel.setName("v_stepContentHolderPanel"); // NOI18N
        v_stepContentHolderPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout v_controlAndContentPanelHolderLayout = new javax.swing.GroupLayout(v_controlAndContentPanelHolder);
        v_controlAndContentPanelHolder.setLayout(v_controlAndContentPanelHolderLayout);
        v_controlAndContentPanelHolderLayout.setHorizontalGroup(
            v_controlAndContentPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(v_stepContentHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
            .addComponent(v_instructionsAndContinueHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
        );
        v_controlAndContentPanelHolderLayout.setVerticalGroup(
            v_controlAndContentPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_controlAndContentPanelHolderLayout.createSequentialGroup()
                .addComponent(v_instructionsAndContinueHolderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_stepContentHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 178, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenuBar1.setName("v_menuBar1"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem5.setAction(actionMap.get("SaveProject")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenu1.add(jMenuItem5);

        jMenuItem9.setAction(actionMap.get("SaveProjectAs")); // NOI18N
        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu1.add(jMenuItem9);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu1.add(jSeparator1);

        jMenuItem10.setAction(actionMap.get("GoToMainMenu")); // NOI18N
        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu1.add(jMenuItem10);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jMenu1.add(jSeparator2);

        jMenuItem6.setAction(actionMap.get("ExitWalkthroughWindow")); // NOI18N
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        jMenu5.setText(resourceMap.getString("jMenu5.text")); // NOI18N
        jMenu5.setName("jMenu5"); // NOI18N

        jMenuItem7.setAction(actionMap.get("ExportMapToFolder")); // NOI18N
        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setToolTipText(resourceMap.getString("jMenuItem7.toolTipText")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenu5.add(jMenuItem7);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jMenu5.add(jSeparator9);

        jMenuItem23.setAction(actionMap.get("ExportMapBaseToFolder")); // NOI18N
        jMenuItem23.setText(resourceMap.getString("jMenuItem23.text")); // NOI18N
        jMenuItem23.setToolTipText(resourceMap.getString("jMenuItem23.toolTipText")); // NOI18N
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        jMenu5.add(jMenuItem23);

        jMenuItem8.setAction(actionMap.get("ExportXmlToFile")); // NOI18N
        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setToolTipText(resourceMap.getString("jMenuItem8.toolTipText")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu5.add(jMenuItem8);

        jMenuBar1.add(jMenu5);

        jMenu4.setText(resourceMap.getString("jMenu4.text")); // NOI18N
        jMenu4.setName("jMenu4"); // NOI18N

        jMenuItem3.setAction(actionMap.get("ShowXMLAnalyzer")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setToolTipText(resourceMap.getString("jMenuItem3.toolTipText")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu4.add(jMenuItem3);

        jMenuBar1.add(jMenu4);

        jMenu2.setAction(actionMap.get("OpenWalkthroughOptions")); // NOI18N
        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem4.setAction(actionMap.get("OpenWalkthroughOptions")); // NOI18N
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu2.add(jMenuItem4);

        jMenuItem16.setAction(actionMap.get("OpenProgramSettings")); // NOI18N
        jMenuItem16.setText(resourceMap.getString("jMenuItem16.text")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenu2.add(jMenuItem16);

        jMenuBar1.add(jMenu2);

        jMenu7.setText(resourceMap.getString("jMenu7.text")); // NOI18N
        jMenu7.setName("jMenu7"); // NOI18N

        jCheckBoxMenuItem1.setText(resourceMap.getString("jCheckBoxMenuItem1.text")); // NOI18N
        jCheckBoxMenuItem1.setName("jCheckBoxMenuItem1"); // NOI18N
        jMenu7.add(jCheckBoxMenuItem1);

        jMenuItem18.setText(resourceMap.getString("jMenuItem18.text")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        jMenu7.add(jMenuItem18);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jMenu7.add(jSeparator7);

        jMenuItem21.setText(resourceMap.getString("jMenuItem21.text")); // NOI18N
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        jMenu7.add(jMenuItem21);

        jMenuItem20.setText(resourceMap.getString("jMenuItem20.text")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        jMenu7.add(jMenuItem20);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jMenu7.add(jSeparator3);

        jMenuItem17.setAction(actionMap.get("ShowConsole")); // NOI18N
        jMenuItem17.setText(resourceMap.getString("jMenuItem17.text")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenu7.add(jMenuItem17);

        jMenuBar1.add(jMenu7);

        jMenu6.setText(resourceMap.getString("jMenu6.text")); // NOI18N
        jMenu6.setName("jMenu6"); // NOI18N

        jMenuItem11.setAction(actionMap.get("ReportBug")); // NOI18N
        jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenu6.add(jMenuItem11);

        jMenuItem12.setAction(actionMap.get("SuggestFeature")); // NOI18N
        jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenu6.add(jMenuItem12);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jMenu6.add(jSeparator4);

        jMenuItem13.setText(resourceMap.getString("jMenuItem13.text")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenu6.add(jMenuItem13);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jMenu6.add(jSeparator5);

        jMenuItem15.setAction(actionMap.get("ViewChangelog")); // NOI18N
        jMenuItem15.setText(resourceMap.getString("jMenuItem15.text")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenu6.add(jMenuItem15);

        jMenuItem14.setText(resourceMap.getString("jMenuItem14.text")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenu6.add(jMenuItem14);

        jMenuBar1.add(jMenu6);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem19.setAction(actionMap.get("RequestHelpOnline")); // NOI18N
        jMenuItem19.setText(resourceMap.getString("jMenuItem19.text")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenu3.add(jMenuItem19);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jMenu3.add(jSeparator6);

        jMenuItem22.setAction(actionMap.get("CheckForUpdates")); // NOI18N
        jMenuItem22.setText(resourceMap.getString("jMenuItem22.text")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        jMenu3.add(jMenuItem22);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jMenu3.add(jSeparator8);

        jMenuItem1.setAction(actionMap.get("ShowHelpContents")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu3.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("ShowAboutBox")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(v_walkthroughStepsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_controlAndContentPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(v_walkthroughSegmentListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(v_walkthroughSegmentListPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_walkthroughStepsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(v_controlAndContentPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-949)/2, (screenSize.height-557)/2, 949, 557);
    }// </editor-fold>//GEN-END:initComponents

    private void WindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_WindowClosing
    {//GEN-HEADEREND:event_WindowClosing
        if(JOptionPane.showConfirmDialog(rootPane, "All unsaved work will be lost if you exit. Are you sure you want to exit?","Confirm Exit",0) == 0)
        {
            Application.getInstance().exit();
        }
    }//GEN-LAST:event_WindowClosing

    private void WakthroughSegmentListResize(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_WakthroughSegmentListResize
    {//GEN-HEADEREND:event_WakthroughSegmentListResize
        if (v_walkthroughSegmentListPanel.getSize().width < 930)
        {
            if (!m_isSegListSmall)
            {
                MakeWalkthroughSegmentListPanelSmallSize();
                m_isSegListSmall = true;
            }
        }
        else
        {
            if (m_isSegListSmall)
            {
                MakeWalkthroughSegmentListPanelLargeSize();
                m_isSegListSmall = false;
            }
        }
    }//GEN-LAST:event_WakthroughSegmentListResize

    private void WindowShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_WindowShown
    {//GEN-HEADEREND:event_WindowShown
        getValidateComponentsTask().execute();
    }//GEN-LAST:event_WindowShown

    private void v_controlAndContentPanelHolderComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_v_controlAndContentPanelHolderComponentResized
    {//GEN-HEADEREND:event_v_controlAndContentPanelHolderComponentResized
        if(v_controlAndContentPanelHolder.getSize() != m_lastOptimalContentPanelSize)
        {
            v_controlAndContentPanelHolder.setSize(v_controlAndContentPanelHolder.getWidth() + 8, v_controlAndContentPanelHolder.getHeight() + 8);
            m_lastOptimalContentPanelSize = new Dimension(v_controlAndContentPanelHolder.getWidth() + 8, v_controlAndContentPanelHolder.getHeight() + 8);
        }
    }//GEN-LAST:event_v_controlAndContentPanelHolderComponentResized
    Dimension m_lastOptimalContentPanelSize = null;
    @Action
    public Task getValidateComponentsTask()
    {
        return new ValidateTask(org.jdesktop.application.Application.getInstance());
    }
    private class ValidateTask extends org.jdesktop.application.Task<Object, Void> {
        ValidateTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AsdfsafTask fields, here.
            super(app);
        }
        @Override
        protected Object doInBackground()
        {
            return null;
        }
        @Override protected void succeeded(Object result)
        {
            ShowPanelForStep(WalkthroughWindow.this.m_project.GetMapData().GetWalkthroughData().GetCurrentStep());
            ShowStepListPanelsForSegment(WalkthroughWindow.this.m_project.GetMapData().GetWalkthroughData().GetCurrentStep().GetSegment());
        }
    }


    @Action
    public void ExitWalkthroughWindow()
    {
        if(JOptionPane.showConfirmDialog(rootPane, "All unsaved work will be lost if you exit. Are you sure you want to exit?","Confirm Exit",0) == 0)
        {
            Application.getInstance().exit();
        }
    }

    @Action
    public void GoToMainMenu()
    {
        if(JOptionPane.showConfirmDialog(rootPane, "All unsaved work will be lost if you return to the main menu. Are you sure you want to leave?","Confirm Leave",0) == 0)
        {
            this.setVisible(false);
            m_mainMenu.setVisible(true);
            this.dispose();
        }
    }

    @Action
    public void ShowAboutBox()
    {
        ProgramAboutBox aboutBox = new ProgramAboutBox(this);
        aboutBox.setLocationRelativeTo(this);
        aboutBox.setVisible(true);
    }

    @Action
    public void ShowHelpContents() //TODO
    {

    }

    @Action
    public void OpenWalkthroughOptions()
    {
        WalkthroughOptionsWindow_MapInProgress window= new WalkthroughOptionsWindow_MapInProgress(this);
        window.setLocationRelativeTo(this);
        window.setVisible(true);
    }

    @Action
    public void NavigateToMapBaseSegment()
    {        
        if(SetCurrentStep(new WalkthroughStep(0, 0)))
            ShowStepListPanelsForSegment(0);
    }

    @Action
    public void NavigateToMapXmlSegment()
    {
        if(SetCurrentStep(new WalkthroughStep(1, 0)))
            ShowStepListPanelsForSegment(1);
    }

    @Action
    public void NavigateToMapExtrasSegment()
    {
        if(SetCurrentStep(new WalkthroughStep(2, 0)))
            ShowStepListPanelsForSegment(2);
    }

    @Action
    public void NavigateToMapFinalizationSegment()
    {
        if(SetCurrentStep(new WalkthroughStep(3, 0)))
            ShowStepListPanelsForSegment(3);
    }

    @Action
    public void NavigateToMapReleaseSegment()
    {
        if(SetCurrentStep(new WalkthroughStep(4, 0)))
            ShowStepListPanelsForSegment(4);
    }
    private Boolean StepExists(WalkthroughStep step)
    {
        ErrorConsole.getConsole().SetSilenced(Exception.class);
        Boolean result = (GetStepContentPanelForStep(step) != null);
        ErrorConsole.getConsole().SetSilenced(null);
        return result;
    }
    @Action
    public void ContinueButtonClick()
    {
        String instructions = m_currentInstructionSet.ReadNextInstructionPortion();
        if(instructions == null)
        {
            WalkthroughStep newStep = IncrementedStep(m_project.GetMapData().GetWalkthroughData().GetCurrentStep());
            if (StepExists(newStep))
            {
                if(SetCurrentStep(newStep) == false)
                    return;
            }
            else
            {
                if(SetCurrentStep(new WalkthroughStep(newStep.GetSegment() + 1, 0)) == false)
                    return;
                ShowStepListPanelsForSegment(newStep.GetSegment() + 1);
            }
        }
        else
        {
            m_project.GetMapData().GetWalkthroughData().SetNextInstructionsIndex(m_currentInstructionSet.GetNextInstructionsIndex());
            v_instructionsTextBox.setText(instructions);
            getScrollTask().execute();
        }
    }
    private JButton GetSegmentButton(int segment)
    {
        switch (segment)
        {
            case 0:
            {
                return v_mapBaseSegmentButton;
            }
            case 1:
            {
                return v_mapXmlSegmentButton;
            }
            case 2:
            {
                return v_mapExtrasSegmentButton;
            }
            case 3:
            {
                return v_mapFinalizationSegmentButton;
            }
            case 4:
            {
                return v_mapReleaseSegmentButton;
            }
        }
        return null;
    }
    public WalkthroughStep IncrementedStep(WalkthroughStep step)
    {
        return new WalkthroughStep(step.GetSegment(), step.GetStep() + 1);
    }

    @Action
    public void SaveProject()
    {
        ((BaseStepContentPanel)v_stepContentHolderPanel.getComponent(0)).ProcessControlContentToMapData();
        if(m_project.GetProjectInfo().SaveLocation.equals("[not set]"))
        {
            SaveProjectAs();
        }
        else
        {
            Project.SaveProject(m_project, m_project.GetProjectInfo().SaveLocation, true);
            JOptionPane.showMessageDialog(this, "Project saved to file!", "Project Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Action
    public void SaveProjectAs()
    {
        try
        {
            ((BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0)).ProcessControlContentToMapData();
        }
        catch (Exception ex)
        {
            //A data saving error should not stop the player from saving his work!
            ErrorConsole.getConsole().appendError(ex);
        }
        FileSave saver = new FileSave(this, "Select a save location", ".tmap", "TripleA Map Projects");
        String path = saver.getFilePath();
        if (path == null)
        {
            return;
        }

        if(!path.endsWith(".tmap"))
            path = path + ".tmap";

        path = new File(path).getAbsolutePath();

        Project.SaveProject(m_project, path, true);
        JOptionPane.showMessageDialog(this, "Project saved to file!", "Project Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void OpenProgramSettings()
    {
        ProgramSettingsWindow sw = new ProgramSettingsWindow(this);
        sw.setLocationRelativeTo(this);
        sw.setVisible(true);
    }

    @Action
    public void ShowConsole()
    {
        ErrorConsole.getConsole().setVisible(true);
    }

    @Action
    public void CheckForUpdates()
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
                try
                {
                    URL url = new URL("http://code.google.com/p/tripleamapcreator/wiki/Program_Update_Information");
                    URLConnection urlC = url.openConnection();
                    // Copy resource to local file, use remote file
                    // if no local file name specified
                    InputStream is = urlC.getInputStream();                    

                    int oneChar;
                    StringBuilder builder = new StringBuilder();

                    while ((oneChar = is.read()) != -1)
                    {
                        builder.appendCodePoint(oneChar);
                    }
                    is.close();
                    
                    String updateInfoSection = builder.toString().split("-~=")[1].trim().replace("&#x27;", "'").replace("<p>", "").replace("</p>", "");
                    String[] lines = updateInfoSection.split("-eol!");
                    
                    String reconstructedInfoSection = "";
                    for(String line : lines)
                    {
                        if(reconstructedInfoSection.length() == 0)
                            reconstructedInfoSection = "    " + line;
                        else
                            reconstructedInfoSection = reconstructedInfoSection + "\r\n" + "    " + line;
                    }
                    
                    Properties updateProps = new Properties();
                    updateProps.load(new StringReader(reconstructedInfoSection));
                    
                    Version latestVersion = new Version(updateProps.getProperty("LatestVersion"));
                    if (latestVersion.compareTo(Constants.ProgramVersion) > 0)
                    {
                        String niceLookingVInfo = reconstructedInfoSection.replace("LatestVersion", "Latest Version").replace("LatestStableVersion", "Latest Stable Version");
                        String message = "An update to the map creator has been found. Here's the latest versioning information:\r\n\r\n" + niceLookingVInfo + "\r\n\r\nDo you want to go to the download page?";
                        int result = JOptionPane.showConfirmDialog(WalkthroughWindow.this, message, "Updates Available", JOptionPane.YES_NO_CANCEL_OPTION);

                        if (result == JOptionPane.YES_OPTION)
                            BrowserLauncher.openURL("http://code.google.com/p/tripleamapcreator/downloads/list");
                    }
                    else
                        JOptionPane.showMessageDialog(WalkthroughWindow.this, "Your version is up to date: " + Constants.ProgramVersion.toString(), "No Updates Available", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception ex)
                {                                      
                    JOptionPane.showMessageDialog(WalkthroughWindow.this, "Unable to check for updates: " + ex.toString(), "Update Check Failed", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    @Action
    public void ShowXMLAnalyzer()
    {
        try{((BaseStepContentPanel) v_stepContentHolderPanel.getComponent(0)).ProcessControlContentToMapData();}
        catch (Exception ex){/*It's okay, the user is probably just in the middle of something*/}
        
        final StringBuilder xmlBuilder = new StringBuilder();
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                MapDataExporter exporter = new MapDataExporter();
                xmlBuilder.append(exporter.GenerateMapXML(GetProject().GetMapData()));
            }
        };
        if(!BackgroundTaskRunner.runInBackground(WalkthroughWindow.this, "Generating map xml...", runner, true))
            return;

        final JDialog dialog = new JDialog(WalkthroughWindow.this);
        dialog.setIconImage(ProgramLauncher.getProgramIcon(dialog));
        dialog.setTitle("XML Analyzer");
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        
        final JScrollPane scroll = new JScrollPane();
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(xmlBuilder.toString());        
        scroll.setViewportView(textArea);
        
        JPanel buttons = new JPanel();
        final JButton button = new JButton(new AbstractAction("OK")
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
        });
        buttons.add(button);        
        
        dialog.add(scroll, BorderLayout.CENTER);        
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(button);
        dialog.pack();
        dialog.setSize(750, 500);
        dialog.setLocationRelativeTo(WalkthroughWindow.this);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
                scroll.getVerticalScrollBar().getModel().setValue(0);
                scroll.getHorizontalScrollBar().getModel().setValue(0);
                button.requestFocus();
            }
        });
        
        dialog.setVisible(true);
        //The thread pauses here until dialog is closed
        dialog.dispose();
    }

    @Action
    public void ExportMapToFolder()
    {
        JFileChooser chooser = new JFileChooser();        
        chooser.setDialogTitle("Select Map Export Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(MapDataExporter.GetLastExportFolder());
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return;
        
        File folder = null;
        try
        {
            folder = chooser.getSelectedFile(); //Get the folder
            if (folder.exists())
                MapDataExporter.SetLastExportFolder(folder);
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }        
        
        //Don't delete folder, because we don't know if the folder is important, like the desktop!
        /*try
        {
            if (file.exists())
                file.delete();
        }
        catch(Exception ex)
        {
        }*/
        MapDataExporter exporter = new MapDataExporter();
        exporter.ExportMapToFolder(GetProject().GetMapData(), folder, true, this);
        JOptionPane.showMessageDialog(this, "Map exported to folder!", "Map Exported", JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void ExportMapBaseToFolder()
    {
        JFileChooser chooser = new JFileChooser();        
        chooser.setDialogTitle("Select Map Base Export Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(MapDataExporter.GetLastExportFolder());
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return;
        
        File folder = null;
        try
        {
            folder = chooser.getSelectedFile(); //Get the folder
            if (folder.exists())
                MapDataExporter.SetLastExportFolder(folder);
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }        
        
        //Don't delete folder, because we don't know if the folder is important, like the desktop!
        /*try
        {
            if (file.exists())
                file.delete();
        }
        catch(Exception ex)
        {
        }*/
        MapDataExporter exporter = new MapDataExporter();
        exporter.ExportMapBaseToFolder(GetProject().GetMapData(), folder, true, this);
        JOptionPane.showMessageDialog(this, "Map base exported to folder!", "Map Base Exported", JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void ExportXmlToFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Xml Save Location");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(MapDataExporter.GetLastExportFolder());
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return;
        
        File file = null;
        try
        {
            file = chooser.getSelectedFile(); //Get the file
            if (file.exists())
                MapDataExporter.SetLastExportFolder(file.getParentFile());
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
        }
        
        MapDataExporter exporter = new MapDataExporter();
        exporter.ExportMapXMLToFile(GetProject().GetMapData(), file, true, this);
        JOptionPane.showMessageDialog(this, "Map xml exported to file!", "Map Xml Exported", JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void RequestHelpOnline()
    {
        BrowserLauncher.openURL("http://sites.google.com/site/tripleamapcreator/forums");
    }

    @Action
    public void ReportBug()
    {
        BrowserLauncher.openURL("http://sites.google.com/site/tripleamapcreator/forums");
    }

    @Action
    public void SuggestFeature()
    {
        BrowserLauncher.openURL("http://sites.google.com/site/tripleamapcreator/forums");
    }

    @Action
    public void ViewChangelog()
    {
        StringBuilder changelog = new StringBuilder();
        
        try
        {
            File changelogFile = FileFinder.FindChangelogFile();
            FileReader reader = new FileReader(changelogFile);
            BufferedReader bReader = new BufferedReader(reader);

            while (true)
            {
                String line = bReader.readLine();
                if (line == null)
                    break;
                changelog.append(line).append("\r\n");
            }
            
            reader.close();
            bReader.close();
        }
        catch(Exception ex)
        {
            changelog.append("[Error loading changelog: ").append(ex.toString()).append("]");
        }

        final JDialog dialog = new JDialog(WalkthroughWindow.this);
        dialog.setIconImage(ProgramLauncher.getProgramIcon(dialog));
        dialog.setTitle("Program Changelog");
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        
        final JScrollPane scroll = new JScrollPane();
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(changelog.toString());        
        scroll.setViewportView(textArea);
        
        JPanel buttons = new JPanel();
        final JButton button = new JButton(new AbstractAction("OK")
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
        });
        buttons.add(button);        
        
        dialog.add(scroll, BorderLayout.CENTER);        
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(button);
        dialog.pack();
        dialog.setSize(750, 500);
        dialog.setLocationRelativeTo(WalkthroughWindow.this);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
                scroll.getVerticalScrollBar().getModel().setValue(0);
                scroll.getHorizontalScrollBar().getModel().setValue(0);
                button.requestFocus();
            }
        });
        
        dialog.setVisible(true);
        //The thread pauses here until dialog is closed
        dialog.dispose();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JButton v_continueOrNextButton;
    private javax.swing.JPanel v_controlAndContentPanelHolder;
    private javax.swing.JPanel v_instructionsAndContinueHolderPanel;
    private javax.swing.JPanel v_instructionsPanel;
    private javax.swing.JScrollPane v_instructionsScrollPane;
    private javax.swing.JTextArea v_instructionsTextBox;
    private javax.swing.JButton v_mapBaseSegmentButton;
    private javax.swing.JButton v_mapExtrasSegmentButton;
    private javax.swing.JButton v_mapFinalizationSegmentButton;
    private javax.swing.JButton v_mapReleaseSegmentButton;
    private javax.swing.JButton v_mapXmlSegmentButton;
    private javax.swing.JPanel v_stepContentHolderPanel;
    private javax.swing.JPanel v_walkthroughSegmentListPanel;
    public javax.swing.JPanel v_walkthroughStepsPanel;
    // End of variables declaration//GEN-END:variables

}
