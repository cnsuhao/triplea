/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Walkthrough.WalkthroughData.Instructions.InstructionActions;

import MainProgram.Walkthrough.UI.WalkthroughWindow;

/**
 *
 * @author Stephen
 */
public abstract class BaseInstructionAction implements java.io.Serializable
{
    public BaseInstructionAction()
    {
    }
    public int Execute(WalkthroughWindow window) 
    {
        return 1;
    }
}
