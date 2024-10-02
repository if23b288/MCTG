package MCTG.core.service;

import MCTG.core.models.User;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Deck;

public class Battle {
    private User player1;
    private User player2;

    public Battle(User player1, User player2) {
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

    private void moveCardToWinner(Card cardToMove, User winner) {
        return;
    }
}
