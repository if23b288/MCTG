package MCTG.api.controller;

import MCTG.core.models.Trade;
import MCTG.core.models.cards.*;
import MCTG.core.models.cards.Stack;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class TradingController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger("BattleController");

    private final Dao<Trade> tradeDao;
    private final Dao<Stack> stackDao;
    private final Dao<Card> cardDao;

    public TradingController(Dao<Trade> tradeDao, Dao<Stack> stackDao, Dao<Card> cardDao) {
        super();
        this.tradeDao = tradeDao;
        this.stackDao = stackDao;
        this.cardDao = cardDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().size() == 1) {
            if (request.getMethod() == Method.GET) {  // GET /tradings
                return getTrades();
            } else if (request.getMethod() == Method.POST) {  // POST /tradings
                return createTrade(request);
            }
        } else {
            if (request.getMethod() == Method.DELETE) {  // DELETE /tradings/{id}
                return deleteTrade(request);
            } else if (request.getMethod() == Method.POST) {  // POST /tradings/{id}
                return tradeCard(request);
            }
        }
        LOGGER.warn("Invalid method");
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response getTrades() {
        Collection<Trade> trades = tradeDao.getAll();
        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                trades.toString()
        );
    }

    private Response createTrade(Request request) {
        try {
            Trade tradeRequest = getTradeFromJson(request.getBody());
            String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
            // check if card is in stack == has card + is not in deck
            if (!userHasCard(username, tradeRequest.getCardToTrade().getCId())) {
                LOGGER.warn("User does not have card");
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
            // check if card is not already in tradeRequest
            Collection<Trade> trades = tradeDao.getAll();
            for (Trade trade : trades) {
                if (trade.getCardToTrade().getCId().equals(tradeRequest.getCardToTrade().getCId())) {
                    LOGGER.warn("Card is already in trade");
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.PLAIN_TEXT,
                            ""
                    );
                }
            }
            tradeRequest.setUsername(username);
            tradeDao.save(tradeRequest);
            return new Response (
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (JsonProcessingException | SQLException e) {
            LOGGER.error("Error creating trade", e);
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }

    public Trade getTradeFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tradeData = objectMapper.readTree(json);
        String id = tradeData.get("Id").asText();
        String cardToTradeId = tradeData.get("CardToTrade").asText();
        Optional<Card> cardToTrade = cardDao.get(cardToTradeId);
        if (cardToTrade.isEmpty()) {
            throw new IllegalArgumentException("Card not found: " + cardToTradeId);
        }
        String type = tradeData.get("Type").asText();
        double minimumDamage = tradeData.get("MinimumDamage").asDouble();
        return new Trade(id, "", cardToTrade.get(), type, minimumDamage);
    }

    private Response deleteTrade(Request request) {
        String id = request.getPathParts().getLast();
        tradeDao.delete(id);
        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response tradeCard(Request request) {
        String tId = request.getPathParts().getLast();
        String cId = request.getBody().replace("\"", "");
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        // check if user has card and card is not in deck
        if (!userHasCard(username, cId)) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        Optional<Trade> trade = tradeDao.get(tId);
        // check if trade exists and is not from the same user
        if (trade.isEmpty() || username.equals(trade.get().getUsername())) {
            LOGGER.warn("Trade does not exist or is from same user");
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        Card card = cardDao.get(cId).get();
        // check if card matches trade requirements
        boolean cardMatches = trade.get().acceptTrade(card);
        if (!cardMatches) {
            LOGGER.warn("Card does not match trade requirements");
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        // swap cards
        try {
            // remove card from stack of accepting user
            stackDao.delete(card.getCId());
            // add card to stack of request user
            stackDao.save(new Stack(trade.get().getUsername(), List.of(card)));
            // remove card from stack of request user
            stackDao.delete(trade.get().getCardToTrade().getCId());
            // add card to stack of accepting user
            stackDao.save(new Stack(username, List.of(trade.get().getCardToTrade())));
            // delete trade
            tradeDao.delete(tId);
            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (SQLException e) {
            LOGGER.error("Error trading card", e);
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }

    private boolean userHasCard(String username, String cId) {
        Optional<Stack> stack = stackDao.get(username);
        if (stack.isEmpty()) {
            LOGGER.warn("Stack is empty");
            return false;
        }
        for (Card card : stack.get().getCards()) {
            if (card.getCId().equals(cId)) {
                return true;
            }
        }
        LOGGER.warn("Card not found in stack");
        return false;
    }
}
