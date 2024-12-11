package MCTG.controller;

import MCTG.api.controller.StatsController;
import MCTG.core.models.user.Stats;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.StatsDao;
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
import static org.mockito.Mockito.when;

public class StatsControllerTest {
    static Dao<Stats> statsDao;
    static StatsController statsController;

    @BeforeAll
    public static void setUp() {
        statsDao = Mockito.mock(StatsDao.class);
        statsController = new StatsController(statsDao);
    }

    @Test
    public void testGetStats() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("stats"));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-token");

        Stats stats = new Stats();
        when(statsDao.get(username)).thenReturn(Optional.of(stats));

        Response response = statsController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals(Optional.of(stats).toString(), response.getContent());
    }

    @Test
    public void testGetScoreboard() {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("scoreboard"));

        Response response = statsController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals("", response.getContent());
    }
}
