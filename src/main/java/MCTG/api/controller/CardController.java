package MCTG.api.controller;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CardController extends Controller {
    private final Dao<Card> cardDao;

    public CardController(Dao<Card> cardDao) {
        super();
        this.cardDao = cardDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return createCards(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response createCards(Request request) {
        try {
            List<Card> cards = getCards(request.getBody());

            for (Card card : cards) {
                if (!createCard(card)) {
                    return new Response(
                            HttpStatus.CONFLICT,
                            "CONFLICT",
                            ContentType.PLAIN_TEXT,
                            "Card already exists"
                    );
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    "BAD REQUEST",
                    ContentType.PLAIN_TEXT,
                    "Invalid JSON"
            );
        }

        return new Response(
                HttpStatus.CREATED,
                "CREATED",
                ContentType.JSON,
                ""
        );
    }

    private boolean createCard(Card newCard) {
        try {
            Collection<Card> cards = cardDao.getAll();
            if (cards.stream().anyMatch(c -> c.getCId().equals(newCard.getCId()))) {
                return false;
            }
            cardDao.save(newCard);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> getCards(String json) throws JsonProcessingException {
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
}
