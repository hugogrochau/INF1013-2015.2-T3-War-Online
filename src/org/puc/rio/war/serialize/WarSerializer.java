package org.puc.rio.war.serialize;

import java.lang.reflect.Type;
import java.util.List;

import org.puc.rio.war.model.Card;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WarSerializer implements JsonSerializer<WarState> {

	@Override
	public JsonElement serialize(final WarState warState, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("conqueredThisTurn", warState.conqueredThisTurn());
		result.addProperty("cardExchangeArmyCount",
				warState.getCardExchangeArmyCount());
		result.addProperty("currentTurnState", warState.getCurrentTurnState()
				.name());
		result.addProperty("currentPlayer", warState.getCurrentPlayer()
				.getName());
		result.addProperty("canStealCardsFrom", warState.getCanStealCardsFrom());
		JsonArray ja = new JsonArray();
		for (Player p : warState.getPlayers()) {
			ja.add(this.serializePlayer(p, type, context));
		}
		result.add("players", ja);
		result.add("map", this.serializeMap(warState.getMap(), type, context));
		// result.add("deck", this.serializeDeck(warState.getDeck()));
		return result;
	}

	public JsonElement serializePlayer(final Player p, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("name", p.getName());
		result.add("unplacedArmies", new JsonPrimitive(p.getUnplacedArmies()));
		result.addProperty("color", p.getColor().getRGB());
		result.add("cards", serializeCards(p.getCards()));
		result.add("objective",
				this.serializeObjective(p.getObjective(), type, context));

		return result;
	}

	public JsonElement serializeObjective(final WarObjective o,
			final Type type, final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		if (o instanceof ConquerContinentsObjective) {
			ConquerContinentsObjective cco = (ConquerContinentsObjective) o;
			result.addProperty("class", ConquerContinentsObjective.class.getName());
			result.addProperty("targetContinent1", cco.getTargetContinent1()
					.getId());
			result.addProperty("targetContinent2", cco.getTargetContinent2()
					.getId());
			result.addProperty("hasToConquerAThirdContinent",
					cco.hasToConquerAThirdContinent());
		} else if (o instanceof ConquerTerritoriesObjective) {
			ConquerTerritoriesObjective cto = (ConquerTerritoriesObjective) o;
			result.addProperty("class", ConquerTerritoriesObjective.class.getName());
			result.addProperty("numberOfArmiesInEach",
					cto.getNumberOfArmiesInEach());
			result.addProperty("numberOfTerritoriesToConquer",
					cto.getNumberOfTerritoriesToConquer());
		} else if (o instanceof DestroyPlayerObjective) {
			DestroyPlayerObjective dpo = (DestroyPlayerObjective) o;
			result.addProperty("class", DestroyPlayerObjective.class.getName());
			result.addProperty("targetPlayerName", dpo.getTargetPlayerName());
		}
		return result;
	}

	public JsonArray serializeCards(List<Card> l) {
		JsonArray ja = new JsonArray();
		for (Card c : l) {
			JsonObject jo = new JsonObject();
			int type = c.getType().getId();
			jo.addProperty("type", type);
			if (type != 1) { // not joker
				TerritoryCard tc = (TerritoryCard) c;
				jo.addProperty("territoryName", tc.getTerritory().getName());
			}
			ja.add(jo);
		}
		return ja;
	}

	public JsonElement serializeMap(final Map m, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		JsonArray ja = new JsonArray();
		for (Territory t : m.getTerritories()) {
			ja.add(this.serializeTerritory(t, type, context));
		}
		result.add("territories", ja);
		return result;
	}

	public JsonElement serializeTerritory(final Territory t, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("name", t.getName());
		result.addProperty("armyCount", t.getArmyCount());
		result.addProperty("ownerName", t.getOwnerName());
		result.addProperty("unmovableArmyCount", t.getUnmovableArmyCount());
		return result;
	}

}
