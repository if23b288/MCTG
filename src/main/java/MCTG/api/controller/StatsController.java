package MCTG.api.controller;

import MCTG.core.models.user.Stats;
import MCTG.core.service.ScoreboardService;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;

import java.util.Optional;

public class StatsController extends Controller {
    private final Dao<Stats> statsDao;
    private final ScoreboardService scoreboardService;

    public StatsController(Dao<Stats> statsDao) {
        super();
        this.statsDao = statsDao;
        this.scoreboardService = new ScoreboardService(statsDao);
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            if (request.getPathParts().getFirst().equals("stats")) {
                return getStats(request);
            } else if (request.getPathParts().getFirst().equals("scoreboard")) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        scoreboardService.getScoreboard()
                );
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response getStats(Request request) {
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        Optional<Stats> stats = statsDao.get(username);
        stats.get().setUsername(username);
        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                stats.toString()
        );
    }
}
