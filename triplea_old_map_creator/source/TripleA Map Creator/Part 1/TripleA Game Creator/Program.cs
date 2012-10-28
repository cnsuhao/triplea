using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace TripleA_Game_Creator
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            GC.Collect();
            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new Form1());
            }
            catch (Exception ex) { new ExceptionViewer().ShowInformationAboutException(ex, false); }
            GC.Collect();
        }
    }
}
