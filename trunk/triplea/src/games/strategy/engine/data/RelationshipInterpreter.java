package games.strategy.engine.data;

import games.strategy.triplea.delegate.Matches;

@SuppressWarnings("serial")
public class RelationshipInterpreter extends GameDataComponent
{
	public RelationshipInterpreter(final GameData data)
	{
		super(data);
	}
	
	/**
	 * @param p1
	 *            first referring player
	 * @param p2
	 *            second referring player
	 * @return whether player p1 is allied to player p2
	 */
	public boolean isAllied(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeIsAllied.match((getRelationshipType(p1, p2)));
	}
	
	/**
	 * returns true if p1 is at war with p2
	 * 
	 * @param p1
	 *            player1
	 * @param p2
	 *            player2
	 * @return whether p1 is at war with p2
	 */
	public boolean isAtWar(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeIsAtWar.match((getRelationshipType(p1, p2)));
	}
	
	/**
	 * 
	 * @param p1
	 *            player1
	 * @param p2
	 *            player2
	 * @return whether player1 is neutral to player2
	 */
	public boolean isNeutral(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeIsNeutral.match((getRelationshipType(p1, p2)));
	}
	
	/**
	 * <strong>example</strong> method on how to extract a boolean from isAlliance();
	 * use this method instead of isAlliance in the spots to be used
	 * 
	 * @param p1
	 *            first referring player
	 * @param p2
	 *            second referring player
	 * @return whether player p1 helps defend at sea player p2
	 */
	public boolean helpsDefendAtSea(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeHelpsDefendAtSea.match((getRelationshipType(p1, p2)));
	}
	
	public boolean canMoveLandUnitsOverOwnedLand(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeCanMoveLandUnitsOverOwnedLand.match(getRelationshipType(p1, p2));
	}
	
	public boolean canMoveAirUnitsOverOwnedLand(final PlayerID p1, final PlayerID p2)
	{
		return Matches.RelationshipTypeCanMoveAirUnitsOverOwnedLand.match(getRelationshipType(p1, p2));
	}
	
	/**
	 * Convenience method to get RelationshipType so you can do relationshipChecks on the relationship between these 2 players
	 * 
	 * @return RelationshipType between these to players
	 */
	RelationshipType getRelationshipType(final PlayerID p1, final PlayerID p2)
	{
		return getData().getRelationshipTracker().getRelationshipType(p1, p2);
	}
}