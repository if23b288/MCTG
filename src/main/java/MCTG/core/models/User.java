package MCTG.core.models;

import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Stack;

public class User {
    private String username;
    private String password;
    private Stack stack;
    private Deck deck;
    private int coins = 20;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.stack = new Stack();
        this.deck = new Deck();
    }

    public void register(String username, String password) {
        return;
    }

    public void login(String username, String password) {
        return;
    }
}
