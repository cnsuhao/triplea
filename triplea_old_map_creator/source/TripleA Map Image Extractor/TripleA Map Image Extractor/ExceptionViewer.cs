using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TripleA_Map_Image_Extractor
{
    public partial class ExceptionViewer : Form
    {
        public ExceptionViewer()
        {
            InitializeComponent();
        }
        public void ShowInformationAboutException(Exception ex, bool allowContinue)
        {
            exceptionInformationTB.Text = String.Concat("Base Exception: \r\n\r\n", ex.GetBaseException().GetType().FullName, ": ", ex.GetBaseException().Message, "\r\n", ex.GetBaseException().StackTrace, "\r\n\r\nComplete Exception:\r\n\r\n", ex.GetType().FullName, ": ", ex.Message, "\r\n", ex.StackTrace);
            ContinueRunningBTN.Enabled = allowContinue;
            this.ShowDialog();
        }
        public void ShowInformationAboutException(Exception ex, bool allowContinue, IWin32Window parent)
        {
            exceptionInformationTB.Text = String.Concat("Base Exception: \r\n\r\n", ex.GetBaseException().GetType().FullName, ": ", ex.GetBaseException().Message, "\r\n", ex.GetBaseException().StackTrace, "\r\n\r\nComplete Exception:\r\n\r\n", ex.GetType().FullName, ": ", ex.Message, "\r\n", ex.StackTrace);
            ContinueRunningBTN.Enabled = allowContinue;
            this.ShowDialog(parent);
        }

        private void ContinueRunningBTN_Click(object sender, EventArgs e)
        {
            this.Hide();
        }

        private void QuitApplicationBTN_Click(object sender, EventArgs e)
        {
            Environment.Exit(0);
        }

        private void copyIntoClipboardBTN_Click(object sender, EventArgs e)
        {
            Clipboard.SetText(exceptionInformationTB.Text);
        }

        private void ExceptionViewer_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
        }
    }
}
