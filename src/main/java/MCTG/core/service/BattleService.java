package MCTG.core.service;

import MCTG.core.models.Battle;
import MCTG.core.models.BattleStatus;
import MCTG.core.models.user.Users;

import java.util.ArrayList;
import java.util.List;

public class BattleService {
    private final List<Battle> battles;

    public BattleService() {
        battles = new ArrayList<>();
    }

    public long joinBattle(Users player) {
        if (!battles.isEmpty()) {
            for (Battle battle : battles) {
                if (battle.getPlayer1() != null && battle.getPlayer2() == null) {
                    long battleId = battle.joinBattle(player);
                    synchronized (this) {
                        notify();
                    }
                    return battleId;
                }
            }
        }
        Battle newBattle = new Battle(player);
        battles.add(newBattle);
        return newBattle.getId();
    }

    public void leaveBattle(long battleId) {
        for (Battle battle : battles) {
            if (battle.getId() == battleId) {
                battles.remove(battle);
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    public BattleStatus getBattleStatus(long battleId) {
        for (Battle battle : battles) {
            if (battle.getId() == battleId) {
                return battle.getStatus();
            }
        }
        return null;
    }

    public Users getWinner(long battleId) {
        for (Battle battle : battles) {
            if (battle.getId() == battleId) {
                return battle.getWinner();
            }
        }
        return null;
    }
}
