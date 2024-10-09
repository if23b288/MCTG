package MCTG.core.models;

import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Stack;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    private Stack stack;
    private Deck deck;
    private int coins;
    private int elo;

    public User() {
        this.stack = new Stack();
        this.deck = new Deck();
        this.coins = 20;
        this.elo = 100;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.stack = new Stack();
        this.deck = new Deck();
        this.coins = 20;
        this.elo = 100;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
