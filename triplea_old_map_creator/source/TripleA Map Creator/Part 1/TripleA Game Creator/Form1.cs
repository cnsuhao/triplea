using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Reflection;
using System.Net;
using System.Threading;
using System.Diagnostics;

namespace TripleA_Game_Creator
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();            
            oldSize = Size;
            foreach (Control cur in this.Controls)
            {
                cur.Tag = cur.Bounds;
                if (cur.Controls.Count > 0)
                {
                    foreach (Control cur2 in cur.Controls)
                    {
                        cur2.Tag = cur2.Bounds;
                        if (cur2.Controls.Count > 0)
                        {
                            foreach (Control cur3 in cur2.Controls)
                            {
                                cur3.Tag = cur3.Bounds;
                            }
                        }
                    }
                }
            }
            RefreshSettings();
            CheckForUpdates();
            m_finishedInit = true;
        }
        bool m_finishedInit = false;
        PerformanceCounter availableMemoryRequester = new PerformanceCounter("Memory", "Available MBytes");
        private Version usersVersion = new Version(1, 0, 1, 5);
        public void CheckForUpdates()
        {
            Thread t = new Thread(new ThreadStart(update));
            t.Priority = ThreadPriority.Lowest;
            t.IsBackground = true;
            t.Start();
        }
        private void update()
        {
            WebClient client = new WebClient(); //http://tmapc.googlecode.com/files/TripleA%20Map%20Creator%20v1.0.0.8.zip
            Version currentCheckingVersion = usersVersion;
            Version newestVersionAvailable = usersVersion;
            bool doBreak = false;
            bool hasStartedFindingVersions = false;

            while (!doBreak)
            {
                try
                {
                    Stream s = client.OpenRead("http://tmapc.googlecode.com/files/TripleA%20Map%20Creator%20v" + currentCheckingVersion.ToString() + ".zip");
                    newestVersionAvailable = currentCheckingVersion;
                    if (currentCheckingVersion.Revision < 9)
                        currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor, currentCheckingVersion.Build, currentCheckingVersion.Revision + 1);
                    else if (currentCheckingVersion.Build < 9)
                        currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor, currentCheckingVersion.Build + 1, 0);
                    else if (currentCheckingVersion.Minor < 9)
                        currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor + 1, 0, 0);
                    else if (currentCheckingVersion.Major < 9)
                        currentCheckingVersion = new Version(currentCheckingVersion.Major + 1, 0, 0, 0);

                    s.Close();
                    hasStartedFindingVersions = true;
                }
                catch
                {
                    if (hasStartedFindingVersions)
                        break;
                    else
                    {
                        if (currentCheckingVersion.Revision < 9)
                            currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor, currentCheckingVersion.Build, currentCheckingVersion.Revision + 1);
                        else if (currentCheckingVersion.Build < 9)
                            currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor, currentCheckingVersion.Build + 1, 0);
                        else if (currentCheckingVersion.Minor < 9)
                            currentCheckingVersion = new Version(currentCheckingVersion.Major, currentCheckingVersion.Minor + 1, 0, 0);
                        else if (currentCheckingVersion.Major < 9)
                            currentCheckingVersion = new Version(currentCheckingVersion.Major + 1, 0, 0, 0);
                    }
                }
            }
            if (Convert.ToInt32(usersVersion.ToString().Replace(".", "")) < Convert.ToInt32(newestVersionAvailable.ToString().Replace(".", "")))
            {
                MessageBox.Show("There is a newer version of the Map Creator available.\r\nYour version: " + usersVersion.ToString() + ".\r\nNewest Version: " + newestVersionAvailable.ToString() + ".\r\n\r\nTo download the latest version, please go to \"http://code.google.com/p/tmapc/downloads/list\" and click on the latest download.", "Checking For Updates");
            }
        }
        public void RefreshSettings()
        {
            try
            {
                if (File.Exists(new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + "/Settings.txt") && File.ReadAllText(new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + "/Settings.txt").Contains("Java Heap Size=\""))
                {
                    string[] lines = File.ReadAllLines(new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + "/Settings.txt");
                    foreach (string cur in lines)
                    {
                        if (cur.Contains("Java Heap Size=\""))
                        {
                            Settings.JavaHeapSize = Convert.ToInt32(cur.Substring(cur.IndexOf("Heap Size=\"") + 11, cur.Substring(cur.IndexOf("Heap Size=\"") + 11).IndexOf("\"")));
                        }
                    }
                }
                else
                {
                    if (MessageBox.Show("The program is unable to locate the settings file. Do you want the program to re-create the settings file?", "Can't Locate Settings", MessageBoxButtons.YesNoCancel) == DialogResult.Yes)
                    {
                        try
                        {
                            List<string> lines = new List<string>();
                            lines.Add("Stop Loading XML File When Error Is Found=\"false\"");
                            lines.Add("Java Heap Size=\"5000\"");
                            File.WriteAllLines(new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + "/Settings.txt", lines.ToArray());
                        }
                        catch (Exception ex) { exceptionViewerWindow.ShowInformationAboutException(ex.GetBaseException(), true); }
                    }
                }
            }
            catch (Exception ex)
            {
                if (MessageBox.Show("An error occured when trying to load the settings file for the program. Please make sure the \"Settings.txt\" file contains no errors. Do you want to view the error message?", "Error loading settings file.", MessageBoxButtons.YesNoCancel) == DialogResult.Yes)
                    exceptionViewerWindow.ShowInformationAboutException(ex.GetBaseException(), true);
            }
        }
        ExceptionViewer exceptionViewerWindow = new ExceptionViewer();
        public static class Settings
        {
            public static int JavaHeapSize = 5000;
        }
        private void button2_Click(object sender, EventArgs e)
        {
            Back();
            DisplayStepTabPage();
            //MessageBox.Show(Step2Info.territories.Count.ToString() + "/" + Step3Info.connections.Count.ToString());
            //MessageBox.Show(TerritoryDefinitionsImageDrawer.Controls.Count.ToString() + "/" + TerritoryConnectionsImageDrawer.Controls.Count.ToString());
        }
        int stepIndex = 0;
        int oldStepIndex = 0;
        private void Back()
        {
            if (stepIndex == 7 && m_utilityWaitingThread != null && m_utilityWaitingThread.IsAlive)
            {
                if (MessageBox.Show("The 'Relief Image Breaker' utility is still running. If you change the current step before it finishes, some of the map files may not be complete. Are you sure you want to go back a step?", "Confirmation", MessageBoxButtons.YesNoCancel) != DialogResult.Yes)
                    return;
            }
            Control oldControl = getControl("label" + stepIndex);
            oldStepIndex = stepIndex;
            if (stepIndex > 0)
                stepIndex--;
            Control newControl = getControl("label" + stepIndex);
            oldControl.Font = new Font(oldControl.Font, FontStyle.Regular);
            newControl.Font = new Font(newControl.Font, FontStyle.Bold);

            if (stepIndex == 0)
            {
                v_backButton.Enabled = false;
                v_nextButton.Enabled = true;
            }
            else
            {
                v_backButton.Enabled = true;
                v_nextButton.Enabled = true;
            }

            if (stepIndex == 5)
            {
                try
                {
                    if (Directory.Exists(cutPath))
                    {
                        foreach (string cur in Directory.GetFiles(cutPath))
                        {
                            //File.Copy(cur, textBox3.Text + "/" + new FileInfo(cur).Name, true);
                            try
                            {
                                File.Delete(cur);
                            }
                            catch { }
                        }
                        Directory.Delete(cutPath);
                    }
                }
                catch (Exception ex) {/*Error Message Unnecessary Because Deletion Is Unnecessary*/ }
            }
            else if (stepIndex == 7)
            {
                try
                {
                    if (Directory.Exists(cutPath) && Directory.Exists(cutPath + "/reliefTiles/") && Directory.Exists(textBox3.Text + "/reliefTiles"))
                    {
                        Directory.Move(cutPath + "/reliefTiles/", textBox3.Text + "/reliefTiles");
                        Directory.Delete(cutPath);
                    }
                }
                catch (Exception ex) { exceptionViewerWindow.ShowInformationAboutException(ex.GetBaseException(), true); }
            }
            UpdateWindowText();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            Next();
            DisplayStepTabPage();
            //MessageBox.Show(Step2Info.territories.Count.ToString() + "/" + Step3Info.connections.Count.ToString());
            //MessageBox.Show(TerritoryDefinitionsImageDrawer.Controls.Count.ToString() + "/" + TerritoryConnectionsImageDrawer.Controls.Count.ToString());
        }
        public void UpdateWindowText()
        {
            switch (stepIndex)
            {
                case 0:
                    {
                        this.Text = defaultWindowText;
                        break;
                    }
                case 1:
                    {
                        this.Text = defaultWindowText;
                        break;
                    }
                case 2:
                    {
                        this.Text = defaultWindowText + " (Approximately 6% Done With Part 1)";
                        break;
                    }
                case 3:
                    {
                        this.Text = defaultWindowText + " (Approximately 60% Done With Part 1)";
                        break;
                    }
                case 4:
                    {
                        this.Text = defaultWindowText + " (Approximately 76% Done With Part 1)";
                        break;
                    }
                case 5:
                    {
                        this.Text = defaultWindowText + " (Approximately 76% Done With Part 1)";
                        break;
                    }
                case 6:
                    {
                        this.Text = defaultWindowText + " (Approximately 80% Done With Part 1)";
                        break;
                    }
                case 7:
                    {
                        this.Text = defaultWindowText + " (Approximately 86% Done With Part 1)";
                        break;
                    }
                case 8:
                    {
                        this.Text = defaultWindowText + " (Approximately 94% Done With Part 1)";
                        break;
                    }
                case 9:
                    {
                        this.Text = defaultWindowText + " (Part 1 Finished)";
                        break;
                    }
            }
        }
        private string defaultWindowText = "TripleA Map Creator - Part 1";
        private void Next()
        {

            if (stepIndex == 7 && m_utilityWaitingThread != null && m_utilityWaitingThread.IsAlive)
            {
                if (MessageBox.Show("The 'Relief Image Breaker' utility is still running. If you change the current step before it finishes, some of the map files may not be complete. Are you sure you want to go forward a step?", "Confirmation", MessageBoxButtons.YesNoCancel) != DialogResult.Yes)
                    return;
            }
            int nonFilledFields = 0;
            int filledFields = 0;
            foreach (Control cur in tabControl1.TabPages[stepIndex].Controls)
            {
                if (cur is TextBox && cur.Name.ToLower() != "textbox3")
                {
                    if (!(cur.Text.Trim().Length > 0))
                    {
                        nonFilledFields++;
                    }
                    else
                    {
                        filledFields++;
                    }
                }
            }
            foreach (Control cur in v_mapPropertiesPanel.Controls)
            {
                if (cur is TextBox && cur.Name.ToLower() != "textbox3")
                {
                    if (!(cur.Text.Trim().Length > 0))
                    {
                        nonFilledFields++;
                    }
                    else
                    {
                        filledFields++;
                    }
                }
            }
            if (tabControl1.SelectedIndex == 1)
            {
                if (textBox3.Text.Trim().Length == 0)
                {
                    MessageBox.Show("Please supply the location of the map's folder before proceeding to the next step.", "Supply Map Folder");
                    return;
                }
                else if (!Directory.Exists(textBox3.Text.Trim()))
                {
                    MessageBox.Show("The map folder you supplied does not exist. Please supply the correct map folder and try again.", "Supply Valid Map Folder");
                    return;
                }
                else if ((filledFields > 0 && nonFilledFields != 0) || (!File.Exists(textBox3.Text + @"\map.properties") && nonFilledFields > 0))
                {
                    MessageBox.Show("Please supply the rest of the required information before proceeding to the next step.", "Complete Current Step");
                    return;
                }
            }
            if (filledFields > 0 && nonFilledFields == 0)
            {
                enteredMapProperties = true;
            }
            else
            {
                enteredMapProperties = false;
            }

            Control oldControl = getControl("label" + stepIndex);
            oldStepIndex = stepIndex;
            if (stepIndex < 9)
                stepIndex++;
            Control newControl = getControl("label" + stepIndex);
            oldControl.Font = new Font(oldControl.Font, FontStyle.Regular);
            newControl.Font = new Font(newControl.Font, FontStyle.Bold);

            if (stepIndex == 9)
            {
                v_backButton.Enabled = true;
                v_nextButton.Enabled = false;
            }
            else
            {
                v_backButton.Enabled = true;
                v_nextButton.Enabled = true;
            }

            if (stepIndex == 5)
            {
                try
                {
                    if (Directory.Exists(cutPath))
                    {
                        foreach (string cur in Directory.GetFiles(cutPath))
                        {
                            //File.Copy(cur, textBox3.Text + "/" + new FileInfo(cur).Name, true);
                            try
                            {
                                File.Delete(cur);
                            }
                            catch { }
                        }
                        Directory.Delete(cutPath);
                    }
                }
                catch (Exception ex) {/*Error Message Unnecessary Because Deletion Is Unnecessary*/ }
            }
            else if (stepIndex == 7)
            {
                try
                {
                    if (Directory.Exists(cutPath) && Directory.Exists(cutPath + "/reliefTiles/") && Directory.Exists(textBox3.Text + "/reliefTiles"))
                    {
                        Directory.Move(cutPath + "/reliefTiles/", textBox3.Text + "/reliefTiles");
                        Directory.Delete(cutPath);
                    }
                }
                catch (Exception ex) { exceptionViewerWindow.ShowInformationAboutException(ex.GetBaseException(), true); }
            }
            UpdateWindowText();
        }
        public Control getControl(string name)
        {
            foreach (Control cur in this.Controls)
            {
                if (cur.Name == name)
                    return cur;
            }
            return new Control();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (MessageBox.Show("Are you sure you want to exit?", "Confirmation", MessageBoxButtons.YesNoCancel) != DialogResult.Yes)
                e.Cancel = true;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            DisplayStepTabPage();
        }
        private void DisplayStepTabPage()
        {
            tabControl1.SelectedIndex = stepIndex;
        }
        Size oldSize = new Size(777, 525);
        private void Form1_Resize(object sender, EventArgs e)
        {
            if (!m_finishedInit)
                return;

            Size change = new Size(Size.Width - oldSize.Width, Size.Height - oldSize.Height);
            tabControl1.Size = ((Rectangle)tabControl1.Tag).Size + change;
            panel2.Location = ((Rectangle)panel2.Tag).Location + new Size(change.Width / 2, change.Height);
            button5.Size = ((Rectangle)button5.Tag).Size + change;
            button14.Size = ((Rectangle)button14.Tag).Size + change;
            button4.Size = ((Rectangle)button4.Tag).Size + change;
            button15.Size = ((Rectangle)button15.Tag).Size + change;
            button6.Size = ((Rectangle)button6.Tag).Size + change;
            button9.Size = ((Rectangle)button9.Tag).Size + change;
            button17.Size = ((Rectangle)button17.Tag).Size + change;
            button19.Size = ((Rectangle)button19.Tag).Size + change;
            button21.Size = ((Rectangle)button21.Tag).Size + change;
            button10.Location = ((Rectangle)button10.Tag).Location + new Size(change.Width / 2, change.Height);
            button13.Location = ((Rectangle)button13.Tag).Location + new Size(change.Width / 2, change.Height);
            button16.Location = ((Rectangle)button16.Tag).Location + new Size(change.Width / 2, change.Height);
            button18.Location = ((Rectangle)button18.Tag).Location + new Size(change.Width / 2, change.Height);
            button20.Location = ((Rectangle)button20.Tag).Location + new Size(change.Width / 2, change.Height);
            button22.Location = ((Rectangle)button22.Tag).Location + new Size(change.Width / 2, change.Height);
            button23.Location = ((Rectangle)button23.Tag).Location + new Size(change.Width / 2, change.Height);
            button7.Location = ((Rectangle)button7.Tag).Location + new Size(change.Width / 2, change.Height);
        }
        int change = 0;
        private void button6_Click(object sender, EventArgs e)
        {
            change += 25;
            button8.Location += new Size(0, 25);
            button11.Location += new Size(0, 25);
            TextBox t1 = new TextBox() { Size = textBox2.Size, Location = new Point(textBox2.Location.X, textBox2.Location.Y + change) };
            t1.TextChanged += new EventHandler(t1_TextChanged);
            t1.DoubleClick += new EventHandler(t1_DoubleClick);
            Button b1 = new Button { Font = button3.Font, Text = button3.Text, Size = button3.Size, Location = new Point(button3.Location.X, button3.Location.Y + change) };
            b1.Click += new EventHandler(b1_Click);
            b1.Tag = t1;
            toolTip1.SetToolTip(b1, "Select a color using the color chooser window.");
            v_mapPropertiesPanel.Controls.AddRange(new Control[] { new TextBox() { Size = textBox1.Size, Location = new Point(textBox1.Location.X, textBox1.Location.Y + change) }, t1, b1 });
        }

        void t1_TextChanged(object sender, EventArgs e)
        {
            try
            {
                ((TextBox)sender).BackColor = ColorTranslator.FromHtml(((TextBox)sender).Text);
            }
            catch { ((TextBox)sender).BackColor = Color.FromKnownColor(KnownColor.Control); }
        }

        void b1_Click(object sender, EventArgs e)
        {
            ColorDialog chooser = new ColorDialog();
            chooser.AllowFullOpen = true;
            chooser.AnyColor = true;
            chooser.FullOpen = true;
            try
            {
                chooser.Color = ColorTranslator.FromHtml(((Button)sender).Text);
            }
            catch { }

            if (chooser.ShowDialog() != DialogResult.Cancel)
            {
                if (!chooser.Color.IsNamedColor)
                    ((TextBox)((Button)sender).Tag).Text = ColorTranslator.ToHtml(chooser.Color).ToString();
                else
                    ((TextBox)((Button)sender).Tag).Text = ColorTranslator.ToHtml(Color.FromArgb(chooser.Color.R, chooser.Color.G, (chooser.Color.B < 255 ? chooser.Color.B + 1 : chooser.Color.B - 1))).ToString();
            }
        }
        void t1_DoubleClick(object sender, EventArgs e)
        {
            ColorDialog chooser = new ColorDialog();
            chooser.AllowFullOpen = true;
            chooser.AnyColor = true;
            chooser.FullOpen = true;
            try
            {
                chooser.Color = ColorTranslator.FromHtml(((TextBox)sender).Text);
            }
            catch { }
            if (chooser.ShowDialog() != DialogResult.Cancel)
            {
                if (!chooser.Color.IsNamedColor)
                    ((TextBox)sender).Text = ColorTranslator.ToHtml(chooser.Color).ToString();
                else
                    ((TextBox)sender).Text = ColorTranslator.ToHtml(Color.FromArgb(chooser.Color.R, chooser.Color.G, (chooser.Color.B < 255 ? chooser.Color.B + 1 : chooser.Color.B - 1))).ToString();
            }
        }
        private void button7_Click(object sender, EventArgs e)
        {
            if (change > 0)
            {
                v_mapPropertiesPanel.Controls.RemoveAt(v_mapPropertiesPanel.Controls.Count - 1);
                v_mapPropertiesPanel.Controls.RemoveAt(v_mapPropertiesPanel.Controls.Count - 1);
                v_mapPropertiesPanel.Controls.RemoveAt(v_mapPropertiesPanel.Controls.Count - 1);
                change -= 25;
                button8.Location += new Size(0, -25);
                button11.Location += new Size(0, -25);
            }
        }
        bool enteredMapProperties = false;
        private void tabControl1_Selecting(object sender, TabControlCancelEventArgs e)
        {
            try
            {
                if (oldStepIndex < stepIndex)
                {
                    if (oldStepIndex == 1)
                    {
                        if (enteredMapProperties)
                        {
                            if (File.Exists(textBox3.Text + "/map.properties"))
                            {
                                if (MessageBox.Show("Another map.properties file was found in the maps folder. Do you want to overwrite it with a new file consisting of what you just entered?", "Overwrite?", MessageBoxButtons.YesNoCancel) != DialogResult.Yes)
                                    return;
                            }
                            string playerName = "";
                            List<String> lines = new List<string>();
                            foreach (Control cur in v_mapPropertiesPanel.Controls)
                            {
                                if (cur is TextBox && cur.Name != "textBox3" && cur.Name != "textBox4" && cur.Name != "textBox7")
                                {
                                    if (playerName == "")
                                        playerName = cur.Text;
                                    else
                                    {
                                        lines.Add("color." + playerName + "=" + cur.Text.Replace("#", ""));
                                        playerName = "";
                                    }
                                }
                            }
                            lines.Add("units.scale=" + textBox4.Text);
                            lines.Add("map.hasRelief=" + comboBox1.Text.ToLower());
                            lines.Add("map.showCapitolMarkers=false");
                            lines.Add("map.width=" + textBox7.Text.Substring(0, textBox7.Text.IndexOf(",")));
                            lines.Add("map.height=" + textBox7.Text.Substring(textBox7.Text.IndexOf(",") + 1));
                            lines.Add("map.scrollWrapX=" + comboBox2.Text.ToLower());
                            File.WriteAllLines(textBox3.Text + "/map.properties", lines.ToArray());
                        }
                    }
                    if (stepIndex == 1)
                    {
                        comboBox1.SelectedIndex = 1;
                        comboBox2.SelectedIndex = 1;
                    }
                }
            }
            catch (Exception ex) { if (MessageBox.Show("An error occured when trying to write the map properties. Make sure you entered everything correctly and try again. Do you want to view the error message?", "Error Writing Map Properties", MessageBoxButtons.YesNoCancel) == DialogResult.Yes) { exceptionViewerWindow.ShowInformationAboutException(ex.GetBaseException(), true); } e.Cancel = true; Back(); }
        }
        FolderBrowserDialog d = new FolderBrowserDialog();
        private void button12_Click(object sender, EventArgs e)
        {
            d.Description = "Please select the map's folder which contains the map files.";
            d.ShowNewFolderButton = true;
            if (d.ShowDialog() == DialogResult.OK)
                textBox3.Text = d.SelectedPath;
        }
        string tripleADirectory = null;
        string file = "";
        private void button13_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;
            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/CenterPicker");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                System.Diagnostics.Process.Start(info);
            }
        }
        public void SetNewestTripleAVersion()
        {
            if (tripleADirectory == null)
            {
                if (Directory.Exists("C:/Program Files/TripleA/") && new DirectoryInfo("C:/Program Files/TripleA/").GetDirectories().Length > 0)
                    tripleADirectory = "C:/Program Files/TripleA/";
                else
                {
                    FolderBrowserDialog od = new FolderBrowserDialog();
                    od.Description = "Please locate the TripleA Program's folder. (Where you installed TripleA)";
                    od.ShowNewFolderButton = true;
                    if (Directory.Exists(@"C:\Program Files\"))
                        od.SelectedPath = @"C:\Program Files\";
                    if (od.ShowDialog() != DialogResult.Cancel)
                        tripleADirectory = od.SelectedPath;
                    else return;
                }
                bool isBaseDirectory = false;
                foreach (DirectoryInfo cur in new DirectoryInfo(tripleADirectory).GetDirectories())
                {
                    if (cur.Name.ToLower() == "bin")
                    {
                        isBaseDirectory = true;
                    }
                }
                if (isBaseDirectory == false)
                {
                    DirectoryInfo newestTripleAVersion = new DirectoryInfo(tripleADirectory).GetDirectories()[0];
                    foreach (DirectoryInfo cur in new DirectoryInfo(tripleADirectory).GetDirectories())
                    {
                        try
                        {
                            if (Convert.ToInt32(cur.Name.Substring(cur.Name.IndexOf("_")).Replace("_", "")) > Convert.ToInt32(newestTripleAVersion.Name.Substring(newestTripleAVersion.Name.IndexOf("_")).Replace("_", "")) && File.Exists(cur.FullName + "/bin/triplea.jar"))
                                newestTripleAVersion = cur;
                        }
                        catch { }
                    }
                    tripleADirectory = newestTripleAVersion.FullName;
                }
            }
        }

        private void button7_Click_1(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false;
                d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/PolygonGrabber");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                System.Diagnostics.Process.Start(info);
            }
        }

        private void button10_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/PlacementPicker");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                System.Diagnostics.Process.Start(info);
            }
        }
        string cutPath = "";
        private void button16_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            if (File.Exists(file))
            {
                cutPath = Directory.GetParent(file.Substring(0, file.Replace("\\", @"\").LastIndexOf(@"\"))).FullName + @"\maps\temp\";
                if (!File.Exists(textBox3.Text + @"\place.txt"))
                    File.Create(textBox3.Text + @"\place.txt").Close();
                Directory.CreateDirectory(cutPath);
                foreach (string cur in Directory.GetFiles(textBox3.Text))
                {
                    File.Copy(cur, cutPath + new FileInfo(cur).Name, true);
                }
                if (Directory.Exists(textBox3.Text + @"\misc"))
                {
                    Directory.CreateDirectory(cutPath + @"\misc");
                    foreach (FileInfo cur in new DirectoryInfo(textBox3.Text + @"\misc").GetFiles())
                    {
                        File.Copy(cur.FullName, cutPath + @"\misc\" + cur.Name, true);
                    }
                }
                double unitsScale = getUnitScale(new DirectoryInfo(textBox3.Text));
                float availableMemory = availableMemoryRequester.NextValue();
                if (File.Exists(file))
                {
                    StreamWriter writer = new StreamWriter("C:/temp.cmd");
                    if (unitsScale != 0)
                        writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/AutoPlacementFinder " + unitsScale.ToString());
                    else
                        writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/AutoPlacementFinder");
                    if (controlDown)
                    {
                        writer.WriteLine("pause");
                        controlDown = false;
                    }
                    writer.Close();
                    ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                    info.WorkingDirectory = @"C:\";
                    System.Diagnostics.Process.Start(info);
                }
            }
        }

        private void button18_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            if (!Directory.Exists(textBox3.Text + @"\baseTiles\"))
                Directory.CreateDirectory(textBox3.Text + @"\baseTiles\");
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/TileImageBreaker");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                System.Diagnostics.Process.Start(info);
            } 
        }

        private void button20_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            cutPath = Directory.GetParent(file.Substring(0, file.Replace("\\", @"\").LastIndexOf(@"\"))).FullName + @"\maps\temp\";
            Directory.CreateDirectory(cutPath + @"\reliefTiles\");
            foreach (string cur in Directory.GetFiles(textBox3.Text))
            {
                File.Copy(cur, cutPath + new FileInfo(cur).Name, true);
            }
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/ReliefImageBreaker");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                m_utilityWaitingThread = new Thread(new ParameterizedThreadStart(runReliefImageBreakerAndWaitForExit));
                m_utilityWaitingThread.Start(info);
            }
        }
        Thread m_utilityWaitingThread;
        public void runReliefImageBreakerAndWaitForExit(object obj)
        {
            ProcessStartInfo info = (ProcessStartInfo)obj;
            System.Diagnostics.Process.Start(info).WaitForExit();
        }
        private void button22_Click(object sender, EventArgs e)
        {
            SetNewestTripleAVersion();
            if (tripleADirectory == null)
                return;

            file = tripleADirectory + @"\bin\triplea.jar";
            if (!File.Exists(file))
            {
                OpenFileDialog d2 = new OpenFileDialog();
                d2.Multiselect = false; d2.Filter = "Jar Files|*.jar|All files (*.*)|*.*";
                d2.InitialDirectory = tripleADirectory;
                d2.Title = "Please locate the 'triplea.jar' file. (It should be in the TripleA Home folder, under a folder named 'bin'.)";
                if (d2.ShowDialog() == DialogResult.OK)
                    file = d2.FileName.Replace("/",@"\");
            }
            float availableMemory = availableMemoryRequester.NextValue();
            if (File.Exists(file))
            {
                StreamWriter writer = new StreamWriter("C:/temp.cmd");
                writer.WriteLine("java.exe -Xmx" + (availableMemory < Settings.JavaHeapSize ? availableMemory - 10 : Settings.JavaHeapSize) + "m" + " -classpath \"" + file + "\" util/image/ImageShrinker");
                if (controlDown)
                {
                    writer.WriteLine("pause");
                    controlDown = false;
                }
                writer.Close();
                ProcessStartInfo info = new ProcessStartInfo("C:/temp.cmd");
                info.WorkingDirectory = @"C:\";
                System.Diagnostics.Process.Start(info);
            } 
        }
        public static double getUnitScale(DirectoryInfo mapFolder)
        {
            double result = 0;
            if (File.Exists(mapFolder.FullName + @"\map.properties"))
            {
                string[] lines = File.ReadAllLines(mapFolder.FullName + @"\map.properties");
                foreach (string cur in lines)
                {
                    if (cur.ToLower().Contains("units.scale="))
                    {
                        result = Convert.ToDouble(cur.ToLower().Substring(cur.ToLower().IndexOf(".scale=") + 7));
                    }
                }
            }
            else
            {
                OpenFileDialog open = new OpenFileDialog();
                open.CheckFileExists = true;
                open.DefaultExt = ".properties";
                open.Filter = "Map Properties Files|*.properties|All files (*.*)|*.*";
                open.InitialDirectory = mapFolder.Parent.FullName;
                open.Multiselect = false;
                open.Title = "Please select the map.properties file for the map.";
                if (open.ShowDialog() != DialogResult.Cancel)
                {
                    string[] lines = File.ReadAllLines(open.FileName);
                    foreach (string cur in lines)
                    {
                        if (cur.ToLower().Contains("units.scale="))
                        {
                            result = Convert.ToDouble(cur.ToLower().Substring(cur.ToLower().IndexOf(".scale=") + 7));
                        }
                    }
                }
            }
            return result;
        }

        private void button3_Click_1(object sender, EventArgs e)
        {
            ColorDialog chooser = new ColorDialog();
            chooser.AllowFullOpen = true;
            chooser.AnyColor = true;
            chooser.FullOpen = true;
            try
            {
                chooser.Color = ColorTranslator.FromHtml(textBox2.Text);
            }
            catch { }
            if (chooser.ShowDialog() != DialogResult.Cancel)
            {
                if (!chooser.Color.IsNamedColor)
                    textBox2.Text = ColorTranslator.ToHtml(chooser.Color).ToString();
                else
                    textBox2.Text = ColorTranslator.ToHtml(Color.FromArgb(chooser.Color.R, chooser.Color.G, (chooser.Color.B < 255 ? chooser.Color.B + 1 : chooser.Color.B - 1))).ToString();
            }
        }

        private void textBox2_TextChanged(object sender, EventArgs e)
        {
            try
            {
                ((TextBox)sender).BackColor = ColorTranslator.FromHtml(((TextBox)sender).Text);
            }
            catch { ((TextBox)sender).BackColor = Color.FromKnownColor(KnownColor.Control); }
        }
        private void textBox2_DoubleClick(object sender, EventArgs e)
        {
            ColorDialog chooser = new ColorDialog();
            chooser.AllowFullOpen = true;
            chooser.AnyColor = true;
            chooser.FullOpen = true;
            try
            {
                chooser.Color = ColorTranslator.FromHtml(textBox2.Text);
            }
            catch { }
            if (chooser.ShowDialog() != DialogResult.Cancel)
            {
                if (!chooser.Color.IsNamedColor)
                    textBox2.Text = ColorTranslator.ToHtml(chooser.Color).ToString();
                else
                    textBox2.Text = ColorTranslator.ToHtml(Color.FromArgb(chooser.Color.R, chooser.Color.G, (chooser.Color.B < 255 ? chooser.Color.B + 1 : chooser.Color.B - 1))).ToString();
            }
        }
        private void button23_Click(object sender, EventArgs e)
        {
            if (File.Exists(new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + @"\TripleA Map Creator Part 2.exe"))
            {
                System.Diagnostics.Process.Start((new FileInfo(Assembly.GetExecutingAssembly().Location).Directory + @"\TripleA Map Creator Part 2.exe"));
                this.Close();
            }
            else
            {
                MessageBox.Show("Part 2 was not found in the same folder as Part 1.", "Part 2 Missing");
            }
        }
        bool controlDown = false;
        private void Form1_KeyDown(object sender, KeyEventArgs e)
        {
            controlDown = e.Control;
        }

        private void Form1_KeyUp(object sender, KeyEventArgs e)
        {
            controlDown = e.Control;
        }
    }
}
