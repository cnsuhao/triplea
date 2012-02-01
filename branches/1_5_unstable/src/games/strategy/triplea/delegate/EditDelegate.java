/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package games.strategy.triplea.delegate;

import games.strategy.common.delegate.BaseDelegate;
import games.strategy.engine.data.ChangeFactory;
import games.strategy.engine.data.GameData;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Resource;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.delegate.IDelegateBridge;
import games.strategy.engine.history.Event;
import games.strategy.engine.history.EventChild;
import games.strategy.engine.history.HistoryNode;
import games.strategy.engine.history.Step;
import games.strategy.engine.message.IRemote;
import games.strategy.triplea.Constants;
import games.strategy.triplea.attatchments.TerritoryAttachment;
import games.strategy.triplea.delegate.remote.IEditDelegate;
import games.strategy.triplea.formatter.MyFormatter;
import games.strategy.triplea.player.ITripleaPlayer;
import games.strategy.util.CompositeMatch;
import games.strategy.util.CompositeMatchAnd;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * Edit game state
 * 
 * @author Tony Clayton
 */
public class EditDelegate extends BaseDelegate implements IEditDelegate
{
	private TripleADelegateBridge m_bridge;
	
	/**
	 * Called before the delegate will run.
	 */
	@Override
	public void start(final IDelegateBridge bridge)
	{
		m_bridge = new TripleADelegateBridge(bridge);
		super.start(m_bridge);
	}
	
	@Override
	public void end()
	{
		super.end();
	}
	
	@Override
	public Serializable saveState()
	{
		final EditExtendedDelegateState state = new EditExtendedDelegateState();
		state.superState = super.saveState();
		// add other variables to state here:
		return state;
	}
	
	@Override
	public void loadState(final Serializable state)
	{
		final EditExtendedDelegateState s = (EditExtendedDelegateState) state;
		super.loadState(s.superState);
		// load other variables from state here:
	}
	
	public static boolean getEditMode(final GameData data)
	{
		final Object editMode = data.getProperties().get(Constants.EDIT_MODE);
		if (editMode == null)
			return false;
		if (!(editMode instanceof Boolean))
			return false;
		return ((Boolean) editMode).booleanValue();
	}
	
	private String checkPlayerID()
	{
		final ITripleaPlayer remotePlayer = (ITripleaPlayer) m_bridge.getRemote();
		if (!m_bridge.getPlayerID().equals(remotePlayer.getID()))
			return "Edit actions can only be performed during players turn";
		return null;
	}
	
	private String checkEditMode()
	{
		final String result = checkPlayerID();
		if (null != result)
			return result;
		if (!getEditMode(getData()))
			return "Edit mode is not enabled";
		return null;
	}
	
	public void initialize(final String name)
	{
		initialize(name, name);
	}
	
	public String setEditMode(final boolean editMode)
	{
		final ITripleaPlayer remotePlayer = (ITripleaPlayer) m_bridge.getRemote();
		if (!m_bridge.getPlayerID().equals(remotePlayer.getID()))
			return "Edit Mode can only be toggled during players turn";
		logEvent("Turning " + (editMode ? "on" : "off") + " Edit Mode", null);
		m_bridge.addChange(ChangeFactory.setProperty(Constants.EDIT_MODE, Boolean.valueOf(editMode), getData()));
		return null;
	}
	
	public boolean getEditMode()
	{
		return EditDelegate.getEditMode(getData());
	}
	
	public String removeUnits(final Territory territory, final Collection<Unit> units)
	{
		String result = null;
		if (null != (result = checkEditMode()))
			return result;
		if (null != (result = EditValidator.validateRemoveUnits(getData(), territory, units)))
			return result;
		logEvent("Removing units owned by " + m_bridge.getPlayerID().getName() + " from " + territory.getName() + ": " + MyFormatter.unitsToTextNoOwner(units), units);
		m_bridge.addChange(ChangeFactory.removeUnits(territory, units));
		return null;
	}
	
	public String addUnits(final Territory territory, final Collection<Unit> units)
	{
		String result = null;
		if (null != (result = checkEditMode()))
			return result;
		if (null != (result = EditValidator.validateAddUnits(getData(), territory, units)))
			return result;
		/* No longer needed, as territory unitProduction is now set by default to equal the territory value. Therefore any time it is different from the default, the map maker set it, so we shouldn't screw with it.
		if(Match.someMatch(units, Matches.UnitIsFactoryOrCanProduceUnits) && !Match.someMatch(territory.getUnits().getUnits(), Matches.UnitIsFactoryOrCanProduceUnits)&& games.strategy.triplea.Properties.getSBRAffectsUnitProduction(m_data))
		{
		    TerritoryAttachment ta = TerritoryAttachment.get(territory);

		    Change change = ChangeFactory.changeUnitProduction(territory, getProduction(territory));
		    m_bridge.addChange(change);
		}*/
		logEvent("Adding units owned by " + m_bridge.getPlayerID().getName() + " to " + territory.getName() + ": " + MyFormatter.unitsToTextNoOwner(units), units);
		m_bridge.addChange(ChangeFactory.addUnits(territory, units));
		return null;
	}
	
	/**
	 * @return gets the production of the territory, ignores wether the
	 *         territory was an original factory
	 */
	protected int getProduction(final Territory territory)
	{
		final TerritoryAttachment ta = TerritoryAttachment.get(territory);
		if (ta != null)
			return ta.getProduction();
		return 0;
		// throw new UnsupportedOperationException("Not implemented");
	}
	
	public String changeTerritoryOwner(final Territory territory, final PlayerID player)
	{
		String result = null;
		if (null != (result = checkEditMode()))
			return result;
		final GameData data = getData();
		// validate this edit
		if (null != (result = EditValidator.validateChangeTerritoryOwner(data, territory, player)))
			return result;
		logEvent("Changing ownership of " + territory.getName() + " from " + territory.getOwner().getName() + " to " + player.getName(), territory);
		if (data.getRelationshipTracker().isAllied(territory.getOwner(), player))
		{
			// change ownership of friendly factories
			final Collection<Unit> units = territory.getUnits().getMatches(Matches.UnitIsFactory);
			for (final Unit unit : units)
				m_bridge.addChange(ChangeFactory.changeOwner(unit, player, territory));
		}
		else
		{
			final CompositeMatch<Unit> enemyNonCom = new CompositeMatchAnd<Unit>();
			enemyNonCom.add(Matches.UnitIsFactoryOrIsInfrastructure);
			enemyNonCom.add(Matches.enemyUnit(player, data));
			final Collection<Unit> units = territory.getUnits().getMatches(enemyNonCom);
			// mark no movement for enemy units
			m_bridge.addChange(ChangeFactory.markNoMovementChange(units));
			// change ownership of enemy AA and factories
			for (final Unit unit : units)
				m_bridge.addChange(ChangeFactory.changeOwner(unit, player, territory));
		}
		// change ownership of territory
		m_bridge.addChange(ChangeFactory.changeOwner(territory, player));
		return null;
	}
	
	public String changePUs(final PlayerID player, final int newTotal)
	{
		String result = null;
		if (null != (result = checkEditMode()))
			return result;
		final Resource PUs = getData().getResourceList().getResource(Constants.PUS);
		final int oldTotal = player.getResources().getQuantity(PUs);
		if (oldTotal == newTotal)
			return "New PUs total is unchanged";
		if (newTotal < 0)
			return "New PUs total is invalid";
		logEvent("Changing PUs for " + player.getName() + " from " + oldTotal + " to " + newTotal, null);
		m_bridge.addChange(ChangeFactory.changeResourcesChange(player, PUs, (newTotal - oldTotal)));
		return null;
	}
	
	public String changeTechTokens(final PlayerID player, final int newTotal)
	{
		String result = null;
		if (null != (result = checkEditMode()))
			return result;
		final Resource techTokens = getData().getResourceList().getResource(Constants.TECH_TOKENS);
		final int oldTotal = player.getResources().getQuantity(techTokens);
		if (oldTotal == newTotal)
			return "New token total is unchanged";
		if (newTotal < 0)
			return "New token total is invalid";
		logEvent("Changing tech tokens for " + player.getName() + " from " + oldTotal + " to " + newTotal, null);
		m_bridge.addChange(ChangeFactory.changeResourcesChange(player, techTokens, (newTotal - oldTotal)));
		return null;
	}
	
	public String addComment(final String message)
	{
		String result = null;
		if (null != (result = checkPlayerID()))
			return result;
		logEvent("COMMENT: " + message, null);
		return null;
	}
	
	// We don't know the current context, so we need to figure
	// out whether it makes more sense to log a new event or a child.
	// If any child events came before us, then we'll log a child event.
	// Otherwise, we'll log a new event.
	private void logEvent(final String message, final Object data)
	{
		// find last event node
		boolean foundChild = false;
		final GameData game_data = getData();
		game_data.acquireReadLock();
		try
		{
			HistoryNode curNode = game_data.getHistory().getLastNode();
			while (!(curNode instanceof Step) && !(curNode instanceof Event))
			{
				if (curNode instanceof EventChild)
				{
					foundChild = true;
					break;
				}
				curNode = (HistoryNode) curNode.getPreviousNode();
			}
		} finally
		{
			game_data.releaseReadLock();
		}
		if (foundChild)
			m_bridge.getHistoryWriter().addChildToEvent(message, game_data);
		else
		{
			m_bridge.getHistoryWriter().startEvent(message);
			m_bridge.getHistoryWriter().setRenderingData(game_data);
		}
	}
	
	/*
	 * @see games.strategy.engine.delegate.IDelegate#getRemoteType()
	 */
	@Override
	public Class<? extends IRemote> getRemoteType()
	{
		return IEditDelegate.class;
	}
}


@SuppressWarnings("serial")
class EditExtendedDelegateState implements Serializable
{
	Serializable superState;
	// add other variables here:
}