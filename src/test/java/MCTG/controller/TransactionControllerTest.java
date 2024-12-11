package MCTG.controller;

import MCTG.api.controller.TransactionController;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.Package;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.Stack;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.core.models.user.Users;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.PackageDao;
import MCTG.persistence.dao.StackDao;
import MCTG.persistence.dao.UsersDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class TransactionControllerTest {
    static Dao<MCTG.core.models.cards.Package> packageDao;
    static Dao<Stack> stackDao;
    static Dao<Users> usersDao;
    static TransactionController transactionController;

    @BeforeAll
    public static void setUp() {
        packageDao = Mockito.mock(PackageDao.class);
        stackDao = Mockito.mock(StackDao.class);
        usersDao = Mockito.mock(UsersDao.class);
        transactionController = new TransactionController(packageDao, stackDao, usersDao);
    }

    @Test
    public void testAcquirePackageSuccess() throws SQLException {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("transactions", "packages"));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-mctgToken");

        Users user = new Users(username, "testPass", username + "-mctgToken", 10, new Timestamp(System.currentTimeMillis()));
        user.setCoins(10);
        Package pack = new Package(1, List.of(
                new MonsterCard("card1", "Dragon", 10, Element.NORMAL, Monster.DRAGON),
                new SpellCard("card2", "WaterSpell", 10, Element.WATER),
                new MonsterCard("card3", "WaterGoblin", 10, Element.WATER, Monster.GOBLIN),
                new SpellCard("card4", "FireSpell", 10, Element.FIRE),
                new SpellCard("card5", "NormalSpell", 10, Element.NORMAL)
        ));

        when(usersDao.get(username)).thenReturn(Optional.of(user));
        when(packageDao.getAll()).thenReturn(List.of(pack));
        doNothing().when(stackDao).save(any(Stack.class));
        doNothing().when(usersDao).update(any());
        doNothing().when(packageDao).delete(any());

        Response response = transactionController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }

    @Test
    public void testAcquirePackageNotEnoughMoney() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("transactions", "packages"));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-mctgToken");

        Users user = new Users(username, "testPass", username + "-mctgToken", 3, new Timestamp(System.currentTimeMillis()));

        when(usersDao.get(username)).thenReturn(Optional.of(user));

        Response response = transactionController.handleRequest(request);

        assertEquals(HttpStatus.CONFLICT.code, response.getStatus());
        assertEquals("Not enough money", response.getContent());
    }

    @Test
    public void testAcquirePackageNoPackagesAvailable() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("transactions", "packages"));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-token");

        Users user = new Users(username, "testPass", username + "-mctgToken", 10, new Timestamp(System.currentTimeMillis()));

        when(usersDao.get(username)).thenReturn(Optional.of(user));
        when(packageDao.getAll()).thenReturn(List.of());

        Response response = transactionController.handleRequest(request);

        assertEquals(HttpStatus.CONFLICT.code, response.getStatus());
        assertEquals("No packages available", response.getContent());
    }
}
