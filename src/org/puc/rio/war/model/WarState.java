package org.puc.rio.war.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class WarState extends Observable {

	private Player currentPlayer;
	private List<Player> players = new ArrayList<Player>();
	private String canStealCardsFrom = null;

	private TurnState currentTurnState;

	private Territory selectedTerritory;
	private Territory targetedTerritory;

	private boolean conqueredThisTurn = false;
	private int cardExchangeArmyCount = 4;

	private Map map = null;

	private Deck deck;

	public enum TurnState {
		PLACING_NEW_ARMIES, ATTACKING, MOVING_ARMIES;
	}

	public WarState(List<Player> players, Map map, Deck deck) {
		this.players = players;
		this.map = map;
		this.deck = deck;
		this.currentPlayer = players.get(0);
		this.currentTurnState = TurnState.PLACING_NEW_ARMIES;
	}

	public WarState(Map map, Deck deck, List<String> names) {
		for (String name: names) {
			this.players.add(new Player(name, Player.playerColors[names.indexOf(name)]));
		}
		this.map = map;
		this.deck = deck;
		this.currentPlayer = players.get(0);
		this.currentTurnState = TurnState.PLACING_NEW_ARMIES;
	}

	public Map getMap() {
		this.setChanged();
		return this.map;
	}

	public List<Player> getPlayers() {
		this.setChanged();
		return this.players;
	}

	public Deck getDeck() {
		this.setChanged();
		return deck;
	}

	public TurnState getCurrentTurnState() {
		return this.currentTurnState;
	}

	public Player getCurrentPlayer() {
		this.setChanged();
		return this.currentPlayer;
	}

	public Territory getSelectedTerritory() {
		this.setChanged();
		return this.selectedTerritory;
	}

	public Territory getTargetedTerritory() {
		this.setChanged();
		return this.targetedTerritory;
	}

	public boolean conqueredThisTurn() {
		return this.conqueredThisTurn;
	}

	public int getCardExchangeArmyCount() {
		return this.cardExchangeArmyCount;
	}

	public String getCanStealCardsFrom() {
		return this.canStealCardsFrom;
	}

	public int getCurrentPlayerIndex() {
		return this.getPlayers().indexOf(this.getCurrentPlayer());
	}

	public void nextTurn() {
		int currentPlayerIndex = players.indexOf(this.currentPlayer);
		if (currentPlayerIndex == players.size() - 1) {
			this.currentPlayer = players.get(0);
		} else {
			this.currentPlayer = players.get(currentPlayerIndex + 1);
		}
		this.clearSelections();
		this.conqueredThisTurn = false;
		this.canStealCardsFrom = null;
		this.currentTurnState = TurnState.PLACING_NEW_ARMIES;
		this.setChanged();
	}

	public void startAttacking() {
		this.clearSelections();
		this.currentTurnState = TurnState.ATTACKING;
		this.setChanged();
	}

	public void startMovingArmies() {
		this.clearSelections();
		this.currentTurnState = TurnState.MOVING_ARMIES;
		this.setChanged();
	}

	public void selectTerritory(Territory t) {
		this.selectedTerritory = t;
		this.setChanged();
	}

	public void unselectTerritory() {
		this.selectedTerritory = null;
		this.setChanged();
	}

	public void targetTerritory(Territory t) {
		this.targetedTerritory = t;
		this.setChanged();
	}

	public void untargetTerritory() {
		this.targetedTerritory = null;
		this.setChanged();
	}

	public void clearSelections() {
		this.selectedTerritory = null;
		this.targetedTerritory = null;
		this.setChanged();
	}

	public void setConqueredThisTurn() {
		this.conqueredThisTurn = true;
		this.setChanged();
	}

	public void setCardExchangeArmyCount(int count) {
		this.cardExchangeArmyCount = count;
		this.setChanged();
	}

	public void setCanStealCardsFrom(String playerName) {
		this.canStealCardsFrom = playerName;
		this.setChanged();
	}

	public void setTurnState(TurnState t) {
		this.currentTurnState = t;
		this.setChanged();
	}

	public void setCurrentPlayerByName(String playerName) {
		for (Player p : this.getPlayers()) {
			if (p.getName().equals(playerName)) {
				this.currentPlayer = p;
			}
		}
		this.setChanged();
	}
}
