package MCTG.core.models;

import MCTG.core.models.cards.Card;
import MCTG.core.models.user.Users;

public class Battle {
    private Users player1;
    private Users player2;

    public Battle(Users player1, Users player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void startBattle() {
        return;
    }

    private void playRound(Card card1, Card card2) {
        return;
    }

    private void specialRules(Card card1, Card card2) {
        return;
    }

    private void calcDamage(Card card1, Card cards2) {
        return;
    }

    private void moveCardToWinner(Card cardToMove, Users winner) {
        return;
    }
}
