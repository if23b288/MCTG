package MCTG.api.controller;

import MCTG.core.models.user.Users;
import MCTG.core.models.cards.Stack;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import MCTG.core.models.cards.Package;

import java.util.Collection;

public class TransactionController extends Controller {
    private final Dao<Package> packageDao;
    private final Dao<Stack> stackDao;
    private final Dao<Users> usersDao;

    public TransactionController(Dao<Package> packageDao, Dao<Stack> stackDao, Dao<Users> usersDao) {
        this.packageDao = packageDao;
        this.stackDao = stackDao;
        this.usersDao = usersDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().size() > 1 && request.getPathParts().get(1).equals("packages")) {
            if (request.getMethod() == Method.POST) {
                return acquirePackage(request);
            } else {
                return new Response (
                        HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
        }

        return new Response (
                HttpStatus.NOT_FOUND,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response acquirePackage(Request request) {
        try {
            // check if user has enough coins
            String username = request.getHeaderMap().getAuthorization().split(" ")[1].split("-")[0];
            Users user = usersDao.get(username).orElse(null);
            if (user == null) {
                return new Response (
                        HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
            if (user.getCoins() < 5) {
                return new Response (
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "Not enough money"
                );
            }
            // decrease coins
            user.setCoins(user.getCoins() - 5);
            // get all packages
            Collection<Package> packages = packageDao.getAll();
            if (packages.isEmpty()) {
                return new Response (
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "No packages available"
                );
            }
            // choose random package
            Package randomPackage = packages.stream().findAny().orElse(null);
            if (randomPackage == null) {
                throw new Exception("No package found");
            }
            // create stack with new cards
            Stack stack = new Stack(username, randomPackage.getCards());
            // add cards to stack from user
            stackDao.save(stack);
            // update user
            usersDao.update(user);
            // delete package
            packageDao.delete(randomPackage);

            return new Response (
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }
}
