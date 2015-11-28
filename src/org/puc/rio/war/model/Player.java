package org.puc.rio.war.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.puc.rio.war.WarGame;
import org.puc.rio.war.objective.WarObjective;

public class Player {

	private String name;
	private Color color;
	private List<Card> cards = new ArrayList<Card>();
	private WarObjective objective;
	private static final Color BLUE = new Color(0, 0, 128, 255);
	private static final Color GREEN = new Color(0, 128, 0, 255);
	private static final Color RED = new Color(128, 0, 0, 255);
	private static final Color LIGHT_BLUE = new Color(0, 128, 128, 255);
	private static final Color PURPLE = new Color(128, 0, 128, 255);
	private static final Color BROWN = new Color(128, 128, 0, 255);
	public final static Color[] playerColors = { GREEN, RED, BLUE, LIGHT_BLUE,
			PURPLE, BROWN };
	private int unplacedArmies = 0;

	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	public Color getColor() {
		return this.color;
	}

	public static Color getForegroundColor(Color c) {
		if (c.equals(BLUE) || c.equals(PURPLE) || c.equals(RED)) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	public int getUnplacedArmies() {
		return this.unplacedArmies;
	}

	public void setUnplacedArmies(int number) {
		this.unplacedArmies = number;
	}

	public void giveArmies(int number) {
		this.unplacedArmies += number;
	}

	public void removeArmies(int number) {
		this.unplacedArmies -= number;
	}

	public void addCard(Card c) {
		this.cards.add(c);
	}

	public void removeCard(Card c) {
		this.cards.remove(c);
	}

	public List<Card> getCards() {
		return this.cards;
	}

	public boolean mustExchangeCards() {
		return this.cards.size() >= 5;
	}

	public boolean canExchangeCards() {
		return hasValidCardExchange(this.getCards());
	}

	public boolean hasValidCardExchange(List<Card> cards) {
		if (this.getCards().size() < 3) {
			return false;
		} else {
			HashMap<CardType, Integer> cardTypeCount = new HashMap<CardType, Integer>();
			boolean hasOneOfEach = true;
			boolean hasThreeOfTheSame = false;
			for (CardType ct : CardType.values()) {
				cardTypeCount.put(ct, 0);
			}
			for (Card c : cards) {
				cardTypeCount.put(c.getType(),
						cardTypeCount.get(c.getType()) + 1);
			}
			int numberOfJokers = cardTypeCount.get(CardType.JOKER);
			int spentJokers = 0;
			for (CardType ct : CardType.values()) {
				if (ct.equals(CardType.JOKER)) {
					continue;
				}
				/* If card type not found */
				if (cardTypeCount.get(ct) == 0) {
					/* If we don't have any jokers to spend */
					if (numberOfJokers - spentJokers <= 0) {
						hasOneOfEach = false;
					/* Spend joker */
					} else {
						spentJokers++;
					}
				}
				if (cardTypeCount.get(ct) == 3 - numberOfJokers) {
					hasThreeOfTheSame = true;
				}
			}
			return hasOneOfEach || hasThreeOfTheSame;
		}
	}

	public boolean isVictorious() {
		return this.objective
				.checkVictory(WarGame.getInstance().getMap(), this);
	}

	public WarObjective getObjective() {
		return this.objective;
	}

	public void setObjective(WarObjective obj) {
		this.objective = obj;
	}

}
