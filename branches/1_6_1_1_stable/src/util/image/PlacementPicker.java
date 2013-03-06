/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package util.image;

import games.strategy.ui.Util;
import games.strategy.util.PointFileReaderWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class PlacementPicker extends JFrame
{
	private static final long serialVersionUID = 953019978051420881L;
	private Point m_currentSquare;
	private Image m_image;
	private final JLabel m_location = new JLabel();
	private Map<String, List<Polygon>> m_polygons = new HashMap<String, List<Polygon>>();
	private Map<String, List<Point>> m_placements;
	private List<Point> m_currentPlacements;
	private String m_currentCountry;
	private static final int PLACE_SIZE = 48;
	
	/**
	 * main(java.lang.String[])
	 * 
	 * Main program begins here.
	 * Asks the user to select the map then runs the
	 * the actual placement picker program.
	 * 
	 * @param java
	 *            .lang.String[] args the command line arguments
	 * @see Picker(java.lang.String) picker
	 */
	public static void main(final String[] args)
	{
		System.out.println("Select the map");
		final String mapName = new FileOpen("Select The Map").getPathString();
		if (mapName != null)
		{
			final PlacementPicker picker = new PlacementPicker(mapName);
			picker.setSize(600, 550);
			picker.setVisible(true);
		}
		else
		{
			System.out.println("No Image Map Selected. Shutting down.");
			System.exit(0);
		}
	}// end main
	
	/**
	 * Constructor PlacementPicker(java.lang.String)
	 * 
	 * Setus up all GUI components, initializes variables with
	 * default or needed values, and prepares the map for user
	 * commands.
	 * 
	 * @param java
	 *            .lang.String mapName name of map file
	 */
	public PlacementPicker(final String mapName)
	{
		super("Placement Picker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final File file = new File(new File(mapName).getParent() + File.pathSeparator + "polygons.txt");
		if (file.exists()
					&& JOptionPane.showConfirmDialog(new JPanel(), "A polygons.txt file was found in the map's folder, do you want to use the file to supply the territories?", "File Suggestion", 1) == 0)
		{
			try
			{
				System.out.println("Polygons : " + file.getPath());
				m_polygons = PointFileReaderWriter.readOneToManyPolygons(new FileInputStream(file.getPath()));
			} catch (final IOException ex1)
			{
				ex1.printStackTrace();
			}
		}
		else
		{
			try
			{
				System.out.println("Select the Polygons file");
				final String polyPath = new FileOpen("Select A Polygon File").getPathString();
				if (polyPath != null)
				{
					System.out.println("Polygons : " + polyPath);
					m_polygons = PointFileReaderWriter.readOneToManyPolygons(new FileInputStream(polyPath));
				}
				else
				{
					System.out.println("Polygons file not given. Will run regardless");
				}
			} catch (final IOException ex1)
			{
				ex1.printStackTrace();
			}
		}
		createImage(mapName);
		final JPanel imagePanel = createMainPanel();
		/*
		Add a mouse listener to show
		X : Y coordinates on the lower
		left corner of the screen.
		*/
		imagePanel.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(final MouseEvent e)
			{
				m_location.setText("x:" + e.getX() + " y:" + e.getY());
				m_currentSquare = new Point(e.getPoint());
				repaint();
			}
		});
		/*
		   Add a mouse listener to monitor
		for right mouse button being
		clicked.	
		*/
		imagePanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				mouseEvent(e.getPoint(), e.isControlDown(), SwingUtilities.isRightMouseButton(e));
			}
		});
		// set up the image panel size dimensions ...etc
		imagePanel.setMinimumSize(new Dimension(m_image.getWidth(this), m_image.getHeight(this)));
		imagePanel.setPreferredSize(new Dimension(m_image.getWidth(this), m_image.getHeight(this)));
		imagePanel.setMaximumSize(new Dimension(m_image.getWidth(this), m_image.getHeight(this)));
		// set up the layout manager
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(imagePanel), BorderLayout.CENTER);
		this.getContentPane().add(m_location, BorderLayout.SOUTH);
		// set up the actions
		final Action openAction = new AbstractAction("Load Placements")
		{
			private static final long serialVersionUID = -2894085191455411106L;
			
			public void actionPerformed(final ActionEvent event)
			{
				loadPlacements();
			}
		};
		openAction.putValue(Action.SHORT_DESCRIPTION, "Load An Existing Placement File");
		final Action saveAction = new AbstractAction("Save Placements")
		{
			private static final long serialVersionUID = -3341738809601318716L;
			
			public void actionPerformed(final ActionEvent event)
			{
				savePlacements();
			}
		};
		saveAction.putValue(Action.SHORT_DESCRIPTION, "Save The Placements To File");
		final Action exitAction = new AbstractAction("Exit")
		{
			private static final long serialVersionUID = -9093426903644867897L;
			
			public void actionPerformed(final ActionEvent event)
			{
				System.exit(0);
			}
		};
		exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit The Program");
		// set up the menu items
		final JMenuItem openItem = new JMenuItem(openAction);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		final JMenuItem saveItem = new JMenuItem(saveAction);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		final JMenuItem exitItem = new JMenuItem(exitAction);
		// set up the menu bar
		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		final JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
	}// end constructor
	
	/**
	 * createImage(java.lang.String)
	 * 
	 * creates the image map and makes sure
	 * it is properly loaded.
	 * 
	 * @param java
	 *            .lang.String mapName the path of image map
	 */
	private void createImage(final String mapName)
	{
		m_image = Toolkit.getDefaultToolkit().createImage(mapName);
		try
		{
			Util.ensureImageLoaded(m_image);
		} catch (final InterruptedException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * javax.swing.JPanel createMainPanel()
	 * 
	 * Creates the main panel and returns
	 * a JPanel object.
	 * 
	 * @return javax.swing.JPanel the panel to return
	 */
	private JPanel createMainPanel()
	{
		final JPanel imagePanel = new JPanel()
		{
			private static final long serialVersionUID = -3941975573431195136L;
			
			@Override
			public void paint(final Graphics g)
			{
				// super.paint(g);
				g.drawImage(m_image, 0, 0, this);
				g.setColor(Color.red);
				if (m_currentSquare != null)
				{
					g.drawRect(m_currentSquare.x, m_currentSquare.y, PLACE_SIZE, PLACE_SIZE);
				}
				if (m_currentPlacements == null)
				{
					return;
				}
				for (final Point item : m_currentPlacements)
				{
					g.fillRect(item.x, item.y, PLACE_SIZE, PLACE_SIZE);
				}
			}// paint
		};
		return imagePanel;
	}
	
	/**
	 * savePlacements()
	 * 
	 * Saves the placements to disk.
	 */
	private void savePlacements()
	{
		try
		{
			final String fileName = new FileSave("Where To Save place.txt ?", "place.txt").getPathString();
			if (fileName == null)
			{
				return;
			}
			final FileOutputStream out = new FileOutputStream(fileName);
			PointFileReaderWriter.writeOneToMany(out, m_placements);
			out.flush();
			out.close();
			System.out.println("Data written to :" + new File(fileName).getCanonicalPath());
		} catch (final FileNotFoundException ex)
		{
			ex.printStackTrace();
		} catch (final HeadlessException ex)
		{
			ex.printStackTrace();
		} catch (final Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * loadPlacements()
	 * 
	 * Loads a pre-defined file with map placement points.
	 */
	private void loadPlacements()
	{
		try
		{
			System.out.println("Load a placement file");
			final String placeName = new FileOpen("Load A Placement File").getPathString();
			if (placeName == null)
			{
				return;
			}
			final FileInputStream in = new FileInputStream(placeName);
			m_placements = PointFileReaderWriter.readOneToMany(in);
			repaint();
		} catch (final FileNotFoundException ex)
		{
			ex.printStackTrace();
		} catch (final IOException ex)
		{
			ex.printStackTrace();
		} catch (final HeadlessException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * java.lang.String findTerritoryName(java.awt.Point)
	 * 
	 * Finds a land territory name or
	 * some sea zone name.
	 * 
	 * @param java
	 *            .awt.point p a point on the map
	 */
	private String findTerritoryName(final Point p)
	{
		String seaName = "there be dragons";
		// try to find a land territory.
		// sea zones often surround a land territory
		for (final String name : m_polygons.keySet())
		{
			final Collection<Polygon> polygons = m_polygons.get(name);
			for (final Polygon poly : polygons)
			{
				if (poly.contains(p))
				{
					if (name.endsWith("Sea Zone") || name.startsWith("Sea Zone"))
					{
						seaName = name;
					}
					else
					{
						return name;
					}
				}// if
			}// while
		}// while
		return seaName;
	}
	
	/**
	 * mouseEvent(java.awt.Point, java.lang.boolean, java.lang.boolean)
	 * 
	 * Usage:
	 * left button start in territory
	 * left button + control, add point
	 * right button and ctrl write
	 * right button remove last
	 * 
	 * @param java
	 *            .awt.Point point a point clicked by mouse
	 * @param java
	 *            .lang.boolean ctrlDown true if ctrl key was hit
	 * @param java
	 *            .lang.boolean rightMouse true if the right mouse button was hit
	 */
	private void mouseEvent(final Point point, final boolean ctrlDown, final boolean rightMouse)
	{
		if (!rightMouse && !ctrlDown)
		{
			m_currentCountry = findTerritoryName(point);
			// If there isn't an existing array, create one
			if (m_placements == null || m_placements.get(m_currentCountry) == null)
				m_currentPlacements = new ArrayList<Point>();
			else
				m_currentPlacements = new ArrayList<Point>(m_placements.get(m_currentCountry));
			JOptionPane.showMessageDialog(this, m_currentCountry);
		}
		else if (!rightMouse && ctrlDown)
		{
			m_currentPlacements.add(point);
		}
		else if (rightMouse && ctrlDown)
		{
			// If there isn't an existing hashmap, create one
			if (m_placements == null)
			{
				m_placements = new HashMap<String, List<Point>>();
			}
			else
			{
				m_placements.put(m_currentCountry, m_currentPlacements);
			}
			m_currentPlacements = new ArrayList<Point>();
			System.out.println("done:" + m_currentCountry);
		}
		else if (rightMouse)
		{
			if (!m_currentPlacements.isEmpty())
			{
				m_currentPlacements.remove(m_currentPlacements.size() - 1);
			}
		}
		repaint();
	}
}// end class PlacementPicker