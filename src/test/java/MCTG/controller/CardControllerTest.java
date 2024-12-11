package MCTG.controller;

import MCTG.api.controller.CardController;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.Stack;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.dao.CardDao;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.DeckDao;
import MCTG.persistence.dao.StackDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardControllerTest {
    static Dao<Card> cardDao;
    static Dao<Stack> stackDao;
    static Dao<Deck> deckDao;

    static CardController cardController;

    @BeforeAll
    public static void setUp() {
        cardDao = Mockito.mock(CardDao.class);
        stackDao = Mockito.mock(StackDao.class);
        deckDao = Mockito.mock(DeckDao.class);

        cardController = new CardController(cardDao, stackDao, deckDao);
    }

    @Test
    public void testCreateCards() throws SQLException {
        String json = "[{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}]";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);

        when(cardDao.getAll()).thenReturn(List.of());
        doNothing().when(cardDao).save(any(Card.class));

        Response response = cardController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }

    @Test
    public void testGetCards() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.GET);
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-token");

        Stack stack = new Stack(username, List.of());
        Deck deck = new Deck(username, List.of());

        when(stackDao.get(username)).thenReturn(Optional.of(stack));
        when(deckDao.get(username)).thenReturn(Optional.of(deck));

        Response response = cardController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals("No cards in stack", response.getContent());
    }

    @Test
    public void testGetCardsFromJson() {
        String json = "[{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53e\", \"Name\":\"FireSpell\", \"Damage\": 20.0}]";
        List<Card> cards = null;
        try {
            cards = cardController.getCardsFromJSON(json);
        } catch(Exception e) {
            fail("Exception thrown");
        }

        assertEquals(2, cards.size(), "List should contain 2 cards");
        assertEquals("67f9048f-99b8-4ae4-b866-d8008d00c53d", cards.getFirst().getCId());
        assertEquals("WaterGoblin", cards.getFirst().getName());
        assertEquals(10.0, cards.getFirst().getDamage());
        assertEquals(Monster.GOBLIN, ((MonsterCard) cards.getFirst()).getMonsterType());
        assertEquals("67f9048f-99b8-4ae4-b866-d8008d00c53e", cards.getLast().getCId());
        assertEquals("FireSpell", cards.getLast().getName());
        assertEquals(20.0, cards.getLast().getDamage());
        assertEquals(Element.FIRE, cards.getLast().getElementType());
    }
}
