package MCTG.core.models;

import MCTG.core.models.cards.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Trade {
    @JsonProperty("Id")
    private String id;
    public String username;
    @JsonProperty("CardToTrade")
    private Card cardToTrade;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("MinimumDamage")
    private double minimumDamage;

    public Trade(String id, String username, Card cardToTrade, String type, double minimumDamage) {
        this.id = id;
        this.username = username;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
    }

    public boolean acceptTrade(Card card) {
        if (card.getDamage() < minimumDamage) {
            System.out.println("Card damage too low");
            return false;
        }
        if (card.getClass().getName().equals("MonsterCard") && !type.equals("monster")) {
            System.out.println("Card type does not match: " + card.getClass().getName());
            return false;
        }
        if (card.getClass().getName().equals("SpellCard") && !type.equals("spell")) {
            System.out.println("Card type does not match: " + card.getClass().getName());
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "\nTrade Request " + id +
                "\nfrom " + username + " (" + cardToTrade.getName() + "/" + cardToTrade.getDamage() + ")" +
                "\nrequests " + type + "/>" + minimumDamage + "\n";
    }
}
