package MCTG.api.controller;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Stack;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import MCTG.core.models.cards.Deck;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeckController extends Controller {
    private final Dao<Deck> deckDao;
    private final Dao<Stack> stackDao;

    public DeckController(Dao<Deck> deckDao, Dao<Stack> stackDao) {
        super();
        this.deckDao = deckDao;
        this.stackDao = stackDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.PUT) {
            return configureDeck(request);
        } else if (request.getMethod() == Method.GET) {
            return getDeck(request);
        }

        return new Response (
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response getDeck(Request request) {
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        Optional<Deck> deck = deckDao.get(username);
        if (request.getParams() != null && request.getParams().equals("format=plain")) {
            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    deck.get().getCards().toString()
            );
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(deck.get().getCards());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    json
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "Error getting deck"
            );
        }
    }

    private Response configureDeck(Request request) {
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        // get card ids from request body
        String[] cardIds = request.getBody().replace("[", "").replace("]", "").replace("\"", "").split(", ");
        if (cardIds.length < 4) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        // get cards
        Optional<Stack> stack = stackDao.get(username);
        if (stack.isEmpty()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        // search for ids in cards
        List<Card> filteredCards = stack.get().getCards().stream().filter(c -> Arrays.asList(cardIds).contains(c.getCId())).collect(Collectors.toList());
        if (filteredCards.size() < cardIds.length) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
        // create Deck object
        Deck deck = new Deck(username, filteredCards);
        // save deck
        try {
            deckDao.save(deck);
            stackDao.delete(new Stack(username, filteredCards));
        } catch (Exception e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "Error configuring deck"
            );
        }

        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                ""
        );
    }
}
