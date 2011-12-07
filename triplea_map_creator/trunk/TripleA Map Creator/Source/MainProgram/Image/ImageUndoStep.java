/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MainProgram.Image;

import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 *
 * @author Stephen
 */
public class ImageUndoStep implements Serializable
{
    public ImageUndoStep(ImageIcon undoImageBlock, ImageIcon redoImageBlock, Rectangle block, Boolean completeBackup, Rectangle fullImageSize)
    {
        UndoImageBlock = undoImageBlock;
        RedoImageBlock = redoImageBlock;
        Block = block;
        CompleteBackup = completeBackup;
        FullImageSize = fullImageSize;
    }
    public Rectangle FullImageSize = null;
    public Boolean CompleteBackup = false;
    public ImageIcon UndoImageBlock = null;
    public ImageIcon RedoImageBlock = null;
    public Rectangle Block = null;
}
