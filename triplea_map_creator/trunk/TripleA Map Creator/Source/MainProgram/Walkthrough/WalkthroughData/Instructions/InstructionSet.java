/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Walkthrough.WalkthroughData.Instructions;

import MainProgram.Walkthrough.UI.WalkthroughWindow;
import MainProgram.Walkthrough.WalkthroughData.Instructions.InstructionActions.BaseInstructionAction;
import java.util.TreeMap;

/**
 *
 * @author Stephen
 */
public class InstructionSet implements java.io.Serializable
{
    private int m_nextReadIndex = 0;
    public InstructionSet(TreeMap<String, BaseInstructionAction> instructionsAndActions)
    {
        m_instructionsAndActions = instructionsAndActions;
    }
    private TreeMap<String, BaseInstructionAction> m_instructionsAndActions = new TreeMap<String, BaseInstructionAction>();

    private String GetInstructionsFromSet(int index)
    {
        return (String)m_instructionsAndActions.keySet().toArray()[index];
    }
    public boolean AreThereInstructionsAtIndex(int index)
    {
        return index >= 0 && m_instructionsAndActions.size() > index;
    }
    private int ExecuteInstructionAction(WalkthroughWindow window, int index)
    {
        if(((BaseInstructionAction)m_instructionsAndActions.values().toArray()[index]) != null)
            return ((BaseInstructionAction)m_instructionsAndActions.values().toArray()[index]).Execute(window);
        else
            return -2;
    }
    public String ReadNextInstructionPortion()
    {
        if(!AreThereInstructionsAtIndex(m_nextReadIndex))
            return null;
        String result = GetInstructionsFromSet(m_nextReadIndex);
        m_nextReadIndex++;
        return result;
    }
    public int ExecuteCurrentAction(WalkthroughWindow window)
    {
        return ExecuteInstructionAction(window, m_nextReadIndex);
    }
    public int GetNextInstructionsIndex()
    {
        return m_nextReadIndex;
    }
    public void SetNextInstructionsIndex(int index)
    {
        m_nextReadIndex = index;
    }
}
