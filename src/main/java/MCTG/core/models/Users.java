package MCTG.core.models;

import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Stack;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Users {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    private String token;
    private Stack stack;
    private Deck deck;
    private int coins;
    private int elo;
    private Timestamp last_updated;

    public Users(String username, String password, String token, int coins, int elo, Timestamp last_updated) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.coins = coins;
        this.elo = elo;
        this.last_updated = last_updated;
    }

    public Users() {
        this.username = "";
        this.password = "";
        this.token = "";
        this.coins = 20;
        this.elo = 100;
        this.last_updated = new Timestamp(System.currentTimeMillis());
    }
}
