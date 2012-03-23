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
package games.strategy.engine.framework.startup.launcher;

import games.strategy.engine.data.GameData;
import games.strategy.engine.framework.ServerGame;
import games.strategy.engine.framework.startup.mc.GameSelectorModel;
import games.strategy.engine.framework.ui.background.WaitWindow;
import games.strategy.engine.gamePlayer.IGamePlayer;
import games.strategy.engine.message.DummyMessenger;
import games.strategy.engine.random.IRandomSource;
import games.strategy.engine.random.ScriptedRandomSource;
import games.strategy.net.INode;
import games.strategy.net.IServerMessenger;
import games.strategy.net.Messengers;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class LocalLauncher implements ILauncher
{
	private static final Logger s_logger = Logger.getLogger(ILauncher.class.getName());
	private final GameData m_gameData;
	private final IRandomSource m_randomSource;
	private final Map<String, String> m_playerTypes;
	private final GameSelectorModel m_gameSelectorModel;
	private final WaitWindow m_gameLoadingWindow = new WaitWindow("Loading game, please wait.");
	
	public LocalLauncher(final GameSelectorModel gameSelectorModel, final IRandomSource randomSource, final Map<String, String> playerTypes)
	{
		m_gameSelectorModel = gameSelectorModel;
		m_gameData = gameSelectorModel.getGameData();
		m_randomSource = randomSource;
		m_playerTypes = playerTypes;
	}
	
	public void launch(final Component parent)
	{
		if (!SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("Wrong thread");
		final Runnable r = new Runnable()
		{
			public void run()
			{
				launchInNewThread(parent);
			}
		};
		final Thread t = new Thread(r);
		t.start();
		m_gameLoadingWindow.setLocationRelativeTo(JOptionPane.getFrameForComponent(parent));
		m_gameLoadingWindow.setVisible(true);
		m_gameLoadingWindow.showWait();
		JOptionPane.getFrameForComponent(parent).setVisible(false);
	}
	
	private void launchInNewThread(final Component parent)
	{
		final Runnable runner = new Runnable()
		{
			public void run()
			{
				Exception exceptionLoadingGame = null;
				ServerGame game = null;
				try
				{
					final IServerMessenger messenger = new DummyMessenger();
					final Messengers messengers = new Messengers(messenger);
					final Set<IGamePlayer> gamePlayers = m_gameData.getGameLoader().createPlayers(m_playerTypes);
					game = new ServerGame(m_gameData, gamePlayers, new HashMap<String, INode>(), messengers);
					game.setRandomSource(m_randomSource);
					// for debugging, we can use a scripted random source
					if (ScriptedRandomSource.useScriptedRandom())
					{
						game.setRandomSource(new ScriptedRandomSource());
					}
					m_gameData.getGameLoader().startGame(game, gamePlayers);
				} catch (final Exception ex)
				{
					ex.printStackTrace();
					exceptionLoadingGame = ex;
				} finally
				{
					m_gameLoadingWindow.doneWait();
				}
				try
				{
					if (exceptionLoadingGame == null)
					{
						s_logger.fine("Game starting");
						game.startGame();
						s_logger.fine("Game over");
					}
				} finally
				{
					// todo(kg), this does not occur on the swing thread, and this notifies setupPanel observers
					m_gameSelectorModel.loadDefaultGame(parent);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							JOptionPane.getFrameForComponent(parent).setVisible(true);
						}
					});
				}
			}
		};
		final Thread thread = new Thread(runner, "Triplea start local thread");
		thread.start();
		if (SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("Wrong thread");
		try
		{
			thread.join();
		} catch (final InterruptedException e)
		{
		}
		s_logger.fine("Thread done!");
	}
}