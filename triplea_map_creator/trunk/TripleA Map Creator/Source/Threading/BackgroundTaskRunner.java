package Threading;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;

import javax.swing.SwingUtilities;

public class BackgroundTaskRunner
{
    public static boolean s_shouldStopBT = false;
    /**
     * 
     * @param parent Parent window
     * @param waitMessage Message displayed to user showing that the program is working
     * @param r - Runnable containing the code that is to be run
     * @param cancelR - If true, the user is able to cancel the task
     * @return Whether the task completed (only returns false if the user canceled the task)
     */
    public static boolean runInBackground(Component parent, String waitMessage, final Runnable r, boolean cancelR)
    {
        s_shouldStopBT = false;

        if(!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Wrong thread");

        Thread t = null;

        Action cRunner = new AbstractAction("Cancel")
        {
            public void actionPerformed(ActionEvent e)
            {
                s_shouldStopBT = true;
            }
        };

        final WaitDialog window = new WaitDialog(parent, waitMessage, cancelR ? cRunner : null);

        final AtomicBoolean doneWait = new AtomicBoolean(false);
        
        
        t = new Thread(new Runnable()
        {        
            public void run()
            {
                try
                {
                    r.run();
                }
                finally
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {                    
                        public void run()
                        {
                            doneWait.set(true);
                            window.setVisible(false);
                            window.dispose();
                        }                    
                    });
                    
                }
            }
        
        });
        t.start();
        
        if(!doneWait.get())
        {
            window.pack();
            window.setLocationRelativeTo(parent);
            window.setVisible(true);
        }
        
        return !s_shouldStopBT;
    }    
}
