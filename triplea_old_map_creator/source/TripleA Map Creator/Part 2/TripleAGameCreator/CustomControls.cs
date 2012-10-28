using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace TripleAGameCreator
{
    public class TextBoxWithErrorProvider : TextBox
    {
        public TextBoxWithErrorProvider(string errProviderString,ErrorIconAlignment errIconAllignment)
        {
            errProviderStringToShow = errProviderString;
            errProvider.SetIconAlignment(this, errIconAllignment);
            errProvider.Tag = this;
        }
        public void ShowErrorProvider()
        {
            errProvider.SetError(this, errProviderStringToShow);
        }
        public string errProviderStringToShow = "";
        public ErrorProvider errProvider = new ErrorProvider();
    }
}
