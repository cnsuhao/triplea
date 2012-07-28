package games.strategy.common.ui;

import games.strategy.debug.Console;
import games.strategy.engine.data.GameData;
import games.strategy.engine.framework.GameRunner;
import games.strategy.engine.framework.IGame;
import games.strategy.engine.framework.ServerGame;
import games.strategy.engine.framework.networkMaintenance.BanPlayerAction;
import games.strategy.engine.framework.networkMaintenance.BootPlayerAction;
import games.strategy.engine.framework.networkMaintenance.MutePlayerAction;
import games.strategy.engine.framework.networkMaintenance.SetPasswordAction;
import games.strategy.engine.framework.startup.login.ClientLoginValidator;
import games.strategy.engine.framework.startup.ui.InGameLobbyWatcher;
import games.strategy.engine.framework.ui.SaveGameFileChooser;
import games.strategy.engine.lobby.client.ui.action.EditGameCommentAction;
import games.strategy.engine.lobby.client.ui.action.RemoveGameFromLobbyAction;
import games.strategy.engine.message.DummyMessenger;
import games.strategy.net.BareBonesBrowserLaunch;
import games.strategy.net.IServerMessenger;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class BasicGameMenuBar<CustomGameFrame extends MainGameFrame> extends JMenuBar
{
	protected final CustomGameFrame m_frame;
	
	public BasicGameMenuBar(final CustomGameFrame frame)
	{
		m_frame = frame;
		createFileMenu(this);
		createGameSpecificMenus(this);
		final InGameLobbyWatcher watcher = createLobbyMenu(this);
		createNetworkMenu(this, watcher);
		createWebHelpMenu(this);
		createHelpMenu(this);
	}
	
	protected void createGameSpecificMenus(final JMenuBar menuBar)
	{
	}
	
	protected InGameLobbyWatcher createLobbyMenu(final JMenuBar menuBar)
	{
		if (!(m_frame.getGame() instanceof ServerGame))
			return null;
		final ServerGame serverGame = (ServerGame) m_frame.getGame();
		final InGameLobbyWatcher watcher = serverGame.getInGameLobbyWatcher();
		if (watcher == null || !watcher.isActive())
		{
			return watcher;
		}
		final JMenu lobby = new JMenu("Lobby");
		menuBar.add(lobby);
		lobby.add(new EditGameCommentAction(watcher, m_frame));
		lobby.add(new RemoveGameFromLobbyAction(watcher));
		return watcher;
	}
	
	/**
	 * @param menuBar
	 */
	protected void createNetworkMenu(final JMenuBar menuBar, final InGameLobbyWatcher watcher)
	{
		// revisit
		// if we are not a client or server game
		// then this will not create the network menu
		if (getGame().getMessenger() instanceof DummyMessenger)
			return;
		final JMenu menuNetwork = new JMenu("Network");
		addAllowObserversToJoin(menuNetwork);
		addBootPlayer(menuNetwork);
		addBanPlayer(menuNetwork);
		addMutePlayer(menuNetwork);
		addSetGamePassword(menuNetwork, watcher);
		addShowPlayers(menuNetwork);
		menuBar.add(menuNetwork);
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addAllowObserversToJoin(final JMenu parentMenu)
	{
		if (!getGame().getMessenger().isServer())
			return;
		final IServerMessenger messeneger = (IServerMessenger) getGame().getMessenger();
		final JCheckBoxMenuItem allowObservers = new JCheckBoxMenuItem("Allow New Observers");
		allowObservers.setSelected(messeneger.isAcceptNewConnections());
		allowObservers.addActionListener(new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				messeneger.setAcceptNewConnections(allowObservers.isSelected());
			}
		});
		parentMenu.add(allowObservers);
		return;
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addBootPlayer(final JMenu parentMenu)
	{
		if (!getGame().getMessenger().isServer())
			return;
		final IServerMessenger messenger = (IServerMessenger) getGame().getMessenger();
		final Action boot = new BootPlayerAction(this, messenger);
		parentMenu.add(boot);
		return;
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addBanPlayer(final JMenu parentMenu)
	{
		if (!getGame().getMessenger().isServer())
			return;
		final IServerMessenger messenger = (IServerMessenger) getGame().getMessenger();
		final Action ban = new BanPlayerAction(this, messenger);
		parentMenu.add(ban);
		return;
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addMutePlayer(final JMenu parentMenu)
	{
		if (!getGame().getMessenger().isServer())
			return;
		final IServerMessenger messenger = (IServerMessenger) getGame().getMessenger();
		final Action mute = new MutePlayerAction(this, messenger);
		parentMenu.add(mute);
		return;
	}
	
	/**
	 * @param menuGame
	 */
	protected void addSetGamePassword(final JMenu parentMenu, final InGameLobbyWatcher watcher)
	{
		if (!getGame().getMessenger().isServer())
			return;
		final IServerMessenger messenger = (IServerMessenger) getGame().getMessenger();
		parentMenu.add(new SetPasswordAction(this, watcher, (ClientLoginValidator) messenger.getLoginValidator()));
	}
	
	/**
	 * @param menuGame
	 */
	protected void addShowPlayers(final JMenu menuGame)
	{
		if (!getGame().getData().getProperties().getEditableProperties().isEmpty())
		{
			final AbstractAction optionsAction = new AbstractAction("Show Who is Who...")
			{
				public void actionPerformed(final ActionEvent e)
				{
					PlayersPanel.showPlayers(getGame(), m_frame);
				}
			};
			menuGame.add(optionsAction);
		}
	}
	
	/**
	 * @param menuBar
	 */
	protected void createHelpMenu(final JMenuBar menuBar)
	{
		final JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		addGameSpecificHelpMenus(helpMenu);
		addGameNotesMenu(helpMenu);
		addConsoleMenu(helpMenu);
		addAboutMenu(helpMenu);
	}
	
	private void createWebHelpMenu(final JMenuBar menuBar)
	{
		final JMenu web = new JMenu("Web");
		menuBar.add(web);
		addWebMenu(web);
	}
	
	private void addWebMenu(final JMenu parentMenu)
	{
		final JMenuItem hostingLink = new JMenuItem("How to Host...");
		final JMenuItem mapLink = new JMenuItem("Install Maps...");
		final JMenuItem bugReport = new JMenuItem("Bug Report...");
		final JMenuItem lobbyRules = new JMenuItem("Lobby Rules...");
		final JMenuItem warClub = new JMenuItem("War Club & Ladder...");
		final JMenuItem devForum = new JMenuItem("Developer Forum...");
		final JMenuItem donateLink = new JMenuItem("Donate...");
		final JMenuItem guidesLink = new JMenuItem("Guides...");
		hostingLink.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://tripleadev.1671093.n2.nabble.com/Download-Maps-Links-Hosting-Games-General-Information-tp4074312p4085700.html");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mapLink.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://tripleadev.1671093.n2.nabble.com/Download-Maps-Links-Hosting-Games-General-Information-tp4074312p4074312.html");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		bugReport.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("https://sourceforge.net/tracker/?group_id=44492");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lobbyRules.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://www.tripleawarclub.org/modules/newbb/viewtopic.php?topic_id=100&forum=1");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		warClub.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://www.tripleawarclub.org/");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		devForum.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://triplea.sourceforge.net/mywiki/Forum");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		donateLink.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("https://sourceforge.net/donate/index.php?group_id=44492");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		guidesLink.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					BareBonesBrowserLaunch.openURL("http://triplea.sourceforge.net/mywiki/Guides");
				} catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		parentMenu.add(hostingLink);
		parentMenu.add(mapLink);
		parentMenu.add(bugReport);
		parentMenu.add(lobbyRules);
		parentMenu.add(warClub);
		parentMenu.add(devForum);
		parentMenu.add(donateLink);
		parentMenu.add(guidesLink);
	}
	
	protected void addGameSpecificHelpMenus(final JMenu helpMenu)
	{
	}
	
	protected void addConsoleMenu(final JMenu parentMenu)
	{
		parentMenu.add(new AbstractAction("Show Console...")
		{
			public void actionPerformed(final ActionEvent e)
			{
				Console.getConsole().setVisible(true);
			}
		});
	}
	
	/**
	 * @param parentMenu
	 * @return
	 */
	protected void addAboutMenu(final JMenu parentMenu)
	{
		final String text = "<h2>" + getData().getGameName() + "</h2>" + "<p><b>Engine Version:</b> " + games.strategy.engine.EngineVersion.VERSION.toString() + "<br><b>Game:</b> "
					+ getData().getGameName() + "<br><b>Game Version:</b>" + getData().getGameVersion() + "</p>" + "<p>For more information please visit,<br><br>"
					+ "<b><a hlink='http://triplea.sourceforge.net/'>http://triplea.sourceforge.net/</a></b><br><br>";
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setBorder(null);
		editorPane.setBackground(getBackground());
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setText(text);
		final JScrollPane scroll = new JScrollPane(editorPane);
		scroll.setBorder(null);
		if (System.getProperty("mrj.version") == null)
		{
			parentMenu.addSeparator();
			parentMenu.add(new AbstractAction("About...")
			{
				public void actionPerformed(final ActionEvent e)
				{
					JOptionPane.showMessageDialog(m_frame, editorPane, "About " + m_frame.getGame().getData().getGameName(), JOptionPane.PLAIN_MESSAGE);
				}
			});
		}
		else
		// On Mac OS X, put the About menu where Mac users expect it to be
		{
			Application.getApplication().addApplicationListener(new ApplicationAdapter()
			{
				@Override
				public void handleAbout(final ApplicationEvent event)
				{
					event.setHandled(true); // otherwise the default About menu will still show appear
					JOptionPane.showMessageDialog(m_frame, editorPane, "About " + m_frame.getGame().getData().getGameName(), JOptionPane.PLAIN_MESSAGE);
				}
			});
		}
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addGameNotesMenu(final JMenu parentMenu)
	{
		// allow the game developer to write notes that appear in the game
		// displays whatever is in the notes field in html
		final String notes = getData().getProperties().get("notes", "");
		if (notes != null && notes.trim().length() != 0)
		{
			parentMenu.add(new AbstractAction("Game Notes...")
			{
				public void actionPerformed(final ActionEvent e)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							final JEditorPane editorPane = new JEditorPane();
							editorPane.setEditable(false);
							editorPane.setContentType("text/html");
							editorPane.setText(notes);
							final JScrollPane scroll = new JScrollPane(editorPane);
							final JDialog dialog = new JDialog(m_frame);
							dialog.setModal(true);
							dialog.add(scroll, BorderLayout.CENTER);
							final JPanel buttons = new JPanel();
							final JButton button = new JButton(new AbstractAction("OK")
							{
								public void actionPerformed(final ActionEvent e)
								{
									dialog.setVisible(false);
								}
							});
							buttons.add(button);
							dialog.getRootPane().setDefaultButton(button);
							dialog.add(buttons, BorderLayout.SOUTH);
							dialog.pack();
							if (dialog.getWidth() < 400)
							{
								dialog.setSize(400, dialog.getHeight());
							}
							if (dialog.getHeight() < 300)
							{
								dialog.setSize(dialog.getWidth(), 300);
							}
							if (dialog.getWidth() > 800)
							{
								dialog.setSize(800, dialog.getHeight());
							}
							if (dialog.getHeight() > 600)
							{
								dialog.setSize(dialog.getWidth(), 600);
							}
							dialog.setLocationRelativeTo(m_frame);
							dialog.addWindowListener(new WindowAdapter()
							{
								@Override
								public void windowOpened(final WindowEvent e)
								{
									scroll.getVerticalScrollBar().getModel().setValue(0);
									scroll.getHorizontalScrollBar().getModel().setValue(0);
									button.requestFocus();
								}
							});
							dialog.setVisible(true);
							dialog.dispose();
						}
					});
					// JOptionPane.showMessageDialog(m_frame, scroll, "Notes", JOptionPane.PLAIN_MESSAGE);
				}
			});
		}
	}
	
	/**
	 * @param menuBar
	 */
	protected void createFileMenu(final JMenuBar menuBar)
	{
		final JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		addSaveMenu(fileMenu);
		addExitMenu(fileMenu);
	}
	
	/**
	 * @param parent
	 */
	protected void addSaveMenu(final JMenu parent)
	{
		final JMenuItem menuFileSave = new JMenuItem(new AbstractAction("Save...")
		{
			public void actionPerformed(final ActionEvent e)
			{
				// For some strange reason,
				// the only way to get a Mac OS X native-style file dialog
				// is to use an AWT FileDialog instead of a Swing JDialog
				if (GameRunner.isMac())
				{
					final FileDialog fileDialog = new FileDialog(m_frame);
					fileDialog.setMode(FileDialog.SAVE);
					SaveGameFileChooser.ensureDefaultDirExists();
					fileDialog.setDirectory(SaveGameFileChooser.DEFAULT_DIRECTORY.getPath());
					fileDialog.setFilenameFilter(new FilenameFilter()
					{
						public boolean accept(final File dir, final String name)
						{ // the extension should be .tsvg, but find svg extensions as well
							return name.endsWith(".tsvg") || name.endsWith(".svg");
						}
					});
					fileDialog.setVisible(true);
					/*DateFormat format = new SimpleDateFormat("yyyy_MM_dd");
					String defaultFileName = "game_" + format.format(new Date()) + "_" + getData().getGameName() + "_round_" + getData().getSequence().getRound();
					defaultFileName = IllegalCharacterRemover.removeIllegalCharacter(defaultFileName);
					defaultFileName = defaultFileName + ".tsvg";
					
					fileDialog.setFile(defaultFileName);*/
					String fileName = fileDialog.getFile();
					final String dirName = fileDialog.getDirectory();
					if (fileName == null)
						return;
					else
					{
						if (!fileName.endsWith(".tsvg"))
							fileName += ".tsvg";
						final File f = new File(dirName, fileName);
						// TODO check this on a MAC
						// disallow sub directories to be entered (in the form directory/name
						/* String filePath = f.getPath().substring(0,f.getPath().lastIndexOf("\\"));
						 if(!fileChooser.getCurrentDirectory().toString().equals(filePath))
						 {
						     int choice = JOptionPane.showConfirmDialog(m_frame,
						         "Special characters are not allowed in the file name.  Please rename it.", "Cancel?", JOptionPane.DEFAULT_OPTION,
						         JOptionPane.WARNING_MESSAGE);
						         return;
						 }*/
						// If the user selects a filename that already exists,
						// the AWT Dialog on Mac OS X will ask the user for confirmation
						// so, we don't need to explicitly ask user if they want to overwrite the old file
						getGame().saveGame(f);
						JOptionPane.showMessageDialog(m_frame, "Game Saved", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				// Non-Mac platforms should use the normal Swing JFileChooser
				else
				{
					final JFileChooser fileChooser = SaveGameFileChooser.getInstance();
					final int rVal = fileChooser.showSaveDialog(m_frame);
					if (rVal == JFileChooser.APPROVE_OPTION)
					{
						File f = fileChooser.getSelectedFile();
						// disallow sub directories to be entered (in the form directory/name) for Windows boxes
						if (GameRunner.isWindows())
						{
							final int slashIndex = Math.min(f.getPath().lastIndexOf("\\"), f.getPath().length());
							final String filePath = f.getPath().substring(0, slashIndex);
							if (!fileChooser.getCurrentDirectory().toString().equals(filePath))
							{
								final int choice = JOptionPane.showConfirmDialog(m_frame, "Sub directories are not allowed in the file name.  Please rename it.", "Cancel?",
											JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
								return;
							}
						}
						if (!f.getName().toLowerCase().endsWith(".tsvg"))
						{
							f = new File(f.getParent(), f.getName() + ".tsvg");
						}
						// A small warning so users will not over-write a file,
						// added by NeKromancer
						if (f.exists())
						{
							final int choice = JOptionPane.showConfirmDialog(m_frame, "A file by that name already exists. Do you wish to over write it?", "Over-write?", JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
							if (choice != JOptionPane.OK_OPTION)
							{
								return;
							}
						}// end if exists
						getGame().saveGame(f);
						JOptionPane.showMessageDialog(m_frame, "Game Saved", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		parent.add(menuFileSave);
	}
	
	/**
	 * @param parentMenu
	 */
	protected void addExitMenu(final JMenu parentMenu)
	{
		final boolean isMac = GameRunner.isMac();
		final JMenuItem leaveGameMenuExit = new JMenuItem(new AbstractAction("Leave Game")
		{
			public void actionPerformed(final ActionEvent e)
			{
				m_frame.leaveGame();
			}
		});
		if (isMac)
		{ // On Mac OS X, the command-Q is reserved for the Quit action,
			// so set the command-L key combo for the Leave Game action
			leaveGameMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}
		else
		{ // On non-Mac operating systems, set the Ctrl-Q key combo for the Leave Game action
			leaveGameMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}
		parentMenu.add(leaveGameMenuExit);
		// Mac OS X automatically creates a Quit menu item under the TripleA menu,
		// so all we need to do is register that menu item with triplea's shutdown mechanism
		if (isMac)
		{
			MacWrapper.registerMacShutdownHandler(m_frame);
		}
		else
		{ // On non-Mac operating systems, we need to manually create an Exit menu item
			final JMenuItem menuFileExit = new JMenuItem(new AbstractAction("Exit")
			{
				public void actionPerformed(final ActionEvent e)
				{
					m_frame.shutdown();
				}
			});
			parentMenu.add(menuFileExit);
		}
	}
	
	public IGame getGame()
	{
		return m_frame.getGame();
	}
	
	public GameData getData()
	{
		return m_frame.getGame().getData();
	}
}