package MCTG.core.models.cards;

import java.util.*;

abstract class CardCollection {
    private List<Card> cards;

    public CardCollection() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card newCard) {
        this.cards.add(newCard);
    }

    public void removeCard(Card cardToRemove) {
        this.cards.remove(cardToRemove);
    }

    public List<Card> getCards() {
        return this.cards;
    }
}
