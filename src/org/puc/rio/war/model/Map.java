package org.puc.rio.war.model;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.puc.rio.war.Util;

public class Map extends Object {

	private List<Territory> territories = new ArrayList<Territory>();

	public Map() {
	}

	public void addTerritory(Territory t) {
		this.territories.add(t);
	}

	public List<Territory> getTerritories() {
		return this.territories;
	}

	public List<Continent> getContinentsOwnedByPlayer(Player p) {
		List<Continent> continentsOwned = new LinkedList<Continent>();
		HashMap<Continent, Integer> territoriesOwnedInContinentCount = new HashMap<Continent, Integer>();
		HashMap<Continent, Integer> totalTerritoriesInContinentCount = new HashMap<Continent, Integer>();
		for (Continent c : Continent.values()) {
			territoriesOwnedInContinentCount.put(c, 0);
			totalTerritoriesInContinentCount.put(c, 0);
		}
		for (Territory t : this.getTerritories()) {
			totalTerritoriesInContinentCount.put(t.getContinent(),
					totalTerritoriesInContinentCount.get(t.getContinent()) + 1);
			if (t.getOwnerName().equals(p.getName())) {
				territoriesOwnedInContinentCount
						.put(t.getContinent(), territoriesOwnedInContinentCount
								.get(t.getContinent()) + 1);
			}
		}
		// System.out.println("Continent ownership status");
		for (Continent c : Continent.values()) {
			// System.out.println(String.format("%s %d/%d", c.toString(),
			// territoriesOwnedInContinentCount.get(c),
			// totalTerritoriesInContinentCount.get(c)));
			if (territoriesOwnedInContinentCount.get(c) == totalTerritoriesInContinentCount
					.get(c)) {
				continentsOwned.add(c);
			}
		}
		return continentsOwned;
	}

	public void calculateNeighbors() {
		ArrayList<Line2D.Double> tLines = new ArrayList<>();
		ArrayList<Line2D.Double> uLines = new ArrayList<>();

		for (Territory t : territories) {
			tLines = Util.getLineSegments(t.getPolygon());
			for (Territory u : territories) {
				if (!t.equals(u)) {
					uLines = Util.getLineSegments(u.getPolygon());
					for (Line2D.Double tl : tLines) {
						for (Line2D.Double ul : uLines) {
							if (tl.intersectsLine(ul)) {
								t.addNeighbor(u);
								continue;
							}
						}
					}
				}
			}
		}
		/* Add bridged neighbors */
		this.bridgeTerritoriesByNames("Argélia", "Espanha");
		this.bridgeTerritoriesByNames("Argélia", "Itália");
		this.bridgeTerritoriesByNames("Alasca", "Sibéria");
		this.bridgeTerritoriesByNames("Brasil", "Nigéria");
		this.bridgeTerritoriesByNames("Austrália", "Indonésia");
		this.bridgeTerritoriesByNames("Austrália", "Nova Zelândia");
		this.bridgeTerritoriesByNames("Bangladesh", "Indonésia");
		this.bridgeTerritoriesByNames("Egito", "Romênia");
		this.bridgeTerritoriesByNames("Egito", "Jordânia");
		this.bridgeTerritoriesByNames("França", "Reino Unido");
		this.bridgeTerritoriesByNames("Groelandia", "Reino Unido");
		this.bridgeTerritoriesByNames("Índia", "Indonésia");
		this.bridgeTerritoriesByNames("Japão", "Cazaquistão");
		this.bridgeTerritoriesByNames("Japão", "Coréia do Norte");
		this.bridgeTerritoriesByNames("Japão", "Mongólia");
		this.bridgeTerritoriesByNames("Somália", "Arábia Saudita");
		this.bridgeTerritoriesByNames("Suécia", "França");
		this.bridgeTerritoriesByNames("Suécia", "Itália");
		this.bridgeTerritoriesByNames("Groelandia", "Quebec");
		this.bridgeTerritoriesByNames("Nova Zelândia", "Indonésia");

	}

	private void bridgeTerritoriesByNames(String nameX, String nameY) {
		Territory x, y;
		x = this.getTerritoryByName(nameX);
		y = this.getTerritoryByName(nameY);
		if (x == null || y == null) {
			System.out.println("not found" + nameX + " " + nameY);
			return;
		}
		// System.out.println("found " + nameX + " " + nameY);
		x.addNeighbor(y);
		y.addNeighbor(x);
	}

	public Territory getTerritoryByName(String name) {
		for (Territory t : this.territories) {
			if (t.getName().equals(name)) {
				// System.out.println(name + "Found");
				return t;
			}
		}
		return null;

	}

	public List<Territory> getTerritoriesByContinent(Continent c) {
		List<Territory> tl = new ArrayList<Territory>();
		for (Territory t : this.territories) {
			if (t.getContinent() == c)
				tl.add(t);
		}
		return tl;
	}

	/* returns maximum amount of armies to move straight after conquest */
	public int conquerTerritory(Territory from, Territory to) {
		to.setOwnerName(from.getOwnerName());
		/* always move at least one */
		this.moveArmies(from, to, 1, true);

		if (from.getAtackableArmyCount() + 1 > 3) {
			return 3;
		} else {
			return from.getAtackableArmyCount() + 1;
		}
	}

	public boolean moveArmies(Territory from, Territory to, int amount,
			boolean movable) {
		if (!from.getOwnerName().equals(to.getOwnerName())) {
			return false;
		}
		if (from.getArmyCount() - 1 < amount) {
			return false;
		}
		from.removeArmies(amount);
		if (movable) {
			to.addArmies(amount);
		} else {
			to.addUnmovableArmies(amount);
		}
		return true;
	}

	public void resetMovableArmiesCount() {
		for (Territory t : this.getTerritories()) {
			t.resetUnmovableArmiesCount();
		}
	}

	public List<Territory> getTerritoriesByOwner(Player owner) {
		List<Territory> territoriesOwned = new LinkedList<Territory>();
		for (Territory t : this.getTerritories()) {
			if (t.getOwnerName().equals(owner.getName())) {
				territoriesOwned.add(t);
			}
		}
		return territoriesOwned;
	}

}
