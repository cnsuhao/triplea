/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

/**
 *
 * @author Stephen
 */
public class FolderFinderTest
{
    public static void main(String[] args)
    {
        String test1 = FolderFinder.getProgramLaunchFolder().getPath();
        String test2 = FolderFinder.getProgramRuntimeFolder_Simple().getPath();
        String test3 = FolderFinder.getProgramRuntimeFolder().getPath();
        String test4 = FolderFinder.getProgramRuntimeFolder_Advanced().getPath();
        
        //Set breakpoint here
        String finalTest = "";
    }
}
