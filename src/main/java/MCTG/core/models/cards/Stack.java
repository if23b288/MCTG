package MCTG.core.models.cards;

import lombok.Getter;

import java.util.List;

@Getter
public class Stack extends CardCollection {
    private final String username;

    public Stack(String username, List<Card> cards) {
        super(cards);
        this.username = username;
    }
}
