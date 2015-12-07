package org.puc.rio.war;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.puc.rio.war.client.Client;
import org.puc.rio.war.model.Card;
import org.puc.rio.war.model.Continent;
import org.puc.rio.war.model.Deck;
import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.Territory;
import org.puc.rio.war.model.TerritoryCard;
import org.puc.rio.war.model.WarState;
import org.puc.rio.war.model.WarState.TurnState;
import org.puc.rio.war.objective.ConquerContinentsObjective;
import org.puc.rio.war.objective.ConquerTerritoriesObjective;
import org.puc.rio.war.objective.DestroyPlayerObjective;
import org.puc.rio.war.objective.WarObjective;
import org.puc.rio.war.serialize.WarDeserializer;
import org.puc.rio.war.serialize.WarSerializer;
import org.puc.rio.war.server.MessageFactory;
import org.puc.rio.war.viewcontroller.WarFrame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WarGame {

	private static WarGame instance;

	private WarFrame warFrame;
	private WarState warState = null;
	private Client client;
	private String myName;
	private List<String> playerNames = new ArrayList<String>(3);
	public final String SERVER_IP = "127.0.0.1";

	private WarGame() {
		this.warFrame = new WarFrame();
	}

	public static WarGame getInstance() {
		if (WarGame.instance == null) {
			WarGame.instance = new WarGame();
		}
		return WarGame.instance;
	}

	public Client getClient() {
		if (this.client == null) {
			try {
				this.client = new Client(SERVER_IP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.client;
	}
	public void loadGame(String jsonData) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(WarState.class, new WarDeserializer());
		Gson gson = gsonBuilder.create();
		this.warState = gson.fromJson(jsonData, WarState.class);
		for (Player p : this.getState().getPlayers()) {
			for (Card c : p.getCards()) {
				this.getState().getDeck().removeCard(c);
			}
		}
		this.getState().getDeck().addJoker(2);
		this.getState().getDeck().shuffle();
		this.getMap().calculateNeighbors();
		this.getWarFrame().startGame();
		this.getState().addObserver(this.getWarFrame().getMapPanel());
		this.getState().addObserver(this.getWarFrame().getUIPanel());
		if (this.getCurrentPlayer().getCards().size() >= 5) {
			this.showCards(true);
		}
	}

	public void startGame(List<String> names) {
		/* Randomize player order */
		this.warState = new WarState(new Map(), new Deck(), names);
		Util.loadTerritories(this.getState().getMap(), this.getState().getDeck());
		this.getState().getDeck().addJoker(2);
		this.getState().getDeck().shuffle();
		this.giveAwayTerritories();
		this.getMap().calculateNeighbors();
		this.giveObjectiveToPlayers();
		this.getCurrentPlayer().giveArmies(WarLogic.calculateArmiesToGain(this.getMap(), this.getCurrentPlayer()));
		this.getWarFrame().startGame();
		this.getState().addObserver(this.getWarFrame().getMapPanel());
		this.getState().addObserver(this.getWarFrame().getUIPanel());
	}

	/* Wargame is a facade, no-one should be able to access the state */
	private WarState getState() {
		return this.warState;
	}

	public List<Player> getPlayers() {
		return this.getState().getPlayers();
	}

	public Player getPlayerByName(String name) {
		for (Player p : this.getPlayers()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public Map getMap() {
		return this.getState().getMap();
	}

	public Deck getDeck() {
		return this.getState().getDeck();
	}

	public Player getCurrentPlayer() {
		return this.getState().getCurrentPlayer();
	}

	public int getCurrentPlayerIndex() {
		return this.getState().getCurrentPlayerIndex();
	}

	public TurnState getTurnState() {
		return this.getState().getCurrentTurnState();
	}

	public boolean isAttacking() {
		return this.getTurnState().equals(TurnState.ATTACKING);
	}

	public boolean isMoving() {
		return this.getTurnState().equals(TurnState.MOVING_ARMIES);
	}

	public boolean isPlacing() {
		return this.getTurnState().equals(TurnState.PLACING_NEW_ARMIES);
	}

	public WarFrame getWarFrame() {
		return this.warFrame;
	}

	public int getCardExchangeArmyCount() {
		return this.getState().getCardExchangeArmyCount();
	}

	public void focusPopupIfExists() {
		if (warFrame.hasPopupActive()) {
			warFrame.focusPopup();
		}
	}

	private void incrementCardExchangeArmyCount() {
		this.getState()
				.setCardExchangeArmyCount(this.getCardExchangeArmyCount() + WarLogic.CARD_EXCHANGE_ARMY_INCREMENT);
	}

	private void giveAwayTerritories() {
		List<Integer> indexes = new ArrayList<Integer>();
		Iterator<Player> pi = this.getPlayers().iterator();
		for (int i = 0; i < this.getMap().getTerritories().size(); i++) {
			indexes.add(i);
		}
		Collections.shuffle(indexes);
		for (Integer i : indexes) {
			if (!pi.hasNext()) {
				pi = this.getPlayers().iterator(); // loop back
			}
			Player p = pi.next();
			Territory t = this.getMap().getTerritories().get(i);
			t.setOwnerName(p.getName());
		}
	}

	private void giveObjectiveToPlayers() {
		List<WarObjective> objectives = new ArrayList<WarObjective>();
		objectives.add(new ConquerTerritoriesObjective(18, 2));
		objectives.add(new ConquerTerritoriesObjective(24, 1));
		objectives.add(new ConquerContinentsObjective(Continent.EUROPE, Continent.OCEANIA, false));
		objectives.add(new ConquerContinentsObjective(Continent.ASIA, Continent.SOUTH_AMERICA, false));
		objectives.add(new ConquerContinentsObjective(Continent.EUROPE, Continent.SOUTH_AMERICA, true));
		objectives.add(new ConquerContinentsObjective(Continent.ASIA, Continent.AFRICA, false));
		objectives.add(new ConquerContinentsObjective(Continent.NORTH_AMERICA, Continent.AFRICA, false));
		objectives.add(new ConquerContinentsObjective(Continent.NORTH_AMERICA, Continent.OCEANIA, false));

		for (Player p : this.getPlayers()) {
			objectives.add(new DestroyPlayerObjective(p.getName()));
		}

		Random r = new Random();
		for (Player p : this.getPlayers()) {
			int index = 0;
			WarObjective objective;
			do {
				index = r.nextInt(objectives.size());
				objective = objectives.get(index);
				/* Disallow player having to destroy himself */
			} while (objective instanceof DestroyPlayerObjective
					&& ((DestroyPlayerObjective) objective).getTargetPlayerName().equals(p.getName()));
			p.setObjective(objective);
			/* Disallow duplicate objectives */
			objectives.remove(index);
		}
	}

	/* Event handlers */
	public void nextTurn() {
		/* Do nothing when a pop up is active */
		if (this.getWarFrame().hasPopupActive()) {
			this.getWarFrame().focusPopup();
			return;
		}
		Player winner = this.checkWinner();
		if (winner != null) {
			this.endGameSequence(winner);
			return;
		}
		if (this.getState().conqueredThisTurn()) {
			this.giveCardToPlayer(this.getCurrentPlayer());
		}
		this.getMap().resetMovableArmiesCount();
		this.getState().nextTurn();
		this.getState().getCurrentPlayer()
				.giveArmies(WarLogic.calculateArmiesToGain(this.getMap(), getState().getCurrentPlayer()));
		if (this.getCurrentPlayer().getCards().size() >= 5) {
			this.showCards(true);
		}
		WarSerializer ws = new WarSerializer();
		this.getClient().sendMessage(MessageFactory.stateMessage(ws.serialize(this.getState(), null, null).toString()));

		/* if player is out of the game */
		if (this.getMap().getTerritoriesByOwner(this.getCurrentPlayer()).size() == 0) {
			this.nextTurn();
		}
		this.getState().notifyObservers();
	}

	public void actionPerformed() {
		/* Do nothing when a pop up is active */
		if (this.getWarFrame().hasPopupActive()) {
			this.getWarFrame().focusPopup();
			return;
		}
		switch (this.getTurnState()) {
		case ATTACKING:
			this.getState().startMovingArmies();
			break;
		case MOVING_ARMIES:
			this.getState().clearSelections();
			break;
		case PLACING_NEW_ARMIES:
			if (this.getSelectedTerritory() != null) {
				this.getWarFrame().spawnChooseNumberFrame(this.getCurrentPlayer().getUnplacedArmies(),
						String.format("How many do you want to place in %s?", this.getSelectedTerritory().getName()));
			}
			break;
		default:
			break;
		}
		this.getState().notifyObservers();
	}

	public void selectTerritory(Territory t) {
		/* Do nothing when a pop up is active */
		if (this.getWarFrame().hasPopupActive()) {
			getWarFrame().focusPopup();
			return;
		}
		switch (this.getTurnState()) {
		case ATTACKING:
			if (this.getSelectedTerritory() == null) {
				/* Select territory to attack from */
				if (t.getOwnerName().equals(this.getCurrentPlayer().getName())) {
					this.warState.selectTerritory(t);
				}
			} else {
				/* Select another territory */
				if (t.getOwnerName().equals(this.getCurrentPlayer().getName())) {
					this.warState.selectTerritory(t);
					/* Select territory to attack */
				} else if (this.getSelectedTerritory().canAttack(t)) {
					this.getState().targetTerritory(t);
					this.getWarFrame().spawnChooseNumberFrame(this.getSelectedTerritory().getAtackableArmyCount(),
							String.format("How many to attack from %s to %s with?",
									this.getSelectedTerritory().getName(), this.getTargetedTerritory().getName()));
				}
			}
			break;
		case MOVING_ARMIES:
			if (t.getOwnerName().equals(this.getCurrentPlayer().getName())) {
				/* select territory */
				if (this.getSelectedTerritory() == null) {
					this.warState.selectTerritory(t);
					/* select another owned territory */
				} else if (!this.getSelectedTerritory().canMoveTo(t)) {
					this.warState.selectTerritory(t);
					/* target owned territory */
				} else {
					this.warState.targetTerritory(t);
					this.getWarFrame().spawnChooseNumberFrame(this.getSelectedTerritory().getMoveableArmyCount(),
							String.format("How many to move from %s to %s?", this.getSelectedTerritory().getName(),
									this.getTargetedTerritory().getName()));
				}
			}
			break;
		case PLACING_NEW_ARMIES:
			if (t.getOwnerName().equals(this.getCurrentPlayer().getName())) {
				this.getState().selectTerritory(t);
			}
			break;
		default:
			break;
		}
		this.getState().notifyObservers();
	}

	public void selectNumber(int number) {
		switch (this.getTurnState()) {
		case ATTACKING:
			/* already conquered and moving armies */
			if (this.getSelectedTerritory().getOwnerName().equals(this.getTargetedTerritory().getOwnerName())) {
				this.getMap().moveArmies(this.getSelectedTerritory(), this.getTargetedTerritory(), number - 1, true);
				Player winner = this.checkWinner();
				if (winner != null) {
					this.endGameSequence(winner);
					return;
				}
				if (this.getState().getCanStealCardsFrom() != null) {
					if (this.getCurrentPlayer().getCards().size() < 5) {
						Player destroyedPlayer = this.getPlayerByName(this.getState().getCanStealCardsFrom());
						this.getWarFrame().spawnCardSelectionFrame(destroyedPlayer,
								5 - this.getCurrentPlayer().getCards().size(), false);
					}
				}
			} else {
				this.getWarFrame().spawnAttackFrame(this.getSelectedTerritory(), this.getTargetedTerritory(), number);
			}
			break;
		case MOVING_ARMIES:
			this.getMap().moveArmies(this.getSelectedTerritory(), this.getTargetedTerritory(), number, false);
			this.getState().clearSelections();
			break;
		case PLACING_NEW_ARMIES:
			this.getCurrentPlayer().removeArmies(number);
			this.warState.getSelectedTerritory().addArmies(number);
			if (this.getCurrentPlayer().getUnplacedArmies() <= 0) {
				this.warState.startAttacking();
			}
			break;
		default:
			break;
		}
		this.getState().notifyObservers();
	}

	public void attackResult(int[] losses) {
		if (this.isAttacking()) {
			this.getSelectedTerritory().removeArmies(losses[0]);
			this.getTargetedTerritory().removeArmies(losses[1]);

			/* attacker conquered */
			if (this.getTargetedTerritory().getArmyCount() == 0) {
				/* Is last territory */
				Player owner = this.getPlayerByName(this.getTargetedTerritory().getOwnerName());
				if (this.getMap().getTerritoriesByOwner(owner).size() == 1) {
					this.getState().setCanStealCardsFrom(owner.getName());
				}
				int maxToMove = this.getMap().conquerTerritory(this.getSelectedTerritory(),
						this.getTargetedTerritory());
				this.getState().setConqueredThisTurn();
				Player winner = this.checkWinner();
				if (winner != null) {
					this.endGameSequence(winner);
					return;
				}
				this.warFrame.spawnChooseNumberFrame(maxToMove, String.format("How many armies to move from %s to %s?",
						this.getSelectedTerritory().getName(), this.getTargetedTerritory().getName()));
			}
		}
		this.getState().notifyObservers();
	}

	public void showObjective() {
		/* Do nothing when a pop up is active */
		if (this.getWarFrame().hasPopupActive()) {
			getWarFrame().focusPopup();
			return;
		}
		this.getWarFrame().spawnTextFrame(this.getCurrentPlayer().getObjective().getDescription());
	}

	public void exchangeCards(List<Card> selectedCards) {
		/* receiving cards */
		if (this.isPlacing()) {
			for (Card c : selectedCards) {
				this.getCurrentPlayer().removeCard(c);
				/* Give extra armies to the territory of the card */
				if (c instanceof TerritoryCard) {
					TerritoryCard tc = (TerritoryCard) c;
					if (tc.getTerritory().getOwnerName().equals(this.getCurrentPlayer().getName())) {
						tc.getTerritory().addArmies(WarLogic.ARMIES_TO_GAIN_FROM_TERRITORY_CARD);
					}
				}
			}
			this.getCurrentPlayer().giveArmies(this.getState().getCardExchangeArmyCount());
			this.incrementCardExchangeArmyCount();
			/* Taking cards from player */
		} else {
			for (Card c : selectedCards) {
				Player loser = this.getPlayerByName(this.getState().getCanStealCardsFrom());
				loser.removeCard(c);
				this.getCurrentPlayer().addCard(c);
			}
		}
		this.getState().notifyObservers();
	}

	/* End event handlers */

	public Territory getTargetedTerritory() {
		return this.warState.getTargetedTerritory();
	}

	public Territory getSelectedTerritory() {
		return this.warState.getSelectedTerritory();
	}

	public void giveCardToPlayer(Player p) {
		Card c = this.getDeck().takeCard();
		p.addCard(c);
	}

	public void receiveCardFromPlayer(Player p, TerritoryCard c) {
		p.removeCard(c);
		this.getDeck().returnCard(c);
	}

	public Player checkWinner() {
		for (Player p : this.getPlayers()) {
			if (p.isVictorious()) {
				return p;
			}
		}
		return null;
	}

	private void endGameSequence(Player p) {
		System.out.println("GAME FINISHED");
		System.out.println("Winner is " + p.getName() + " " + p.getObjective().getDescription());
		this.getWarFrame().getUIPanel().showGameEndedPanel(p);
		this.getState().notifyObservers();
	}

	public void showCards(boolean forcedToExchange) {
		this.getWarFrame().spawnCardSelectionFrame(this.getCurrentPlayer(), 3, forcedToExchange);
	}

	public void addPlayer(String name, boolean self) {
		this.playerNames.add(name);
		if (self) {
			this.getClient().sendMessage(MessageFactory.nameMessage(name));
			this.myName = name;
		} 
		// first player starts game
		System.out.println("Added player: " + name);
		if (this.playerNames.size() >= WarLogic.MIN_PLAYERS) {
			System.out.println("Have " + WarLogic.MIN_PLAYERS + " players, starting game");
			WarGame.getInstance().startGame(this.playerNames);
			WarSerializer ws = new WarSerializer();
			this.getClient().sendMessage(MessageFactory.stateMessage(ws.serialize(this.getState(), null, null).toString()));
		}
	}
	
	public boolean isMyTurn() {
		return this.myName.equals(this.getState().getCurrentPlayer().getName());
	}

	public boolean hasStarted() {
		return this.warState != null;
	}

}
