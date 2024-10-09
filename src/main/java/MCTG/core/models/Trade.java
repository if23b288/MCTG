package MCTG.core.models;

import MCTG.core.models.cards.Card;

public class Trade {
    private Card tradeInCard;
    private String cardType;
    private int minDamage;

    public Trade(Card tradeInCard, String cardType, int minDamage) {
        this.tradeInCard = tradeInCard;
        this.cardType = cardType;
        this.minDamage = minDamage;
    }

    public void acceptTrade(Card card) {
        // TODO: does card match requirements?
        return;
    }
}
