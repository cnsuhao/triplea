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
/*
 * TripleAPlayer.java
 * 
 * Created on November 2, 2001, 8:45 PM
 */
package games.strategy.triplea;

import games.strategy.common.player.AbstractHumanPlayer;
import games.strategy.engine.data.GameData;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.ProductionRule;
import games.strategy.engine.data.RepairRule;
import games.strategy.engine.data.Resource;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.gamePlayer.IGamePlayer;
import games.strategy.net.GUID;
import games.strategy.triplea.attatchments.PlayerAttachment;
import games.strategy.triplea.attatchments.PoliticalActionAttachment;
import games.strategy.triplea.attatchments.TerritoryAttachment;
import games.strategy.triplea.delegate.BidPurchaseDelegate;
import games.strategy.triplea.delegate.DiceRoll;
import games.strategy.triplea.delegate.IBattle;
import games.strategy.triplea.delegate.Matches;
import games.strategy.triplea.delegate.SpecialMoveDelegate;
import games.strategy.triplea.delegate.TechTracker;
import games.strategy.triplea.delegate.dataObjects.BattleListing;
import games.strategy.triplea.delegate.dataObjects.CasualtyDetails;
import games.strategy.triplea.delegate.dataObjects.CasualtyList;
import games.strategy.triplea.delegate.dataObjects.FightBattleDetails;
import games.strategy.triplea.delegate.dataObjects.MoveDescription;
import games.strategy.triplea.delegate.dataObjects.TechResults;
import games.strategy.triplea.delegate.dataObjects.TechRoll;
import games.strategy.triplea.delegate.remote.IAbstractPlaceDelegate;
import games.strategy.triplea.delegate.remote.IBattleDelegate;
import games.strategy.triplea.delegate.remote.IEditDelegate;
import games.strategy.triplea.delegate.remote.IMoveDelegate;
import games.strategy.triplea.delegate.remote.IPoliticsDelegate;
import games.strategy.triplea.delegate.remote.IPurchaseDelegate;
import games.strategy.triplea.delegate.remote.ITechDelegate;
import games.strategy.triplea.formatter.MyFormatter;
import games.strategy.triplea.player.ITripleaPlayer;
import games.strategy.triplea.ui.BattleDisplay;
import games.strategy.triplea.ui.PlaceData;
import games.strategy.triplea.ui.TripleAFrame;
import games.strategy.util.CompositeMatchAnd;
import games.strategy.util.IntegerMap;
import games.strategy.util.Match;
import games.strategy.util.Tuple;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.SwingUtilities;

/**
 * As a rule, nothing that changes GameData should be in here (it should be in a delegate, and done through an IDelegate using a change).
 * 
 * @author Sean Bridges
 * @version 1.0
 */
public class TripleAPlayer extends AbstractHumanPlayer<TripleAFrame> implements IGamePlayer, ITripleaPlayer
{
	/** Creates new TripleAPlayer */
	public TripleAPlayer(final String name, final String type)
	{
		super(name, type);
	}
	
	public void reportError(final String error)
	{
		m_ui.notifyError(error);
	}
	
	public void reportMessage(final String message, final String title)
	{
		m_ui.notifyMessage(message, title);
	}
	
	public void reportPoliticalMessage(final String message)
	{
		m_ui.notifyPoliticalMessage(message);
	}
	
	@Override
	public void start(final String name)
	{
		boolean badStep = false;
		try
		{
			m_ui.setEditDelegate((IEditDelegate) getPlayerBridge().getRemote("edit"));
		} catch (final Exception e)
		{
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				m_ui.getEditModeButtonModel().addActionListener(m_editModeAction);
				m_ui.getEditModeButtonModel().setEnabled(true);
			}
		});
		if (name.endsWith("Bid"))
			purchase(true);
		else if (name.endsWith("Tech"))
			tech();
		else if (name.endsWith("TechActivation"))
		{
		} // the delegate handles everything
		else if (name.endsWith("Purchase"))
			purchase(false);
		else if (name.endsWith("Move"))
		{
			move(name.endsWith("NonCombatMove"), name);
			if (!name.endsWith("NonCombatMove"))
				m_ui.waitForMoveForumPoster(getPlayerID(), getPlayerBridge());
		}
		else if (name.endsWith("Battle"))
			battle();
		else if (name.endsWith("Place"))
			place(name.endsWith("BidPlace"));
		else if (name.endsWith("Politics"))
			politics(true);
		else if (name.endsWith("EndTurn"))
			endTurn();
		else
			badStep = true;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				m_ui.getEditModeButtonModel().setEnabled(false);
				m_ui.getEditModeButtonModel().removeActionListener(m_editModeAction);
				m_ui.setEditDelegate(null);
			}
		});
		if (badStep)
			throw new IllegalArgumentException("Unrecognized step name:" + name);
	}
	
	private final AbstractAction m_editModeAction = new AbstractAction()
	{
		private static final long serialVersionUID = -8076017427171022731L;
		
		public void actionPerformed(final ActionEvent ae)
		{
			final boolean editMode = ((ButtonModel) ae.getSource()).isSelected();
			try
			{
				// Set edit mode
				// All GameDataChangeListeners will be notified upon success
				final IEditDelegate editDelegate = (IEditDelegate) getPlayerBridge().getRemote("edit");
				editDelegate.setEditMode(editMode);
			} catch (final Exception e)
			{
				e.printStackTrace();
				// toggle back to previous state since setEditMode failed
				m_ui.getEditModeButtonModel().setSelected(!m_ui.getEditModeButtonModel().isSelected());
			}
		}
	};
	
	private void politics(final boolean firstRun)
	{
		if (!getPlayerID().amNotDeadYet(getGameData()))
			return;
		if (!games.strategy.triplea.Properties.getUsePolitics(getGameData()))
			return;
		final IPoliticsDelegate iPoliticsDelegate = (IPoliticsDelegate) getPlayerBridge().getRemote();
		final PoliticalActionAttachment actionChoice = m_ui.getPoliticalActionChoice(getPlayerID(), firstRun, iPoliticsDelegate);
		if (actionChoice != null)
		{
			iPoliticsDelegate.attemptAction(actionChoice);
			politics(false);
		}
	}
	
	public boolean acceptPoliticalAction(final String acceptanceQuestion)
	{
		if (!getPlayerID().amNotDeadYet(getGameData()))
			return true;
		return m_ui.acceptPoliticalAction("To " + getPlayerID().getName() + ": " + acceptanceQuestion);
	}
	
	private void tech()
	{
		// can we tech?
		final PlayerID id = getPlayerID();
		getGameData().acquireReadLock();
		try
		{
			if (!isTechDevelopment(getGameData()))
				return;
			final int techCost = TechTracker.getTechCost(id);
			int money = id.getResources().getQuantity(Constants.PUS);
			if (money < techCost)
			{
				final PlayerAttachment pa = PlayerAttachment.get(id);
				if (pa == null)
					return;
				final Collection<PlayerID> helpPay = pa.getHelpPayTechCost();
				if (helpPay == null || helpPay.isEmpty())
					return;
				for (final PlayerID p : helpPay)
				{
					money += p.getResources().getQuantity(Constants.PUS);
				}
				if (money < techCost)
					return;
			}
		} finally
		{
			getGameData().releaseReadLock();
		}
		final TechRoll techRoll = m_ui.getTechRolls(id);
		if (techRoll != null)
		{
			final ITechDelegate techDelegate = (ITechDelegate) getPlayerBridge().getRemote();
			final TechResults techResults = techDelegate.rollTech(techRoll.getRolls(), techRoll.getTech(), techRoll.getNewTokens(), techRoll.getWhoPaysHowMuch());
			if (techResults.isError())
			{
				m_ui.notifyError(techResults.getErrorString());
				tech();
			}
			else
				m_ui.notifyTechResults(techResults);
		}
	}
	
	private void move(final boolean nonCombat, final String stepName)
	{
		final PlayerID id = getPlayerID();
		if (stepName.endsWith("AirborneCombatMove"))
		{
			if (!SpecialMoveDelegate.allowAirborne(id, getGameData()))
				return;
		}
		else if (!hasUnitsThatCanMove(nonCombat))
			return;
		final MoveDescription moveDescription = m_ui.getMove(id, getPlayerBridge(), nonCombat, stepName);
		if (moveDescription == null)
		{
			if (nonCombat)
			{
				if (!canAirLand(true, id))
					move(nonCombat, stepName);
			}
			else
			{
				if (canUnitsFight())
					move(nonCombat, stepName);
			}
			return;
		}
		final IMoveDelegate moveDel = (IMoveDelegate) getPlayerBridge().getRemote();
		final String error = moveDel.move(moveDescription.getUnits(), moveDescription.getRoute(), moveDescription.getTransportsThatCanBeLoaded(), moveDescription.getDependentUnits());
		if (error != null)
			m_ui.notifyError(error);
		move(nonCombat, stepName);
	}
	
	private boolean canAirLand(final boolean movePhase, final PlayerID player)
	{
		Collection<Territory> airCantLand;
		if (movePhase)
			airCantLand = ((IMoveDelegate) getPlayerBridge().getRemote()).getTerritoriesWhereAirCantLand(player);
		else
			airCantLand = ((IAbstractPlaceDelegate) getPlayerBridge().getRemote()).getTerritoriesWhereAirCantLand();
		if (airCantLand.isEmpty())
			return true;
		else
		{
			final StringBuilder buf = new StringBuilder("Air in following territories cant land:");
			final Iterator<Territory> iter = airCantLand.iterator();
			while (iter.hasNext())
			{
				buf.append((iter.next()).getName());
				buf.append(" ");
			}
			if (!m_ui.getOKToLetAirDie(getPlayerID(), buf.toString(), movePhase))
				return false;
			return true;
		}
	}
	
	private boolean canUnitsFight()
	{
		Collection<Territory> unitsCantFight;
		unitsCantFight = ((IMoveDelegate) getPlayerBridge().getRemote()).getTerritoriesWhereUnitsCantFight();
		if (unitsCantFight.isEmpty())
			return false;
		else
		{
			final StringBuilder buf = new StringBuilder("Units in the following territories will die:");
			final Iterator<Territory> iter = unitsCantFight.iterator();
			while (iter.hasNext())
			{
				buf.append((iter.next()).getName());
				buf.append(" ");
			}
			if (m_ui.getOKToLetUnitsDie(getPlayerID(), buf.toString(), true))
				return false;
			return true;
		}
	}
	
	private boolean hasUnitsThatCanMove(final boolean nonCom)
	{
		final PlayerID id = getPlayerID();
		final CompositeMatchAnd<Unit> moveableUnitOwnedByMe = new CompositeMatchAnd<Unit>();
		moveableUnitOwnedByMe.add(Matches.unitIsOwnedBy(id));
		moveableUnitOwnedByMe.add(Matches.UnitCanMove);
		// if not non combat, can not move aa units
		if (!nonCom)
			moveableUnitOwnedByMe.add(Matches.UnitCanNotMoveDuringCombatMove.invert());
		getGameData().acquireReadLock();
		try
		{
			for (final Territory item : getGameData().getMap().getTerritories())
			{
				if (item.getUnits().someMatch(moveableUnitOwnedByMe))
				{
					return true;
				}
			}
			return false;
		} finally
		{
			getGameData().releaseReadLock();
		}
	}
	
	private void purchase(final boolean bid)
	{
		final PlayerID id = getPlayerID();
		if (bid)
		{
			if (!BidPurchaseDelegate.doesPlayerHaveBid(getGameData(), id))
				return;
		}
		// NoPUPurchaseDelegate will run first, before this section. After running, the engine will wait for the player by coming here. Exit if we are a No PU delegate purchase
		if (getPlayerBridge().getStepName().endsWith("NoPUPurchase"))
			return;
		// we have no production frontier
		else if (id.getProductionFrontier() == null || id.getProductionFrontier().getRules().isEmpty())
		{
			return;
		}
		else
		{
			// if my capital is captured, I can't produce, but I may have PUs if I captured someone else's capital
			final List<Territory> capitalsListOriginal = new ArrayList<Territory>(TerritoryAttachment.getAllCapitals(id, getGameData()));
			final List<Territory> capitalsListOwned = new ArrayList<Territory>(TerritoryAttachment.getAllCurrentlyOwnedCapitals(id, getGameData()));
			final PlayerAttachment pa = PlayerAttachment.get(id);
			if ((!capitalsListOriginal.isEmpty() && capitalsListOwned.isEmpty()) || (pa != null && pa.getRetainCapitalProduceNumber() > capitalsListOwned.size()))
				return;
		}
		
		if (!canWePurchaseOrRepair())
			return;
		
		// Check if any factories need to be repaired
		String error = null;
		IPurchaseDelegate purchaseDel = (IPurchaseDelegate) getPlayerBridge().getRemote();
		if (id.getRepairFrontier() != null && id.getRepairFrontier().getRules() != null && !id.getRepairFrontier().getRules().isEmpty())
		{
			if (isSBRAffectsUnitProduction(getGameData()))
			{
				final GameData data = getGameData();
				final Collection<Territory> bombedTerrs = new ArrayList<Territory>();
				for (final Territory t : Match.getMatches(data.getMap().getTerritories(), Matches.territoryHasOwnedIsFactoryOrCanProduceUnits(data, id)))
				{
					final TerritoryAttachment ta = TerritoryAttachment.get(t);
					// changed this to > from !=
					if (ta.getProduction() > ta.getUnitProduction())
					{
						bombedTerrs.add(t);
					}
				}
				final Collection<Unit> damagedUnits = new ArrayList<Unit>();
				final Match<Unit> myFactories = new CompositeMatchAnd<Unit>(Matches.unitIsOwnedBy(id), Matches.UnitCanBeDamaged);
				for (final Territory t : bombedTerrs)
				{
					damagedUnits.addAll(Match.getMatches(t.getUnits().getUnits(), myFactories));
				}
				if (bombedTerrs.size() > 0 && damagedUnits.size() > 0)
				{
					final HashMap<Unit, IntegerMap<RepairRule>> repair = m_ui.getRepair(id, bid);
					if (repair != null)
					{
						purchaseDel = (IPurchaseDelegate) getPlayerBridge().getRemote();
						error = purchaseDel.purchaseRepair(repair);
						if (error != null)
						{
							m_ui.notifyError(error);
							// dont give up, keep going
							purchase(bid);
						}
					}
				}
			}
			else if (isDamageFromBombingDoneToUnitsInsteadOfTerritories(getGameData()))
			{
				final GameData data = getGameData();
				final Match<Unit> myDamaged = new CompositeMatchAnd<Unit>(Matches.unitIsOwnedBy(id), Matches.UnitHasSomeUnitDamage());
				final Collection<Unit> damagedUnits = new ArrayList<Unit>();
				for (final Territory t : data.getMap().getTerritories())
				{
					damagedUnits.addAll(Match.getMatches(t.getUnits().getUnits(), myDamaged));
				}
				if (damagedUnits.size() > 0)
				{
					final HashMap<Unit, IntegerMap<RepairRule>> repair = m_ui.getRepair(id, bid);
					if (repair != null)
					{
						purchaseDel = (IPurchaseDelegate) getPlayerBridge().getRemote(); // TODO: veq fix
						error = purchaseDel.purchaseRepair(repair);
						if (error != null)
						{
							m_ui.notifyError(error);
							// dont give up, keep going
							purchase(bid);
						}
					}
				}
			}
		}
		final IntegerMap<ProductionRule> prod = m_ui.getProduction(id, bid);
		if (prod == null)
			return;
		purchaseDel = (IPurchaseDelegate) getPlayerBridge().getRemote();
		error = purchaseDel.purchase(prod);
		if (error != null)
		{
			m_ui.notifyError(error);
			// dont give up, keep going
			purchase(bid);
		}
	}
	
	private boolean canWePurchaseOrRepair()
	{
		final PlayerID id = getPlayerID();
		if (id.getProductionFrontier() != null && id.getProductionFrontier().getRules() != null)
		{
			for (final ProductionRule rule : id.getProductionFrontier().getRules())
			{
				if (id.getResources().has(rule.getCosts()))
					return true;
			}
		}
		if (id.getRepairFrontier() != null && id.getRepairFrontier().getRules() != null)
		{
			for (final RepairRule rule : id.getRepairFrontier().getRules())
			{
				if (id.getResources().has(rule.getCosts()))
					return true;
			}
		}
		return false;
	}
	
	private void battle()
	{
		final PlayerID id = getPlayerID();
		while (true)
		{
			final IBattleDelegate battleDel = (IBattleDelegate) getPlayerBridge().getRemote();
			final BattleListing battles = battleDel.getBattles();
			if (battles.isEmpty())
			{
				final IBattle battle = battleDel.getCurrentBattle();
				if (battle != null)
				{
					// this should never happen, but it happened once....
					System.err.println("Current battle exists but is not on pending list:  " + battle.toString());
					battleDel.fightCurrentBattle();
				}
				return;
			}
			final FightBattleDetails details = m_ui.getBattle(id, battles.getBattles(), battles.getStrategicRaids());
			if (getPlayerBridge().isGameOver())
				return;
			final String error = battleDel.fightBattle(details.getWhere(), details.isBombingRaid());
			if (error != null)
				m_ui.notifyError(error);
		}
	}
	
	private void place(final boolean bid)
	{
		final PlayerID id = getPlayerID();
		final IAbstractPlaceDelegate placeDel = (IAbstractPlaceDelegate) getPlayerBridge().getRemote();
		// nothing to place
		// nothing placed
		if (id.getUnits().size() == 0 && placeDel.getPlacementsMade() == 0)
			return;
		while (true)
		{
			final PlaceData data = m_ui.waitForPlace(id, bid, getPlayerBridge());
			if (data == null)
			{
				// this only happens in lhtr rules
				if (canAirLand(false, id))
					return;
				else
					continue;
			}
			final String error = placeDel.placeUnits(data.getUnits(), data.getAt());
			if (error != null)
				m_ui.notifyError(error);
		}
	}
	
	private void endTurn()
	{
		m_ui.waitForEndTurn(getPlayerID(), getPlayerBridge());
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#selectCasualties(java.lang.String, java.util.Collection, java.util.Map, int, java.lang.String, games.strategy.triplea.delegate.DiceRoll, games.strategy.engine.data.PlayerID, java.util.List)
	 */
	public CasualtyDetails selectCasualties(final Collection<Unit> selectFrom, final Map<Unit, Collection<Unit>> dependents, final int count, final String message, final DiceRoll dice,
				final PlayerID hit, final CasualtyList defaultCasualties, final GUID battleID)
	{
		return m_ui.getBattlePanel().getCasualties(selectFrom, dependents, count, message, dice, hit, defaultCasualties, battleID);
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#selectFixedDice(int, int, boolean, java.lang.String)
	 */
	public int[] selectFixedDice(final int numDice, final int hitAt, final boolean hitOnlyIfEquals, final String title, final int diceSides)
	{
		return m_ui.selectFixedDice(numDice, hitAt, hitOnlyIfEquals, title, diceSides);
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#selectBombardingTerritory(games.strategy.engine.data.Unit, games.strategy.engine.data.Territory, java.util.Collection, boolean)
	 */
	public Territory selectBombardingTerritory(final Unit unit, final Territory unitTerritory, final Collection<Territory> territories, final boolean noneAvailable)
	{
		return m_ui.getBattlePanel().getBombardment(unit, unitTerritory, territories, noneAvailable);
	}
	
	/*
	 * Ask if the player wants to attack subs
	 */
	public boolean selectAttackSubs(final Territory unitTerritory)
	{
		return m_ui.getBattlePanel().getAttackSubs(unitTerritory);
	}
	
	/*
	 * Ask if the player wants to attack transports
	 */
	public boolean selectAttackTransports(final Territory unitTerritory)
	{
		return m_ui.getBattlePanel().getAttackTransports(unitTerritory);
	}
	
	/*
	 * Ask if the player wants to attack units
	 */
	public boolean selectAttackUnits(final Territory unitTerritory)
	{
		return m_ui.getBattlePanel().getAttackUnits(unitTerritory);
	}
	
	/*
	 * Ask if the player wants to shore bombard
	 */
	public boolean selectShoreBombard(final Territory unitTerritory)
	{
		return m_ui.getBattlePanel().getShoreBombard(unitTerritory);
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#shouldBomberBomb(games.strategy.engine.data.Territory)
	 */
	public boolean shouldBomberBomb(final Territory territory)
	{
		return m_ui.getStrategicBombingRaid(territory);
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#shouldBomberBomb(games.strategy.engine.data.Territory)
	 */
	public Unit whatShouldBomberBomb(final Territory territory, final Collection<Unit> potentialTargets, final Collection<Unit> bombers)
	{
		return m_ui.getStrategicBombingRaidTarget(territory, potentialTargets, bombers);
	}
	
	public Territory whereShouldRocketsAttack(final Collection<Territory> candidates, final Territory from)
	{
		return m_ui.getRocketAttack(candidates, from);
	}
	
	/* (non-Javadoc)
	 * @see games.strategy.triplea.player.ITripleaPlayer#getNumberOfFightersToMoveToNewCarrier(java.util.Collection, games.strategy.engine.data.Territory)
	 */
	public Collection<Unit> getNumberOfFightersToMoveToNewCarrier(final Collection<Unit> fightersThatCanBeMoved, final Territory from)
	{
		return m_ui.moveFightersToCarrier(fightersThatCanBeMoved, from);
	}
	
	/*
	 * @see games.strategy.triplea.player.ITripleaPlayer#selectTerritoryForAirToLand(java.util.Collection, java.lang.String)
	 */
	public Territory selectTerritoryForAirToLand(final Collection<Territory> candidates, final Territory currentTerritory, final String unitMessage)
	{
		return m_ui.selectTerritoryForAirToLand(candidates, currentTerritory, unitMessage);
	}
	
	/* (non-Javadoc)
	 * @see games.strategy.triplea.player.ITripleaPlayer#confirmMoveInFaceOfAA(java.util.Collection)
	 */
	public boolean confirmMoveInFaceOfAA(final Collection<Territory> aaFiringTerritories)
	{
		final String question = "AA guns will fire in " + MyFormatter.territoriesToText(aaFiringTerritories, "and") + ", do you still want to move?";
		return m_ui.getOK(question);
	}
	
	public boolean confirmMoveKamikaze()
	{
		final String question = "Not all air units in destination territory can land, do you still want to move?";
		return m_ui.getOK(question);
	}
	
	public boolean confirmMoveHariKari()
	{
		final String question = "All units in destination territory will automatically die, do you still want to move?";
		return m_ui.getOK(question);
	}
	
	/* (non-Javadoc)
	 * @see games.strategy.triplea.player.ITripleaPlayer#retreatQuery(games.strategy.net.GUID, boolean, java.util.Collection, java.lang.String, java.lang.String)
	 */
	public Territory retreatQuery(final GUID battleID, final boolean submerge, final Collection<Territory> possibleTerritories, final String message)
	{
		return m_ui.getBattlePanel().getRetreat(battleID, message, possibleTerritories, submerge);
	}
	
	/* (non-Javadoc)
	 * @see games.strategy.triplea.player.ITripleaPlayer#scrambleQuery(games.strategy.net.GUID, java.util.Collection, java.lang.String, java.lang.String)
	 
	public Collection<Unit> scrambleQuery(final GUID battleID, final Collection<Territory> possibleTerritories, final String message, final PlayerID player)
	{
		return m_ui.getBattlePanel().getScramble(getPlayerBridge(), battleID, message, possibleTerritories, player);
	}*/

	public HashMap<Territory, Collection<Unit>> scrambleUnitsQuery(final Territory scrambleTo, final Map<Territory, Tuple<Collection<Unit>, Collection<Unit>>> possibleScramblers)
	{
		return m_ui.scrambleUnitsQuery(scrambleTo, possibleScramblers);
	}
	
	public Collection<Unit> selectUnitsQuery(final Territory current, final Collection<Unit> possible, final String message)
	{
		return m_ui.selectUnitsQuery(current, possible, message);
	}
	
	public void confirmEnemyCasualties(final GUID battleId, final String message, final PlayerID hitPlayer)
	{
		// no need, we have already confirmed since we are firing player
		if (m_ui.playing(hitPlayer))
			return;
		// we dont want to confirm enemy casualties
		if (!BattleDisplay.getShowEnemyCasualtyNotification())
			return;
		m_ui.getBattlePanel().confirmCasualties(battleId, message);
	}
	
	public void confirmOwnCasualties(final GUID battleId, final String message)
	{
		m_ui.getBattlePanel().confirmCasualties(battleId, message);
	}
	
	public final boolean isSBRAffectsUnitProduction(final GameData data)
	{
		return games.strategy.triplea.Properties.getSBRAffectsUnitProduction(data);
	}
	
	public final boolean isDamageFromBombingDoneToUnitsInsteadOfTerritories(final GameData data)
	{
		return games.strategy.triplea.Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data);
	}
	
	private static boolean isTechDevelopment(final GameData data)
	{
		return games.strategy.triplea.Properties.getTechDevelopment(data);
	}
	
	public HashMap<Territory, HashMap<Unit, IntegerMap<Resource>>> selectKamikazeSuicideAttacks(final HashMap<Territory, Collection<Unit>> possibleUnitsToAttack)
	{
		final PlayerID id = getPlayerID();
		final PlayerAttachment pa = PlayerAttachment.get(id);
		if (pa == null)
			return null;
		final IntegerMap<Resource> resourcesAndAttackValues = pa.getSuicideAttackResources();
		if (resourcesAndAttackValues.size() <= 0)
			return null;
		final IntegerMap<Resource> playerResourceCollection = id.getResources().getResourcesCopy();
		final IntegerMap<Resource> attackTokens = new IntegerMap<Resource>();
		for (final Resource possible : resourcesAndAttackValues.keySet())
		{
			final int amount = playerResourceCollection.getInt(possible);
			if (amount > 0)
				attackTokens.put(possible, amount);
		}
		if (attackTokens.size() <= 0)
			return null;
		final HashMap<Territory, HashMap<Unit, IntegerMap<Resource>>> rVal = new HashMap<Territory, HashMap<Unit, IntegerMap<Resource>>>();
		for (final Entry<Resource, Integer> entry : attackTokens.entrySet())
		{
			final Resource r = entry.getKey();
			final int max = entry.getValue();
			final HashMap<Territory, IntegerMap<Unit>> selection = m_ui.selectKamikazeSuicideAttacks(possibleUnitsToAttack, r, max);
			for (final Entry<Territory, IntegerMap<Unit>> selectionEntry : selection.entrySet())
			{
				final Territory t = selectionEntry.getKey();
				HashMap<Unit, IntegerMap<Resource>> currentTerr = rVal.get(t);
				if (currentTerr == null)
					currentTerr = new HashMap<Unit, IntegerMap<Resource>>();
				for (final Entry<Unit, Integer> unitEntry : selectionEntry.getValue().entrySet())
				{
					final Unit u = unitEntry.getKey();
					IntegerMap<Resource> currentUnit = currentTerr.get(u);
					if (currentUnit == null)
						currentUnit = new IntegerMap<Resource>();
					currentUnit.add(r, unitEntry.getValue());
					currentTerr.put(u, currentUnit);
				}
				rVal.put(t, currentTerr);
			}
		}
		
		return rVal;
	}
}