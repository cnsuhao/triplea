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

import games.strategy.engine.data.Change;
import games.strategy.engine.data.ChangeFactory;
import games.strategy.engine.data.CompositeChange;
import games.strategy.engine.data.GameData;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Resource;
import games.strategy.engine.data.Route;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.delegate.IDelegateBridge;
import games.strategy.triplea.Constants;
import games.strategy.triplea.Properties;
import games.strategy.triplea.TripleAUnit;
import games.strategy.triplea.attatchments.TerritoryAttachment;
import games.strategy.triplea.attatchments.UnitAttachment;
import games.strategy.triplea.formatter.MyFormatter;
import games.strategy.triplea.player.ITripleaPlayer;
import games.strategy.util.CompositeMatch;
import games.strategy.util.CompositeMatchAnd;
import games.strategy.util.IntegerMap;
import games.strategy.util.Match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Logic to fire rockets.
 */
public class RocketsFireHelper
{
	private boolean isWW2V2(final GameData data)
	{
		return games.strategy.triplea.Properties.getWW2V2(data);
	}
	
	private boolean isAllRocketsAttack(final GameData data)
	{
		return games.strategy.triplea.Properties.getAllRocketsAttack(data);
	}
	
	/*private boolean isRocketsCanFlyOverImpassables(GameData data)
	{
	    return games.strategy.triplea.Properties.getRocketsCanFlyOverImpassables(data);
	}*/
	/**
	 * @return
	 */
	private boolean isSBRAffectsUnitProduction(final GameData data)
	{
		return games.strategy.triplea.Properties.getSBRAffectsUnitProduction(data);
	}
	
	private boolean isDamageFromBombingDoneToUnitsInsteadOfTerritories(final GameData data)
	{
		return games.strategy.triplea.Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data);
	}
	
	private boolean isOneRocketAttackPerFactory(final GameData data)
	{
		return games.strategy.triplea.Properties.getRocketAttackPerFactoryRestricted(data);
	}
	
	private boolean isPUCap(final GameData data)
	{
		return games.strategy.triplea.Properties.getPUCap(data);
	}
	
	private boolean isLimitRocketDamagePerTurn(final GameData data)
	{
		return games.strategy.triplea.Properties.getLimitRocketDamagePerTurn(data);
	}
	
	private boolean isLimitRocketDamageToProduction(final GameData data)
	{
		return games.strategy.triplea.Properties.getLimitRocketDamageToProduction(data);
	}
	
	public RocketsFireHelper()
	{
	}
	
	public void fireRockets(final IDelegateBridge bridge, final PlayerID player)
	{
		final GameData data = bridge.getData();
		final Set<Territory> rocketTerritories = getTerritoriesWithRockets(data, player);
		if (rocketTerritories.isEmpty())
		{
			getRemote(bridge).reportMessage("No rockets to fire", "No rockets to fire");
			return;
		}
		// TODO this is weird! Check the parens
		if ((isWW2V2(data) || isAllRocketsAttack(data)) || isOneRocketAttackPerFactory(data))
			fireWW2V2(bridge, player, rocketTerritories);
		else
			fireWW2V1(bridge, player, rocketTerritories);
	}
	
	private void fireWW2V2(final IDelegateBridge bridge, final PlayerID player, final Set<Territory> rocketTerritories)
	{
		final GameData data = bridge.getData();
		final Set<Territory> attackedTerritories = new HashSet<Territory>();
		for (final Territory territory : rocketTerritories)
		{
			final Set<Territory> targets = getTargetsWithinRange(territory, data, player);
			targets.removeAll(attackedTerritories);
			if (targets.isEmpty())
				continue;
			final Territory target = getTarget(targets, player, bridge, territory);
			if (target != null)
			{
				attackedTerritories.add(target);
				fireRocket(player, target, bridge, territory);
			}
		}
	}
	
	private void fireWW2V1(final IDelegateBridge bridge, final PlayerID player, final Set<Territory> rocketTerritories)
	{
		final GameData data = bridge.getData();
		final Set<Territory> targets = new HashSet<Territory>();
		for (final Territory territory : rocketTerritories)
		{
			targets.addAll(getTargetsWithinRange(territory, data, player));
		}
		if (targets.isEmpty())
		{
			getRemote(bridge).reportMessage("No targets to attack with rockets", "No targets to attack with rockets");
			return;
		}
		final Territory attacked = getTarget(targets, player, bridge, null);
		if (attacked != null)
			fireRocket(player, attacked, bridge, null);
	}
	
	Set<Territory> getTerritoriesWithRockets(final GameData data, final PlayerID player)
	{
		final Set<Territory> territories = new HashSet<Territory>();
		final CompositeMatch<Unit> ownedRockets = new CompositeMatchAnd<Unit>();
		ownedRockets.add(Matches.UnitIsRocket);
		ownedRockets.add(Matches.unitIsOwnedBy(player));
		final BattleTracker tracker = MoveDelegate.getBattleTracker(data);
		for (final Territory current : data.getMap())
		{
			if (current.isWater())
				continue;
			if (tracker.wasConquered(current))
				continue;
			if (current.getUnits().someMatch(ownedRockets))
				territories.add(current);
		}
		return territories;
	}
	
	private Set<Territory> getTargetsWithinRange(final Territory territory, final GameData data, final PlayerID player)
	{
		final Collection<Territory> possible = data.getMap().getNeighbors(territory, 3);
		final Set<Territory> hasFactory = new HashSet<Territory>();
		// boolean rocketsOverImpassables = isRocketsCanFlyOverImpassables(data);
		final Match<Territory> impassable = Matches.TerritoryIsNotImpassable;
		for (final Territory current : possible)
		{
			final Route route = data.getMap().getRoute(territory, current, impassable);
			// TODO EW: this assumes range 3 territories for Rockets, doesn't take movementCost into account. //TODO veq: make new unit attachment for rocket range, defaults to 3 movement cost.
			if (route != null && route.numberOfSteps() <= 3)
			{
				if (current.getUnits().someMatch(new CompositeMatchAnd<Unit>(Matches.enemyUnit(player, data), Matches.UnitIsAtMaxDamageOrNotCanBeDamaged(current).invert())))
					hasFactory.add(current);
			}
		}
		return hasFactory;
	}
	
	private Territory getTarget(final Collection<Territory> targets, final PlayerID player, final IDelegateBridge bridge, final Territory from)
	{
		// ask even if there is only once choice
		// that will allow the user to not attack if he doesnt want to
		return ((ITripleaPlayer) bridge.getRemote()).whereShouldRocketsAttack(targets, from);
	}
	
	private void fireRocket(final PlayerID player, final Territory attackedTerritory, final IDelegateBridge bridge, final Territory attackFrom)
	{
		final GameData data = bridge.getData();
		final PlayerID attacked = attackedTerritory.getOwner();
		final Resource PUs = data.getResourceList().getResource(Constants.PUS);
		// int cost = bridge.getRandom(Constants.MAX_DICE);
		final boolean SBRAffectsUnitProd = isSBRAffectsUnitProduction(data);
		final boolean DamageFromBombingDoneToUnits = isDamageFromBombingDoneToUnitsInsteadOfTerritories(data);
		// unit damage vs territory damage
		final Collection<Unit> enemyUnits = attackedTerritory.getUnits().getMatches(Matches.enemyUnit(player, data));
		final Collection<Unit> enemyTargets = Match.getMatches(enemyUnits, Matches.UnitIsAtMaxDamageOrNotCanBeDamaged(attackedTerritory).invert());
		final Collection<Unit> targets = new ArrayList<Unit>();
		if (!SBRAffectsUnitProd && DamageFromBombingDoneToUnits)
		{
			Unit target;
			if (enemyTargets.size() == 1)
				target = enemyTargets.iterator().next();
			else
			{
				final ITripleaPlayer iplayer = (ITripleaPlayer) bridge.getRemote(player);
				target = iplayer.whatShouldBomberBomb(attackedTerritory, enemyTargets);
			}
			if (target == null)
				throw new IllegalStateException("No Targets in " + attackedTerritory.getName());
			targets.add(target);
		}
		int cost = 0;
		if (!games.strategy.triplea.Properties.getLL_DAMAGE_ONLY(data))
			cost = bridge.getRandom(data.getDiceSides(), "Rocket fired by " + player.getName() + " at " + attacked.getName());
		else
		{
			final CompositeMatch<Unit> ownedRockets = new CompositeMatchAnd<Unit>(Matches.unitIsOwnedBy(player), Matches.UnitIsRocket);
			final Collection<Unit> rockets = new ArrayList<Unit>(Match.getMatches(attackFrom.getUnits().getUnits(), ownedRockets));
			int highestMaxDice = 0;
			int highestBonus = 0;
			final int diceSides = data.getDiceSides();
			for (final Unit u : rockets)
			{
				final UnitAttachment ua = UnitAttachment.get(u.getType());
				int maxDice = ua.getBombingMaxDieSides();
				int bonus = ua.getBombingBonus();
				if (maxDice < 0 && bonus < 0 && diceSides >= 5)
				{
					maxDice = (diceSides + 1) / 3;
					bonus = (diceSides + 1) / 3;
				}
				if (bonus < 0)
					bonus = 0;
				if (maxDice < 0)
					maxDice = diceSides;
				if ((bonus + (maxDice + 1) / 2) > (highestBonus + (highestMaxDice + 1) / 2))
				{
					highestMaxDice = maxDice;
					highestBonus = bonus;
				}
			}
			if (highestMaxDice > 0)
				cost = bridge.getRandom(highestMaxDice, "Rocket fired by " + player.getName() + " at " + attacked.getName()) + highestBonus;
			else
				cost = highestBonus;
		}
		// account for 0 base
		cost++;
		final TerritoryAttachment ta = TerritoryAttachment.get(attackedTerritory);
		int territoryProduction = ta.getProduction();
		int unitProduction = 0;
		if (SBRAffectsUnitProd)
		{
			// get current production
			unitProduction = ta.getUnitProduction();
			// Detemine the min that can be taken as losses
			// int alreadyLost = DelegateFinder.moveDelegate(data).PUsAlreadyLost(attackedTerritory);
			final int alreadyLost = territoryProduction - unitProduction;
			final int limit = 2 * territoryProduction - alreadyLost;
			cost = Math.min(cost, limit);
			// Record production lost
			// DelegateFinder.moveDelegate(data).PUsLost(attackedTerritory, cost);
			final Collection<Unit> damagedFactory = Match.getMatches(attackedTerritory.getUnits().getUnits(), Matches.UnitIsFactoryOrCanBeDamaged);
			final IntegerMap<Unit> hits = new IntegerMap<Unit>();
			for (final Unit factory : damagedFactory)
			{
				hits.put(factory, 1);
			}
			bridge.addChange(ChangeFactory.unitsHit(hits));
			/* Change change = ChangeFactory.attachmentPropertyChange(ta, (new Integer(unitProduction - cost)).toString(), "unitProduction");
			 bridge.addChange(change);
			 bridge.getHistoryWriter().addChildToEvent("Rocket attack costs " + cost + " production.");*/
		}
		else if (DamageFromBombingDoneToUnits && !targets.isEmpty())
		{
			// we are doing damage to 'target', not to the territory
			final Unit target = targets.iterator().next();
			// UnitAttachment ua = UnitAttachment.get(target.getType());
			final TripleAUnit taUnit = (TripleAUnit) target;
			final int damageLimit = taUnit.getHowMuchMoreDamageCanThisUnitTake(target, attackedTerritory);
			cost = Math.max(0, Math.min(cost, damageLimit));
			final int totalDamage = taUnit.getUnitDamage() + cost;
			// Record production lost
			// DelegateFinder.moveDelegate(data).PUsLost(attackedTerritory, cost);
			// apply the hits to the targets
			final IntegerMap<Unit> hits = new IntegerMap<Unit>();
			final CompositeChange change = new CompositeChange();
			hits.put(target, 1);
			change.add(ChangeFactory.unitPropertyChange(target, totalDamage, TripleAUnit.UNIT_DAMAGE));
			// taUnit.setUnitDamage(totalDamage);
			bridge.addChange(ChangeFactory.unitsHit(hits));
			bridge.addChange(change);
		}
		// in WW2V2, limit rocket attack cost to production value of factory.
		else if (isWW2V2(data) || isLimitRocketDamageToProduction(data))
		{
			// If we are limiting total PUs lost then take that into account
			if (isPUCap(data) || isLimitRocketDamagePerTurn(data))
			{
				final int alreadyLost = DelegateFinder.moveDelegate(data).PUsAlreadyLost(attackedTerritory);
				territoryProduction -= alreadyLost;
				territoryProduction = Math.max(0, territoryProduction);
			}
			if (cost > territoryProduction)
			{
				cost = territoryProduction;
			}
		}
		// Record the PUs lost
		DelegateFinder.moveDelegate(data).PUsLost(attackedTerritory, cost);
		if (SBRAffectsUnitProd)
		{
			getRemote(bridge).reportMessage("Rocket attack in " + attackedTerritory.getName() + " costs: " + cost + " production.",
						"Rocket attack in " + attackedTerritory.getName() + " costs: " + cost + " production.");
			bridge.getHistoryWriter().startEvent("Rocket attack in " + attackedTerritory.getName() + " costs: " + cost + " production.");
			final Change change = ChangeFactory.attachmentPropertyChange(ta, (new Integer(unitProduction - cost)).toString(), "unitProduction");
			bridge.addChange(change);
		}
		else if (DamageFromBombingDoneToUnits && !targets.isEmpty())
		{
			getRemote(bridge).reportMessage("Rocket attack in " + attackedTerritory.getName() + " does " + cost + " damage to " + targets.iterator().next(),
						"Rocket attack in " + attackedTerritory.getName() + " does " + cost + " damage to " + targets.iterator().next());
			bridge.getHistoryWriter().startEvent("Rocket attack in " + attackedTerritory.getName() + " does " + cost + " damage to " + targets.iterator().next());
		}
		else
		{
			cost *= Properties.getPU_Multiplier(data);
			getRemote(bridge).reportMessage("Rocket attack in " + attackedTerritory.getName() + " costs:" + cost, "Rocket attack in " + attackedTerritory.getName() + " costs:" + cost);
			// Trying to remove more PUs than the victim has is A Bad Thing[tm]
			final int availForRemoval = attacked.getResources().getQuantity(PUs);
			if (cost > availForRemoval)
				cost = availForRemoval;
			final String transcriptText = attacked.getName() + " lost " + cost + " PUs to rocket attack by " + player.getName();
			bridge.getHistoryWriter().startEvent(transcriptText);
			final Change rocketCharge = ChangeFactory.changeResourcesChange(attacked, PUs, -cost);
			bridge.addChange(rocketCharge);
		}
		// this is null in WW2V1
		if (attackFrom != null)
		{
			final List<Unit> units = attackFrom.getUnits().getMatches(new CompositeMatchAnd<Unit>(Matches.UnitIsRocket, Matches.unitIsOwnedBy(player)));
			if (units.size() > 0)
			{
				// only one fired
				final Change change = ChangeFactory.markNoMovementChange(Collections.singleton(units.get(0)));
				bridge.addChange(change);
			}
			else
			{
				throw new IllegalStateException("No rockets?" + attackFrom.getUnits().getUnits());
			}
		}
		// kill any units that can die if they have reached max damage (veqryn)
		if (Match.someMatch(targets, Matches.UnitCanDieFromReachingMaxDamage))
		{
			final List<Unit> unitsCanDie = Match.getMatches(targets, Matches.UnitCanDieFromReachingMaxDamage);
			unitsCanDie.retainAll(Match.getMatches(unitsCanDie, Matches.UnitIsAtMaxDamageOrNotCanBeDamaged(attackedTerritory)));
			if (!unitsCanDie.isEmpty())
			{
				// targets.removeAll(unitsCanDie);
				final Change removeDead = ChangeFactory.removeUnits(attackedTerritory, unitsCanDie);
				final String transcriptText = MyFormatter.unitsToText(unitsCanDie) + " lost in " + attackedTerritory.getName();
				bridge.getHistoryWriter().addChildToEvent(transcriptText, unitsCanDie);
				bridge.addChange(removeDead);
			}
		}
	}
	
	private ITripleaPlayer getRemote(final IDelegateBridge bridge)
	{
		return (ITripleaPlayer) bridge.getRemote();
	}
}