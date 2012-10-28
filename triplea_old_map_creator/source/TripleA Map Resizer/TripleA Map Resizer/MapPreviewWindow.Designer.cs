using System.Windows.Forms;
namespace TripleA_Map_Resizer
{
    partial class MapPreviewWindow
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MapPreviewWindow));
            this.drawPanel = new DrawingPanel();
            this.SuspendLayout();
            // 
            // drawPanel
            // 
            this.drawPanel.BackColor = System.Drawing.SystemColors.ControlLight;
            this.drawPanel.Location = new System.Drawing.Point(0, 0);
            this.drawPanel.Name = "drawPanel";
            this.drawPanel.Size = new System.Drawing.Size(200, 100);
            this.drawPanel.TabIndex = 0;
            this.drawPanel.MouseMove += new System.Windows.Forms.MouseEventHandler(this.mouseMove);
            this.drawPanel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.mouseDown);
            this.drawPanel.MouseUp += new System.Windows.Forms.MouseEventHandler(this.mouseUp);
            // 
            // MapPreviewWindow
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.ClientSize = new System.Drawing.Size(642, 516);
            this.Controls.Add(this.drawPanel);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "MapPreviewWindow";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Map Preview Window";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MapPreviewWindow_FormClosing);
            this.ResumeLayout(false);

        }

        #endregion

        private DrawingPanel drawPanel;
    }
    public class DrawingPanel : Panel
    {
        public DrawingPanel()
        {
            this.SetStyle(ControlStyles.AllPaintingInWmPaint, true);
            this.SetStyle(ControlStyles.UserPaint, true);
            this.SetStyle(ControlStyles.DoubleBuffer, true);
            this.UpdateStyles();
        }
    }
}