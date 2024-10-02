package MCTG.core.models.cards;

public class Deck extends CardCollection {
    public Deck() {
        super();
    }

    public Card getRandomCard() {
        // return random card from super.cards
        return super.getCards().get((int) (Math.random() * super.getCards().size()));
    }

    public void removeCard(Card cardToRemove) {
        super.removeCard(cardToRemove);
    }
}
