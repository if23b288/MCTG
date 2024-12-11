package MCTG.controller;

import MCTG.api.controller.DeckController;
import MCTG.core.models.cards.*;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DeckControllerTest {
    static Dao<Deck> deckDao;
    static Dao<Stack> stackDao;
    static DeckController deckController;

    @BeforeAll
    public static void setUp() {
        deckDao = Mockito.mock(DeckDao.class);
        stackDao = Mockito.mock(StackDao.class);
        deckController = new DeckController(deckDao, stackDao);
    }

    @Test
    public void testGetDeck() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.GET);
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-token");

        Deck deck = new Deck(username, List.of());
        when(deckDao.get(username)).thenReturn(Optional.of(deck));

        Response response = deckController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals("[]", response.getContent());
    }

    @Test
    public void testConfigureDeck() throws Exception {
        String username = "testUser";
        String json = "[\"card1\", \"card2\", \"card3\", \"card4\"]";
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setBody(json);
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-token");

        Stack stack = new Stack(username, List.of(
                new MonsterCard("card1", "FireElf", 10.0, Element.FIRE, Monster.ELF),
                new SpellCard("card2", "WaterSpell", 20.0, Element.WATER),
                new SpellCard("card3", "RegularSpell", 30.0, Element.NORMAL),
                new MonsterCard("card4", "WaterGoblin", 40.0, Element.WATER, Monster.GOBLIN)
        ));
        when(stackDao.get(username)).thenReturn(Optional.of(stack));
        doNothing().when(deckDao).save(any(Deck.class));

        Response response = deckController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
    }
}