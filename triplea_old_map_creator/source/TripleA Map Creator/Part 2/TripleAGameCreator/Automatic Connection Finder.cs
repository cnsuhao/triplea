using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.IO;
using System.Drawing.Drawing2D;

namespace TripleAGameCreator
{
    public partial class Automatic_Connection_Finder : Form
    {
        public Automatic_Connection_Finder()
        {
            InitializeComponent();
            Automatic_Connection_Finder.CheckForIllegalCrossThreadCalls = false;
            SetUpForAnotherScan();
        }
        public Form1 main = null;
        private void button1_Click(object sender, EventArgs e)
        {
            if (!isScanningForConnections)
            {
                button1.Text = "Cancel";
                this.Text = "Territory Connection Scanner - Initializing...";
                isScanningForConnections = true;
                if (v_performIslandSearching.Checked)
                    lineWidth = (Convert.ToInt32(numericUpDown1.Text) * 2) - 1;//Lines with widths more than 1 pixel tend to need more polygon enlarging
                else
                    lineWidth = Convert.ToInt32(numericUpDown1.Text);
                hasCanceled = false;
                numericUpDown1.Enabled = false;
                v_performIslandSearching.Enabled = false;
                scanningThread.Start();
            }
            else
            {
                hasCanceled = true;
                SetUpForAnotherScan();
            }
        }

        public void SetUpForAnotherScan()
        {
            if(scanningThread != null && scanningThread.IsAlive)
                scanningThread.Abort();
            scanningThread = new Thread(new ThreadStart(startFindingConnections));
            scanningThread.Priority = ThreadPriority.Lowest;
            isScanningForConnections = false;
            this.Text = "Territory Connection Scanner";
            button1.Text = "Start";
            numericUpDown1.Enabled = true;
            v_performIslandSearching.Enabled = true;
            toolStripProgressBar1.Value = 0;
        }
        public Thread scanningThread = null;
        public class Territory
        {
            public GraphicsPath p_inflatedPolygon = null;
            public Rectangle p_polygonBounds = new Rectangle(-1,-1,1,1);
            public List<Point> p_originalPolygonPoints = new List<Point>();
            public string name = "";
            public Dictionary<string,string> p_neighbors = new Dictionary<string,string>();
        }
        public List<Territory> territories = new List<Territory>();
        public int lineWidth = 0;
        public bool isScanningForConnections = false;
        public bool hasCanceled = false;
        public void startFindingConnections()
        {
            try
            {
                territories.Clear();
                connections.Clear();
                string[] full;
                Step1Info.LoadedFile.Replace("/", @"\");
                Step1Info.MapImageLocation.Replace("/", @"\");
                if (Step1Info.MapImageLocation.Contains(@"\"))
                {
                    if (File.Exists(Step1Info.MapImageLocation.Substring(0, Step1Info.MapImageLocation.LastIndexOf(@"\")) + "/polygons.txt"))
                        full = File.ReadAllLines(Step1Info.MapImageLocation.Substring(0, Step1Info.MapImageLocation.LastIndexOf(@"\")) + "/polygons.txt");
                    else
                    {
                        if (File.Exists(new FileInfo(Step1Info.MapImageLocation.Substring(0, Step1Info.MapImageLocation.LastIndexOf(@"\"))).Directory.Parent + "/polygons.txt"))
                        {
                            full = File.ReadAllLines(new FileInfo(Step1Info.MapImageLocation.Substring(0, Step1Info.MapImageLocation.LastIndexOf(@"\"))).Directory.Parent + "/polygons.txt");
                        }
                        else
                        {
                            OpenFileDialog open = new OpenFileDialog();
                            open.Title = "Unable to locate the needed polygons file. Please select the 'polygons.txt' file for the map.";
                            open.Filter = "Text Files|*.txt|All files (*.*)|*.*";
                            if (open.ShowDialog(this) != DialogResult.Cancel)
                                full = File.ReadAllLines(open.FileName);
                            else
                            {
                                MessageBox.Show(this, "You need to specify a polygons file for the program to be able to find the connectons.", "Unable To Find Connections");
                                this.Close();
                                return;
                            }
                        }
                    }
                }
                else if (Step1Info.LoadedFile.Contains(@"\"))
                {
                    if (File.Exists(Step1Info.LoadedFile.Substring(0, Step1Info.LoadedFile.LastIndexOf(@"\")) + "/polygons.txt"))
                        full = File.ReadAllLines(Step1Info.LoadedFile.Substring(0, Step1Info.LoadedFile.LastIndexOf(@"\")) + "/polygons.txt");
                    else
                    {
                        if (File.Exists(new FileInfo(Step1Info.LoadedFile.Substring(0, Step1Info.LoadedFile.LastIndexOf(@"\"))).Directory.Parent + "/polygons.txt"))
                        {
                            full = File.ReadAllLines(new FileInfo(Step1Info.LoadedFile.Substring(0, Step1Info.LoadedFile.LastIndexOf(@"\"))).Directory.Parent + "/polygons.txt");
                        }
                        else
                        {
                            OpenFileDialog open = new OpenFileDialog();
                            open.Title = "Unable to locate the needed polygons file. Please select the 'polygons.txt' file for the map.";
                            open.Filter = "Text Files|*.txt|All files (*.*)|*.*";
                            if (open.ShowDialog(this) != DialogResult.Cancel)
                                full = File.ReadAllLines(open.FileName);
                            else
                            {
                                MessageBox.Show(this, "You need to specify a polygons file for the program to be able to find the connectons.", "Unable To Find Connections");
                                this.Close();
                                return;
                            }
                        }
                    }
                }
                else
                {
                    OpenFileDialog open = new OpenFileDialog();
                    open.Title = "Unable to locate the needed polygons file. Please select the 'polygons.txt' file for the map.";
                    open.Filter = "Text Files|*.txt|All files (*.*)|*.*";
                    if (open.ShowDialog(this) != DialogResult.Cancel)
                        full = File.ReadAllLines(open.FileName);
                    else
                    {
                        MessageBox.Show(this, "You need to specify a polygons file for the program to be able to find the connectons.", "Unable To Find Connections");
                        this.Close();
                        return;
                    }
                }
                int gcCollectCountdown = 25;
                bool addPoints = false;
                if (!v_performIslandSearching.Checked)
                    addPoints = MessageBox.Show("Do you want the program to increase the accuracy of the scan by adding reference points to the territory polygons?\r\n\r\nNote: The scan will take longer if you use this feature, but it will also allow the program to be able to find the connections between sea zones.", "Scanning Options", MessageBoxButtons.YesNo) == DialogResult.Yes;
                foreach (string cur in full)
                {
                    if (!cur.Contains("<"))
                        continue;
                    string tName = cur.Substring(0, cur.IndexOf("<")).Trim();
                    if (Step2Info.territories.ContainsKey(tName))
                    {
                        //MessageBox.Show(cur);
                        int curPointIndex = 0;
                        Territory t = new Territory();
                        t.name = tName;
                        List<Point> points = new List<Point>();
                        while (true)
                        {
                            try
                            {
                                curPointIndex = cur.Substring(curPointIndex).IndexOf("(") + curPointIndex;
                                if (curPointIndex > -1)
                                {
                                    string curPointSubstring = cur.Substring(curPointIndex, cur.Substring(curPointIndex).IndexOf(")"));
                                    Point curPoint = new Point(Convert.ToInt32(curPointSubstring.Substring(1, curPointSubstring.IndexOf(",") - 1)), Convert.ToInt32(curPointSubstring.Substring(curPointSubstring.IndexOf(",") + 1, curPointSubstring.Length - (curPointSubstring.IndexOf(",") + 1))));
                                    points.Add(curPoint);
                                    curPointIndex += curPointSubstring.Length;
                                }
                                else
                                {
                                    break;
                                }
                            }
                            catch { break; }
                        }
                        if (!v_performIslandSearching.Checked && addPoints)
                        {
                            points = FillInPointsBetweenListOfPoints(points);
                            gcCollectCountdown--;
                            if (gcCollectCountdown <= 0)
                            {
                                GC.Collect();
                                gcCollectCountdown = 50;
                            }
                        }
                        GraphicsPath curPolygon = new GraphicsPath(points.ToArray(), getPolygonBytes(points));
                        Rectangle inflatedPolygonBounds = Inflate(Rectangle.Round(curPolygon.GetBounds()), lineWidth);
                        if (v_performIslandSearching.Checked)
                        {
                            curPolygon.Transform(new Matrix(curPolygon.GetBounds(), new PointF[] { new PointF(inflatedPolygonBounds.Left, inflatedPolygonBounds.Top), new PointF(inflatedPolygonBounds.Right, inflatedPolygonBounds.Top), new PointF(inflatedPolygonBounds.Left, inflatedPolygonBounds.Bottom) }));
                        }
                        t.p_originalPolygonPoints = points;
                        t.p_inflatedPolygon = curPolygon;
                        t.p_polygonBounds = inflatedPolygonBounds;
                        territories.Add(t);
                    }
                }
                List<string> lines = new List<string>();
                toolStripProgressBar1.Minimum = 0;
                toolStripProgressBar1.Maximum = territories.Count;
                int terIndex = 0;
                int terNum = territories.Count;
                while (terIndex < territories.Count)
                {
                    Territory cur;
                    if (!v_performIslandSearching.Checked)
                        cur = territories[0];
                    else
                        cur = territories[terIndex];
                    toolStripProgressBar1.Value++;
                    this.Text = String.Concat("Territory Connection Scanner - Processing ", toolStripProgressBar1.Value, " Of ", toolStripProgressBar1.Maximum);
                    int index = 0;
                    bool br = false;
                    while (index < territories.Count)
                    {
                        Territory cur2 = territories[index];
                        if (cur2.name != cur.name)
                        {
                            if (cur.p_polygonBounds.IntersectsWith(cur2.p_polygonBounds))
                            {
                                if (v_performIslandSearching.Checked)
                                {
                                    if (!cur.p_neighbors.ContainsKey(cur2.name))
                                    {
                                        foreach (Point point in cur2.p_originalPolygonPoints)
                                        {
                                            if (cur.p_inflatedPolygon.IsVisible(point))
                                            {
                                                connections.Add(new Connection() { t1 = cur, t2 = cur2 });
                                                cur2.p_neighbors.Add(cur.name,cur.name);
                                                break;
                                            }
                                        }
                                    }
                                
                                }
                                else
                                {
                                    foreach (Point p in cur.p_originalPolygonPoints)
                                    {
                                        foreach (Point p2 in cur2.p_originalPolygonPoints)
                                        {
                                            int xDiff = p.X - p2.X;
                                            int yDiff = p.Y - p2.Y;
                                            //if((xDiff > -33 && xDiff < 33 && yDiff > -33 && yDiff < 33))
                                            //MessageBox.Show("Points: " + p.X + "," + p.Y + "|" + p2.X + "," + p2.Y + ". Diff: " + xDiff + "," + yDiff);
                                            if (xDiff > -lineWidth && xDiff < lineWidth && yDiff > -lineWidth && yDiff < lineWidth)
                                            {
                                                connections.Add(new Connection() { t1 = cur, t2 = cur2 });
                                                br = true;
                                                break;
                                            }
                                        }
                                        if (br)
                                        {
                                            br = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        index++;
                    }
                    if (!v_performIslandSearching.Checked)
                        territories.Remove(cur);
                    else
                        terIndex++;
                }
                toolStripProgressBar1.Value = 0;
                this.Text = "Territory Connection Scanner";
                isScanningForConnections = false;
                button1.Text = "Start";
                numericUpDown1.Enabled = true;
                v_performIslandSearching.Enabled = true;
                GC.Collect();
                this.Close();
            }
            catch (ThreadAbortException ex)
            {
                return;
            }
            catch (Exception ex)
            {
                if (connections.Count == 0)
                {
                    if (MessageBox.Show("An error occured trying to find the connections. Make sure the polygons file for the map has no errors in it. (Like misspelling a territory name.) Do you want to view the error message", "Error Occured", MessageBoxButtons.YesNoCancel) == DialogResult.Yes)
                    {
                        exceptionViewerWindow.ShowInformationAboutException(ex, true);
                        SetUpForAnotherScan();
                    }
                }
            }
        }
        ExceptionViewer exceptionViewerWindow = new ExceptionViewer();
        private byte[] getPolygonBytes(List<Point> list)
        {
            byte[] result = new byte[list.Count];
            for (int i = 0; i < list.Count; i++)
            {
                result[i] = 1;
            }
            return result;
        }
        private Rectangle Inflate(Rectangle rect, int inflateAmount)
        {
            return new Rectangle(rect.X - inflateAmount, rect.Y - inflateAmount, rect.Size.Width + inflateAmount * 2, rect.Size.Height + inflateAmount * 2);
        }
        private List<Point> FillInPointsBetweenListOfPoints(List<Point> origPoints)
        {
            List<Point> points = new List<Point>();
            foreach (Point curPoint in origPoints)
            {
                if (points.Count > 0)
                {
                    if((curPoint.X != points[points.Count - 1].X || curPoint.Y != points[points.Count - 1].Y))
                        points.AddRange(GetPointsToAddBetweenTwoPoints(curPoint, points[points.Count - 1]));
                }
                points.Add(curPoint);
            }
            return points;
        }

        private List<Point> GetPointsToAddBetweenTwoPoints(Point startPoint,Point destPoint)
        {
            List<Point> points = new List<Point>();
            Size difference = new Size(destPoint.X - startPoint.X, destPoint.Y - startPoint.Y);
            PointF position = new PointF(startPoint.X,startPoint.Y);
            Ratio difRatioSimple = increaseOrDecreaseRatioUntilSimplified(new Ratio(difference.Width, difference.Height));
            int timesLooped = 0;
            int pixelDifference = (int)(ToPositive((int)(destPoint.X - position.X)) + ToPositive((int)(destPoint.Y - position.Y)));
            int lastPixelDifference = pixelDifference;
            while (pixelDifference > 0)
            {
                SizeF jumpSize = new SizeF(difRatioSimple.xRatio,difRatioSimple.yRatio);
                position += jumpSize;
                Point pointTA = new Point((int)position.X,(int)position.Y);
                points.Add(pointTA);
                pixelDifference = (int)(ToPositive((int)(destPoint.X - position.X)) + ToPositive((int)(destPoint.Y - position.Y)));
                timesLooped++;
                if (pixelDifference > lastPixelDifference)
                    break;
                lastPixelDifference = pixelDifference;
            }
            return points;
        }
        private int ToPositive(int num)
        {
            if (num < 0)
                return -num;
            else
                return num;
        }
        private Ratio increaseOrDecreaseRatioUntilSimplified(Ratio ratio)
        {
            if (ratio.xRatio == 0)
            {
                if (ratio.yRatio > 0)
                    return new Ratio(0, 1);
                else if (ratio.yRatio < 0)
                    return new Ratio(0, -1);
            }
            else if (ratio.yRatio == 0)
            {
                if (ratio.xRatio > 0)
                    return new Ratio(1, 0);
                else if (ratio.xRatio < 0)
                    return new Ratio(-1, 0);
            }
            else
            {
                Ratio result = new Ratio(ratio.xRatio, ratio.yRatio);
                bool xNegative = false;
                bool yNegative = false;
                if (result.xRatio < 0)
                {
                    result.xRatio = -result.xRatio;
                    xNegative = true;
                }
                if (result.yRatio < 0)
                {
                    result.yRatio = -result.yRatio;
                    yNegative = true;
                }
                if (result.xRatio < result.yRatio)
                {
                    double downsizeRatio = 1 / result.xRatio;
                    result.xRatio = 1;
                    result.yRatio = (float)(result.yRatio * downsizeRatio);
                }
                else if (result.yRatio < result.xRatio)
                {
                    double downsizeRatio = 1 / result.yRatio;
                    result.yRatio = 1;
                    result.xRatio = (float)(result.xRatio * downsizeRatio);
                }
                if (xNegative)
                {
                    result.xRatio = -result.xRatio;
                }
                if (yNegative)
                {
                    result.yRatio = -result.yRatio;
                }
                return result;
            }
            return new Ratio(0, 0);
        }
        private class Ratio
        {
            public float xRatio = 0F;
            public float yRatio = 0F;
            public Ratio(float x, float y)
            {
                xRatio = x;
                yRatio = y;
            }
        }
        public List<Connection> connections = new List<Connection>();
        public class Connection
        {
            public Territory t1 = new Territory();
            public Territory t2 = new Territory();
        }

        private void Automatic_Connection_Finder_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (isScanningForConnections)
            {
                e.Cancel = true;
                MessageBox.Show("Please wait for the Territory Connection Scanner to finish running.", "Still Running");
            }
            else
            {
                scanningThread.Abort();
                scanningThread = new Thread(new ThreadStart(startFindingConnections));
                scanningThread.Priority = ThreadPriority.Lowest;
            }
        }
    }
}
