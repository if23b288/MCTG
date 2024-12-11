package MCTG.core.models.cards;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Deck extends CardCollection {
    private String username;

    public Deck() {
        super();
    }

    public Deck(String username, List<Card> cards) {
        super(cards);
        this.username = username;
    }

    public void shuffle() {
        List<Card> cards = super.getCards();
        Collections.shuffle(cards);
        super.setCards(cards);
    }

    public void addCard(Card cardToAdd) {
        List<Card> cards = super.getCards();
        cards.add(cardToAdd);
        super.setCards(cards);
    }

    public void removeCard(Card cardToRemove) {
        super.removeCard(cardToRemove);
    }
}
