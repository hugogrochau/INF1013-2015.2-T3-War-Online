package org.puc.rio.war.model;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.puc.rio.war.WarLogic;

public class Territory extends Object {

	private String name;
	private GeneralPath polygon;
	private Point2D.Double center = new Point2D.Double(0., 0.);
	private String ownerName = null;
	private int armyCount = 1;
	private Set<Territory> neighbors = new HashSet<Territory>();
	private Continent continent;
	private int unmovableArmiesCount = 0;

	public Territory(String name, List<Point2D.Double> points, Continent c) {
		this.name = name;
		this.continent = c;
		this.createPolygon(points);
	}

	public void setOwnerName(String name) {
		this.ownerName = name;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public Continent getContinent() {
		return this.continent;
	}

	public GeneralPath getPolygon() {
		return this.polygon;
	}

	public Point2D.Double getCenter() {
		return this.center;
	}

	public int getArmyCount() {
		return this.armyCount;
	}

	public int addArmies(int armies) {
		this.armyCount += armies;
		return this.armyCount;
	}

	public int removeArmies(int armies) {
		this.armyCount -= armies;
		return this.armyCount;
	}

	public void setArmies(int armies) {
		this.armyCount = armies;
	}

	public String getName() {
		return this.name;
	}

	public void addNeighbor(Territory t) {
		this.neighbors.add(t);
	}

	public Set<Territory> getNeighbors() {
		return this.neighbors;
	}

	public boolean isNeighbor(Territory t) {
		return this.neighbors.contains(t);
	}

	public boolean canAttack(Territory t) {
		if (this.getArmyCount() <= 1) {
			return false;
		}
		if (t.getOwnerName().equals(this.getOwnerName())) {
			return false;
		}
		if (!this.isNeighbor(t)) {
			return false;
		}
		return true;
	}

	public boolean canMoveTo(Territory t) {
		if (this.getMoveableArmyCount() < 1) {
			return false;
		}
		if (!t.getOwnerName().equals(this.getOwnerName())) {
			return false;
		}
		if (!this.isNeighbor(t)) {
			return false;
		}
		/* has been moved to in the last round */
		return true;
	}

	@Override
	public boolean equals(Object another) {
		return this.name == ((Territory) another).getName();
	}

	private void createPolygon(List<Point2D.Double> points) {
		GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		boolean first = true;
		for (Point2D.Double point : points) {
			if (first) {
				gp.moveTo(point.x, point.y);
				first = false;
			} else {
				gp.lineTo(point.x, point.y);
			}
			this.center.x += point.x;
			this.center.y += point.y;
		}
		this.center.x /= points.size();
		this.center.y /= points.size();
		gp.closePath();
		this.polygon = gp;
	}

	public int getMoveableArmyCount() {
		return Math.max(0, this.getArmyCount() - 1 - this.unmovableArmiesCount);
	}

	public int getAtackableArmyCount() {
		if (this.getArmyCount() <= 1) {
			return 0;
		} else if (this.getArmyCount() > WarLogic.MAX_DICE) {
			return WarLogic.MAX_DICE;
		} else {
			return this.getArmyCount() - 1;
		}
	}

	public void addUnmovableArmies(int amount) {
		this.unmovableArmiesCount += amount;
		this.addArmies(amount);
	}

	public void resetUnmovableArmiesCount() {
		this.unmovableArmiesCount = 0;
	}

	public void setUnmovableArmies(int unmovableArmiesCount) {
		this.unmovableArmiesCount = unmovableArmiesCount;
	}

	public int getUnmovableArmyCount() {
		return this.unmovableArmiesCount;
	}
}