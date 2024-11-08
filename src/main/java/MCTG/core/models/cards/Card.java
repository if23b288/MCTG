package MCTG.core.models.cards;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Card {
    private final String cId;
    private final String name;
    private final double damage;
    private final Element elementType;

    @Override
    public String toString() {
        return "Card: " + name + " | Damage: " + damage + " | Element: " + elementType + "\n";
    }
}
