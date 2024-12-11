package MCTG.controller;

import MCTG.api.controller.TradingController;
import MCTG.core.models.Trade;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.Stack;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.dao.CardDao;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.StackDao;
import MCTG.persistence.dao.TradeDao;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class TradingControllerTest {
    static Dao<Trade> tradeDao;
    static Dao<Stack> stackDao;
    static Dao<Card> cardDao;
    static TradingController tradingController;

    @BeforeAll
    public static void setUp() {
        tradeDao = Mockito.mock(TradeDao.class);
        stackDao = Mockito.mock(StackDao.class);
        cardDao = Mockito.mock(CardDao.class);
        tradingController = new TradingController(tradeDao, stackDao, cardDao);
    }

    @Test
    public void testGetTrades() {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("trades"));

        Card cardToTrade = new MonsterCard("card1", "Dragon", 10.0, Element.NORMAL, Monster.DRAGON);
        Trade trade = new Trade("1", "otherUser", cardToTrade, "monster", 10.0);

        when(tradeDao.getAll()).thenReturn(List.of(trade));

        Response response = tradingController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals(List.of(trade).toString(), response.getContent());
    }

    @Test
    public void testCreateTrade() throws SQLException {
        String json = "{\"Id\": \"1\", \"CardToTrade\": \"card1\", \"Type\": \"monster\", \"MinimumDamage\": 10.0}";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("trades"));
        request.setBody(json);
        request.getHeaderMap().ingest("Authorization: Bearer testUser-mctgToken");

        Card card = new MonsterCard("card1", "Dragon", 10.0, Element.NORMAL, Monster.DRAGON);
        when(cardDao.get(any())).thenReturn(Optional.of(card));
        when(tradeDao.getAll()).thenReturn(List.of());
        when(stackDao.get(any())).thenReturn(Optional.of(new Stack("testUser", List.of(card))));
        doNothing().when(tradeDao).save(any(Trade.class));

        Response response = tradingController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }

    @Test
    public void testDeleteTrade() {
        Request request = new Request();
        request.setMethod(Method.DELETE);
        request.setPathParts(List.of("tradings", "1"));

        doNothing().when(tradeDao).delete(any());

        Response response = tradingController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    public void testTradeCard() throws SQLException {
        String json = "\"card2\"";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("tradings", "1"));
        request.setBody(json);
        request.getHeaderMap().ingest("Authorization: Bearer testUser-mctgToken");

        Card cardToTrade = new MonsterCard("card1", "Dragon", 10.0, Element.NORMAL, Monster.DRAGON);
        Card cardToReceive = new SpellCard("card2", "WaterSpell", 15.0, Element.WATER);
        Trade trade = new Trade("1", "otherUser", cardToTrade, "monster", 10.0);

        when(tradeDao.get("1")).thenReturn(Optional.of(trade));
        when(cardDao.get("card2")).thenReturn(Optional.of(cardToReceive));
        when(stackDao.get("testUser")).thenReturn(Optional.of(new Stack("testUser", List.of(cardToReceive))));
        when(stackDao.get("otherUser")).thenReturn(Optional.of(new Stack("otherUser", List.of(cardToTrade))));
        doNothing().when(stackDao).delete(any(String.class));
        doNothing().when(stackDao).save(any(Stack.class));
        doNothing().when(tradeDao).delete("1");

        Response response = tradingController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
    }
}
