/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package MainProgram.Map;

import Global.Constants;
import MainProgram.Console.ErrorConsole;
import MainProgram.Map.Player.PlayerData;
import MainProgram.Map.Territory.TerritoryData;
import MainProgram.Map.Unit.PSUnitType;
import MainProgram.Map.Unit.PlayerSpecificUnitData;
import MainProgram.Map.Unit.UnitCollection;
import MainProgram.Map.Unit.UnitData;
import MainProgram.Map.Unit.UnitProperty;
import MainProgram.Walkthrough.UI.StepContentPanels.Segment2Step4ContentPanel_All;
import MainProgram.Walkthrough.UI.StepContentPanels.Segment2Step6ContentPanel_All;
import Threading.BackgroundTaskRunner;
import Utils.Others;
import Utils.Strings;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;

/**
 *
 * @author Stephen
 */
public class MapDataExporter
{
    public static String LAST_EXPORT_FOLDER = "Last Export Folder";
    public static void SetLastExportFolder(File folder)
    {
        Preferences.userNodeForPackage(MapDataExporter.class).put(LAST_EXPORT_FOLDER, folder.getPath());
    }
    public static File GetLastExportFolder()
    {
        return new File(Preferences.userNodeForPackage(MapDataExporter.class).get(LAST_EXPORT_FOLDER, System.getProperties().getProperty("user.dir")));
    }
    
    public MapDataExporter()
    {
    }
    
    public void ExportMapToFolder(final MapData map, final File folder, boolean showProgress, Component progressWindowParent)
    {
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                ExportMapBaseToFolder(map, folder, false, null);

                File gamesFolder = new File(folder, "games");
                ExportMapXMLToFile(map, new File(gamesFolder, map.GetMapName() + ".xml"), false, null);
            }
        };
        if(showProgress)
            BackgroundTaskRunner.runInBackground(progressWindowParent, "Exporting map to folder...", runner, true);
        else
            runner.run();
    }
    
    public void ExportMapBaseToFolder(final MapData map, final File folder, boolean showProgress, Component progressWindowParent)
    {
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    folder.mkdirs();

                    File baseTilesFolder = new File(folder, "baseTiles");
                    baseTilesFolder.mkdirs();

                    Image image = map.GetBaseMapImage().getImage();
                    GraphicsConfiguration m_localGraphicSystem = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                    for (int x = 0; (x) * Constants.TILE_SIZE < image.getWidth(null); x++)
                    {
                        for (int y = 0; (y) * Constants.TILE_SIZE < image.getHeight(null); y++)
                        {
                            Rectangle bounds = new Rectangle(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);

                            BufferedImage tile = m_localGraphicSystem.createCompatibleImage(Constants.TILE_SIZE, Constants.TILE_SIZE, Transparency.TRANSLUCENT);
                            tile.getGraphics().drawImage(image, 0, 0, Constants.TILE_SIZE, Constants.TILE_SIZE, bounds.x, bounds.y, bounds.x + Constants.TILE_SIZE, bounds.y + Constants.TILE_SIZE, null);

                            String filename = x + "_" + y + ".png";
                            ImageIO.write(tile, "png", new File(baseTilesFolder, filename));
                        }
                    }
                    
                    File flagsFolder = new File(folder, "flags");
                    flagsFolder.mkdirs();
                    
                    for(PlayerData player : map.GetPlayersData().values())
                    {
                        BufferedImage flag = player.GetFlag();
                        ImageIO.write(flag, "png", new File(flagsFolder, player.GetName() + ".png"));
                    }
                    
                    File unitsFolder = new File(folder, "units");
                    unitsFolder.mkdirs();
                    
                    for(PlayerData player : map.GetPlayersData().values())
                    {
                        File PSUnitFolder = new File(unitsFolder, player.GetName());
                        PSUnitFolder.mkdirs();
                        
                        for(UnitData unit : map.GetUnitsData().values())
                        {
                            if (unit.GetPlayerSpecificUnitData().containsKey(player.GetName()))
                            {
                                BufferedImage unitImage = unit.GetPlayerSpecificUnitData().get(player.GetName()).GetImage();
                                ImageIO.write(unitImage, "png", new File(PSUnitFolder, unit.GetName() + ".png"));
                            }
                        }
                    }

                    
                    
                    StringBuilder centers = new StringBuilder();                    
                    for(Entry<String, Point> entry : map.GetTDefinitions().entrySet())
                        centers.append(Strings.Format_NL("{0}  ({1}, {2})", entry.getKey(), entry.getValue().x, entry.getValue().y));

                    File centersFile = new File(folder, "centers.txt");                    
                    FileWriter centersWriter = new FileWriter(centersFile);
                    try
                    {
                        centersWriter.write(centers.toString());
                    }
                    finally
                    {
                        centersWriter.close();
                    }
                    
                    
                    
                    StringBuilder mapProps = new StringBuilder();                    
                    mapProps.append(Strings.Format_NL("#Color settings for the map, values must be in 6 digit hex form"));
                    
                    for(PlayerData player : map.GetPlayersData().values())
                        mapProps.append(Strings.Format_NL("color.{0}={1}", player.GetName(), Others.ColorToHex(player.GetColor())));
                    
                    mapProps.append(Strings.Format_NL(""));
                    mapProps.append(Strings.Format_NL("units.scale={0}", map.GetUnitScale()));
                    mapProps.append(Strings.Format_NL("map.width={0}", map.GetBaseMapImage().getIconWidth()));
                    mapProps.append(Strings.Format_NL("map.height={0}", map.GetBaseMapImage().getIconHeight()));
                    mapProps.append(Strings.Format_NL("map.scrollWrapX={0}", map.GetScrollWrapOption() == ScrollWrapOption.Horizontal || map.GetScrollWrapOption() == ScrollWrapOption.Both ? "true" : "false"));
                    mapProps.append(Strings.Format_NL("map.scrollWrapY={0}", map.GetScrollWrapOption() == ScrollWrapOption.Vertical || map.GetScrollWrapOption() == ScrollWrapOption.Both ? "true" : "false"));
                    
                    mapProps.append(Strings.Format_NL(""));
                    mapProps.append(Strings.Format_NL("map.hasRelief=false"));
                    mapProps.append(Strings.Format_NL("map.showCapitolMarkers=false"));
                    
                    
                    mapProps.append(Strings.Format_NL(""));
                    mapProps.append(Strings.Format_NL("map.showTerritoryNames={0}", map.GetShowTerritoryNames()));
                    mapProps.append(Strings.Format_NL("map.showConvoyNames=false"));

                    File mapPropsFile = new File(folder, "map.properties");                    
                    FileWriter mapPropsWriter = new FileWriter(mapPropsFile);
                    try
                    {
                        mapPropsWriter.write(mapProps.toString());
                    }
                    finally
                    {
                        mapPropsWriter.close();
                    }
                    
                    
                    
                    StringBuilder unitDrawPoints = new StringBuilder();                    
                    
                    for (String ter : map.GetTDefinitions().keySet())
                    {
                        unitDrawPoints.append(Strings.Format("{0}  ", ter));
                        for(Point point : map.GetTUnitDrawPoints().get(ter))
                            unitDrawPoints.append(Strings.Format("({0},{1})  ", point.x, point.y));
                        unitDrawPoints.append(Strings.Format("\r\n"));
                    }
                    
                    File placeFile = new File(folder, "place.txt");                    
                    FileWriter placeWriter = new FileWriter(placeFile);
                    try
                    {
                        placeWriter.write(unitDrawPoints.toString());
                    }
                    finally
                    {
                        placeWriter.close();
                    }
                    
                    
                    
                    StringBuilder polygons = new StringBuilder();                    
                    
                    for (String ter : map.GetTDefinitions().keySet())
                    {
                        polygons.append(Strings.Format("{0}  ", ter));
                        for(Polygon poly : map.GetTPolygons().get(ter))
                        {
                            polygons.append(Strings.Format("<  "));
                            for(int n = 0;n < poly.npoints;n++)
                                polygons.append(Strings.Format("({0},{1})  ", poly.xpoints[n], poly.ypoints[n]));
                            polygons.append(Strings.Format("  >  "));
                        }
                        polygons.append(Strings.Format("\r\n"));
                    }
                    
                    File polygonsFile = new File(folder, "polygons.txt");                    
                    FileWriter polygonsWriter = new FileWriter(polygonsFile);
                    try
                    {
                        polygonsWriter.write(polygons.toString());
                    }
                    finally
                    {
                        polygonsWriter.close();
                    }
                    
                    
                    float scale = (float) (map.GetMinimapScalePercentage() / 100.0F);

                    Dimension fullsize = new Dimension(map.GetBaseMapImage().getIconWidth(), map.GetBaseMapImage().getIconHeight());
                    Image scaledMinimap = map.GetBaseMapImage().getImage().getScaledInstance((int) (fullsize.width * scale), (int) (fullsize.height * scale), BufferedImage.SCALE_SMOOTH);
                    BufferedImage finalMinimap = new BufferedImage(scaledMinimap.getWidth(null), scaledMinimap.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    finalMinimap.getGraphics().drawImage(scaledMinimap, 0, 0, null);
                    
                    File minimapFile = new File(folder, "smallMap.jpeg");
                    ImageIO.write(finalMinimap, "jpeg", minimapFile);
                }
                catch (IOException ex)
                {
                    ErrorConsole.getConsole().appendError(ex);
                }
            }
        };
        if (showProgress)
            BackgroundTaskRunner.runInBackground(progressWindowParent, "Exporting map base to folder...", runner, true);
        else
            runner.run();
    }
    
    public void ExportMapXMLToFile(final MapData map, final File file, boolean showProgress, Component progressWindowParent)
    {
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String xml = GenerateMapXML(map);

                    file.getParentFile().mkdirs();
                    FileWriter writer = new FileWriter(file);
                    try
                    {
                        writer.write(xml);
                    }
                    finally
                    {
                        writer.close();
                    }
                }
                catch (IOException ex)
                {
                    ErrorConsole.getConsole().appendError(ex);
                }
            }
        };
        if (showProgress)
            BackgroundTaskRunner.runInBackground(progressWindowParent, "Exporting map xml...", runner, true);
        else
            runner.run();
    }

    public String GenerateMapXML(final MapData map)
    {
        try
        {
            StringBuilder builder = new StringBuilder();

            //Btw, we use Strings.Format every time so that the xml lines up and is easier to read
            builder.append(Strings.Format_NL("<?xml version=\"1.0\" ?>"));
            builder.append(Strings.Format_NL("<!DOCTYPE game SYSTEM \"game.dtd\">"));
            builder.append(Strings.Format_NL("<game>"));

            builder.append(Strings.Format_NL("    <info name=\"{0}\" version=\"{1}\"/>", map.GetMapName(), map.GetMapVersion().toString()));
            builder.append(Strings.Format_NL("    <loader javaClass=\"games.strategy.triplea.TripleA\"/>"));
            builder.append(Strings.Format_NL("    <map>"));

            builder.append(Strings.Format_NL("        <!-- Territory Definitions -->"));
            for (TerritoryData ter : map.GetTData().values())
            {
                if (ter.IsWater())
                    builder.append(Strings.Format_NL("        <territory name=\"{0}\" water=\"true\"/>", ter.GetName()));
                else
                    builder.append(Strings.Format_NL("        <territory name=\"{0}\"/>", ter.GetName()));
            }

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <!-- Territory Connections -->"));
            for (Entry<String, TreeSet<String>> conSet : map.GetTConnections().entrySet())
            {
                for (String other : conSet.getValue())
                    builder.append(Strings.Format_NL("        <connection t1=\"{0}\" t2=\"{1}\"/>", conSet.getKey(), other));
            }
            builder.append(Strings.Format_NL("    </map>"));

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <resourceList>"));
            builder.append(Strings.Format_NL("        <resource name=\"PUs\"/>"));
            builder.append(Strings.Format_NL("    </resourceList>"));
            
            String[] playerNames_InOrder = new String[map.GetPlayersData().size()];
            for(PlayerData player : map.GetPlayersData().values())
                playerNames_InOrder[player.GetOrderIndex()] = player.GetName();

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <playerList>"));
            builder.append(Strings.Format_NL("        <!-- In Turn Order -->"));
            for (String playerName : playerNames_InOrder)
                builder.append(Strings.Format_NL("        <player name=\"{0}\" optional=\"false\"/>", playerName));

            builder.append(Strings.Format_NL(""));
            for (PlayerData player : map.GetPlayersData().values())
            {
                for (String alliance : player.GetJoinedAlliances().keySet())
                    builder.append(Strings.Format_NL("        <alliance player=\"{0}\" alliance=\"{1}\"/>", player.GetName(), alliance));
            }
            builder.append(Strings.Format_NL("    </playerList>"));

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <unitList>"));
            for (UnitData unit : map.GetUnitsData().values())
                builder.append(Strings.Format_NL("        <unit name=\"{0}\"/>", unit.GetName()));
            builder.append(Strings.Format_NL("    </unitList>"));

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <gamePlay>"));
            builder.append(Strings.Format_NL("        <delegate name=\"initDelegate\" javaClass=\"games.strategy.triplea.delegate.InitializationDelegate\" display=\"Initializing Delegates\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"bid\" javaClass=\"games.strategy.triplea.delegate.BidPurchaseDelegate\" display=\"Bid Purchase\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"placeBid\" javaClass=\"games.strategy.triplea.delegate.BidPlaceDelegate\" display=\"Bid Placement\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"tech\" javaClass=\"games.strategy.triplea.delegate.TechnologyDelegate\" display=\"Research Technology\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"tech_activation\" javaClass=\"games.strategy.triplea.delegate.TechActivationDelegate\" display=\"Activate Technology\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"purchase\" javaClass=\"games.strategy.triplea.delegate.PurchaseDelegate\" display=\"Purchase Units\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"purchaseNoPU\" javaClass=\"games.strategy.triplea.delegate.NoPUPurchaseDelegate\" display=\"Purchase Units\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"move\" javaClass=\"games.strategy.triplea.delegate.MoveDelegate\" display=\"Combat Move\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"battle\" javaClass=\"games.strategy.triplea.delegate.BattleDelegate\" display=\"Combat\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"place\" javaClass=\"games.strategy.triplea.delegate.PlaceDelegate\" display=\"Place Units\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"endTurn\" javaClass=\"games.strategy.triplea.delegate.EndTurnDelegate\" display=\"Turn Complete\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"endTurnNoPU\" javaClass=\"games.strategy.triplea.delegate.NoPUEndTurnDelegate\" display=\"Turn Complete\"/>"));
            builder.append(Strings.Format_NL("        <delegate name=\"endRound\" javaClass=\"games.strategy.triplea.delegate.EndRoundDelegate\" display=\"Round Complete\"/>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <sequence>"));            
            builder.append(Strings.Format_NL("            <step name=\"gameInitDelegate\" delegate=\"initDelegate\" maxRunCount=\"1\"/>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("            <!-- Bidding Phase -->"));            
            for(String playerName : map.GetPlayersData().keySet())
                builder.append(Strings.Format_NL("            <step name=\"{0}BidPlace\" delegate=\"placeBid\" player=\"{0}\" maxRunCount=\"1\"/>", playerName));
            
            for(String playerName : playerNames_InOrder)
            {
                builder.append(Strings.Format_NL(""));
                builder.append(Strings.Format_NL("            <!-- {0} Game Sequence -->", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}Tech\" delegate=\"tech\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}Purchase\" delegate=\"purchase\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}CombatMove\" delegate=\"move\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}Battle\" delegate=\"battle\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}NonCombatMove\" delegate=\"move\" player=\"{0}\" display=\"Non Combat Move\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}Place\" delegate=\"place\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}TechActivation\" delegate=\"tech_activation\" player=\"{0}\"/>", playerName));
                builder.append(Strings.Format_NL("            <step name=\"{0}EndTurn\" delegate=\"endTurn\" player=\"{0}\"/>", playerName));
            }
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("            <step name=\"endRoundStep\" delegate=\"endRound\"/>"));            
            builder.append(Strings.Format_NL("        </sequence>"));
            builder.append(Strings.Format_NL("    </gamePlay>"));            

            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <production>"));
            for(UnitData unit : map.GetUnitsData().values())
            {
                for(PlayerSpecificUnitData psud : unit.GetPlayerSpecificUnitData().values())
                {
                    if(!psud.GetPurchasable())
                        continue;
                    builder.append(Strings.Format_NL("        <productionRule name=\"buy{0}_{1}\">", unit.GetName(), psud.GetPlayerName()));
                    builder.append(Strings.Format_NL("            <cost resource=\"PUs\" quantity=\"{0}\" />", psud.GetCost()));
                    builder.append(Strings.Format_NL("            <result resourceOrUnit=\"{0}\" quantity=\"1\"/>", unit.GetName()));
                    builder.append(Strings.Format_NL("        </productionRule>"));
                }
            }
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <!-- Production Frontiers -->"));
            
            for (PlayerData player : map.GetPlayersData().values())
            {
                StringBuilder frontierBuilder = new StringBuilder();
                frontierBuilder.append(Strings.Format_NL(""));
                frontierBuilder.append(Strings.Format_NL("        <productionFrontier name=\"production_{0}\">", player.GetName()));
                for (UnitData unit : map.GetUnitsData().values())
                {
                    PlayerSpecificUnitData psud = unit.GetPlayerSpecificUnitData().get(player.GetName());
                    if (!psud.GetPurchasable())
                        continue;
                    frontierBuilder.append(Strings.Format_NL("            <frontierRules name=\"buy{0}_{1}\"/>", unit.GetName(), player.GetName()));
                }
                frontierBuilder.append(Strings.Format_NL("        </productionFrontier>"));
                if(frontierBuilder.toString().contains("<frontierRules "))
                    builder.append(frontierBuilder); //Only write to xml if the frontier contains any data (empty ones cause errors)
            }
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <!-- Production Frontier Assignments -->"));
            for (PlayerData player : map.GetPlayersData().values())
            {
                builder.append(Strings.Format_NL("        <playerProduction player=\"{0}\" frontier=\"production_{0}\"/>", player.GetName()));
            }
            builder.append(Strings.Format_NL("    </production>"));

            //builder.append(Strings.Format_NL("    <technology>"));
            //Not yet supported
            //builder.append(Strings.Format_NL("    </technology>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <attatchmentList>"));
            builder.append(Strings.Format_NL("        <!-- Unit Attachments -->"));
            TreeMap<String, UnitProperty> defaultUnitProps = new Segment2Step4ContentPanel_All().GetDefaultUnitProperties();
            for (UnitData unit : map.GetUnitsData().values())
            {
                StringBuilder uDataBuilder = new StringBuilder();
                uDataBuilder.append(Strings.Format_NL(""));
                uDataBuilder.append(Strings.Format_NL("        <attatchment name=\"unitAttatchment\" attatchTo=\"{0}\" javaClass=\"games.strategy.triplea.attatchments.UnitAttachment\" type=\"unitType\">", unit.GetName()));
                for(UnitProperty prop : unit.GetProperties().values())
                {
                    //Only write prop value if value is not default (or if it is the movement prop, because we need at least one prop in attachment)
                    if(prop.GetName().toLowerCase().equals("movement") || (!defaultUnitProps.containsKey(prop.GetName()) || !defaultUnitProps.get(prop.GetName()).GetValue().equals(prop.GetValue())))
                        uDataBuilder.append(Strings.Format_NL("            <option name=\"{0}\" value=\"{1}\"/>", prop.GetName(), prop.GetValue()));
                }
                uDataBuilder.append(Strings.Format_NL("        </attatchment>", unit.GetName()));
                String uDataTS = uDataBuilder.toString();
                if(uDataTS.contains("<option "))
                    builder.append(uDataTS); //Only write to xml if the attachment contains any data (empty ones cause errors)
            }
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <!-- Territory Data -->"));
            for (TerritoryData ter : map.GetTData().values())
            {
                //Trim data, which cuts data that is incompatible with terrain type (Like water can't be someone's capital)
                ter = Segment2Step6ContentPanel_All.TrimTData(ter);

                StringBuilder tDataBuilder = new StringBuilder();
                tDataBuilder.append(Strings.Format_NL("        <attatchment name=\"territoryAttatchment\" attatchTo=\"{0}\" javaClass=\"games.strategy.triplea.attatchments.TerritoryAttachment\" type=\"territory\">", ter.GetName()));
                if (ter.GetProduction() > 0)
                    tDataBuilder.append(Strings.Format_NL("            <option name=\"production\" value=\"{0}\"/>", ter.GetProduction()));
                if (ter.IsVictoryCity())
                    tDataBuilder.append(Strings.Format_NL("            <option name=\"victoryCity\" value=\"true\"/>"));
                if (ter.IsImpassable())
                    tDataBuilder.append(Strings.Format_NL("            <option name=\"isImpassible\" value=\"true\"/>"));
                if (ter.IsBlockadeZone())
                    tDataBuilder.append(Strings.Format_NL("            <option name=\"blockadeZone\" value=\"true\"/>"));
                if (ter.GetCapitalTo() != null)
                    tDataBuilder.append(Strings.Format_NL("            <option name=\"capital\" value=\"{0}\"/>", ter.GetCapitalTo()));
                tDataBuilder.append(Strings.Format_NL("        </attatchment>"));
                String tDataTS = tDataBuilder.toString();
                if(tDataTS.contains("<option "))
                    builder.append(tDataTS); //Only write to xml if the attachment contains any data (empty ones cause errors)
            }
            builder.append(Strings.Format_NL("    </attatchmentList>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <initialize>"));            
            builder.append(Strings.Format_NL("        <ownerInitialize>"));
            for(String playerName : map.GetPlayersData().keySet())
            {
                 builder.append(Strings.Format_NL("            <!-- {0} Owned Territories -->", playerName));
                 for(TerritoryData ter : map.GetTData().values())
                 {
                     if(ter.GetOwner() != null && ter.GetOwner().equals(playerName))
                         builder.append(Strings.Format_NL("            <territoryOwner territory=\"{0}\" owner=\"{1}\"/>", ter.GetName(), playerName));
                 }
            }
            builder.append(Strings.Format_NL("        </ownerInitialize>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <unitInitialize>"));
            for(String playerName : map.GetPlayersData().keySet())
            {
                 builder.append(Strings.Format_NL(""));
                 builder.append(Strings.Format_NL("            <!-- {0} Unit Placements -->", playerName));
                 for(String terName : map.GetTData().keySet())
                 {
                     UnitCollection terUnits = map.GetTUnitPlacements().get(terName);
                     if(terUnits != null)
                     {
                         for(PSUnitType unitType : terUnits.GetUnits().keySet())
                         {
                             int count = terUnits.GetUnitOfTypeXCount(unitType);
                             if(unitType.GetOwner().equals(playerName) && count > 0)
                                 builder.append(Strings.Format_NL("            <unitPlacement unitType=\"{0}\" territory=\"{1}\" quantity=\"{2}\" owner=\"{3}\"/>", unitType.GetUnitType(), terName, count, unitType.GetOwner()));
                         }
                     }
                 }
            }
            builder.append(Strings.Format_NL("        </unitInitialize>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("        <resourceInitialize>"));
            for(PlayerData player : map.GetPlayersData().values())
            {
                builder.append(Strings.Format_NL("            <resourceGiven player=\"{0}\" resource=\"PUs\" quantity=\"{1}\"/>", player.GetName(), player.GetResources()));  
            }
            builder.append(Strings.Format_NL("        </resourceInitialize>"));
            builder.append(Strings.Format_NL("    </initialize>"));
            
            builder.append(Strings.Format_NL(""));
            builder.append(Strings.Format_NL("    <propertyList>"));
            List<MapProperty> props = new ArrayList<MapProperty>();
            props.addAll(map.GetPlayerBids().values());
            props.addAll(map.GetMapProperties().values());
            props.add(new MapProperty("notes", map.GetMapNotes(), false));
            props.add(new MapProperty("mapName", map.GetMapName(), false));
            for(MapProperty prop : props)
            {
                if(prop.GetValue() instanceof Number)
                {
                    String propName = prop.GetName();
                    if(map.GetPlayersData().containsKey(propName))
                        propName = propName + " bid"; //I know this is kind of a hack, but it should be okay
                    builder.append(Strings.Format_NL("        <property name=\"{0}\" value=\"{1}\" editable=\"{2}\">", propName, prop.GetValue(), prop.IsEditable() ? "true" : "false"));
                    builder.append(Strings.Format_NL("            <number min=\"0\" max=\"500000\"/>"));
                    builder.append(Strings.Format_NL("        </property>"));
                }
                else if(prop.GetValue() instanceof Boolean)
                {
                    builder.append(Strings.Format_NL("        <property name=\"{0}\" value=\"{1}\" editable=\"{2}\">", prop.GetName(), prop.GetValue(), prop.IsEditable() ? "true" : "false"));
                    builder.append(Strings.Format_NL("            <boolean/>"));
                    builder.append(Strings.Format_NL("        </property>"));
                }
                else if (prop.GetValue() instanceof String) //For map notes
                {
                    if (prop.GetName().equals("notes"))
                    {
                        builder.append(Strings.Format_NL("        <property name=\"{0}\">", prop.GetName()));
                        builder.append(Strings.Format_NL("            <value>"));
                        builder.append(Strings.Format_NL("            <![CDATA[{0}]]>", prop.GetValue()));
                        builder.append(Strings.Format_NL("            </value>"));
                        builder.append(Strings.Format_NL("        </property>"));
                    }
                    else //Probably mapName prop, btw
                        builder.append(Strings.Format_NL("        <property name=\"{0}\" value=\"{1}\" editable=\"{2}\"/>", prop.GetName(), prop.GetValue(), prop.IsEditable()));
                }
            }
            builder.append(Strings.Format_NL("    </propertyList>"));
            builder.append(Strings.Format_NL("</game>"));

            return builder.toString();
        }
        catch (Exception ex)
        {
            ErrorConsole.getConsole().appendError(ex);
            return null;
        }
    }
}
