namespace TripleA_Game_Creator
{
    partial class ExceptionViewer
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ExceptionViewer));
            this.label1 = new System.Windows.Forms.Label();
            this.exceptionInformationTB = new System.Windows.Forms.TextBox();
            this.ContinueRunningBTN = new System.Windows.Forms.Button();
            this.QuitApplicationBTN = new System.Windows.Forms.Button();
            this.copyIntoClipboardBTN = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.Font = new System.Drawing.Font("Arial", 9.5F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(2, 5);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(433, 37);
            this.label1.TabIndex = 0;
            this.label1.Text = "An unhandled exeption has occured in the program. The following information can b" +
                "e used to find the cause of the error:";
            // 
            // exceptionInformationTB
            // 
            this.exceptionInformationTB.Location = new System.Drawing.Point(5, 45);
            this.exceptionInformationTB.Multiline = true;
            this.exceptionInformationTB.Name = "exceptionInformationTB";
            this.exceptionInformationTB.ReadOnly = true;
            this.exceptionInformationTB.Size = new System.Drawing.Size(430, 193);
            this.exceptionInformationTB.TabIndex = 1;
            // 
            // ContinueRunningBTN
            // 
            this.ContinueRunningBTN.Location = new System.Drawing.Point(150, 242);
            this.ContinueRunningBTN.Name = "ContinueRunningBTN";
            this.ContinueRunningBTN.Size = new System.Drawing.Size(140, 38);
            this.ContinueRunningBTN.TabIndex = 2;
            this.ContinueRunningBTN.Text = "Continue Running";
            this.ContinueRunningBTN.UseVisualStyleBackColor = true;
            this.ContinueRunningBTN.Click += new System.EventHandler(this.ContinueRunningBTN_Click);
            // 
            // QuitApplicationBTN
            // 
            this.QuitApplicationBTN.Location = new System.Drawing.Point(294, 242);
            this.QuitApplicationBTN.Name = "QuitApplicationBTN";
            this.QuitApplicationBTN.Size = new System.Drawing.Size(140, 38);
            this.QuitApplicationBTN.TabIndex = 2;
            this.QuitApplicationBTN.Text = "Quit Application";
            this.QuitApplicationBTN.UseVisualStyleBackColor = true;
            this.QuitApplicationBTN.Click += new System.EventHandler(this.QuitApplicationBTN_Click);
            // 
            // copyIntoClipboardBTN
            // 
            this.copyIntoClipboardBTN.Location = new System.Drawing.Point(5, 242);
            this.copyIntoClipboardBTN.Name = "copyIntoClipboardBTN";
            this.copyIntoClipboardBTN.Size = new System.Drawing.Size(141, 38);
            this.copyIntoClipboardBTN.TabIndex = 2;
            this.copyIntoClipboardBTN.Text = "Copy Exception Information Into Clipboard";
            this.copyIntoClipboardBTN.UseVisualStyleBackColor = true;
            this.copyIntoClipboardBTN.Click += new System.EventHandler(this.copyIntoClipboardBTN_Click);
            // 
            // ExceptionViewer
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(439, 283);
            this.Controls.Add(this.copyIntoClipboardBTN);
            this.Controls.Add(this.QuitApplicationBTN);
            this.Controls.Add(this.ContinueRunningBTN);
            this.Controls.Add(this.exceptionInformationTB);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "ExceptionViewer";
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "TripleA Map Creator Exception Viewer";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.ExceptionViewer_FormClosing);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox exceptionInformationTB;
        private System.Windows.Forms.Button ContinueRunningBTN;
        private System.Windows.Forms.Button QuitApplicationBTN;
        private System.Windows.Forms.Button copyIntoClipboardBTN;
    }
}