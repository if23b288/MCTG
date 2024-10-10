package MCTG.core.models.cards;

import lombok.Getter;

import java.util.*;

@Getter
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

}
