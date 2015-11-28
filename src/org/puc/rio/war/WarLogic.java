package org.puc.rio.war;

import org.puc.rio.war.model.Continent;
import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;

public class WarLogic {

	public final static int MAX_PLAYERS = 6;
	public final static int MIN_PLAYERS = 3;
	public final static int MAX_DICE = 3;
	public final static int ARMIES_TO_GAIN_FROM_TERRITORY_CARD = 2;
	public final static int MINIMUM_ARMIES_TO_GAIN_PER_TURN = 3;
	public final static int CARD_EXCHANGE_ARMY_INCREMENT= 2;

	public static int calculateArmiesToGain(Map m, Player p) {
		int countFromTotalTerritories = m.getTerritoriesByOwner(p).size() / 2;
		int countFromContinentsOwned = 0;

		for (Continent c : m.getContinentsOwnedByPlayer(p)) {
			System.out.println(String.format("Player %s owns %s and gains %d territories from it", p.getName(), c.toString(), c.getTerritoriesToGain()));
			countFromContinentsOwned += c.getTerritoriesToGain();
		}
		
		/* Always get at least the minimum amount */
		return Math.max(countFromTotalTerritories + countFromContinentsOwned, MINIMUM_ARMIES_TO_GAIN_PER_TURN);
	}
}
