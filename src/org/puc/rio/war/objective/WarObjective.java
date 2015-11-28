package org.puc.rio.war.objective;

import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;

public abstract class WarObjective {
	private String description;
	
	public WarObjective(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}

	public abstract boolean checkVictory(Map m, Player p);
}
