package MCTG.service;

import MCTG.core.models.BattleStatus;
import MCTG.core.models.cards.Deck;
import MCTG.core.models.user.Users;
import MCTG.core.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BattleServiceTest {
    private BattleService battleService;
    private Users player1;
    private Users player2;

    @BeforeEach
    public void setUp() {
        battleService = new BattleService();
        player1 = Mockito.mock(Users.class);
        player2 = Mockito.mock(Users.class);
    }

    @Test
    public void testJoinBattle() {
        when(player1.getUsername()).thenReturn("player1");
        when(player2.getUsername()).thenReturn("player2");
        when(player1.getDeck()).thenReturn(new Deck());
        when(player2.getDeck()).thenReturn(new Deck());

        long battleId = battleService.joinBattle(player1);
        long battleId2 = battleService.joinBattle(player2);
        assertEquals(battleId, battleId2);
        assertEquals(BattleStatus.FINISHED, battleService.getBattleStatus(battleId));
    }
}