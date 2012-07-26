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
package games.strategy.triplea.delegate;

import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.data.UnitType;
import games.strategy.triplea.attatchments.UnitAttachment;
import games.strategy.util.CompositeMatch;
import games.strategy.util.CompositeMatchAnd;
import games.strategy.util.IntegerMap;
import games.strategy.util.Match;

import java.util.ArrayList;
import java.util.Collection;

public class BidPlaceDelegate extends AbstractPlaceDelegate
{
	public BidPlaceDelegate()
	{
	}
	
	// Allow production of any number of units
	@Override
	protected String checkProduction(final Territory to, final Collection<Unit> units, final PlayerID player)
	{
		return null;
	}
	
	// Return whether we can place bid in a certain territory
	@Override
	protected String canProduce(final Territory to, final Collection<Unit> units, final PlayerID player)
	{
		// we can place if no enemy units and its water
		if (to.isWater())
		{
			if (Match.someMatch(units, Matches.UnitIsLand))
				return "Cant place land units at sea";
			else if (to.getUnits().someMatch(Matches.enemyUnit(player, getData())))
				return null;
			else
				return "Cant place in sea zone containing enemy units";
		}
		// we can place on territories we own
		else
		{
			if (Match.someMatch(units, Matches.UnitIsSea))
				return "Cant place sea units on land";
			else if (to.getOwner().equals(player))
				return null;
			else
				return "You dont own " + to.getName();
		}
	}
	
	@Override
	protected String canProduce(final Territory producer, final Territory to, final Collection<Unit> units, final PlayerID player)
	{
		// we can place if no enemy units and its water
		if (to.isWater())
		{
			if (Match.someMatch(units, Matches.UnitIsLand))
				return "Cant place land units at sea";
			else if (to.getUnits().someMatch(Matches.enemyUnit(player, getData())))
				return null;
			else
				return "Cant place in sea zone containing enemy units";
		}
		// we can place on territories we own
		else
		{
			if (Match.someMatch(units, Matches.UnitIsSea))
				return "Cant place sea units on land";
			else if (to.getOwner().equals(player))
				return null;
			else
				return "You dont own " + to.getName();
		}
	}
	
	@Override
	protected int getMaxUnitsToBePlaced(final Collection<Unit> units, final Territory to, final PlayerID player, final boolean countSwitchedProductionToNeighbors)
	{
		return units.size();
	}
	
	@Override
	protected int getMaxUnitsToBePlacedFrom(final Territory producer, final Collection<Unit> units, final Territory to, final PlayerID player, final boolean countSwitchedProductionToNeighbors,
				final Collection<Territory> notUsableAsOtherProducers)
	{
		return units.size();
	}
	
	// Allow player to place as many units as they want in bid phase
	protected int getMaxUnitsToBePlaced(final Territory to, final PlayerID player)
	{
		return -1;
	}
	
	// Return collection of bid units which can placed in a land territory
	@Override
	protected Collection<Unit> getUnitsToBePlacedLand(final Territory to, final Collection<Unit> units, final PlayerID player)
	{
		final Collection<Unit> unitsAtStartOfTurnInTO = unitsAtStartOfStepInTerritory(to);
		final Collection<Unit> placeableUnits = new ArrayList<Unit>();
		final CompositeMatch<Unit> groundUnits = new CompositeMatchAnd<Unit>(Matches.UnitIsLand, Matches.UnitIsNotFactoryOrConstruction); // we add factories and constructions later
		final CompositeMatch<Unit> airUnits = new CompositeMatchAnd<Unit>(Matches.UnitIsAir, Matches.UnitIsNotFactoryOrConstruction);
		placeableUnits.addAll(Match.getMatches(units, groundUnits));
		placeableUnits.addAll(Match.getMatches(units, airUnits));
		if (Match.someMatch(units, Matches.UnitIsFactoryOrConstruction))
		{
			final IntegerMap<String> constructionsMap = howManyOfEachConstructionCanPlace(to, units, player);
			final Collection<Unit> skipUnit = new ArrayList<Unit>();
			for (final Unit currentUnit : Match.getMatches(units, Matches.UnitIsFactoryOrConstruction))
			{
				final int maxUnits = howManyOfConstructionUnit(currentUnit, constructionsMap);
				if (maxUnits > 0)
				{
					// we are doing this because we could have multiple unitTypes with the same constructionType, so we have to be able to place the max placement by constructionType of each unitType
					if (skipUnit.contains(currentUnit))
						continue;
					placeableUnits.addAll(Match.getNMatches(units, maxUnits, Matches.unitIsOfType(currentUnit.getType())));
					skipUnit.addAll(Match.getMatches(units, Matches.unitIsOfType(currentUnit.getType())));
				}
			}
		}
		// remove any units that require other units to be consumed on creation (veqryn)
		if (Match.someMatch(placeableUnits, Matches.UnitConsumesUnitsOnCreation))
		{
			final Collection<Unit> unitsWhichConsume = Match.getMatches(placeableUnits, Matches.UnitConsumesUnitsOnCreation);
			for (final Unit unit : unitsWhichConsume)
			{
				if (Matches.UnitWhichConsumesUnitsHasRequiredUnits(unitsAtStartOfTurnInTO, to).invert().match(unit))
					placeableUnits.remove(unit);
			}
		}
		// now check stacking limits
		final Collection<Unit> placeableUnits2 = new ArrayList<Unit>();
		final Collection<UnitType> typesAlreadyChecked = new ArrayList<UnitType>();
		for (final Unit currentUnit : placeableUnits)
		{
			final UnitType ut = currentUnit.getType();
			if (typesAlreadyChecked.contains(ut))
				continue;
			typesAlreadyChecked.add(ut);
			placeableUnits2.addAll(Match.getNMatches(placeableUnits,
						UnitAttachment.getMaximumNumberOfThisUnitTypeToReachStackingLimit("placementLimit", ut, to, player, getData()), Matches.unitIsOfType(ut)));
		}
		return placeableUnits2;
	}
}