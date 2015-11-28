package org.puc.rio.war.model;

public enum CardType {
	TRIANGLE(3), SQUARE(4), CIRCLE(0), JOKER(1);
	
	private final int id;

	CardType(int value) {
		this.id = value;
	}

	public int getId() {
		return this.id;
	}

	public static CardType getById(int id) {
		switch (id) {
		case 3:
			return CardType.TRIANGLE;
		case 4:
			return CardType.SQUARE;
		case 0:
			return CardType.CIRCLE;
		case 1:
			return CardType.JOKER;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		switch (this.id) {
		case 3:
			return "\u25B2";
		case 4:
			return "\u25FC";
		case 0:
			return "\u25CF";
		}
		return null;
	}
}