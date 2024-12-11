package MCTG.server.utils;

import MCTG.api.controller.*;
import MCTG.core.models.Trade;
import MCTG.core.models.user.Profile;
import MCTG.core.models.user.Stats;
import MCTG.core.models.user.Users;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Package;
import MCTG.core.models.cards.Stack;
import MCTG.core.service.*;
import MCTG.persistence.dao.*;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class Router {

    private final AuthorizationService authorizationService;

    private final List<Route> routes = new ArrayList<>();

    public Router() {
        // DAOS
        Dao<Users> userDao = new UsersDao();
        Dao<Package> packageDao = new PackageDao();
        Dao<Card> cardDao = new CardDao();
        Dao<Stack> stackDao = new StackDao();
        Dao<Deck> deckDao = new DeckDao();
        Dao<Profile> profileDao = new ProfileDao();
        Dao<Stats> statsDao = new StatsDao();
        Dao<Trade> tradeDao = new TradeDao();
        // SERVICES
        authorizationService = new AuthorizationService(userDao);
        // CONTROLLERS
        UserController userController = new UserController(userDao, profileDao, authorizationService);
        SessionController sessionController = new SessionController(userDao);
        CardController cardController = new CardController(cardDao, stackDao, deckDao);
        PackageController packageController = new PackageController(packageDao, cardController);
        TransactionController transactionController = new TransactionController(packageDao, stackDao, userDao);
        DeckController deckController = new DeckController(deckDao, stackDao);
        StatsController statsController = new StatsController(statsDao);
        BattleController battleController = new BattleController(userDao, deckDao, statsDao);
        TradingController tradingController = new TradingController(tradeDao, stackDao, cardDao);
        // ROUTES
        routes.add(new Route("users", userController, false));
        routes.add(new Route("sessions", sessionController, false));
        routes.add(new Route("packages", packageController, true));
        routes.add(new Route("transactions", transactionController, true));
        routes.add(new Route("cards", cardController, true));
        routes.add(new Route("deck", deckController, true));
        routes.add(new Route("stats", statsController, true));
        routes.add(new Route("scoreboard", statsController, true));
        routes.add(new Route("battles", battleController, true));
        routes.add(new Route("tradings", tradingController, true));
    }

    public Response resolve(Request request) {
        for (Route route : routes) {
            if (route.getRoute().equals(request.getPathParts().getFirst())) {
                if (route.getNeedsAuthorization() && !authorizationService.isAuthorized(request.getHeaderMap().getAuthorization())) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.PLAIN_TEXT,
                            ""
                    );
                }
                return route.getController().handleRequest(request);
            }
        }

        return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.PLAIN_TEXT,
                ""
        );
    }
}
