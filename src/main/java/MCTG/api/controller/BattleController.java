package MCTG.api.controller;

import MCTG.core.models.BattleStatus;
import MCTG.core.models.cards.Deck;
import MCTG.core.models.user.Stats;
import MCTG.core.models.user.Users;
import MCTG.core.service.BattleService;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Optional;

public class BattleController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger("BattleController");

    private final Dao<Users> userDao;
    private final Dao<Deck> deckDao;
    private final Dao<Stats> statsDao;
    private final BattleService battleService = new BattleService();
    private static final long TIMEOUT = 30000; // 30 seconds

    public BattleController (Dao<Users> userDao, Dao<Deck> deckDao, Dao<Stats> statsDao) {
        this.userDao = userDao;
        this.deckDao = deckDao;
        this.statsDao = statsDao;
    }

    @Override
    public Response handleRequest(Request request) {
        // POST /battles
        if (request.getMethod() == Method.POST) {
            try {
                return startBattle(request);
            } catch (JsonProcessingException | SQLException e) {
                LOGGER.error("Error starting battle", e);
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
        }
        LOGGER.warn("Invalid method");
        return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                "");
    }

    private Response startBattle(Request request) throws JsonProcessingException, SQLException {
        String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
        Optional<Users> dbUser = userDao.get(username);
        if (dbUser.isEmpty()) {
            LOGGER.warn("User not found");
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.PLAIN_TEXT,
                    "User not found"
            );
        }
        Optional<Deck> dbDeck = deckDao.get(username);
        if (dbDeck.get().getCards().isEmpty()) {
            LOGGER.warn("No Deck");
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.PLAIN_TEXT,
                    "No Deck"
            );
        }
        dbUser.get().setDeck(dbDeck.get());

        synchronized (battleService) {
            long battleId = battleService.joinBattle(dbUser.get());
            long startTime = System.currentTimeMillis();
            while (battleService.getBattleStatus(battleId) == BattleStatus.WAITING) {
                try {
                    battleService.wait(TIMEOUT);
                    if (System.currentTimeMillis() - startTime >= TIMEOUT) {
                        battleService.leaveBattle(battleId);
                        LOGGER.warn("Server Timeout");
                        return new Response(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ContentType.PLAIN_TEXT,
                                "Server Timeout"
                        );
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Thread interrupted", e);
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.PLAIN_TEXT,
                            "Thread interrupted"
                    );
                }
            }
            while (battleService.getBattleStatus(battleId) != BattleStatus.FINISHED) {
                try {
                    battleService.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Thread interrupted", e);
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.PLAIN_TEXT,
                            "Thread interrupted"
                    );
                }
            }
            String response = "Draw!";
            Users winner = battleService.getWinner(battleId);
            Optional<Stats> stats = statsDao.get(username);
            if (winner != null && winner.getUsername().equals(username)) {
                response = "You won!";
                if (stats.get().getUsername() != null) {
                    stats.get().setWins(stats.get().getWins() + 1);
                    stats.get().setElo(stats.get().getElo() + 3);
                    dbUser.get().setCoins(dbUser.get().getCoins() + 5);
                    statsDao.update(stats.get());
                    userDao.update(dbUser.get());
                } else {
                    Stats newStats = new Stats(username, 1, 0, 0, 103);
                    statsDao.save(newStats);
                    dbUser.get().setCoins(dbUser.get().getCoins() + 5);
                    userDao.update(dbUser.get());
                }
            } else if (winner != null && !winner.getUsername().equals(username)) {
                response = "You lost!";
                if (stats.get().getUsername() != null) {
                    stats.get().setLosses(stats.get().getLosses() + 1);
                    stats.get().setElo(stats.get().getElo() - 5);
                    statsDao.update(stats.get());
                } else {
                    Stats newStats = new Stats(username, 0, 1, 0, 95);
                    statsDao.save(newStats);
                }
            } else {
                if (stats.get().getUsername() != null) {
                    stats.get().setDraws(stats.get().getDraws() + 1);
                    statsDao.update(stats.get());
                } else {
                    Stats newStats = new Stats(username, 0, 0, 1, 100);
                    statsDao.save(newStats);
                }
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    response
            );
        }
    }
}
