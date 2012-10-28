using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TripleA_Map_Resizer
{
    public partial class MapPreviewWindow : Form
    {
        public MapPreviewWindow()
        {
            InitializeComponent();
            MouseDown += new MouseEventHandler(mouseDown);
            MouseMove += new MouseEventHandler(mouseMove);
            MouseUp += new MouseEventHandler(mouseUp);
            this.Cursor = Cursors.Hand;
            this.SetStyle(ControlStyles.AllPaintingInWmPaint, true);
            this.UpdateStyles();
        }

        private void MapPreviewWindow_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
            Hide();
        }
        protected override void OnPaint(PaintEventArgs e)
        {
            //base.OnPaint(e);
        }
        protected override void OnPaintBackground(PaintEventArgs e)
        {
            if (!down)
                base.OnPaintBackground(e);
        }
        public void DisplayImage(Image image)
        {
            drawPanel.BackgroundImage = image;
            drawPanel.BackgroundImageLayout = ImageLayout.None;
            drawPanel.Size = image.Size;
            this.Show();
        }
        bool down = false;
        Point omLocation = new Point();
        Point oLocation = new Point();
        private void mouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                omLocation = new Point(Main.MousePosition.X, Main.MousePosition.Y);
                oLocation = new Point(-this.AutoScrollPosition.X, -this.AutoScrollPosition.Y);
                down = true;
                //this.Cursor = Cursors.Hand;
            }
        }

        private void mouseMove(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                if (down)
                {
                    this.AutoScrollPosition = new Point(oLocation.X + omLocation.X - Main.MousePosition.X, oLocation.Y + omLocation.Y - Main.MousePosition.Y);
                }
            }
        }
        private void mouseUp(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                down = false;
                //this.Cursor = Cursors.Default;
            }
        }
    }
}
