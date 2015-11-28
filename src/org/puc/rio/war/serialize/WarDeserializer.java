package org.puc.rio.war.serialize;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.puc.rio.war.Util;
import org.puc.rio.war.model.Card;
import org.puc.rio.war.model.Continent;
import org.puc.rio.war.model.Deck;
import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.Territory;
import org.puc.rio.war.model.TerritoryCard;
import org.puc.rio.war.model.WarState;
import org.puc.rio.war.objective.ConquerContinentsObjective;
import org.puc.rio.war.objective.ConquerTerritoriesObjective;
import org.puc.rio.war.objective.DestroyPlayerObjective;
import org.puc.rio.war.objective.WarObjective;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class WarDeserializer implements JsonDeserializer<WarState> {

	@Override
	public WarState deserialize(JsonElement je, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jo = je.getAsJsonObject();
		Map map = new Map();
		Deck deck = new Deck();
		Util.loadTerritories(map, deck);
		this.deserializeMap(jo.get("map").getAsJsonObject(), map);
		List<Player> players = this.deserializePlayers(jo.get("players")
				.getAsJsonArray(), map);
		WarState state = new WarState(players, map, deck);
		if (jo.get("conqueredThisTurn").getAsBoolean()) {
			state.setConqueredThisTurn();
		}
		state.setCardExchangeArmyCount(jo.get("cardExchangeArmyCount")
				.getAsInt());
		state.setTurnState(WarState.TurnState.valueOf(jo
				.get("currentTurnState").getAsString()));
		state.setCurrentPlayerByName(jo.get("currentPlayer").getAsString());
		try {
			state.setCanStealCardsFrom(jo.get("canStealCardsFrom")
					.getAsString());
		} catch (UnsupportedOperationException uoe) {
		}
		return state;
	}

	private List<Player> deserializePlayers(JsonArray ja, Map map) {
		List<Player> players = new ArrayList<Player>();
		for (JsonElement je : ja) {
			JsonObject jop = je.getAsJsonObject();
			Color c = new Color(jop.get("color").getAsInt(), false);
			Player p = new Player(jop.get("name").getAsString(), c);
			p.setUnplacedArmies(jop.get("unplacedArmies").getAsInt());
			p.setObjective(this.deserializeObjective(jop.get("objective")
					.getAsJsonObject()));
			JsonArray jac = jop.get("cards").getAsJsonArray();
			for (JsonElement jec : jac) {
				p.addCard(this.deserializeCard(jec.getAsJsonObject(), map));
			}
			players.add(p);
		}
		return players;
	}

	private Card deserializeCard(JsonObject jo, Map map) {
		int type = jo.get("type").getAsInt();
		if (type == 1) { // joker
			return new Card(1);
		} else {
			return new TerritoryCard(map.getTerritoryByName(jo.get(
					"territoryName").getAsString()), type);
		}
	}

	private WarObjective deserializeObjective(JsonObject jo) {
		String className = jo.get("class").getAsString();
		if (className.equals(ConquerContinentsObjective.class.getName())) {
			Continent c1 = Continent.getById(jo.get("targetContinent1")
					.getAsInt());
			Continent c2 = Continent.getById(jo.get("targetContinent2")
					.getAsInt());
			boolean hasToConquerAThirdContinent = jo.get(
					"hasToConquerAThirdContinent").getAsBoolean();
			return new ConquerContinentsObjective(c1, c2,
					hasToConquerAThirdContinent);
		} else if (className
				.equals(ConquerTerritoriesObjective.class.getName())) {
			int numberOfArmiesInEach = jo.get("numberOfArmiesInEach")
					.getAsInt();
			int numberOfTerritoriesToConquer = jo.get(
					"numberOfTerritoriesToConquer").getAsInt();
			return new ConquerTerritoriesObjective(
					numberOfTerritoriesToConquer, numberOfArmiesInEach);
		} else if (className.equals(DestroyPlayerObjective.class.getName())) {
			return new DestroyPlayerObjective(jo.get("targetPlayerName")
					.getAsString());
		}
		return null;
	}

	private Map deserializeMap(JsonObject jo, Map map) {
		JsonArray ja = jo.get("territories").getAsJsonArray();
		for (JsonElement je : ja) {
			JsonObject jot = je.getAsJsonObject();
			Territory t = map.getTerritoryByName(jot.get("name").getAsString());
			this.deserializeTerritory(jot, t);
		}
		return map;
	}

	private Territory deserializeTerritory(JsonObject jot, Territory t) {
		t.setArmies(jot.get("armyCount").getAsInt());
		t.setOwnerName(jot.get("ownerName").getAsString());
		t.setUnmovableArmies(jot.get("unmovableArmyCount").getAsInt());
		return t;
	}

}
