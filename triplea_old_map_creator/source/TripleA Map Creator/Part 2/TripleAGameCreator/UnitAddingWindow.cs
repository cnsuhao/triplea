using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TripleAGameCreator
{
    public partial class UnitAddingWindow : Form
    {
        public UnitAddingWindow()
        {
            InitializeComponent();
            this.Tag = this.Size;
            v_unitsPanelsHolderPanel.Tag = v_unitsPanelsHolderPanel.Size;
            label1.Tag = label1.Location;
            label2.Tag = label2.Location;
            v_unitCollectionResultStringTextbox.Tag = v_unitCollectionResultStringTextbox.Bounds;
            v_doneButton.Tag = v_doneButton.Location;
            v_cancelButton.Tag = v_cancelButton.Location;
            v_unitCollectionResultStringTextbox.Focus();
        }
        public string RetrieveUnitsString(Territory territory,string currentUnits)
        {
            Value = "";
            this.Text = "Changing " + territory.Name + "'s Units";
            v_unitCollectionResultStringTextbox.Text = currentUnits;
            v_unitCollectionResultStringTextbox.SelectAll();
            v_unitCollectionResultStringTextbox.Focus();
            UpdateUnitPanels(Step5Info.units.Values);
            this.ShowDialog();
            return this.Value;
        }
        private void UpdateUnitPanels(Dictionary<string,Unit>.ValueCollection units)
        {
            nextLocation = new Point(0,v_unitsPanelsHolderPanel.AutoScrollPosition.Y);
            v_unitsPanelsHolderPanel.SuspendLayout();
            foreach (Control cur in v_unitsPanelsHolderPanel.Controls)
            {
                System.Windows.Forms.NativeWindow.FromHandle(cur.Handle).DestroyHandle();
            }
            v_unitsPanelsHolderPanel.Controls.Clear();
            foreach (Unit unit in units)
            {
                AddUnitPanel(CreateUnitPanel(unit));
            }
            v_unitsPanelsHolderPanel.ResumeLayout();
        }
        private void AddUnitPanel(Panel panel)
        {
            panel.Location = nextLocation;
            v_unitsPanelsHolderPanel.Controls.Add(panel);
            nextLocation.X += unitPanelSize.Width;
            if (nextLocation.X + unitPanelSize.Width > v_unitsPanelsHolderPanel.Width - 4)
            {
                nextLocation.Y += unitPanelSize.Height;
                nextLocation.X = 0;
            }
        }
        public Size unitPanelSize = new Size(90, 70);
        public Point nextLocation = new Point();
        private Panel CreateUnitPanel(Unit unit)
        {
            string resultText = v_unitCollectionResultStringTextbox.Text;

            Panel unitPanel = new Panel() { BorderStyle = BorderStyle.Fixed3D };
            unitPanel.Size = new Size(unitPanelSize.Width, unitPanelSize.Height);
            unitPanel.Controls.Add(new Label() { AutoSize = false, Text = unit.Name, TextAlign = ContentAlignment.MiddleCenter, Location = new Point(0, 0), Size = new Size(unitPanelSize.Width, 20) });
            NumericUpDown unitAmountNC = new NumericUpDown();
            unitAmountNC.Maximum = 1000;
            unitAmountNC.Location = new Point(3, 20);
            unitAmountNC.Size = new Size(unitPanel.Width - 4 - 6, 20);
            if (resultText.Contains(unit.Name))
            {
                string afterName = resultText.Substring(resultText.IndexOf(unit.Name) + unit.Name.Length);
                if (!afterName.Contains(","))
                    afterName = afterName + ",";

                string num = afterName.Substring(afterName.IndexOf(":") + 1, afterName.Substring(afterName.IndexOf(":") + 1).IndexOf(",")).Trim();
                try
                {
                    unitAmountNC.Value = Convert.ToInt32(num);
                }
                catch (OverflowException ex) { if (num.Trim().StartsWith("-"))unitAmountNC.Value = 0; else unitAmountNC.Value = unitAmountNC.Maximum; }
                catch (ArgumentOutOfRangeException ex)
                {
                    try
                    {
                        if (Convert.ToDouble(num) > 0)
                            unitAmountNC.Value = unitAmountNC.Maximum;
                        else
                            unitAmountNC.Text = "0";
                    }
                    catch { if (num.Trim().StartsWith("-"))unitAmountNC.Value = 0; else unitAmountNC.Value = unitAmountNC.Maximum; }
                }
                catch (FormatException ex) { unitAmountNC.Text = "0"; }
                catch (ArgumentException ex) { unitAmountNC.Text = "0"; }
            }
            else
                unitAmountNC.Value = 0;
            unitAmountNC.ValueChanged += new EventHandler(unitAmountNC_ValueChanged);
            unitAmountNC.Tag = unit.Name;
            unitPanel.Controls.Add(unitAmountNC);
            Button lessButton = new Button();
            lessButton.Location = new Point(0, unitAmountNC.Bottom + 1);
            lessButton.Size = new Size((unitPanel.Width - 4) / 2, (unitPanel.Height - 4) - lessButton.Location.Y);
            lessButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            lessButton.Click += new EventHandler(lessButton_Click);
            lessButton.Tag = unitAmountNC;
            lessButton.Text = "-";
            unitPanel.Controls.Add(lessButton);
            Button moreButton = new Button();
            moreButton.Location = new Point(((unitPanel.Width - 4) / 2), unitAmountNC.Bottom + 1);
            moreButton.Size = new Size((unitPanel.Width - 4) / 2, (unitPanel.Height - 4) - moreButton.Location.Y);
            moreButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            moreButton.Click += new EventHandler(moreButton_Click);
            moreButton.Tag = unitAmountNC;
            moreButton.Text = "+";
            unitPanel.Controls.Add(moreButton);
            return unitPanel;
        }

        void unitAmountNC_ValueChanged(object sender, EventArgs e)
        {
            UpdateResultText(((NumericUpDown)sender));
        }
        private void UpdateResultText(NumericUpDown num)
        {
            string unitName = num.Tag.ToString();
            string resultText = v_unitCollectionResultStringTextbox.Text;
            string newText = "";
            if (resultText.Contains(unitName))
            {
                string beforeName = resultText.Substring(0, resultText.IndexOf(unitName));
                string afterName = resultText.Substring(resultText.IndexOf(unitName) + unitName.Length);
                if (afterName.Contains(","))
                    afterName = afterName.Substring(afterName.IndexOf(","));
                else
                    afterName = "";
                if (num.Value > 0)
                {
                    newText = String.Concat(beforeName, unitName, ":", num.Value.ToString(), afterName);
                }
                else
                {
                    newText = beforeName;
                    if (afterName.Length > 0)
                        newText += afterName.Substring(1);
                    else if (beforeName.Length > 0)
                        newText = newText.Substring(0, newText.Length - 1);
                }
            }
            else
            {
                if (resultText.Trim().Length > 0)
                    newText = String.Concat(resultText, ",", unitName, ":", num.Value.ToString());
                else
                    newText = String.Concat(unitName, ":", num.Value.ToString());
            }
            updatePanels = false;
            v_unitCollectionResultStringTextbox.Text = newText;
        }
        void moreButton_Click(object sender, EventArgs e)
        {
            if (((NumericUpDown)((Button)sender).Tag).Value < ((NumericUpDown)((Button)sender).Tag).Maximum)
                ((NumericUpDown)((Button)sender).Tag).Value++;
        }
        void lessButton_Click(object sender, EventArgs e)
        {
            if(((NumericUpDown)((Button)sender).Tag).Value > 0)
                ((NumericUpDown)((Button)sender).Tag).Value--;
        }
        string Value = "";
        private void v_doneButton_Click(object sender, EventArgs e)
        {
            Value = v_unitCollectionResultStringTextbox.Text;
            v_unitCollectionResultStringTextbox.Focus();
            Hide();
        }

        private void v_cancelButton_Click(object sender, EventArgs e)
        {
            Value = "";
            v_unitCollectionResultStringTextbox.Focus();
            Hide();
        }

        private void UnitAddingWindow_Resize(object sender, EventArgs e)
        {
            Size oldSize = (Size)this.Tag;
            Size change = new Size(Width - oldSize.Width, Height - oldSize.Height);
            v_unitsPanelsHolderPanel.Size = (Size)v_unitsPanelsHolderPanel.Tag + change;
            v_doneButton.Location = (Point)v_doneButton.Tag + new Size(change.Width / 2, change.Height);
            v_cancelButton.Location = (Point)v_cancelButton.Tag + new Size(change.Width / 2, change.Height);
            v_unitCollectionResultStringTextbox.Location = ((Rectangle)v_unitCollectionResultStringTextbox.Tag).Location + new Size(0,change.Height);
            v_unitCollectionResultStringTextbox.Size = ((Rectangle)v_unitCollectionResultStringTextbox.Tag).Size + new Size(change.Width, 0);
            label1.Location = (Point)label1.Tag + new Size(0,change.Height);
            label2.Location = (Point)label2.Tag + new Size(change.Width / 2, 0);
            UpdateUnitPanels(Step5Info.units.Values);
        }
        private bool updatePanels = true;
        private void v_unitCollectionResultStringTextbox_TextChanged(object sender, EventArgs e)
        {
            if (updatePanels)
                UpdateUnitPanels(Step5Info.units.Values);
            if (!updatePanels)
                updatePanels = true;
        }
    }
}
