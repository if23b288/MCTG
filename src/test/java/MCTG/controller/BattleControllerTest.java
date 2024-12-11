package MCTG.controller;

import MCTG.api.controller.BattleController;
import MCTG.core.models.BattleStatus;
import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.core.models.user.Stats;
import MCTG.core.models.user.Users;
import MCTG.core.service.BattleService;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.DeckDao;
import MCTG.persistence.dao.StatsDao;
import MCTG.persistence.dao.UsersDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BattleControllerTest {
    static Dao<Users> userDao;
    static Dao<Deck> deckDao;
    static Dao<Stats> statsDao;
    static BattleService battleService;
    static BattleController battleController;

    @BeforeAll
    public static void setUp() {
        userDao = Mockito.mock(UsersDao.class);
        deckDao = Mockito.mock(DeckDao.class);
        statsDao = Mockito.mock(StatsDao.class);
        battleService = Mockito.mock(BattleService.class);
        battleController = new BattleController(userDao, deckDao, statsDao);
    }

    @Test
    public void testStartBattleSuccess() throws ExecutionException, InterruptedException {
        String username1 = "test1";
        Request request1 = new Request();
        request1.setMethod(Method.POST);
        request1.getHeaderMap().ingest("Authorization: Bearer " + username1 + "-mctgToken");

        String username2 = "test2";
        Request request2 = new Request();
        request2.setMethod(Method.POST);
        request2.getHeaderMap().ingest("Authorization: Bearer " + username2 + "-mctgToken");

        Users user1 = new Users(username1, "testPass", username1 + "-mctgToken", 10, new Timestamp(System.currentTimeMillis()));
        Deck deck1 = new Deck(username1, List.of(
                new MonsterCard("card1", "FireElf", 10.0, Element.FIRE, Monster.ELF),
                new SpellCard("card2", "WaterSpell", 20.0, Element.WATER),
                new SpellCard("card3", "RegularSpell", 30.0, Element.NORMAL),
                new MonsterCard("card4", "WaterGoblin", 40.0, Element.WATER, Monster.GOBLIN)
        ));
        Stats stats1 = new Stats(username1, 0, 0, 0, 100);

        Users user2 = new Users(username2, "testPass", username2 + "-mctgToken", 10, new Timestamp(System.currentTimeMillis()));
        Deck deck2 = new Deck(username2, List.of(
                new MonsterCard("card1", "Dragon", 50.0, Element.FIRE, Monster.ELF),
                new MonsterCard("card2", "Ork", 30.0, Element.NORMAL, Monster.ORK),
                new SpellCard("card3", "FireSpell", 35.0, Element.FIRE),
                new MonsterCard("card4", "WaterGoblin", 55.0, Element.WATER, Monster.GOBLIN)
        ));
        Stats stats2 = new Stats(username2, 0, 0, 0, 100);

        when(userDao.get(username1)).thenReturn(Optional.of(user1));
        when(userDao.get(username2)).thenReturn(Optional.of(user2));
        when(deckDao.get(username1)).thenReturn(Optional.of(deck1));
        when(deckDao.get(username2)).thenReturn(Optional.of(deck2));
        when(statsDao.get(username1)).thenReturn(Optional.of(stats1));
        when(statsDao.get(username2)).thenReturn(Optional.of(stats2));
        when(battleService.joinBattle(user1)).thenReturn(123456789L);
        when(battleService.getBattleStatus(123456789L)).thenReturn(BattleStatus.FINISHED);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Response> future1 = executor.submit(() -> battleController.handleRequest(request1));
        Future<Response> future2 = executor.submit(() -> battleController.handleRequest(request2));

        Response response1 = future1.get();
        Response response2 = future2.get();

        assertEquals(HttpStatus.OK.code, response1.getStatus());
        assertEquals(HttpStatus.OK.code, response2.getStatus());

        executor.shutdown();
    }

    @Test
    public void testStartBattleNoDeck() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-mctgToken");

        Users user = new Users(username, "testPass", username + "-mctgToken", 10, new Timestamp(System.currentTimeMillis()));
        Deck deck = new Deck();
        when(userDao.get(username)).thenReturn(Optional.of(user));
        when(deckDao.get(username)).thenReturn(Optional.of(deck));

        Response response = battleController.handleRequest(request);

        assertEquals(HttpStatus.NOT_FOUND.code, response.getStatus());
        assertEquals("No Deck", response.getContent());
    }
}
