package org.puc.rio.war.objective;

import java.util.List;

import org.puc.rio.war.model.Continent;
import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;

public class ConquerContinentsObjective extends WarObjective {

	Continent targetContinent1;
	Continent targetContinent2;
	boolean hasToConquerAThirdContinent;

	public ConquerContinentsObjective(Continent c1, Continent c2,
			boolean hasToConquerAThirdContinent) {
		super(String.format("Conquer %s, %s %s", c1.toString(), c2.toString(),
				hasToConquerAThirdContinent ? "and any other continent" : ""));
		this.targetContinent1 = c1;
		this.targetContinent2 = c2;
		this.hasToConquerAThirdContinent = hasToConquerAThirdContinent;
	}

	@Override
	public boolean checkVictory(Map m, Player p) {
		List<Continent> continentsOwned = m.getContinentsOwnedByPlayer(p);
		return continentsOwned.contains(targetContinent1)
				&& continentsOwned.contains(targetContinent2)
				&& (continentsOwned.size() >= 3 || !hasToConquerAThirdContinent);
	}

	public Continent getTargetContinent1() {
		return targetContinent1;
	}

	public Continent getTargetContinent2() {
		return targetContinent2;
	}

	public boolean hasToConquerAThirdContinent() {
		return hasToConquerAThirdContinent;
	}

}
