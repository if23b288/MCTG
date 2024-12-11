package MCTG.api.controller;

import MCTG.core.models.cards.*;
import MCTG.core.models.cards.Stack;
import MCTG.core.models.cards.monster.*;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class CardController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger("CardController");

    private final Dao<Card> cardDao;
    private final Dao<Stack> stackDao;
    private final Dao<Deck> deckDao;

    public CardController(Dao<Card> cardDao, Dao<Stack> stackDao, Dao<Deck> deckDao) {
        super();
        this.cardDao = cardDao;
        this.stackDao = stackDao;
        this.deckDao = deckDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {  // POST /cards
            return createCards(request);
        } else if (request.getMethod() == Method.GET) {  // GET /cards
            return getCards(request);
        }

        LOGGER.warn("Invalid method");
        return new Response (
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response createCards(Request request) {
        try {
            List<Card> cards = getCardsFromJSON(request.getBody());

            for (Card card : cards) {
                if (!createCard(card)) {
                    return new Response (
                            HttpStatus.CONFLICT,
                            ContentType.PLAIN_TEXT,
                            ""
                    );
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Invalid JSON", e);
            return new Response (
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }

        return new Response (
                HttpStatus.CREATED,
                ContentType.JSON,
                ""
        );
    }

    private boolean createCard(Card newCard) {
        try {
            Collection<Card> cards = cardDao.getAll();
            if (cards.stream().anyMatch(c -> c.getCId().equals(newCard.getCId()))) {
                LOGGER.warn("Card already exists");
                return false;
            }
            cardDao.save(newCard);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to create card", e);
            return false;
        }
    }

    public List<Card> getCardsFromJSON(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> cardDataList = objectMapper.readValue(json, new TypeReference<>() {});
        List<Card> cards = new ArrayList<>();

        for (Map<String, Object> cardData : cardDataList) {
            String id = (String) cardData.get("Id");
            String name = (String) cardData.get("Name");
            double damage = (double) cardData.get("Damage");
            Element element = Element.getElement((String) cardData.get("Name"));

            if (name.contains("Spell")) {
                cards.add(new SpellCard(id, name, damage, element));
            } else {
                Monster monster = Monster.getMonster((String) cardData.get("Name"));
                cards.add(new MonsterCard(id, name, damage, element, monster));
            }
        }
        return cards;
    }

    private Response getCards(Request request) {
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        Optional<Stack> stack = stackDao.get(username);
        Optional<Deck> deck = deckDao.get(username);

        if (stack.get().getCards().isEmpty() && deck.get().getCards().isEmpty()) {
            return new Response (
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "[]"
            );
        }
        List<Card> allCards = stack.get().getCards();
        allCards.addAll(deck.get().getCards());

        return new Response (
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                allCards.toString()
        );
    }
}
