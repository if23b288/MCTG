package MCTG.core.models;

import MCTG.core.models.cards.Deck;
import MCTG.core.models.cards.Stack;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    private String token;
    private Stack stack;
    private Deck deck;
    private int coins = 20;
    private int elo = 100;
}
