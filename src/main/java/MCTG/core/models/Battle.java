package MCTG.core.models;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.core.models.user.Users;
import lombok.Data;

import java.util.Random;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Data
public class Battle {
    private static final Logger LOGGER = LogManager.getLogger("Battle");

    private static final Random random = new Random();
    private long id;
    private Users player1;
    private Users player2;
    private BattleStatus status;
    private int roundsPlayed;
    private Users winner;

    public Battle(Users player1) {
        this.id = generateId();
        this.player1 = player1;
        this.player2 = null;
        this.status = BattleStatus.WAITING;
        this.roundsPlayed = 0;
        this.winner = null;

        LOGGER.info("Player1: ({}) created Battle {}", player1.getUsername(), this.id);
    }

    // returns battle id
    public long joinBattle(Users player) {
        if (this.player1 != null) {
            LOGGER.info("Player2: ({}) joined Battle {}", player.getUsername(), this.id);
            this.player2 = player;
            this.status = BattleStatus.STARTED;
            return startBattle();
        }
        LOGGER.info("Player1 ({}) joined Battle {}", player.getUsername(), this.id);
        this.player1 = player;
        return this.id;
    }

    private long generateId() {
        try {
            return Long.parseLong(System.currentTimeMillis() + String.valueOf(random.nextInt(1000)));
        } catch (Exception e) {
            System.err.println("generateId: " + e.getMessage());
        }
        return -1;
    }

    public long startBattle() {
        LOGGER.info("Battle {} started", this.id);
        while (!isBattleOver()) {
            mixDecks();
            playRound();
            this.roundsPlayed++;
        }
        return this.id;
    }

    private Boolean isBattleOver() {
        if (player1.getDeck().getCards().isEmpty() || player2.getDeck().getCards().isEmpty() || this.roundsPlayed >= 100) {
            LOGGER.info("Battle {} is over", this.id);
            this.status = BattleStatus.FINISHED;
            if (player1.getDeck().getCards().isEmpty()) {
                LOGGER.info("Player1 ({}) has no cards left", player1.getUsername());
                this.winner = player2;
            } else if (player2.getDeck().getCards().isEmpty()) {
                LOGGER.info("Player2 ({}) has no cards left", player2.getUsername());
                this.winner = player1;
            }
            LOGGER.info("The Winner of Battle {} is {}", this.id, this.winner == null ? "No Winner!" : this.winner.getUsername());
            return true;
        }
        return false;
    }

    private void mixDecks() {
        player1.getDeck().shuffle();
        player2.getDeck().shuffle();
    }

    private void playRound() {
        LOGGER.info("--------------------------Playing round {}--------------------------", (this.roundsPlayed + 1));
        Card card1 = player1.getDeck().getCards().getFirst();
        Card card2 = player2.getDeck().getCards().getFirst();
        double damage1 = card1.getDamage();
        double damage2 = card2.getDamage();
        if (card1 instanceof SpellCard || card2 instanceof SpellCard) {
            damage1 = damageAfterEffectiveness(card1, damage1, card2);
            damage2 = damageAfterEffectiveness(card2, damage2, card1);
        }
        damage1 = applySpecialRules(card1, damage1, card2);
        damage2 = applySpecialRules(card2, damage2, card1);
        LOGGER.info("{} played {} with {} damage", player1.getUsername(), card1.getName(), damage1);
        LOGGER.info("{} played {} with {} damage", player2.getUsername(), card2.getName(), damage2);
        if (damage1 > damage2) {
            LOGGER.info("{} won the round", player1.getUsername());
            moveCardToWinner(card2, player1, player2);
        } else if (damage2 > damage1) {
            LOGGER.info("{} won the round", player2.getUsername());
            moveCardToWinner(card1, player2, player1);
        }
    }

    private double damageAfterEffectiveness(Card card1, double damage, Card cards2) {
        if (card1.getElementType() == Element.WATER) {
            if (cards2.getElementType() == Element.FIRE) {
                damage *= 2;
            } else if (cards2.getElementType() == Element.NORMAL) {
                damage *= 0.5;
            }
        } else if (card1.getElementType() == Element.FIRE) {
            if (cards2.getElementType() == Element.NORMAL) {
                damage *= 2;
            } else if (cards2.getElementType() == Element.WATER) {
                damage *= 0.5;
            }
        } else if (card1.getElementType() == Element.NORMAL) {
            if (cards2.getElementType() == Element.WATER) {
                damage *= 2;
            } else if (cards2.getElementType() == Element.FIRE) {
                damage *= 0.5;
            }
        }
        return damage;
    }

    private double applySpecialRules(Card card1, double damage, Card card2) {
        if (card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
            // MonsterCard vs MonsterCard
            if (((MonsterCard) card1).getMonsterType() == Monster.GOBLIN && ((MonsterCard) card2).getMonsterType() == Monster.DRAGON) {
                damage = 0;
            } else if (((MonsterCard) card1).getMonsterType() == Monster.ORK && ((MonsterCard) card2).getMonsterType() == Monster.WIZARD) {
                damage = 0;
            } else if (((MonsterCard) card1).getMonsterType() == Monster.DRAGON && ((MonsterCard) card2).getMonsterType() == Monster.ELF && card2.getElementType() == Element.FIRE) {
                damage = 0;
            }
        } else if (card1 instanceof MonsterCard && card2 instanceof SpellCard) {
            // MonsterCard vs SpellCard
            if (((MonsterCard) card1).getMonsterType() == Monster.KNIGHT && card2.getElementType() == Element.WATER) {
                damage = 0;
            }
        } else if (card1 instanceof SpellCard && card2 instanceof MonsterCard) {
            // SpellCard vs MonsterCard
            if (((MonsterCard) card2).getMonsterType() == Monster.KRAKEN) {
                damage = 0;
            }
        }
        return damage;
    }

    private void moveCardToWinner(Card cardToMove, Users winner, Users loser) {
        loser.getDeck().removeCard(cardToMove);
        winner.getDeck().addCard(cardToMove);
    }
}
