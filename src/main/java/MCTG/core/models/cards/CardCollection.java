package MCTG.core.models.cards;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
abstract class CardCollection {
    private List<Card> cards;

    public CardCollection() {
        this.cards = new ArrayList<>();
    }

    public CardCollection(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public void removeCard(Card cardToRemove) {
        this.cards.remove(cardToRemove);
    }
}
