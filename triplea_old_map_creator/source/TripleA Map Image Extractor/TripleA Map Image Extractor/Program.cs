using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Drawing;
using System.IO;
using System.Threading;
using System.Net;

namespace TripleA_Map_Image_Extractor
{
    static class Program
    {
        static ProgressBarWindow barW;
        [STAThread]
        static void Main(string[] args)
        {
            GC.Collect();
            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                InitializeForms();
                CheckForUpdates();
                barW.Opacity = 0;
                barW.Show();
                Size mapSize = new Size();
                FolderBrowserDialog open = new FolderBrowserDialog();
                open.ShowNewFolderButton = true;
                WriteLine("Please specify the folder containing the base tiles for the map.");
                open.Description = "Please specify the folder containing the base tiles for the map. You can usually find it in the 'baseTiles' folder in the map's folder.";
                if (open.ShowDialog() != DialogResult.Cancel)
                {
                    if (new DirectoryInfo(open.SelectedPath).GetDirectories().Length > 0)
                    {
                        foreach (DirectoryInfo cur in new DirectoryInfo(open.SelectedPath).GetDirectories())
                        {
                            if (cur.Name.ToLower() == "basetiles")
                                open.SelectedPath = cur.FullName;
                        }
                    }
                    mapSize = getMapSize(new DirectoryInfo(open.SelectedPath));
                    barW.progressBar1.Value = 0;
                    List<FileInfo> files = new List<FileInfo>(new DirectoryInfo(open.SelectedPath).GetFiles());
                    List<FileInfo> images = new List<FileInfo>();
                    foreach (FileInfo cur in files)
                    {
                        if (cur.Extension.ToLower() == ".png")
                        {
                            images.Add(cur);
                        }
                    }
                    barW.Opacity = 100;
                    barW.progressBar1.Maximum = images.Count;
                    WriteLine("The program will now form the base tiles into one image...");
                    Image fullImage = new Bitmap(mapSize.Width, mapSize.Height);
                    Graphics grphx = Graphics.FromImage(fullImage);
                    foreach (FileInfo image in images)
                    {
                        barW.progressBar1.Value++;
                        int x = Convert.ToInt32(image.Name.Substring(0, image.Name.IndexOf("_")));
                        int y = Convert.ToInt32(image.Name.Substring(image.Name.IndexOf("_") + 1, image.Name.Substring(image.Name.IndexOf("_") + 1).IndexOf(".")));
                        Image imageToPaste = Image.FromFile(image.FullName);
                        Point pasteLoc = new Point(x * 256, y * 256);
                        WriteLine(String.Concat("Drawing base tile ", x, ",", y, " to the map image..."));
                        grphx.DrawImage(imageToPaste, pasteLoc);
                        imageToPaste.Dispose();
                    }
                    barW.Opacity = 0;
                    WriteLine("Done forming image. Please select save location.");
                    SaveFileDialog save = new SaveFileDialog();
                    save.DefaultExt = ".png";
                    save.Filter = "Png Image Files|*.png|All files (*.*)|*.*";
                    save.Title = "Please select location to save map image file to.";
                    if (save.ShowDialog() != DialogResult.Cancel)
                    {
                        fullImage.Save(save.FileName, System.Drawing.Imaging.ImageFormat.Png);
                        WriteLine("Map image saved to " + save.FileName);
                    }
                    else
                    {
                        WriteLine("Save location not specified. The program will now shut down.");
                    }
                }
                else
                {
                    WriteLine("Folder not specified. The program will now shut down.");
                }
                barW.Opacity = 0;
            }
            catch (Exception ex) { if (MessageBox.Show("An error occured when running the Map Image Extractor. Make sure you selected the correct folder and try again. Do you want to view the error message?", "Error Occurred", MessageBoxButtons.YesNoCancel) == DialogResult.Yes) { exceptionViewerWindow.ShowInformationAboutException(ex, false); } }
            GC.Collect();
        }

        private static void InitializeForms()
        {
            barW = new ProgressBarWindow();
            exceptionViewerWindow = new ExceptionViewer();
        }
        public static ExceptionViewer exceptionViewerWindow;
        public static Size getMapSize(DirectoryInfo baseTilesFolder)
        {
            Size result = new Size();
            if (File.Exists(baseTilesFolder.Parent.FullName + @"\map.properties"))
            {
                string[] lines = File.ReadAllLines(baseTilesFolder.Parent.FullName + @"\map.properties");
                foreach (string cur in lines)
                {
                    if (cur.ToLower().Contains("map.width="))
                    {
                        result.Width = Convert.ToInt32(cur.ToLower().Substring(cur.ToLower().IndexOf(".width=") + 7));
                    }
                    if (cur.ToLower().Contains("map.height="))
                    {
                        result.Height = Convert.ToInt32(cur.ToLower().Substring(cur.ToLower().IndexOf(".height=") + 8));
                    }
                }
            }
            else
            {
                OpenFileDialog open = new OpenFileDialog();
                open.CheckFileExists = true;
                open.DefaultExt = ".properties";
                open.Filter = "Map Properties Files|*.properties|All files (*.*)|*.*";
                open.InitialDirectory = baseTilesFolder.Parent.FullName;
                open.Multiselect = false;
                open.Title = "Please select the map.properties file for the map.";
                if (open.ShowDialog() != DialogResult.Cancel)
                {
                    string[] lines = File.ReadAllLines(open.FileName);
                    foreach (string cur in lines)
                    {
                        if (cur.Contains("map.width="))
                        {
                            result.Width = Convert.ToInt32(cur.ToLower().Substring(cur.ToLower().IndexOf(".width=") + 7));
                        }
                        if (cur.Contains("map.height="))
                        {
                            result.Height = Convert.ToInt32(cur.ToLower().Substring(cur.ToLower().IndexOf(".height=") + 8));
                        }
                    }
                }
            }
            return result;
        }
        public static void WriteLine(string line)
        {
            Console.WriteLine(line);
        }
        private static Version usersVersion = new Version(1,0,1,5);
        public static void CheckForUpdates()
        {
            Thread t = new Thread(new ThreadStart(update));
            t.Priority = ThreadPriority.Lowest;
            t.IsBackground = true;
            t.Start();
        }
        private static void update()
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
    }
}
