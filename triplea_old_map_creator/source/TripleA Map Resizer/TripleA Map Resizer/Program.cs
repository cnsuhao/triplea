using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace TripleA_Map_Resizer
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
            ExceptionViewer exViewer = null;
            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                exViewer = new ExceptionViewer();
                Application.Run(new Main());
            }
            catch (Exception ex) {exViewer.ShowInformationAboutException(ex, false); }
            GC.Collect();
        }
    }
}
