/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Walkthrough.WalkthroughData;

import MainProgram.Walkthrough.WalkthroughStep;

/**
 *
 * @author Stephen
 */
public class WalkthroughData implements java.io.Serializable
{
    private WalkthroughOptions m_options = null;
    private WalkthroughStep m_farthestStepReached = null;
    private WalkthroughStep m_currentStep = null;
    private int m_nextInstructionsIndex = 0;

    public WalkthroughData(WalkthroughOptions options, WalkthroughStep startStep)
    {
        this(options, startStep, startStep);
    }
    public WalkthroughData(WalkthroughOptions options, WalkthroughStep startStep, WalkthroughStep farthestStep)
    {
        m_options = options;
        m_currentStep = startStep;
        m_farthestStepReached = farthestStep;
    }
    public void SetCurrentStep(WalkthroughStep step)
    {
        SetCurrentStep(step, true);
    }
    public void SetCurrentStep(WalkthroughStep step, boolean resetInsIndex)
    {
        if(resetInsIndex && !m_currentStep.equals(step))
            m_nextInstructionsIndex = 0;
        m_currentStep = step;
        if (step.GetSegment() > m_farthestStepReached.GetSegment() || (step.GetSegment() == m_farthestStepReached.GetSegment() && step.GetStep() > m_farthestStepReached.GetStep()))
        {
            m_farthestStepReached = step;
        }
    }
    public WalkthroughStep GetCurrentStep()
    {
        return m_currentStep;
    }
    public WalkthroughStep GetFarthestStepReached()
    {
        return m_farthestStepReached;
    }
    public WalkthroughOptions GetWalkthroughOptions()
    {
        return m_options;
    }
    public void SetWalkthroughOptions(WalkthroughOptions options)
    {
        m_options = options;
    }
    public int GetNextInstructionsIndex()
    {
        return m_nextInstructionsIndex;
    }
    public void SetNextInstructionsIndex(int index)
    {
        m_nextInstructionsIndex = index;
    }
}
