package games.strategy.engine.framework.ui;

import games.strategy.engine.data.GameParseException;
import games.strategy.engine.framework.GameRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.DefaultListModel;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class NewGameChooserModel extends DefaultListModel
{

    private static final Logger log = Logger.getLogger(NewGameChooserModel.class.getName());
    
    public NewGameChooserModel()
    {
        populate();
    }

    public NewGameChooserEntry get(int i)
    {
        return (NewGameChooserEntry) super.get(i);
    }
    
    private List<File> allMapFiles() {
    	List<File> rVal = new ArrayList<File>();
    	rVal.addAll(Arrays.asList(new File(GameRunner.getRootFolder(), "maps").listFiles()));
    	rVal.addAll(Arrays.asList(GameRunner.getUserMapsFolder().listFiles()));
    	return rVal;
    }
    
    private void populate()
    {
        
        List<NewGameChooserEntry> entries = new ArrayList<NewGameChooserEntry>();
        for(File map : allMapFiles()) 
        {
            if(map.isDirectory()) 
            {
                populateFromDirectory(map, entries);
            }
            else if(map.isFile() && map.getName().toLowerCase().endsWith(".zip")) 
            {
                populateFromZip(map, entries);
            }
        }     
        
        //remove any null entries
        do {} while(entries.remove(null));
        
        Collections.sort(entries, new Comparator<NewGameChooserEntry>()
        {
        
            public int compare(NewGameChooserEntry o1, NewGameChooserEntry o2)
            {
                return o1.getGameData().getGameName().toLowerCase().compareTo(o2.getGameData().getGameName().toLowerCase());
            }
        
        });
        for(NewGameChooserEntry entry : entries) 
        {
            addElement(entry);
        }
    }
    
    private void populateFromZip(File map, List<NewGameChooserEntry> entries)
    {
        try
        {
            FileInputStream fis = new FileInputStream(map);
            try
            {
                ZipInputStream zis = new ZipInputStream(fis);
                ZipEntry entry = zis.getNextEntry();
                while(entry != null) {
                                                   
                    if(entry.getName().startsWith("games/") && entry.getName().toLowerCase().endsWith(".xml")) 
                    {
                        URLClassLoader loader = new URLClassLoader(new URL[] {map.toURL()});
                        URL url = loader.getResource(entry.getName());
                        try
                        {
                            entries.add(createEntry(new URI(url.toString().replace(" ", "%20"))));
                        } catch (Exception e)
                        {
                            System.err.println("Could not parse:" + url);
                            e.printStackTrace();
                        }
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
                
            } finally
            {
                fis.close();
            }
        } catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
            
        
    }
    
    public NewGameChooserEntry findByName(String name) 
    {
        for(int i = 0; i < size(); i++) 
        {
            if(get(i).getGameData().getGameName().equals(name)) {
                return get(i);
            }
        }
        return null;
    }
     
    
    private NewGameChooserEntry createEntry(URI uri) throws IOException, GameParseException, SAXException
    {     
        return new NewGameChooserEntry(uri);     
    }

    private void populateFromDirectory(File mapDir, List<NewGameChooserEntry> entries)
    {
        File games = new File(mapDir, "games");
        if(!games.exists()) 
        {
            //no games in this map dir
            return;
        }
        
        for(File game : games.listFiles()) 
        {
            if(game.isFile() && game.getName().toLowerCase().endsWith("xml")) {
                try
                {
                    NewGameChooserEntry entry = createEntry(game.toURI());
                    entries.add(entry);
                } catch(SAXParseException e) {
                    System.err.println("Could not parse:" + game + " error at line:" + e.getLineNumber() + " column:" + e.getColumnNumber());
                    e.printStackTrace();
                }
                catch(Exception e) {
                    System.err.println("Could not parse:" + game);
                    e.printStackTrace();
                }
                
               
            }
        }

        
    }
    
    
    
}


