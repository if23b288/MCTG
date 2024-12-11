package MCTG.controller;

import MCTG.api.controller.CardController;
import MCTG.api.controller.PackageController;
import MCTG.core.models.cards.*;
import MCTG.core.models.cards.Package;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.PackageDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class PackageControllerTest {
    static Dao<Package> packageDao;
    static CardController cardController;
    static PackageController packageController;

    @BeforeAll
    public static void setUp() {
        packageDao = Mockito.mock(PackageDao.class);
        cardController = Mockito.mock(CardController.class);
        packageController = new PackageController(packageDao, cardController);
    }

    @Test
    public void testCreatePackage() throws JsonProcessingException, SQLException {
        String json = "[{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}]";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);

        List<Card> cards = List.of(new MonsterCard("67f9048f-99b8-4ae4-b866-d8008d00c53d", "WaterGoblin", 10.0, Element.WATER, Monster.GOBLIN));
        when(cardController.getCardsFromJSON(json)).thenReturn(cards);
        doNothing().when(packageDao).save(any(Package.class));

        Response response = packageController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }
}