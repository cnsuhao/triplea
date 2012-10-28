namespace TripleAGameCreator
{
    partial class UnitAddingWindow
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
            this.v_unitCollectionResultStringTextbox = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.v_doneButton = new System.Windows.Forms.Button();
            this.v_cancelButton = new System.Windows.Forms.Button();
            this.v_unitsPanelsHolderPanel = new System.Windows.Forms.Panel();
            this.label2 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // v_unitCollectionResultStringTextbox
            // 
            this.v_unitCollectionResultStringTextbox.Location = new System.Drawing.Point(59, 247);
            this.v_unitCollectionResultStringTextbox.Multiline = true;
            this.v_unitCollectionResultStringTextbox.Name = "v_unitCollectionResultStringTextbox";
            this.v_unitCollectionResultStringTextbox.Size = new System.Drawing.Size(376, 34);
            this.v_unitCollectionResultStringTextbox.TabIndex = 0;
            this.v_unitCollectionResultStringTextbox.Text = "infantry:1,artillery:159,armour:102,cruiser:91,battleship:234";
            this.v_unitCollectionResultStringTextbox.TextChanged += new System.EventHandler(this.v_unitCollectionResultStringTextbox_TextChanged);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(7, 254);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(52, 17);
            this.label1.TabIndex = 1;
            this.label1.Text = "Result:";
            // 
            // v_doneButton
            // 
            this.v_doneButton.Location = new System.Drawing.Point(114, 288);
            this.v_doneButton.Name = "v_doneButton";
            this.v_doneButton.Size = new System.Drawing.Size(106, 30);
            this.v_doneButton.TabIndex = 2;
            this.v_doneButton.Text = "Done";
            this.v_doneButton.UseVisualStyleBackColor = true;
            this.v_doneButton.Click += new System.EventHandler(this.v_doneButton_Click);
            // 
            // v_cancelButton
            // 
            this.v_cancelButton.Location = new System.Drawing.Point(226, 288);
            this.v_cancelButton.Name = "v_cancelButton";
            this.v_cancelButton.Size = new System.Drawing.Size(106, 30);
            this.v_cancelButton.TabIndex = 2;
            this.v_cancelButton.Text = "Cancel";
            this.v_cancelButton.UseVisualStyleBackColor = true;
            this.v_cancelButton.Click += new System.EventHandler(this.v_cancelButton_Click);
            // 
            // v_unitsPanelsHolderPanel
            // 
            this.v_unitsPanelsHolderPanel.AutoScroll = true;
            this.v_unitsPanelsHolderPanel.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.v_unitsPanelsHolderPanel.Location = new System.Drawing.Point(10, 26);
            this.v_unitsPanelsHolderPanel.Name = "v_unitsPanelsHolderPanel";
            this.v_unitsPanelsHolderPanel.Size = new System.Drawing.Size(425, 215);
            this.v_unitsPanelsHolderPanel.TabIndex = 3;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label2.Location = new System.Drawing.Point(171, 5);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(105, 17);
            this.label2.TabIndex = 4;
            this.label2.Text = "Available Units:";
            // 
            // UnitAddingWindow
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(447, 326);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.v_unitsPanelsHolderPanel);
            this.Controls.Add(this.v_cancelButton);
            this.Controls.Add(this.v_doneButton);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.v_unitCollectionResultStringTextbox);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.MinimumSize = new System.Drawing.Size(455, 360);
            this.Name = "UnitAddingWindow";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Changing Territory ???\'s Units";
            this.Resize += new System.EventHandler(this.UnitAddingWindow_Resize);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox v_unitCollectionResultStringTextbox;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button v_doneButton;
        private System.Windows.Forms.Button v_cancelButton;
        private System.Windows.Forms.Panel v_unitsPanelsHolderPanel;
        private System.Windows.Forms.Label label2;
    }
}