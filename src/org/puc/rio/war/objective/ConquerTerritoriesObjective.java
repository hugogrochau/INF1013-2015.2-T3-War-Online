package org.puc.rio.war.objective;

import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.Territory;

public class ConquerTerritoriesObjective extends WarObjective {

	int numberOfTerritoriesToConquer;

	int numberOfArmiesInEach;

	public ConquerTerritoriesObjective(int numberOfTerritoriesToConquer,
			int numberOfArmiesInEach) {
		super(
				String.format(
						"Conquer %d territories and occupy them with at least %d armies.",
						numberOfTerritoriesToConquer, numberOfArmiesInEach));
		this.numberOfTerritoriesToConquer = numberOfTerritoriesToConquer;
		this.numberOfArmiesInEach = numberOfArmiesInEach;
	}

	@Override
	public boolean checkVictory(Map m, Player p) {
		int numberOfTerritoriesOwnedWithCondition = 0;
		for (Territory t : m.getTerritories()) {
			if (t.getOwnerName().equals(p.getName())
					&& t.getArmyCount() >= this.numberOfArmiesInEach) {
				numberOfTerritoriesOwnedWithCondition++;
			}
		}
		return numberOfTerritoriesOwnedWithCondition >= this.numberOfTerritoriesToConquer;
	}

	public int getNumberOfArmiesInEach() {
		return this.numberOfArmiesInEach;
	}

	public int getNumberOfTerritoriesToConquer() {
		return numberOfTerritoriesToConquer;
	}

}
