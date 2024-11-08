package MCTG.core.models.cards;

import lombok.Data;

import java.util.List;

@Data
public class Deck extends CardCollection {
    private String username;

    public Deck() {
        super();
    }

    public Deck(String username, List<Card> cards) {
        super(cards);
        this.username = username;
    }

    public Card getRandomCard() {
        // return random card from super.cards
        return super.getCards().get((int) (Math.random() * super.getCards().size()));
    }

    public void removeCard(Card cardToRemove) {
        super.removeCard(cardToRemove);
    }
}
