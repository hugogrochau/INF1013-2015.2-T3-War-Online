package org.puc.rio.war.model;


public enum Continent {

	NORTH_AMERICA(0), SOUTH_AMERICA(1), AFRICA(2), EUROPE(3), ASIA(4), OCEANIA(
			5);
	private final int id;

	Continent(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static Continent getById(int id) {
		for (Continent c: Continent.values()) {
			if (c.id == id) {
				return c;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return super.toString().replace("_", " ");
	}

	public int getTerritoriesToGain() {
		switch (this) {
		case NORTH_AMERICA:
			return 5;
		case SOUTH_AMERICA:
			return 2;
		case AFRICA:
			return 3;
		case EUROPE:
			return 5;
		case ASIA:
			return 7;
		case OCEANIA:
			return 2;
		default:
			return 0;
		}
	}
}
