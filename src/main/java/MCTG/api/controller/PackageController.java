package MCTG.api.controller;

import MCTG.core.models.cards.Card;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import MCTG.core.models.cards.Package;

import java.util.List;

public class PackageController extends Controller {
    private final Dao<Package> packageDao;
    private final CardController cardController;

    public PackageController(Dao<Package> packageDao, CardController cardController) {
        this.packageDao = packageDao;
        this.cardController = cardController;
    }

    @Override
    public Response handleRequest(Request request) {
        cardController.handleRequest(request);
        
        if (request.getMethod() == Method.POST) {
            return createPackage(request);
        }

        return new Response (
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response createPackage(Request request) {
        try {
            List<Card> cards = cardController.getCardsFromJSON(request.getBody());
            Package newPackage = new Package(1, cards);
            packageDao.save(newPackage);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }

        return new Response (
                HttpStatus.CREATED,
                ContentType.JSON,
                ""
        );
    }


}
