/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.UI;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Stephen
 */
public class NotEditableTableModel extends DefaultTableModel
{
    @Override
    public boolean isCellEditable(int x, int y)
    {
        return false;
    }

    public NotEditableTableModel(Object[][] data, Object[] columns)
    {
        super(data, columns);
    }
}
