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
package games.strategy.engine.lobby.client.ui;

import games.strategy.engine.EngineVersion;
import games.strategy.engine.framework.GameRunner;
import games.strategy.engine.framework.GameRunner2;
import games.strategy.engine.framework.startup.ui.InGameLobbyWatcher;
import games.strategy.engine.framework.startup.ui.ServerOptions;
import games.strategy.engine.lobby.server.GameDescription;
import games.strategy.engine.lobby.server.IModeratorController;
import games.strategy.engine.lobby.server.ModeratorController;
import games.strategy.net.INode;
import games.strategy.net.Messengers;
import games.strategy.net.Node;
import games.strategy.ui.TableSorter;
import games.strategy.util.Version;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class LobbyGamePanel extends JPanel
{
	private JButton m_hostGame;
	private JButton m_joinGame;
	private JButton m_bootGame;
	private LobbyGameTableModel m_gameTableModel;
	private final Messengers m_messengers;
	private JTable m_gameTable;
	private TableSorter m_tableSorter;
	
	public LobbyGamePanel(final Messengers messengers)
	{
		m_messengers = messengers;
		createComponents();
		layoutComponents();
		setupListeners();
		setWidgetActivation();
	}
	
	private void createComponents()
	{
		m_hostGame = new JButton("Host Game");
		m_joinGame = new JButton("Join Game");
		m_bootGame = new JButton("Boot Game");
		m_gameTableModel = new LobbyGameTableModel(m_messengers.getMessenger(), m_messengers.getChannelMessenger(), m_messengers.getRemoteMessenger());
		m_tableSorter = new TableSorter(m_gameTableModel);
		m_gameTable = new LobbyGameTable(m_tableSorter);
		m_tableSorter.setTableHeader(m_gameTable.getTableHeader());
		// only allow one row to be selected
		m_gameTable.setColumnSelectionAllowed(false);
		m_gameTable.setRowSelectionAllowed(true);
		m_gameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// by default, sort newest first
		final int nameColumn = m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Name);
		m_tableSorter.setSortingStatus(nameColumn, TableSorter.ASCENDING);
		// these should add up to 700 at most
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Players)).setPreferredWidth(42);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Round)).setPreferredWidth(40);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.PW)).setPreferredWidth(22);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.GV)).setPreferredWidth(32);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.EV)).setPreferredWidth(42);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Started)).setPreferredWidth(54);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Status)).setPreferredWidth(110);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Name)).setPreferredWidth(148);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Comments)).setPreferredWidth(150);
		m_gameTable.getColumnModel().getColumn(m_gameTableModel.getColumnIndex(LobbyGameTableModel.Column.Host)).setPreferredWidth(60);
		m_gameTable.setDefaultRenderer(Date.class, new DefaultTableCellRenderer()
		{
			private final SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
			
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setText(format.format((Date) value));
				return this;
			}
		});
	}
	
	private void layoutComponents()
	{
		final JScrollPane scroll = new JScrollPane(m_gameTable);
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		final JToolBar toolBar = new JToolBar();
		toolBar.add(m_hostGame);
		toolBar.add(m_joinGame);
		if (isAdmin())
			toolBar.add(m_bootGame);
		toolBar.setFloatable(false);
		add(toolBar, BorderLayout.SOUTH);
	}
	
	public boolean isAdmin()
	{
		return ((IModeratorController) m_messengers.getRemoteMessenger().getRemote(ModeratorController.getModeratorControllerName())).isAdmin();
	}
	
	private void setupListeners()
	{
		m_hostGame.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				hostGame();
			}
		});
		m_joinGame.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				joinGame();
			}
		});
		m_bootGame.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				bootGame();
			}
		});
		m_gameTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(final ListSelectionEvent e)
			{
				setWidgetActivation();
			}
		});
		
		m_gameTable.addMouseListener(new MouseListener()
		{
			public void mouseClicked(final MouseEvent event)
			{
				if (event.getClickCount() == 2)
				{
					joinGame();
				}
			}
			
			public void mousePressed(final MouseEvent e)
			{
			} // ignore
			
			public void mouseReleased(final MouseEvent e)
			{
			} // ignore
			
			public void mouseEntered(final MouseEvent e)
			{
			} // ignore
			
			public void mouseExited(final MouseEvent e)
			{
			} // ignore
		});
	}
	
	private void joinGame()
	{
		final int selectedIndex = m_gameTable.getSelectedRow();
		if (selectedIndex == -1)
			return;
		// we sort the table, so get the correct index
		final int modelIndex = m_tableSorter.modelIndex(selectedIndex);
		final GameDescription description = m_gameTableModel.get(modelIndex);
		final Version engineVersionOfGameToJoin = new Version(description.getEngineVersion());
		final List<String> commands = new ArrayList<String>();
		if (EngineVersion.VERSION.equals(engineVersionOfGameToJoin))
		{
			populateBasicJavaArgs(commands);
		}
		else
		{
			final String newClassPath;
			try
			{
				newClassPath = findOldJar(engineVersionOfGameToJoin, false);
			} catch (final Exception e)
			{
				JOptionPane.showMessageDialog(getParent(), "Host is using a different engine than you, and can not find correct engine: " + engineVersionOfGameToJoin.toStringFull("_"),
							"Correct TripleA Engine Not Found", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// ask user if we really want to do this?
			final String messageString = "<html>This TripleA engine is version "
						+ EngineVersion.VERSION.toString()
						+ " and you are trying to join a game made with version "
						+ engineVersionOfGameToJoin.toString()
						+ "<br>However, this TripleA can only play with engines that are the exact same version as itself (x_x_x_x)."
						+ "<br><br>TripleA now comes with older engines included with it, and has found the engine used by the host. This is a new feature and is in 'beta' stage."
						+ "<br>It will attempt to run a new instance of TripleA using the older engine jar file, and this instance will join the host's game."
						+ "<br>Your current instance will not be closed. Please report any bugs or issues."
						+ "<br><br>Do you wish to continue?</html>";
			final int answer = JOptionPane.showConfirmDialog(null, messageString, "Run old jar to join hosted game?", JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION)
				return;
			populateBasicJavaArgs(commands, newClassPath);
		}
		commands.add("-D" + GameRunner2.TRIPLEA_CLIENT_PROPERTY + "=true");
		commands.add("-D" + GameRunner2.TRIPLEA_PORT_PROPERTY + "=" + description.getPort());
		commands.add("-D" + GameRunner2.TRIPLEA_HOST_PROPERTY + "=" + description.getHostedBy().getAddress().getHostAddress());
		commands.add("-D" + GameRunner2.TRIPLEA_NAME_PROPERTY + "=" + m_messengers.getMessenger().getLocalNode().getName());
		final String javaClass = "games.strategy.engine.framework.GameRunner";
		commands.add(javaClass);
		exec(commands);
	}
	
	public static String findOldJar(final Version oldVersionNeeded, final boolean ignoreMicro) throws IOException
	{
		if (EngineVersion.VERSION.equals(oldVersionNeeded, ignoreMicro))
			return System.getProperty("java.class.path");
		// so, what we do here is try to see if our installed copy of triplea includes older jars with it that are the same engine as was used for this savegame, and if so try to run it
		// System.out.println("System classpath: " + System.getProperty("java.class.path"));
		// we don't care what the last (micro) number is of the version number. example: triplea 1.5.2.1 can open 1.5.2.0 savegames.
		final String jarName = "triplea_" + oldVersionNeeded.toStringFull("_", ignoreMicro);
		final File oldJarsFolder = new File(GameRunner.getRootFolder(), "old/");
		final File[] files = oldJarsFolder.listFiles();
		if (files == null)
			throw new IOException("Can not find 'old' engine jars folder");
		File ourOldJar = null;
		for (final File f : Arrays.asList(files))
		{
			final String jarPath = f.getCanonicalPath();
			if (jarPath.indexOf(jarName) != -1 && jarPath.indexOf(".jar") != -1)
			{
				ourOldJar = f;
				break;
			}
		}
		if (ourOldJar == null)
			throw new IOException("Can not find 'old' engine jar for version: " + oldVersionNeeded.toStringFull("_"));
		final String newClassPath = ourOldJar.getCanonicalPath();
		if (newClassPath == null || newClassPath.length() <= 0)
			throw new IOException("Can not find 'old' engine jar for version: " + oldVersionNeeded.toStringFull("_"));
		return newClassPath;
	}
	
	protected void hostGame()
	{
		final ServerOptions options = new ServerOptions(JOptionPane.getFrameForComponent(this), m_messengers.getMessenger().getLocalNode().getName(), 3300, true);
		options.setLocationRelativeTo(JOptionPane.getFrameForComponent(this));
		options.setNameEditable(false);
		options.setVisible(true);
		if (!options.getOKPressed())
		{
			return;
		}
		final List<String> commands = new ArrayList<String>();
		populateBasicJavaArgs(commands);
		commands.add("-D" + GameRunner2.TRIPLEA_SERVER_PROPERTY + "=true");
		commands.add("-D" + GameRunner2.TRIPLEA_PORT_PROPERTY + "=" + options.getPort());
		commands.add("-D" + GameRunner2.TRIPLEA_NAME_PROPERTY + "=" + options.getName());
		commands.add("-D" + GameRunner2.LOBBY_HOST + "=" + m_messengers.getMessenger().getRemoteServerSocketAddress().getAddress().getHostAddress());
		commands.add("-D" + GameRunner2.LOBBY_PORT + "=" + m_messengers.getMessenger().getRemoteServerSocketAddress().getPort());
		commands.add("-D" + GameRunner2.LOBBY_GAME_COMMENTS + "=" + options.getComments());
		commands.add("-D" + GameRunner2.LOBBY_GAME_HOSTED_BY + "=" + m_messengers.getMessenger().getLocalNode().getName());
		if (options.getPassword() != null && options.getPassword().length() > 0)
			commands.add("-D" + GameRunner2.TRIPLEA_SERVER_PASSWORD_PROPERTY + "=" + options.getPassword());
		final String fileName = System.getProperty(GameRunner2.TRIPLEA_GAME_PROPERTY, "");
		if (fileName.length() > 0)
			commands.add("-D" + GameRunner2.TRIPLEA_GAME_PROPERTY + "=" + fileName);
		final String javaClass = "games.strategy.engine.framework.GameRunner";
		commands.add(javaClass);
		exec(commands);
	}
	
	public static void startGame(final String savegamePath, final String classpath)
	{
		final List<String> commands = new ArrayList<String>();
		populateBasicJavaArgs(commands, classpath);
		if (savegamePath != null && savegamePath.length() > 0)
			commands.add("-D" + GameRunner2.TRIPLEA_GAME_PROPERTY + "=" + savegamePath);
		// add in any existing command line items
		for (final String property : GameRunner2.getProperties())
		{
			// we add game property above, and we add version bin in the populateBasicJavaArgs
			if (GameRunner2.TRIPLEA_GAME_PROPERTY.equals(property) || GameRunner2.TRIPLEA_ENGINE_VERSION_BIN.equals(property))
				continue;
			final String value = System.getProperty(property);
			if (value != null)
			{
				commands.add("-D" + property + "=" + value);
			}
			else if (GameRunner2.LOBBY_HOST.equals(property) || GameRunner2.LOBBY_PORT.equals(property) || GameRunner2.LOBBY_GAME_HOSTED_BY.equals(property))
			{
				// for these 3 properties, we clear them after hosting, but back them up.
				final String oldValue = System.getProperty(property + GameRunner2.OLD_EXTENSION);
				if (oldValue != null)
				{
					commands.add("-D" + property + "=" + oldValue);
				}
			}
		}
		// classpath for main
		final String javaClass = "games.strategy.engine.framework.GameRunner";
		commands.add(javaClass);
		// System.out.println("Commands: " + commands);
		exec(commands);
	}
	
	private void bootGame()
	{
		final int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to disconnect the selected game?", "Remove Game From Lobby", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return;
		final int selectedIndex = m_gameTable.getSelectedRow();
		if (selectedIndex == -1)
			return;
		// we sort the table, so get the correct index
		final int modelIndex = m_tableSorter.modelIndex(selectedIndex);
		final GameDescription description = m_gameTableModel.get(modelIndex);
		final INode lobbyWatcherNode = new Node(description.getHostedBy().getName() + "_" + InGameLobbyWatcher.LOBBY_WATCHER_NAME, description.getHostedBy().getAddress(),
					description.getHostedBy().getPort());
		final IModeratorController controller = (IModeratorController) m_messengers.getRemoteMessenger().getRemote(ModeratorController.getModeratorControllerName());
		controller.boot(lobbyWatcherNode);
		JOptionPane.showMessageDialog(null, "The game you selected has been disconnected from the lobby.");
	}
	
	private static void exec(final List<String> commands)
	{
		final ProcessBuilder builder = new ProcessBuilder(commands);
		// merge the streams, so we only have to start one reader thread
		builder.redirectErrorStream(true);
		try
		{
			final Process p = builder.start();
			final InputStream s = p.getInputStream();
			// we need to read the input stream to prevent possible
			// deadlocks
			final Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						while (s.read() >= 0)
						{
							// just read
						}
					} catch (final IOException e)
					{
						e.printStackTrace();
					}
				}
			}, "Process ouput gobbler");
			t.setDaemon(true);
			t.start();
		} catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	private static void populateBasicJavaArgs(final List<String> commands)
	{
		populateBasicJavaArgs(commands, System.getProperty("java.class.path"));
	}
	
	private static void populateBasicJavaArgs(final List<String> commands, final String classpath)
	{
		final String javaCommand = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		commands.add(javaCommand);
		commands.add("-classpath");
		if (classpath != null && classpath.length() > 0)
			commands.add(classpath);
		else
			commands.add(System.getProperty("java.class.path"));
		// for whatever reason, .maxMemory() returns a value about 12% smaller than the real Xmx value, so we are going to add 64m to that to compensate
		final long maxMemory = ((long) (Runtime.getRuntime().maxMemory() * 1.15) + 67108864);
		commands.add("-Xmx" + maxMemory);
		// commands.add("-Xmx512m"); //TODO: this may need updating 640m
		// preserve noddraw to fix 1742775
		final String[] preservedSystemProperties = { "sun.java2d.noddraw" };
		for (final String key : preservedSystemProperties)
		{
			if (System.getProperties().getProperty(key) != null)
			{
				final String value = System.getProperties().getProperty(key);
				if (value.matches("[a-zA-Z0-9.]+"))
				{
					commands.add("-D" + key + "=" + value);
				}
			}
		}
		if (GameRunner.isMac())
		{
			commands.add("-Dapple.laf.useScreenMenuBar=true");
			commands.add("-Xdock:name=\"TripleA\"");
			final File icons = new File(GameRunner.getRootFolder(), "icons/triplea_icon.png");
			if (!icons.exists())
				throw new IllegalStateException("Icon file not found");
			commands.add("-Xdock:icon=" + icons.getAbsolutePath() + "");
		}
		final String version = System.getProperty(GameRunner2.TRIPLEA_ENGINE_VERSION_BIN);
		if (version != null && version.length() > 0)
		{
			final Version testVersion;
			try
			{
				testVersion = new Version(version);
				commands.add("-D" + GameRunner2.TRIPLEA_ENGINE_VERSION_BIN + "=" + testVersion.toString());
			} catch (final Exception e)
			{
				// nothing
			}
		}
	}
	
	private void setWidgetActivation()
	{
		final boolean selected = m_gameTable.getSelectedRow() >= 0;
		m_joinGame.setEnabled(selected);
	}
}